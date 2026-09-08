# SOFP — Estado actual

> Documento de continuidad. La fuente de verdad técnica es el código, los tests y los commits actuales; `docs/` es documentación auxiliar.

## Estado verificado — 08/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

HEAD actual de la rama: `678a27a4921af8947fc9b404100978c00c72cfa2` — `docs: registrar suite general 626 tests`.

El último cambio funcional continúa siendo `87052df953dbd282a43c5647d05b68d1854c4f17` — `test: cubrir navegacion hacia obligaciones`. Los commits posteriores son documentales.

No se realizó merge a `main`.

## Estado funcional

La Fase 8 continúa sobre el shell Swing integrado con Inicio, Cuentas, Categorías, Gastos, Movimientos, Inversiones, Reportes y Obligaciones.

Criterio central:

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

`Movimientos` es el historial financiero común y consolidado, no una segunda fuente de verdad.

## Gastos y FormaPago

El flujo funcional vigente es:

**`GastosPanel` → `GastoService` → `MovimientoService` → `Movimiento` `EGRESO` → `Movimientos`.**

`FormaPago` está integrada y validada. Opciones actuales: `EFECTIVO`, `TRANSFERENCIA`, `TARJETA_DEBITO`, `TARJETA_CREDITO` y `QR`.

La tarjeta de crédito dispone del modelo de obligaciones. Cuando `GastoService` recibe `TARJETA_CREDITO` y tiene `ObligacionService`, registra el movimiento de egreso y crea una `Obligacion` asociada al movimiento persistido. Si no se dispone del servicio, el flujo se rechaza con `IllegalStateException`.

## Obligaciones

El dominio contiene `Obligacion` con `importeOriginal`, `saldoPendiente`, `estado` y relación con el `Movimiento` de origen.

Estados actuales: `PENDIENTE`, `PARCIAL` y `PAGADA`.

`ObligacionService` permite listar por usuario y registrar pagos autorizados. El dominio rechaza pagos no positivos, pagos superiores al saldo y pagos sobre obligaciones ya pagadas.

La UI Swing de obligaciones ya está implementada mediante `ObligacionesPanel`. El panel lista las obligaciones del usuario autorizado, muestra importe original, saldo pendiente, estado y fecha de origen, permite registrar pagos, refresca la información y conserva la obligación seleccionada al refrescar.

La navegación hacia obligaciones está integrada en `SidebarPanel`/`MainFrame` y tiene cobertura específica.

## UI — estado reciente

El shell Swing dispone de paneles para Inicio, Cuentas, Categorías, Gastos, Movimientos, Inversiones, Reportes y Obligaciones, integrados mediante `MainFrame` y `CardLayout`.

Commits funcionales recientes del bloque de obligaciones/UI:

- `43cfd9c` — autorización de pagos en el servicio.
- `456fbfb` — tests de autorización de pagos.
- `f20023d` — creación de `ObligacionesPanel`.
- `264dd54` — navegación/sidebar.
- `87b8e46` — integración en `MainFrame`.
- `7194a5d` — tests del panel.
- `029de48` — test de navegación.
- `166b5f0` — conservación de selección al refrescar obligaciones.
- `87052df` — cobertura de navegación hacia obligaciones.

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

El aislamiento de datos por usuario/perfil está implementado en los servicios y repositorios correspondientes. La consulta de obligaciones utilizada por la UI es específica del usuario autorizado.

## Validación reciente

### Suite general

El usuario ejecutó `mvn test` el **08/09/2026 14:41:08 -03:00**:

- Tests run: **626**.
- Failures: **0**.
- Errors: **0**.
- Skipped: **0**.
- `BUILD SUCCESS`.
- Duración: **12:35 min**.

Esta es la suite general completa más reciente y valida la integración de la UI de obligaciones y pagos.

### Tests focalizados de obligaciones/UI

El usuario ejecutó `mvn -Dtest=MainFrameNavigationTest,MainFrameObligacionesTest,ObligacionesPanelTest,ObligacionServiceTest test` el **08/09/2026 14:14:14 -03:00**.

Resultado: **14/14**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`, duración **01:48 min**.

## Próximo paso

La integración de obligaciones/UI está validada por la suite focalizada y por la suite general completa. El siguiente paso funcional puede ser evolucionar ingresos/transferencias, pasivos y patrimonio neto, análisis históricos, vencimientos y dashboard.

Como tarea de pulido posterior queda limpiar la salida de consola de la aplicación sin eliminar la posibilidad de diagnóstico.

Antes de cualquier cambio revisar código actual, clases relacionadas, servicios, repositorios, tests, reglas de negocio, últimos commits y comparación con `main`.

Después de cambios importantes: tests específicos, relacionados y suite completa cuando corresponda; `git diff`, `git diff --check` y `git status`.
