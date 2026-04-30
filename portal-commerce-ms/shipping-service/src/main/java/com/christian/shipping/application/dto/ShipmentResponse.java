package com.christian.shipping.application.dto;

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
public class ShipmentResponse {
    private UUID id;
    private UUID saleId;
    private String shipmentNumber;
    private String status;
    private UUID correlationId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
