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

### Build 014 — Repository JPA de Movimiento

- Se incorporó `MovimientoRepository` y `MovimientoRepositoryTest`.
- Se verificaron guardado, actualización, búsqueda por ID, listado general, listado por cuenta y listado por categoría.
- La batería general quedó en **64 tests en verde**.
- Commit: `4f0b20f` — `Build 014 - Implementación de MovimientoRepository`.

## 2026-08-11

### Build 015 — Servicio de saldo de cuentas

- Se incorporó `CuentaService` para calcular el saldo a partir de movimientos.
- `INGRESO` suma y `EGRESO` resta.
- Se verificaron cuenta sin movimientos, ingresos, egresos y múltiples movimientos.
- Se incorporó `CuentaServiceTest`.
- La batería general quedó en **68 tests en verde**.
- Commit: `4697815` — `feat: implementar servicio de saldo de cuentas`.

## 2026-08-12

### Build 016 — Servicio de movimientos

- Se incorporó `MovimientoService` y `MovimientoServiceTest`.
- Se implementó el registro de movimientos con transacción explícita, `flush()`, `commit()` y `rollback()`.
- Se implementaron búsquedas y listados por ID, cuenta y categoría.
- La batería general quedó en **74 tests en verde**.
- Commit: `8f8594e` — `feat: implementar servicio de movimientos`.

### Build 017 — Repository JPA de Categoria

- Se incorporó `CategoriaRepository` y `CategoriaRepositoryTest`.
- Commit: `f462b3b` — `feat: implementar CategoriaRepository`.

### Build 018 — Servicio de Categoria

- Se incorporó `CategoriaService` y `CategoriaServiceTest`.
- La batería general quedó en **82/82 tests en verde**.
- Commit: `d57e0b4` — `feat: implementar CategoriaService`.

### Build 019 — Servicio de PerfilFinanciero

- Se incorporó `PerfilFinancieroService` y `PerfilFinancieroServiceTest`.
- La batería general quedó en **88/88 tests en verde**.
- Commit: `1cc00ca` — `feat: implementar PerfilFinancieroService`.

### Build 020 — Servicio de Usuario

- Se incorporó `UsuarioService` y `UsuarioServiceTest`.
- La batería general quedó en **93/93 tests en verde**.
- Commit: `87786fe` — `feat: implementar UsuarioService`.

### Build 021 — Servicio de InstitucionFinanciera

- Se incorporó `InstitucionFinancieraService` y `InstitucionFinancieraServiceTest`.
- La batería general quedó en **101/101 tests en verde**.
- Commit: `20e21c3` — `feat: implementar InstitucionFinancieraService`.

### Build 022 — Servicio de Moneda

- Se incorporó `MonedaService` y `MonedaServiceTest`.
- Se corrigió el aislamiento de H2 mediante `JpaTestManager.close()`.
- La batería general quedó en **109/109 tests en verde**.
- Commit: `0d0db87` — `feat: implementar MonedaService`.

### Build 023 — Ampliación de CuentaService

- Se amplió `CuentaService` para registrar, buscar, listar y listar por perfil, además del cálculo de saldo.
- La batería general quedó en **113/113 tests en verde**.
- Commit: `ea595d4` — `feat: ampliar CuentaService`.

### Build 024 — Ampliación inicial de MovimientoService

- Se ampliaron las operaciones de modificación de descripción, observaciones y categoría.
- Se reforzaron las validaciones de IDs y existencia.
- La batería general quedó en **118/118 tests en verde**.
- Commit: `110f7d7` — `feat: ampliar MovimientoService`.

### Build 025 — Ampliación de Movimiento y MovimientoService

- Se ampliaron las operaciones de modificación de tipo, importe y fecha/hora.
- Se agregaron 3 nuevos tests.
- La batería general quedó en **121/121 tests en verde**.
- Commits: `da3b89d` y `81883ea`.

### Build 026 — Eliminación de Movimiento

- Se incorporó la eliminación en `MovimientoRepository` y `MovimientoService`.
- Se validaron ID y existencia del movimiento y se mantuvo el patrón transaccional explícito.
- La batería general quedó en **121/121 tests en verde** al cerrar el Build.
- Commit: `d386d02` — `feat: completar operaciones de Movimiento`.

### Build 027 — Ampliación de CuentaService

- Se ampliaron las operaciones de `CuentaService` para modificar nombre, identificador externo, tipo de cuenta, institución financiera y moneda.
- Se incorporaron las operaciones `activar(Long cuentaId)` y `desactivar(Long cuentaId)`.
- `CuentaService` pasó a recibir `EntityManager` por constructor para gestionar explícitamente estas transacciones.
- Se incorporó `obtenerCuenta(...)` para centralizar la validación de cuenta inexistente.
- `CuentaServiceTest` incorporó siete nuevos casos.
- La batería general quedó en **128/128 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0` al cerrar el Build.
- Commit: `00d862c` — `feat: ampliar CuentaService`.

### Cobertura posterior al Build 027 — Eliminación de MovimientoService

- Se agregó un caso específico en `MovimientoServiceTest` para cubrir la eliminación de un movimiento mediante `MovimientoService.eliminar(...)`.
- El test verifica que, luego de eliminar el movimiento, `buscarPorId(...)` no lo encuentre.
- `MovimientoServiceTest` pasó de 15 a **16 tests**, todos en verde en la ejecución individual.
- La batería general pasó a **129/129 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.
- Commit: `3e93be2` — `test: cubrir eliminacion de MovimientoService`.

## Estado actual

El código de `main` está en el commit `3e93be2`. La batería general confirmada es de **129/129 tests en verde**. El próximo bloque funcional a definir es el **Build 028**.

La documentación de continuidad se actualiza en la rama `docs/continuidad-sofp`.

## Regla histórica

Los hechos importantes del proyecto deben registrarse con Build y/o commit cuando exista. Las conversaciones pueden aportar contexto narrativo, pero no sustituyen este historial.
