package com.petsitter.pay.service;

import com.petsitter.common.entity.Payment;
import com.petsitter.pay.dto.PayRequestDTO;

/**
 * 支付服务
 */
public interface PaymentService {

    /**
     * 模拟支付：校验订单 → 模拟调用第三方支付 → 落支付单 → 回调改订单状态
     *
     * @param userId 支付用户 ID（网关透传）
     * @param dto    支付请求
     * @return 支付单号
     */
    String pay(Long userId, PayRequestDTO dto);

    /**
     * 根据支付单号查询支付单
     */
    Payment getByPaymentNo(String paymentNo);
}
