package com.petsitter.order.mq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 下单成功事件消息体（供 msg-service 等下游消费，如发送预约通知）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 订单号 */
    private String orderNo;

    /** 下单用户 ID */
    private Long userId;

    /** 服务标题 */
    private String serviceTitle;

    /** 订单金额 */
    private BigDecimal amount;

    /** 预约上门时间 */
    private LocalDateTime appointmentTime;

    /** 下单时间 */
    private LocalDateTime createTime;
}
