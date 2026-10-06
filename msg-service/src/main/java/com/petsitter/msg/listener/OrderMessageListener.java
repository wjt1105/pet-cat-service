package com.petsitter.msg.listener;

import com.petsitter.msg.config.RabbitMQConfig;
import com.petsitter.msg.message.OrderMessage;
import com.petsitter.msg.service.NotifyService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 下单消息消费者
 * <p>
 * 手动 ACK：业务处理成功才确认，处理异常则拒绝并重回队列（requeue），保证消息不丢失。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderMessageListener {

    private final NotifyService notifyService;

    /**
     * 监听下单成功队列
     *
     * @param message     消息体（JSON 反序列化为 OrderMessage）
     * @param channel     RabbitMQ Channel（手动确认用）
     * @param deliveryTag 消息投递标签
     */
    @RabbitListener(queues = RabbitMQConfig.ORDER_CREATE_QUEUE)
    public void onOrderCreated(@Payload OrderMessage message,
                               Channel channel,
                               @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        try {
            log.info("收到下单成功消息：orderNo={}, userId={}", message.getOrderNo(), message.getUserId());
            notifyService.sendOrderCreatedNotify(message);
            // 处理成功，手动确认
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("下单消息处理失败：orderNo={}，原因：{}", message.getOrderNo(), e.getMessage(), e);
            // 处理失败：拒绝并重回队列，等待重投
            channel.basicNack(deliveryTag, false, true);
        }
    }
}
