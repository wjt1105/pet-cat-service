package com.petsitter.msg.config;

import com.petsitter.msg.message.OrderMessage;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * RabbitMQ 配置：声明与 order-service 相同的交换机/队列/绑定（重复声明幂等，保证启动顺序无关）
 */
@Configuration
public class RabbitMQConfig {

    /** 订单事件交换机（与 order-service 一致） */
    public static final String ORDER_EXCHANGE = "order.exchange";

    /** 下单成功队列（与 order-service 一致） */
    public static final String ORDER_CREATE_QUEUE = "order.create.queue";

    /** 下单成功路由键（与 order-service 一致） */
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
     * JSON 消息转换器。
     * 生产端发送时会在消息头写入 __TypeId__ = com.petsitter.order.mq.OrderMessage，
     * 本服务没有该类，通过 idClassMapping 映射到本地同结构类，避免跨服务共享代码。
     */
    @Bean
    public MessageConverter messageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("com.petsitter.order.mq.OrderMessage", OrderMessage.class);
        typeMapper.setIdClassMapping(idClassMapping);
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }
}
