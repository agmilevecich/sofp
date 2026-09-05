# SOFP — Continuidad consolidada

## Estado verificado — 05/09/2026

La fuente de verdad técnica es el código, los tests y los commits actuales de GitHub. `docs/` es documentación auxiliar.

- Rama estable: `main` → `a4be859`.
- Rama de trabajo: `feature/swing-shell` → `85b767c`.
- Último commit: `85b767c` — `test: aislar persistencia en CategoriaServiceTest`.
- Suite general más reciente: **580/580 tests**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`.
- Comando ejecutado: `mvn clean test`.
- Duración: **10:58 min**.
- Finalización informada: **05/09/2026 09:49:58 -03:00**.

## Último cambio

`CategoriaServiceTest` fue aislado explícitamente cerrando `JpaTestManager` antes de crear el `EntityManager` de cada test. Esto resolvió el conflicto de unicidad de la moneda `ARS` que aparecía en la suite general.

Validación específica posterior: `CategoriaServiceTest` **23/23**, BUILD SUCCESS.

## Estado funcional

La Fase 8 — Swing continúa implementada e integrada con cuentas, categorías, movimientos, inversiones y reportes. La UI mantiene las reglas de negocio en los servicios.

La regla de fondos insuficientes está implementada: un `EGRESO` no puede superar el saldo disponible; un egreso igual al saldo es válido. También se valida al modificar importe o tipo cuando corresponde.

La gestión de categorías con movimientos quedó implementada: no se elimina físicamente una categoría referenciada por movimientos; se conserva el historial mediante desactivación y la UI informa la situación de forma amigable.

## Validación acumulada relevante

- `MovimientoFondosInsuficientesTest`: 6/6.
- `MovimientoServiceTest`: 57/57.
- `RegistrarMovimientoPanelTest`: 4/4.
- `RegistrarCuentaPanelTest`: 6/6.
- `CategoriaServiceTest`: 23/23.
- Suite general: **580/580**.

## Próximo paso

Antes de implementar otro bloque, reconstruir nuevamente el estado desde GitHub y revisar código, tests y reglas de negocio relacionadas. El siguiente bloque candidato es `FormaPago`, manteniendo la distinción entre `Cuenta` y medio de pago y el núcleo financiero común basado en `Movimiento`.

No hacer merge automático a `main`. No crear ramas nuevas salvo indicación explícita.
