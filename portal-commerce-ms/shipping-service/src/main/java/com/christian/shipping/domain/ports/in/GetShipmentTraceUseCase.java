package com.christian.shipping.domain.ports.in;

import com.christian.shipping.application.dto.ShipmentTraceResponse;

import java.util.List;
import java.util.UUID;

public interface GetShipmentTraceUseCase {
    List<ShipmentTraceResponse> getShipmentTrace(UUID saleId);
}
