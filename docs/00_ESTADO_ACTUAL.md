# SOFP — Estado actual

> Documento de continuidad. La fuente de verdad técnica es el código, los tests y los commits actuales; `docs/` es documentación auxiliar y puede quedar desactualizada.

## Estado verificado — 05/09/2026

**Rama estable:** `main` → `a4be859`.
**Rama de trabajo:** `feature/swing-shell` → `a301276`.

Último commit: `a301276` — `docs: consolidar continuidad del proyecto`.
El último cambio funcional/test sigue siendo `85b767c` — `test: aislar persistencia en CategoriaServiceTest`.

La rama de trabajo continúa divergida respecto de `main`. No se realizó merge a `main`.

## Estado funcional

La Fase 8 continúa sobre el shell Swing integrado con cuentas, categorías, movimientos, inversiones y reportes. Los cambios conceptuales derivados del análisis de `ControlFinanzas` se adoptaron como criterios de diseño para los próximos bloques, no como funcionalidades ya implementadas.

Criterio central acordado:

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

Los paneles pueden representar gastos, ingresos, transferencias, inversiones, préstamos/deudas, pagos de tarjeta, historial y dashboard, pero deben converger en el mismo núcleo financiero sin duplicar reglas de negocio.

Se adoptó además el criterio de distinguir **Cuenta** de **medio/forma de pago**. Las formas previstas son tarjeta de crédito, tarjeta de débito, QR, transferencia y efectivo. `FormaPago` deberá integrarse al modelo cuando corresponda, sin confundirlo con la cuenta afectada.

La tarjeta de crédito requiere tratamiento diferenciado: una compra puede generar una obligación/pasivo sin producir inmediatamente una salida de fondos de una cuenta bancaria.

El objetivo funcional de largo plazo es que SOFP pueda representar activos, pasivos y patrimonio neto, además de liquidez, ingresos, gastos, inversiones, préstamos y transferencias. Regla conceptual: `TOTAL ACTIVOS - TOTAL PASIVOS = PATRIMONIO NETO`.

Los préstamos otorgados deben representar un derecho de cobro: disminuyen la liquidez disponible pero no desaparecen del patrimonio.

Las transferencias entre cuentas propias no deben contabilizarse como ingreso ni gasto.

## Regla implementada — Fondos insuficientes

**Estado: completada y validada.**

Un `EGRESO` no puede superar el saldo disponible de la cuenta. Un egreso exactamente igual al saldo disponible está permitido y deja saldo cero.

La regla está implementada en `MovimientoService` y se aplica al registro público de movimientos y a las modificaciones de importe y tipo que puedan producir un saldo inválido. Al modificar un movimiento, el cálculo excluye correctamente el movimiento actual.

Producción: `5dd8372` — `fix: validar fondos disponibles en movimientos`.

## Estado Swing

Implementados y conectados: `MainFrame`, `HeaderPanel`, `SidebarPanel`, `InicioPanel`, `CuentasPanel`, `CategoriasPanel`, `MovimientosPanel`, `InversionesPanel`, `ReportesPanel`, `StatusBarPanel`, `RegistrarCuentaPanel`, `RegistrarMovimientoPanel` y `ui.Main`.

`MainFrame` usa `CardLayout` para Inicio, Cuentas, Categorías, Movimientos, Inversiones y Reportes. La UI integra los servicios existentes y no duplica reglas de negocio.

El formulario de movimientos utiliza LGoodDatePicker para la fecha y obtiene automáticamente la hora mediante `LocalTime.now()` al registrar.

## Validación vigente — 05/09/2026

Pruebas relevantes:

- `MovimientoFondosInsuficientesTest`: 6/6;
- `MovimientoServiceTest`: 57/57;
- `RegistrarMovimientoPanelTest`: 4/4;
- `RegistrarCuentaPanelTest`: 6/6;
- `CategoriaServiceTest`: **23/23**;
- suite general: **580/580**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`.

Comando de suite: `mvn clean test`.
Duración: **10:58 min**.
Finalización: **05/09/2026 09:49:58 -03:00**.

## Últimos cambios funcionales/test

- `85b767c` — `test: aislar persistencia en CategoriaServiceTest`;
- `3cffeb9` — `test: completar datos requeridos en alta de cuenta`;
- `2b2bf3e` — `test: adaptar cambio de tipo a fondos disponibles`;
- `805e9fa` — `test: corregir comparacion de saldo en movimiento`;
- `5dd8372` — `fix: validar fondos disponibles en movimientos`.

## Regla de negocio — Categorías con movimientos

No debe eliminarse físicamente una categoría que ya esté referenciada por movimientos. Debe conservarse el historial financiero mediante desactivación y la UI debe informar la situación de forma comprensible.

La regla está implementada y validada en `CategoriaServiceTest`; la prueba relevante verifica que la categoría se desactiva y que el movimiento conserva su referencia.

## Próximo paso

Antes de implementar el siguiente bloque, reconstruir nuevamente el código real y revisar `FormaPago`, las relaciones con `Movimiento`, `Cuenta` y los servicios/tests relacionados.

No se realizó merge a `main` y no se crean ramas nuevas salvo indicación explícita.
