package com.christian.sales.infrastructure.config;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String SALES_EXCHANGE = "sales.exchange";

    @Bean
    public DirectExchange salesExchange() {
        return new DirectExchange(SALES_EXCHANGE, true, false);
    }
}
