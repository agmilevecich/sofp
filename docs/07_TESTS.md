# SOFP — Tests

## Estado de validación — 03/09/2026

La última batería relacionada de Swing fue ejecutada localmente el **03/09/2026**:

`mvn -Dtest=InversionesPanelTest,ReportesPanelTest,MainFrameInversionesTest,MainFrameReportesTest,MainFrameMovimientosTest,CuentasPanelTest,RegistrarCuentaPanelTest test`

Resultado:

- **20/20 tests en verde**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- Duración: **09:43 min**;
- Finalización: **10:55:56 -03:00**.

La batería cubrió inversiones, reportes, navegación e integración en `MainFrame`, movimientos y alta de cuentas.

Validación individual adicional del mismo día: `MainFrameMovimientosTest` **3/3**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`, duración 2:12 min, finalización **10:45:01 -03:00**.

## Última suite general

La última suite general conocida fue ejecutada el **01/09/2026**:

- **529/529 tests en verde**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- Duración: **14:25 min**;
- Finalización: **19:25:53 -03:00**.

Los **20/20** del 03/09 corresponden a la batería relacionada de Swing y no sustituyen la suite general.

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

La cobertura incluye layout, navegación, contexto usuario/perfil, cuentas, movimientos, inversiones, reportes, persistencia y callbacks de actualización.

## Alta de cuentas

`RegistrarCuentaPanelTest` cubre construcción, dependencias obligatorias, instituciones activas, monedas, alta real, persistencia e identificador externo vacío.

`CuentasPanelTest` cubre listado autorizado, integración del formulario, refresco automático y rechazo de perfil ajeno.

`MainFrameMovimientosTest` cubre la compatibilidad del constructor histórico por `perfilFinancieroId` y la integración contextual de cuentas.

El 02/09 una expectativa incorrecta comprobaba que el botón estuviera habilitado con el nombre vacío. Se corrigió en `29b5e11` para introducir un nombre válido antes de verificar la habilitación. El 03/09 la batería relacionada pasó **20/20**.

## Inversiones y reportes

`InversionesPanelTest`, `MainFrameInversionesTest`, `ReportesPanelTest` y `MainFrameReportesTest` forman parte de la batería de 20 tests verdes.

La integración de `MainFrame` fue corregida en `c7cca8f` para conservar el `PerfilFinanciero` al delegar desde los constructores públicos. Esto permitió que los paneles contextuales consultaran posiciones y movimientos del perfil correcto.

## Incidentes de entorno

Durante las pruebas Swing Surefire muestra el mensaje `Surefire is going to kill self fork JVM. The exit has elapsed 30 seconds after System.exit(0)`. En las ejecuciones registradas el proceso terminó con `BUILD SUCCESS`, sin failures ni errors. No se considera un fallo de tests ni se realiza un cambio especulativo.

Una ejecución anterior intentó ejecutar un `SwingApplicationTest` obsoleto compilado en `target`; se resolvió mediante limpieza de Maven, sin modificar código ni tests para ocultar el problema.

## Criterio de validación

No considerar una funcionalidad terminada solamente porque compila. Mantener validación específica, relacionada y suite general cuando corresponda.
