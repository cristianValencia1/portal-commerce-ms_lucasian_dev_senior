package com.christian.sales.domain.model;

import com.christian.sales.domain.enums.OutboxStatus;
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
public class OutboxEvent {
    private UUID id;
    private UUID eventId;
    private UUID aggregateId;
    private String aggregateType;
    private String eventType;
    private String payload;
    private OutboxStatus status;
    private Integer retryCount;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
}
