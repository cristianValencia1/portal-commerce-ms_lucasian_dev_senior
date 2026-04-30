package com.christian.shipping.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleCreatedPayload {
    private UUID saleId;
    private String customerEmail;
    private String customerPhone;
    private String postalCode;
    private List<SaleCreatedItem> items;
}
