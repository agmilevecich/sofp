# SOFP — Historial del proyecto

## Estado documental — 07/09/2026

Este documento conserva el punto de continuidad de la evolución del proyecto. Los estados técnicos deben verificarse siempre contra código, tests y Git.

## Hitos principales

1. Construcción progresiva del dominio financiero con JPA/Hibernate y H2.
2. Consolidación de `Movimiento` como núcleo financiero común.
3. Implementación de operaciones financieras e inversiones.
4. Auditoría transversal de seguridad y aislamiento por usuario/perfil, integrada en `main`.
5. Construcción del shell Swing de la Fase 8.
6. Integración de cuentas, categorías, movimientos, inversiones y reportes en el shell.
7. Implementación del primer corte funcional de Gastos.
8. Integración de `FormaPago` al flujo de Gastos.
9. Pulido visual incremental de Cuentas, Movimientos y Categorías.
10. Validación de la suite general con **602/602 tests**.
11. Cobertura específica adicional de las reglas de saldo de movimientos con **3/3** y validación relacionada con **61/61**.

## Gastos

El primer corte de Gastos quedó integrado mediante:

**`GastosPanel` → `GastoService` → `MovimientoService` → `Movimiento` `EGRESO` → `Movimientos`.**

La arquitectura mantiene una única fuente de verdad financiera.

## FormaPago

Se incorporó `FormaPago` al dominio de `Movimiento` y al flujo de Gastos.

Opciones actuales: `EFECTIVO`, `TRANSFERENCIA`, `TARJETA_DEBITO`, `TARJETA_CREDITO` y `QR`.

`GastoService` exige una forma de pago. `TARJETA_CREDITO` permanece bloqueada hasta disponer de un modelo correcto de obligaciones/pasivos.

## Reglas de saldo

El bloque de reglas de fondos quedó ampliado y validado.

`MovimientoServiceSaldoTest` cubre rechazo de egreso superior al saldo, aceptación de egreso exactamente igual al saldo y aumento de un egreso hasta el saldo disponible.

La última validación relacionada informada por el usuario fue **61/61**, sin fallos, errores ni tests omitidos. El commit `06a9fd8` corrigió el fixture para ejercitar la API pública del servicio con el usuario propietario.

## Shell Swing — pulido visual

Los ajustes visuales realizados anteriormente fueron:

- `5faff68` — `style: mejorar layout del panel de cuentas`.
- `5310ba3` — `style: mejorar layout del panel de movimientos`.
- `26f7f58` — `style: mejorar layout del panel de categorias`.

Las pruebas específicas correspondientes informadas por el usuario fueron **3/3**, **3/3** y **4/4**, respectivamente.

## Validación

Suite general más reciente informada por el usuario el **07/09/2026 14:59:12 -03:00**:

**602 tests, 0 failures, 0 errors, 0 skipped, BUILD SUCCESS.**

Comando: `mvn test`.
Duración: **10:54 min**.

Esta suite general no fue repetida después de la cobertura adicional de saldo. La validación relacionada posterior, ejecutada el **07/09/2026 20:12:52 -03:00**, quedó en **61/61**.

## Estado Git

`main`: `a4be85913847200cb70976d5266d9cbba10b3100`.

`feature/swing-shell`: `06a9fd849aeef9947ad79b9cd6a9943ec93ee8c3` antes de iniciar la actualización documental.

Comparación verificada antes de esta actualización: **306 commits por delante y 2 por detrás**, con merge-base `96f3d99969b0090dda9f502cf2cf999b87650386`.

La rama de trabajo continúa sin merge a `main`.

## Criterios permanentes

ControlFinanzas es referencia funcional, no arquitectura para copiar.

La UI no duplica reglas de negocio.

Los paneles especializados convergen en el núcleo financiero basado en `Movimiento`.

Los tests son condición de cierre.

No considerar implementada una funcionalidad solamente por estar documentada.

## Próximos hitos

1. Obligaciones/pasivos para tarjeta de crédito.
2. Ingresos y transferencias mediante el núcleo común.
3. Pasivos y patrimonio neto.
4. Análisis histórico, resúmenes, evolución patrimonial, vencimientos y dashboard.
5. Pulido posterior de la salida de consola de la aplicación.

No hacer merge a `main` automáticamente ni crear nuevas ramas salvo indicación explícita.
