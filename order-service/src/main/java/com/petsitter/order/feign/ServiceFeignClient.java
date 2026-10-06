package com.petsitter.order.feign;

import com.petsitter.common.entity.PetService;
import com.petsitter.common.result.Result;
import com.petsitter.order.feign.dto.StockDeductRequest;
import com.petsitter.order.feign.fallback.ServiceFeignFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * service-service 远程调用客户端
 * 注意：服务间直连，路径使用 service-service 的真实 Mapping（不含网关的 /api/service 前缀）
 */
@FeignClient(name = "service-service", fallbackFactory = ServiceFeignFallbackFactory.class)
public interface ServiceFeignClient {

    /**
     * 根据 ID 查询喂猫服务详情
     */
    @GetMapping("/{id}")
    Result<PetService> getServiceById(@PathVariable("id") Long id);

    /**
     * 扣减服务库存
     */
    @PostMapping("/stock/deduct")
    Result<Void> deductStock(@RequestBody StockDeductRequest request);
}
