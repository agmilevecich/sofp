# SOFP — Pendientes

## Estado — 03/09/2026

**Rama estable:** `main` → `a4be859` (`docs: crear contexto de continuidad actualizado`).  
**Rama de trabajo:** `feature/swing-shell` → `29b5e11` (`test: corregir expectativa de habilitacion del alta de cuentas`).

Comparación: **122 commits por delante y 2 commits por detrás de `main`**, estado `diverged`. Merge base: `96f3d999`.

Los dos commits exclusivos de `main` son documentales: `39badd1` y `a4be859`.

## Seguridad — CERRADA

La etapa de seguridad y aislamiento por usuario está cerrada e integrada en `main`. Se completaron autorizaciones por propietario para perfiles, cuentas, categorías, movimientos, posiciones/cartera y operaciones financieras, además del cierre de caminos internos relevantes.

## Fase 8 — Swing

Implementados: `MainFrame`, `HeaderPanel`, `SidebarPanel`, `InicioPanel`, `CuentasPanel`, `MovimientosPanel`, `InversionesPanel`, `ReportesPanel`, `StatusBarPanel` y `ui.Main`.

`MainFrame` usa `CardLayout` para Inicio, Cuentas, Movimientos, Inversiones y Reportes. La UI integra los servicios existentes y no duplica reglas de negocio.

## Bloques cerrados

### Alta de movimientos

Formulario integrado con `MovimientoService.registrar(...)`, categoría autorizada y activa, tipo, importe, fecha/hora, descripción y `usuarioId`. Refresco del listado mediante callback.

Validación histórica: **10/10 tests en verde**.

### Alta de cuentas

`RegistrarCuentaPanel` permite tipo, institución, moneda e identificador externo y delega a `CuentaService.registrar(cuenta, usuarioId)`. Soporta alta con o sin transacción activa. `CuentasPanel` refresca mediante callback. `MainFrame` conserva el constructor histórico por `perfilFinancieroId` y el contextual.

### Inversiones y reportes

`InversionesPanel` muestra posiciones filtradas por usuario/perfil. `ReportesPanel` usa `CarteraActivoService` para reportes de movimientos. Ambos están integrados en `MainFrame`.

## Última validación

El **03/09/2026**:

`mvn -Dtest=InversionesPanelTest,ReportesPanelTest,MainFrameInversionesTest,MainFrameReportesTest,MainFrameMovimientosTest,CuentasPanelTest,RegistrarCuentaPanelTest test`

Resultado: **20/20**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`, **09:43 min**, finalización **10:55:56 -03:00**.

`MainFrameMovimientosTest` también fue ejecutado individualmente: **3/3**, `BUILD SUCCESS`, finalización **10:45:01 -03:00**.

La última suite general conocida continúa siendo **529/529**, ejecutada el 01/09/2026, `BUILD SUCCESS`, 14:25 min.

## Pendientes funcionales

No queda pendiente un arreglo dentro de los bloques validados de movimientos, cuentas, inversiones o reportes.

El próximo trabajo debe definirse como un nuevo bloque funcional de Fase 8, revisando antes el código actual, clases relacionadas, servicios, repositorios, reglas de negocio y tests.

## Pendientes de integración

No hacer merge automático a `main`. Antes de una eventual integración, revisar explícitamente los dos commits documentales exclusivos de `main`.

## Pendientes de validación

Cuando corresponda por alcance: tests específicos → tests relacionados → suite general → `git diff` → `git diff --check` → `git status`.

## Continuidad

`feature/swing-shell` sigue siendo la rama activa. No crear ramas nuevas. En una nueva sesión reconstruir el estado desde GitHub: código → tests → commits → `main` → documentación.
