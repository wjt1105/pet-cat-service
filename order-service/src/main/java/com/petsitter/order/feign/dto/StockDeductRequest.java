package com.petsitter.order.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 远程扣库存请求体
 * 字段与 service-service 端 StockDeductDTO 保持一致（靠 JSON 字段名匹配）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockDeductRequest {

    /** 服务 ID */
    private Long serviceId;

    /** 扣减数量 */
    private Integer count;
}
