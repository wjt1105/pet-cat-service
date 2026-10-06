package com.petsitter.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.petsitter.common.entity.Orders;
import com.petsitter.common.exception.BusinessException;
import com.petsitter.common.result.ResultCode;
import com.petsitter.order.dto.CreateOrderDTO;
import com.petsitter.order.lock.RedisLockService;
import com.petsitter.order.mapper.OrderMapper;
import com.petsitter.order.mq.OrderMQProducer;
import com.petsitter.order.mq.OrderMessage;
import com.petsitter.order.service.OrderGlobalTxService;
import com.petsitter.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 订单服务实现：负责分布式锁编排，全局事务委托给 {@link OrderGlobalTxService}
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final RedisLockService redisLockService;
    private final OrderGlobalTxService orderGlobalTxService;
    private final OrderMQProducer orderMQProducer;
    private final OrderMapper orderMapper;

    @Override
    public String createOrder(Long userId, CreateOrderDTO dto) {
        // 以服务维度加锁：同一服务的下单请求串行进入临界区，配合数据库原子扣减防止超卖
        String lockKey = redisLockService.buildStockLockKey(dto.getServiceId());
        String lockValue = redisLockService.newLockValue();
        boolean locked = redisLockService.tryLock(
                lockKey, lockValue, RedisLockService.DEFAULT_EXPIRE_MILLIS);
        if (!locked) {
            // 抢锁失败：快速失败，避免大量请求阻塞
            throw new BusinessException(ResultCode.ORDER_CREATE_BUSY);
        }

        try {
            // 全局事务方法返回时，Seata 已完成提交（因此锁释放在事务提交之后，杜绝"锁先放、事务未提交"的窗口）
            Orders order = orderGlobalTxService.createOrderInGlobalTx(userId, dto);

            // 事务提交成功后再发送 MQ，保证消息语义为"下单成功"
            orderMQProducer.sendOrderCreated(OrderMessage.builder()
                    .orderNo(order.getOrderNo())
                    .userId(order.getUserId())
                    .serviceTitle(order.getServiceTitle())
                    .amount(order.getAmount())
                    .appointmentTime(order.getAppointmentTime())
                    .createTime(order.getCreateTime())
                    .build());

            return order.getOrderNo();
        } finally {
            // 无论成功失败都释放锁；Lua 脚本会校验持有者，避免误删
            redisLockService.unlock(lockKey, lockValue);
        }
    }

    @Override
    public Orders getByOrderNo(String orderNo) {
        Orders order = orderMapper.selectOne(
                new LambdaQueryWrapper<Orders>().eq(Orders::getOrderNo, orderNo));
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        return order;
    }

    @Override
    public boolean markPaid(String orderNo) {
        Orders order = orderMapper.selectOne(
                new LambdaQueryWrapper<Orders>().eq(Orders::getOrderNo, orderNo));
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        // 只有待支付状态（0）才能改为已支付（1）
        if (order.getStatus() != null && order.getStatus() != 0) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR);
        }
        order.setStatus(1);
        int rows = orderMapper.updateById(order);
        return rows > 0;
    }
}
