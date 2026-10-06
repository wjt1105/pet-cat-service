package com.petsitter.service.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.petsitter.common.entity.PetService;
import com.petsitter.common.result.Result;
import com.petsitter.service.dto.ServicePageQuery;
import com.petsitter.service.dto.StockDeductDTO;
import com.petsitter.service.handler.ServiceBlockHandler;
import com.petsitter.service.service.PetServiceService;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 喂猫服务接口
 */
@Validated
@RestController
@RequiredArgsConstructor
public class PetServiceController {

    private final PetServiceService petServiceService;

    /**
     * 分页查询所有上架的上门喂猫服务
     */
    @GetMapping("/list")
    @SentinelResource(value = "serviceList",
            blockHandlerClass = ServiceBlockHandler.class,
            blockHandler = "listBlockHandler")
    public Result<IPage<PetService>> list(@Valid ServicePageQuery query) {
        return Result.success(petServiceService.pageList(query));
    }

    /**
     * 根据 ID 查询服务详情
     */
    @GetMapping("/{id}")
    @SentinelResource(value = "serviceDetail",
            blockHandlerClass = ServiceBlockHandler.class,
            blockHandler = "detailBlockHandler")
    public Result<PetService> detail(@PathVariable Long id) {
        return Result.success(petServiceService.getDetail(id));
    }

    /**
     * 扣减库存（供订单服务远程调用）
     */
    @PostMapping("/stock/deduct")
    @SentinelResource(value = "deductStock",
            blockHandlerClass = ServiceBlockHandler.class,
            blockHandler = "deductBlockHandler")
    public Result<Void> deductStock(@Valid @RequestBody StockDeductDTO dto) {
        petServiceService.deductStock(dto.getServiceId(), dto.getCount());
        return Result.success();
    }
}
