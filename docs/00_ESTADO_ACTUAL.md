# SOFP — Estado actual

> Documento de continuidad. La fuente de verdad técnica es el código, los tests y los commits actuales; `docs/` es documentación auxiliar.

## Estado verificado — 08/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

Último cambio funcional verificado: `f9339db2a500d5530eea95dc39e14e2725f4eb8f` — `test: cubrir navegacion hacia ingresos`.

Los commits posteriores a ese cambio, si los hubiera, son documentales. No se realizó merge a `main`.

## Estado funcional

La Fase 8 continúa sobre el shell Swing integrado con Inicio, Cuentas, Categorías, Ingresos, Gastos, Movimientos, Inversiones, Reportes y Obligaciones.

Criterio central:

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

`Movimientos` es el historial financiero común y consolidado, no una segunda fuente de verdad.

## Ingresos

El flujo funcional vigente es:

**`IngresosPanel` → `IngresoService` → `MovimientoService` → `Movimiento` `INGRESO` → `Movimientos`.**

`IngresosPanel` permite seleccionar cuenta y categoría activas, ingresar importe, fecha y descripción, y registrar el ingreso con el `usuarioId` autorizado. El registro reutiliza el núcleo financiero común y queda persistido como `TipoMovimiento.INGRESO`.

El formulario tiene constructor sin contexto para el shell y constructor con contexto para el flujo real. La validación específica cubre construcción, filtrado de cuentas/categorías activas, registro persistente y dependencias obligatorias.

## Gastos y FormaPago

El flujo funcional vigente es:

**`GastosPanel` → `GastoService` → `MovimientoService` → `Movimiento` `EGRESO` → `Movimientos`.**

`FormaPago` está integrada y validada. Opciones actuales: `EFECTIVO`, `TRANSFERENCIA`, `TARJETA_DEBITO`, `TARJETA_CREDITO` y `QR`.

La tarjeta de crédito dispone del modelo de obligaciones. Cuando `GastoService` recibe `TARJETA_CREDITO` y tiene `ObligacionService`, registra el movimiento de egreso y crea una `Obligacion` asociada al movimiento persistido. Si no se dispone del servicio, el flujo se rechaza con `IllegalStateException`.

## Obligaciones

El dominio contiene `Obligacion` con `importeOriginal`, `saldoPendiente`, `estado` y relación con el `Movimiento` de origen.

Estados actuales: `PENDIENTE`, `PARCIAL` y `PAGADA`.

`ObligacionService` permite listar por usuario y registrar pagos autorizados. El dominio rechaza pagos no positivos, pagos superiores al saldo y pagos sobre obligaciones ya pagadas.

La UI Swing de obligaciones está implementada mediante `ObligacionesPanel`. El panel lista las obligaciones del usuario autorizado, muestra importe original, saldo pendiente, estado y fecha de origen, permite registrar pagos, refresca la información y conserva la obligación seleccionada al refrescar.

La navegación hacia obligaciones e ingresos está integrada en `SidebarPanel`/`MainFrame` y tiene cobertura específica.

## UI — estado reciente

El shell Swing dispone de paneles para Inicio, Cuentas, Categorías, Ingresos, Gastos, Movimientos, Inversiones, Reportes y Obligaciones, integrados mediante `MainFrame` y `CardLayout`.

Cambios funcionales recientes del bloque de Ingresos:

- `d99cc6a` — `feat: agregar formulario de ingresos`.
- `2977f36` — `test: cubrir formulario de ingresos`.
- `4e6b363` — `feat: integrar ingresos al shell`.
- `cd781a1` — `feat: agregar ingresos a la navegacion`.
- `f9339db` — `test: cubrir navegacion hacia ingresos`.

## Reglas financieras vigentes

- Un `EGRESO` no puede superar el saldo disponible.
- Un egreso igual al saldo disponible está permitido y deja saldo cero.
- Las modificaciones de importe y tipo también respetan fondos disponibles.
- Categorías con movimientos se conservan y se desactivan en lugar de eliminarse físicamente.
- Cuenta y forma de pago son conceptos distintos.
- Una compra con `TARJETA_CREDITO` genera un movimiento de egreso y una obligación; no se debe modelar como pago inmediato de la cuenta.
- Las transferencias entre cuentas propias no son ingresos ni gastos; se modelan mediante `OperacionFinanciera`.
- Los paneles especializados no deben duplicar el núcleo financiero.

## Seguridad

El aislamiento de datos por usuario/perfil está implementado en los servicios y repositorios correspondientes. Los flujos de ingresos, gastos y obligaciones utilizan autorización por usuario.

## Validación reciente

### Suite general

El usuario ejecutó `mvn test` el **08/09/2026 15:16:17 -03:00**:

- Tests run: **630**.
- Failures: **0**.
- Errors: **0**.
- Skipped: **0**.
- `BUILD SUCCESS`.
- Duración: **11:55 min**.

Esta es la suite general completa más reciente y valida la integración de Ingresos, incluida su navegación, sin regresiones en el resto del proyecto.

### Tests focalizados de Ingresos y navegación

El usuario ejecutó:

`mvn -Dtest=IngresosPanelTest,MainFrameNavigationTest test`

Resultado informado el **08/09/2026 14:55:35 -03:00**: **5/5**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`, duración **01:21 min**.

### Tests relacionados

El usuario ejecutó el **08/09/2026 14:58:53 -03:00**:

`mvn -Dtest=IngresoServiceTest,IngresosPanelTest,MainFrameNavigationTest,MainFrameObligacionesTest,ObligacionesPanelTest,GastosPanelTest test`

Resultado: **18/18**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`, duración **01:31 min**.

## Próximo paso

El bloque de Ingresos está implementado, integrado al shell y validado mediante tests específicos, relacionados y suite general.

El siguiente paso funcional puede ser evolucionar transferencias mediante el núcleo común, o continuar con pasivos y patrimonio neto, análisis históricos, vencimientos y dashboard.

Como tarea de pulido posterior queda limpiar la salida de consola de la aplicación sin eliminar la posibilidad de diagnóstico.

Antes de cualquier cambio revisar código actual, clases relacionadas, servicios, repositorios, tests, reglas de negocio, últimos commits y comparación con `main`.

Después de cambios importantes: tests específicos, relacionados y suite completa cuando corresponda; `git diff`, `git diff --check` y `git status`.
