package com.petsitter.order.service;

import com.petsitter.common.entity.Orders;
import com.petsitter.common.entity.PetService;
import com.petsitter.common.exception.BusinessException;
import com.petsitter.common.result.Result;
import com.petsitter.common.result.ResultCode;
import com.petsitter.order.dto.CreateOrderDTO;
import com.petsitter.order.feign.ServiceFeignClient;
import com.petsitter.order.feign.dto.StockDeductRequest;
import com.petsitter.order.mapper.OrderMapper;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 下单全局事务编排（独立 Bean）
 * <p>
 * 单独成类的原因：{@link GlobalTransactional} 基于 AOP 代理，同类内部自调用不会生效；
 * 由外层 {@code OrderServiceImpl} 注入并调用，确保注解真正生效。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderGlobalTxService {

    /** 下单数量固定为 1（一次预约对应一次上门服务） */
    private static final int ORDER_COUNT = 1;

    private static final DateTimeFormatter ORDER_NO_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private final ServiceFeignClient serviceFeignClient;
    private final OrderMapper orderMapper;

    /**
     * 在一个 Seata 全局事务内完成：查服务 → 扣库存 → 建订单。
     * 任意一步抛异常，TC 都会驱动各分支（service-service 库存分支、本地订单分支）整体回滚。
     *
     * @param userId 下单用户 ID（由网关从 JWT 解析后透传）
     * @param dto    下单请求
     * @return 已落库的订单
     */
    @GlobalTransactional(name = "petsitter-create-order", rollbackFor = Exception.class)
    public Orders createOrderInGlobalTx(Long userId, CreateOrderDTO dto) {
        // 1. 远程查询服务详情（校验服务是否存在、是否上架，并取价格/铲屎官等快照数据）
        Result<PetService> serviceResult = serviceFeignClient.getServiceById(dto.getServiceId());
        if (!isSuccess(serviceResult) || serviceResult.getData() == null) {
            throw new BusinessException(serviceResult.getCode(), serviceResult.getMessage());
        }
        PetService service = serviceResult.getData();
        if (service.getStatus() == null || service.getStatus() != 1) {
            throw new BusinessException(ResultCode.SERVICE_OFFLINE);
        }

        // 2. 远程扣减库存（XID 已由 FeignXidInterceptor 透传，库存操作加入当前全局事务）
        Result<Void> deductResult = serviceFeignClient.deductStock(
                new StockDeductRequest(service.getId(), ORDER_COUNT));
        if (!isSuccess(deductResult)) {
            // 库存不足 / 下游降级：抛出异常触发全局回滚
            throw new BusinessException(deductResult.getCode(), deductResult.getMessage());
        }

        // 3. 构建订单并本地落库（状态 0-待支付，金额与标题等使用快照，避免服务后续改价影响订单）
        Orders order = new Orders();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setServiceId(service.getId());
        order.setSitterId(service.getSitterId());
        order.setServiceTitle(service.getTitle());
        order.setServicePrice(service.getPrice());
        order.setAppointmentTime(dto.getAppointmentTime());
        order.setServiceAddress(dto.getServiceAddress());
        order.setRemark(dto.getRemark());
        order.setAmount(service.getPrice());
        order.setStatus(0);
        orderMapper.insert(order);

        log.info("全局事务内下单成功：orderNo={}, xid 由 Seata 上下文管理", order.getOrderNo());
        return order;
    }

    private boolean isSuccess(Result<?> result) {
        return result != null && ResultCode.SUCCESS.getCode().equals(result.getCode());
    }

    /**
     * 生成订单号：PS + 17 位时间戳 + 4 位随机数
     */
    private String generateOrderNo() {
        String timePart = LocalDateTime.now().format(ORDER_NO_FORMATTER);
        int random = ThreadLocalRandom.current().nextInt(1000, 10000);
        return "PS" + timePart + random;
    }
}
