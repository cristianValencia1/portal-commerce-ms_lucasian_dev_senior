package com.christian.shipping.infrastructure.repository;

import com.christian.shipping.infrastructure.entity.ShipmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ShipmentJpaRepository extends JpaRepository<ShipmentEntity, UUID> {
    Optional<ShipmentEntity> findBySaleId(UUID saleId);
    Optional<ShipmentEntity> findByShipmentNumber(String shipmentNumber);
}
