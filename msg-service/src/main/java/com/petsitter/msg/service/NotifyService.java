package com.petsitter.msg.service;

import com.petsitter.msg.message.OrderMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

/**
 * 通知服务：模拟发送短信 / 站内信
 * 实际项目中可在此对接阿里云短信、微信模板消息等渠道
 */
@Slf4j
@Service
public class NotifyService {

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * 发送下单成功通知（模拟实现：输出日志即视为已发送）
     */
    public void sendOrderCreatedNotify(OrderMessage message) {
        String appointmentTime = message.getAppointmentTime() != null
                ? message.getAppointmentTime().format(TIME_FORMATTER) : "待定";

        // 模拟短信通知
        log.info("============ 模拟短信通知 ============");
        log.info("【萌宠到家】尊敬的用户，您已成功预约「{}」，", message.getServiceTitle());
        log.info("订单号：{}，金额：{} 元，", message.getOrderNo(), message.getAmount());
        log.info("上门时间：{}。请及时完成支付，铲屎官将准时为您服务！");
        log.info("=====================================");

        // 模拟站内信通知
        log.info("[站内信] 用户 {} 的订单 {} 已创建，等待支付", message.getUserId(), message.getOrderNo());
    }
}
