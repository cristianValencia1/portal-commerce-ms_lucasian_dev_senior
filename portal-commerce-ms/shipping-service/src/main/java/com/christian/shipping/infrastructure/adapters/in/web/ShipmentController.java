package com.christian.shipping.infrastructure.adapters.in.web;

import com.christian.shipping.application.dto.ShipmentResponse;
import com.christian.shipping.application.dto.ShipmentTraceResponse;
import com.christian.shipping.domain.ports.in.GetShipmentBySaleIdUseCase;
import com.christian.shipping.domain.ports.in.GetShipmentTraceUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shipments")
public class ShipmentController {

    private final GetShipmentBySaleIdUseCase getShipmentBySaleIdUseCase;
    private final GetShipmentTraceUseCase getShipmentTraceUseCase;

    public ShipmentController(GetShipmentBySaleIdUseCase getShipmentBySaleIdUseCase,
                              GetShipmentTraceUseCase getShipmentTraceUseCase) {
        this.getShipmentBySaleIdUseCase = getShipmentBySaleIdUseCase;
        this.getShipmentTraceUseCase = getShipmentTraceUseCase;
    }

    @GetMapping("/sale/{saleId}")
    public ResponseEntity<ShipmentResponse> getShipment(@PathVariable UUID saleId) {
        return ResponseEntity.ok(getShipmentBySaleIdUseCase.getShipmentBySaleId(saleId));
    }

    @GetMapping("/sale/{saleId}/trace")
    public ResponseEntity<List<ShipmentTraceResponse>> getShipmentTrace(@PathVariable UUID saleId) {
        return ResponseEntity.ok(getShipmentTraceUseCase.getShipmentTrace(saleId));
    }
}
