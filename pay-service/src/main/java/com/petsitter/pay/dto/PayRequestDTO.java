package com.petsitter.pay.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 模拟支付请求
 */
@Data
public class PayRequestDTO {

    /** 订单号 */
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    /** 支付金额（元），须与订单金额一致 */
    @NotNull(message = "支付金额不能为空")
    private BigDecimal amount;

    /** 支付方式：1-微信 2-支付宝 */
    @NotNull(message = "支付方式不能为空")
    private Integer payType;
}
