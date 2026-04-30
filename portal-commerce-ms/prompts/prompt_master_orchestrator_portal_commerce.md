# Prompt Maestro Orquestador - Proyecto Completo Microservicios Sales & Shipping

## Rol que debe asumir la IA

Actúa como un **arquitecto de software senior y desarrollador backend Java/Spring Boot**, con experiencia en:

- arquitectura hexagonal / clean architecture;
- microservicios;
- Java 17;
- Spring Boot 3.x;
- PostgreSQL;
- RabbitMQ;
- Docker Compose;
- patrón Outbox;
- idempotencia;
- trazabilidad distribuida;
- documentación técnica;
- pruebas unitarias e integración;
- despliegue escalable en AWS.

Tu objetivo es generar un proyecto completo, profesional, ejecutable localmente y listo para entrega técnica.

---

# 1. Objetivo general del proyecto

Genera desde cero una solución basada en microservicios para gestionar el flujo de **ventas y envíos** de un portal de compras.

La solución debe incluir:

- microservicio `sales-service`;
- microservicio `shipping-service`;
- base de datos PostgreSQL;
- RabbitMQ como broker de eventos;
- Docker Compose para ejecución local;
- arquitectura hexagonal;
- patrón Outbox;
- idempotencia;
- trazabilidad funcional y técnica;
- OpenAPI;
- pruebas;
- documentación técnica;
- SPECs;
- evidencia del uso de IA;
- propuesta de despliegue en AWS.

---

# 2. Arquitectura objetivo

La solución debe tener esta arquitectura lógica:

```text
Cliente/API Consumer
        |
        v
 sales-service
        |
        | Guarda venta + trazabilidad + outbox
        v
 PostgreSQL schema sales
        |
        | Publica evento SALE_CREATED
        v
 RabbitMQ
        |
        | Consume evento
        v
 shipping-service
        |
        | Crea envío + trazabilidad + processed_event
        v
 PostgreSQL schema shipping
```

---

# 3. Stack técnico obligatorio

Usa:

- Java 17
- Spring Boot 3.x
- Maven
- Spring Web
- Spring Data JPA
- Bean Validation
- PostgreSQL Driver
- Spring AMQP
- RabbitMQ
- Lombok
- springdoc-openapi
- Logback
- JUnit 5
- Mockito
- Docker
- Docker Compose

---

# 4. Estructura general del repositorio

Genera un repositorio con esta estructura:

```text
portal-commerce-ms/
│
├── sales-service/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│
├── shipping-service/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│
├── db/
│   ├── Dockerfile
│   ├── init/
│   │   ├── 01-init-schemas.sql
│   │   ├── 02-sales-schema.sql
│   │   ├── 03-shipping-schema.sql
│   │   └── 04-indexes.sql
│   └── sql/
│       ├── 01-init-schemas.sql
│       ├── 02-sales-schema.sql
│       ├── 03-shipping-schema.sql
│       └── 04-indexes.sql
│
├── docs/
│   ├── functional-spec.md
│   ├── technical-spec.md
│   ├── acceptance-criteria.md
│   ├── ai-usage.md
│   ├── aws-deployment.md
│   └── architecture.md
│
├── prompts/
│   ├── prompt-database.md
│   ├── prompt-sales-service.md
│   ├── prompt-shipping-service.md
│   ├── prompt-configuracion-general.md
│   └── prompt-master-orchestrator.md
│
├── docker-compose.yml
├── .env
└── README.md
```

---

# 5. Base de datos PostgreSQL

## 5.1 Requisitos

Genera una imagen Docker de PostgreSQL con:

- PostgreSQL 15.7
- base de datos `commerce_platform`
- usuario `postgres`
- contraseña `postgres`
- schema `sales`
- schema `shipping`
- scripts SQL autoejecutables en `/docker-entrypoint-initdb.d`

---

## 5.2 Scripts requeridos

Genera estos archivos:

```text
db/init/01-init-schemas.sql
db/init/02-sales-schema.sql
db/init/03-shipping-schema.sql
db/init/04-indexes.sql
```

---

## 5.3 Tablas schema `sales`

Crea:

### `sales.sales`

Campos:

