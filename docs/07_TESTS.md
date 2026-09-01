# SOFP — Tests

## Última validación general

La suite general fue ejecutada localmente el **01/09/2026** y finalizó con:

- Tests run: **529**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **14:25 min**
- Finalización: **19:25:53 -03:00**

La validación global vigente es **529/529 tests en verde**.

## Cobertura de seguridad

La feature `feature/seguridad-aislamiento-datos` cubre autorización por propietario en cuentas, categorías, movimientos, operaciones financieras, perfiles y posiciones/cartera.

La auditoría agregó cobertura para:

- lectura propia por ID;
- rechazo de lectura de recursos ajenos;
- listados por perfil con verificación del propietario;
- cálculo de saldo y evolución contextualizados por usuario;
- altas de cuentas, categorías y movimientos con verificación de propietario;
- lectura de perfil propio frente a perfil ajeno;
- aislamiento de posición y cartera por usuario;
- caminos alternativos de creación y acceso.

Test agregado: `AislamientoDatosServiceTest`.

Validación específica de seguridad: **7/7 tests en verde**.

## Cobertura del shell Swing

La fase de interfaz agregó pruebas para:

- estructura y layout de `MainFrame`;
- presencia de header, sidebar, área central y barra de estado;
- navegación entre Inicio, Cuentas, Movimientos, Inversiones y Reportes;
- cuentas filtradas por perfil/usuario;
- conservación de la cuenta seleccionada;
- movimientos de la cuenta seleccionada filtrados por usuario;
- posiciones de inversión del perfil filtradas por usuario;
- reporte de movimientos de inversión;
- existencia y comportamiento del punto de entrada `ui.Main`.

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
- `ReportesPanelTest`.

## Validación específica de reportes

- `ReportesPanelTest`: **3/3 tests en verde**.
- `MainFrameReportesTest`: **1/1 test en verde**.
- Total: **4/4 tests en verde**.

La suite relacionada de UI quedó en **13/13 tests en verde**.

## Incidentes de entorno de compilación

Una ejecución previa intentó ejecutar un `SwingApplicationTest` compilado previamente aunque el test ya no formaba parte de los archivos versionados de la rama. El fallo correspondía a artefactos obsoletos en `target`.

Se limpió el proyecto con Maven y se volvió a ejecutar la suite. La validación definitiva posterior fue **529/529 tests en verde**.

Durante ejecuciones de tests de UI Surefire mostró un mensaje indicando que esperaba más de 30 segundos después de `System.exit(0)`. El proceso terminó igualmente con `BUILD SUCCESS`, sin failures ni errors. No se modificó código de forma especulativa por ese mensaje.

## Cierre

La cobertura específica del shell y la suite general están en verde. El resultado global vigente es **529/529 tests**, sin fallos, errores ni tests omitidos.
