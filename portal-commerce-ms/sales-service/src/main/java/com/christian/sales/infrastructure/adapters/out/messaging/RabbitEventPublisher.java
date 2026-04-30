package com.christian.sales.infrastructure.adapters.out.messaging;

import com.christian.sales.domain.model.Sale;
import com.christian.sales.domain.ports.out.EventPublisherPort;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class RabbitEventPublisher implements EventPublisherPort {

    private static final String EXCHANGE_NAME = "commerce.exchange";
    private static final String ROUTING_KEY = "sales.created";

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public RabbitEventPublisher(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publishSaleCreated(Sale sale) {
        try {
            SaleCreatedPayload payload = new SaleCreatedPayload(
                    sale.getId().toString(),
                    sale.getCorrelationId().toString(),
                    sale.getCustomer().getEmail(),
                    sale.getCustomer().getPhone(),
                    sale.getCustomer().getPostalCode());
            SaleCreatedEvent event = new SaleCreatedEvent(
                    UUID.randomUUID(),
                    "SALE_CREATED",
                    LocalDateTime.now(),
                    sale.getCorrelationId(),
                    payload);
            String message = objectMapper.writeValueAsString(event);
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, message);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to serialize sale created event", exception);
        }
    }

    private record SaleCreatedEvent(UUID eventId, String eventType, LocalDateTime occurredAt, UUID correlationId, SaleCreatedPayload payload) {
    }

    private record SaleCreatedPayload(String saleId, String correlationId, String customerEmail, String customerPhone, String postalCode) {
    }
}
