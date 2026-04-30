package com.christian.sales.domain.ports.out;

import com.christian.sales.domain.model.Sale;

public interface EventPublisherPort {
    void publishSaleCreated(Sale sale);
}
