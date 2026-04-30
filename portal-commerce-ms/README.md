# Portal Commerce MS — Documentación del sistema

Última actualización: 2026-04-29

## Propósito

Este documento central describe la arquitectura de `portal-commerce-ms`, enumera los microservicios internos, su responsabilidad funcional, endpoints principales, y pasos para desplegar y probar localmente mediante Docker Compose.

## Resumen de componentes

- `postgres-commerce` — Base de datos PostgreSQL con scripts de inicialización (schemas y datos mínimos).
- `rabbitmq` — Broker de mensajería RabbitMQ con UI de management.
- `sales-service` — Microservicio responsable de gestión de ventas (API REST, persistencia en schema `sales`, publicación de eventos `sale.created`).
- `shipping-service` — Microservicio responsable de orquestar envíos y procesar eventos de ventas (consumer de `sale.created`, persistencia en schema `shipping`).

## Estructura del repositorio (selección)

- [sales-service](sales-service)
- [shipping-service](shipping-service)
- [db](db)
- docker-compose.yml
- .env

## Descripción funcional por microservicio

### sales-service
- Puerto: `8081`
- Responsabilidad: Exponer API REST para CRUD de ventas, validación, persistencia en Postgres (schema `sales`) y publicar eventos de tipo `sale.created` a RabbitMQ.
- Endpoints importantes:
  - `GET /actuator/health` — health
  - OpenAPI JSON: `http://localhost:8081/api/v1/openapi`
  - Swagger UI: `http://localhost:8081/api/v1/swagger-ui/index.html`
- Documentación detallada: [sales-service/README.md](sales-service/README.md)

### shipping-service
- Puerto: `8082`
- Responsabilidad: Consumir eventos de ventas (`sale.created`), crear órdenes de envío, persistir en schema `shipping` y exponer endpoints de consulta.
- Endpoints importantes:
  - `GET /actuator/health` — health
  - OpenAPI JSON: `http://localhost:8082/api/v1/openapi`
  - Swagger UI: `http://localhost:8082/api/v1/swagger-ui/index.html`
- Documentación detallada: [shipping-service/README.md](shipping-service/README.md)

### postgres-commerce
- Puerto: `5432` (mapeado a `${POSTGRES_PORT}`)
- Inicializa schemas y tablas desde `db/sql/`.

### rabbitmq
- Management UI: `http://localhost:15672` (guest/guest)

## Flujo general (request → persistence → event → consumer)

1. Cliente llama a `sales-service` (POST /api/sales).
2. `sales-service` valida, persiste entidad `Sale` en schema `sales` y publica evento `sale.created` (incluye `eventId`, `saleId`, payload).
3. `shipping-service` consume `sale.created` desde la cola `shipping.sale-created.queue`, procesa el evento y persiste `ShippingOrder` en schema `shipping`.

## Despliegue y ejecución local

1. Copia/edita `.env` en la raíz si es necesario (ya existe un `.env` con valores por defecto).
2. Levanta todo con Docker Compose:

```bash
cd portal-commerce-ms
docker-compose up --build -d
```

3. Verifica health endpoints:

```bash
curl -v http://localhost:8081/actuator/health
curl -v http://localhost:8082/actuator/health
```

4. Revisar logs:

```bash
docker compose logs --tail=200 sales-service
docker compose logs --tail=200 shipping-service
```

## Contratos de eventos y recomendaciones

- Eventos deben contener `eventId` y `createdAt` para trazabilidad.
- Mantener JSON Schema de eventos y versionarlo (ej. `sale.created.v1`).
- Implementar idempotencia en consumidores: tabla `processed_events(event_id, processed_at)`.

## Observabilidad y pruebas

- Actuator y metrics en cada servicio.
- RabbitMQ management UI para inspección de colas y mensajes.
- Para pruebas E2E: crear una venta vía `sales-service` y validar que `shipping-service` la procesa (revisar DB o logs).

---

Ver README individuales para detalles por servicio.

# portal-commerce-ms

