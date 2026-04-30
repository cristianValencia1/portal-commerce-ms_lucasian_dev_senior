package com.christian.sales.infrastructure.adapters.out.persistence;

import com.christian.sales.domain.model.Sale;
import com.christian.sales.domain.model.SaleItem;
import com.christian.sales.domain.ports.out.SaleRepositoryPort;
import com.christian.sales.infrastructure.entity.SaleEntity;
import com.christian.sales.infrastructure.entity.SaleItemEntity;
import com.christian.sales.infrastructure.repository.SaleJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class SaleRepositoryAdapter implements SaleRepositoryPort {

    private final SaleJpaRepository saleJpaRepository;

    public SaleRepositoryAdapter(SaleJpaRepository saleJpaRepository) {
        this.saleJpaRepository = saleJpaRepository;
    }

    @Override
    public Sale save(Sale sale) {
        SaleEntity entity = toEntity(sale);
        SaleEntity saved = saleJpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Sale> findById(UUID saleId) {
        return saleJpaRepository.findById(saleId).map(this::toDomain);
    }

    private SaleEntity toEntity(Sale sale) {
        SaleEntity entity = SaleEntity.builder()
                .id(sale.getId())
                .customerFullName(sale.getCustomer().getFullName())
                .customerEmail(sale.getCustomer().getEmail())
                .customerPhone(sale.getCustomer().getPhone())
                .customerPostalCode(sale.getCustomer().getPostalCode())
                .totalAmount(sale.getTotalAmount())
                .status(sale.getStatus())
                .correlationId(sale.getCorrelationId())
                .createdAt(sale.getCreatedAt())
                .updatedAt(sale.getUpdatedAt())
                .build();

        List<SaleItemEntity> items = sale.getItems().stream()
                .map(item -> SaleItemEntity.builder()
                        .id(item.getId())
                        .sale(entity)
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .subtotal(item.getSubtotal())
                        .build())
                .collect(Collectors.toList());

        entity.setItems(items);
        return entity;
    }

    private Sale toDomain(SaleEntity entity) {
        List<SaleItem> items = entity.getItems().stream()
                .map(itemEntity -> SaleItem.builder()
                        .id(itemEntity.getId())
                        .productId(itemEntity.getProductId())
                        .productName(itemEntity.getProductName())
                        .quantity(itemEntity.getQuantity())
                        .unitPrice(itemEntity.getUnitPrice())
                        .subtotal(itemEntity.getSubtotal())
                        .build())
                .collect(Collectors.toList());

        return Sale.builder()
                .id(entity.getId())
                .customer(com.christian.sales.domain.model.Customer.builder()
                        .fullName(entity.getCustomerFullName())
                        .email(entity.getCustomerEmail())
                        .phone(entity.getCustomerPhone())
                        .postalCode(entity.getCustomerPostalCode())
                        .build())
                .items(items)
                .totalAmount(entity.getTotalAmount())
                .status(entity.getStatus())
                .correlationId(entity.getCorrelationId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
