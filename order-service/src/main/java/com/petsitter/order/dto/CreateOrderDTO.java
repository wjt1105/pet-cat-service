package com.petsitter.order.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 下单请求
 */
@Data
public class CreateOrderDTO {

    /** 喂猫服务 ID */
    @NotNull(message = "服务ID不能为空")
    private Long serviceId;

    /** 预约上门时间 */
    @NotNull(message = "预约上门时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime appointmentTime;

    /** 服务地址 */
    @NotBlank(message = "服务地址不能为空")
    private String serviceAddress;

    /** 备注（可选） */
    private String remark;
}
