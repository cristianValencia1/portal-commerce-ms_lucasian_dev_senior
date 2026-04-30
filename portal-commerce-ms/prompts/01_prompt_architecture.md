# Prompt de Arquitectura — Portal Commerce MS

## Rol

Actúa como arquitecto de software senior especializado en microservicios, Java Spring Boot, arquitectura hexagonal, PostgreSQL, RabbitMQ, resiliencia, trazabilidad e integración asíncrona.

## Objetivo

Diseña la arquitectura completa del sistema `portal-commerce-ms`, compuesto por:

- `sales-service`
- `shipping-service`
- PostgreSQL
- RabbitMQ
- Docker Compose
- documentación técnica
- propuesta futura de despliegue en AWS

La arquitectura debe estar alineada con un reto técnico senior donde se evalúan mantenibilidad, resiliencia, trazabilidad, escalabilidad, separación de responsabilidades e integración asíncrona.

---

## Contexto funcional

El sistema gestiona el flujo de ventas y envíos de un portal de compras.

### Sales Service

Responsable de:

- crear ventas;
- validar datos del cliente;
- validar productos;
- persistir la venta;
- registrar trazabilidad;
- crear evento outbox;
- publicar evento `SALE_CREATED`.

### Shipping Service

Responsable de:

- consumir evento `SALE_CREATED`;
- crear envío asociado;
- registrar trazabilidad;
- evitar duplicados;
- exponer consultas de envío.

---

## Arquitectura objetivo

Propón una arquitectura basada en:

- microservicios;
- arquitectura hexagonal;
- integración asíncrona;
- PostgreSQL con schemas separados;
- RabbitMQ;
- Outbox Pattern;
- idempotencia;
- trazabilidad con `correlationId`.

Incluye un diagrama lógico en texto usando este estilo:

```text
Cliente/API Consumer
        |
        v
sales-service
        |
        | Persistencia + Outbox
        v
PostgreSQL schema sales
        |
        | Evento SALE_CREATED
        v
RabbitMQ
        |
        v
shipping-service
        |
        v
PostgreSQL schema shipping
```

---

## Decisiones que debes justificar

Explica y justifica:

1. Por qué usar microservicios.
2. Por qué usar arquitectura hexagonal.
3. Por qué usar comunicación asíncrona.
4. Por qué usar Outbox Pattern.
5. Por qué usar idempotencia en consumidores.
6. Por qué usar PostgreSQL.
7. Por qué usar RabbitMQ.
8. Cómo se maneja la trazabilidad.
9. Cómo se manejan fallos parciales.
10. Cómo escalar la solución en AWS.

---

## Entregables esperados

Genera:

1. descripción de arquitectura;
2. responsabilidades por microservicio;
3. separación por capas;
4. flujo funcional;
5. flujo técnico;
6. estrategia de persistencia;
7. estrategia de eventos;
8. estrategia de trazabilidad;
9. estrategia de resiliencia;
10. trade-offs;
11. riesgos y mitigaciones;
12. recomendaciones de evolución.

---

## Restricciones

- No usar MySQL.
- No proponer monolito.
- No omitir Outbox.
- No omitir idempotencia.
- No omitir trazabilidad.
- No entregar respuestas superficiales.
- No mezclar lógica de negocio con infraestructura.

---

## Instrucción final

Genera una arquitectura técnica completa, clara y defendible para una sustentación ante arquitectos de software, explicando decisiones, trade-offs, riesgos y evolución futura.
