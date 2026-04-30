package com.christian.shipping.application.usecase;

import com.christian.shipping.application.dto.SaleCreatedEvent;
import com.christian.shipping.domain.enums.ShipmentStatus;
import com.christian.shipping.domain.enums.ShipmentTraceType;
import com.christian.shipping.domain.exception.BusinessValidationException;
import com.christian.shipping.domain.model.ProcessedEvent;
import com.christian.shipping.domain.model.Shipment;
import com.christian.shipping.domain.model.ShipmentTrace;
import com.christian.shipping.domain.ports.in.CreateShipmentFromSaleEventUseCase;
import com.christian.shipping.domain.ports.out.ProcessedEventRepositoryPort;
import com.christian.shipping.domain.ports.out.ShipmentRepositoryPort;
import com.christian.shipping.domain.ports.out.ShipmentTraceRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CreateShipmentFromSaleEventUseCaseImpl implements CreateShipmentFromSaleEventUseCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateShipmentFromSaleEventUseCaseImpl.class);

    private final ShipmentRepositoryPort shipmentRepositoryPort;
    private final ShipmentTraceRepositoryPort shipmentTraceRepositoryPort;
    private final ProcessedEventRepositoryPort processedEventRepositoryPort;

    public CreateShipmentFromSaleEventUseCaseImpl(
            ShipmentRepositoryPort shipmentRepositoryPort,
            ShipmentTraceRepositoryPort shipmentTraceRepositoryPort,
            ProcessedEventRepositoryPort processedEventRepositoryPort) {
        this.shipmentRepositoryPort = shipmentRepositoryPort;
        this.shipmentTraceRepositoryPort = shipmentTraceRepositoryPort;
        this.processedEventRepositoryPort = processedEventRepositoryPort;
    }

    @Override
    @Transactional
    public void createShipmentFromEvent(SaleCreatedEvent event) {
        if (event == null) {
            throw new BusinessValidationException("SaleCreatedEvent cannot be null");
        }

        UUID eventId = event.getEventId();
        UUID correlationId = event.getCorrelationId();

        LOGGER.info("Received SALE_CREATED event: {} for correlationId={}", eventId, correlationId);
        recordTrace(null, event.getPayload().getSaleId(), ShipmentTraceType.EVENT_RECEIVED, "Event received", ShipmentStatus.PENDING, correlationId);

        if (processedEventRepositoryPort.findByEventId(eventId).isPresent()) {
            LOGGER.warn("Duplicate event ignored: {}", eventId);
            recordTrace(null, event.getPayload().getSaleId(), ShipmentTraceType.DUPLICATE_EVENT_IGNORED, "Duplicate event ignored", ShipmentStatus.PENDING, correlationId);
            return;
        }

        UUID saleId = event.getPayload().getSaleId();
        if (saleId == null) {
            throw new BusinessValidationException("saleId is required in event payload");
        }

        try {
            if (shipmentRepositoryPort.findBySaleId(saleId).isPresent()) {
                LOGGER.warn("Shipment already exists for saleId={}", saleId);
                recordTrace(null, saleId, ShipmentTraceType.DUPLICATE_EVENT_IGNORED, "Shipment already exists for saleId", ShipmentStatus.CREATED, correlationId);
                processedEventRepositoryPort.save(ProcessedEvent.builder()
                        .id(UUID.randomUUID())
                        .eventId(eventId)
                        .saleId(saleId)
                        .processedAt(LocalDateTime.now())
                        .build());
                return;
            }

            Shipment shipment = Shipment.builder()
                    .id(UUID.randomUUID())
                    .saleId(saleId)
                    .shipmentNumber(generateShipmentNumber())
                    .status(ShipmentStatus.CREATED)
                    .correlationId(correlationId)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            Shipment created = shipmentRepositoryPort.save(shipment);
            recordTrace(created.getId(), saleId, ShipmentTraceType.SHIPMENT_CREATED, "Shipment created", created.getStatus(), correlationId);
            processedEventRepositoryPort.save(ProcessedEvent.builder()
                    .id(UUID.randomUUID())
                    .eventId(eventId)
                    .saleId(saleId)
                    .processedAt(LocalDateTime.now())
                    .build());
        } catch (Exception ex) {
            LOGGER.error("Shipment creation failed for event {}", eventId, ex);
            recordTrace(null, saleId, ShipmentTraceType.SHIPMENT_FAILED, "Shipment creation failed: " + ex.getMessage(), ShipmentStatus.FAILED, correlationId);
            throw ex;
        }
    }

    private void recordTrace(UUID shipmentId,
                             UUID saleId,
                             ShipmentTraceType traceType,
                             String description,
                             ShipmentStatus status,
                             UUID correlationId) {
        shipmentTraceRepositoryPort.save(ShipmentTrace.builder()
                .id(UUID.randomUUID())
                .shipmentId(shipmentId)
                .saleId(saleId)
                .traceType(traceType)
                .description(description)
                .status(status)
                .correlationId(correlationId)
                .createdAt(LocalDateTime.now())
                .build());
    }

    private String generateShipmentNumber() {
        return "SHIP-" + UUID.randomUUID().toString();
    }
}
