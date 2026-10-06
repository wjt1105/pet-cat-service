package com.petsitter.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付单
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("payment")
public class Payment extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 支付单号 */
    private String paymentNo;

    /** 订单 ID */
    private Long orderId;

    /** 订单号 */
    private String orderNo;

    /** 支付用户 ID */
    private Long userId;

    /** 支付金额（元） */
    private BigDecimal amount;

    /** 支付方式：1-微信 2-支付宝 */
    private Integer payType;

    /** 状态：0-待支付 1-支付成功 2-支付失败 3-已退款 */
    private Integer status;

    /** 第三方交易流水号 */
    private String tradeNo;

    /** 支付时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;
}
