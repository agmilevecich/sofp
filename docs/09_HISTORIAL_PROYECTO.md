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

### Build 014 — Repository JPA de Movimiento

- Se incorporó `MovimientoRepository`.
- Se incorporó `MovimientoRepositoryTest`.
- Se verificaron guardado, actualización, búsqueda por ID, listado general, listado por cuenta y listado por categoría.
- La batería general quedó en **64 tests en verde**.
- Commit: `4f0b20f` — `Build 014 - Implementación de MovimientoRepository`.

## 2026-08-11

### Build 015 — Servicio de saldo de cuentas

- Se inició la capa `service`.
- Se incorporó `CuentaService`, utilizando `MovimientoRepository` para calcular el saldo de una cuenta.
- Se definieron las reglas `INGRESO` suma y `EGRESO` resta.
- Se verificó el caso de cuenta sin movimientos.
- Se verificó el cálculo con un ingreso.
- Se verificó el cálculo con un egreso.
- Se verificó el cálculo con múltiples movimientos.
- Se incorporó `CuentaServiceTest` con 4 casos.
- Se detectó y resolvió el aislamiento de la base H2 entre tests cerrando `JpaTestManager` en el `tearDown()`.
- La batería general quedó en **68 tests en verde**.
- Commit de código: `4697815` — `feat: implementar servicio de saldo de cuentas`.
- El commit de código fue publicado en `main` de GitHub y Bitbucket.

## 2026-08-12

### Build 016 — Servicio de movimientos

- Se amplió la capa `service` con `MovimientoService`.
- Se incorporó `MovimientoServiceTest` con 6 casos.
- Se implementó el registro de movimientos con transacción explícita.
- Se agregó `flush()` antes del `commit` para asegurar la sincronización de la persistencia.
- Se agregó `rollback()` ante excepciones cuando la transacción permanece activa.
- Se implementaron búsquedas y listados por ID, cuenta y categoría, además del listado general.
- Se resolvieron incidencias de entidades transitorias, aislamiento de H2 y ausencia de transacción activa.
- Los 6 tests de `MovimientoServiceTest` terminaron en verde.
- La batería general quedó en **74 tests en verde**.
- Commit de código: `8f8594e` — `feat: implementar servicio de movimientos`.
- El commit de código fue publicado en `main` de GitHub y Bitbucket.

## Estado al establecer el sistema de continuidad

El repositorio queda preparado para conservar contexto de largo plazo mediante documentación versionada en `docs/`.

## Regla histórica

Los hechos importantes del proyecto deben registrarse con fecha, Build y/o commit. Las conversaciones pueden aportar contexto narrativo, pero no sustituyen este historial.
