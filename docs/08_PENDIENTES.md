# SOFP — Pendientes

Este documento contiene únicamente trabajo pendiente o por decidir.

Las etapas `feature/operacion-financiera`, `feature/identificacion-activo` y `feature/cartera-activos` fueron integradas en `main` y quedaron validadas.

Actualmente se encuentra desarrollada y validada `feature/costo-promedio-activo`, pendiente de integración en `main`.

### Costo promedio de posición activa

Implementado y validado en `feature/costo-promedio-activo`:
- costo de adquisición acumulado de compras;
- precio promedio de la posición;
- costo de adquisición remanente después de ventas;
- reinicio del costo al cerrar la posición;
- rechazo de ventas superiores a la posición;
- rechazo de movimiento nulo;
- rechazo de movimiento de otro activo;
- cobertura específica en `PosicionActivoTest` con **8/8 tests en verde**.

La feature contiene 2 commits funcionales sobre `main` y no está detrás de `main`. Se agregó además un commit documental de cierre.

## Validación global

La suite general más reciente fue ejecutada desde IntelliJ IDEA el **28/08/2026 19:12:22 -03:00**:

- **447/447 tests en verde**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración: **12:27 min**.

## Próximo cambio

Antes de integrar `feature/costo-promedio-activo` en `main` se debe:
- sincronizar la rama local;
- revisar `git status`;
- revisar `git diff main...feature/costo-promedio-activo`;
- ejecutar `git diff --check`;
- confirmar la validación específica y la suite general;
- integrar mediante `git merge --ff-only`;
- actualizar y validar el estado de Git después del merge.

No hacer merge a `main` automáticamente.

## Pendientes de arquitectura / evolución

Luego del cierre de esta feature se deberá definir la siguiente evolución funcional a partir del código, entidades, repositorios, servicios, tests y casos de uso existentes.

La parte gráfica se considera una etapa posterior: primero se continuará consolidando el backend y sus reglas de negocio para que la UI se apoye sobre servicios ya estabilizados.
