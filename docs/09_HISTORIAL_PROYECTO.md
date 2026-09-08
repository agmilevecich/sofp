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
10. Autorización de pagos de obligaciones por usuario.
11. Integración de obligaciones en el shell Swing.
12. Corrección del refresco de obligaciones para conservar la selección.
13. Cobertura de navegación hacia Obligaciones.
14. Validación focalizada del bloque de obligaciones/UI con **14/14 tests**.

## Gastos

El flujo de Gastos quedó integrado mediante:

**`GastosPanel` → `GastoService` → `MovimientoService` → `Movimiento` `EGRESO` → `Movimientos`.**

La arquitectura mantiene una única fuente de verdad financiera.

## FormaPago

Se incorporó `FormaPago` al dominio de `Movimiento` y al flujo de Gastos.

Opciones actuales: `EFECTIVO`, `TRANSFERENCIA`, `TARJETA_DEBITO`, `TARJETA_CREDITO` y `QR`.

`GastoService` exige una forma de pago.

## Obligaciones

El modelo de obligaciones está implementado y validado en dominio, persistencia y servicio.

`Obligacion` representa el pasivo originado por una compra con tarjeta de crédito y conserva importe original, saldo pendiente, estado y movimiento de origen.

`ObligacionService` permite alta, consulta, listado por usuario y registro de pagos autorizados con persistencia transaccional.

Los estados son `PENDIENTE`, `PARCIAL` y `PAGADA`.

La compra con tarjeta de crédito se integra en `GastoService`: primero se registra el movimiento de egreso y luego se crea la obligación asociada. Si no se dispone de `ObligacionService`, el flujo se rechaza; no se simula una salida inmediata de fondos por ausencia del modelo.

### UI Swing de obligaciones

`ObligacionesPanel` ya está integrado en el shell y permite:

- consultar obligaciones del usuario;
- mostrar importe original, saldo pendiente, estado y fecha de origen;
- seleccionar una obligación;
- registrar un pago mediante `ObligacionService` con autorización por usuario;
- refrescar el estado después del pago;
- conservar la selección al refrescar;
- informar errores mediante la interfaz.

`MainFrame`/`SidebarPanel` incorporan la navegación hacia Obligaciones.

## Reglas de saldo

El bloque de reglas de fondos quedó ampliado y validado.

`MovimientoServiceSaldoTest` cubre rechazo de egreso superior al saldo, aceptación de egreso exactamente igual al saldo y aumento de un egreso hasta el saldo disponible.

## Shell Swing

La Fase 8 integra Inicio, Cuentas, Categorías, Gastos, Movimientos, Inversiones, Reportes y Obligaciones mediante `MainFrame` y `CardLayout`.

Los ajustes visuales realizados anteriormente fueron:

- `5faff68` — mejora de layout de `CuentasPanel`.
- `5310ba3` — mejora de layout de `MovimientosPanel`.
- `26f7f58` — mejora de layout de `CategoriasPanel`.

El bloque reciente de obligaciones/UI quedó distribuido en commits pequeños:

- `43cfd9c` — autorización de pagos.
- `456fbfb` — tests de autorización.
- `f20023d` — `ObligacionesPanel`.
- `264dd54` — navegación/sidebar.
- `87b8e46` — integración en `MainFrame`.
- `7194a5d` — tests del panel.
- `029de48` — test de navegación.
- `166b5f0` — conservación de selección al refrescar.
- `87052df` — cobertura de navegación hacia obligaciones.

## Validación

Suite general informada por el usuario el **08/09/2026 13:27:36 -03:00**:

**618 tests, 0 failures, 0 errors, 0 skipped, BUILD SUCCESS.**

Comando: `mvn test`. Duración: **21:26 min**.

Esta suite es anterior a la incorporación de la UI de obligaciones.

Suite focalizada posterior informada por el usuario el **08/09/2026 14:14:14 -03:00**:

**14 tests, 0 failures, 0 errors, 0 skipped, BUILD SUCCESS.**

Comando:

`mvn -Dtest=MainFrameNavigationTest,MainFrameObligacionesTest,ObligacionesPanelTest,ObligacionServiceTest test`

Duración: **01:48 min**.

No se considera validada la suite completa posterior a la UI hasta que vuelva a ejecutarse.

## Estado Git

`main`: `a4be85913847200cb70976d5266d9cbba10b3100`.

La rama de trabajo es `feature/swing-shell`. El último commit funcional antes de esta actualización documental es `87052df953dbd282a43c5647d05b68d1854c4f17`.

La actualización documental genera commits posteriores; el SHA final debe verificarse al cerrar este bloque.

## Criterios permanentes

ControlFinanzas es referencia funcional, no arquitectura para copiar.

La UI no duplica reglas de negocio.

Los paneles especializados convergen en el núcleo financiero basado en `Movimiento`.

Los tests son condición de cierre.

No considerar implementada una funcionalidad solamente por estar documentada.

No hacer merge a `main` automáticamente ni crear ramas nuevas salvo indicación explícita.

## Próximos hitos

1. Ejecutar la suite general `mvn test` sobre el estado actual.
2. Evolucionar ingresos y transferencias mediante el núcleo común.
3. Ampliar pasivos y patrimonio neto.
4. Evolucionar análisis histórico, resúmenes, evolución patrimonial, vencimientos y dashboard.
5. Pulir posteriormente la salida de consola de la aplicación.
