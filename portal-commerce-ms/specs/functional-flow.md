# Servicio de Ventas — Flujo End-to-End y descripción técnica

Última actualización: 2026-04-30

Resumen ejecutivo
- Propósito: describir, desde la perspectiva de arquitecto de software, el flujo completo de una venta (API, dominio, persistencia, trazas, outbox y mensajería), detallar las responsabilidades de cada componente y explicar las decisiones técnicas y métodos clave para fines de sustentación.

Alcance
- `sales-service` (creación de venta, persistencia, outbox y publicación de evento)
- interacción con `postgres-commerce` (schema `sales`) y `rabbitmq` (exchange/cola)

1. Flujo end-to-end (alto nivel)
1. Cliente HTTP realiza POST `/api/v1/sales` enviando JSON con `customer` y `items`.
2. Controlador REST valida el body con `@Valid` y mapea a DTO `CreateSaleRequest`.
3. `CreateSaleUseCaseImpl.create()` orquesta la creación:
   - valida reglas de negocio (cantidad, precio), genera `correlationId` y `id` de la venta;
   - construye `Sale` (modelo de dominio) y llama al puerto de persistencia `SaleRepositoryPort.save()`;
   - crea registros de trazabilidad (`SaleTrace`) a través de `SaleTraceRepositoryPort`;
   - construye y persiste un `OutboxEvent` (persisted via `OutboxEventRepositoryPort`);
   - publica el evento de negocio (vía `EventPublisherPort` que delega en `RabbitEventPublisher` o similar).
4. Transacciones y atomicidad: la persistencia de la venta y del outbox deben estar coordinadas para garantizar consistencia (en esta implementación se persiste la venta y luego se crean trazas y outbox dentro de la misma transacción `@Transactional` en la capa de caso de uso).
5. Un proceso externo o hilo publica los outbox events a RabbitMQ si se usa patrón outbox asíncrono; alternativamente la publicación se hace directamente tras persistir (con cuidado de no perder mensajes en fallos).

2. Componentes y responsabilidades (con referencias a código)
- API layer (adaptador in web)
  - `SalesController` ([sales-service/src/.../SalesController.java](sales-service/src/main/java/com/christian/sales/infrastructure/adapters/in/web/SalesController.java))
  - Responsabilidad: validar input (`@Valid`), traducir DTOs y devolver `CreateSaleResponse`.

- Application / Use Cases
  - `CreateSaleUseCaseImpl` ([sales-service/src/.../CreateSaleUseCaseImpl.java](sales-service/src/main/java/com/christian/sales/application/usecase/CreateSaleUseCaseImpl.java))
  - Métodos clave:
    - `create(CreateSaleRequest request)` — entrada del caso de uso. Rol: orquestación transaccional, aplicar reglas de negocio, persistir agregado, crear outbox y publicar evento.
    - `recordTrace(Sale sale, TraceType traceType, String description)` — crea objetos `SaleTrace` y los persiste; su rol es proporcionar trazabilidad del flujo de negocio.
  - Por qué: centraliza lógica de dominio (evita lógica de negocio en controladores o repositorios) y facilita pruebas unitarias.

- Domain model
  - `Sale`, `SaleItem`, `SaleTrace`, `OutboxEvent` (modelos de dominio).
  - Rol: representar invariantes y datos de negocio.

- Adapters / Persistence
  - `SaleRepositoryAdapter` ([.../SaleRepositoryAdapter.java]) — convierte `Sale` ⇄ `SaleEntity` y delega en `SaleJpaRepository`.
  - `SaleTraceRepositoryAdapter` — persiste trazas.
  - `OutboxEventRepositoryAdapter` — persiste eventos outbox.
  - Por qué adapters: separan la infraestructura (JPA) del dominio (puertos), permitiendo pruebas y reemplazo de tecnología.

- Repositorios JPA
  - `SaleJpaRepository`, `SaleTraceJpaRepository`, `OutboxEventJpaRepository`.
  - Mapping: entidades JPA están en `infrastructure/entity` y tienen `@Column(name=...)` para alinear con el schema SQL (evita errores como `customer_full_name does not exist`).

- Messaging / Publicador
  - `RabbitEventPublisher` — serializa el dominio a JSON y envía a un exchange/queue.
  - Alternativa: publicar a través del outbox processor (recomendado para resiliencia).

3. Transacciones y orden de persistencia
- La implementación aplica `@Transactional` en `CreateSaleUseCaseImpl.create()` para garantizar que la creación de `Sale`, `SaleTrace` y `OutboxEvent` ocurran en una unidad de trabajo.
- Cuestión crítica: si se publica el evento AMQP dentro de la misma transacción (antes de commit), existe riesgo de publicación con rollback; por eso recomendamos:
  1) persistir `Sale` + `OutboxEvent` dentro de la transacción;
  2) un worker (o thread) lee outbox rows y publica a RabbitMQ de forma idempotente (patrón outbox).

