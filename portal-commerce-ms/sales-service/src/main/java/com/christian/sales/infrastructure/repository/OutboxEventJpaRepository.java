package com.christian.sales.infrastructure.repository;

import com.christian.sales.infrastructure.entity.OutboxEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OutboxEventJpaRepository extends JpaRepository<OutboxEventEntity, UUID> {
}
