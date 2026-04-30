package com.christian.sales.domain.model;

import com.christian.sales.domain.enums.SaleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sale {
    private UUID id;
    private Customer customer;
    private List<SaleItem> items;
    private BigDecimal totalAmount;
    private SaleStatus status;
    private UUID correlationId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
