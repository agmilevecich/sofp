# SOFP — Tests

## Estado de validación — 03/09/2026

### Selector de fecha y hora de movimientos

`RegistrarMovimientoPanelTest` fue ejecutado individualmente:

`mvn -Dtest=RegistrarMovimientoPanelTest test`

Resultado:

- **4/4 tests en verde**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- Duración: **03:27 min**;
- Finalización: **12:39:51 -03:00**.

La cobertura verifica la construcción del formulario, la fecha inicial igual a `LocalDate.now()`, el uso del selector de fecha y el registro de la hora del sistema junto con la fecha seleccionada.

El selector utiliza LGoodDatePicker, con domingo como primer día de la semana. La hora ya no se introduce mediante ComboBox.

### Batería relacionada de Swing

Se ejecutó:

`mvn -Dtest=InversionesPanelTest,ReportesPanelTest,MainFrameInversionesTest,MainFrameReportesTest,MainFrameMovimientosTest,CuentasPanelTest,RegistrarCuentaPanelTest test`

Resultado: **20/20 tests en verde**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`, duración 09:43 min, finalización 10:55:56 -03:00.

`MainFrameMovimientosTest` también fue ejecutado individualmente: **3/3**, `BUILD SUCCESS`, finalización 10:45:01 -03:00.

## Última suite general

La última suite general conocida fue ejecutada el **01/09/2026**:

- **529/529 tests en verde**;
- Failures: 0;
- Errors: 0;
- Skipped: 0;
- `BUILD SUCCESS`;
- Duración: 14:25 min;
- Finalización: 19:25:53 -03:00.

Los 4/4 y 20/20 del 03/09 son validaciones específicas/relacionadas y no sustituyen la suite general.

## Cobertura de seguridad

`AislamientoDatosServiceTest`: **7/7** en verde. Suite general posterior a seguridad: **512/512** en verde.

La seguridad cubre autorización por propietario en perfiles, cuentas, categorías, movimientos, operaciones financieras y posiciones/cartera, además del cierre de caminos internos relevantes.

## Cobertura Swing

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

La cobertura incluye layout, navegación, contexto usuario/perfil, cuentas, movimientos, inversiones, reportes, persistencia, callbacks de actualización y selector de fecha/hora.

## Alta de cuentas

`RegistrarCuentaPanelTest` cubre construcción, dependencias obligatorias, instituciones activas, monedas, alta real, persistencia e identificador externo vacío.

`CuentasPanelTest` cubre listado autorizado, integración del formulario, refresco automático y rechazo de perfil ajeno.

`MainFrameMovimientosTest` cubre la compatibilidad del constructor histórico por `perfilFinancieroId` y la integración contextual de cuentas.

La expectativa incorrecta sobre la habilitación del alta de cuentas fue corregida en `29b5e11`, sin modificar producción.

## Inversiones y reportes

`InversionesPanelTest`, `MainFrameInversionesTest`, `ReportesPanelTest` y `MainFrameReportesTest` forman parte de la batería de 20 tests verdes.

La integración de `MainFrame` fue corregida en `c7cca8f` para conservar el `PerfilFinanciero` al delegar desde los constructores públicos.

## Incidentes de entorno

Durante las pruebas Swing Surefire muestra el mensaje `Surefire is going to kill self fork JVM. The exit has elapsed 30 seconds after System.exit(0)`. En las ejecuciones registradas el proceso terminó con `BUILD SUCCESS`, sin failures ni errors. No se considera un fallo de tests ni se realiza un cambio especulativo.

Una ejecución anterior intentó ejecutar un `SwingApplicationTest` obsoleto compilado en `target`; se resolvió mediante limpieza de Maven, sin modificar código ni tests para ocultar el problema.

## Criterio de validación

No considerar una funcionalidad terminada solamente porque compila. Mantener validación específica, relacionada y suite general cuando corresponda.
