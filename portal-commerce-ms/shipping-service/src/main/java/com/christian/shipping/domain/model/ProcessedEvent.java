package com.christian.shipping.domain.model;

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
public class ProcessedEvent {
    private UUID id;
    private UUID eventId;
    private UUID saleId;
    private LocalDateTime processedAt;
}
