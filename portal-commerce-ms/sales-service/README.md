# Sales Service — Documentación técnica

Última actualización: 2026-04-29

## Resumen

Este documento describe de forma técnica y funcional el `sales-service`, su arquitectura, flujo de procesamiento (sincrónico y asíncrono), manejo de errores, persistencia, despliegue y pruebas básicas.

El `sales-service` forma parte del conjunto `portal-commerce-ms` y se integra con `postgres-commerce` (PostgreSQL) y `rabbitmq` (RabbitMQ) mediante `docker-compose`.

## Arquitectura y capas

- Lenguaje / Runtime: Java 17
- Framework: Spring Boot 3.1.5
- Componentes principales: Spring Web (REST), Spring Data JPA, Spring AMQP, Spring Boot Actuator, Springdoc OpenAPI

Capas lógicas:

- Adapters (in.web): controladores REST y `RestExceptionHandler` para normalizar errores.
- Application: servicios / casos de uso que coordinan la lógica de negocio.
- Domain: entidades, excepciones de dominio (`BusinessValidationException`, `ResourceNotFoundException`).
- Infrastructure: repositorios JPA, productores/consumidores AMQP, configuración de datasource.

Archivos clave (ruta relativa al workspace):

- `portal-commerce-ms/sales-service/src/main/java/com/christian/sales/SalesServiceApplication.java`
- `portal-commerce-ms/sales-service/src/main/java/com/christian/sales/infrastructure/adapters/in/web/RestExceptionHandler.java`
- `portal-commerce-ms/sales-service/pom.xml`
- `portal-commerce-ms/sales-service/Dockerfile`
- `portal-commerce-ms/docker-compose.yml`
- `portal-commerce-ms/.env`

## Flujo HTTP (síncrono) — paso a paso

1. El cliente realiza una petición HTTP (por ejemplo POST `/api/sales`) al `sales-service`.
2. Spring MVC enruta la petición al controlador correspondiente. Los DTOs recibidos se validan con Jakarta Validation (`@Valid`).
   - Si la validación falla, Spring lanza `MethodArgumentNotValidException`.
   - `RestExceptionHandler` captura la excepción y devuelve un `ErrorResponse` con `HTTP 400` y lista de errores por campo.
3. Si la entrada es válida, el controlador delega al servicio de aplicación (caso de uso) que aplica reglas de negocio.
   - Si se detecta una violación de reglas se lanza `BusinessValidationException` → manejado como `400`.
   - Si no se encuentra un recurso se lanza `ResourceNotFoundException` → manejado como `404`.
4. El servicio usa repositorios JPA para persistir/consultar entidades en PostgreSQL.
5. Tras el éxito, el controlador retorna `HTTP 201` o `200` con el DTO de respuesta.

## Flujo asíncrono (eventos via RabbitMQ)

1. Después de persistir una venta (sale), el `sales-service` publica un evento (por ejemplo `sale.created`) al `Exchange` definido.
2. Configuración del broker y nombres de exchange/queue/routing key provienen de variables de entorno en `.env` (`EXCHANGE_NAME`, `ROUTING_KEY`, `QUEUE_NAME`).
3. `shipping-service` (u otros consumidores) están suscritos a la cola y procesan el evento.
4. Recomendaciones: incluir un `eventId` único en el payload y persistir eventos procesados en el consumidor para idempotencia.

## Manejo de errores y respuestas estandarizadas

- `ErrorResponse` (estructura usada por `RestExceptionHandler`):
  - `timestamp`: fecha/hora del error
  - `message`: resumen legible del error
  - `details`: lista de strings con mensajes por campo o causa

- Mapeos recomendados:
  - `MethodArgumentNotValidException` → `400 Bad Request` (detalles de validación)
  - `BusinessValidationException` → `400 Bad Request`
  - `ResourceNotFoundException` → `404 Not Found`
  - Excepciones no manejadas → `500 Internal Server Error`

## Persistencia

- DataSource: HikariCP conectado a `jdbc:postgresql://postgres-commerce:5432/commerce_platform` (ver `.env`).
- Cada servicio usa un schema por defecto según `HIBERNATE_DEFAULT_SCHEMA` (`sales` para este servicio).
- Repositorios: interfaces JPA en `infrastructure.repository` que exponen operaciones CRUD y queries custom.
- Inicialización de DB: scripts en `db/sql/` (copiados por el contenedor postgres a `/docker-entrypoint-initdb.d/`).

## Observabilidad y endpoints útiles

- Actuator health:
  - Sales: `http://localhost:8081/actuator/health`
- OpenAPI / Swagger (springdoc):
  - OpenAPI JSON: `http://localhost:8081/api/v1/openapi`
  - Swagger UI (navegador): `http://localhost:8081/api/v1/swagger-ui/index.html`
  - Ejemplo curl para obtener OpenAPI JSON:

```bash
curl -sS http://localhost:8081/api/v1/openapi | jq .
```
- RabbitMQ Management UI:
  - `http://localhost:15672` (usuario `guest` / contraseña `guest`)

## Comandos para desarrollo y pruebas locales

Levantar todo con Docker Compose (determinístico y replicable):

```bash
cd portal-commerce-ms
docker-compose up --build -d
```

Comprobar health:

```bash
curl -v http://localhost:8081/actuator/health
curl -v http://localhost:8082/actuator/health
```

Ver logs (ejemplo sales):

```bash
docker compose logs --tail=200 sales-service
```

Para ejecutar sólo el build del JAR localmente (sin Docker):

```bash
cd portal-commerce-ms/sales-service
mvn -B package -DskipTests
```

> Nota: en entornos Windows sin `mvn` en PATH, usar la imagen Maven o instalar Maven localmente.

## Contratos de mensaje (ejemplo)

Ejemplo de evento `sale.created` (JSON):

```json
{
  "eventId": "uuid-v4",
  "saleId": 12345,
  "timestamp": "2026-04-29T23:55:00Z",
  "customer": {
    "id": 987,
    "name": "Juan Perez"
  },
  "items": [ { "sku": "ABC", "qty": 2, "price": 12.5 } ]
}
```

Versionar y documentar estos contratos (JSON Schema/OpenAPI) es recomendable.

## Pruebas recomendadas

- Unitarias: Mockito + JUnit (componentes, servicios de dominio y repositorios mockeados).
- Integración: levantar `docker-compose` y ejecutar pruebas que consuman/produzcan eventos.
- End-to-end: simular creación de venta y verificar que `shipping-service` procesa el evento correctamente y persiste el resultado.

## Puntos abiertos y recomendaciones

- Añadir `idempotency` en consumidores para evitar doble procesamiento.
- Externalizar configuraciones sensibles (usar secretos en despliegues productivos, no `.env`).
- Añadir retries y circuit-breaker para llamadas externas si se integran más servicios.
- Compilar con `-parameters` si se necesita inspección de nombres de parámetros en tiempo de ejecución.

---

Si quieres, puedo:

- Añadir este README también en `shipping-service` con ajustes de dominio.
- Generar JSON Schema para los eventos.
- Añadir un archivo `CONTRACTS.md` que liste los eventos y sus campos con ejemplos.

*** Fin del documento ***
