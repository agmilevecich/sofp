# SOFP — Historial del proyecto

## 2026-08-31 — Cierre de seguridad y aislamiento de datos

La feature `feature/seguridad-aislamiento-datos` completó la revisión transversal de seguridad y aislamiento de recursos por usuario/perfil y fue integrada en `main` mediante fast-forward.

Validación final de seguridad:

- `AislamientoDatosServiceTest`: **7/7 en verde**;
- suite general: **512/512 tests en verde**;
- Failures: 0;
- Errors: 0;
- Skipped: 0;
- `BUILD SUCCESS`.

## 2026-08-31 / 2026-09-01 — Fase 8: shell Swing

La rama `feature/swing-shell` desarrolló el shell Swing sobre la base estable de seguridad.

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
- `ui.Main` como punto de entrada.

La integración mantiene las reglas de negocio en los servicios existentes y pasa el contexto de usuario/perfil a la UI.

## Bloque de reportes de inversiones

`ReportesPanel` utiliza la funcionalidad de reporte existente en `CarteraActivoService`. Fue integrado al `MainFrame` y la navegación quedó conectada a la tarjeta `REPORTES`, sin agregar lógica de negocio duplicada.

Validación específica:

- `ReportesPanelTest`: **3/3 tests en verde**;
- `MainFrameReportesTest`: **1/1 test en verde**;
- total: **4/4 tests en verde**;
- suite relacionada de UI: **13/13 tests en verde**.

## Validación general del shell Swing

Suite general ejecutada localmente el **01/09/2026**:

- Tests run: **529**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **14:25 min**
- Finalización: **19:25:53 -03:00**.

La validación global vigente es **529/529 tests en verde**.

Una ejecución anterior había fallado por un `SwingApplicationTest` compilado previamente en `target`. La limpieza de Maven eliminó el artefacto obsoleto y permitió ejecutar la suite correctamente, sin modificar código ni tests por ese motivo.

## 2026-09-02 — Fase 8: alta de movimientos desde Swing

Se completó el bloque de alta de movimientos desde la interfaz Swing.

Se incorporó `RegistrarMovimientoPanel` al flujo de movimientos de la cuenta seleccionada. El formulario permite seleccionar categoría autorizada y activa, tipo de movimiento, importe, fecha/hora y descripción, y utiliza el `usuarioId` del contexto.

La operación se delega a `MovimientoService.registrar(...)`. Después de un alta exitosa, `MovimientosPanel` recibe un callback y refresca el listado.

Para permitir la prueba de la operación real sin bloquearla con diálogos Swing, se separó la operación en `registrarMovimiento()`, manteniendo `registrar()` como coordinador de la interacción visual y los mensajes al usuario.

Commits principales del bloque:

- `39d96be` — `feat: agregar formulario de alta de movimientos`;
- `62b8e2a` — `fix: corregir tipos del formulario de movimientos`;
- `76389ca` — `feat: integrar alta de movimientos`;
- `1757b60` — `fix: conservar alta de movimientos sin depender del listado`;
- `978288f` — `feat: conectar alta de movimientos al shell`;
- `7b67880` — `fix: corregir constructor base del shell`;
- `deaae7d` — `refactor: separar alta de movimientos de dialogos`;
- `e8c9c50` — `test: cubrir alta real de movimientos desde formulario`.

## Validación del bloque de alta

Suite ejecutada localmente el **02/09/2026**:

`mvn -Dtest=RegistrarMovimientoPanelTest,MovimientosPanelTest,MainFrameMovimientosTest,MainFrameNavigationTest test`

Resultado:

- Tests run: **10**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **04:38 min**
- Finalización: **12:26:39 -03:00**.

`RegistrarMovimientoPanelTest`: **4/4 tests en verde**.

La prueba nueva confirma registración real, persistencia en H2, importe, fecha/hora, descripción y notificación al contenedor. La cobertura de `MovimientosPanel` verifica la actualización del listado.

Durante la ejecución apareció el mensaje conocido de Surefire sobre la espera posterior a `System.exit(0)`, pero la ejecución terminó con `BUILD SUCCESS`, sin failures ni errors.

## Estado actual — 02/09/2026

`main` permanece como rama estable.

`feature/swing-shell` es la rama activa y su último commit funcional es `e8c9c50` — `test: cubrir alta real de movimientos desde formulario`.

La rama se encuentra actualmente **73 commits por delante y 2 commits por detrás de `main`**. Los 2 commits de diferencia de `main` corresponden a documentación posterior y no se incorporan automáticamente a la feature.

La documentación de continuidad se mantiene en la rama de trabajo para reflejar el estado real sin modificar `main`.

## Próximo avance

El bloque de alta de movimientos queda cerrado dentro de su alcance. El siguiente avance debe definirse como un nuevo bloque funcional de Fase 8, partiendo nuevamente del código real de `feature/swing-shell`.

No se realiza merge automático a `main` y no se crean ramas nuevas para continuar.
