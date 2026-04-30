# Shipping Service — Documentación técnica

Última actualización: 2026-04-29

## Resumen

Este documento describe de forma técnica y funcional el `shipping-service`, su arquitectura, flujo de procesamiento (consumo de eventos, persistencia y exposición de APIs), manejo de errores, despliegue y pruebas básicas.

## Arquitectura y capas

- Lenguaje / Runtime: Java 17
- Framework: Spring Boot 3.1.5
- Componentes: Spring Web, Spring Data JPA, Spring AMQP, Spring Boot Actuator, Springdoc OpenAPI

Capas lógicas:

- Adapters (in.web): controladores REST y `RestExceptionHandler`.
- Application: servicios que procesan eventos y orquestan lógica de envío.
- Domain: entidades y excepciones (`BusinessValidationException`, `ResourceNotFoundException`).
- Infrastructure: JPA repositories, listeners AMQP, configuración datasource.

## Flujo principal (consumo de eventos `sale.created`)

1. `shipping-service` escucha la cola `shipping.sale-created.queue` configurada en RabbitMQ.
2. Al recibir un mensaje `sale.created`, el listener valida el payload y verifica idempotencia (recomendado).
3. El listener crea/transforma un `ShippingOrder` y persiste en Postgres (schema `shipping`).
4. En caso de error, se puede requeuear o guardar en una tabla de errores para análisis.

## Endpoints y observabilidad

- Actuator health: `http://localhost:8082/actuator/health`
- Swagger UI: `http://localhost:8082/swagger-ui/index.html`

## Persistencia

- DataSource: HikariCP conectado a `jdbc:postgresql://postgres-commerce:5432/commerce_platform`.
- Schema por defecto: `shipping` (configurado con `HIBERNATE_DEFAULT_SCHEMA`).

## Despliegue y pruebas

Levantar todo con Docker Compose:

```bash
cd portal-commerce-ms
docker-compose up --build -d
```

Probar consumo de eventos (flujo E2E):

1. Crear una venta mediante `sales-service` API.
2. Verificar que `shipping-service` procesó el evento revisando logs o la tabla correspondiente en DB schema `shipping`.

## Recomendaciones

- Implementar idempotency key y tabla `processed_events`.
- Manejar retries y backoff en consumidores AMQP.
- Documentar contratos de evento y versionarlos.

---

Para detalles sobre el despliegue y comandos, ver `portal-commerce-ms/README.md`.
