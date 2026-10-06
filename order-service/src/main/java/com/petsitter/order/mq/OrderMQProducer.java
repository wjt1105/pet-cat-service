package com.petsitter.order.mq;

import com.petsitter.order.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * 订单消息生产者
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderMQProducer {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 发送下单成功事件消息
     * 调用时机：全局事务提交成功之后，保证"下单成功"才发消息。
     */
    public void sendOrderCreated(OrderMessage message) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_EXCHANGE,
                RabbitMQConfig.ORDER_CREATE_ROUTING_KEY,
                message);
        log.info("下单成功消息已发送：orderNo={}", message.getOrderNo());
    }
}
