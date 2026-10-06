package com.petsitter.order.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.petsitter.common.entity.Orders;
import com.petsitter.common.result.Result;
import com.petsitter.order.dto.CreateOrderDTO;
import com.petsitter.order.handler.OrderBlockHandler;
import com.petsitter.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 订单接口
 */
@Validated
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 创建订单
     * X-User-Id 由网关鉴权后透传，服务本身不直接解析 JWT
     */
    @PostMapping("/create")
    @SentinelResource(value = "orderCreate",
            blockHandlerClass = OrderBlockHandler.class,
            blockHandler = "createBlockHandler")
    public Result<String> create(@RequestHeader("X-User-Id") Long userId,
                                 @Valid @RequestBody CreateOrderDTO dto) {
        return Result.success(orderService.createOrder(userId, dto));
    }

    /**
     * 根据订单号查询详情
     */
    @GetMapping("/{orderNo}")
    public Result<Orders> detail(@PathVariable String orderNo) {
        return Result.success(orderService.getByOrderNo(orderNo));
    }

    /**
     * 支付回调：标记订单已支付（内部调用，无需网关鉴权）
     */
    @PostMapping("/{orderNo}/paid")
    public Result<Boolean> markPaid(@PathVariable String orderNo) {
        return Result.success(orderService.markPaid(orderNo));
    }
}
