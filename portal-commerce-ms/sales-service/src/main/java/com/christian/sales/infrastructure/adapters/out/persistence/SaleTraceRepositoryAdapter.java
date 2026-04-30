package com.christian.sales.infrastructure.adapters.out.persistence;

import com.christian.sales.domain.model.SaleTrace;
import com.christian.sales.domain.ports.out.SaleTraceRepositoryPort;
import com.christian.sales.infrastructure.entity.SaleTraceEntity;
import com.christian.sales.infrastructure.repository.SaleTraceJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class SaleTraceRepositoryAdapter implements SaleTraceRepositoryPort {

    private final SaleTraceJpaRepository saleTraceJpaRepository;

    public SaleTraceRepositoryAdapter(SaleTraceJpaRepository saleTraceJpaRepository) {
        this.saleTraceJpaRepository = saleTraceJpaRepository;
    }

    @Override
    public SaleTrace save(SaleTrace trace) {
        SaleTraceEntity entity = SaleTraceEntity.builder()
                .id(trace.getId())
                .saleId(trace.getSaleId())
                .traceType(trace.getTraceType())
                .description(trace.getDescription())
                .status(trace.getStatus())
                .correlationId(trace.getCorrelationId())
                .createdAt(trace.getCreatedAt())
                .build();
        return toDomain(saleTraceJpaRepository.save(entity));
    }

    @Override
    public List<SaleTrace> findBySaleId(UUID saleId) {
        return saleTraceJpaRepository.findBySaleId(saleId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private SaleTrace toDomain(SaleTraceEntity entity) {
        return SaleTrace.builder()
                .id(entity.getId())
                .saleId(entity.getSaleId())
                .traceType(entity.getTraceType())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .correlationId(entity.getCorrelationId())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
