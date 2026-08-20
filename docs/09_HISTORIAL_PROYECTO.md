# SOFP — Historial del proyecto

Este documento conserva una línea temporal resumida de decisiones, avances y puntos de continuidad.

## 2026-08-20

### Build 044 — Ampliación de cobertura de Movimiento — Cerrado

- Se amplió `MovimientoTest` sin modificar código de producción.
- Se incorporaron **23 tests nuevos**.
- `MovimientoTest` pasó de **4 a 27 tests en verde**.
- Se cubrieron validaciones del constructor y operaciones de modificación de `Movimiento`.
- La ejecución específica quedó en **27/27 tests en verde**.
- La suite general posterior quedó en **282/282 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.
- `BUILD SUCCESS`.
- La ejecución general finalizó a las **13:19:27 -03:00** y tuvo una duración de **07:21 min**.
- Commit: `6f53f79` — `test: ampliar cobertura de Movimiento`.
- El commit fue publicado en `main` de GitHub y Bitbucket mediante `git pushall`.
- **Build 044 queda cerrado y validado.**

### Build 043 — Ampliación de cobertura de MovimientoService

- Se amplió `MovimientoServiceTest` sin modificar código de producción.
- `MovimientoServiceTest` pasó de **37 a 50 tests en verde**.
- La batería general quedó en **259/259 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.
- `BUILD SUCCESS`.
- La ejecución general finalizó a las 19:51:15 -03:00.
- Commit: `b6384f0` — `test: ampliar cobertura de MovimientoService`.

### Build 042 — Ampliación de cobertura de CuentaService

- Se amplió `CuentaServiceTest` sin modificar código de producción.
- `CuentaServiceTest` pasó de **40 a 47 tests en verde**.
- La batería general quedó en **246/246 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.
- `BUILD SUCCESS`.
- La ejecución general finalizó a las 16:46:27 -03:00.
- Commit: `526b378` — `test: ampliar cobertura de CuentaService`.

### Builds 041–034

Los Builds 041 a 034 ampliaron progresivamente las validaciones y la cobertura de `InstitucionFinanciera`, `MonedaService`, `PerfilFinancieroService`, `UsuarioService`, `CategoriaService`, `CuentaService` y `MovimientoService`. Los resultados completos permanecen registrados en `docs/06_BUILDS.md` y `docs/07_TESTS.md`.

## Estado actual

El último bloque cerrado es el **Build 044**.

La batería general actual es de **282/282 tests en verde**.

El último commit de código es `6f53f79` — `test: ampliar cobertura de Movimiento`.

La documentación de continuidad se mantiene en la rama `docs/continuidad-sofp`.

## Builds recientes

- Build 029 — Eliminación en `CategoriaRepository`.
- Build 030 — Eliminación en `CategoriaService`.
- Build 031 — Eliminación en `CuentaRepository`.
- Build 032 — Eliminación en `CuentaService`.
- Build 033 — Reglas de negocio de `Movimiento`.
- Build 034 — Ampliación de cobertura de `MovimientoServiceTest`.
- Build 035 — Ampliación de cobertura de `CuentaServiceTest`.
- Build 036 — Ampliación de cobertura de `InstitucionFinancieraServiceTest`.
- Build 037 — Ampliación de cobertura de `MonedaServiceTest`.
- Build 038 — Ampliación de cobertura de `PerfilFinancieroServiceTest`.
- Build 039 — Ampliación de cobertura de `UsuarioServiceTest`.
- Build 040 — Ampliación de cobertura de `CategoriaServiceTest`.
- Build 041 — Reforzamiento de validaciones de servicios y dominio.
- Build 042 — Ampliación de cobertura de `CuentaServiceTest`.
- Build 043 — Ampliación de cobertura de `MovimientoServiceTest`.
- Build 044 — Ampliación de cobertura de `MovimientoTest` — cerrado con 282/282 tests en verde.

## Regla histórica

Los hechos importantes del proyecto deben registrarse con Build y/o commit cuando exista. Las conversaciones pueden aportar contexto narrativo, pero no sustituyen este historial.
