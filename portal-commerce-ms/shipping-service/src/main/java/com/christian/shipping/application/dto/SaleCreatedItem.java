package com.christian.shipping.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleCreatedItem {
    private String productId;
    private String productName;
    private Integer quantity;
    private String unitPrice;
}
