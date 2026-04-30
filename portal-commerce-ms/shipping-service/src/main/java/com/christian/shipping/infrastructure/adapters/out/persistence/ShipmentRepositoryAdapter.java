package com.christian.shipping.infrastructure.adapters.out.persistence;

import com.christian.shipping.domain.model.Shipment;
import com.christian.shipping.domain.ports.out.ShipmentRepositoryPort;
import com.christian.shipping.infrastructure.entity.ShipmentEntity;
import com.christian.shipping.infrastructure.repository.ShipmentJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class ShipmentRepositoryAdapter implements ShipmentRepositoryPort {

    private final ShipmentJpaRepository shipmentJpaRepository;

    public ShipmentRepositoryAdapter(ShipmentJpaRepository shipmentJpaRepository) {
        this.shipmentJpaRepository = shipmentJpaRepository;
    }

    @Override
    public Shipment save(Shipment shipment) {
        ShipmentEntity entity = toEntity(shipment);
        ShipmentEntity saved = shipmentJpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Shipment> findBySaleId(UUID saleId) {
        return shipmentJpaRepository.findBySaleId(saleId).map(this::toDomain);
    }

    @Override
    public Optional<Shipment> findByShipmentNumber(String shipmentNumber) {
        return shipmentJpaRepository.findByShipmentNumber(shipmentNumber).map(this::toDomain);
    }

    private ShipmentEntity toEntity(Shipment shipment) {
        return ShipmentEntity.builder()
                .id(shipment.getId())
                .saleId(shipment.getSaleId())
                .shipmentNumber(shipment.getShipmentNumber())
                .status(shipment.getStatus())
                .correlationId(shipment.getCorrelationId())
                .createdAt(shipment.getCreatedAt())
                .updatedAt(shipment.getUpdatedAt())
                .build();
    }

    private Shipment toDomain(ShipmentEntity entity) {
        return Shipment.builder()
                .id(entity.getId())
                .saleId(entity.getSaleId())
                .shipmentNumber(entity.getShipmentNumber())
                .status(entity.getStatus())
                .correlationId(entity.getCorrelationId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
