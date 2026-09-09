# SOFP — Estado actual

> Documento de continuidad. La fuente de verdad técnica es el código, los tests y los commits actuales; `docs/` es documentación auxiliar.

## Estado verificado — 09/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell` → `13a68fb8429d930b2137b9c9e78f7b884077f33e`.

La comparación actual con `main` indica **411 commits adelante y 0 atrás**. No se realizó merge a `main`.

## Último cambio funcional

- `9b92eac` — `feat: exponer moneda de la obligacion`.
- `47ced65` — `feat: mostrar moneda en obligaciones`.
- `fe0aa71` — `test: verificar moneda en obligaciones`.
- `13a68fb` — `fix: estabilizar formato de moneda en obligaciones`.

El último cambio corrige el formato de moneda de `ObligacionesPanel` mediante `Locale.ROOT`, evitando que la representación dependa del locale del entorno.

## Estado funcional

La Fase 8 continúa sobre el shell Swing integrado con Inicio, Cuentas, Categorías, Ingresos, Gastos, Movimientos, Inversiones, Reportes, Obligaciones y Transferencias.

Criterio central:

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

`Movimientos` es el historial financiero común y consolidado, no una segunda fuente de verdad.

## Moneda en movimientos y obligaciones

Los movimientos admiten moneda explícita y los flujos existentes conservan la compatibilidad con la moneda de la cuenta cuando no se informa otra.

En una compra con tarjeta de crédito se conserva la moneda económica del consumo. `Obligacion.getMoneda()` expone la moneda del movimiento de origen, por lo que una obligación originada en USD permanece identificada como USD y una originada en ARS como ARS.

`ObligacionesPanel` muestra tanto el importe original como el saldo pendiente con el código de moneda.

No se realiza conversión automática ARS↔USD al crear la obligación.

## Transferencias

`TransferenciasPanel` utiliza `OperacionFinancieraService` para registrar transferencias entre cuentas propias. Una transferencia genera una única `OperacionFinanciera` con un `EGRESO` en origen y un `INGRESO` en destino.

## Ingresos

**`IngresosPanel` → `IngresoService` → `MovimientoService` → `Movimiento` `INGRESO` → `Movimientos`.**

## Gastos y FormaPago

**`GastosPanel` → `GastoService` → `MovimientoService` → `Movimiento` `EGRESO` → `Movimientos`.**

`FormaPago` está integrada y validada. Opciones actuales: `EFECTIVO`, `TRANSFERENCIA`, `TARJETA_DEBITO`, `TARJETA_CREDITO` y `QR`.

La tarjeta de crédito dispone del modelo de obligaciones. Cuando `GastoService` recibe `TARJETA_CREDITO` y tiene `ObligacionService`, registra el movimiento de egreso y crea una `Obligacion` asociada.

## Obligaciones

`Obligacion` contiene `importeOriginal`, `saldoPendiente`, `estado` y relación con el `Movimiento` de origen. Estados: `PENDIENTE`, `PARCIAL` y `PAGADA`.

`ObligacionService` permite listar por usuario y registrar pagos autorizados. `ObligacionesPanel` consulta, permite pagar y refresca conservando la selección.

## Reglas financieras vigentes

- Un `EGRESO` no puede superar el saldo disponible.
- Un egreso igual al saldo disponible está permitido y deja saldo cero.
- Las modificaciones de importe y tipo también respetan fondos disponibles.
- Categorías con movimientos se conservan y se desactivan en lugar de eliminarse físicamente.
- Cuenta y forma de pago son conceptos distintos.
- Una compra con `TARJETA_CREDITO` genera un movimiento de egreso y una obligación.
- La moneda de la obligación es la moneda económica del movimiento de origen; no se convierte automáticamente al crear la obligación.
- Las transferencias entre cuentas propias no son ingresos ni gastos; se modelan mediante `OperacionFinanciera`.
- Los paneles especializados no deben duplicar el núcleo financiero.

## Validación reciente

### Suite general — 09/09/2026 13:15:48 -03:00

El usuario ejecutó `mvn test`:

- Tests run: **642**.
- Failures: **0**.
- Errors: **0**.
- Skipped: **0**.
- `BUILD SUCCESS`.
- Duración: **09:43 min**.

### ObligacionesPanelTest — 09/09/2026 13:05:03 -03:00

El usuario ejecutó `mvn test -Dtest=ObligacionesPanelTest`:

- Tests run: **4**.
- Failures: **0**.
- Errors: **0**.
- Skipped: **0**.
- `BUILD SUCCESS`.
- Duración: **01:20 min**.

La prueba incluye la visualización de obligaciones en USD con importe y saldo pendiente expresados en la moneda correspondiente.

## Estado local informado

El usuario ejecutó `git diff`, `git diff --check` y `git status` el 09/09/2026 y confirmó:

- working tree limpio;
- sin cambios para commit;
- rama `feature/swing-shell` sincronizada con `github/feature/swing-shell`.

## Próximo paso

El bloque de moneda en obligaciones queda completado y validado. Antes de implementar otro bloque se debe reconstruir nuevamente el estado desde código, tests y commits.

El pendiente funcional de mayor nivel continúa siendo ampliar pasivos y patrimonio neto. También quedan análisis históricos, resúmenes, evolución patrimonial, vencimientos y dashboard, además del pulido posterior de salida de consola.

Antes de cualquier cambio revisar código actual, clases relacionadas, servicios, repositorios, tests, reglas de negocio, últimos commits y comparación con `main`.

Después de cambios importantes: tests específicos, relacionados y suite completa cuando corresponda; `git diff`, `git diff --check` y `git status`.
