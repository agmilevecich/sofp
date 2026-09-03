# SOFP — Pendientes

## Estado — 03/09/2026

**Rama estable:** `main` → `a4be859` (`docs: crear contexto de continuidad actualizado`).
**Rama de trabajo:** `feature/swing-shell` → `e873832` (`test: verificar fecha del sistema en selector de movimientos`) antes de esta actualización documental.

Comparación actual: **139 commits por delante y 2 commits por detrás de `main`**, estado `diverged`. Merge base: `96f3d999`.

Los dos commits exclusivos de `main` son documentales: `39badd1` y `a4be859`.

## Seguridad — CERRADA

La etapa de seguridad y aislamiento por usuario está cerrada e integrada en `main`. Se completaron autorizaciones por propietario para perfiles, cuentas, categorías, movimientos, posiciones/cartera y operaciones financieras, además del cierre de caminos internos relevantes.

## Fase 8 — Swing

Implementados: `MainFrame`, `HeaderPanel`, `SidebarPanel`, `InicioPanel`, `CuentasPanel`, `MovimientosPanel`, `InversionesPanel`, `ReportesPanel`, `StatusBarPanel` y `ui.Main`.

`MainFrame` usa `CardLayout` para Inicio, Cuentas, Movimientos, Inversiones y Reportes. La UI integra los servicios existentes y no duplica reglas de negocio.

## Bloques cerrados

### Alta de movimientos

Formulario integrado con `MovimientoService.registrar(...)`, categoría autorizada y activa, tipo, importe, fecha/hora, descripción y `usuarioId`. El listado se refresca mediante callback.

El campo de fecha utiliza **LGoodDatePicker**, con domingo como primer día y fecha inicial igual a la fecha del sistema. La hora ya no se ingresa manualmente: se toma de `LocalTime.now()` al registrar el movimiento.

Validación específica más reciente: **4/4 tests en verde**, `BUILD SUCCESS`.

### Alta de cuentas

`RegistrarCuentaPanel` permite tipo, institución, moneda e identificador externo y delega a `CuentaService.registrar(cuenta, usuarioId)`. Soporta alta con o sin transacción activa. `CuentasPanel` refresca mediante callback. `MainFrame` conserva el constructor histórico por `perfilFinancieroId` y el contextual.

### Inversiones y reportes

`InversionesPanel` muestra posiciones filtradas por usuario/perfil. `ReportesPanel` usa `CarteraActivoService` para reportes de movimientos. Ambos están integrados en `MainFrame`.

## Pendientes funcionales

No queda pendiente un arreglo dentro de los bloques validados de movimientos, cuentas, inversiones o reportes.

El próximo trabajo debe definirse como un nuevo bloque funcional de Fase 8, revisando antes el código actual, clases relacionadas, servicios, repositorios, reglas de negocio y tests.

## Pendientes de validación

La última suite general conocida continúa siendo **529/529**, ejecutada el 01/09/2026. Antes de cerrar un nuevo bloque: tests específicos → tests relacionados → suite general cuando corresponda → `git diff` → `git diff --check` → `git status`.

## Pendientes de integración

No hacer merge a `main` automáticamente. Antes de una eventual integración, revisar explícitamente los dos commits documentales exclusivos de `main`.

## Incidentes conocidos

Surefire muestra un mensaje posterior a `System.exit(0)` durante algunas pruebas Swing, pero las ejecuciones registradas terminan con `BUILD SUCCESS`. No se considera un fallo ni se modifica código especulativamente por ese mensaje.

Durante una prueba manual hubo una modificación accidental de `RegistrarMovimientoPanel.java` al escribir sobre el código. El archivo fue corregido y el posterior `git syncsofp` confirmó árbol limpio y sincronizado.

## Continuidad

`feature/swing-shell` sigue siendo la rama activa. No crear ramas nuevas. En una nueva sesión reconstruir el estado desde GitHub: código → tests → commits → `main` → documentación.
