# SOFP — Historial de Builds

## Build 059 — Suite general posterior a venta y posición

**Estado: COMPLETADO Y VALIDADO.**

Se ejecutó la suite general después de completar la cobertura de venta de activo, persistencia de relaciones e integración con posición.

Pruebas específicas previas:

- `OperacionFinancieraTest`: **17/17 tests en verde**.
- `OperacionFinancieraServiceTest`: **22/22 tests en verde**.
- `OperacionFinancieraCompraServiceTest`: **13/13 tests en verde**.
- `OperacionFinancieraVentaServiceTest`: **13/13 tests en verde**.
- `PosicionActivoServiceTest`: **4/4 tests en verde**.

Suite general ejecutada el **27/08/2026 15:24:11 -03:00**:

- Tests run: **433**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **17:35 min**

## Etapa — Seguridad y aislamiento por usuario

La rama `feature/seguridad-aislamiento-datos` completó la auditoría transversal y fue integrada en `main` mediante fast-forward.

Se implementaron y validaron autorizaciones por usuario/propietario para perfiles, cuentas, categorías, movimientos, posiciones/cartera y operaciones financieras, además del cierre de caminos internos que podían saltar las validaciones públicas.

## Validación final de seguridad

El **31/08/2026** se ejecutó `AislamientoDatosServiceTest`: **7/7 tests en verde**.

Luego se ejecutó la suite general:

- Tests run: **512**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **15:25 min**

## Fase 8 — Interfaz de usuario Swing

La rama `feature/swing-shell` desarrolló progresivamente el shell Swing y su integración con cuentas, movimientos, inversiones y reportes.

Se incorporaron y conectaron `MainFrame`, `HeaderPanel`, `SidebarPanel`, `InicioPanel`, `CuentasPanel`, `MovimientosPanel`, `InversionesPanel`, `ReportesPanel`, `StatusBarPanel` y `ui.Main`.

La integración mantiene las reglas de negocio en los servicios existentes y pasa el contexto de usuario/perfil a la UI.

## Bloque de reportes de inversiones

`ReportesPanel` utiliza la funcionalidad de reporte existente en `CarteraActivoService`. Fue integrado al `MainFrame` y la navegación quedó conectada a la tarjeta `REPORTES`, sin duplicar lógica de negocio.

Validación específica:

- `ReportesPanelTest`: **3/3 tests en verde**;
- `MainFrameReportesTest`: **1/1 test en verde**;
- total: **4/4 tests en verde**;
- suite relacionada de UI: **13/13 tests en verde**.

## Bloque de alta de movimientos desde Swing

Se agregó `RegistrarMovimientoPanel` al flujo de movimientos de una cuenta. El formulario utiliza categoría autorizada y activa, tipo, importe, fecha/hora, descripción y `usuarioId` de contexto.

`MovimientosPanel` recibe un callback después de un alta exitosa y actualiza el listado. Se separó `registrarMovimiento()` de la interacción con diálogos para permitir pruebas de persistencia e integración.

Commits principales:

- `39d96be` — `feat: agregar formulario de alta de movimientos`;
- `62b8e2a` — `fix: corregir tipos del formulario de movimientos`;
- `76389ca` — `feat: integrar alta de movimientos`;
- `1757b60` — `fix: conservar alta de movimientos sin depender del listado`;
- `978288f` — `feat: conectar alta de movimientos al shell`;
- `7b67880` — `fix: corregir constructor base del shell`;
- `deaae7d` — `refactor: separar alta de movimientos de dialogos`;
- `e8c9c50` — `test: cubrir alta real de movimientos desde formulario`.

## Bloque de alta de cuentas desde Swing

Se incorporó `RegistrarCuentaPanel` a `CuentasPanel` y al `MainFrame` contextual. El formulario permite seleccionar tipo de cuenta, institución financiera, moneda e identificador externo y delega la operación a `CuentaService.registrar(cuenta, usuarioId)`.

`CuentaService` quedó preparado para iniciar y cerrar su propia transacción cuando no existe una activa, sin interferir con los tests que ya ejecutan dentro de una transacción existente.

`CuentasPanel` refresca el listado mediante callback después de una registración exitosa. `MainFrame` conserva el constructor histórico basado en `perfilFinancieroId` para el listado y utiliza el constructor contextual completo cuando dispone del perfil y servicios auxiliares.

Commits recientes del bloque:

- `d5674aa` — `feat: agregar formulario de alta de cuentas`;
- `34d4d7d` — `fix: completar transaccion de alta de cuentas`;
- `19b2988` — `fix: permitir alta de cuentas dentro de transaccion existente`;
- `731e520` — `feat: integrar alta de cuentas en el panel`;
- `76cc4b0` — `fix: corregir contexto del panel de cuentas`;
- `a8ae7f9` — `feat: conectar alta de cuentas al shell`;
- `c5d9098` — `test: cubrir alta de cuentas desde el shell`;
- `0919a8e` — `fix: corregir orden de servicios en MainFrame`;
- `0d7769e` — `fix: conservar listado de cuentas con perfil id`.

## Validación específica más reciente

Suite ejecutada localmente el **02/09/2026** después de la corrección de integración de cuentas:

`mvn -Dtest=RegistrarCuentaPanelTest,CuentasPanelTest,MainFrameMovimientosTest test`

Resultado:

- Tests run: **11**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **05:24 min**
- Finalización: **14:05:26 -03:00**

Detalle:

- `RegistrarCuentaPanelTest`: **5/5**;
- `CuentasPanelTest`: **3/3**;
- `MainFrameMovimientosTest`: **3/3**.

La ejecución confirma alta real de cuentas, persistencia, callback de refresco y compatibilidad del constructor histórico de `MainFrame` basado en `perfilFinancieroId`.

## Validación general vigente

La última suite general sigue siendo la ejecutada el **01/09/2026**:

- Tests run: **529**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **14:25 min**
- Finalización: **19:25:53 -03:00**

Por lo tanto, **529/529** es la última validación global disponible. La suite específica de 11/11 del 02/09 valida los cambios posteriores, pero no sustituye la suite general.

Durante las ejecuciones de UI Surefire mostró el mensaje conocido de espera posterior a `System.exit(0)`, pero las ejecuciones terminaron con `BUILD SUCCESS`, sin failures ni errors. No se realizó ningún cambio especulativo por ese mensaje.

## Estado actual

`main` permanece como rama estable en `75d0a18` — `docs: actualizar contexto tras cierre de seguridad`.

`feature/swing-shell` es la rama activa y su último commit actual es `0d7769e` — `fix: conservar listado de cuentas con perfil id`.

La comparación verificada es **90 commits por delante y 2 commits por detrás de `main`**.

## Próximo paso

El bloque de alta de cuentas queda validado dentro de su alcance. El siguiente trabajo debe definirse como un nuevo bloque funcional de Fase 8, revisando nuevamente el estado real del código, servicios, repositorios, reglas de negocio y tests.

No se realiza merge automático a `main` y no se crean ramas nuevas.
