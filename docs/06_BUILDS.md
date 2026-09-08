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

Cobertura histórica: `MovimientoFondosInsuficientesTest` **6/6**, `MovimientoServiceTest` **57/57**, `RegistrarMovimientoPanelTest` **4/4**.

### Cobertura adicional de saldo — 07/09/2026

**Estado: COMPLETADO Y VALIDADO.**

Se agregó `MovimientoServiceSaldoTest` para cubrir explícitamente tres reglas mediante la API pública del servicio:

- rechazo de egreso superior al saldo;
- aceptación de egreso exactamente igual al saldo;
- aumento de importe de un egreso hasta el saldo disponible.

`MovimientoServiceSaldoTest`: **3/3**.

La prueba se corrigió en `06a9fd8` porque el fixture inicial utilizaba el overload interno de `registrar`, que no aplica la validación pública de saldo. No fue necesario modificar producción.

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

La tarjeta de crédito ya no está limitada por ausencia de modelo: `GastoService` puede crear una `Obligacion` mediante `ObligacionService` después de registrar el movimiento de egreso. Si no existe `ObligacionService`, la tarjeta de crédito se rechaza con `IllegalStateException`.

## Bloque — Obligaciones y pagos

**Estado: COMPLETADO EN DOMINIO, PERSISTENCIA Y SERVICIO; UI PENDIENTE.**

Se incorporaron:

- `EstadoObligacion` con estados `PENDIENTE`, `PARCIAL` y `PAGADA`.
- `Obligacion` con importe original, saldo pendiente, estado y relación uno a uno con el movimiento de origen.
- `ObligacionRepository`.
- `ObligacionService` para alta, consulta y registro de pagos.
- `ObligacionTest`, `ObligacionJpaTest` y `ObligacionServiceTest`.

Las reglas del dominio incluyen rechazo de pagos no positivos, sobrepagos y pagos sobre obligaciones ya pagadas, además de transición automática a `PARCIAL` o `PAGADA`.

La siguiente evolución pendiente es llevar estas capacidades a una interfaz Swing específica.

## Bloque — Compatibilidad del shell y expectativas de tests

**Estado: COMPLETADO Y VALIDADO.**

- `6c7d70a` — `fix: mantener compatibilidad de MainFrame sin ObligacionService`.
- `7f05cd1` — `test: actualizar expectativas de gastos con crédito`.

La compatibilidad mantiene operativos constructores anteriores de `MainFrame` sin `ObligacionService`. El test de tarjeta de crédito sin servicio fue alineado con la excepción de estado vigente.

## Suite general — 08/09/2026 13:27:36

**Estado: COMPLETADO Y VALIDADO.**

Resultado informado por el usuario:

- Tests run: **618**.
- Failures: **0**.
- Errors: **0**.
- Skipped: **0**.
- `BUILD SUCCESS`.
- Duración: **21:26 min**.
- Finalización: **08/09/2026 13:27:36 -03:00**.
- Comando: `mvn test`.

Antes de la suite general, `GastosPanelTest` + `MainFrameMovimientosTest` quedaron en **8/8**.

La suite general había fallado previamente con 1 failure y 2 errors por incompatibilidad de expectativas y constructores; las correcciones `6c7d70a` y `7f05cd1` resolvieron esos problemas.

## Validación final local — 08/09/2026

El usuario informó:

- `git diff`: limpio.
- `git diff --check`: sin errores.
- `git status`: working tree limpio.
- Rama local sincronizada con `github/feature/swing-shell`.

## Estado Git vigente

`main` permanece en `a4be85913847200cb70976d5266d9cbba10b3100`.

`feature/swing-shell` estaba en `7f05cd1ef48576e0bc59a3828cd254e0bd4a9d9b` al iniciar la actualización documental y se encuentra **326 commits por delante y 0 por detrás** de `main`, con merge-base `a4be859`.

La actualización de esta documentación generará commits posteriores y debe verificarse nuevamente el SHA final al cerrar el bloque documental.

## Próximos bloques

1. Crear integración Swing para consultar obligaciones y registrar pagos.
2. Evolucionar ingresos y transferencias mediante el núcleo común.
3. Incorporar progresivamente pasivos y patrimonio neto.
4. Evolucionar análisis, resúmenes, evolución patrimonial, vencimientos y dashboard.
5. Como pulido posterior, limpiar la salida de consola de la aplicación sin eliminar la posibilidad de diagnóstico.

Antes de cerrar cualquier bloque: tests específicos → tests relacionados → suite general cuando corresponda → `git diff` → `git diff --check` → `git status`.
