# SOFP — Historial del proyecto

Este documento conserva una línea temporal resumida de decisiones, avances y puntos de continuidad.

## 2026-08-18

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

El último bloque funcional confirmado es el **Build 034**. El código de producción no fue modificado en este Build; se amplió la cobertura de pruebas de `MovimientoService`.

La batería general actual es de **163/163 tests en verde**.

La documentación de continuidad se mantiene en la rama `docs/continuidad-sofp`.

## Builds recientes

- Build 029 — Eliminación en `CategoriaRepository`.
- Build 030 — Eliminación en `CategoriaService`.
- Build 031 — Eliminación en `CuentaRepository`.
- Build 032 — Eliminación en `CuentaService`.
- Build 033 — Reglas de negocio de `Movimiento`.
- Build 034 — Ampliación de cobertura de `MovimientoServiceTest`.

## Regla histórica

Los hechos importantes del proyecto deben registrarse con Build y/o commit cuando exista. Las conversaciones pueden aportar contexto narrativo, pero no sustituyen este historial.
