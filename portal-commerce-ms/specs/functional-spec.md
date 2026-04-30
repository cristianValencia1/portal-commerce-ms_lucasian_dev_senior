
# Functional Specification

## 1. Descripción general

El sistema implementa una arquitectura basada en microservicios para gestionar el flujo de ventas y envíos de un portal de compras.

La solución permite:

- registrar ventas;
- validar datos de cliente y productos;
- generar eventos de integración;
- procesar envíos de forma asíncrona;
- consultar ventas, envíos y trazabilidad;
- garantizar idempotencia y resiliencia.

---

## 2. Actores

- Cliente (consumidor de API REST)
- Sales Service
- Shipping Service

---

## 3. Casos de uso

### UC-01: Crear venta

**Entrada:**
- datos del cliente
- lista de productos

**Validaciones:**
- email válido
- teléfono válido
- código postal válido
- productos no vacíos
- cantidades > 0

**Resultado:**
- venta registrada
- trazabilidad generada
- evento `SALE_CREATED` generado

---

### UC-02: Procesar envío

**Disparador:**
- evento `SALE_CREATED`

**Resultado:**
- envío creado
- trazabilidad registrada
- control de duplicados

---

### UC-03: Consultar venta

**Entrada:**
- saleId

**Salida:**
- información de la venta

---

### UC-04: Consultar envío

**Entrada:**
- saleId

**Salida:**
- información del envío

---

### UC-05: Consultar trazabilidad

**Entrada:**
- saleId

**Salida:**
- historial del proceso

---

## 4. Reglas de negocio

- No se permite crear una venta sin productos válidos.
- No se puede generar un envío sin una venta registrada.
- No se permite reprocesar un evento ya procesado.
- Toda operación debe registrar trazabilidad.
- Cada evento debe tener un identificador único (`eventId`).

---

## 5. Flujo funcional

```text
Cliente → Sales Service → DB → Outbox → Evento → Shipping Service → DB