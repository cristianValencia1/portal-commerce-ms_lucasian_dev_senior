package com.christian.sales.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSaleResponse {
    private UUID saleId;
    private String status;
    private UUID correlationId;
}
