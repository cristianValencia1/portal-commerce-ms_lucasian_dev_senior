package com.christian.shipping.domain.ports.out;

import com.christian.shipping.domain.model.ShipmentTrace;

import java.util.List;
import java.util.UUID;

public interface ShipmentTraceRepositoryPort {
    ShipmentTrace save(ShipmentTrace trace);
    List<ShipmentTrace> findBySaleId(UUID saleId);
}
