# SOFP — Historial del proyecto

## 2026-08-31 — Cierre de seguridad y aislamiento de datos

`feature/seguridad-aislamiento-datos` completó la revisión transversal de seguridad y fue integrada en `main` mediante fast-forward.

Validación final: `AislamientoDatosServiceTest` **7/7** y suite general **512/512**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`.

## 2026-08-31 / 2026-09-01 — Fase 8: shell Swing

`feature/swing-shell` desarrolló el shell Swing y conectó `MainFrame`, `HeaderPanel`, `SidebarPanel`, `InicioPanel`, `CuentasPanel`, `MovimientosPanel`, `InversionesPanel`, `ReportesPanel`, `StatusBarPanel` y `ui.Main`.

`MainFrame` usa `CardLayout` para Inicio, Cuentas, Movimientos, Inversiones y Reportes, integrando los servicios existentes y manteniendo las reglas de negocio fuera de la UI.

## 2026-09-03 — Selector de fecha y hora de movimientos

Se incorporó **LGoodDatePicker** y se reemplazó el ingreso manual de fecha por `DatePicker` en `RegistrarMovimientoPanel`.

El selector usa configuración regional `es-AR`, comienza la semana en domingo, muestra inicialmente la fecha del sistema y utiliza formato visual `dd/MM/uuuu`.

Se eliminó el ingreso de hora mediante ComboBox. Al registrar un movimiento, la hora se obtiene automáticamente con `LocalTime.now()` y se combina con la fecha seleccionada.

Commits del bloque: `bd436d4`, `418c8f4`, `fcf5f69`, `bdd03b3`, `688d8b0`, `53f138c`, `0fc7309`, `1363c0a`, `f820dff`, `e873832`.

Validación final del bloque: `RegistrarMovimientoPanelTest` **4/4**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`, 03:27 min, finalización 12:39:51 -03:00.

## Bloque — Alta de cuentas

`RegistrarCuentaPanel` permite seleccionar tipo, institución financiera, moneda e identificador externo y delega a `CuentaService.registrar(cuenta, usuarioId)`. `CuentaService` soporta llamadas con o sin transacción activa. `CuentasPanel` refresca mediante callback. `MainFrame` conserva el constructor histórico por `perfilFinancieroId` y el contextual completo.

## Bloque — Inversiones y reportes

`InversionesPanel` muestra posiciones filtradas por usuario/perfil. `ReportesPanel` utiliza `CarteraActivoService` para reportes de movimientos. Ambos quedaron integrados en `MainFrame`.

`7ac8f99` integró inversiones y reportes. `c7cca8f` corrigió los constructores de `MainFrame` para conservar el `PerfilFinanciero` y permitir la carga contextual real.

## 2026-09-03 — Corrección de expectativa de test

`MainFrameMovimientosTest` tenía una expectativa incompatible con la regla del formulario: esperaba el botón habilitado con el nombre vacío. El test fue corregido en `29b5e11` para completar un nombre válido antes de comprobar la habilitación.

No se modificó código de producción.

## Validaciones — 2026-09-03

Batería relacionada de Swing: **20/20 tests en verde**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`, 09:43 min, finalización 10:55:56 -03:00.

`MainFrameMovimientosTest`: **3/3**, `BUILD SUCCESS`, finalización 10:45:01 -03:00.

`RegistrarMovimientoPanelTest`: **4/4**, `BUILD SUCCESS`, finalización 12:39:51 -03:00.

La última suite general sigue siendo la del 01/09/2026: **529/529**, `BUILD SUCCESS`, 14:25 min, finalización 19:25:53 -03:00.

Durante las pruebas Swing Surefire mostró el mensaje posterior a `System.exit(0)`, pero no produjo fallos ni errores.

## Estado Git — 2026-09-03

El usuario confirmó mediante `git syncsofp` que `feature/swing-shell` estaba sincronizada con GitHub y `git status` informó `nothing to commit, working tree clean`.

Último commit de código/test antes de esta actualización documental: `e873832` — `test: verificar fecha del sistema en selector de movimientos`.

`main` apunta a `a4be859` — `docs: crear contexto de continuidad actualizado`.

Comparación: **139 commits adelante y 2 atrás**, estado `diverged`, merge base `96f3d999`. Los dos commits exclusivos de `main` son documentales: `39badd1` y `a4be859`.

## Próximo avance

Definir el siguiente bloque funcional de Fase 8 a partir del código actual. No hacer merge automático a `main` ni crear ramas nuevas. Antes de una eventual integración, revisar explícitamente los dos commits documentales exclusivos de `main`.
