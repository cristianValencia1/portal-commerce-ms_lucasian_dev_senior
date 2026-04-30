package com.christian.shipping.application.usecase;

import com.christian.shipping.application.dto.ShipmentTraceResponse;
import com.christian.shipping.application.mapper.ShipmentMapper;
import com.christian.shipping.domain.ports.in.GetShipmentTraceUseCase;
import com.christian.shipping.domain.ports.out.ShipmentTraceRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GetShipmentTraceUseCaseImpl implements GetShipmentTraceUseCase {

    private final ShipmentTraceRepositoryPort shipmentTraceRepositoryPort;

    public GetShipmentTraceUseCaseImpl(ShipmentTraceRepositoryPort shipmentTraceRepositoryPort) {
        this.shipmentTraceRepositoryPort = shipmentTraceRepositoryPort;
    }

    @Override
    public List<ShipmentTraceResponse> getShipmentTrace(UUID saleId) {
        return shipmentTraceRepositoryPort.findBySaleId(saleId).stream()
                .map(ShipmentMapper::toShipmentTraceResponse)
                .collect(Collectors.toList());
    }
}
