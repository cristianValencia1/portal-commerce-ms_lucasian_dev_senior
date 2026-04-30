
# Prompt para Generación de Pruebas (Testing Strategy)

## Objetivo

Genera una estrategia completa de pruebas para el sistema de microservicios:

- sales-service
- shipping-service

La solución debe incluir:

- pruebas unitarias
- pruebas de integración
- pruebas de flujo (E2E)
- validación de eventos
- validación de idempotencia
- validación de trazabilidad

---

## Contexto del sistema

Arquitectura:

- Microservicios (Spring Boot)
- PostgreSQL (schemas: sales, shipping)
- RabbitMQ (mensajería asíncrona)
- Outbox Pattern
- Idempotencia con processed_event
- Docker Compose

---

## Tipos de pruebas requeridas

## 1. Pruebas unitarias

### Sales Service

Debe probar:

- creación de venta válida
- rechazo de venta sin productos
- validación de email inválido
- cálculo correcto de totales
- creación de evento outbox

### Shipping Service

Debe probar:

- creación de envío desde evento válido
- rechazo de evento duplicado
- validación de existencia de envío previo
- registro en processed_event

---

## 2. Pruebas de integración

Debe validar:

### Flujo completo

1. crear venta
2. persistir en BD
3. registrar en outbox
4. publicar evento
5. consumir evento
6. crear envío
7. persistir envío

---

## 3. Pruebas de eventos

Validar:

- estructura del evento `SALE_CREATED`
- presencia de:
  - eventId
  - correlationId
  - saleId
- correcta serialización/deserialización JSON

---

## 4. Pruebas de idempotencia

Escenario:

- enviar el mismo evento 2 veces

Resultado esperado:

- solo un envío creado
- segundo evento ignorado
- registro en `processed_event`

---

## 5. Pruebas de trazabilidad

Validar:

- creación de registros en:
  - sale_trace
  - shipment_trace
- consistencia del correlationId
- orden lógico de eventos

---

## 6. Pruebas de errores

Casos:

- fallo en persistencia
- fallo en publicación de evento
- fallo en consumo de evento
- datos inválidos

Validar:

- manejo correcto de errores
- respuesta HTTP adecuada
- logs generados

---

## Frameworks y herramientas

Usa:

- JUnit 5
- Mockito
- Testcontainers (recomendado)
- Spring Boot Test
- RestAssured (opcional para E2E)

---

## Ejemplos requeridos

Genera:

### Unit Test ejemplo

- CreateSaleUseCaseTest
- CreateShipmentFromEventTest

### Integration Test ejemplo

- SalesToShippingFlowTest

---

## Buenas prácticas

- no usar datos hardcodeados innecesarios
- usar builders o factories para objetos de prueba
- aislar lógica de negocio
- usar mocks en unit tests
- usar base real en integration tests
- limpiar contexto entre pruebas

---

## Estructura esperada

```text
test/
├── unit/
│   ├── sales/
│   └── shipping/
├── integration/
│   ├── sales/
│   ├── shipping/
│   └── flow/