package com.christian.shipping.infrastructure.adapters.in.messaging;

import com.christian.shipping.application.dto.SaleCreatedEvent;
import com.christian.shipping.domain.ports.in.CreateShipmentFromSaleEventUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class SaleCreatedEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SaleCreatedEventListener.class);

    private final CreateShipmentFromSaleEventUseCase createShipmentFromSaleEventUseCase;
    private final ObjectMapper objectMapper;

    public SaleCreatedEventListener(CreateShipmentFromSaleEventUseCase createShipmentFromSaleEventUseCase,
                                    ObjectMapper objectMapper) {
        this.createShipmentFromSaleEventUseCase = createShipmentFromSaleEventUseCase;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "${app.rabbitmq.queue}")
    public void handleSaleCreated(@Payload String message) {
        try {
            SaleCreatedEvent event = objectMapper.readValue(message, SaleCreatedEvent.class);
            LOGGER.info("Consumed SALE_CREATED message: {}", event.getEventId());
            createShipmentFromSaleEventUseCase.createShipmentFromEvent(event);
        } catch (Exception ex) {
            LOGGER.error("Failed to consume SALE_CREATED message", ex);
            throw new RuntimeException(ex);
        }
    }
}
