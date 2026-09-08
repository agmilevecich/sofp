# SOFP — Estado actual

> Documento de continuidad. La fuente de verdad técnica es el código, los tests y los commits actuales; `docs/` es documentación auxiliar.

## Estado verificado — 08/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell` → `7f05cd1ef48576e0bc59a3828cd254e0bd4a9d9b`.

Último commit: `7f05cd1ef48576e0bc59a3828cd254e0bd4a9d9b` — `test: actualizar expectativas de gastos con crédito`.

La comparación verificada con `main` indica que `feature/swing-shell` está **326 commits por delante y 0 por detrás**. El merge-base es `a4be85913847200cb70976d5266d9cbba10b3100`. No se realizó merge a `main`.

## Estado funcional

La Fase 8 continúa sobre el shell Swing integrado con Inicio, Cuentas, Categorías, Gastos, Movimientos, Inversiones y Reportes.

Criterio central:

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

`Movimientos` es el historial financiero común y consolidado, no una segunda fuente de verdad.

## Gastos y FormaPago

El flujo funcional vigente es:

**`GastosPanel` → `GastoService` → `MovimientoService` → `Movimiento` `EGRESO` → `Movimientos`.**

`FormaPago` está integrada y validada. Opciones actuales: `EFECTIVO`, `TRANSFERENCIA`, `TARJETA_DEBITO`, `TARJETA_CREDITO` y `QR`.

La tarjeta de crédito **ya dispone del modelo de obligaciones**. Cuando `GastoService` recibe `TARJETA_CREDITO` y tiene `ObligacionService`, registra el movimiento de egreso y crea una `Obligacion` asociada al movimiento persistido. Si no se dispone del servicio de obligaciones, el flujo se rechaza con `IllegalStateException`.

## Obligaciones

El dominio contiene `Obligacion` con `importeOriginal`, `saldoPendiente`, `estado` y relación uno a uno con el `Movimiento` de origen.

Estados actuales: `PENDIENTE`, `PARCIAL` y `PAGADA`.

`ObligacionService` permite registrar, consultar y registrar pagos con persistencia transaccional. El dominio rechaza pagos no positivos, pagos superiores al saldo y pagos sobre obligaciones ya pagadas.

La integración funcional de obligaciones está implementada en servicios y dominio, pero **todavía no existe un panel Swing específico para listar obligaciones y registrar sus pagos**.

## UI — estado reciente

El shell Swing dispone de paneles para Inicio, Cuentas, Categorías, Gastos, Movimientos, Inversiones y Reportes, integrados mediante `MainFrame` y `CardLayout`.

Los últimos ajustes inmediatos fueron correcciones de compatibilidad y expectativas de tests:

- `6c7d70a` — `fix: mantener compatibilidad de MainFrame sin ObligacionService`.
- `7f05cd1` — `test: actualizar expectativas de gastos con crédito`.

La primera corrección permite que constructores antiguos de `MainFrame` sigan funcionando sin `ObligacionService`; la segunda actualiza la expectativa del test al comportamiento vigente.

## Reglas financieras vigentes

- Un `EGRESO` no puede superar el saldo disponible.
- Un egreso igual al saldo disponible está permitido y deja saldo cero.
- Las modificaciones de importe y tipo también respetan fondos disponibles.
- Categorías con movimientos se conservan y se desactivan en lugar de eliminarse físicamente.
- Cuenta y forma de pago son conceptos distintos.
- Una compra con `TARJETA_CREDITO` genera un movimiento de egreso y una obligación; no se debe modelar como pago inmediato de la cuenta.
- Las transferencias entre cuentas propias no son ingresos ni gastos; se modelan como movimientos relacionados mediante `OperacionFinanciera`.
- Los paneles especializados no deben duplicar el núcleo financiero.

## Seguridad

La auditoría transversal de aislamiento de datos quedó completada e integrada en `main`, cubriendo perfiles, cuentas, categorías, movimientos, posiciones/cartera y operaciones financieras con autorización por propietario.

## Validación reciente

El usuario ejecutó la suite general el **08/09/2026 13:27:36 -03:00** mediante `mvn test`:

- Tests run: **618**.
- Failures: **0**.
- Errors: **0**.
- Skipped: **0**.
- `BUILD SUCCESS`.
- Duración: **21:26 min**.

Antes de la suite general se validaron los tests focalizados de `GastosPanelTest` y `MainFrameMovimientosTest`: **8/8**, sin fallos ni errores.

La ejecución general había presentado previamente 1 fallo y 2 errores por expectativas y compatibilidad de constructores; esos problemas fueron corregidos en `6c7d70a` y `7f05cd1`, y la suite posterior quedó en **618/618**.

La validación local final informada por el usuario fue limpia: `git diff`, `git diff --check` y `git status`; rama sincronizada con `github/feature/swing-shell` y working tree limpio.

## Próximo paso

El bloque de obligaciones/pasivos ya no es un pendiente de modelado básico: dominio, persistencia, servicio, pagos y cobertura de tests existen.

El siguiente bloque funcional lógico es **llevar las obligaciones a Swing**: consultar obligaciones del usuario/perfil y permitir registrar pagos desde una interfaz especializada, manteniendo las reglas en dominio/servicio y evitando duplicar el núcleo financiero.

Después podrán evolucionarse ingresos/transferencias, pasivos y patrimonio neto, análisis históricos, vencimientos y dashboard.

Como tarea de pulido posterior queda limpiar la salida de consola de la aplicación sin eliminar la posibilidad de diagnóstico.

## Continuidad

Antes de cualquier cambio revisar código actual, clases relacionadas, servicios, repositorios, tests, reglas de negocio, últimos commits y comparación con `main`.

Después de cambios importantes: tests específicos, relacionados y suite completa cuando corresponda; `git diff`, `git diff --check` y `git status`.

No asumir ejecuciones de tests, sincronizaciones o merges que no hayan sido informados o verificados.
