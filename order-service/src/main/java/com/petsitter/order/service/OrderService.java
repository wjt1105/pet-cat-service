package com.petsitter.order.service;

import com.petsitter.common.entity.Orders;
import com.petsitter.order.dto.CreateOrderDTO;

/**
 * 订单服务
 */
public interface OrderService {

    /**
     * 创建订单
     *
     * @param userId 下单用户 ID（网关透传）
     * @param dto    下单请求
     * @return 订单号
     */
    String createOrder(Long userId, CreateOrderDTO dto);

    /**
     * 根据订单号查询订单详情
     */
    Orders getByOrderNo(String orderNo);

    /**
     * 支付回调：标记订单已支付（仅供内部服务调用）
     *
     * @param orderNo 订单号
     * @return true-成功修改
     */
    boolean markPaid(String orderNo);
}
