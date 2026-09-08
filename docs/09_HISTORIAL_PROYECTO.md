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
15. Implementación del formulario de Ingresos sobre el núcleo financiero común.
16. Integración de Ingresos en `MainFrame` y `SidebarPanel`.
17. Cobertura de formulario y navegación de Ingresos.
18. Validación relacionada de Ingresos con **18/18 tests**.
19. Validación general posterior a Ingresos con **630/630 tests**.

## Gastos

El flujo de Gastos quedó integrado mediante:

**`GastosPanel` → `GastoService` → `MovimientoService` → `Movimiento` `EGRESO` → `Movimientos`.**

La arquitectura mantiene una única fuente de verdad financiera.

## Ingresos

El flujo de Ingresos quedó integrado mediante:

**`IngresosPanel` → `IngresoService` → `MovimientoService` → `Movimiento` `INGRESO` → `Movimientos`.**

El formulario permite seleccionar cuenta y categoría activas, ingresar importe, fecha y descripción, y registrar el ingreso utilizando el usuario autorizado.

Commits del bloque:

- `d99cc6a` — `feat: agregar formulario de ingresos`.
- `2977f36` — `test: cubrir formulario de ingresos`.
- `4e6b363` — `feat: integrar ingresos al shell`.
- `cd781a1` — `feat: agregar ingresos a la navegacion`.
- `f9339db` — `test: cubrir navegacion hacia ingresos`.

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

`ObligacionesPanel` está integrado en el shell y permite consultar obligaciones, mostrar importe original/saldo/estado/fecha, seleccionar una obligación, registrar un pago autorizado, refrescar el estado y conservar la selección al refrescar.

`MainFrame`/`SidebarPanel` incorporan la navegación hacia Obligaciones.

## Reglas de saldo

El bloque de reglas de fondos quedó ampliado y validado.

`MovimientoServiceSaldoTest` cubre rechazo de egreso superior al saldo, aceptación de egreso exactamente igual al saldo y aumento de un egreso hasta el saldo disponible.

## Shell Swing

La Fase 8 integra Inicio, Cuentas, Categorías, Ingresos, Gastos, Movimientos, Inversiones, Reportes y Obligaciones mediante `MainFrame` y `CardLayout`.

Los ajustes visuales realizados anteriormente fueron:

- `5faff68` — mejora de layout de `CuentasPanel`.
- `5310ba3` — mejora de layout de `MovimientosPanel`.
- `26f7f58` — mejora de layout de `CategoriasPanel`.

## Validación

### Suite general más reciente

El usuario ejecutó `mvn test` el **08/09/2026 15:16:17 -03:00**.

**630 tests, 0 failures, 0 errors, 0 skipped, BUILD SUCCESS.**

Duración: **11:55 min**.

Esta ejecución es la validación general posterior a la incorporación de Ingresos.

### Suite focalizada de Ingresos/navegación

El usuario ejecutó el **08/09/2026 14:55:35 -03:00**:

`mvn -Dtest=IngresosPanelTest,MainFrameNavigationTest test`

**5 tests, 0 failures, 0 errors, 0 skipped, BUILD SUCCESS.**

Duración: **01:21 min**.

### Suite relacionada

El usuario ejecutó el **08/09/2026 14:58:53 -03:00**:

`mvn -Dtest=IngresoServiceTest,IngresosPanelTest,MainFrameNavigationTest,MainFrameObligacionesTest,ObligacionesPanelTest,GastosPanelTest test`

**18 tests, 0 failures, 0 errors, 0 skipped, BUILD SUCCESS.**

Duración: **01:31 min**.

## Estado Git

`main`: `a4be85913847200cb70976d5266d9cbba10b3100`.

La rama de trabajo es `feature/swing-shell`. El último cambio funcional verificado es `f9339db2a500d5530eea95dc39e14e2725f4eb8f` — `test: cubrir navegacion hacia ingresos`.

La rama está 366 commits adelante y 0 atrás respecto de `main` según la comparación verificada en GitHub. No se realizó merge a `main`.

Los commits documentales posteriores actualizan el estado de continuidad y no agregan comportamiento funcional.

## Criterios permanentes

ControlFinanzas es referencia funcional, no arquitectura para copiar.

La UI no duplica reglas de negocio.

Los paneles especializados convergen en el núcleo financiero basado en `Movimiento`.

Los tests son condición de cierre.

No considerar implementada una funcionalidad solamente por estar documentada.

No hacer merge a `main` automáticamente ni crear ramas nuevas salvo indicación explícita.

## Próximos hitos

1. Evolucionar transferencias mediante `OperacionFinanciera`, manteniéndolas diferenciadas de ingresos y gastos.
2. Ampliar pasivos y patrimonio neto.
3. Evolucionar análisis histórico, resúmenes, evolución patrimonial, vencimientos y dashboard.
4. Pulir posteriormente la salida de consola de la aplicación.
