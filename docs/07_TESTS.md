# SOFP — Tests

## Última validación general

La última suite general ejecutada localmente fue el **01/09/2026** y finalizó con:

- Tests run: **529**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **14:25 min**
- Finalización: **19:25:53 -03:00**

La validación global vigente es **529/529 tests en verde**.

## Cobertura de seguridad

La feature `feature/seguridad-aislamiento-datos` cubre autorización por propietario en perfiles, cuentas, categorías, movimientos, operaciones financieras y posiciones/cartera.

Se cubren lecturas propias, rechazo de recursos ajenos, listados contextualizados, cálculo de saldo/evolución, altas protegidas y caminos alternativos de creación/acceso.

Test transversal: `AislamientoDatosServiceTest`.

Validación específica de seguridad: **7/7 tests en verde**.

## Cobertura del shell Swing

La fase de interfaz cubre:

- estructura y layout de `MainFrame`;
- header, sidebar, área central y barra de estado;
- navegación entre Inicio, Cuentas, Movimientos, Inversiones y Reportes;
- cuentas filtradas por perfil/usuario;
- conservación de la cuenta seleccionada;
- movimientos de la cuenta seleccionada filtrados por usuario;
- posiciones de inversión filtradas por usuario/perfil;
- reporte de movimientos de inversión;
- punto de entrada `ui.Main`;
- alta de movimientos desde Swing;
- persistencia del movimiento registrado;
- notificación para refrescar el listado.

Tests relacionados:

- `MainFrameTest`;
- `MainFrameLayoutTest`;
- `MainFrameNavigationTest`;
- `MainFrameMovimientosTest`;
- `MainFrameInversionesTest`;
- `MainFrameReportesTest`;
- `CuentasPanelTest`;
- `MovimientosPanelTest`;
- `InversionesPanelTest`;
- `ReportesPanelTest`;
- `RegistrarMovimientoPanelTest`.

## Validación de reportes

- `ReportesPanelTest`: **3/3 tests en verde**;
- `MainFrameReportesTest`: **1/1 test en verde**;
- total reportes: **4/4**;
- suite relacionada anterior de UI: **13/13**.

## Validación del alta de movimientos

Suite ejecutada localmente el **02/09/2026**:

`mvn -Dtest=RegistrarMovimientoPanelTest,MovimientosPanelTest,MainFrameMovimientosTest,MainFrameNavigationTest test`

Resultado:

- Tests run: **10**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **04:38 min**
- Finalización: **12:26:39 -03:00**

`RegistrarMovimientoPanelTest`: **4/4 tests en verde**.

La prueba `deberiaRegistrarMovimientoYNotificarAlContenedor` verifica la registración real de un movimiento, persistencia en H2, importe, fecha/hora, descripción y ejecución del callback de actualización.

`MovimientosPanelTest` verifica además la recarga del listado después de incorporar un segundo movimiento.

## Incidentes de entorno de compilación

Una ejecución anterior intentó ejecutar un `SwingApplicationTest` compilado previamente aunque el test ya no formaba parte de los archivos versionados. El fallo correspondía a artefactos obsoletos en `target`.

Se limpió el proyecto con Maven y la suite general quedó en **529/529 tests en verde**.

Durante las ejecuciones de UI Surefire mostró un mensaje indicando que esperaba más de 30 segundos después de `System.exit(0)`. Las ejecuciones terminaron con `BUILD SUCCESS`, sin failures ni errors. No se modificó código especulativamente por ese mensaje.

## Cierre

El bloque de alta de movimientos desde Swing está validado dentro de su alcance con **10/10 tests verdes**. La última validación global disponible sigue siendo **529/529 tests verdes**.
