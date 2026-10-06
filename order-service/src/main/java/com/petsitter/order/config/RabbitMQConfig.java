package com.petsitter.order.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 配置：下单成功事件的交换机、队列、绑定关系
 */
@Configuration
public class RabbitMQConfig {

    /** 订单事件交换机 */
    public static final String ORDER_EXCHANGE = "order.exchange";

    /** 下单成功队列 */
    public static final String ORDER_CREATE_QUEUE = "order.create.queue";

    /** 下单成功路由键 */
    public static final String ORDER_CREATE_ROUTING_KEY = "order.create";

    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(ORDER_EXCHANGE, true, false);
    }

    @Bean
    public Queue orderCreateQueue() {
        return new Queue(ORDER_CREATE_QUEUE, true);
    }

    @Bean
    public Binding orderCreateBinding(Queue orderCreateQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(orderCreateQueue)
                .to(orderExchange)
                .with(ORDER_CREATE_ROUTING_KEY);
    }

    /**
     * 消息使用 JSON 序列化（默认是 JDK 序列化，不可读且跨语言不友好）
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
