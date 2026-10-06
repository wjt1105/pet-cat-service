package com.petsitter.common.result;

import lombok.Getter;

/**
 * 统一响应状态码
 */
@Getter
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "没有访问权限"),
    NOT_FOUND(404, "请求资源不存在"),
    SYSTEM_ERROR(500, "系统繁忙，请稍后再试"),

    // 用户模块 1xxx
    USER_NOT_FOUND(1001, "用户不存在"),
    USERNAME_ALREADY_EXISTS(1002, "用户名已存在"),
    PASSWORD_ERROR(1003, "用户名或密码错误"),
    USER_DISABLED(1004, "账号已被禁用"),

    // 服务模块 2xxx
    SERVICE_NOT_FOUND(2001, "喂猫服务不存在"),
    SERVICE_OFFLINE(2002, "服务已下架"),
    SERVICE_STOCK_NOT_ENOUGH(2003, "服务库存不足"),
    SERVICE_DEGRADED(2004, "服务触发限流或熔断降级，请稍后再试"),

    // 订单模块 3xxx
    ORDER_NOT_FOUND(3001, "订单不存在"),
    ORDER_STATUS_ERROR(3002, "订单状态不允许此操作"),
    ORDER_ALREADY_PAID(3003, "订单已支付，请勿重复支付"),
    ORDER_CANNOT_CANCEL(3004, "当前订单不可取消"),
    ORDER_CREATE_BUSY(3005, "下单人数过多，请稍后再试"),

    // 支付模块 4xxx
    PAYMENT_NOT_FOUND(4001, "支付单不存在"),
    PAYMENT_FAILED(4002, "支付失败"),

    // 消息模块 5xxx
    MESSAGE_SEND_FAILED(5001, "消息发送失败");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
