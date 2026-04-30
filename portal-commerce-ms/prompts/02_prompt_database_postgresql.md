# Prompt para generar imagen Docker de PostgreSQL con esquemas y tablas

## Objetivo

Genera una solución completa para construir una **imagen Docker de PostgreSQL** lista para inicializar la base de datos de un sistema basado en microservicios con los dominios **sales** y **shipping**.

La solución debe crear automáticamente, al iniciar el contenedor, los **schemas**, **tablas**, **índices**, **restricciones** y estructuras mínimas necesarias para soportar:

- registro de ventas;
- detalle de productos vendidos;
- trazabilidad de ventas;
- outbox de eventos para integración asíncrona;
- creación de envíos;
- trazabilidad de envíos;
- control de eventos procesados para idempotencia.

---

## Contexto técnico

Estoy construyendo una solución con:

- Java
- Spring Boot
- arquitectura hexagonal / clean architecture
- integración asíncrona entre microservicios
- PostgreSQL
- RabbitMQ
- Docker Compose

La base de datos debe quedar preparada para dos contextos funcionales separados dentro de una misma instancia PostgreSQL usando **schemas**:

- `sales`
- `shipping`

La base principal debe llamarse:

- `commerce_platform`

---

## Requerimiento principal

Quiero que generes los archivos necesarios para construir una imagen Docker de PostgreSQL que, al levantarse, ejecute scripts SQL de inicialización ubicados en `/docker-entrypoint-initdb.d`.

La solución debe incluir como mínimo:

1. Un `Dockerfile` basado en PostgreSQL.
2. Scripts SQL ordenados para inicialización automática.
3. Creación de schemas:
   - `sales`
   - `shipping`
4. Creación de tablas:
   - `sales.sales`
   - `sales.sale_items`
   - `sales.sale_trace`
   - `sales.outbox_event`
   - `shipping.shipments`
   - `shipping.shipment_trace`
   - `shipping.processed_event`
5. Índices recomendados para búsqueda, correlación e idempotencia.
6. Restricciones primarias y foráneas.
7. Tipos de datos apropiados para PostgreSQL.
8. Soporte para `UUID`.
9. Uso de `JSONB` para payload del outbox.
10. Un ejemplo de `docker-compose.yml` para levantar esta imagen.
11. Un árbol final de carpetas del proyecto.

---

## Reglas de diseño

Diseña la base de datos con estas consideraciones:

### Schema `sales`
Debe soportar:

- registro principal de una venta;
- datos básicos del cliente;
- estado de la venta;
- correlación técnica;
- timestamps;
- detalle de productos;
- trazabilidad funcional;
- almacenamiento de eventos de integración con patrón outbox.

### Schema `shipping`
Debe soportar:

- creación de envíos asociados a una venta;
- estado logístico;
- trazabilidad;
- control de eventos consumidos para evitar duplicados.

---

## Estructura funcional esperada

### Tabla `sales.sales`
Debe contener al menos:

- `id`
- `customer_name`
- `customer_email`
- `customer_phone`
- `postal_code`
- `total_amount`
- `status`
- `correlation_id`
- `created_at`
- `updated_at`

### Tabla `sales.sale_items`
Debe contener al menos:

- `id`
- `sale_id`
- `product_id`
- `product_name`
- `quantity`
- `unit_price`
- `subtotal`

### Tabla `sales.sale_trace`
Debe contener al menos:

- `id`
- `sale_id`
- `trace_type`
- `description`
- `status`
- `correlation_id`
- `created_at`

### Tabla `sales.outbox_event`
Debe contener al menos:

- `id`
- `event_id`
- `aggregate_id`
- `aggregate_type`
- `event_type`
- `payload`
- `status`
- `retry_count`
- `created_at`
- `processed_at`

### Tabla `shipping.shipments`
Debe contener al menos:

- `id`
- `sale_id`
- `shipment_number`
- `status`
- `correlation_id`
- `created_at`
- `updated_at`

### Tabla `shipping.shipment_trace`
Debe contener al menos:

- `id`
- `shipment_id`
- `sale_id`
- `trace_type`
- `description`
- `status`
- `correlation_id`
- `created_at`

### Tabla `shipping.processed_event`
Debe contener al menos:

- `id`
- `event_id`
- `sale_id`
- `processed_at`

---

## Requisitos técnicos SQL

Quiero que generes SQL válido para PostgreSQL, incluyendo:

- `CREATE SCHEMA IF NOT EXISTS`
- `CREATE EXTENSION IF NOT EXISTS "uuid-ossp"` o alternativa equivalente
- claves primarias con `UUID`
- `NUMERIC(14,2)` para montos
- `VARCHAR` donde aplique
- `TEXT` para descripciones largas
- `JSONB` para payload de eventos
- índices sobre:
  - `correlation_id`
  - `sale_id`
  - `event_id`
  - `status`
- `UNIQUE` en:
  - `event_id`
  - `shipment_number` cuando corresponda

---

## Buenas prácticas esperadas

La respuesta debe seguir estas buenas prácticas:

- separar scripts por orden de ejecución;
- usar nombres de archivo con prefijo numérico, por ejemplo:
  - `01-init-schemas.sql`
  - `02-sales-schema.sql`
  - `03-shipping-schema.sql`
  - `04-indexes.sql`
- incluir comentarios SQL breves y claros;
- evitar dependencias innecesarias;
- hacer la inicialización idempotente en la medida de lo posible;
- entregar código limpio y listo para copiar.

---

## Salida esperada

Devuélveme la respuesta en este formato exacto y en este orden:

1. Explicación breve de la solución.
2. Árbol de archivos.
3. `Dockerfile`
4. `01-init-schemas.sql`
5. `02-sales-schema.sql`
6. `03-shipping-schema.sql`
7. `04-indexes.sql`
8. `docker-compose.yml`
9. Recomendaciones finales de uso.

---

## Restricciones

- No uses MySQL.
- No uses scripts incompletos.
- No omitas índices ni restricciones.
- No expliques teoría innecesaria.
- No entregues pseudocódigo: entrega archivos reales.
- Todo debe estar alineado con PostgreSQL y ejecución en Docker.

---

## Instrucción final

Genera todos los archivos completos, listos para copiar y pegar, asegurando que la base de datos quede inicializada automáticamente al levantar el contenedor por primera vez.