Commerce microservices platform for local development with:

- `sales-service` (Spring Boot)
- `shipping-service` (Spring Boot)
- `PostgreSQL` with separate schemas for sales and shipping
- `RabbitMQ` for asynchronous integration
- Docker Compose orchestration

## Arquitectura general

Este repositorio orquesta un ecosistema de microservicios en un mismo entorno local:

- `postgres-commerce`: base de datos PostgreSQL con volumen persistente
- `rabbitmq`: broker RabbitMQ con panel de administración
- `sales-service`: expone API REST para crear y consultar ventas
- `shipping-service`: consume eventos de venta y expone consulta de envíos

## Inicio rápido

Desde el directorio raíz `portal-commerce-ms`, ejecuta:

```bash
docker compose up --build
```

Para ejecutar en segundo plano:

```bash
docker compose up --build -d
```

Para detener y borrar contenedores y volúmenes:

```bash
docker compose down -v
```

## Variables de entorno

El archivo `.env` define la configuración del entorno local:

- `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD`
- `POSTGRES_PORT`
- `RABBITMQ_PORT`, `RABBITMQ_MANAGEMENT_PORT`
- `SALES_SERVICE_PORT`, `SHIPPING_SERVICE_PORT`
- `DB_URL`, `DB_USER`, `DB_PASSWORD`
- `RABBIT_HOST`
- `EXCHANGE_NAME`, `ROUTING_KEY`, `QUEUE_NAME`

## Servicios expuestos

### RabbitMQ

- AMQP: `amqp://guest:guest@localhost:5672`
- Management UI: `http://localhost:15672`

### Sales Service

- Base URL: `http://localhost:8081/api/v1/sales`
- Crear venta: `POST /api/v1/sales`
- Consultar venta: `GET /api/v1/sales/{saleId}`
- Consultar rastro de venta: `GET /api/v1/sales/{saleId}/trace`

### Shipping Service

- Base URL: `http://localhost:8082/api/v1/shipments`
- Consultar envío por venta: `GET /api/v1/shipments/sale/{saleId}`
- Consultar rastro de envío: `GET /api/v1/shipments/sale/{saleId}/trace`

## Ejemplo de payload de venta

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

## Flujo de eventos

1. `sales-service` recibe la creación de una venta.
2. La venta se guarda en PostgreSQL en el esquema `sales`.
3. Un evento `sales.created` se publica en `commerce.exchange`.
4. `shipping-service` consume el evento desde `shipping.sale-created.queue`.
5. `shipping-service` guarda el estado de envío en el esquema `shipping`.

## Estructura de archivos clave

- `docker-compose.yml`
- `.env`
- `application.yml`
- `sales-service/Dockerfile`
- `shipping-service/Dockerfile`
- `db/Dockerfile`
- `db/sql/` (scripts de inicialización de base de datos)

## Archivos fuente clave (ejemplos)

- `sales-service`:
  - [SalesServiceApplication.java](sales-service/src/main/java/com/christian/sales/SalesServiceApplication.java)
  - [RestExceptionHandler.java](sales-service/src/main/java/com/christian/sales/infrastructure/adapters/in/web/RestExceptionHandler.java)
  - [pom.xml](sales-service/pom.xml)
  - [Dockerfile](sales-service/Dockerfile)
- `shipping-service`:
  - [ShippingServiceApplication.java](shipping-service/src/main/java/com/christian/shipping/ShippingServiceApplication.java)
  - [RestExceptionHandler.java](shipping-service/src/main/java/com/christian/shipping/infrastructure/adapters/in/web/RestExceptionHandler.java)
  - [pom.xml](shipping-service/pom.xml)
  - [Dockerfile](shipping-service/Dockerfile)

## Notas adicionales

- La base de datos se inicializa con los scripts en `db/sql` al iniciar el contenedor.
- La configuración Spring Boot de cada servicio se basa en variables de entorno.
- El broker RabbitMQ y los servicios comparten la red Docker `commerce-net`.