- `id UUID PRIMARY KEY`
- `customer_name VARCHAR(150)`
- `customer_email VARCHAR(120)`
- `customer_phone VARCHAR(20)`
- `postal_code VARCHAR(12)`
- `total_amount NUMERIC(14,2)`
- `status VARCHAR(30)`
- `correlation_id VARCHAR(100)`
- `created_at TIMESTAMP`
- `updated_at TIMESTAMP`

### `sales.sale_items`

Campos:

- `id UUID PRIMARY KEY`
- `sale_id UUID`
- `product_id VARCHAR(80)`
- `product_name VARCHAR(150)`
- `quantity INTEGER`
- `unit_price NUMERIC(14,2)`
- `subtotal NUMERIC(14,2)`

### `sales.sale_trace`

Campos:

- `id UUID PRIMARY KEY`
- `sale_id UUID`
- `trace_type VARCHAR(50)`
- `description TEXT`
- `status VARCHAR(30)`
- `correlation_id VARCHAR(100)`
- `created_at TIMESTAMP`

### `sales.outbox_event`

Campos:

- `id UUID PRIMARY KEY`
- `event_id VARCHAR(100) UNIQUE`
- `aggregate_id UUID`
- `aggregate_type VARCHAR(50)`
- `event_type VARCHAR(50)`
- `payload JSONB`
- `status VARCHAR(30)`
- `retry_count INTEGER`
- `created_at TIMESTAMP`
- `processed_at TIMESTAMP`

---

## 5.4 Tablas schema `shipping`

Crea:

### `shipping.shipments`

Campos:

- `id UUID PRIMARY KEY`
- `sale_id UUID`
- `shipment_number VARCHAR(50) UNIQUE`
- `status VARCHAR(30)`
- `correlation_id VARCHAR(100)`
- `created_at TIMESTAMP`
- `updated_at TIMESTAMP`

### `shipping.shipment_trace`

Campos:

- `id UUID PRIMARY KEY`
- `shipment_id UUID`
- `sale_id UUID`
- `trace_type VARCHAR(50)`
- `description TEXT`
- `status VARCHAR(30)`
- `correlation_id VARCHAR(100)`
- `created_at TIMESTAMP`

### `shipping.processed_event`

Campos:

- `id UUID PRIMARY KEY`
- `event_id VARCHAR(100) UNIQUE`
- `sale_id UUID`
- `processed_at TIMESTAMP`

---

## 5.5 Índices requeridos

Crea índices para:

- `correlation_id`
- `sale_id`
- `event_id`
- `status`
- `created_at`

---

# 6. Microservicio `sales-service`

## 6.1 Responsabilidad

Debe encargarse de:

- crear ventas;
- validar cliente y productos;
- persistir venta;
- registrar trazabilidad;
- crear evento en outbox;
- publicar evento `SALE_CREATED`;
- consultar venta;
- consultar trazabilidad.

---

## 6.2 Endpoints

Implementa:

```text
POST /api/v1/sales
GET /api/v1/sales/{saleId}
GET /api/v1/sales/{saleId}/trace
```

---

## 6.3 Header de correlación

El endpoint `POST /api/v1/sales` debe aceptar:

```text
X-Correlation-Id
```

Si no llega, debe generarse automáticamente.

---

## 6.4 DTO request ejemplo

```json
{
  "customer": {
    "fullName": "Christian David Valencia",
    "email": "christian@email.com",
    "phone": "3001234567",
    "postalCode": "660001"
  },
  "items": [
    {
      "productId": "P-001",
      "productName": "Laptop",
      "quantity": 1,
      "unitPrice": 3500000
    }
  ]
}
```

---

## 6.5 Validaciones

Implementa:

- cliente obligatorio;
- nombre obligatorio;
- email válido;
- teléfono de 10 dígitos;
- código postal de 5 o 6 dígitos;
- lista de productos no vacía;
- cantidad mayor que cero;
- precio mayor que cero.

---

## 6.6 Dominio

Crea modelos:

```text
Sale
Customer
SaleItem
SaleTrace
OutboxEvent
```

Crea enums:

```text
SaleStatus
TraceType
OutboxStatus
```

---

## 6.7 Patrón Outbox

Cuando se crea una venta:

