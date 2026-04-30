package com.christian.sales.application.usecase;

import com.christian.sales.application.dto.SaleTraceResponse;
import com.christian.sales.application.mapper.SaleMapper;
import com.christian.sales.domain.ports.in.GetSaleTraceUseCase;
import com.christian.sales.domain.ports.out.SaleTraceRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GetSaleTraceUseCaseImpl implements GetSaleTraceUseCase {

    private final SaleTraceRepositoryPort saleTraceRepositoryPort;

    public GetSaleTraceUseCaseImpl(SaleTraceRepositoryPort saleTraceRepositoryPort) {
        this.saleTraceRepositoryPort = saleTraceRepositoryPort;
    }

    @Override
    public List<SaleTraceResponse> getSaleTrace(UUID saleId) {
        return saleTraceRepositoryPort.findBySaleId(saleId).stream()
                .map(SaleMapper::toSaleTraceResponse)
                .collect(Collectors.toList());
    }
}
