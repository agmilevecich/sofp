# SOFP — Historial de Builds

## Build 059 — Suite general posterior a venta y posición

**Estado: COMPLETADO Y VALIDADO.**

Se ejecutó la suite general después de completar la cobertura de venta de activo, persistencia de relaciones e integración con posición.

Pruebas específicas previas:

- `OperacionFinancieraTest`: **17/17**.
- `OperacionFinancieraServiceTest`: **22/22**.
- `OperacionFinancieraCompraServiceTest`: **13/13**.
- `OperacionFinancieraVentaServiceTest`: **13/13**.
- `PosicionActivoServiceTest`: **4/4**.

Suite general ejecutada el **27/08/2026 15:24:11 -03:00**: **433/433**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`, duración **17:35 min**.

## Etapa — Seguridad y aislamiento por usuario

La rama `feature/seguridad-aislamiento-datos` completó la auditoría transversal y fue integrada en `main` mediante fast-forward.

Validación final registrada: `AislamientoDatosServiceTest` **7/7** y suite general **512/512**, `BUILD SUCCESS`.

## Fase 8 — Interfaz Swing

`feature/swing-shell` desarrolló progresivamente el shell Swing y su integración con cuentas, categorías, ingresos, movimientos, inversiones, reportes, gastos y obligaciones.

Componentes conectados incluyen `MainFrame`, `HeaderPanel`, `SidebarPanel`, `InicioPanel`, `CuentasPanel`, `CategoriasPanel`, `IngresosPanel`, `MovimientosPanel`, `GastosPanel`, `InversionesPanel`, `ReportesPanel`, `ObligacionesPanel`, `StatusBarPanel`, `RegistrarCuentaPanel`, `RegistrarMovimientoPanel` y `ui.Main`.

`MainFrame` utiliza `CardLayout` para Inicio, Cuentas, Categorías, Ingresos, Gastos, Movimientos, Inversiones, Reportes y Obligaciones.

## Bloque — Fondos insuficientes

**Estado: COMPLETADO Y VALIDADO.**

`MovimientoService` impide registrar un `EGRESO` superior al saldo disponible. Un egreso igual al saldo está permitido y deja saldo cero. La regla también se aplica a modificaciones de importe y tipo, excluyendo el movimiento actual cuando corresponde.

Producción: `5dd8372` — `fix: validar fondos disponibles en movimientos`.

Cobertura histórica: `MovimientoFondosInsuficientesTest` **6/6**, `MovimientoServiceTest` **57/57**, `RegistrarMovimientoPanelTest` **4/4**.

### Cobertura adicional de saldo — 07/09/2026

**Estado: COMPLETADO Y VALIDADO.**

`MovimientoServiceSaldoTest`: **3/3**.

## Bloque — Categorías con movimientos

**Estado: COMPLETADO Y VALIDADO.**

Una categoría referenciada por movimientos no se elimina físicamente. Se conserva el historial y se desactiva.

`CategoriaServiceTest`: **23/23**.

## Bloque — Gastos

**Estado: COMPLETADO Y VALIDADO — primer corte funcional.**

Flujo:

**Gastos → `GastoService` → `MovimientoService` → `Movimiento` `EGRESO` → `Movimientos`.**

El panel permite cuenta, categoría, importe, fecha, descripción y forma de pago. El registro se conserva en el historial común y mantiene la regla de fondos disponibles.

## Bloque — Ingresos

**Estado: COMPLETADO Y VALIDADO.**

Se incorporó `IngresosPanel` como panel especializado de carga de ingresos, conectado mediante:

**`IngresosPanel` → `IngresoService` → `MovimientoService` → `Movimiento` `INGRESO` → `Movimientos`.**

El formulario permite cuenta, categoría, importe, fecha y descripción, utilizando cuentas y categorías activas del perfil/usuario autorizado.

Commits:

- `d99cc6a` — `feat: agregar formulario de ingresos`.
- `2977f36` — `test: cubrir formulario de ingresos`.
- `4e6b363` — `feat: integrar ingresos al shell`.
- `cd781a1` — `feat: agregar ingresos a la navegacion`.
- `f9339db` — `test: cubrir navegacion hacia ingresos`.

Validaciones: **5/5** focalizada, **18/18** relacionada y **630/630** suite general.

## Bloque — FormaPago

**Estado: COMPLETADO Y VALIDADO.**

`FormaPago` quedó integrada a `Movimiento`, `MovimientoService`, `GastoService` y `GastosPanel`.

Formas disponibles: `EFECTIVO`, `TRANSFERENCIA`, `TARJETA_DEBITO`, `TARJETA_CREDITO` y `QR`.

La tarjeta de crédito permite crear una `Obligacion` mediante `ObligacionService` después de registrar el movimiento de egreso. Si no existe `ObligacionService`, la tarjeta de crédito se rechaza con `IllegalStateException`.

## Bloque — Obligaciones y pagos

**Estado: COMPLETADO EN DOMINIO, PERSISTENCIA, SERVICIO Y UI SWING; TESTS FOCALIZADOS VERDES.**

Se incorporaron:

- `EstadoObligacion` con estados `PENDIENTE`, `PARCIAL` y `PAGADA`.
- `Obligacion` con importe original, saldo pendiente, estado y relación con el movimiento de origen.
- `ObligacionRepository` y consulta por usuario.
- `ObligacionService` para alta, consulta y registro de pagos.
- autorización de pagos por usuario.
- `ObligacionesPanel` para consulta y registro de pagos.
- integración de navegación en `MainFrame`/`SidebarPanel`.
- `ObligacionTest`, `ObligacionJpaTest`, `ObligacionServiceTest`, `ObligacionesPanelTest` y pruebas de integración/navegación.

Las reglas del dominio incluyen rechazo de pagos no positivos, sobrepagos y pagos sobre obligaciones ya pagadas, además de transición automática a `PARCIAL` o `PAGADA`.

La primera ejecución focalizada del panel tuvo un fallo porque el refresco perdía la selección. `166b5f0` corrigió el comportamiento y la ejecución posterior quedó en **14/14**.

## Bloque — Compatibilidad del shell y expectativas de tests

**Estado: COMPLETADO Y VALIDADO.**

- `6c7d70a` — `fix: mantener compatibilidad de MainFrame sin ObligacionService`.
- `7f05cd1` — `test: actualizar expectativas de gastos con crédito`.

## Suite general — 08/09/2026 15:16:17

**Estado: COMPLETADO Y VALIDADO.**

Resultado informado por el usuario:

- Tests run: **630**.
- Failures: **0**.
- Errors: **0**.
- Skipped: **0**.
- `BUILD SUCCESS`.
- Duración: **11:55 min**.
- Comando: `mvn test`.

Esta es la suite general completa más reciente y valida la integración de Ingresos y su navegación, además de los bloques anteriores.

## Suite focalizada de Ingresos — 08/09/2026 14:55:35

**Estado: COMPLETADO Y VALIDADO.**

Comando:

`mvn -Dtest=IngresosPanelTest,MainFrameNavigationTest test`

Resultado: **5/5**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`, duración **01:21 min**.

