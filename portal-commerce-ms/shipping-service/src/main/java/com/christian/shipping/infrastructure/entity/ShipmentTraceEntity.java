package com.christian.shipping.infrastructure.entity;

import com.christian.shipping.domain.enums.ShipmentStatus;
import com.christian.shipping.domain.enums.ShipmentTraceType;
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
@Table(name = "shipment_trace", schema = "shipping")
public class ShipmentTraceEntity {
    @Id
    private UUID id;

    private UUID shipmentId;

    @Column(nullable = false)
    private UUID saleId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipmentTraceType traceType;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipmentStatus status;

    @Column(nullable = false)
    private UUID correlationId;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
