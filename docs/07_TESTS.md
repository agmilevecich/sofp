# SOFP — Tests

## Última validación general

La última suite general ejecutada localmente fue el **01/09/2026**:

- Tests run: **529**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **14:25 min**
- Finalización: **19:25:53 -03:00**

La validación global disponible sigue siendo **529/529 tests en verde**. No se ejecutó una nueva suite general después de los cambios posteriores.

## Validación específica más reciente

El **02/09/2026** se ejecutó:

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

La ejecución confirmó alta real de cuentas, persistencia, refresco mediante callback y compatibilidad del constructor histórico de `MainFrame` basado en `perfilFinancieroId`.

## Cobertura de seguridad

La feature `feature/seguridad-aislamiento-datos` cubre autorización por propietario en perfiles, cuentas, categorías, movimientos, operaciones financieras y posiciones/cartera.

Validación específica de seguridad: `AislamientoDatosServiceTest`, **7/7 tests en verde**.

La suite general posterior a seguridad fue de **512/512 tests en verde**.

## Cobertura del shell Swing

La fase de interfaz cubre:

- estructura y layout de `MainFrame`;
- header, sidebar, área central y barra de estado;
- navegación entre Inicio, Cuentas, Movimientos, Inversiones y Reportes;
- cuentas filtradas por perfil/usuario;
- alta de cuentas desde Swing;
- conservación de la cuenta seleccionada;
- movimientos de la cuenta seleccionada filtrados por usuario;
- alta de movimientos desde Swing;
- posiciones de inversión filtradas por usuario/perfil;
- reporte de movimientos de inversión;
- punto de entrada `ui.Main`;
- persistencia y callbacks de actualización en los formularios de alta.

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
- `RegistrarCuentaPanelTest`;
- `RegistrarMovimientoPanelTest`.

## Validación de reportes

- `ReportesPanelTest`: **3/3 tests en verde**;
- `MainFrameReportesTest`: **1/1 test en verde**;
- total reportes: **4/4**;
- suite relacionada anterior de UI: **13/13**.

## Validación del alta de movimientos

La suite específica del bloque de movimientos ejecutada el **02/09/2026** antes del bloque de cuentas fue:

`mvn -Dtest=RegistrarMovimientoPanelTest,MovimientosPanelTest,MainFrameMovimientosTest,MainFrameNavigationTest test`

- **10/10 tests en verde**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración: **04:38 min**;
- finalización: **12:26:39 -03:00**.

`RegistrarMovimientoPanelTest`: **4/4**.

## Validación del alta de cuentas

`RegistrarCuentaPanelTest` cubre la construcción del formulario, dependencias obligatorias, instituciones activas, monedas, alta real, persistencia y tratamiento del identificador externo vacío.

`CuentasPanelTest` cubre listado autorizado, integración del formulario, refresco automático y rechazo de perfil ajeno.

`MainFrameMovimientosTest` cubre la compatibilidad del shell con la construcción histórica basada en `perfilFinancieroId` y la integración contextual de cuentas.

La ejecución más reciente de **11/11** confirmó todo lo anterior sin fallos.

## Incidentes de entorno

Una ejecución anterior intentó ejecutar un `SwingApplicationTest` compilado previamente aunque el test ya no formaba parte de los archivos versionados. El problema correspondía a artefactos obsoletos en `target` y se resolvió mediante limpieza de Maven, sin modificar código para ocultar el problema.

Durante las ejecuciones de UI Surefire mostró el mensaje conocido de espera posterior a `System.exit(0)`. Las ejecuciones terminaron con `BUILD SUCCESS`, sin failures ni errors. No se realizó ningún cambio especulativo por ese mensaje.

## Criterio de validación

No se considera cerrada una funcionalidad solamente porque compile. Se mantienen tests específicos, relacionados y suite general cuando corresponde.

La última validación global conocida es **529/529**; la última validación específica es **11/11**.
