package com.christian.sales.infrastructure.adapters.in.web;

import com.christian.sales.application.dto.CreateSaleRequest;
import com.christian.sales.application.dto.CreateSaleResponse;
import com.christian.sales.application.dto.SaleResponse;
import com.christian.sales.application.dto.SaleTraceResponse;
import com.christian.sales.domain.ports.in.CreateSaleUseCase;
import com.christian.sales.domain.ports.in.GetSaleTraceUseCase;
import com.christian.sales.domain.ports.in.GetSaleUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sales")
public class SalesController {

    private final CreateSaleUseCase createSaleUseCase;
    private final GetSaleUseCase getSaleUseCase;
    private final GetSaleTraceUseCase getSaleTraceUseCase;

    public SalesController(CreateSaleUseCase createSaleUseCase,
                           GetSaleUseCase getSaleUseCase,
                           GetSaleTraceUseCase getSaleTraceUseCase) {
        this.createSaleUseCase = createSaleUseCase;
        this.getSaleUseCase = getSaleUseCase;
        this.getSaleTraceUseCase = getSaleTraceUseCase;
    }

    @PostMapping
    public ResponseEntity<CreateSaleResponse> createSale(@Valid @RequestBody CreateSaleRequest request) {
        CreateSaleResponse response = createSaleUseCase.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{saleId}")
    public ResponseEntity<SaleResponse> getSale(@PathVariable UUID saleId) {
        return ResponseEntity.ok(getSaleUseCase.getSale(saleId));
    }

    @GetMapping("/{saleId}/trace")
    public ResponseEntity<List<SaleTraceResponse>> getSaleTrace(@PathVariable UUID saleId) {
        return ResponseEntity.ok(getSaleTraceUseCase.getSaleTrace(saleId));
    }
}
