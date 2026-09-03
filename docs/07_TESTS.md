# SOFP — Tests

## Estado de validación — 03/09/2026

### Gestión de categorías

`CategoriasPanelTest` + `MainFrameCategoriasTest`:

- **3/3 tests en verde**;
- Failures: 0;
- Errors: 0;
- Skipped: 0;
- `BUILD SUCCESS`;
- Duración: **02:24 min**;
- Finalización: **13:40:32 -03:00**.

`CategoriaServiceTest`:

- **22/22 tests en verde**;
- Failures: 0;
- Errors: 0;
- Skipped: 0;
- `BUILD SUCCESS`;
- Duración: **10:33 min**;
- Finalización: **13:53:50 -03:00**.

Total del bloque de categorías: **25/25 tests en verde**.

### Movimientos

`RegistrarMovimientoPanelTest` + `MainFrameMovimientosTest`:

- **7/7 tests en verde**;
- Failures: 0;
- Errors: 0;
- Skipped: 0;
- `BUILD SUCCESS`;
- Duración: **04:23 min**;
- Finalización: **14:47:52 -03:00**.

`MovimientoServiceTest`:

- **50/50 tests en verde**;
- Failures: 0;
- Errors: 0;
- Skipped: 0;
- `BUILD SUCCESS`;
- Duración: **22:41 min**;
- Finalización: **15:31:16 -03:00**.

Total conocido del bloque de movimientos: **57/57 tests en verde**.

### Inversiones y reportes

`InversionesPanelTest` + `MainFrameInversionesTest` + `MainFrameReportesTest`:

- **5/5 tests en verde**;
- Failures: 0;
- Errors: 0;
- Skipped: 0;
- `BUILD SUCCESS`;
- Duración: **03:08 min**;
- Finalización: **15:49:46 -03:00**.

`CarteraActivoServiceTest` + `CarteraActivoServiceComposicionTest` + `CarteraActivoServiceMovimientosTest`:

- **16/16 tests en verde**;
- Failures: 0;
- Errors: 0;
- Skipped: 0;
- `BUILD SUCCESS`;
- Duración: **09:38 min**;
- Finalización: **16:09:32 -03:00**.

Total del bloque de inversiones/reportes: **21/21 tests en verde**.

### Selector de fecha y hora de movimientos

`RegistrarMovimientoPanelTest` fue ejecutado individualmente:

- **4/4 tests en verde**;
- Failures: 0;
- Errors: 0;
- Skipped: 0;
- `BUILD SUCCESS`;
- Duración: **03:27 min**;
- Finalización: **12:39:51 -03:00**.

El selector utiliza LGoodDatePicker, con domingo como primer día de la semana, fecha inicial igual a `LocalDate.now()` y formato `dd/MM/uuuu`. La hora se toma con `LocalTime.now()` al registrar.

## Alta de cuentas

`RegistrarCuentaPanelTest` y `CuentasPanelTest` cubren construcción, dependencias, instituciones activas, monedas, alta real, persistencia, identificador externo vacío, listado autorizado, refresco y rechazo de perfil ajeno.

## Cobertura Swing

Tests relacionados:

- `MainFrameTest`;
- `MainFrameLayoutTest`;
- `MainFrameNavigationTest`;
- `MainFrameMovimientosTest`;
- `MainFrameCategoriasTest`;
- `MainFrameInversionesTest`;
- `MainFrameReportesTest`;
- `CuentasPanelTest`;
- `MovimientosPanelTest`;
- `CategoriasPanelTest`;
- `InversionesPanelTest`;
- `ReportesPanelTest`;
- `RegistrarCuentaPanelTest`;
- `RegistrarMovimientoPanelTest`.

La cobertura incluye layout, navegación, contexto usuario/perfil, cuentas, categorías, movimientos, inversiones, reportes, persistencia, callbacks de actualización y selector de fecha/hora.

## Seguridad

`AislamientoDatosServiceTest`: **7/7** en verde. Suite general posterior a seguridad: **512/512** en verde.

La seguridad cubre autorización por propietario en perfiles, cuentas, categorías, movimientos, operaciones financieras y posiciones/cartera, además del cierre de caminos internos relevantes.

## Última suite general

La última suite general conocida fue ejecutada el **01/09/2026**:

- **529/529 tests en verde**;
- Failures: 0;
- Errors: 0;
- Skipped: 0;
- `BUILD SUCCESS`;
- Duración: **14:25 min**;
- Finalización: **19:25:53 -03:00**.

Las ejecuciones específicas del 03/09/2026 son validaciones posteriores de bloques y todavía no sustituyen una nueva suite general.

## Incidentes de entorno

Durante las pruebas Swing Surefire muestra el mensaje `Surefire is going to kill self fork JVM. The exit has elapsed 30 seconds after System.exit(0)`. En las ejecuciones registradas el proceso terminó con `BUILD SUCCESS`, sin failures ni errors. No se considera un fallo de tests ni se realiza un cambio especulativo.

Una ejecución anterior intentó ejecutar un `SwingApplicationTest` obsoleto compilado en `target`; se resolvió mediante limpieza de Maven, sin modificar código ni tests para ocultar el problema.

## Estado Git

La validación local final del 03/09/2026 informó:

`git diff` → sin cambios.

`git diff --check` → sin errores.

`git status` → `On branch feature/swing-shell` / `nothing to commit, working tree clean`.

## Criterio de validación

No considerar una funcionalidad terminada solamente porque compila. Mantener validación específica, relacionada y suite general cuando corresponda.
