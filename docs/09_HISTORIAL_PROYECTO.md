# SOFP — Historial del proyecto

Este documento conserva una línea temporal resumida de decisiones, avances y puntos de continuidad.

## 2026-08-14

### Build 026 — Eliminación de Movimiento

- Se completó la operación de eliminación de movimientos en `MovimientoRepository` y `MovimientoService`.
- `MovimientoRepository` incorporó `eliminar(Movimiento movimiento)` y garantiza que la entidad esté gestionada antes de ejecutar `remove()` cuando corresponde.
- `MovimientoService` incorporó `eliminar(Long movimientoId)`, validando el ID y la existencia del movimiento antes de iniciar la operación transaccional.
- La eliminación utiliza `begin`, `flush`, `commit` y `rollback()` ante excepciones.
- Se consolidó el nombre `modificarTipoMovimiento(...)` en la entidad `Movimiento` para mantener coherencia con el servicio.
- Se ejecutó la batería general y quedó en **121/121 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.
- Commit de código: `d386d02` — `feat: completar operaciones de Movimiento`.
- El commit fue publicado en `main` de GitHub y Bitbucket.

## Línea histórica anterior

Los Builds 001 a 025 se encuentran registrados en `docs/06_BUILDS.md` y en el historial Git del repositorio.

### Últimos hitos

- Build 023 — Ampliación de `CuentaService`, 113/113 tests en verde, commit `ea595d4`.
- Build 024 — Ampliación inicial de `MovimientoService`, 118/118 tests en verde, commit `110f7d7`.
- Build 025 — Ampliación de `Movimiento` y nuevas operaciones de `MovimientoService`, 121/121 tests en verde, commits `da3b89d` y `81883ea`.

## Estado de continuidad

El último Build confirmado es el **Build 026** y el próximo bloque a definir es el **Build 027**.

La fuente permanente de verdad del proyecto es el código del repositorio, el historial Git, la documentación de `docs/` y los tests automatizados.

## Regla histórica

Los hechos importantes del proyecto deben registrarse con fecha, Build y/o commit. Las conversaciones pueden aportar contexto narrativo, pero no sustituyen este historial.
