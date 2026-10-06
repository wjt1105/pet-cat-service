package com.petsitter.pay.feign;

import com.petsitter.common.entity.Orders;
import com.petsitter.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * order-service 远程调用客户端
 * 支付成功后回调 order-service 标记订单已支付
 */
@FeignClient(name = "order-service")
public interface OrderFeignClient {

    /**
     * 根据订单号查询订单详情（支付前校验）
     */
    @GetMapping("/{orderNo}")
    Result<Orders> getByOrderNo(@PathVariable("orderNo") String orderNo);

    /**
     * 标记订单已支付（内部调用接口，无网关前缀）
     */
    @PostMapping("/{orderNo}/paid")
    Result<Boolean> markPaid(@PathVariable("orderNo") String orderNo);
}
