-- 01-init-schemas.sql
-- Inicializacion base de extensiones y schemas

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE SCHEMA IF NOT EXISTS sales;
CREATE SCHEMA IF NOT EXISTS shipping;

