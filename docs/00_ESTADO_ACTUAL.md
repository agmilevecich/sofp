# SOFP — Estado actual

> Documento de continuidad. La fuente de verdad técnica es el código, los tests y los commits actuales; `docs/` es documentación auxiliar y puede quedar desactualizada.

## Estado verificado — 03/09/2026

**Rama estable:** `main` → `a4be859` (`docs: crear contexto de continuidad actualizado`).
**Rama de trabajo:** `feature/swing-shell` → `66b22f3` (`fix: gestionar transaccion al registrar categoria`).

La rama de trabajo está **139 commits por delante y 2 por detrás de `main`**, estado `diverged`. Merge base: `96f3d999`. Los dos commits exclusivos de `main` (`39badd1` y `a4be859`) son documentales y no se incorporan automáticamente.

## Estado Git local verificado — 03/09/2026

El usuario ejecutó `git syncsofp` y obtuvo `Already up to date` / `Everything up-to-date`.

Posteriormente `git status` informó `On branch feature/swing-shell` y `nothing to commit, working tree clean`. La validación final incluyó `git diff` y `git diff --check`, sin cambios ni errores.

## Validación vigente

### Categorías

El bloque de gestión de categorías quedó validado con `CategoriasPanelTest`, `MainFrameCategoriasTest` y `CategoriaServiceTest`.

- UI y navegación: **3/3 tests**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`, 02:24 min, finalización 13:40:32 -03:00.
- Servicio: **22/22 tests**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`, 10:33 min, finalización 13:53:50 -03:00.
- Total del bloque: **25/25 tests en verde**.

La corrección mínima aplicada fue `66b22f3`, que gestionó la transacción y `flush` en `CategoriaService.registrar(...)` para que la persistencia quedara disponible durante el flujo Swing sin duplicar reglas de negocio.

### Movimientos

El bloque de movimientos quedó validado con la integración del formulario, navegación, persistencia y servicio.

- `RegistrarMovimientoPanelTest` y `MainFrameMovimientosTest`: **7/7 tests**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`, 04:23 min, finalización 14:47:52 -03:00.
- `MovimientoServiceTest`: **50/50 tests**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`, 22:41 min, finalización 15:31:16 -03:00.
- Total conocido del bloque: **57/57 tests en verde**.

El selector de fecha utiliza LGoodDatePicker (`DatePicker`), con configuración `es-AR`, domingo como primer día, fecha inicial igual a `LocalDate.now()` y formato visual `dd/MM/uuuu`. La hora se obtiene automáticamente con `LocalTime.now()` al registrar y se combina con la fecha seleccionada.

### Inversiones y reportes

El bloque quedó validado tanto en UI como en servicios de cartera.

- `InversionesPanelTest`, `MainFrameInversionesTest` y `MainFrameReportesTest`: **5/5 tests**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`, 03:08 min, finalización 15:49:46 -03:00.
- `CarteraActivoServiceTest`, `CarteraActivoServiceComposicionTest` y `CarteraActivoServiceMovimientosTest`: **16/16 tests**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`, 09:38 min, finalización 16:09:32 -03:00.
- Total del bloque: **21/21 tests en verde**.

`InversionesPanel` muestra posiciones filtradas por usuario/perfil y `ReportesPanel` utiliza `CarteraActivoService` para reportes de movimientos. Ambos están integrados en `MainFrame`.

### Alta de cuentas

`RegistrarCuentaPanel` permite tipo, institución financiera, moneda e identificador externo y delega a `CuentaService.registrar(cuenta, usuarioId)`. `CuentaService` soporta alta con o sin transacción activa. `CuentasPanel` refresca mediante callback. `MainFrame` conserva el constructor histórico por `perfilFinancieroId` y el contextual.

## Seguridad implementada

La auditoría transversal de seguridad y aislamiento de datos quedó completada e integrada en `main`. Se mantienen protegidos por propietario/usuario perfiles, cuentas, categorías, movimientos, posiciones/cartera y operaciones financieras, y se cerraron caminos internos relevantes que podían saltar validaciones públicas.

## Fase 8 — Interfaz de usuario Swing

Implementados y conectados: `MainFrame`, `HeaderPanel`, `SidebarPanel`, `InicioPanel`, `CuentasPanel`, `MovimientosPanel`, `InversionesPanel`, `ReportesPanel`, `StatusBarPanel`, `CategoriasPanel`, `RegistrarCuentaPanel`, `RegistrarMovimientoPanel` y `ar.com.agmilevecich.sofp.ui.Main`.

`MainFrame` usa `CardLayout` para Inicio, Cuentas, Categorías, Movimientos, Inversiones y Reportes. La UI integra los servicios existentes y no duplica reglas de negocio.

## Validación general conocida

La última suite general conocida continúa siendo la ejecutada el **01/09/2026**: **529/529 tests**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`, 14:25 min, finalización 19:25:53 -03:00.

Las validaciones específicas del 03/09/2026 no sustituyen todavía una nueva suite general.

## Últimos cambios de código/test

- `66b22f3` — `fix: gestionar transaccion al registrar categoria`;
- `e873832` — `test: verificar fecha del sistema en selector de movimientos`;
- `f820dff` — `feat: mostrar fecha del sistema en selector de movimientos`;
- `1363c0a` — `test: corregir comparacion de hora del sistema`;
- `688d8b0` — `feat: registrar hora del sistema en movimientos`.

Los bloques anteriores de categorías, movimientos, cuentas, inversiones y reportes están validados dentro de sus alcances registrados.

## Incidentes conocidos

Durante las pruebas Swing Surefire muestra el mensaje posterior a `System.exit(0)`. En las ejecuciones registradas el proceso terminó con `BUILD SUCCESS`, sin failures ni errors; no se considera un fallo y no se modifica código especulativamente por ese mensaje.

Durante una prueba manual hubo una modificación accidental de `RegistrarMovimientoPanel.java`. El archivo fue corregido y `git syncsofp` confirmó posteriormente árbol limpio y sincronizado.

## Criterio de estado

Los bloques de alta de movimientos, alta de cuentas, gestión de categorías, inversiones y reportes están cerrados dentro de sus alcances validados. El selector de fecha/hora de movimientos también está validado individualmente.

No se realizó merge a `main` y no se crean ramas nuevas.

## Próximo paso

Definir el próximo bloque funcional de Fase 8 a partir del código real de `feature/swing-shell`. Antes de modificar código, revisar implementación, clases relacionadas, servicios, repositorios, reglas de negocio y tests. Antes de una eventual integración, revisar explícitamente los dos commits documentales exclusivos de `main`.
