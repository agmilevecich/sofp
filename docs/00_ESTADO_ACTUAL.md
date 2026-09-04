# SOFP — Estado actual

> Documento de continuidad. La fuente de verdad técnica es el código, los tests y los commits actuales; `docs/` es documentación auxiliar y puede quedar desactualizada.

## Estado verificado — 04/09/2026

**Rama estable:** `main` → `a4be859` (`docs: crear contexto de continuidad actualizado`).
**Rama de trabajo:** `feature/swing-shell` → `70c2455` (`test: cubrir registro de categoria sin nombre`).

La rama de trabajo está **186 commits por delante y 2 por detrás de `main`**, estado `diverged`. Merge base: `96f3d999`. Los dos commits exclusivos de `main` (`39badd1` y `a4be859`) son documentales y no se incorporan automáticamente.

## Estado funcional

La Fase 8 continúa sobre el shell Swing integrado con cuentas, categorías, movimientos, inversiones y reportes. Los cambios conceptuales derivados del análisis de `ControlFinanzas` se adoptaron como **criterios de diseño para los próximos bloques**, no como funcionalidades ya implementadas.

Criterio central acordado:

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`**.

Los paneles pueden representar gastos, ingresos, transferencias, inversiones, préstamos/deudas, pagos de tarjeta, historial y dashboard, pero deben converger en el mismo núcleo financiero sin duplicar reglas de negocio.

Se adoptó además el criterio de distinguir **cuenta** de **medio/forma de pago**. Las formas previstas son tarjeta de crédito, tarjeta de débito, QR, transferencia y efectivo. `FormaPago` deberá integrarse al modelo cuando corresponda, sin confundirlo con la cuenta afectada.

La tarjeta de crédito requiere tratamiento diferenciado: una compra puede generar una obligación/pasivo sin producir inmediatamente una salida de fondos de una cuenta bancaria.

El objetivo funcional de largo plazo es que SOFP pueda representar activos, pasivos y patrimonio neto, además de liquidez, ingresos, gastos, inversiones, préstamos y transferencias. Regla conceptual: `TOTAL ACTIVOS - TOTAL PASIVOS = PATRIMONIO NETO`.

Los préstamos otorgados deben representar un derecho de cobro: disminuyen la liquidez disponible pero no desaparecen del patrimonio.

Las transferencias entre cuentas propias no deben contabilizarse como ingreso ni gasto.

## Reglas de negocio pendientes detectadas

### Fondos insuficientes

Un `EGRESO` no debe poder registrarse si supera el saldo disponible de la cuenta. El importe igual al saldo disponible debe permitirse y dejar saldo cero. La validación corresponde al servicio/dominio financiero, no solamente a Swing.

También deberá revisarse la modificación de movimientos para impedir que cambiar importe o tipo de un movimiento produzca un saldo inválido.

### Categorías con movimientos

No debe eliminarse físicamente una categoría que ya esté referenciada por movimientos. La alternativa prevista es impedir la eliminación y mantener/desactivar la categoría para conservar el historial financiero, mostrando un mensaje amigable en la UI.

## Estado Swing

Implementados y conectados: `MainFrame`, `HeaderPanel`, `SidebarPanel`, `InicioPanel`, `CuentasPanel`, `CategoriasPanel`, `MovimientosPanel`, `InversionesPanel`, `ReportesPanel`, `StatusBarPanel`, `RegistrarCuentaPanel`, `RegistrarMovimientoPanel` y `ui.Main`.

`MainFrame` usa `CardLayout` para Inicio, Cuentas, Categorías, Movimientos, Inversiones y Reportes. La UI integra los servicios existentes y no duplica reglas de negocio.

El formulario de movimientos utiliza LGoodDatePicker para la fecha y obtiene automáticamente la hora mediante `LocalTime.now()` al registrar.

## Validación reciente

`CategoriasPanelTest`: **3/3 tests en verde**, `BUILD SUCCESS` (04/09/2026).

El último test agregado cubre el rechazo de registro de una categoría sin nombre mediante `IllegalArgumentException`; el cambio está en `70c2455`. citeturn59file0

La última suite general conocida continúa siendo **568/568 tests en verde**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`. No se debe atribuir una nueva ejecución general a la jornada actual sin que sea informada.

## Últimos cambios funcionales/test

- `70c2455` — `test: cubrir registro de categoria sin nombre`;
- `6eefc36` — `fix: manejar error al registrar categoria`;
- `b284368` — `test: cubrir representacion de categoria`;
- `22f0e81` — `feat: mostrar nombre de categoria en combobox`;
- `22920c6` — `fix: mostrar nombre al seleccionar perfil`;
- `1e6f851` — `feat: separar seleccion de perfil de dialogo Swing`;
- `33e392c` — `fix: usar panel de movimientos en shell`.

## Incidentes conocidos

Durante las pruebas Swing Surefire puede mostrar el mensaje posterior a `System.exit(0)`. En las ejecuciones registradas que terminaron en `BUILD SUCCESS` no se considera un fallo funcional.

## Próximo paso

Antes de implementar los criterios derivados de ControlFinanzas, reconstruir nuevamente el código real y revisar `Movimiento`, `MovimientoService`, `CuentaService`, `CategoriaService`, repositorios, `FormaPago`/modelado relacionado y tests. El próximo bloque debe comenzar por una funcionalidad concreta y pequeña, con sus reglas de negocio y pruebas correspondientes.

No se realizó merge a `main` y no se crean ramas nuevas.
