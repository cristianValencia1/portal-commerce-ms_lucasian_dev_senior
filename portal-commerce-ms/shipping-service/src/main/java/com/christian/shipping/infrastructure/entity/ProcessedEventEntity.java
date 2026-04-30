package com.christian.shipping.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "processed_event", schema = "shipping")
public class ProcessedEventEntity {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID eventId;

    @Column(nullable = false)
    private UUID saleId;

    @Column(nullable = false)
    private LocalDateTime processedAt;
}
