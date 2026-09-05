# SOFP — Estado actual

> Documento de continuidad. La fuente de verdad técnica es el código, los tests y los commits actuales; `docs/` es documentación auxiliar.

## Estado verificado — 05/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell` → `04ee8214736bf21cca669ad203c8fb9dc7a1ce39`.

Último commit de la rama: `04ee8214736bf21cca669ad203c8fb9dc7a1ce39` — `docs: actualizar indice de continuidad 2026-09-05`.

La comparación verificada antes de las actualizaciones documentales indica que `feature/swing-shell` está **274 commits por delante y 2 por detrás** de `main`. No se realizó merge a `main`.

## Estado funcional

La Fase 8 continúa sobre el shell Swing integrado con Inicio, Cuentas, Categorías, Gastos, Movimientos, Inversiones y Reportes.

Criterio central:

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

`Movimientos` es el historial financiero común y consolidado, no una segunda fuente de verdad.

## Gastos

Primer corte funcional completado y validado. El flujo es:

**`GastosPanel` → `GastoService` → `MovimientoService` → `Movimiento` `EGRESO` → `Movimientos`.**

El panel permite seleccionar cuenta, categoría, importe, fecha, descripción y forma de pago. El servicio especializado delega las reglas financieras al núcleo existente.

## FormaPago

Integración completada y validada.

`Movimiento` persiste una `FormaPago` opcional para conservar compatibilidad con movimientos existentes. `MovimientoService` dispone del registro público que propaga la forma de pago y mantiene el registro anterior.

`GastoService` exige forma de pago y rechaza `TARJETA_CREDITO` hasta que exista un modelo de obligaciones/pasivos que permita representar correctamente la compra a crédito sin simular una salida inmediata de fondos.

Formas actuales: `EFECTIVO`, `TRANSFERENCIA`, `TARJETA_DEBITO`, `TARJETA_CREDITO`, `QR`.

## Reglas financieras vigentes

- Un `EGRESO` no puede superar el saldo disponible.
- Un egreso igual al saldo disponible está permitido y deja saldo cero.
- Las modificaciones de importe y tipo también respetan fondos disponibles.
- Categorías con movimientos se conservan y se desactivan en lugar de eliminarse físicamente.
- Cuenta y forma de pago son conceptos distintos.
- Las transferencias entre cuentas propias no son ingresos ni gastos; se modelan como movimientos relacionados mediante `OperacionFinanciera`.
- Los paneles especializados no deben duplicar el núcleo financiero.

## Seguridad

La auditoría transversal de aislamiento de datos quedó completada e integrada en `main`, cubriendo perfiles, cuentas, categorías, movimientos, posiciones/cartera y operaciones financieras con autorización por propietario.

## Validación vigente

Suite general ejecutada localmente y reportada por el usuario el **05/09/2026 13:04:09 -03:00** mediante `mvn test`:

- Tests run: **590**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **11:29 min**

Esta es la última ejecución conocida y valida el estado funcional actual de la rama.

## Próximo paso

El siguiente bloque funcional debe partir de `feature/swing-shell` tal como está ahora. Prioridad inmediata: modelar obligaciones/pasivos si se decide habilitar compras con tarjeta de crédito. No crear un modelo paralelo ni alterar `main`.

## Continuidad

Antes de cualquier cambio revisar código actual, clases relacionadas, servicios, repositorios, tests, reglas de negocio, últimos commits y comparación con `main`.

Después de cambios importantes: tests específicos, relacionados y suite completa cuando corresponda; `git diff`, `git diff --check` y `git status`.

No asumir ejecuciones de tests, sincronizaciones o merges que no hayan sido informados o verificados.
