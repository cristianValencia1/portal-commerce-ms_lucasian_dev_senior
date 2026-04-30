# Prompt para generar el microservicio Shipping Service

## Objetivo

Genera un microservicio llamado `shipping-service` en **Java con Spring Boot**, conectado a **PostgreSQL**, aplicando **arquitectura hexagonal / clean architecture**, para gestionar la creación de envíos a partir de eventos generados por el microservicio `sales-service`.

El microservicio debe permitir:

- consumir eventos `SALE_CREATED` desde RabbitMQ;
- crear un envío asociado a una venta;
- registrar trazabilidad funcional del envío;
- asignar estado inicial del proceso logístico;
- evitar duplicados si el mismo mensaje se procesa más de una vez;
- consultar un envío por `saleId`;
- consultar la trazabilidad del envío por `saleId`.

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
        default_schema: shipping
        format_sql: true
```

Debe usar el schema:

```text
shipping
```

Tablas disponibles:

- `shipping.shipments`
- `shipping.shipment_trace`
- `shipping.processed_event`

---

## Arquitectura requerida

Organiza el proyecto usando esta estructura:

```text
shipping-service/
└─ src/main/java/com/christian/shipping/
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
   │  │  │  ├─ web/
   │  │  │  └─ messaging/
   │  │  └─ out/
   │  │     └─ persistence/
   │  ├─ config/
   │  ├─ entity/
   │  └─ repository/
   │
   └─ ShippingServiceApplication.java
```

---

## Dominio requerido

Crea los siguientes modelos de dominio:

### `Shipment`
Campos:

- `id`
- `saleId`
- `shipmentNumber`
- `status`
- `correlationId`
- `createdAt`
- `updatedAt`

### `ShipmentTrace`
Campos:

- `id`
- `shipmentId`
- `saleId`
- `traceType`
- `description`
- `status`
- `correlationId`
- `createdAt`

### `ProcessedEvent`
Campos:

- `id`
- `eventId`
- `saleId`
- `processedAt`

### `SaleCreatedEvent`
Debe representar el evento recibido desde `sales-service`.

Campos mínimos:

- `eventId`
- `eventType`
- `occurredAt`
- `correlationId`
- `payload`

### `SaleCreatedPayload`
Campos mínimos:

- `saleId`
- `customerEmail`
- `customerPhone`
- `postalCode`
- `items`

---

## Enums requeridos

Crea:

```text
ShipmentStatus
- PENDING
- CREATED
- FAILED

