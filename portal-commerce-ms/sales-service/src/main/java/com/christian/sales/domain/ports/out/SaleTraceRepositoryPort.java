package com.christian.sales.domain.ports.out;

import com.christian.sales.domain.model.SaleTrace;

import java.util.List;
import java.util.UUID;

public interface SaleTraceRepositoryPort {
    SaleTrace save(SaleTrace trace);
    List<SaleTrace> findBySaleId(UUID saleId);
}
