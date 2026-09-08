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

`feature/swing-shell` desarrolló progresivamente el shell Swing y su integración con cuentas, categorías, movimientos, inversiones, reportes, gastos y obligaciones.

Componentes conectados incluyen `MainFrame`, `HeaderPanel`, `SidebarPanel`, `InicioPanel`, `CuentasPanel`, `CategoriasPanel`, `MovimientosPanel`, `GastosPanel`, `InversionesPanel`, `ReportesPanel`, `ObligacionesPanel`, `StatusBarPanel`, `RegistrarCuentaPanel`, `RegistrarMovimientoPanel` y `ui.Main`.

`MainFrame` utiliza `CardLayout` para Inicio, Cuentas, Categorías, Gastos, Movimientos, Inversiones, Reportes y Obligaciones.

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

## Suite general — 08/09/2026 13:27:36

**Estado: COMPLETADO Y VALIDADO.**

Resultado informado por el usuario:

- Tests run: **618**.
- Failures: **0**.
- Errors: **0**.
- Skipped: **0**.
- `BUILD SUCCESS`.
- Duración: **21:26 min**.
- Comando: `mvn test`.

Esta suite es anterior a la UI de obligaciones.

## Suite focalizada — 08/09/2026 14:14:14

**Estado: COMPLETADO Y VALIDADO.**

Comando:

`mvn -Dtest=MainFrameNavigationTest,MainFrameObligacionesTest,ObligacionesPanelTest,ObligacionServiceTest test`

Resultado informado por el usuario: **14/14**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`, duración **01:48 min**.

Esta validación cubre el bloque de obligaciones/UI posterior a la suite general.

## Estado Git

`main` permanece en `a4be85913847200cb70976d5266d9cbba10b3100`.

La rama de trabajo es `feature/swing-shell`. El último commit funcional antes del bloque documental es `87052df953dbd282a43c5647d05b68d1854c4f17`.

Los commits documentales posteriores deben considerarse parte del estado actual de la rama.

## Próximos bloques

1. Ejecutar `mvn test` sobre el estado actual para validar la integración completa de obligaciones/UI.
2. Evolucionar ingresos y transferencias mediante el núcleo común.
3. Incorporar progresivamente pasivos y patrimonio neto.
4. Evolucionar análisis, resúmenes, evolución patrimonial, vencimientos y dashboard.
5. Como pulido posterior, limpiar la salida de consola de la aplicación sin eliminar la posibilidad de diagnóstico.

Antes de cerrar cualquier bloque: tests específicos → tests relacionados → suite general cuando corresponda → `git diff` → `git diff --check` → `git status`.
