# Build 034 — Ampliación de cobertura de MovimientoService

## Objetivo

Ampliar la cobertura de `MovimientoServiceTest` sobre las validaciones de entrada, búsquedas de movimientos inexistentes y operaciones de modificación/eliminación sobre movimientos inexistentes.

## Cambios

Se incorporaron **17 tests nuevos** en `MovimientoServiceTest`, cubriendo:

- IDs nulos en búsqueda, listados, modificaciones y eliminación.
- Descripción, categoría, tipo, importe y fecha/hora nulos.
- Búsqueda de un movimiento inexistente mediante `Optional.empty()`.
- Intentos de modificar tipo, importe y fecha/hora de movimientos inexistentes.
- Intento de eliminar un movimiento inexistente.

No se modificó código de producción en este Build.

## Resultado

`MovimientoServiceTest`: **32/32 tests en verde**.

Batería general: **163/163 tests en verde**, con:

- Failures: 0
- Errors: 0
- Skipped: 0
- BUILD SUCCESS

También se ejecutó `git diff --check`, sin errores.

## Commit asociado

- `4d9dc2a` — `test: ampliar cobertura de MovimientoService`

El commit se encuentra en la rama local `main` y queda pendiente de publicación junto con el siguiente paso de sincronización.

## Próximo paso

Actualizar la documentación de continuidad y luego sincronizar `main` y `docs/continuidad-sofp` con los remotos.
