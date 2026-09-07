# SOFP — Estado actual

> Documento de continuidad. La fuente de verdad técnica es el código, los tests y los commits actuales; `docs/` es documentación auxiliar.

## Estado verificado — 07/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell` → `26f7f5891f1657d6c343d20a378b291c3eb310fd`.

Último commit de la rama: `26f7f5891f1657d6c343d20a378b291c3eb310fd` — `style: mejorar layout del panel de categorias`.

La comparación verificada indica que `feature/swing-shell` está **292 commits por delante y 2 por detrás** de `main`. El merge-base es `96f3d99969b0090dda9f502cf2cf999b87650386`. No se realizó merge a `main`.

## Estado funcional

La Fase 8 continúa sobre el shell Swing integrado con Inicio, Cuentas, Categorías, Gastos, Movimientos, Inversiones y Reportes.

Criterio central:

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

`Movimientos` es el historial financiero común y consolidado, no una segunda fuente de verdad.

## Gastos y FormaPago

El flujo funcional vigente es:

**`GastosPanel` → `GastoService` → `MovimientoService` → `Movimiento` `EGRESO` → `Movimientos`.**

`FormaPago` está integrada y validada. Opciones actuales: `EFECTIVO`, `TRANSFERENCIA`, `TARJETA_DEBITO`, `TARJETA_CREDITO` y `QR`.

`GastoService` exige forma de pago. `TARJETA_CREDITO` continúa rechazada hasta disponer del modelo correcto de obligaciones/pasivos; no debe simularse una salida inmediata de fondos por una compra a crédito.

## UI — estado reciente

Los últimos cambios de `feature/swing-shell` son ajustes visuales del shell, sin cambios de reglas financieras:

- `5faff68` — mejora de layout de `CuentasPanel`.
- `5310ba3` — mejora de layout de `MovimientosPanel`.
- `26f7f58` — mejora de layout de `CategoriasPanel`.

Los tres paneles siguen el patrón visual de espaciado y secciones tituladas, manteniendo la lógica existente.

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

Suite general ejecutada y reportada por el usuario el **07/09/2026 14:59:12 -03:00** mediante `mvn test`:

- Tests run: **602**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **10:54 min**

Además, las pruebas específicas recientes de UI quedaron verdes:

- `CuentasPanelTest`: **3/3**.
- `MovimientosPanelTest`: **3/3**.
- `CategoriasPanelTest`: **4/4**.

## Próximo paso

La rama queda funcionalmente validada y con el shell Swing en etapa de pulido visual. El siguiente bloque funcional pendiente sigue siendo el diseño/modelado de obligaciones y pasivos si se decide habilitar compras con tarjeta de crédito.

Como tarea de pulido futura queda limpiar la salida de consola al ejecutar la aplicación, sin prioridad inmediata y sin alterar ahora la configuración de logging.

## Continuidad

Antes de cualquier cambio revisar código actual, clases relacionadas, servicios, repositorios, tests, reglas de negocio, últimos commits y comparación con `main`.

Después de cambios importantes: tests específicos, relacionados y suite completa cuando corresponda; `git diff`, `git diff --check` y `git status`.

No asumir ejecuciones de tests, sincronizaciones o merges que no hayan sido informados o verificados.
