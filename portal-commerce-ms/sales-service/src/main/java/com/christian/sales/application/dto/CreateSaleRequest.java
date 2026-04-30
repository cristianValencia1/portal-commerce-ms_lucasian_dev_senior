package com.christian.sales.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSaleRequest {
    @NotNull(message = "customer is required")
    @Valid
    private CustomerRequest customer;

    @NotEmpty(message = "items are required")
    @Valid
    private List<SaleItemRequest> items;
}
