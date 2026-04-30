package com.christian.shipping.infrastructure.adapters.out.persistence;

import com.christian.shipping.domain.model.ProcessedEvent;
import com.christian.shipping.domain.ports.out.ProcessedEventRepositoryPort;
import com.christian.shipping.infrastructure.entity.ProcessedEventEntity;
import com.christian.shipping.infrastructure.repository.ProcessedEventJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class ProcessedEventRepositoryAdapter implements ProcessedEventRepositoryPort {

    private final ProcessedEventJpaRepository processedEventJpaRepository;

    public ProcessedEventRepositoryAdapter(ProcessedEventJpaRepository processedEventJpaRepository) {
        this.processedEventJpaRepository = processedEventJpaRepository;
    }

    @Override
    public ProcessedEvent save(ProcessedEvent processedEvent) {
        ProcessedEventEntity entity = ProcessedEventEntity.builder()
                .id(processedEvent.getId())
                .eventId(processedEvent.getEventId())
                .saleId(processedEvent.getSaleId())
                .processedAt(processedEvent.getProcessedAt())
                .build();
        ProcessedEventEntity saved = processedEventJpaRepository.save(entity);
        return ProcessedEvent.builder()
                .id(saved.getId())
                .eventId(saved.getEventId())
                .saleId(saved.getSaleId())
                .processedAt(saved.getProcessedAt())
                .build();
    }

    @Override
    public Optional<ProcessedEvent> findByEventId(UUID eventId) {
        return processedEventJpaRepository.findByEventId(eventId)
                .map(entity -> ProcessedEvent.builder()
                        .id(entity.getId())
                        .eventId(entity.getEventId())
                        .saleId(entity.getSaleId())
                        .processedAt(entity.getProcessedAt())
                        .build());
    }
}
