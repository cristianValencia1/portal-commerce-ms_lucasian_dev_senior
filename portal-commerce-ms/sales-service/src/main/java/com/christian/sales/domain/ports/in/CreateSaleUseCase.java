package com.christian.sales.domain.ports.in;

import com.christian.sales.application.dto.CreateSaleRequest;
import com.christian.sales.application.dto.CreateSaleResponse;

public interface CreateSaleUseCase {
    CreateSaleResponse create(CreateSaleRequest request);
}
