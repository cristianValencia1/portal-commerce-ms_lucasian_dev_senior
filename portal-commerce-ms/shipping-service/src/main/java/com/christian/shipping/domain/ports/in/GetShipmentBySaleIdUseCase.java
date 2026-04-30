package com.christian.shipping.domain.ports.in;

import com.christian.shipping.application.dto.ShipmentResponse;

import java.util.UUID;

public interface GetShipmentBySaleIdUseCase {
    ShipmentResponse getShipmentBySaleId(UUID saleId);
}
