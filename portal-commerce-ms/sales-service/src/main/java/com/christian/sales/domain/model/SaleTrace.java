package com.christian.sales.domain.model;

import com.christian.sales.domain.enums.TraceType;
import com.christian.sales.domain.enums.SaleStatus;
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
public class SaleTrace {
    private UUID id;
    private UUID saleId;
    private TraceType traceType;
    private String description;
    private SaleStatus status;
    private UUID correlationId;
    private LocalDateTime createdAt;
}
