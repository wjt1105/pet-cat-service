package com.petsitter.pay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.petsitter.common.entity.Orders;
import com.petsitter.common.entity.Payment;
import com.petsitter.common.exception.BusinessException;
import com.petsitter.common.result.Result;
import com.petsitter.common.result.ResultCode;
import com.petsitter.pay.dto.PayRequestDTO;
import com.petsitter.pay.feign.OrderFeignClient;
import com.petsitter.pay.mapper.PaymentMapper;
import com.petsitter.pay.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 支付服务实现
 * <p>
 * 流程：Feign 校验订单 → 防重复支付检查 → 模拟第三方支付成功 → 本地落支付单（本地事务）
 * → Feign 回调 order-service 标记订单已支付。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private static final DateTimeFormatter PAY_NO_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private final PaymentMapper paymentMapper;
    private final OrderFeignClient orderFeignClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String pay(Long userId, PayRequestDTO dto) {
        // 1. 远程查询订单，校验存在性与归属
        Result<Orders> orderResult = orderFeignClient.getByOrderNo(dto.getOrderNo());
        if (orderResult == null || !ResultCode.SUCCESS.getCode().equals(orderResult.getCode())
                || orderResult.getData() == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        Orders order = orderResult.getData();
        if (!userId.equals(order.getUserId())) {
            throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "只能支付自己的订单");
        }
        // 2. 订单状态校验：0-待支付 才能支付；1-已支付 直接拒绝，防止重复支付
        if (order.getStatus() != null && order.getStatus() != 0) {
            throw new BusinessException(ResultCode.ORDER_ALREADY_PAID);
        }
        // 3. 金额一致性校验（防篡改）
        if (order.getAmount() == null || order.getAmount().compareTo(dto.getAmount()) != 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "支付金额与订单金额不一致");
        }
        // 4. 同一订单只允许存在一条支付单（数据库唯一索引兜底）
        Long count = paymentMapper.selectCount(
                new LambdaQueryWrapper<Payment>().eq(Payment::getOrderNo, dto.getOrderNo()));
        if (count != null && count > 0) {
            throw new BusinessException(ResultCode.ORDER_ALREADY_PAID);
        }

        // 5. 模拟第三方支付成功，生成交易流水号
        String tradeNo = "MOCK" + System.currentTimeMillis();

        // 6. 落支付单（本地事务）
        Payment payment = new Payment();
        payment.setPaymentNo(generatePaymentNo());
        payment.setOrderId(order.getId());
        payment.setOrderNo(order.getOrderNo());
        payment.setUserId(userId);
        payment.setAmount(dto.getAmount());
        payment.setPayType(dto.getPayType());
        payment.setStatus(1);
        payment.setTradeNo(tradeNo);
        payment.setPayTime(LocalDateTime.now());
        paymentMapper.insert(payment);

        // 7. 支付回调：远程标记订单已支付
        Result<Boolean> paidResult = orderFeignClient.markPaid(order.getOrderNo());
        if (paidResult == null || !ResultCode.SUCCESS.getCode().equals(paidResult.getCode())
                || !Boolean.TRUE.equals(paidResult.getData())) {
            // 订单状态修改失败：抛异常回滚本地支付单，保证两边状态一致
            throw new BusinessException(ResultCode.PAYMENT_FAILED.getCode(), "订单状态更新失败，支付已回滚");
        }

        log.info("支付成功：paymentNo={}, orderNo={}, tradeNo={}",
                payment.getPaymentNo(), order.getOrderNo(), tradeNo);
        return payment.getPaymentNo();
    }

    @Override
    public Payment getByPaymentNo(String paymentNo) {
        Payment payment = paymentMapper.selectOne(
                new LambdaQueryWrapper<Payment>().eq(Payment::getPaymentNo, paymentNo));
        if (payment == null) {
            throw new BusinessException(ResultCode.PAYMENT_NOT_FOUND);
        }
        return payment;
    }

    /**
     * 生成支付单号：PAY + 17 位时间戳 + 4 位随机数
     */
    private String generatePaymentNo() {
        String timePart = LocalDateTime.now().format(PAY_NO_FORMATTER);
        int random = ThreadLocalRandom.current().nextInt(1000, 10000);
        return "PAY" + timePart + random;
    }
}
