package com.christian.sales.infrastructure.repository;

import com.christian.sales.infrastructure.entity.SaleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SaleJpaRepository extends JpaRepository<SaleEntity, UUID> {
}