ShipmentTraceType
- EVENT_RECEIVED
- DUPLICATE_EVENT_IGNORED
- SHIPMENT_CREATED
- SHIPMENT_FAILED
```

---

## Puertos de entrada

Crea interfaces:

```text
CreateShipmentFromSaleEventUseCase
GetShipmentBySaleIdUseCase
GetShipmentTraceUseCase
```

---

## Puertos de salida

Crea interfaces:

```text
ShipmentRepositoryPort
ShipmentTraceRepositoryPort
ProcessedEventRepositoryPort
```

---

## Caso de uso principal

### `CreateShipmentFromSaleEventUseCaseImpl`

Debe:

1. recibir un `SaleCreatedEvent`;
2. obtener `eventId`, `saleId` y `correlationId`;
3. registrar log de recepción del evento;
4. validar si el `eventId` ya existe en `processed_event`;
5. si el evento ya fue procesado:
   - registrar traza `DUPLICATE_EVENT_IGNORED`;
   - no crear un nuevo envío;
   - finalizar sin error;
6. si no existe:
   - crear un envío asociado a `saleId`;
   - generar `shipmentNumber`;
   - asignar estado inicial `CREATED`;
   - persistir el envío;
   - registrar traza `SHIPMENT_CREATED`;
   - registrar el evento en `processed_event`;
7. ejecutar todo dentro de una transacción.

Debe estar anotado con `@Transactional`.

---

## Reglas de negocio

Implementa estas reglas:

- No crear dos envíos para la misma venta.
- No reprocesar un evento ya registrado.
- Todo evento recibido debe generar trazabilidad.
- Si falla la creación del envío, debe registrarse traza de error.
- El `eventId` debe ser único.
- El `shipmentNumber` debe ser único.

---

## DTOs requeridos

Crea:

```text
SaleCreatedEvent
SaleCreatedPayload
SaleCreatedItem
ShipmentResponse
ShipmentTraceResponse
ErrorResponse
```

---

## Endpoints requeridos

Implementa un controlador REST:

```text
GET /api/v1/shipments/sale/{saleId}
GET /api/v1/shipments/sale/{saleId}/trace
```

### `GET /api/v1/shipments/sale/{saleId}`

Debe retornar la información del envío asociado a la venta.

### `GET /api/v1/shipments/sale/{saleId}/trace`

Debe retornar la trazabilidad funcional del envío.

---

## Entidades JPA

Crea entidades para:

```text
ShipmentEntity
ShipmentTraceEntity
ProcessedEventEntity
```

Usa:

```java
@Table(name = "shipments", schema = "shipping")
```

Cuando aplique.

---

## Persistencia

Implementa adapters JPA:

```text
JpaShipmentRepositoryAdapter
JpaShipmentTraceRepositoryAdapter
JpaProcessedEventRepositoryAdapter
```

Y repositorios Spring Data:

```text
ShipmentJpaRepository
ShipmentTraceJpaRepository
ProcessedEventJpaRepository
```

Métodos mínimos requeridos:

```text
findBySaleId
existsBySaleId
existsByEventId
save
findTraceBySaleId
```

---

## RabbitMQ

Configura el consumo desde:

- exchange: `commerce.exchange`
- routing key: `sales.created`
- queue: `shipping.sale-created.queue`

El listener debe estar en:

```text
infrastructure/adapters/in/messaging/SaleCreatedEventListener.java
```

Debe:

1. recibir el mensaje;
2. deserializar el JSON;
3. extraer el `correlationId`;
4. enviar el evento al caso de uso;
5. manejar errores de consumo;
6. permitir reintentos según configuración.

---

## Manejo de duplicados

La idempotencia debe basarse en la tabla:

```text
shipping.processed_event
```

Estrategia:

1. consultar `existsByEventId(eventId)`;
2. si existe, ignorar procesamiento;
3. si no existe, crear envío y guardar evento procesado;
4. todo en una transacción.

---

## Logging y trazabilidad

Implementa:

- logs con Logback;
- uso de `correlationId`;
- logs en recepción, procesamiento, duplicado y error;
- trazabilidad funcional en `shipment_trace`.

Cada log importante debe incluir:

- `correlationId`
- `eventId`
- `saleId`
- acción ejecutada
- estado de negocio

---

## Manejo de errores

Crea un `GlobalExceptionHandler` para endpoints REST.

Debe manejar:

- envío no encontrado;
- errores de negocio;
- errores técnicos no controlados.

Formato de error:

```json
{
  "timestamp": "2026-04-29T10:00:00",
  "status": 404,
  "error": "NOT_FOUND",
  "message": "Shipment not found",
  "path": "/api/v1/shipments/sale/{saleId}"
}
```

---

## OpenAPI

Configura documentación OpenAPI con springdoc.

Debe documentar:

- consulta de envío;
- consulta de trazabilidad;
- códigos de respuesta;
- ejemplos de response.

---

## Pruebas mínimas

Genera pruebas unitarias para:

1. crear envío con evento válido;
2. ignorar evento duplicado;
3. no crear segundo envío para la misma venta;
4. registrar evento procesado.

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
5. DTOs de eventos y respuestas
6. casos de uso
7. puertos
8. entidades JPA
9. repositorios
10. adapters
11. listener RabbitMQ
12. controlador REST
13. configuración RabbitMQ
14. manejador global de errores
15. pruebas unitarias básicas
16. `Dockerfile`

---

## Restricciones

- No usar MySQL.
- No mezclar lógica de negocio dentro del listener.
- No acoplar dominio directamente a JPA.
- No omitir idempotencia.
- No omitir trazabilidad.
- No omitir control de duplicados.
- No generar pseudocódigo.
- Entregar código real y compilable.

---

## Instrucción final

Genera el microservicio completo `shipping-service`, listo para integrarse con PostgreSQL y RabbitMQ usando Docker Compose, siguiendo arquitectura hexagonal, buenas prácticas de código limpio, idempotencia, trazabilidad, OpenAPI y consumo asíncrono de eventos `SALE_CREATED`.
