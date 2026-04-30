package com.christian.sales.infrastructure.entity;

import com.christian.sales.domain.enums.SaleStatus;
import com.christian.sales.domain.enums.TraceType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Entity
@Table(name = "sale_trace", schema = "sales")
public class SaleTraceEntity {
    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID saleId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TraceType traceType;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SaleStatus status;

    @Column(nullable = false)
    private UUID correlationId;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
