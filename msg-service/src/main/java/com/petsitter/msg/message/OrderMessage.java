package com.petsitter.msg.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 下单成功事件消息体
 * 字段与 order-service 生产的消息保持一致（靠 JSON 字段名反序列化，无需共享类）
 */
@Data
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime appointmentTime;

    /** 下单时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
