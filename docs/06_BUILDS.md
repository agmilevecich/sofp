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

## Validaciones posteriores al Build 059

Se incorporaron y validaron identificación por símbolo, cartera de activos, costo promedio, valorización, reportes, evolución histórica y seguridad de perfil financiero.

## Etapa — Seguridad y aislamiento por usuario

La rama `feature/seguridad-aislamiento-datos` completó la auditoría transversal y fue integrada en `main` mediante fast-forward.

Se implementaron y validaron autorizaciones por usuario/propietario para perfiles, cuentas, categorías, movimientos, posiciones/cartera y operaciones financieras, incluyendo cobertura transversal mediante `AislamientoDatosServiceTest`.

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

Se incorporaron y conectaron:

- `MainFrame`;
- `HeaderPanel`;
- `SidebarPanel`;
- `InicioPanel`;
- `CuentasPanel`;
- `MovimientosPanel`;
- `InversionesPanel`;
- `ReportesPanel`;
- `StatusBarPanel`;
- `ui.Main` como punto de entrada;
- integración contextual por usuario/perfil;
- pruebas de paneles, estructura, layout, navegación e integración.

## Bloque de reportes de inversiones

`ReportesPanel` utiliza la funcionalidad de reporte existente en `CarteraActivoService`. Fue integrado al `MainFrame` y la navegación quedó conectada a la tarjeta `REPORTES`, sin duplicar lógica de negocio.

Validación específica:

- `ReportesPanelTest`: **3/3 tests en verde**;
- `MainFrameReportesTest`: **1/1 test en verde**;
- total: **4/4 tests en verde**;
- suite relacionada de UI: **13/13 tests en verde**.

## Bloque de alta de movimientos desde Swing

Se agregó `RegistrarMovimientoPanel` al flujo de movimientos de una cuenta.

El formulario permite seleccionar categoría autorizada y activa, tipo de movimiento, importe, fecha/hora y descripción. La operación se delega a `MovimientoService.registrar(...)` utilizando el `usuarioId` de contexto.

`MovimientosPanel` recibe un callback después de un alta exitosa y actualiza el listado de movimientos de la cuenta.

Se separó la operación de alta sin diálogos en `registrarMovimiento()` para permitir probar la interacción real sin bloquear la prueba con `JOptionPane`.

Commits principales del bloque:

- `39d96be` — `feat: agregar formulario de alta de movimientos`;
- `62b8e2a` — `fix: corregir tipos del formulario de movimientos`;
- `76389ca` — `feat: integrar alta de movimientos`;
- `1757b60` — `fix: conservar alta de movimientos sin depender del listado`;
- `978288f` — `feat: conectar alta de movimientos al shell`;
- `7b67880` — `fix: corregir constructor base del shell`;
- `deaae7d` — `refactor: separar alta de movimientos de dialogos`;
- `e8c9c50` — `test: cubrir alta real de movimientos desde formulario`.

## Validación específica del bloque de alta

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

`RegistrarMovimientoPanelTest`: **4/4 tests en verde**. La prueba nueva confirma alta real, persistencia en H2 y notificación al contenedor para refrescar el listado.

## Validación general vigente

La última suite general sigue siendo la ejecutada el **01/09/2026**:

- Tests run: **529**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **14:25 min**
- Finalización: **19:25:53 -03:00**

Por lo tanto, **529/529** es la última validación global disponible. Los 10/10 del 02/09 validan específicamente el bloque de alta de movimientos posterior a esa suite general.

Durante las ejecuciones de UI Surefire mostró un mensaje de espera posterior a `System.exit(0)`, pero las ejecuciones terminaron con `BUILD SUCCESS`, sin failures ni errors. No se realizó ningún cambio especulativo por ese mensaje.

## Estado actual

`main` permanece como rama estable.

`feature/swing-shell` es la rama activa. El último commit funcional es `e8c9c50`; la documentación se actualiza ahora para reflejarlo.

## Próximo paso

El bloque de alta de movimientos queda cerrado dentro de su alcance. El siguiente trabajo de Fase 8 debe definirse como un nuevo bloque funcional, sin merge automático a `main`, sin crear ramas nuevas y manteniendo cambios pequeños, tests específicos y reglas de negocio fuera de la UI.
