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
public class SaleCreatedEvent {
    private UUID eventId;
    private String eventType;
    private LocalDateTime occurredAt;
    private UUID correlationId;
    private SaleCreatedPayload payload;
}
