# SOFP — Continuidad 2026-09-07

## Estado verificado

Rama estable: `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
Rama de trabajo: `feature/swing-shell` → `26f7f5891f1657d6c343d20a378b291c3eb310fd` antes de la actualización documental.

La rama de trabajo está divergida respecto de `main`: **292 commits por delante y 2 por detrás**. Merge-base: `96f3d99969b0090dda9f502cf2cf999b87650386`. No se realizó merge a `main`.

Último commit de código: `26f7f5891f1657d6c343d20a378b291c3eb310fd` — `style: mejorar layout del panel de categorias`.

## Últimos cambios

La rama completó una secuencia de pulido visual del shell Swing:

- `5faff68` — `style: mejorar layout del panel de cuentas`.
- `5310ba3` — `style: mejorar layout del panel de movimientos`.
- `26f7f58` — `style: mejorar layout del panel de categorias`.

Son cambios de presentación/layout y mantienen la lógica funcional existente.

## Estado funcional actual

La Fase 8 continúa sobre el shell Swing integrado con Inicio, Cuentas, Categorías, Gastos, Movimientos, Inversiones y Reportes.

Arquitectura funcional acordada:

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

Gastos utiliza:

**`GastosPanel` → `GastoService` → `MovimientoService` → `Movimiento` `EGRESO` → `Movimientos`.**

No existe una segunda fuente de verdad financiera para Gastos.

## FormaPago — cerrado y validado

`FormaPago` está integrada al dominio de `Movimiento` y al flujo de Gastos. `GastosPanel` ofrece las cinco opciones actuales: `EFECTIVO`, `TRANSFERENCIA`, `TARJETA_DEBITO`, `TARJETA_CREDITO` y `QR`.

La forma de pago se persiste en `Movimiento` y puede modificarse en el dominio.

`GastoService` exige una forma de pago.

`TARJETA_CREDITO` se rechaza explícitamente por ahora porque todavía no existe el modelo de obligaciones/pasivos necesario para representar correctamente una compra a crédito sin simular una salida inmediata de fondos.

## Reglas financieras vigentes

- `EGRESO` mayor al saldo disponible: rechazado.
- `EGRESO` igual al saldo disponible: permitido y deja saldo cero.
- Las modificaciones de importe y tipo también respetan fondos disponibles.
- Categorías con movimientos no se eliminan físicamente; se desactivan para conservar historial.
- Cuenta y forma de pago son conceptos distintos.
- Transferencias entre cuentas propias no son ingresos ni gastos; se representan como movimientos relacionados mediante `OperacionFinanciera`.
- Los paneles especializados no deben duplicar el núcleo financiero.

## Validación más reciente

Suite general ejecutada y reportada por el usuario el **07/09/2026 14:59:12 -03:00** mediante `mvn test`:

- Tests run: **602**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **10:54 min**

Pruebas específicas recientes:

- `CuentasPanelTest`: **3/3**.
- `MovimientosPanelTest`: **3/3**.
- `CategoriasPanelTest`: **4/4**.

Total: **10/10**.

El fallo histórico observado en `RegistrarMovimientoPanelTest.deberiaRegistrarMovimientoConLaHoraDelSistema` no reapareció en la suite general del 07/09/2026.

## Próximos pasos

1. Diseñar y modelar obligaciones/pasivos para tarjeta de crédito antes de habilitar su efecto financiero.
2. Evolucionar ingresos y transferencias mediante el núcleo común.
3. Incorporar progresivamente pasivos y patrimonio neto.
4. Evolucionar análisis, resúmenes, evolución patrimonial, vencimientos y dashboard.
5. Como pulido posterior, limpiar la salida de consola de la aplicación sin eliminar la posibilidad de diagnóstico.

## Reglas de continuidad

La fuente de verdad es siempre el código, tests y commits actuales. `docs/` es documentación auxiliar.

Antes de cualquier cambio: revisar rama, últimos commits, comparación con `main`, implementación, clases relacionadas, servicios, repositorios, tests y reglas de negocio.

No modificar `main`, no crear ramas nuevas salvo indicación explícita y no asumir tests ejecutados sin resultado informado.

Después de cambios importantes: tests específicos, relacionados y suite completa cuando corresponda; `git diff`, `git diff --check` y `git status`.
