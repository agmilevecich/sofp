# SOFP — Continuidad consolidada

## Estado verificado — 05/09/2026

La fuente de verdad técnica es el código, los tests y los commits actuales de GitHub. `docs/` es documentación auxiliar.

- Rama estable: `main` → `a4be859`.
- Rama de trabajo: `feature/swing-shell` → `98dead73`.
- Último commit: `98dead73` — `test: preparar saldo para registro de gasto`.
- Suite general más reciente informada: **586/586 tests**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`.
- Comando: `mvn test`.
- Duración: **10:43 min**.
- Finalización informada: **05/09/2026 12:34:00 -03:00**.

La rama está divergida respecto de `main`: **260 commits por delante y 2 por detrás**. No se realizó merge.

## Últimos cambios

El bloque de Gastos incorporó `GastoService` y `GastosPanel`, conectados al núcleo financiero mediante `MovimientoService`.

El flujo validado es:

**Gastos → `GastoService` → `MovimientoService` → `Movimiento` `EGRESO` → `Movimientos`.**

El commit `98dead73` ajustó únicamente el fixture de `GastosPanelTest`: se agregó un ingreso previo de $1.000 para poder registrar un gasto de $100 respetando la regla de fondos disponibles.

## Estado funcional

La Fase 8 — Swing continúa implementada e integrada con cuentas, categorías, movimientos, inversiones, reportes y gastos. La UI mantiene las reglas de negocio en los servicios.

La regla de fondos insuficientes está implementada: un `EGRESO` no puede superar el saldo disponible; un egreso igual al saldo es válido. También se valida al modificar importe o tipo cuando corresponde.

La gestión de categorías con movimientos quedó implementada: no se elimina físicamente una categoría referenciada por movimientos; se conserva el historial mediante desactivación y la UI informa la situación de forma amigable.

El primer corte funcional de Gastos está implementado y validado. Los gastos aparecen como `EGRESO` en el historial común de `Movimientos`, sin crear una segunda fuente de verdad.

## FormaPago

`FormaPago` está definida con efectivo, transferencia, tarjeta de débito, tarjeta de crédito y QR.

La integración de `FormaPago` con `Movimiento` no debe hacerse de forma aislada: se incorporará dentro del flujo de Gastos cuando corresponda.

La tarjeta de crédito requiere posteriormente representar obligaciones/pasivos sin asumir una salida inmediata de fondos de una cuenta bancaria.

## Validación acumulada relevante

- `MovimientoFondosInsuficientesTest`: 6/6.
- `MovimientoServiceTest`: 57/57.
- `RegistrarMovimientoPanelTest`: 4/4.
- `RegistrarCuentaPanelTest`: 6/6.
- `CategoriaServiceTest`: 23/23.
- `GastosPanelTest`: cobertura del primer corte funcional.
- Suite general: **586/586**.

## Próximo paso

El primer corte de **GastosPanel** queda cerrado.

El próximo bloque funcional es integrar `FormaPago` al flujo de Gastos de forma coherente con el modelo financiero. Después se podrá avanzar hacia tarjetas de crédito, obligaciones/pasivos y patrimonio neto.

No hacer merge automático a `main`. No crear ramas nuevas salvo indicación explícita.