4. Modelado de entidades y decisiones técnicas
- IDs: UUID para `Sale.id`, `SaleItem.id`, `SaleTrace.id` y `OutboxEvent.id` (evita colisiones y facilita correlación distribuida).
- `OutboxEvent.payload`: decisión entre `TEXT` y `JSONB` en Postgres. Si se requiere consultas JSON en BD, usar `JSONB` y mapear `JsonNode`/`ObjectNode`. En esta base de ejemplo se dejó `TEXT` por simplicidad y compatibilidad con JPA `String`.
- Validaciones: DTOs usan Jakarta Validation (`@NotNull`, `@NotEmpty`, `@Pattern`, `@DecimalMin`) para garantizar integridad a la entrada.

5. Manejo de errores y API
- `RestExceptionHandler` centraliza conversiones de `MethodArgumentNotValidException`, `ConstraintViolationException`, `BusinessValidationException` y otros a respuestas HTTP amigables.
- Patrón: devolver `400` para validaciones y `500` para errores no esperados, con `correlationId` en logs para rastreo.

6. Observabilidad, métricas y logging
- Añadir trazas distribuidas: propagar `correlationId` en headers y en payloads de eventos.
- Métricas: contar ventas creadas, retries outbox, latencia de persistencia y publish attempts.
- Logs: estructurados (JSON) con `saleId`, `correlationId` y `event` para auditoría.

7. Seguridad y validación
- Autenticación/Autorización (no implementado en el ejemplo): proteger endpoints con JWT/OAuth2; validar scopes para creación de ventas.
- Sanitización de entradas: aunque se validan tipos y patrones, escapar/limitar longitudes y validar códigos de producto contra catálogo.

8. Testing
- Unit tests:
  - Mockear puertos (`SaleRepositoryPort`, `EventPublisherPort`) y probar `CreateSaleUseCaseImpl.create()` en escenarios: happy path, cantidades inválidas, repo failure.
- Integration tests:
  - arrancar Postgres en memoria (testcontainers) y validar persistencia, traces y outbox rows;
  - pruebas E2E usando docker-compose local y curl/Postman para validar contrato HTTP y flujo asíncrono.

9. Deployment y operación
- Dockerfile: empaquetado con `spring-boot:repackage` para obtener jar ejecutable.
- docker-compose: orquesta `postgres-commerce`, `rabbitmq`, `sales-service`, `shipping-service`.
- Healthchecks: exponer `/actuator/health` y configurar dependencias de `depends_on` en compose.

10. Puntos de defensa / rationale arquitectónico
- Hexagonal / Ports & Adapters: facilita intercambiar infra sin tocar dominio; permite probar casos de uso aislados.
- Outbox pattern: garantiza durabilidad de eventos sin depender de la disponibilidad inmediata de RabbitMQ.
- Uso de UUID: adecuado para sistemas distribuidos y correlación entre servicios.
- Validación en la frontera (DTOs) + validación en el dominio: doble capa para seguridad y robustez.

11. Referencias a archivos clave (para la sustentación)
- `CreateSaleUseCaseImpl` — orquestación y transacción: [sales-service/src/main/java/com/christian/sales/application/usecase/CreateSaleUseCaseImpl.java](sales-service/src/main/java/com/christian/sales/application/usecase/CreateSaleUseCaseImpl.java)
- `SalesController` — entrada HTTP: [sales-service/src/main/java/com/christian/sales/infrastructure/adapters/in/web/SalesController.java](sales-service/src/main/java/com/christian/sales/infrastructure/adapters/in/web/SalesController.java)
- `SaleRepositoryAdapter` — mapping entidad/ dominio: [sales-service/src/main/java/com/christian/sales/infrastructure/adapters/out/persistence/SaleRepositoryAdapter.java](sales-service/src/main/java/com/christian/sales/infrastructure/adapters/out/persistence/SaleRepositoryAdapter.java)
- `OutboxEventEntity` — esquema de outbox: [sales-service/src/main/java/com/christian/sales/infrastructure/entity/OutboxEventEntity.java](sales-service/src/main/java/com/christian/sales/infrastructure/entity/OutboxEventEntity.java)
- SQL schema: [db/sql/02-sales-schema.sql](db/sql/02-sales-schema.sql)

12. Recomendaciones operacionales
- Implementar un outbox-processor con retries exponenciales y dead-lettering.
- Añadir pruebas contractuales (Pact o similares) entre `sales-service` y `shipping-service`.
- Monitorizar la latencia de commit + publicación y el tamaño del outbox para dimensionamiento.

Conclusión
- La arquitectura aplicada (hexagonal + outbox + eventos) proporciona un balance entre consistencia y resiliencia para integraciones asincrónicas. El documento anterior resume los flujos técnicos que debes exponer en la sustentación: responsabilidades, métodos clave, decisiones técnicas y recomendaciones para producción.

---
Si quieres, puedo:
- generar una versión en PDF/Markdown formateado para presentación; o
- expandir la sección de métodos con fragmentos de código y números de línea exactos para cada método (útil para la defensa).
