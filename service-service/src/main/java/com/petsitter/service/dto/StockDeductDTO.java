package com.petsitter.service.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 库存扣减请求（供订单服务远程调用）
 */
@Data
public class StockDeductDTO {

    @NotNull(message = "服务ID不能为空")
    private Long serviceId;

    @NotNull(message = "扣减数量不能为空")
    @Min(value = 1, message = "扣减数量至少为 1")
    private Integer count;
}
