

---

# 📄 `technical-spec.md`

```md
# Technical Specification

## 1. Arquitectura

Se implementa una arquitectura de microservicios basada en:

- Spring Boot
- Arquitectura Hexagonal (Ports & Adapters)
- Integración asíncrona mediante eventos

---

## 2. Microservicios

### Sales Service

Responsabilidades:

- registrar ventas
- validar datos
- persistir información
- registrar trazabilidad
- generar eventos (Outbox)

---

### Shipping Service

Responsabilidades:

- consumir eventos
- crear envíos
- registrar trazabilidad
- garantizar idempotencia

---

## 3. Integración

- RabbitMQ como broker de mensajería
- exchange: `commerce.exchange`
- routing key: `sales.created`

---

## 4. Persistencia

Se utiliza PostgreSQL con una base:

- `commerce_platform`

Y dos schemas:

- `sales`
- `shipping`

---

## 5. Patrones de diseño

### Outbox Pattern
- Garantiza consistencia entre BD y eventos

### Hexagonal Architecture
- Separa dominio de infraestructura

### Adapter Pattern
- Desacopla persistencia y mensajería

---

## 6. Idempotencia

Se implementa mediante la tabla:

- `shipping.processed_event`

Estrategia:

- validar `eventId` antes de procesar
- ignorar duplicados

---

## 7. Trazabilidad

Se implementa mediante:

- `correlationId`
- tablas:
  - `sale_trace`
  - `shipment_trace`

---

## 8. Validaciones

### Entrada:
- DTOs con Bean Validation

### Negocio:
- validación de productos
- validación de estados

---

## 9. Manejo de errores

- GlobalExceptionHandler
- respuestas estándar JSON
- manejo de errores de negocio y técnicos

---

## 10. Pruebas

- Unitarias (JUnit + Mockito)
- Integración (flujo completo)

---

## 11. Docker

- PostgreSQL
- RabbitMQ
- microservicios
- docker-compose

---

## 12. Trade-offs

- PostgreSQL vs MySQL → flexibilidad (JSONB)
- RabbitMQ vs Kafka → simplicidad
- JSONB vs estructura rígida → flexibilidad

---

## 13. Riesgos

- duplicación de eventos
- fallos de red
- inconsistencias en integración

---

## 14. Mejoras futuras

- observabilidad distribuida
- circuit breakers
- retries avanzados