# Prompt para generar el microservicio Sales Service

## Objetivo

Genera un microservicio llamado `sales-service` en **Java con Spring Boot**, conectado a **PostgreSQL**, aplicando **arquitectura hexagonal / clean architecture**, para gestionar el registro de ventas de un portal de compras.

El microservicio debe permitir:

- crear una venta;
- validar datos del cliente y de la orden;
- persistir la venta en PostgreSQL;
- asignar estado inicial;
- registrar trazabilidad funcional;
- registrar un evento en una tabla outbox;
- publicar posteriormente un evento `SALE_CREATED`;
- consultar una venta registrada;
- consultar la trazabilidad de una venta.

---

## Stack técnico obligatorio

Usa:

- Java 17
- Spring Boot 3.x
- Maven
- Spring Web
- Spring Data JPA
- Spring Validation
- PostgreSQL Driver
- Lombok
- Spring AMQP para RabbitMQ
- springdoc-openapi
- Logback
- JUnit 5
- Mockito

---

## Base de datos

El microservicio debe conectarse a PostgreSQL:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://postgres-commerce:5432/commerce_platform
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: none
    properties:
      hibernate:
        default_schema: sales
        format_sql: true
```

Debe usar el schema:

```text
sales
```

Tablas disponibles:

- `sales.sales`
- `sales.sale_items`
- `sales.sale_trace`
- `sales.outbox_event`

---

## Arquitectura requerida

Organiza el proyecto usando esta estructura:

```text
sales-service/
└─ src/main/java/com/christian/sales/
   ├─ domain/
   │  ├─ model/
   │  ├─ enums/
   │  ├─ exception/
   │  └─ ports/
   │     ├─ in/
   │     └─ out/
   │
   ├─ application/
   │  ├─ usecase/
   │  ├─ dto/
   │  └─ mapper/
   │
   ├─ infrastructure/
   │  ├─ adapters/
   │  │  ├─ in/
   │  │  │  └─ web/
   │  │  └─ out/
   │  │     ├─ persistence/
   │  │     └─ messaging/
   │  ├─ config/
   │  ├─ entity/
   │  └─ repository/
   │
   └─ SalesServiceApplication.java
```

---

## Dominio requerido

Crea los siguientes modelos de dominio:

### `Sale`
Campos:

- `id`
- `customer`
- `items`
- `totalAmount`
- `status`
- `correlationId`
- `createdAt`
- `updatedAt`

### `Customer`
Campos:

- `fullName`
- `email`
- `phone`
- `postalCode`

### `SaleItem`
Campos:

- `id`
- `productId`
- `productName`
- `quantity`
- `unitPrice`
- `subtotal`

### `SaleTrace`
Campos:

- `id`
- `saleId`
- `traceType`
- `description`
- `status`
- `correlationId`
- `createdAt`

### `OutboxEvent`
Campos:

- `id`
- `eventId`
- `aggregateId`
- `aggregateType`
- `eventType`
- `payload`
- `status`
- `retryCount`
- `createdAt`
- `processedAt`

---

## Enums requeridos

Crea:

```text
SaleStatus
- RECEIVED
- VALIDATED
- REGISTERED
- FAILED

TraceType
- SALE_RECEIVED
- SALE_VALIDATED
- SALE_REGISTERED
- OUTBOX_EVENT_CREATED
- EVENT_PUBLISHED
- SALE_FAILED

