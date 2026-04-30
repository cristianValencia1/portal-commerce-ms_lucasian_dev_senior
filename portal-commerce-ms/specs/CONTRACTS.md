# Event Contracts

Última actualización: 2026-04-29

Este archivo define los contratos JSON (ejemplos y JSON Schema) para los eventos intercambiados entre `sales-service` y `shipping-service`.

## Evento: sale.created (v1)

Descripción: publicado por `sales-service` tras la creación exitosa de una venta.

Ejemplo de payload:

```json
{
  "eventId": "11111111-2222-3333-4444-555555555555",
  "eventType": "sale.created",
  "version": "1",
  "createdAt": "2026-04-29T23:55:00Z",
  "sale": {
    "saleId": "911d6606-0c50-4856-bd4e-e83be9636c9c",
    "customer": {
      "fullName": "Ana López",
      "email": "ana.lopez@example.com"
    },
    "items": [
      { "productId": "P1001", "productName": "Camiseta azul", "quantity": 2, "unitPrice": 29.9 }
    ],
    "totalAmount": 59.8
  }
}
```

Nota: este bloque es el evento publicado en la cola por `sales-service`. No confundir con el payload HTTP para `POST /api/v1/sales` (ver sección "HTTP Endpoints"): el endpoint HTTP espera únicamente `customer` y `items` — el `saleId` y `totalAmount` los genera el servicio.

JSON Schema (simplificado):

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "sale.created.v1",
  "type": "object",
  "required": ["eventId","eventType","version","createdAt","sale"],
  "properties": {
    "eventId": { "type": "string", "format": "uuid" },
    "eventType": { "type": "string", "const": "sale.created" },
    "version": { "type": "string" },
    "createdAt": { "type": "string", "format": "date-time" },
    "sale": {
      "type": "object",
      "required": ["saleId","customer","items","totalAmount"],
      "properties": {
        "saleId": { "type": "string", "format": "uuid" },
        "customer": {
          "type": "object",
          "required": ["fullName"],
          "properties": {
            "fullName": { "type": "string" },
            "email": { "type": "string", "format": "email" }
          }
        },
        "items": {
          "type": "array",
          "items": {
            "type": "object",
            "required": ["productId","quantity","unitPrice"],
            "properties": {
              "productId": { "type": "string" },
              "productName": { "type": "string" },
              "quantity": { "type": "integer", "minimum": 1 },
              "unitPrice": { "type": "number" }
            }
          }
        },
        "totalAmount": { "type": "number" }
      }
    }
  }
}
```

## Evento: shipping.created (v1)

Descripción: publicado por `shipping-service` cuando se crea una orden de envío a partir de un `sale.created`.

Ejemplo de payload:

```json
{
  "eventId": "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee",
  "eventType": "shipping.created",
  "version": "1",
  "createdAt": "2026-04-29T23:56:00Z",
  "shipping": {
    "shippingId": "3b9f1c2d-7a4e-4a2b-9f1e-0a1b2c3d4e5f",
    "saleId": "911d6606-0c50-4856-bd4e-e83be9636c9c",
    "status": "PENDING",
    "address": {
      "postalCode": "01234",
      "line1": "Av. Principal 100"
    }
  }
}
```

JSON Schema (simplificado):

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "shipping.created.v1",
  "type": "object",
  "required": ["eventId","eventType","version","createdAt","shipping"],
  "properties": {
    "eventId": { "type": "string", "format": "uuid" },
    "eventType": { "type": "string", "const": "shipping.created" },
    "version": { "type": "string" },
    "createdAt": { "type": "string", "format": "date-time" },
    "shipping": {
      "type": "object",
      "required": ["shippingId","saleId","status"],
      "properties": {
        "shippingId": { "type": "string", "format": "uuid" },
        "saleId": { "type": "string", "format": "uuid" },
        "status": { "type": "string" },
        "address": {
          "type": "object",
          "properties": {
            "postalCode": { "type": "string" },
            "line1": { "type": "string" }
          }
        }
      }
    }
  }
}
```

