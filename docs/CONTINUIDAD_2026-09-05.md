# SOFP — Continuidad 2026-09-05

## Estado verificado

Rama estable: `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
Rama de trabajo: `feature/swing-shell` → `597982d4450f60b8e06083e71b5f364905bc79cf`.

La rama de trabajo está divergida respecto de `main`: **274 commits por delante y 2 por detrás**. No se realizó merge a `main`.

Último commit: `597982d4450f60b8e06083e71b5f364905bc79cf` — `docs: sincronizar estado actual con continuidad 2026-09-05`.

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

`GastoService` exige una forma de pago para registrar un gasto.

`TARJETA_CREDITO` se rechaza explícitamente por ahora porque todavía no existe el modelo de obligaciones/pasivos necesario para representar correctamente una compra a crédito sin simular una salida inmediata de fondos.

## Validación más reciente

Suite general ejecutada y reportada por el usuario el **05/09/2026 13:04:09 -03:00** mediante `mvn test`:

- Tests run: **590**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **11:29 min**

Esta ejecución valida la integración actual de `FormaPago` y mantiene en verde la suite completa.

## Reglas financieras vigentes

- `EGRESO` mayor al saldo disponible: rechazado.
- `EGRESO` igual al saldo disponible: permitido y deja saldo cero.
- Las modificaciones de importe y tipo también respetan fondos disponibles.
- Categorías con movimientos no se eliminan físicamente; se desactivan para conservar historial.
- Cuenta y forma de pago son conceptos distintos.
- Transferencias entre cuentas propias no son ingresos ni gastos; se representan como movimientos relacionados mediante `OperacionFinanciera`.
- Los paneles especializados no deben duplicar el núcleo financiero.

## Próximos pasos

1. Diseñar y modelar obligaciones/pasivos para tarjeta de crédito antes de habilitar su efecto financiero.
2. Evolucionar ingresos y transferencias mediante el núcleo común.
3. Incorporar progresivamente pasivos y patrimonio neto.
4. Evolucionar análisis, resúmenes, evolución patrimonial, vencimientos y dashboard según el roadmap.

## Reglas de continuidad

La fuente de verdad es siempre el código, tests y commits actuales. `docs/` es documentación auxiliar.

Antes de cualquier cambio: revisar rama, últimos commits, comparación con `main`, implementación, clases relacionadas, servicios, repositorios, tests y reglas de negocio.

No modificar `main`, no crear ramas nuevas salvo indicación explícita y no asumir tests ejecutados sin resultado informado.

Después de cambios importantes: tests específicos, relacionados y suite completa cuando corresponda; `git diff`, `git diff --check` y `git status`.
