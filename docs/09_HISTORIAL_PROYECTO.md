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

### Build 017 — Repository JPA de Categoria

- Se incorporó `CategoriaRepository`.
- Se incorporó `CategoriaRepositoryTest`.
- Se verificaron las operaciones principales del repositorio y todos los casos del test terminaron en verde.
- El nuevo bloque quedó validado sin incidencias pendientes.
- Commit de código: `f462b3b` — `feat: implementar CategoriaRepository`.
- El commit de código fue publicado en `main` de GitHub y Bitbucket.

### Build 018 — Servicio de Categoria

- Se amplió la capa `service` con `CategoriaService`.
- Se incorporó `CategoriaServiceTest` con 4 casos.
- Se implementaron el registro de categorías, búsqueda por ID, listado general y listado por perfil financiero.
- El servicio utiliza `CategoriaRepository` como frontera de persistencia.
- Los 4 tests de `CategoriaServiceTest` terminaron en verde.
- Se ejecutó la batería general del proyecto y los **82/82 tests terminaron en verde**.
- No quedaron incidencias pendientes para este bloque.
- Commit de código: `d57e0b4` — `feat: implementar CategoriaService`.
- El commit de código fue publicado en `main` de GitHub y Bitbucket.

### Build 019 — Servicio de PerfilFinanciero

- Se amplió la capa `service` con `PerfilFinancieroService`.
- Se incorporó `PerfilFinancieroServiceTest` con 6 casos.
- Se implementaron guardado, búsqueda por ID, listado general, listado por usuario, cambio de descripción, activación y desactivación.
- El servicio utiliza `PerfilFinancieroRepository` como frontera de persistencia.
- Los 6 tests de `PerfilFinancieroServiceTest` terminaron en verde.
- Se ejecutó la batería general del proyecto y los **88/88 tests terminaron en verde**.
- No quedaron incidencias pendientes para este bloque.
- Commit de código: `1cc00ca` — `feat: implementar PerfilFinancieroService`.
- El commit de código fue publicado en `main` de GitHub y Bitbucket.

### Build 020 — Servicio de Usuario

- Se amplió la capa `service` con `UsuarioService`.
- Se incorporó `UsuarioServiceTest` con 5 casos.
- Se implementaron guardado y búsqueda por ID, búsqueda por email, listado, activación y desactivación.
- La batería general quedó en **93/93 tests en verde**.

### Build 021 — Servicio de InstitucionFinanciera

- Se amplió la capa `service` con `InstitucionFinancieraService`.
- Se incorporó `InstitucionFinancieraServiceTest` con 8 casos.
- Se implementaron búsqueda, listado, renombrado, actualización de sitio web y descripción, activación y desactivación.
- La batería general quedó en **101/101 tests en verde**.

### Build 022 — Servicio de Moneda

- Se amplió la capa `service` con `MonedaService`.
- Se incorporó `MonedaServiceTest` con 8 casos.
- Se implementaron búsqueda, listado y modificación de nombre y decimales, incluyendo validaciones para monedas inexistentes.
- Se resolvió el aislamiento de H2 mediante el cierre de `JpaTestManager` en el `tearDown()` correspondiente.
- La batería general quedó en **109/109 tests en verde**.

### Build 023 — Ampliación de CuentaService

- Se amplió `CuentaService` para cubrir operaciones de gestión de `Cuenta`, además del cálculo de saldo.
- Se amplió `CuentaServiceTest` hasta 8 casos.
- Se verificaron registrar, buscar, listar y listar por perfil, junto con el cálculo de saldo.
- La batería general quedó en **113/113 tests en verde**.
- Commit de código: `ea595d4` — `feat: ampliar CuentaService`.
- El commit fue publicado en `main` de GitHub y Bitbucket.

### Build 024 — Ampliación inicial de MovimientoService

- Se amplió `MovimientoService` para modificar descripción, observaciones y categoría.
- Se agregaron validaciones de IDs y entidades obligatorias.
- Se incorporó el manejo de movimiento inexistente mediante `IllegalArgumentException`.
- `MovimientoServiceTest` quedó ampliado a 11 casos.
- La batería general quedó en **118/118 tests en verde**.
- Commit de código: `110f7d7` — `feat: ampliar MovimientoService`.
- El commit fue publicado en `main` de GitHub y Bitbucket.

### Build 025 — Ampliación de Movimiento y finalización de nuevas operaciones de MovimientoService

- Se amplió la entidad `Movimiento` con:
  - `cambiarTipoMovimiento(TipoMovimiento tipoMovimiento)`.
  - `cambiarImporte(BigDecimal importe)`.
  - `cambiarFechaHora(LocalDateTime fechaHora)`.
- Se completó `MovimientoService` con:
  - `modificarTipoMovimiento(Long movimientoId, TipoMovimiento tipoMovimiento)`.
  - `modificarImporte(Long movimientoId, BigDecimal importe)`.
  - `modificarFechaHora(Long movimientoId, LocalDateTime fechaHora)`.
- Las nuevas operaciones mantienen el patrón transaccional de `MovimientoService`: validación, búsqueda, `begin`, modificación, `flush`, `commit` y `rollback` ante errores.
- Se agregaron 3 nuevos tests a `MovimientoServiceTest`.
- La batería general del proyecto quedó en **118/118 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.
- Commit de dominio: `da3b89d` — `feat: ampliar operaciones de Movimiento`.
- Commit de servicio y tests: `81883ea` — `feat: completar operaciones de MovimientoService`.
- Ambos commits fueron publicados en `main` de GitHub y Bitbucket.

## Estado al establecer el sistema de continuidad

El repositorio queda preparado para conservar contexto de largo plazo mediante documentación versionada en `docs/`.

## Regla histórica

Los hechos importantes del proyecto deben registrarse con fecha, Build y/o commit. Las conversaciones pueden aportar contexto narrativo, pero no sustituyen este historial.
