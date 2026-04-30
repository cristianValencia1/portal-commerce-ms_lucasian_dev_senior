package com.christian.sales.application.dto;

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
public class SaleResponse {
    private UUID id;
    private CustomerRequest customer;
    private List<SaleItemResponse> items;
    private BigDecimal totalAmount;
    private String status;
    private UUID correlationId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
