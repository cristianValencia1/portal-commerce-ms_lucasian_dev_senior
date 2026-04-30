package com.christian.sales.domain.ports.in;

import com.christian.sales.application.dto.SaleTraceResponse;

import java.util.List;
import java.util.UUID;

public interface GetSaleTraceUseCase {
    List<SaleTraceResponse> getSaleTrace(UUID saleId);
}
