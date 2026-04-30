package com.christian.sales.domain.ports.out;

import com.christian.sales.domain.model.OutboxEvent;

public interface OutboxEventRepositoryPort {
    OutboxEvent save(OutboxEvent outboxEvent);
}
