# SOFP — Tests

## Última validación

La suite general fue ejecutada localmente el **01/09/2026** después de limpiar artefactos compilados obsoletos y finalizó con:

- Tests run: **525**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **32:12 min**

La validación vigente es **525/525 tests en verde**.

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

## Cobertura del shell Swing

La fase inicial de interfaz agregó pruebas para:

- estructura y layout de `MainFrame`;
- presencia de header, sidebar, área central y barra de estado;
- navegación entre Inicio, Cuentas, Movimientos e Inversiones;
- cuentas filtradas por perfil/usuario;
- conservación de la cuenta seleccionada;
- movimientos de la cuenta seleccionada filtrados por usuario;
- posiciones de inversión del perfil filtradas por usuario;
- existencia y comportamiento del punto de entrada `ui.Main`.

Tests relacionados:

- `MainFrameTest`;
- `MainFrameLayoutTest`;
- `MainFrameNavigationTest`;
- `MainFrameMovimientosTest`;
- `MainFrameInversionesTest`;
- `CuentasPanelTest`;
- `MovimientosPanelTest`;
- `InversionesPanelTest`.

## Validación específica

`AislamientoDatosServiceTest`: **7/7 tests en verde**.

Durante la primera ejecución se detectó un problema exclusivamente en el fixture del test: el código de moneda generado excedía la longitud máxima de 10 caracteres. Se corrigió el dato de prueba sin modificar reglas de negocio ni lógica de seguridad.

## Incidente de compilación resuelto

Una ejecución de la suite general había intentado ejecutar un `SwingApplicationTest` compilado previamente, aunque ese test ya no formaba parte de los archivos versionados de la rama. El fallo era un `NoClassDefFoundError` por artefactos obsoletos en `target`.

Se realizó una limpieza de Maven y se volvió a ejecutar la suite completa. El resultado definitivo fue **525/525 tests en verde**.

## Cierre

La suite específica y la suite general posterior a los cambios están en verde. La validación global vigente es **525/525 tests**, sin fallos, errores ni tests omitidos.
