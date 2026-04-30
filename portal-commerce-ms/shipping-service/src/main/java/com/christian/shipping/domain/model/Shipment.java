package com.christian.shipping.domain.model;

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
public class Shipment {
    private UUID id;
    private UUID saleId;
    private String shipmentNumber;
    private ShipmentStatus status;
    private UUID correlationId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