OutboxStatus
- PENDING
- PUBLISHED
- FAILED
```

---

## Puertos de entrada

Crea interfaces:

```text
CreateSaleUseCase
GetSaleUseCase
GetSaleTraceUseCase
```

---

## Puertos de salida

Crea interfaces:

```text
SaleRepositoryPort
SaleTraceRepositoryPort
OutboxEventRepositoryPort
EventPublisherPort
```

---

## Casos de uso

### `CreateSaleUseCaseImpl`

Debe:

1. recibir una solicitud de creación de venta;
2. obtener o generar un `correlationId`;
3. validar que exista al menos un producto;
4. validar que cantidades y precios sean mayores que cero;
5. calcular subtotal por producto usando Streams o lambdas;
6. calcular total de la venta;
7. crear la venta con estado `REGISTERED`;
8. persistir la venta;
9. registrar trazabilidad:
   - `SALE_RECEIVED`
   - `SALE_VALIDATED`
   - `SALE_REGISTERED`
   - `OUTBOX_EVENT_CREATED`
10. crear un evento outbox `SALE_CREATED`;
11. retornar respuesta con `saleId`, `status` y `correlationId`.

Debe estar anotado con `@Transactional`.

---

## DTOs requeridos

Crea:

```text
CreateSaleRequest
CustomerRequest
SaleItemRequest
CreateSaleResponse
SaleResponse
SaleItemResponse
SaleTraceResponse
ErrorResponse
```

### `CreateSaleRequest`

Debe incluir validaciones:

- `customer` obligatorio;
- `items` obligatorio y no vacío.

### `CustomerRequest`

Debe validar:

- `fullName` obligatorio;
- `email` con `@Email`;
- `phone` con patrón de 10 dígitos;
- `postalCode` con patrón de 5 o 6 dígitos.

### `SaleItemRequest`

Debe validar:

- `productId` obligatorio;
- `productName` obligatorio;
- `quantity` mayor que cero;
- `unitPrice` mayor que cero.

---

## Endpoints requeridos

Implementa un controlador REST:

```text
POST /api/v1/sales
GET /api/v1/sales/{saleId}
GET /api/v1/sales/{saleId}/trace
```

### `POST /api/v1/sales`

Debe recibir header opcional:

```text
X-Correlation-Id
```

Si no llega, debe generarse automáticamente.

---

## Entidades JPA

Crea entidades para:

```text
SaleEntity
SaleItemEntity
SaleTraceEntity
OutboxEventEntity
```

Usa:

```java
@Table(name = "sales", schema = "sales")
```

Cuando aplique.

---

## Persistencia

Implementa adapters JPA:

```text
JpaSaleRepositoryAdapter
JpaSaleTraceRepositoryAdapter
JpaOutboxEventRepositoryAdapter
```

Y repositorios Spring Data:

```text
SaleJpaRepository
SaleTraceJpaRepository
OutboxEventJpaRepository
```

---

## Evento outbox

Cuando se cree una venta, registra un evento en `sales.outbox_event` con:

- `eventId` UUID;
- `aggregateId` igual a `saleId`;
- `aggregateType` = `SALE`;
- `eventType` = `SALE_CREATED`;
- `payload` JSON con datos básicos de la venta;
- `status` = `PENDING`;
- `retryCount` = 0.

El `payload` puede almacenarse como `String` en Java, aunque en PostgreSQL sea `JSONB`.

---

## Publicador outbox

Crea un componente:

```text
OutboxPublisherJob
```

Debe:

1. buscar eventos pendientes;
2. publicar cada evento en RabbitMQ;
3. marcar evento como `PUBLISHED`;
4. incrementar retry o marcar `FAILED` si ocurre error.

Puede usar `@Scheduled`.

---

## RabbitMQ

Configura:

- exchange: `commerce.exchange`
- routing key: `sales.created`
- queue destino sugerida: `shipping.sale-created.queue`

El `sales-service` solo debe publicar el evento. El consumo lo hará `shipping-service`.

---

## Logging y trazabilidad

Implementa:

- logs con Logback;
- uso de `correlationId`;
- registro del `correlationId` en trazabilidad de negocio;
- propagación del `correlationId` dentro del evento.

Cada log importante debe incluir:

- `correlationId`
- `saleId`, cuando exista
- acción ejecutada
- estado de negocio

---

## Manejo de errores

Crea un `GlobalExceptionHandler` para responder errores consistentes.

Debe manejar:

- errores de validación;
- venta no encontrada;
- reglas de negocio inválidas;
- errores técnicos no controlados.

Formato de error:

```json
{
  "timestamp": "2026-04-29T10:00:00",
  "status": 400,
  "error": "BAD_REQUEST",
  "message": "Validation failed",
  "path": "/api/v1/sales"
}
```

---

## OpenAPI

Configura documentación OpenAPI con springdoc.

Debe documentar:

- creación de venta;
- consulta de venta;
- consulta de trazabilidad;
- códigos de respuesta;
- ejemplos de request y response.

---

## Pruebas mínimas

Genera pruebas unitarias para:

1. crear venta válida;
2. rechazar venta sin productos;
3. calcular correctamente subtotal y total;
4. registrar evento outbox.

Usa:

- JUnit 5
- Mockito

---

## Archivos esperados

Devuelve los archivos principales completos:

1. `pom.xml`
2. `application.yml`
3. estructura de paquetes
4. clases de dominio
5. DTOs
6. casos de uso
7. puertos
8. entidades JPA
9. repositorios
10. adapters
11. controlador REST
12. configuración RabbitMQ
13. publicador outbox
14. manejador global de errores
15. pruebas unitarias básicas
16. `Dockerfile`

---

## Restricciones

- No usar MySQL.
- No mezclar lógica de negocio dentro del controlador.
- No acoplar dominio directamente a JPA.
- No omitir validaciones.
- No omitir trazabilidad.
- No omitir outbox.
- No generar pseudocódigo.
- Entregar código real y compilable.

---

## Instrucción final

Genera el microservicio completo `sales-service`, listo para integrarse con PostgreSQL y RabbitMQ usando Docker Compose, siguiendo arquitectura hexagonal, buenas prácticas de código limpio, trazabilidad, validaciones, OpenAPI y patrón Outbox.
