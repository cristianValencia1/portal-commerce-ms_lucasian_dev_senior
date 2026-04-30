package com.christian.sales.application.mapper;

import com.christian.sales.application.dto.*;
import com.christian.sales.domain.enums.SaleStatus;
import com.christian.sales.domain.model.Customer;
import com.christian.sales.domain.model.Sale;
import com.christian.sales.domain.model.SaleItem;
import com.christian.sales.domain.model.SaleTrace;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public final class SaleMapper {
    private SaleMapper() {
    }

    public static Customer toCustomer(CustomerRequest request) {
        if (request == null) {
            return null;
        }
        return Customer.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .postalCode(request.getPostalCode())
                .build();
    }

    public static List<SaleItem> toSaleItems(List<SaleItemRequest> requests) {
        return requests.stream()
                .map(item -> {
                    BigDecimal subtotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                    return SaleItem.builder()
                            .id(UUID.randomUUID())
                            .productId(item.getProductId())
                            .productName(item.getProductName())
                            .quantity(item.getQuantity())
                            .unitPrice(item.getUnitPrice())
                            .subtotal(subtotal)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public static SaleResponse toSaleResponse(Sale sale) {
        if (sale == null) {
            return null;
        }
        return SaleResponse.builder()
                .id(sale.getId())
                .customer(toCustomerRequest(sale.getCustomer()))
                .items(toSaleItemResponseList(sale.getItems()))
                .totalAmount(sale.getTotalAmount())
                .status(sale.getStatus().name())
                .correlationId(sale.getCorrelationId())
                .createdAt(sale.getCreatedAt())
                .updatedAt(sale.getUpdatedAt())
                .build();
    }

    public static CustomerRequest toCustomerRequest(Customer customer) {
        if (customer == null) {
            return null;
        }
        return CustomerRequest.builder()
                .fullName(customer.getFullName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .postalCode(customer.getPostalCode())
                .build();
    }

    public static List<SaleItemResponse> toSaleItemResponseList(List<SaleItem> items) {
        return items.stream()
                .map(item -> SaleItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .subtotal(item.getSubtotal())
                        .build())
                .collect(Collectors.toList());
    }

    public static SaleTraceResponse toSaleTraceResponse(SaleTrace trace) {
        if (trace == null) {
            return null;
        }
        return SaleTraceResponse.builder()
                .id(trace.getId())
                .saleId(trace.getSaleId())
                .traceType(trace.getTraceType().name())
                .description(trace.getDescription())
                .status(trace.getStatus().name())
                .correlationId(trace.getCorrelationId())
                .createdAt(trace.getCreatedAt())
                .build();
    }
}
