# SOFP — Historial de Builds

## Build 026 — Eliminación de Movimiento

Se incorporó la eliminación de movimientos en `MovimientoRepository` y `MovimientoService`, manteniendo validaciones y control transaccional.

### Cambios principales

- `MovimientoRepository.eliminar(Movimiento movimiento)`.
- Gestión de la entidad mediante `contains()`/`merge()` antes de `remove()` cuando corresponde.
- `MovimientoService.eliminar(Long movimientoId)`.
- Validación del ID y verificación de existencia.
- Transacción explícita con `begin`, `flush`, `commit` y `rollback` ante excepciones.
- Consolidación del nombre `modificarTipoMovimiento(...)` en `Movimiento`.

### Tests

Batería general: **121/121 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.

### Commit asociado

- `d386d02` — `feat: completar operaciones de Movimiento`.

El commit fue publicado en `main` de GitHub y Bitbucket.

### Próximo paso

Definir el Build 027 a partir del estado real del dominio, repositorios, servicios y casos de uso pendientes.

## Regla para futuros Builds

Agregar cada Build nuevo inmediatamente después de cerrarlo, incluyendo número y nombre, objetivo, cambios principales, tests ejecutados, resultado, commit asociado y próximo paso.
