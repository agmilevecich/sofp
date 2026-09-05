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

`feature/swing-shell` desarrolló progresivamente el shell Swing y su integración con cuentas, categorías, movimientos, inversiones, reportes y gastos.

Componentes conectados: `MainFrame`, `HeaderPanel`, `SidebarPanel`, `InicioPanel`, `CuentasPanel`, `CategoriasPanel`, `MovimientosPanel`, `GastosPanel`, `InversionesPanel`, `ReportesPanel`, `StatusBarPanel`, `RegistrarCuentaPanel`, `RegistrarMovimientoPanel` y `ui.Main`.

`MainFrame` utiliza `CardLayout` para Inicio, Cuentas, Categorías, Gastos, Movimientos, Inversiones y Reportes.

## Bloque — Fondos insuficientes

**Estado: COMPLETADO Y VALIDADO.**

`MovimientoService` impide registrar un `EGRESO` superior al saldo disponible. Un egreso igual al saldo está permitido y deja saldo cero. La regla también se aplica a modificaciones de importe y tipo, excluyendo el movimiento actual cuando corresponde.

Producción: `5dd8372` — `fix: validar fondos disponibles en movimientos`.

Cobertura: `MovimientoFondosInsuficientesTest` **6/6**, `MovimientoServiceTest` **57/57**, `RegistrarMovimientoPanelTest` **4/4**.

## Bloque — Categorías con movimientos

**Estado: COMPLETADO Y VALIDADO.**

Una categoría referenciada por movimientos no se elimina físicamente. Se conserva el historial y se desactiva. La UI informa la situación de forma amigable.

`CategoriaServiceTest`: **23/23**.

La corrección de aislamiento de persistencia quedó registrada en `85b767c`.

## Bloque — Gastos

**Estado: COMPLETADO Y VALIDADO — primer corte funcional.**

Flujo:

**Gastos → `GastoService` → `MovimientoService` → `Movimiento` `EGRESO` → `Movimientos`.**

El panel permite cuenta, categoría, importe, fecha, descripción y posteriormente forma de pago. El registro se conserva en el historial común y mantiene la regla de fondos disponibles.

El fixture de prueba se ajustó en `98dead73` agregando un ingreso previo de $1.000 antes del gasto de $100.

## Bloque — FormaPago

**Estado: COMPLETADO Y VALIDADO.**

`FormaPago` quedó integrada a `Movimiento`, `MovimientoService`, `GastoService` y `GastosPanel`.

Formas disponibles: `EFECTIVO`, `TRANSFERENCIA`, `TARJETA_DEBITO`, `TARJETA_CREDITO` y `QR`.

`GastoService` exige forma de pago. `TARJETA_CREDITO` se rechaza temporalmente porque requiere un modelo de obligaciones/pasivos y no debe simular una salida inmediata de fondos.

Cobertura agregada en dominio y UI para selección, persistencia, modificación y rechazo de tarjeta de crédito.

## Suite general — 05/09/2026 13:04:09

Resultado informado por el usuario:

- Tests run: **590**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **11:29 min**
- Finalización: **05/09/2026 13:04:09 -03:00**

Este es el último resultado de tests conocido y valida el estado actual de la rama `feature/swing-shell`.

## Estado Git vigente

`main` permanece en `a4be85913847200cb70976d5266d9cbba10b3100`.

`feature/swing-shell` está en `11c189af1a4f8670f66e321cc033ab94e0139366` al inicio de esta actualización documental y continúa sin merge a `main`.

La comparación verificada es **274 commits por delante y 2 por detrás**.

## Próximos bloques

1. Modelar obligaciones/pasivos para tarjeta de crédito.
2. Evolucionar ingresos y transferencias mediante el núcleo común.
3. Incorporar progresivamente pasivos y patrimonio neto.
4. Evolucionar análisis, resúmenes, evolución patrimonial, vencimientos y dashboard.

Antes de cerrar cualquier bloque: tests específicos → tests relacionados → suite general cuando corresponda → `git diff` → `git diff --check` → `git status`.
