package com.christian.sales.application.usecase;

import com.christian.sales.application.dto.SaleResponse;
import com.christian.sales.application.mapper.SaleMapper;
import com.christian.sales.domain.exception.ResourceNotFoundException;
import com.christian.sales.domain.ports.in.GetSaleUseCase;
import com.christian.sales.domain.ports.out.SaleRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetSaleUseCaseImpl implements GetSaleUseCase {

    private final SaleRepositoryPort saleRepositoryPort;

    public GetSaleUseCaseImpl(SaleRepositoryPort saleRepositoryPort) {
        this.saleRepositoryPort = saleRepositoryPort;
    }

    @Override
    public SaleResponse getSale(UUID saleId) {
        return saleRepositoryPort.findById(saleId)
                .map(SaleMapper::toSaleResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found for id " + saleId));
    }
}
