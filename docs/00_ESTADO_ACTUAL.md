# SOFP — Estado actual

> Documento de continuidad. La fuente de verdad técnica es el código, los tests y los commits actuales; `docs/` es documentación auxiliar.

## Estado verificado — 08/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

HEAD funcional documentado: `87052df953dbd282a43c5647d05b68d1854c4f17` — `test: cubrir navegacion hacia obligaciones`.

Desde el bloque documental anterior se incorporó la UI Swing de obligaciones y pagos y se corrigió el refresco para conservar la selección de la obligación.

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

La UI Swing de obligaciones ya está implementada mediante `ObligacionesPanel`. El panel:

- lista las obligaciones del usuario autorizado;
- muestra importe original, saldo pendiente, estado y fecha de origen;
- permite seleccionar una obligación;
- registra pagos mediante `ObligacionService` con autorización por usuario;
- refresca la información después del pago;
- conserva la obligación seleccionada al refrescar;
- informa errores mediante la interfaz Swing.

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

El usuario ejecutó `mvn test` el **08/09/2026 13:27:36 -03:00**:

- Tests run: **618**.
- Failures: **0**.
- Errors: **0**.
- Skipped: **0**.
- `BUILD SUCCESS`.
- Duración: **21:26 min**.

Esta sigue siendo la última suite general completa conocida. Fue ejecutada antes de la incorporación de la UI de obligaciones.

### Tests focalizados de obligaciones/UI

El usuario ejecutó:

`mvn -Dtest=MainFrameNavigationTest,MainFrameObligacionesTest,ObligacionesPanelTest,ObligacionServiceTest test`

Resultado informado el **08/09/2026 14:14:14 -03:00**:

- Tests run: **14**.
- Failures: **0**.
- Errors: **0**.
- Skipped: **0**.
- `BUILD SUCCESS`.
- Duración: **01:48 min**.

`ObligacionesPanelTest`: **3/3**.

La suite focalizada valida navegación, integración de `MainFrame`, panel de obligaciones y servicio de obligaciones.

No se debe asumir todavía que la suite completa posterior a estos cambios fue ejecutada.

## Próximo paso

El bloque de obligaciones ya está cerrado en dominio, persistencia, servicio, autorización y UI Swing, con tests focalizados verdes.

El siguiente paso de validación es ejecutar la **suite completa `mvn test`** sobre el estado actual y revisar `git diff`, `git diff --check` y `git status`.

Después podrán evolucionarse ingresos/transferencias, pasivos y patrimonio neto, análisis históricos, vencimientos y dashboard.

Como tarea de pulido posterior queda limpiar la salida de consola de la aplicación sin eliminar la posibilidad de diagnóstico.

## Continuidad

Antes de cualquier cambio revisar código actual, clases relacionadas, servicios, repositorios, tests, reglas de negocio, últimos commits y comparación con `main`.

Después de cambios importantes: tests específicos, relacionados y suite completa cuando corresponda; `git diff`, `git diff --check` y `git status`.

No asumir ejecuciones de tests, sincronizaciones o merges que no hayan sido informados o verificados.
