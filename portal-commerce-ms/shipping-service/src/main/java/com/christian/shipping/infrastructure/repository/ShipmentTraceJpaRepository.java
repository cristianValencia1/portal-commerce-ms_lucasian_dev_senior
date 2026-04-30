package com.christian.shipping.infrastructure.repository;

import com.christian.shipping.infrastructure.entity.ShipmentTraceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ShipmentTraceJpaRepository extends JpaRepository<ShipmentTraceEntity, UUID> {
    List<ShipmentTraceEntity> findBySaleId(UUID saleId);
}
