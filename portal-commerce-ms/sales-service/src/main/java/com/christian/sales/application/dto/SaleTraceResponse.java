package com.christian.sales.application.dto;

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
public class SaleTraceResponse {
    private UUID id;
    private UUID saleId;
    private String traceType;
    private String description;
    private String status;
    private UUID correlationId;
    private LocalDateTime createdAt;
}
