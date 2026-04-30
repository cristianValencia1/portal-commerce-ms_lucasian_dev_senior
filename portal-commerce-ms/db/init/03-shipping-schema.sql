-- Shipment aggregate linked to a sale.
CREATE TABLE IF NOT EXISTS shipping.shipments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    sale_id UUID NOT NULL,
    shipment_number VARCHAR(60) NOT NULL,
    status VARCHAR(40) NOT NULL,
    correlation_id UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_shipments_shipment_number UNIQUE (shipment_number)
);

-- Shipment traceability history.
CREATE TABLE IF NOT EXISTS shipping.shipment_trace (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    shipment_id UUID NOT NULL,
    sale_id UUID NOT NULL,
    trace_type VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(40) NOT NULL,
    correlation_id UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_shipment_trace_shipment
        FOREIGN KEY (shipment_id)
        REFERENCES shipping.shipments (id)
        ON DELETE CASCADE
);

-- Idempotency control for consumed integration events.
CREATE TABLE IF NOT EXISTS shipping.processed_event (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    event_id UUID NOT NULL,
    sale_id UUID NOT NULL,
    processed_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_processed_event_event_id UNIQUE (event_id)
);

