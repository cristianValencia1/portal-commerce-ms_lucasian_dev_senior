package com.christian.sales.domain.ports.out;

import com.christian.sales.domain.model.Sale;

import java.util.Optional;
import java.util.UUID;

public interface SaleRepositoryPort {
    Sale save(Sale sale);
    Optional<Sale> findById(UUID saleId);
}
