# SOFP — Historial del proyecto

Este documento conserva una línea temporal resumida de decisiones, avances y puntos de continuidad.

## 2026-08-05

- Se trabajó sobre el diseño de `Cuenta`.
- Se estableció el enfoque de desarrollo incremental por Builds.

## 2026-08-06

- Evolución del dominio.
- Incorporación de `InstitucionFinanciera`.
- Consolidación de pruebas y estructura del dominio.

## 2026-08-07

- Reorganización de documentación técnica.
- Implementación de `Cuenta` en el dominio.
- Incorporación de `Categoria`.
- Implementación de `Movimiento`.
- Incorporación de `TipoMovimiento`.
- Validación de importes positivos.
- Tests unitarios y JPA para `Movimiento`.

## 2026-08-09

### Build 011 — Aislamiento y estabilización de tests JPA con H2

- Se separó la infraestructura de pruebas JPA de la persistencia de producción mediante `JpaTestManager`.
- Los tests utilizan H2 en memoria con `create-drop`.
- Se aisló la información utilizada por los tests para evitar conflictos entre ejecuciones.
- Se resolvió la violación de unicidad sobre `USUARIOS.EMAIL` que aparecía al ejecutar la batería general.
- Se incorporó el cierre de `EntityManager` y `JpaTestManager` en los tests JPA correspondientes.
- Se verificaron los tests JPA individualmente.
- Se ejecutó la batería general del proyecto y todos los tests terminaron en verde.
- El commit de código de Build 011 todavía está pendiente de registrarse en `main`.

## Estado al establecer el sistema de continuidad

El repositorio queda preparado para conservar contexto de largo plazo mediante documentación versionada en `docs/`.

## Regla histórica

Los hechos importantes del proyecto deben registrarse con fecha, Build y/o commit. Las conversaciones pueden aportar contexto narrativo, pero no sustituyen este historial.
