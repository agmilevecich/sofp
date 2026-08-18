# SOFP — Historial del proyecto

Este documento conserva una línea temporal resumida de decisiones, avances y puntos de continuidad.

## 2026-08-18

### Build 036 — Ampliación de cobertura de InstitucionFinancieraService

- Se amplió `InstitucionFinancieraServiceTest` con **15 tests nuevos**.
- Se cubrieron institución nula al guardar, IDs nulos, búsquedas inexistentes y operaciones sobre instituciones inexistentes.
- `InstitucionFinancieraServiceTest` quedó en **23/23 tests en verde**.
- La batería general quedó en **201/201 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.
- `BUILD SUCCESS`.
- `git diff --check` sin errores de whitespace.
- Las líneas en blanco accidentales de `InstitucionFinancieraService.java` fueron descartadas antes del commit.
- Commit: `bd7f4bd` — `test: ampliar cobertura de InstitucionFinancieraService`.
- El commit de código fue publicado en `main` de GitHub y Bitbucket.

### Build 035 — Ampliación de cobertura de CuentaService

- Se amplió `CuentaServiceTest` con **20 tests nuevos**.
- Se cubrieron IDs nulos en cálculo de saldo, búsqueda, listado por perfil y operaciones de modificación/activación/desactivación/eliminación.
- Se cubrieron parámetros nulos y cuentas inexistentes en las operaciones de modificación.
- `CuentaServiceTest` quedó en **40/40 tests en verde**.
- La batería general quedó en **186/186 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.
- `BUILD SUCCESS`.
- `git diff --check` sin errores de whitespace.
- Commit: `57b8ad5` — `test: ampliar cobertura de CuentaService`.
- El commit de código fue publicado en `main` de GitHub y Bitbucket.

### Build 034 — Ampliación de cobertura de MovimientoService

- Se amplió `MovimientoServiceTest` con **17 tests nuevos**.
- Se cubrieron IDs nulos en búsqueda, listados, modificaciones y eliminación.
- Se cubrieron parámetros nulos de descripción, categoría, tipo, importe y fecha/hora.
- Se cubrieron búsquedas de movimientos inexistentes y operaciones sobre movimientos inexistentes.
- `MovimientoServiceTest` quedó en **32/32 tests en verde**.
- La batería general quedó en **163/163 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.
- `BUILD SUCCESS`.
- `git diff --check` sin errores.
- Commit: `4d9dc2a` — `test: ampliar cobertura de MovimientoService`.

## Estado actual

El último bloque confirmado es el **Build 036**. El código de producción no fue modificado en este Build; se amplió la cobertura de pruebas de `InstitucionFinancieraService`.

La batería general actual es de **201/201 tests en verde**.

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

## Regla histórica

Los hechos importantes del proyecto deben registrarse con Build y/o commit cuando exista. Las conversaciones pueden aportar contexto narrativo, pero no sustituyen este historial.
