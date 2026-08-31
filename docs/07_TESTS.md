# SOFP — Tests

## Última validación

La suite general fue ejecutada localmente el **31/08/2026** y finalizó con:

- Tests run: **515**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **22:17 min**

La validación vigente es **515/515 tests en verde**.

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
- existencia y comportamiento del punto de entrada `ui.Main`.

Tests relacionados:

- `MainFrameTest`;
- `MainFrameLayoutTest`;
- `MainFrameNavigationTest`.

## Validación específica

`AislamientoDatosServiceTest`: **7/7 tests en verde**.

Durante la primera ejecución se detectó un problema exclusivamente en el fixture del test: el código de moneda generado excedía la longitud máxima de 10 caracteres. Se corrigió el dato de prueba sin modificar reglas de negocio ni lógica de seguridad.

## Cierre

La suite específica y la suite general posterior a los cambios están en verde. La validación global vigente es **515/515 tests**, sin fallos, errores ni tests omitidos.
