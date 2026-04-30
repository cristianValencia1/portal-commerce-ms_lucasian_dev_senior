package com.christian.shipping.application.usecase;

import com.christian.shipping.application.dto.ShipmentResponse;
import com.christian.shipping.application.mapper.ShipmentMapper;
import com.christian.shipping.domain.exception.ResourceNotFoundException;
import com.christian.shipping.domain.ports.in.GetShipmentBySaleIdUseCase;
import com.christian.shipping.domain.ports.out.ShipmentRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetShipmentBySaleIdUseCaseImpl implements GetShipmentBySaleIdUseCase {

    private final ShipmentRepositoryPort shipmentRepositoryPort;

    public GetShipmentBySaleIdUseCaseImpl(ShipmentRepositoryPort shipmentRepositoryPort) {
        this.shipmentRepositoryPort = shipmentRepositoryPort;
    }

    @Override
    public ShipmentResponse getShipmentBySaleId(UUID saleId) {
        return shipmentRepositoryPort.findBySaleId(saleId)
                .map(ShipmentMapper::toShipmentResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found for saleId " + saleId));
    }
}
