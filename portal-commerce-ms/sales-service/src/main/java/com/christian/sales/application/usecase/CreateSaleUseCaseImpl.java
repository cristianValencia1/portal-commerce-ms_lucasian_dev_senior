package com.christian.sales.application.usecase;

import com.christian.sales.application.dto.CreateSaleRequest;
import com.christian.sales.application.dto.CreateSaleResponse;
import com.christian.sales.application.mapper.SaleMapper;
import com.christian.sales.domain.enums.OutboxStatus;
import com.christian.sales.domain.enums.SaleStatus;
import com.christian.sales.domain.enums.TraceType;
import com.christian.sales.domain.exception.BusinessValidationException;
import com.christian.sales.domain.model.OutboxEvent;
import com.christian.sales.domain.model.Sale;
import com.christian.sales.domain.model.SaleTrace;
import com.christian.sales.domain.ports.in.CreateSaleUseCase;
import com.christian.sales.domain.ports.out.EventPublisherPort;
import com.christian.sales.domain.ports.out.OutboxEventRepositoryPort;
import com.christian.sales.domain.ports.out.SaleRepositoryPort;
import com.christian.sales.domain.ports.out.SaleTraceRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CreateSaleUseCaseImpl implements CreateSaleUseCase {

    private final SaleRepositoryPort saleRepositoryPort;
    private final SaleTraceRepositoryPort saleTraceRepositoryPort;
    private final OutboxEventRepositoryPort outboxEventRepositoryPort;
    private final EventPublisherPort eventPublisherPort;

    public CreateSaleUseCaseImpl(
            SaleRepositoryPort saleRepositoryPort,
            SaleTraceRepositoryPort saleTraceRepositoryPort,
            OutboxEventRepositoryPort outboxEventRepositoryPort,
            EventPublisherPort eventPublisherPort) {
        this.saleRepositoryPort = saleRepositoryPort;
        this.saleTraceRepositoryPort = saleTraceRepositoryPort;
        this.outboxEventRepositoryPort = outboxEventRepositoryPort;
        this.eventPublisherPort = eventPublisherPort;
    }

    @Override
    @Transactional
    public CreateSaleResponse create(CreateSaleRequest request) {
        if (request == null) {
            throw new BusinessValidationException("CreateSaleRequest cannot be null");
        }

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BusinessValidationException("At least one sale item is required");
        }

        UUID correlationId = UUID.randomUUID();
        var items = SaleMapper.toSaleItems(request.getItems());

        items.forEach(item -> {
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new BusinessValidationException("quantity must be greater than zero");
            }
            if (item.getUnitPrice() == null || item.getUnitPrice().signum() <= 0) {
                throw new BusinessValidationException("unitPrice must be greater than zero");
            }
        });

        var sale = Sale.builder()
                .id(UUID.randomUUID())
                .customer(SaleMapper.toCustomer(request.getCustomer()))
                .items(items)
                .totalAmount(items.stream().map(it -> it.getSubtotal()).reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add))
                .status(SaleStatus.REGISTERED)
                .correlationId(correlationId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        recordTrace(sale, TraceType.SALE_RECEIVED, "Sale received");
        recordTrace(sale, TraceType.SALE_VALIDATED, "Sale validated");

        var persistedSale = saleRepositoryPort.save(sale);
        recordTrace(persistedSale, TraceType.SALE_REGISTERED, "Sale registered");

        var outboxEvent = OutboxEvent.builder()
                .id(UUID.randomUUID())
                .eventId(UUID.randomUUID())
                .aggregateId(persistedSale.getId())
                .aggregateType("Sale")
                .eventType("SALE_CREATED")
                .payload(buildPayload(persistedSale))
                .status(OutboxStatus.PENDING)
                .retryCount(0)
                .createdAt(LocalDateTime.now())
                .processedAt(null)
                .build();

        outboxEventRepositoryPort.save(outboxEvent);
        recordTrace(persistedSale, TraceType.OUTBOX_EVENT_CREATED, "Outbox event created");

        eventPublisherPort.publishSaleCreated(persistedSale);
        recordTrace(persistedSale, TraceType.EVENT_PUBLISHED, "Sale created event published");

        return CreateSaleResponse.builder()
                .saleId(persistedSale.getId())
                .status(persistedSale.getStatus().name())
                .correlationId(persistedSale.getCorrelationId())
                .build();
    }

    private void recordTrace(Sale sale, TraceType traceType, String description) {
        var trace = SaleTrace.builder()
                .id(UUID.randomUUID())
                .saleId(sale.getId())
                .traceType(traceType)
                .description(description)
                .status(sale.getStatus())
                .correlationId(sale.getCorrelationId())
                .createdAt(LocalDateTime.now())
                .build();
        saleTraceRepositoryPort.save(trace);
    }

    private String buildPayload(Sale sale) {
        return "{\"saleId\":\"" + sale.getId() + "\",\"status\":\"" + sale.getStatus() + "\",\"correlationId\":\"" + sale.getCorrelationId() + "\"}";
    }
}
