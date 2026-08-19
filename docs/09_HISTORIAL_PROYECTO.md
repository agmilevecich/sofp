# SOFP — Historial del proyecto

Este documento conserva una línea temporal resumida de decisiones, avances y puntos de continuidad.

## 2026-08-19

### Build 043 — Ampliación de cobertura de MovimientoService

- Se amplió `MovimientoServiceTest` sin modificar código de producción.
- `MovimientoServiceTest` pasó de **37 a 50 tests en verde**.
- Se incorporó cobertura adicional para operaciones y validaciones existentes.
- La batería general quedó en **259/259 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.
- `BUILD SUCCESS`.
- La ejecución general finalizó a las 19:51:15 -03:00.
- `git diff --check` sin errores de whitespace.
- Commit: `b6384f0` — `test: ampliar cobertura de MovimientoService`.
- El commit fue publicado en `main` de GitHub y Bitbucket mediante `git pushall`.

### Build 042 — Ampliación de cobertura de CuentaService

- Se amplió `CuentaServiceTest` sin modificar código de producción.
- `CuentaServiceTest` pasó de **40 a 47 tests en verde**.
- La batería general quedó en **246/246 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.
- `BUILD SUCCESS`.
- La ejecución general finalizó a las 16:46:27 -03:00.
- `git diff --check` sin errores de whitespace.
- Commit: `526b378` — `test: ampliar cobertura de CuentaService`.
- El commit fue publicado en `main` de GitHub y Bitbucket mediante `git pushall`.

### Build 041 — Reforzamiento de validaciones de servicios y dominio

- Se reforzaron validaciones de parámetros nulos en `InstitucionFinanciera`, `MonedaService` y `PerfilFinancieroService`.
- `InstitucionFinancieraServiceTest` pasó de **23 a 26 tests en verde**.
- Se incorporaron 3 tests nuevos para nombre nulo, sitio web nulo y descripción nula.
- La batería general quedó en **239/239 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.
- `BUILD SUCCESS`.
- La ejecución general finalizó a las 15:09:48 -03:00.
- Commit: `a9de29c` — `feat: reforzar validaciones de servicios y dominio`.
- El commit fue publicado en `main` de GitHub y Bitbucket mediante `git pushall`.

### Build 040 — Ampliación de cobertura de CategoriaService

- Se amplió `CategoriaServiceTest`, pasando de **12 a 21 tests en verde**.
- Se incorporaron **9 tests nuevos** para cubrir validaciones de parámetros nulos.
- La batería general quedó en **236/236 tests en verde**.
- `BUILD SUCCESS`.
- Commit: `9be5972` — `test: ampliar cobertura de CategoriaService`.

## 2026-08-18

### Build 039 — Ampliación de cobertura de UsuarioService

- Se amplió `UsuarioServiceTest`, pasando de **5 a 15 tests en verde**.
- Se incorporaron **10 tests nuevos** y validaciones explícitas de IDs nulos en `UsuarioService.activar(...)` y `desactivar(...)`.
- La batería general quedó en **227/227 tests en verde**.
- Commit: `0e27dfe` — `test: ampliar cobertura de UsuarioService`.

### Build 038 — Ampliación de cobertura de PerfilFinancieroService

- Se amplió `PerfilFinancieroServiceTest`, pasando de **6 a 13 tests en verde**.
- La batería general quedó en **217/217 tests en verde**.
- Commit: `e2d3268` — `test: ampliar cobertura de PerfilFinancieroService`.

### Build 037 — Ampliación de cobertura de MonedaService

- Se amplió `MonedaServiceTest`, pasando de **8 a 17 tests en verde**.
- La batería general quedó en **210/210 tests en verde**.

### Build 036 — Ampliación de cobertura de InstitucionFinancieraService

- Se amplió `InstitucionFinancieraServiceTest` con **15 tests nuevos**.
- `InstitucionFinancieraServiceTest` quedó en **23/23 tests en verde**.
- La batería general quedó en **201/201 tests en verde**.
- Commit: `bd7f4bd` — `test: ampliar cobertura de InstitucionFinancieraService`.

### Build 035 — Ampliación de cobertura de CuentaService

- Se amplió `CuentaServiceTest` con **20 tests nuevos**.
- `CuentaServiceTest` quedó en **40/40 tests en verde**.
- La batería general quedó en **186/186 tests en verde**.
- Commit: `57b8ad5` — `test: ampliar cobertura de CuentaService`.

### Build 034 — Ampliación de cobertura de MovimientoService

- Se amplió `MovimientoServiceTest` con **17 tests nuevos**.
- `MovimientoServiceTest` quedó en **32/32 tests en verde**.
- La batería general quedó en **163/163 tests en verde**.
- Commit: `4d9dc2a` — `test: ampliar cobertura de MovimientoService`.

## Estado actual

El último bloque confirmado es el **Build 043**. Se amplió la cobertura de `MovimientoServiceTest` y la batería general quedó en **259/259 tests en verde**.

El último commit de código confirmado es `b6384f0` — `test: ampliar cobertura de MovimientoService`.

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

## Regla histórica

Los hechos importantes del proyecto deben registrarse con Build y/o commit cuando exista. Las conversaciones pueden aportar contexto narrativo, pero no sustituyen este historial.
