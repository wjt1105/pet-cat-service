package com.petsitter.order.handler;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.petsitter.common.result.Result;
import com.petsitter.common.result.ResultCode;
import com.petsitter.order.dto.CreateOrderDTO;
import lombok.extern.slf4j.Slf4j;

/**
 * 下单接口 Sentinel 限流/熔断处理
 */
@Slf4j
public final class OrderBlockHandler {

    private OrderBlockHandler() {
    }

    /**
     * 参数列表与原方法一致，末尾追加 BlockException
     */
    public static Result<String> createBlockHandler(Long userId, CreateOrderDTO dto, BlockException ex) {
        log.warn("下单接口触发 Sentinel 规则：userId={}, rule={}", userId, ex.getClass().getSimpleName());
        return Result.error(ResultCode.ORDER_CREATE_BUSY);
    }
}
