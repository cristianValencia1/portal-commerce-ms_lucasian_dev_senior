-- 04-indexes.sql
-- Indices para consultas, trazabilidad, outbox e idempotencia

CREATE INDEX IF NOT EXISTS idx_sales_correlation_id
    ON sales.sales(correlation_id);

CREATE INDEX IF NOT EXISTS idx_sales_status
    ON sales.sales(status);

CREATE INDEX IF NOT EXISTS idx_sale_items_sale_id
    ON sales.sale_items(sale_id);

CREATE INDEX IF NOT EXISTS idx_sale_trace_sale_id
    ON sales.sale_trace(sale_id);

CREATE INDEX IF NOT EXISTS idx_sale_trace_correlation_id
    ON sales.sale_trace(correlation_id);

CREATE INDEX IF NOT EXISTS idx_outbox_status
    ON sales.outbox_event(status);

CREATE INDEX IF NOT EXISTS idx_outbox_aggregate_id
    ON sales.outbox_event(aggregate_id);

CREATE INDEX IF NOT EXISTS idx_outbox_created_at
    ON sales.outbox_event(created_at);

CREATE INDEX IF NOT EXISTS idx_shipments_sale_id
    ON shipping.shipments(sale_id);

CREATE INDEX IF NOT EXISTS idx_shipments_status
    ON shipping.shipments(status);

CREATE INDEX IF NOT EXISTS idx_shipment_trace_shipment_id
    ON shipping.shipment_trace(shipment_id);

CREATE INDEX IF NOT EXISTS idx_shipment_trace_sale_id
    ON shipping.shipment_trace(sale_id);

CREATE INDEX IF NOT EXISTS idx_shipment_trace_correlation_id
    ON shipping.shipment_trace(correlation_id);

CREATE INDEX IF NOT EXISTS idx_processed_event_event_id
    ON shipping.processed_event(event_id);

CREATE INDEX IF NOT EXISTS idx_processed_event_sale_id
    ON shipping.processed_event(sale_id);

