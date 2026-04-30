package com.christian.shipping.domain.ports.out;

import com.christian.shipping.domain.model.Shipment;

import java.util.Optional;
import java.util.UUID;

public interface ShipmentRepositoryPort {
    Shipment save(Shipment shipment);
    Optional<Shipment> findBySaleId(UUID saleId);
    Optional<Shipment> findByShipmentNumber(String shipmentNumber);
}
