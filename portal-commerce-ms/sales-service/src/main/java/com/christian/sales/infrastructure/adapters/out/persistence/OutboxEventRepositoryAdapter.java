package com.christian.sales.infrastructure.adapters.out.persistence;

import com.christian.sales.domain.model.OutboxEvent;
import com.christian.sales.domain.ports.out.OutboxEventRepositoryPort;
import com.christian.sales.infrastructure.entity.OutboxEventEntity;
import com.christian.sales.infrastructure.repository.OutboxEventJpaRepository;
import org.springframework.stereotype.Component;

@Component
public class OutboxEventRepositoryAdapter implements OutboxEventRepositoryPort {

    private final OutboxEventJpaRepository outboxEventJpaRepository;

    public OutboxEventRepositoryAdapter(OutboxEventJpaRepository outboxEventJpaRepository) {
        this.outboxEventJpaRepository = outboxEventJpaRepository;
    }

    @Override
    public OutboxEvent save(OutboxEvent outboxEvent) {
        OutboxEventEntity entity = OutboxEventEntity.builder()
                .id(outboxEvent.getId())
                .eventId(outboxEvent.getEventId())
                .aggregateId(outboxEvent.getAggregateId())
                .aggregateType(outboxEvent.getAggregateType())
                .eventType(outboxEvent.getEventType())
                .payload(outboxEvent.getPayload())
                .status(outboxEvent.getStatus())
                .retryCount(outboxEvent.getRetryCount())
                .createdAt(outboxEvent.getCreatedAt())
                .processedAt(outboxEvent.getProcessedAt())
                .build();
        OutboxEventEntity saved = outboxEventJpaRepository.save(entity);
        return OutboxEvent.builder()
                .id(saved.getId())
                .eventId(saved.getEventId())
                .aggregateId(saved.getAggregateId())
                .aggregateType(saved.getAggregateType())
                .eventType(saved.getEventType())
                .payload(saved.getPayload())
                .status(saved.getStatus())
                .retryCount(saved.getRetryCount())
                .createdAt(saved.getCreatedAt())
                .processedAt(saved.getProcessedAt())
                .build();
    }
}
