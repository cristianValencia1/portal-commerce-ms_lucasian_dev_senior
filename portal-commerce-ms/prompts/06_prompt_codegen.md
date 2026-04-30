# Prompt de Codegen — Portal Commerce MS

## Rol

Actúa como desarrollador backend senior Java/Spring Boot con experiencia en microservicios, arquitectura hexagonal, PostgreSQL, RabbitMQ, Docker y pruebas automatizadas.

## Objetivo

Genera código fuente real, compilable y organizado para el proyecto `portal-commerce-ms`, respetando la arquitectura definida para:

- `sales-service`
- `shipping-service`
- PostgreSQL
- RabbitMQ
- Docker Compose

El código debe ser limpio, mantenible y listo para ejecutar localmente.

---

## Stack obligatorio

Usa:

- Java 17
- Spring Boot 3.x
- Maven
- Spring Web
- Spring Data JPA
- Spring Validation
- PostgreSQL Driver
- Spring AMQP
- Lombok
- springdoc-openapi
- JUnit 5
- Mockito

---

## Reglas generales de generación

1. Genera archivos completos.
2. No entregues pseudocódigo.
3. No mezcles capas.
4. No pongas lógica de negocio en controladores ni listeners.
5. El dominio no debe depender de JPA, Spring ni RabbitMQ.
6. Usa puertos de entrada y salida.
7. Implementa adapters para persistencia, web y mensajería.
8. Implementa manejo de errores.
9. Implementa validaciones.
10. Implementa logs y trazabilidad.

---

## Estructura por microservicio

Cada servicio debe respetar esta estructura:

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

## Sales Service

Genera código para:

- crear venta;
- validar cliente y productos;
- calcular total;
- persistir venta e items;
- registrar trazabilidad;
- crear evento outbox;
- publicar evento pendiente;
- consultar venta;
- consultar trazabilidad.

Endpoints:

```text
POST /api/v1/sales
GET /api/v1/sales/{saleId}
GET /api/v1/sales/{saleId}/trace
```

---

## Shipping Service

Genera código para:

- consumir `SALE_CREATED`;
- validar duplicados;
- crear envío;
- registrar trazabilidad;
- guardar evento procesado;
- consultar envío por venta;
- consultar trazabilidad.

Endpoints:

```text
GET /api/v1/shipments/sale/{saleId}
GET /api/v1/shipments/sale/{saleId}/trace
```

---

## Persistencia

Usa PostgreSQL:

- schema `sales` para `sales-service`;
- schema `shipping` para `shipping-service`.

No uses `ddl-auto=create`. Asume scripts SQL existentes.

---

## RabbitMQ

Usa:

```text
exchange: commerce.exchange
queue: shipping.sale-created.queue
routing key: sales.created
```

---

## Pruebas

Genera pruebas unitarias mínimas para:

- creación de venta válida;
- venta sin productos;
- cálculo de total;
- creación de evento outbox;
- creación de envío;
- evento duplicado;
- validación de idempotencia.

---

## Salida esperada

Devuelve:

1. árbol de carpetas;
2. `pom.xml`;
3. `application.yml`;
4. clases de dominio;
5. DTOs;
6. puertos;
7. casos de uso;
8. adapters;
9. entidades JPA;
10. repositorios;
11. controladores;
12. listener RabbitMQ;
13. configuración RabbitMQ;
14. exception handlers;
15. pruebas;
16. Dockerfiles.

---

## Restricciones

- No usar MySQL.
- No generar código incompleto.
- No omitir idempotencia.
- No omitir outbox.
- No omitir trazabilidad.
- No omitir validaciones.
- No omitir pruebas.

---

## Instrucción final

Genera código completo, compilable y organizado para el proyecto `portal-commerce-ms`, preparado para ejecución local con Docker Compose y sustentación técnica senior.
