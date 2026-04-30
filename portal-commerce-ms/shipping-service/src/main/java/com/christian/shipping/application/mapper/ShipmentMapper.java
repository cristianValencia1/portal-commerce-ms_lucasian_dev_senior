package com.christian.shipping.application.mapper;

import com.christian.shipping.application.dto.SaleCreatedEvent;
import com.christian.shipping.application.dto.SaleCreatedPayload;
import com.christian.shipping.application.dto.ShipmentResponse;
import com.christian.shipping.application.dto.ShipmentTraceResponse;
import com.christian.shipping.domain.model.Shipment;
import com.christian.shipping.domain.model.ShipmentTrace;

public final class ShipmentMapper {

    private ShipmentMapper() {
    }

    public static ShipmentResponse toShipmentResponse(Shipment shipment) {
        if (shipment == null) {
            return null;
        }
        return ShipmentResponse.builder()
                .id(shipment.getId())
                .saleId(shipment.getSaleId())
                .shipmentNumber(shipment.getShipmentNumber())
                .status(shipment.getStatus().name())
                .correlationId(shipment.getCorrelationId())
                .createdAt(shipment.getCreatedAt())
                .updatedAt(shipment.getUpdatedAt())
                .build();
    }

    public static ShipmentTraceResponse toShipmentTraceResponse(ShipmentTrace trace) {
        if (trace == null) {
            return null;
        }
        return ShipmentTraceResponse.builder()
                .id(trace.getId())
                .shipmentId(trace.getShipmentId())
                .saleId(trace.getSaleId())
                .traceType(trace.getTraceType().name())
                .description(trace.getDescription())
                .status(trace.getStatus().name())
                .correlationId(trace.getCorrelationId())
                .createdAt(trace.getCreatedAt())
                .build();
    }

    public static SaleCreatedPayload getPayload(SaleCreatedEvent event) {
        return event == null ? null : event.getPayload();
    }
}
