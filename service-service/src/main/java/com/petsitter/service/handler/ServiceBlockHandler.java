package com.petsitter.service.handler;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.petsitter.common.entity.PetService;
import com.petsitter.common.result.Result;
import com.petsitter.common.result.ResultCode;
import com.petsitter.service.dto.ServicePageQuery;
import com.petsitter.service.dto.StockDeductDTO;
import lombok.extern.slf4j.Slf4j;

/**
 * Sentinel 限流/熔断降级处理（方法必须为 static，参数列表与原方法一致并追加 BlockException）
 */
@Slf4j
public final class ServiceBlockHandler {

    private ServiceBlockHandler() {
    }

    public static Result<IPage<PetService>> listBlockHandler(ServicePageQuery query, BlockException ex) {
        log.warn("服务列表触发 Sentinel 规则：{}", ex.getClass().getSimpleName());
        return Result.error(ResultCode.SERVICE_DEGRADED);
    }

    public static Result<PetService> detailBlockHandler(Long id, BlockException ex) {
        log.warn("服务详情触发 Sentinel 规则：id={}, rule={}", id, ex.getClass().getSimpleName());
        return Result.error(ResultCode.SERVICE_DEGRADED);
    }

    public static Result<Void> deductBlockHandler(StockDeductDTO dto, BlockException ex) {
        log.warn("库存扣减触发 Sentinel 规则：serviceId={}, rule={}",
                dto.getServiceId(), ex.getClass().getSimpleName());
        return Result.error(ResultCode.SERVICE_DEGRADED);
    }
}
