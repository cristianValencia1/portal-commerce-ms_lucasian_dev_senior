-- Sales indexes for correlation, filtering, and traversal.
CREATE INDEX IF NOT EXISTS idx_sales_sales_correlation_id
    ON sales.sales (correlation_id);

CREATE INDEX IF NOT EXISTS idx_sales_sales_status
    ON sales.sales (status);

CREATE INDEX IF NOT EXISTS idx_sales_sale_items_sale_id
    ON sales.sale_items (sale_id);

CREATE INDEX IF NOT EXISTS idx_sales_sale_trace_sale_id
    ON sales.sale_trace (sale_id);

CREATE INDEX IF NOT EXISTS idx_sales_sale_trace_correlation_id
    ON sales.sale_trace (correlation_id);

CREATE INDEX IF NOT EXISTS idx_sales_sale_trace_status
    ON sales.sale_trace (status);

CREATE INDEX IF NOT EXISTS idx_sales_outbox_event_aggregate_id
    ON sales.outbox_event (aggregate_id);

CREATE INDEX IF NOT EXISTS idx_sales_outbox_event_event_id
    ON sales.outbox_event (event_id);

CREATE INDEX IF NOT EXISTS idx_sales_outbox_event_status
    ON sales.outbox_event (status);

CREATE INDEX IF NOT EXISTS idx_sales_outbox_event_created_at
    ON sales.outbox_event (created_at);

-- Shipping indexes for correlation, lookup, and idempotency.
CREATE INDEX IF NOT EXISTS idx_shipping_shipments_sale_id
    ON shipping.shipments (sale_id);

CREATE INDEX IF NOT EXISTS idx_shipping_shipments_correlation_id
    ON shipping.shipments (correlation_id);

CREATE INDEX IF NOT EXISTS idx_shipping_shipments_status
    ON shipping.shipments (status);

CREATE INDEX IF NOT EXISTS idx_shipping_shipment_trace_shipment_id
    ON shipping.shipment_trace (shipment_id);

CREATE INDEX IF NOT EXISTS idx_shipping_shipment_trace_sale_id
    ON shipping.shipment_trace (sale_id);

CREATE INDEX IF NOT EXISTS idx_shipping_shipment_trace_correlation_id
    ON shipping.shipment_trace (correlation_id);

CREATE INDEX IF NOT EXISTS idx_shipping_shipment_trace_status
    ON shipping.shipment_trace (status);

CREATE INDEX IF NOT EXISTS idx_shipping_processed_event_event_id
    ON shipping.processed_event (event_id);

CREATE INDEX IF NOT EXISTS idx_shipping_processed_event_sale_id
    ON shipping.processed_event (sale_id);

