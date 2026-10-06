package com.petsitter.order.feign.fallback;

import com.petsitter.common.entity.PetService;
import com.petsitter.common.result.Result;
import com.petsitter.common.result.ResultCode;
import com.petsitter.order.feign.ServiceFeignClient;
import com.petsitter.order.feign.dto.StockDeductRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * service-service Feign 熔断/异常降级工厂
 * 相比直接 fallback，可拿到触发降级的具体异常原因，便于定位。
 * 降级时统一返回 SERVICE_DEGRADED，业务侧据此抛异常并让 Seata 回滚。
 */
@Slf4j
@Component
public class ServiceFeignFallbackFactory implements FallbackFactory<ServiceFeignClient> {

    @Override
    public ServiceFeignClient create(Throwable cause) {
        log.error("调用 service-service 触发熔断或异常：{}", cause.getMessage());

        return new ServiceFeignClient() {
            @Override
            public Result<PetService> getServiceById(Long id) {
                return Result.error(ResultCode.SERVICE_DEGRADED);
            }

            @Override
            public Result<Void> deductStock(StockDeductRequest request) {
                return Result.error(ResultCode.SERVICE_DEGRADED);
            }
        };
    }
}
