package com.christian.sales.infrastructure.entity;

import com.christian.sales.domain.enums.SaleStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sales", schema = "sales")
public class SaleEntity {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String customerFullName;

    @Column(nullable = false)
    private String customerEmail;

    @Column(nullable = false)
    private String customerPhone;

    @Column(nullable = false)
    private String customerPostalCode;

    @OneToMany(mappedBy = "sale", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private List<SaleItemEntity> items = new ArrayList<>();

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SaleStatus status;

    @Column(nullable = false)
    private UUID correlationId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
