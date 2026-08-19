# SOFP — Historial del proyecto

Este documento conserva una línea temporal resumida de decisiones, avances y puntos de continuidad.

## 2026-08-19

### Build 040 — Ampliación de cobertura de CategoriaService

- Se amplió `CategoriaServiceTest`, pasando de **12 a 21 tests en verde**.
- Se incorporaron **9 tests nuevos** para cubrir validaciones de parámetros nulos en registro, búsqueda, listado por perfil, modificación de nombre y descripción, activación, desactivación y eliminación.
- El test de nombre nulo utiliza una categoría existente para respetar el orden real de validación de `CategoriaService.modificarNombre(...)`.
- No se modificó código de producción.
- La batería general quedó en **236/236 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.
- `BUILD SUCCESS`.
- La ejecución general finalizó a las 11:28:34 -03:00.
- `git diff --check` sin errores de whitespace.
- Commit: `9be5972` — `test: ampliar cobertura de CategoriaService`.
- El commit fue publicado en `main` de GitHub y Bitbucket mediante `git pushall`.

## 2026-08-18

### Build 039 — Ampliación de cobertura de UsuarioService

- Se amplió `UsuarioServiceTest`, pasando de **5 a 15 tests en verde**.
- Se incorporaron **10 tests nuevos**, cubriendo búsquedas inexistentes, listado vacío, parámetros nulos y operaciones de activación/desactivación sobre usuarios inexistentes.
- Se incorporó validación explícita de IDs nulos en `UsuarioService.activar(...)` y `UsuarioService.desactivar(...)`.
- La batería general quedó en **227/227 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.
- `BUILD SUCCESS`.
- La ejecución general finalizó a las 22:34:24 -03:00.
- `git diff --check` sin errores de whitespace.
- Commit: `0e27dfe` — `test: ampliar cobertura de UsuarioService`.
- El commit fue publicado en `main` de GitHub y Bitbucket mediante `git pushall`.

### Build 038 — Ampliación de cobertura de PerfilFinancieroService

- Se amplió `PerfilFinancieroServiceTest`, pasando de **6 a 13 tests en verde**.
- Se incorporaron **7 tests nuevos**, cubriendo validaciones de parámetros nulos, búsquedas inexistentes y operaciones sobre perfiles inexistentes.
- La batería general quedó en **217/217 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.
- `BUILD SUCCESS`.
- La ejecución general finalizó a las 19:27:39 -03:00.
- Commit: `e2d3268` — `test: ampliar cobertura de PerfilFinancieroService`.
- El commit fue publicado en `main` de GitHub y Bitbucket mediante `git pushall`.

### Build 037 — Ampliación de cobertura de MonedaService

- Se amplió `MonedaServiceTest`, pasando de **8 a 17 tests en verde**.
- Se revisaron `MonedaService`, `Moneda` y `MonedaRepository` para mantener la cobertura alineada con el comportamiento real del servicio.
- La batería general quedó en **210/210 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.
- `BUILD SUCCESS`.
- La ejecución general finalizó a las 18:52:19 -03:00.

### Build 036 — Ampliación de cobertura de InstitucionFinancieraService

- Se amplió `InstitucionFinancieraServiceTest` con **15 tests nuevos**.
- Se cubrieron institución nula al guardar, IDs nulos, búsquedas inexistentes y operaciones sobre instituciones inexistentes.
- `InstitucionFinancieraServiceTest` quedó en **23/23 tests en verde**.
- La batería general quedó en **201/201 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.
- `BUILD SUCCESS`.
- `git diff --check` sin errores de whitespace.
- Commit: `bd7f4bd` — `test: ampliar cobertura de InstitucionFinancieraService`.

### Build 035 — Ampliación de cobertura de CuentaService

- Se amplió `CuentaServiceTest` con **20 tests nuevos**.
- Se cubrieron IDs nulos en cálculo de saldo, búsqueda, listado por perfil y operaciones de modificación/activación/desactivación/eliminación.
- `CuentaServiceTest` quedó en **40/40 tests en verde**.
- La batería general quedó en **186/186 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.
- `BUILD SUCCESS`.
- Commit: `57b8ad5` — `test: ampliar cobertura de CuentaService`.

### Build 034 — Ampliación de cobertura de MovimientoService

- Se amplió `MovimientoServiceTest` con **17 tests nuevos**.
- Se cubrieron IDs nulos en búsqueda, listados, modificaciones y eliminación.
- Se cubrieron parámetros nulos de descripción, categoría, tipo, importe y fecha/hora.
- Se cubrieron búsquedas y operaciones sobre movimientos inexistentes.
- `MovimientoServiceTest` quedó en **32/32 tests en verde**.
- La batería general quedó en **163/163 tests en verde**.
- Commit: `4d9dc2a` — `test: ampliar cobertura de MovimientoService`.

## Estado actual

El último bloque confirmado es el **Build 040**. Se amplió la cobertura de pruebas de `CategoriaService` y la batería general quedó en **236/236 tests en verde**.

El último commit de código confirmado es `9be5972` — `test: ampliar cobertura de CategoriaService`.

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

## Regla histórica

Los hechos importantes del proyecto deben registrarse con Build y/o commit cuando exista. Las conversaciones pueden aportar contexto narrativo, pero no sustituyen este historial.
