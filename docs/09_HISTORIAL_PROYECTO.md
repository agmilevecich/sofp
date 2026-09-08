# SOFP — Historial del proyecto

## Estado documental — 08/09/2026

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
9. Implementación de obligaciones y pagos para compras con tarjeta de crédito.
10. Pulido visual incremental de Cuentas, Movimientos y Categorías.
11. Corrección de compatibilidad del shell y expectativas de tests.
12. Validación de la suite general con **618/618 tests**.

## Gastos

El flujo de Gastos quedó integrado mediante:

**`GastosPanel` → `GastoService` → `MovimientoService` → `Movimiento` `EGRESO` → `Movimientos`.**

La arquitectura mantiene una única fuente de verdad financiera.

## FormaPago

Se incorporó `FormaPago` al dominio de `Movimiento` y al flujo de Gastos.

Opciones actuales: `EFECTIVO`, `TRANSFERENCIA`, `TARJETA_DEBITO`, `TARJETA_CREDITO` y `QR`.

`GastoService` exige una forma de pago.

## Obligaciones

El modelo de obligaciones ya está implementado y validado en dominio, persistencia y servicio.

`Obligacion` representa el pasivo originado por una compra con tarjeta de crédito y conserva importe original, saldo pendiente, estado y movimiento de origen.

`ObligacionService` permite alta, consulta y registro de pagos con persistencia transaccional.

Los estados son `PENDIENTE`, `PARCIAL` y `PAGADA`.

La compra con tarjeta de crédito se integra en `GastoService`: primero se registra el movimiento de egreso y luego se crea la obligación asociada. Si no se dispone de `ObligacionService`, el flujo se rechaza; no se simula una salida inmediata de fondos por ausencia del modelo.

La capacidad que falta es la interfaz Swing específica para consultar obligaciones y registrar pagos.

## Reglas de saldo

El bloque de reglas de fondos quedó ampliado y validado.

`MovimientoServiceSaldoTest` cubre rechazo de egreso superior al saldo, aceptación de egreso exactamente igual al saldo y aumento de un egreso hasta el saldo disponible.

## Shell Swing

La Fase 8 integra Inicio, Cuentas, Categorías, Gastos, Movimientos, Inversiones y Reportes mediante `MainFrame` y `CardLayout`.

Los ajustes visuales realizados anteriormente fueron:

- `5faff68` — mejora de layout de `CuentasPanel`.
- `5310ba3` — mejora de layout de `MovimientosPanel`.
- `26f7f58` — mejora de layout de `CategoriasPanel`.

Los últimos fixes antes de esta actualización documental fueron:

- `6c7d70a` — `fix: mantener compatibilidad de MainFrame sin ObligacionService`.
- `7f05cd1` — `test: actualizar expectativas de gastos con crédito`.

## Validación

Suite general informada por el usuario el **08/09/2026 13:27:36 -03:00**:

**618 tests, 0 failures, 0 errors, 0 skipped, BUILD SUCCESS.**

Comando: `mvn test`.
Duración: **21:26 min**.

Antes de la suite general, `GastosPanelTest` + `MainFrameMovimientosTest` quedaron en **8/8**.

La ejecución general había presentado previamente 1 failure y 2 errors relacionados con la excepción esperada de tarjeta de crédito y constructores de `MainFrame`. Las correcciones `6c7d70a` y `7f05cd1` resolvieron esos problemas.

Validación final local informada por el usuario: `git diff`, `git diff --check` y `git status` limpios; working tree limpio y rama sincronizada con `github/feature/swing-shell`.

## Estado Git

`main`: `a4be85913847200cb70976d5266d9cbba10b3100`.

`feature/swing-shell`: `7f05cd1ef48576e0bc59a3828cd254e0bd4a9d9b` al iniciar esta actualización documental.

Comparación verificada: **326 commits por delante y 0 por detrás**, con merge-base `a4be85913847200cb70976d5266d9cbba10b3100`.

La actualización documental está siendo realizada directamente sobre `feature/swing-shell`; el SHA final deberá verificarse al cerrar este bloque.

## Criterios permanentes

ControlFinanzas es referencia funcional, no arquitectura para copiar.

La UI no duplica reglas de negocio.

Los paneles especializados convergen en el núcleo financiero basado en `Movimiento`.

Los tests son condición de cierre.

No considerar implementada una funcionalidad solamente por estar documentada.

No hacer merge a `main` automáticamente ni crear ramas nuevas salvo indicación explícita.

## Próximos hitos

1. Crear UI Swing de obligaciones y pagos.
2. Integrar esa UI en el shell y definir refresco después de gastos con tarjeta y pagos.
3. Evolucionar ingresos y transferencias mediante el núcleo común.
4. Ampliar pasivos y patrimonio neto.
5. Evolucionar análisis histórico, resúmenes, evolución patrimonial, vencimientos y dashboard.
6. Pulir posteriormente la salida de consola de la aplicación.
