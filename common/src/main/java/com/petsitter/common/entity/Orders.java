package com.petsitter.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("orders")
public class Orders extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 订单号 */
    private String orderNo;

    /** 下单用户 ID */
    private Long userId;

    /** 服务 ID */
    private Long serviceId;

    /** 铲屎官用户 ID */
    private Long sitterId;

    /** 服务标题快照 */
    private String serviceTitle;

    /** 服务单价快照（元） */
    private BigDecimal servicePrice;

    /** 预约上门时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime appointmentTime;

    /** 服务地址 */
    private String serviceAddress;

    /** 备注 */
    private String remark;

    /** 订单总金额（元） */
    private BigDecimal amount;

    /** 状态：0-待支付 1-待服务 2-已完成 3-已取消 */
    private Integer status;

    /** 支付时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;

    /** 完成时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime finishTime;

    /** 取消时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime cancelTime;
}
