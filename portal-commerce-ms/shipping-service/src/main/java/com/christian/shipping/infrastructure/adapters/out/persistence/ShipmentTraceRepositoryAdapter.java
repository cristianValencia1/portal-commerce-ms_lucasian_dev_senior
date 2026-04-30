package com.christian.shipping.infrastructure.adapters.out.persistence;

import com.christian.shipping.domain.model.ShipmentTrace;
import com.christian.shipping.domain.ports.out.ShipmentTraceRepositoryPort;
import com.christian.shipping.infrastructure.entity.ShipmentTraceEntity;
import com.christian.shipping.infrastructure.repository.ShipmentTraceJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ShipmentTraceRepositoryAdapter implements ShipmentTraceRepositoryPort {

    private final ShipmentTraceJpaRepository shipmentTraceJpaRepository;

    public ShipmentTraceRepositoryAdapter(ShipmentTraceJpaRepository shipmentTraceJpaRepository) {
        this.shipmentTraceJpaRepository = shipmentTraceJpaRepository;
    }

    @Override
    public ShipmentTrace save(ShipmentTrace trace) {
        ShipmentTraceEntity entity = ShipmentTraceEntity.builder()
                .id(trace.getId())
                .shipmentId(trace.getShipmentId())
                .saleId(trace.getSaleId())
                .traceType(trace.getTraceType())
                .description(trace.getDescription())
                .status(trace.getStatus())
                .correlationId(trace.getCorrelationId())
                .createdAt(trace.getCreatedAt())
                .build();
        ShipmentTraceEntity saved = shipmentTraceJpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<ShipmentTrace> findBySaleId(UUID saleId) {
        return shipmentTraceJpaRepository.findBySaleId(saleId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private ShipmentTrace toDomain(ShipmentTraceEntity entity) {
        return ShipmentTrace.builder()
                .id(entity.getId())
                .shipmentId(entity.getShipmentId())
                .saleId(entity.getSaleId())
                .traceType(entity.getTraceType())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .correlationId(entity.getCorrelationId())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