## Suite relacionada de Ingresos — 08/09/2026 14:58:53

**Estado: COMPLETADO Y VALIDADO.**

Comando:

`mvn -Dtest=IngresoServiceTest,IngresosPanelTest,MainFrameNavigationTest,MainFrameObligacionesTest,ObligacionesPanelTest,GastosPanelTest test`

Resultado: **18/18**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`, duración **01:31 min**.

## Estado Git

`main` permanece en `a4be85913847200cb70976d5266d9cbba10b3100`.

La rama de trabajo es `feature/swing-shell`. El último cambio funcional del bloque de Ingresos es `f9339db2a500d5530eea95dc39e14e2725f4eb8f`.

La documentación posterior a este cambio se considera documental y no modifica el comportamiento funcional.

## Próximos bloques

1. Evolucionar transferencias mediante el núcleo común y `OperacionFinanciera`.
2. Incorporar progresivamente pasivos y patrimonio neto.
3. Evolucionar análisis, resúmenes, evolución patrimonial, vencimientos y dashboard.
4. Como pulido posterior, limpiar la salida de consola de la aplicación sin eliminar la posibilidad de diagnóstico.

Antes de cerrar cualquier bloque: tests específicos → tests relacionados → suite general cuando corresponda → `git diff` → `git diff --check` → `git status`.
