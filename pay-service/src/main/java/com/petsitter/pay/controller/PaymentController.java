package com.petsitter.pay.controller;

import com.petsitter.common.entity.Payment;
import com.petsitter.common.result.Result;
import com.petsitter.pay.dto.PayRequestDTO;
import com.petsitter.pay.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 支付接口
 */
@Validated
@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 模拟支付（X-User-Id 由网关鉴权后透传）
     */
    @PostMapping("/pay")
    public Result<String> pay(@RequestHeader("X-User-Id") Long userId,
                              @Valid @RequestBody PayRequestDTO dto) {
        return Result.success(paymentService.pay(userId, dto));
    }

    /**
     * 根据支付单号查询支付单
     */
    @GetMapping("/{paymentNo}")
    public Result<Payment> detail(@PathVariable String paymentNo) {
        return Result.success(paymentService.getByPaymentNo(paymentNo));
    }
}
