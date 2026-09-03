# SOFP — Historial del proyecto

## 2026-08-31 — Cierre de seguridad y aislamiento de datos

`feature/seguridad-aislamiento-datos` completó la revisión transversal de seguridad y fue integrada en `main` mediante fast-forward.

Validación final: `AislamientoDatosServiceTest` **7/7** y suite general **512/512**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`.

## 2026-08-31 / 2026-09-01 — Fase 8: shell Swing

`feature/swing-shell` desarrolló el shell Swing y conectó `MainFrame`, `HeaderPanel`, `SidebarPanel`, `InicioPanel`, `CuentasPanel`, `MovimientosPanel`, `InversionesPanel`, `ReportesPanel`, `StatusBarPanel` y `ui.Main`.

`MainFrame` usa `CardLayout` para Inicio, Cuentas, Categorías, Movimientos, Inversiones y Reportes, integrando los servicios existentes y manteniendo las reglas de negocio fuera de la UI.

## 2026-09-03 — Selector de fecha y hora de movimientos

Se incorporó **LGoodDatePicker** y se reemplazó el ingreso manual de fecha por `DatePicker` en `RegistrarMovimientoPanel`.

El selector usa configuración regional `es-AR`, comienza la semana en domingo, muestra inicialmente la fecha del sistema y utiliza formato visual `dd/MM/uuuu`.

Se eliminó el ingreso de hora mediante ComboBox. Al registrar un movimiento, la hora se obtiene automáticamente con `LocalTime.now()` y se combina con la fecha seleccionada.

Commits del bloque: `bd436d4`, `418c8f4`, `fcf5f69`, `bdd03b3`, `688d8b0`, `53f138c`, `0fc7309`, `1363c0a`, `f820dff`, `e873832`.

Validación individual del bloque: `RegistrarMovimientoPanelTest` **4/4**, `BUILD SUCCESS`, 03:27 min, finalización 12:39:51 -03:00.

## 2026-09-03 — Gestión de categorías

`CategoriasPanel` se integró en la navegación de `MainFrame` y permite gestionar las categorías del perfil del usuario mediante `CategoriaService`.

Durante la validación se detectó que el alta no hacía visible la persistencia esperada cuando el servicio no gestionaba la transacción. La corrección mínima quedó en `66b22f3` (`fix: gestionar transaccion al registrar categoria`), con gestión de transacción y `flush` cuando corresponde.

Validaciones finales del bloque: UI/navegación **3/3** y servicio **22/22**; total **25/25 tests en verde**.

## 2026-09-03 — Validación de movimientos

`MainFrameMovimientosTest` + `RegistrarMovimientoPanelTest`: **7/7**, `BUILD SUCCESS`, 04:23 min, finalización 14:47:52 -03:00.

`MovimientoServiceTest`: **50/50**, `BUILD SUCCESS`, 22:41 min, finalización 15:31:16 -03:00.

Total conocido del bloque: **57/57 tests en verde**.

## 2026-09-03 — Validación de inversiones y reportes

`InversionesPanelTest` + `MainFrameInversionesTest` + `MainFrameReportesTest`: **5/5**, `BUILD SUCCESS`, 03:08 min, finalización 15:49:46 -03:00.

`CarteraActivoServiceTest` + `CarteraActivoServiceComposicionTest` + `CarteraActivoServiceMovimientosTest`: **16/16**, `BUILD SUCCESS`, 09:38 min, finalización 16:09:32 -03:00.

Total del bloque: **21/21 tests en verde**.

## 2026-09-03 — Validación relacionada de Swing

Durante la jornada se registraron baterías relacionadas de UI y formularios. La batería previa de Swing fue **20/20**; las validaciones posteriores cubrieron específicamente categorías, movimientos, inversiones, reportes y servicios de cartera.

No se suman los resultados de distintas ejecuciones como una única cifra de suite porque pueden compartir clases de prueba entre comandos.

## Estado Git — 2026-09-03

El usuario confirmó mediante `git syncsofp` que `feature/swing-shell` estaba sincronizada con GitHub. La validación final informó:

`git diff` → sin cambios.

`git diff --check` → sin errores.

`git status` → `On branch feature/swing-shell` / `nothing to commit, working tree clean`.

Último commit de código/test antes de estas actualizaciones documentales: `66b22f3` — `fix: gestionar transaccion al registrar categoria`.

Las actualizaciones de esta documentación generan commits posteriores de tipo `docs:` y no representan cambios funcionales.

`main` apunta a `a4be859` — `docs: crear contexto de continuidad actualizado`.

Comparación conocida antes de estas actualizaciones documentales: **139 commits adelante y 2 atrás**, estado `diverged`, merge base `96f3d999`. Los dos commits exclusivos de `main` son documentales: `39badd1` y `a4be859`.

## Incidentes

Durante las pruebas Swing Surefire mostró el mensaje posterior a `System.exit(0)`, pero no produjo fallos ni errores en las ejecuciones registradas.

Durante una prueba manual hubo una modificación accidental de `RegistrarMovimientoPanel.java`; el archivo fue corregido y el posterior `git syncsofp` confirmó árbol limpio y sincronizado.

## Próximo avance

Definir el siguiente bloque funcional de Fase 8 a partir del código actual. No hacer merge automático a `main` ni crear ramas nuevas. Antes de una eventual integración, revisar explícitamente los commits documentales exclusivos de `main`.
