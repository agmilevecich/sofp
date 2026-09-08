# SOFP — Estado actual

> Documento de continuidad. La fuente de verdad técnica es el código, los tests y los commits actuales; `docs/` es documentación auxiliar.

## Estado verificado — 08/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell` → `9c6f274111ccf72602dd755e527b21faacc86b11`.

La comparación actual con `main` indica **390 commits adelante y 0 atrás**. No se realizó merge a `main`.

Último cambio funcional:

- `1753074` — `feat: agregar formulario de transferencias`.
- `aa29d44` — `test: cubrir formulario de transferencias`.
- `ef71bdc` — `feat: preparar integracion de transferencias en Main`.
- `0d7b708` — `feat: integrar transferencias al shell`.
- `99c9218` — `feat: agregar transferencias a la navegacion`.
- `9c6f274` — `test: cubrir navegacion a transferencias`.

## Estado funcional

La Fase 8 continúa sobre el shell Swing integrado con Inicio, Cuentas, Categorías, Ingresos, Gastos, Movimientos, Inversiones, Reportes, Obligaciones y Transferencias.

Criterio central:

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

`Movimientos` es el historial financiero común y consolidado, no una segunda fuente de verdad.

## Transferencias

El formulario `TransferenciasPanel` está implementado e integrado al shell.

El flujo funcional es:

**`TransferenciasPanel` → `OperacionFinancieraService` → `OperacionFinanciera` + `Movimiento` EGRESO/INGRESO.**

La transferencia entre cuentas propias se mantiene diferenciada de ingresos y gastos. El formulario utiliza cuentas y categorías activas del perfil/usuario autorizado, importe, fecha y descripción, y delega la operación al servicio central.

Una transferencia genera una única `OperacionFinanciera` con dos movimientos: `EGRESO` en la cuenta origen e `INGRESO` en la cuenta destino.

La integración al shell incluye creación del `OperacionFinancieraRepository` y `OperacionFinancieraService` en `Main`, inyección compatible en `MainFrame`, tarjeta del módulo y botón de navegación en `SidebarPanel`.

`TransferenciasPanelTest` cubre construcción del shell, filtrado de cuentas/categorías activas, persistencia de una transferencia con sus dos movimientos y dependencias obligatorias. `MainFrameNavigationTest` cubre la navegación hacia Transferencias.

## Ingresos

El flujo funcional es:

**`IngresosPanel` → `IngresoService` → `MovimientoService` → `Movimiento` `INGRESO` → `Movimientos`.**

El formulario permite cuenta, categoría, importe, fecha y descripción, utilizando cuentas y categorías activas del perfil/usuario autorizado.

## Gastos y FormaPago

El flujo funcional es:

**`GastosPanel` → `GastoService` → `MovimientoService` → `Movimiento` `EGRESO` → `Movimientos`.**

`FormaPago` está integrada y validada. Opciones actuales: `EFECTIVO`, `TRANSFERENCIA`, `TARJETA_DEBITO`, `TARJETA_CREDITO` y `QR`.

La tarjeta de crédito dispone del modelo de obligaciones. Cuando `GastoService` recibe `TARJETA_CREDITO` y tiene `ObligacionService`, registra el movimiento de egreso y crea una `Obligacion` asociada al movimiento persistido. Sin el servicio, el flujo se rechaza con `IllegalStateException`.

## Obligaciones

El dominio contiene `Obligacion` con `importeOriginal`, `saldoPendiente`, `estado` y relación con el `Movimiento` de origen.

Estados actuales: `PENDIENTE`, `PARCIAL` y `PAGADA`.

`ObligacionService` permite listar por usuario y registrar pagos autorizados. La UI `ObligacionesPanel` consulta, permite pagar y refresca conservando la selección.

## Reglas financieras vigentes

- Un `EGRESO` no puede superar el saldo disponible.
- Un egreso igual al saldo disponible está permitido y deja saldo cero.
- Las modificaciones de importe y tipo también respetan fondos disponibles.
- Categorías con movimientos se conservan y se desactivan en lugar de eliminarse físicamente.
- Cuenta y forma de pago son conceptos distintos.
- Una compra con `TARJETA_CREDITO` genera un movimiento de egreso y una obligación; no se modela como pago inmediato de la cuenta.
- Las transferencias entre cuentas propias no son ingresos ni gastos; se modelan mediante `OperacionFinanciera`.
- Los paneles especializados no deben duplicar el núcleo financiero.

## Seguridad

El aislamiento de datos por usuario/perfil está implementado en los servicios y repositorios correspondientes. Los flujos de ingresos, gastos, obligaciones y transferencias utilizan autorización por usuario.

## Validación reciente

### Suite general

El usuario ejecutó `mvn test` el **08/09/2026 18:13:55 -03:00**:

- Tests run: **634**.
- Failures: **0**.
- Errors: **0**.
- Skipped: **0**.
- `BUILD SUCCESS`.
- Duración: **11:52 min**.

### Tests de Transferencias y navegación

El usuario ejecutó `mvn -Dtest=MainFrameNavigationTest test` el **08/09/2026 18:51:06 -03:00**:

- **1/1**.
- Failures: **0**.
- Errors: **0**.
- Skipped: **0**.
- `BUILD SUCCESS`.
- Duración: **42.833 s**.

Luego ejecutó `mvn -Dtest=TransferenciasPanelTest,MainFrameNavigationTest test` el **08/09/2026 18:53:49 -03:00**:

- **5/5**.
- Failures: **0**.
- Errors: **0**.
- Skipped: **0**.
- `BUILD SUCCESS`.
- Duración: **01:30 min**.

## Próximo paso

El bloque de Transferencias está implementado, integrado al shell y validado mediante tests específicos, navegación y suite general.

Próximos bloques funcionales candidatos:

1. ampliar pasivos y patrimonio neto;
2. análisis históricos, resúmenes, evolución patrimonial, vencimientos y dashboard;
3. como pulido posterior, limpiar la salida de consola de la aplicación sin eliminar la posibilidad de diagnóstico.

Antes de cualquier cambio revisar código actual, clases relacionadas, servicios, repositorios, tests, reglas de negocio, últimos commits y comparación con `main`.

Después de cambios importantes: tests específicos, relacionados y suite completa cuando corresponda; `git diff`, `git diff --check` y `git status`.