1. persistir venta;
2. persistir items;
3. registrar trazabilidad;
4. crear registro en `sales.outbox_event` con estado `PENDING`;
5. un job programado debe publicar eventos pendientes a RabbitMQ;
6. si publica correctamente, marcar como `PUBLISHED`;
7. si falla, incrementar `retry_count` o marcar como `FAILED`.

---

# 7. Microservicio `shipping-service`

## 7.1 Responsabilidad

Debe encargarse de:

- consumir eventos `SALE_CREATED`;
- crear envíos;
- registrar trazabilidad;
- evitar duplicados;
- consultar envío;
- consultar trazabilidad.

---

## 7.2 Consumo RabbitMQ

Debe escuchar:

```text
exchange: commerce.exchange
routing key: sales.created
queue: shipping.sale-created.queue
```

---

## 7.3 Endpoints

Implementa:

```text
GET /api/v1/shipments/sale/{saleId}
GET /api/v1/shipments/sale/{saleId}/trace
```

---

## 7.4 Idempotencia

Debe usar la tabla:

```text
shipping.processed_event
```

Regla:

- si `eventId` ya existe, no crear nuevo envío;
- registrar traza `DUPLICATE_EVENT_IGNORED`;
- finalizar sin error;
- si no existe, crear envío y registrar evento procesado.

---

## 7.5 Dominio

Crea modelos:

```text
Shipment
ShipmentTrace
ProcessedEvent
SaleCreatedEvent
SaleCreatedPayload
```

Crea enums:

```text
ShipmentStatus
ShipmentTraceType
```

---

# 8. Arquitectura hexagonal por servicio

Cada microservicio debe tener esta estructura:

```text
src/main/java/com/christian/{service}/
├── domain/
│   ├── model/
│   ├── enums/
│   ├── exception/
│   └── ports/
│       ├── in/
│       └── out/
├── application/
│   ├── usecase/
│   ├── dto/
│   └── mapper/
├── infrastructure/
│   ├── adapters/
│   │   ├── in/
│   │   │   ├── web/
│   │   │   └── messaging/
│   │   └── out/
│   │       ├── persistence/
│   │       └── messaging/
│   ├── config/
│   ├── entity/
│   └── repository/
└── {Service}Application.java
```

---

# 9. RabbitMQ

Configura:

```text
exchange: commerce.exchange
queue: shipping.sale-created.queue
routing key: sales.created
```

Debe existir configuración Java para:

- exchange;
- queue;
- binding;
- RabbitTemplate;
- listener.

---

# 10. Docker Compose

Genera `docker-compose.yml` con:

- `postgres-commerce`
- `rabbitmq`
- `sales-service`
- `shipping-service`

Puertos:

```text
PostgreSQL: 5432
RabbitMQ AMQP: 5672
RabbitMQ UI: 15672
sales-service: 8081
shipping-service: 8082
```

Incluye:

- red compartida;
- healthchecks;
- variables de entorno;
- dependencias con `depends_on`.

---

# 11. Configuración por servicio

## 11.1 `sales-service/application.yml`

Debe incluir:

- puerto 8081;
- conexión PostgreSQL schema `sales`;
- RabbitMQ;
- OpenAPI;
- logging.

## 11.2 `shipping-service/application.yml`

Debe incluir:

- puerto 8082;
- conexión PostgreSQL schema `shipping`;
- RabbitMQ;
- OpenAPI;
- logging.

---

# 12. Logging y trazabilidad

Implementa:

- Logback;
- `correlationId`;
- logs en operaciones clave;
- trazabilidad funcional en base de datos.

Cada log relevante debe contener:

- `correlationId`;
- `saleId`;
- `eventId`, cuando aplique;
- acción;
- estado.

---

# 13. Manejo de errores

Implementa en ambos servicios:

```text
GlobalExceptionHandler
ErrorResponse
BusinessException
NotFoundException
```

Formato estándar:

```json
{
  "timestamp": "2026-04-29T10:00:00",
  "status": 400,
  "error": "BAD_REQUEST",
  "message": "Validation failed",
  "path": "/api/v1/resource"
}
```

---

# 14. OpenAPI

Configura `springdoc-openapi` en ambos servicios.

URLs esperadas:

```text
sales-service: http://localhost:8081/swagger-ui.html
shipping-service: http://localhost:8082/swagger-ui.html
```

