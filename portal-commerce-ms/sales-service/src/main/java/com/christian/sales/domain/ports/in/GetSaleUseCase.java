package com.christian.sales.domain.ports.in;

import com.christian.sales.application.dto.SaleResponse;

import java.util.UUID;

public interface GetSaleUseCase {
    SaleResponse getSale(UUID saleId);
}