## Versionado y prácticas

- Incluir `version` en cada evento para permitir cambios no rompientes.
- Mantener los JSON Schemas en este archivo o separados en `/contracts` si se planea ampliar.
- Validar mensajes tanto en los productores como en los consumidores.

## HTTP Endpoints — Ejemplos de payloads

A continuación se muestran ejemplos de request/response para los endpoints HTTP expuestos por los servicios.

### Sales Service

- POST /api/v1/sales — Request (crear venta)

```json
{
  "customer": {
    "fullName": "Ana López",
    "email": "ana.lopez@example.com",
    "phone": "5512345678",
    "postalCode": "01234"
  },
  "items": [
    {
      "productId": "P1001",
      "productName": "Camiseta azul",
      "quantity": 2,
      "unitPrice": 29.90
    }
  ]
}
```

- POST /api/v1/sales — Response (ejemplo)

```json
{
  "saleId": "911d6606-0c50-4856-bd4e-e83be9636c9c",
  "status": "REGISTERED",
  "correlationId": "0f56a897-ac8e-4cb7-bdae-b22628a9ca1d"
}
```

- GET /api/v1/sales/{saleId} — Response (ejemplo `SaleResponse`)

```json
{
  "id": "911d6606-0c50-4856-bd4e-e83be9636c9c",
  "customer": {
    "fullName": "Ana López",
    "email": "ana.lopez@example.com",
    "phone": "5512345678",
    "postalCode": "01234"
  },
  "items": [
    {
      "id": "9ced2cd9-b85d-45a8-a65d-f4efab6de585",
      "productId": "P1001",
      "productName": "Camiseta azul",
      "quantity": 2,
      "unitPrice": 29.90,
      "subtotal": 59.80
    }
  ],
  "totalAmount": 59.80,
  "status": "REGISTERED",
  "correlationId": "0f56a897-ac8e-4cb7-bdae-b22628a9ca1d",
  "createdAt": "2026-04-30T14:43:46.308211",
  "updatedAt": null
}
```

- GET /api/v1/sales/{saleId}/trace — Response (ejemplo `SaleTraceResponse[]`)

```json
[
  {
    "id": "67406c48-8ad1-4aa8-91b9-a2cc7e2a8b43",
    "saleId": "911d6606-0c50-4856-bd4e-e83be9636c9c",
    "traceType": "SALE_RECEIVED",
    "description": "Sale received",
    "status": "REGISTERED",
    "correlationId": "0f56a897-ac8e-4cb7-bdae-b22628a9ca1d",
    "createdAt": "2026-04-30T14:43:46.318266"
  }
]
```

### Shipping Service

- GET /api/v1/shipments/sale/{saleId} — Response (ejemplo `ShipmentResponse`)

```json
{
  "id": "3b9f1c2d-7a4e-4a2b-9f1e-0a1b2c3d4e5f",
  "saleId": "911d6606-0c50-4856-bd4e-e83be9636c9c",
  "shipmentNumber": "SHP-20260430-0001",
  "status": "PENDING",
  "correlationId": "0f56a897-ac8e-4cb7-bdae-b22628a9ca1d",
  "createdAt": "2026-04-30T14:45:00.000",
  "updatedAt": null
}
```

- GET /api/v1/shipments/sale/{saleId}/trace — Response (ejemplo `ShipmentTraceResponse[]`)

```json
[
  {
    "id": "c1d2e3f4-5678-90ab-cdef-111213141516",
    "shipmentId": "3b9f1c2d-7a4e-4a2b-9f1e-0a1b2c3d4e5f",
    "saleId": "911d6606-0c50-4856-bd4e-e83be9636c9c",
    "traceType": "SHIPMENT_CREATED",
    "description": "Shipment created from sale.created event",
    "status": "PENDING",
    "correlationId": "0f56a897-ac8e-4cb7-bdae-b22628a9ca1d",
    "createdAt": "2026-04-30T14:45:01.000"
  }
]
```

---

Incluye estos ejemplos en tus pruebas E2E y en la documentación de API para facilitar la integración.
