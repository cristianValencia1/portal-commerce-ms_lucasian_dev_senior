# Prompt para Configuración General del Sistema (Microservicios)

## Objetivo

Genera la configuración completa del ecosistema de microservicios compuesto por:

- sales-service
- shipping-service
- PostgreSQL
- RabbitMQ

Todo debe ser orquestado mediante Docker Compose, incluyendo redes, variables de entorno, dependencias y configuración base para ejecución local.

---

## Contexto

El sistema implementa:

- Arquitectura de microservicios
- Spring Boot
- PostgreSQL con schemas separados (sales, shipping)
- RabbitMQ para comunicación asíncrona
- Patrón Outbox
- Idempotencia en procesamiento de eventos

---

## Requerimientos

Genera los siguientes archivos completos y funcionales:

### 1. docker-compose.yml

Debe incluir:

- servicio postgres-commerce
- servicio rabbitmq
- servicio sales-service
- servicio shipping-service

### PostgreSQL

- imagen: postgres:15.7
- base: commerce_platform
- usuario: postgres
- password: postgres
- volumen persistente
- scripts en /docker-entrypoint-initdb.d

---

### RabbitMQ

- imagen: rabbitmq:3-management
- puertos:
  - 5672
  - 15672

---

### sales-service

- puerto 8081
- variables:
  - DB_URL
  - DB_USER
  - DB_PASSWORD
  - RABBIT_HOST

---

### shipping-service

- puerto 8082
- mismas variables que sales-service

---

### 2. application.yml base

Debe incluir:

- configuración PostgreSQL
- configuración RabbitMQ
- logs
- OpenAPI

---

### 3. RabbitMQ

- exchange: commerce.exchange
- routing key: sales.created
- queue: shipping.sale-created.queue

---

### 4. Red Docker

- red compartida

---

### 5. Healthchecks

- postgres
- rabbitmq
- servicios

---

### 6. .env

Debe contener:

- credenciales
- puertos
- nombres

---

## Salida esperada

1. docker-compose.yml
2. application.yml
3. .env
4. instrucciones de uso
5. recomendaciones

---

## Restricciones

- No Kubernetes
- No pseudocódigo

---

## Instrucción final

Genera toda la configuración para levantar el ecosistema completo con Docker Compose listo para desarrollo local.
