# Prompt Pack Curado — Portal Commerce MS

## Objetivo

Este paquete contiene la versión depurada y persistida de los prompts necesarios para generar, documentar, probar y sustentar el proyecto `portal-commerce-ms`.

## Prompts conservados

| Archivo | Propósito |
|---|---|
| `00_prompt_master_orchestrator.md` | Prompt principal para generar el proyecto completo end-to-end. |
| `01_prompt_architecture.md` | Prompt para sustentar y diseñar la arquitectura técnica. |
| `02_prompt_database_postgresql.md` | Prompt para generar Docker + PostgreSQL + schemas + tablas. |
| `03_prompt_configuracion_general.md` | Prompt para generar Docker Compose, `.env`, redes y configuración general. |
| `04_prompt_sales_service.md` | Prompt específico para construir `sales-service`. |
| `05_prompt_shipping_service.md` | Prompt específico para construir `shipping-service`. |
| `06_prompt_codegen.md` | Prompt especializado para generación de código real y compilable. |
| `07_prompt_testing.md` | Prompt para generar estrategia y casos de prueba. |

## Archivos descartados o fusionados

| Archivo original | Acción | Motivo |
|---|---|---|
| `prompt_docker_postgresql_bd.md` duplicado | Descartado | Mismo contenido que `(1)`. |
| `prompt_shipping_service_postgresql.md` duplicado | Descartado | Mismo contenido que `(1)`. |
| `prompt_configuracion_general.md` | Reemplazado por `(1)` | Se conserva versión con PostgreSQL 15.7, alineada al orquestador. |
| `prompt_master_orchestrator_portal_commerce.md` | Reemplazado por `(1)` | Se conserva versión más alineada al repo `db/init`. |
| `prompt-architecture.md` | Regenerado | Archivo original vacío. |
| `prompt-codegen.md` | Regenerado | Archivo original vacío. |

## Orden recomendado de uso

1. `01_prompt_architecture.md`
2. `02_prompt_database_postgresql.md`
3. `03_prompt_configuracion_general.md`
4. `04_prompt_sales_service.md`
5. `05_prompt_shipping_service.md`
6. `06_prompt_codegen.md`
7. `07_prompt_testing.md`
8. `00_prompt_master_orchestrator.md`

## Nota

El prompt maestro puede usarse solo, pero para mejores resultados conviene ejecutar primero los prompts especializados y luego usar el maestro para consolidar.
