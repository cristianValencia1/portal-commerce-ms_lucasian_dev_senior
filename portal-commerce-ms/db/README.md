# PostgreSQL Docker Init

Paquete base para levantar PostgreSQL con inicializacion automatica de schemas y tablas para los contextos `sales` y `shipping`.

## Estructura

- `Dockerfile`
- `sql/01-init-schemas.sql`
- `sql/02-sales-schema.sql`
- `sql/03-shipping-schema.sql`
- `sql/04-indexes.sql`

## Uso

```bash
docker compose up --build
```

