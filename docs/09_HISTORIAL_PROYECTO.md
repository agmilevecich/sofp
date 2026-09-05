# SOFP — Historial del proyecto

## Estado documental — 05/09/2026

Este documento conserva el punto de continuidad actual de la evolución del proyecto. Los estados técnicos deben verificarse siempre contra código, tests y Git.

## Hitos principales

1. Construcción progresiva del dominio financiero con JPA/Hibernate y H2.
2. Consolidación de `Movimiento` como núcleo financiero común.
3. Implementación de operaciones financieras e inversiones.
4. Auditoría transversal de seguridad y aislamiento por usuario/perfil, integrada en `main`.
5. Construcción del shell Swing de la Fase 8.
6. Integración de cuentas, categorías, movimientos, inversiones y reportes en el shell.
7. Implementación del primer corte funcional de Gastos.
8. Integración de `FormaPago` al flujo de Gastos.

## Gastos

El primer corte de Gastos quedó integrado mediante:

**`GastosPanel` → `GastoService` → `MovimientoService` → `Movimiento` `EGRESO` → `Movimientos`.**

La arquitectura mantiene una única fuente de verdad financiera.

## FormaPago

Se incorporó `FormaPago` al dominio de `Movimiento` y al flujo de Gastos.

Opciones actuales: `EFECTIVO`, `TRANSFERENCIA`, `TARJETA_DEBITO`, `TARJETA_CREDITO` y `QR`.

`GastoService` exige una forma de pago. `TARJETA_CREDITO` permanece bloqueada hasta disponer de un modelo correcto de obligaciones/pasivos.

## Validación

Suite general más reciente informada por el usuario el **05/09/2026 13:04:09 -03:00**:

**590 tests, 0 failures, 0 errors, 0 skipped, BUILD SUCCESS.**

Comando: `mvn test`.
Duración: **11:29 min**.

## Estado Git

`main`: `a4be85913847200cb70976d5266d9cbba10b3100`.

`feature/swing-shell`: los cambios funcionales y documentales continúan sin merge a `main`.

Comparación verificada: **274 commits por delante y 2 por detrás**.

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

No hacer merge a `main` automáticamente ni crear nuevas ramas salvo indicación explícita.
