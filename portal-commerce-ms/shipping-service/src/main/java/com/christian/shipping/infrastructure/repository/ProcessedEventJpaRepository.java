package com.christian.shipping.infrastructure.repository;

import com.christian.shipping.infrastructure.entity.ProcessedEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProcessedEventJpaRepository extends JpaRepository<ProcessedEventEntity, UUID> {
    Optional<ProcessedEventEntity> findByEventId(UUID eventId);
}
