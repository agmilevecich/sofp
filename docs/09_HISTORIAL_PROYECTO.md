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
20. Implementación de `TransferenciasPanel` sobre `OperacionFinancieraService`.
21. Integración de Transferencias en el shell y cobertura de navegación.
22. Validación específica de Transferencias con **4/4 tests**.
23. Validación de Transferencias y navegación con **5/5 tests**.
24. Validación general posterior a Transferencias con **634/634 tests**.

## Transferencias

El formulario `TransferenciasPanel` quedó integrado al shell Swing y utiliza `OperacionFinancieraService` para registrar transferencias entre cuentas propias.

El flujo es:

**`TransferenciasPanel` → `OperacionFinancieraService` → `OperacionFinanciera` + `Movimiento` `EGRESO`/`INGRESO`.**

Una transferencia genera un egreso en la cuenta origen y un ingreso en la cuenta destino, manteniendo la operación diferenciada de ingresos y gastos.

Commits:

- `1753074` — `feat: agregar formulario de transferencias`.
- `aa29d44` — `test: cubrir formulario de transferencias`.

## Ingresos

El flujo quedó integrado mediante:

**`IngresosPanel` → `IngresoService` → `MovimientoService` → `Movimiento` `INGRESO` → `Movimientos`.**

## Gastos, FormaPago y Obligaciones

El flujo de Gastos continúa sobre `Movimiento` como núcleo financiero. `FormaPago` está integrada con las cinco opciones vigentes. Las compras con tarjeta de crédito generan una obligación mediante `ObligacionService`; las obligaciones admiten pagos autorizados por usuario y cuentan con UI Swing.

## Reglas de saldo

`MovimientoService` rechaza egresos superiores al saldo, permite egresos exactamente iguales al saldo y aplica la regla a modificaciones de importe y tipo.

## Shell Swing

La Fase 8 integra Inicio, Cuentas, Categorías, Ingresos, Gastos, Movimientos, Inversiones, Reportes, Obligaciones y Transferencias mediante `MainFrame`, `SidebarPanel` y `CardLayout`.

## Validación

### Suite general más reciente

El usuario ejecutó `mvn test` el **08/09/2026 18:13:55 -03:00**.

**634 tests, 0 failures, 0 errors, 0 skipped, BUILD SUCCESS.**

Duración: **11:52 min**.

Esta ejecución es la validación general posterior a la incorporación de Transferencias.

### Suite focalizada de Transferencias

El usuario ejecutó el **08/09/2026 17:59:29 -03:00**:

`mvn -Dtest=TransferenciasPanelTest test`

**4 tests, 0 failures, 0 errors, 0 skipped, BUILD SUCCESS.**

Duración: **01:16 min**.

### Suite Transferencias + navegación

El usuario ejecutó el **08/09/2026 18:01:19 -03:00**:

`mvn -Dtest=TransferenciasPanelTest,MainFrameNavigationTest test`

**5 tests, 0 failures, 0 errors, 0 skipped, BUILD SUCCESS.**

Duración: **39 s**.

## Estado Git

`main`: `a4be85913847200cb70976d5266d9cbba10b3100`.

La rama de trabajo es `feature/swing-shell`. El último cambio funcional es `aa29d44` — `test: cubrir formulario de transferencias`.

La comparación verificada antes de actualizar esta documentación indicó **377 commits adelante y 0 atrás** respecto de `main`.

Los commits posteriores a `aa29d44` corresponden exclusivamente a documentación.

## Criterios permanentes

ControlFinanzas es referencia funcional, no arquitectura para copiar.

La UI no duplica reglas de negocio.

Los paneles especializados convergen en el núcleo financiero basado en `Movimiento`.

Las transferencias propias se modelan mediante `OperacionFinanciera`.

Los tests son condición de cierre.

No hacer merge a `main` automáticamente ni crear ramas nuevas salvo indicación explícita.

## Próximos hitos

1. Ampliar pasivos y patrimonio neto.
2. Evolucionar análisis histórico, resúmenes, evolución patrimonial, vencimientos y dashboard.
3. Pulir posteriormente la salida de consola de la aplicación.
