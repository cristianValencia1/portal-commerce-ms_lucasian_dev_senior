package com.christian.shipping.domain.model;

import com.christian.shipping.domain.enums.ShipmentTraceType;
import com.christian.shipping.domain.enums.ShipmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentTrace {
    private UUID id;
    private UUID shipmentId;
    private UUID saleId;
    private ShipmentTraceType traceType;
    private String description;
    private ShipmentStatus status;
    private UUID correlationId;
    private LocalDateTime createdAt;
}
