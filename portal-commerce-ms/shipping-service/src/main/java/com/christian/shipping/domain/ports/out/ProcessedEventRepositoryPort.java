package com.christian.shipping.domain.ports.out;

import com.christian.shipping.domain.model.ProcessedEvent;

import java.util.Optional;
import java.util.UUID;

public interface ProcessedEventRepositoryPort {
    ProcessedEvent save(ProcessedEvent processedEvent);
    Optional<ProcessedEvent> findByEventId(UUID eventId);
}