---

# 15. Pruebas

Genera pruebas unitarias mínimas:

## `sales-service`

- crear venta válida;
- rechazar venta sin productos;
- calcular total correctamente;
- crear evento outbox.

## `shipping-service`

- crear envío con evento válido;
- ignorar evento duplicado;
- no crear segundo envío para la misma venta;
- registrar evento procesado.

Incluye una prueba de integración sugerida:

- crear venta;
- publicar evento;
- consumir evento;
- crear envío;
- consultar venta y envío.

---

# 16. Documentación requerida

Genera archivos en `docs/`:

## `functional-spec.md`

Debe explicar:

- propósito funcional;
- actores;
- casos de uso;
- reglas de negocio;
- flujo funcional.

## `technical-spec.md`

Debe explicar:

- arquitectura;
- microservicios;
- integración;
- persistencia;
- patrones;
- idempotencia;
- trazabilidad;
- errores;
- pruebas;
- Docker;
- trade-offs;
- riesgos.

## `acceptance-criteria.md`

Debe incluir criterios claros:

- creación de venta;
- validaciones;
- evento outbox;
- publicación;
- consumo;
- idempotencia;
- consultas.

## `ai-usage.md`

Debe incluir:

- en qué partes se usó IA;
- qué se validó manualmente;
- qué decisiones no fueron delegadas;
- riesgos detectados;
- correcciones realizadas.

## `aws-deployment.md`

Debe proponer despliegue en AWS con:

- ECR;
- ECS Fargate;
- RDS PostgreSQL;
- Amazon MQ o SQS/SNS;
- CloudWatch;
- Secrets Manager;
- ALB;
- VPC;
- estrategia de escalabilidad;
- observabilidad;
- seguridad;
- criterios generales de costo.

---

# 17. README principal

Genera un `README.md` con:

- descripción del proyecto;
- arquitectura;
- requisitos;
- cómo levantar localmente;
- endpoints;
- ejemplo de request;
- comandos útiles;
- cómo ejecutar pruebas;
- cómo consultar Swagger;
- cómo reiniciar base de datos;
- decisiones técnicas principales.

---

# 18. Variables de entorno

Genera `.env` con:

```env
POSTGRES_DB=commerce_platform
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres

POSTGRES_PORT=5432
RABBITMQ_PORT=5672
RABBITMQ_UI_PORT=15672

SALES_SERVICE_PORT=8081
SHIPPING_SERVICE_PORT=8082

RABBITMQ_DEFAULT_USER=guest
RABBITMQ_DEFAULT_PASS=guest
```

---

# 19. Requisitos de calidad

La solución debe:

- compilar sin errores;
- tener código limpio;
- evitar lógica de negocio en controladores;
- desacoplar dominio de infraestructura;
- usar validaciones;
- implementar idempotencia;
- implementar outbox;
- tener documentación suficiente;
- ser ejecutable con un solo comando.

Comando esperado:

```bash
docker compose up --build
```

---

# 20. Restricciones

- No usar MySQL.
- No omitir PostgreSQL.
- No omitir RabbitMQ.
- No omitir arquitectura hexagonal.
- No omitir outbox.
- No omitir idempotencia.
- No omitir trazabilidad.
- No generar pseudocódigo.
- No entregar fragmentos incompletos.
- No mezclar capas.
- No colocar lógica de negocio en controladores o listeners.
- No omitir pruebas.
- No omitir documentación.

---

# 21. Salida esperada

Genera el proyecto completo con:

1. árbol de carpetas;
2. archivos completos;
3. código fuente de ambos microservicios;
4. scripts SQL;
5. Dockerfiles;
6. docker-compose;
7. `.env`;
8. documentación;
9. pruebas;
10. instrucciones de ejecución.

---

# 22. Instrucción final

Genera la solución completa **portal-commerce-ms** de punta a punta, lista para copiar en un repositorio, compilar, ejecutar localmente con Docker Compose y presentar como prueba técnica senior.

Asegúrate de que cada archivo generado sea real, coherente, funcional y esté alineado con arquitectura hexagonal, PostgreSQL, RabbitMQ, Outbox Pattern, idempotencia, trazabilidad, OpenAPI, pruebas y documentación técnica.
