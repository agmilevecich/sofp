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

Se incorporaron y conectaron `MainFrame`, `HeaderPanel`, `SidebarPanel`, `InicioPanel`, `CuentasPanel`, `MovimientosPanel`, `InversionesPanel`, `ReportesPanel`, `StatusBarPanel` y `ui.Main`.

La integración mantiene las reglas de negocio en los servicios existentes y pasa el contexto de usuario/perfil a la UI.

## Bloque de reportes de inversiones

`ReportesPanel` utiliza la funcionalidad de reporte existente en `CarteraActivoService`. Fue integrado al `MainFrame` y la navegación quedó conectada a la tarjeta `REPORTES`, sin agregar lógica de negocio duplicada.

Validación específica: `ReportesPanelTest` **3/3**, `MainFrameReportesTest` **1/1**, total **4/4** y suite relacionada anterior de UI **13/13**.

## Validación general — 01/09/2026

La suite general ejecutada el **01/09/2026** produjo:

- Tests run: **529**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **14:25 min**
- Finalización: **19:25:53 -03:00**.

La validación global disponible sigue siendo **529/529 tests en verde**.

Una ejecución anterior había fallado por un `SwingApplicationTest` compilado previamente en `target`; la limpieza de Maven eliminó el artefacto obsoleto sin modificar código ni tests por ese motivo.

## 2026-09-02 — Alta de movimientos desde Swing

Se completó el bloque de alta de movimientos desde la interfaz Swing. `RegistrarMovimientoPanel` permite seleccionar categoría autorizada y activa, tipo, importe, fecha/hora y descripción y utiliza el `usuarioId` del contexto.

La operación se delega a `MovimientoService.registrar(...)`. Después de un alta exitosa, `MovimientosPanel` recibe un callback y refresca el listado.

Se separó `registrarMovimiento()` de la interacción visual para permitir probar el alta real sin bloquearla con diálogos Swing.

Validación histórica del bloque: **10/10 tests en verde**.

## 2026-09-02 — Alta de cuentas desde Swing

Se incorporó `RegistrarCuentaPanel` al flujo de cuentas. El formulario permite seleccionar tipo de cuenta, institución financiera, moneda e identificador externo y delega el alta a `CuentaService.registrar(cuenta, usuarioId)`.

`CuentasPanel` refresca el listado mediante callback después de una registración exitosa. `MainFrame` conserva el constructor histórico basado en `perfilFinancieroId` y utiliza el constructor contextual completo cuando dispone de `PerfilFinanciero` y servicios auxiliares.

`CuentaService` fue ajustado para soportar el alta cuando no existe una transacción activa y también cuando la llamada ya se encuentra dentro de una transacción existente.

Commits principales del bloque:

- `d5674aa` — `feat: agregar formulario de alta de cuentas`;
- `34d4d7d` — `fix: completar transaccion de alta de cuentas`;
- `19b2988` — `fix: permitir alta de cuentas dentro de transaccion existente`;
- `731e520` — `feat: integrar alta de cuentas en el panel`;
- `76cc4b0` — `fix: corregir contexto del panel de cuentas`;
- `a8ae7f9` — `feat: conectar alta de cuentas al shell`;
- `c5d9098` — `test: cubrir alta de cuentas desde el shell`;
- `0919a8e` — `fix: corregir orden de servicios en MainFrame`;
- `0d7769e` — `fix: conservar listado de cuentas con perfil id`.

## Validación más reciente — 02/09/2026

Se ejecutó:

`mvn -Dtest=RegistrarCuentaPanelTest,CuentasPanelTest,MainFrameMovimientosTest test`

Resultado:

- Tests run: **11**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **05:24 min**
- Finalización: **14:05:26 -03:00**.

Detalle: `RegistrarCuentaPanelTest` **5/5**, `CuentasPanelTest` **3/3**, `MainFrameMovimientosTest` **3/3**.

La ejecución confirma alta real de cuentas, persistencia, callback de refresco y compatibilidad del constructor histórico de `MainFrame` basado en `perfilFinancieroId`.

Durante las ejecuciones de UI Surefire mostró el mensaje conocido de espera posterior a `System.exit(0)`, pero las ejecuciones terminaron con `BUILD SUCCESS`, sin failures ni errors.

## Estado actual — 02/09/2026

`main` permanece como rama estable en `75d0a18` — `docs: actualizar contexto tras cierre de seguridad`.

`feature/swing-shell` es la rama activa y su último commit actual es `0d7769e` — `fix: conservar listado de cuentas con perfil id`.

La comparación verificada es **90 commits por delante y 2 commits por detrás de `main`**.

## Próximo avance

Los bloques de alta de movimientos y cuentas quedan cerrados dentro de sus alcances validados. El siguiente avance debe definirse como un nuevo bloque funcional de Fase 8, partiendo nuevamente del código real de `feature/swing-shell`.

No se realiza merge automático a `main` y no se crean ramas nuevas para continuar.
