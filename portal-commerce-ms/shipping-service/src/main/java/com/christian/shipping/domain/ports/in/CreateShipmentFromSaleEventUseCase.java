package com.christian.shipping.domain.ports.in;

import com.christian.shipping.application.dto.SaleCreatedEvent;

public interface CreateShipmentFromSaleEventUseCase {
    void createShipmentFromEvent(SaleCreatedEvent event);
}
