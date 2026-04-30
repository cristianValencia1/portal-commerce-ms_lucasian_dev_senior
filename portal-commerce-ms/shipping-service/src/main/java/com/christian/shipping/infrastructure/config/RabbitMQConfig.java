package com.christian.shipping.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${app.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${app.rabbitmq.queue}")
    private String queueName;

    @Value("${app.rabbitmq.routing-key}")
    private String routingKey;

    @Bean
    public DirectExchange shippingExchange() {
        return new DirectExchange(exchangeName, true, false);
    }

    @Bean
    public Queue saleCreatedQueue() {
        return new Queue(queueName, true);
    }

    @Bean
    public Binding saleCreatedBinding(Queue saleCreatedQueue, DirectExchange shippingExchange) {
        return BindingBuilder.bind(saleCreatedQueue).to(shippingExchange).with(routingKey);
    }
}
