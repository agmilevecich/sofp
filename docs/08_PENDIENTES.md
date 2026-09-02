# SOFP — Pendientes

## Estado — 02/09/2026

**Rama estable:** `main`.  
**Último commit integrado:** `75d0a18` — `docs: actualizar contexto tras cierre de seguridad`.  
**Rama de trabajo:** `feature/swing-shell`.  
**Último commit actual:** `0d7769e` — `fix: conservar listado de cuentas con perfil id`.

La comparación verificada es **90 commits por delante y 2 commits por detrás de `main`**. La feature permanece separada y no se sincronizan automáticamente los commits de documentación de `main`.

## Seguridad

La etapa de seguridad y aislamiento de datos está **cerrada e integrada en `main`**.

Se completaron autorizaciones por usuario/propietario para perfiles, cuentas, categorías, movimientos, posiciones/cartera y operaciones financieras, además del cierre de caminos internos que podían saltar las validaciones públicas.

## Fase 8 — Interfaz de usuario Swing

El shell Swing está implementado en `feature/swing-shell` con `MainFrame`, `HeaderPanel`, `SidebarPanel`, `InicioPanel`, `CuentasPanel`, `MovimientosPanel`, `InversionesPanel`, `ReportesPanel`, `StatusBarPanel` y `ui.Main`.

La interfaz utiliza los servicios existentes y mantiene las reglas de negocio fuera de la UI.

## Bloque cerrado — Alta de movimientos

`RegistrarMovimientoPanel` permite registrar movimientos para la cuenta seleccionada utilizando categoría autorizada y activa, tipo, importe, fecha/hora, descripción y `usuarioId`.

El alta se delega a `MovimientoService.registrar(...)`. Después de una registración exitosa, `MovimientosPanel` recibe un callback y refresca el listado.

Validación específica histórica: **10/10 tests en verde**.

## Bloque cerrado — Alta de cuentas

`RegistrarCuentaPanel` permite registrar una cuenta seleccionando tipo, institución financiera, moneda e identificador externo. El alta se delega a `CuentaService.registrar(cuenta, usuarioId)`.

`CuentasPanel` actualiza el listado mediante callback después de un alta exitosa.

`MainFrame` conserva el constructor histórico basado en `perfilFinancieroId` y el constructor contextual completo con `PerfilFinanciero`, `InstitucionFinancieraService` y `MonedaService`.

`CuentaService` soporta el alta con o sin una transacción ya activa.

## Validación más reciente

Suite ejecutada localmente el **02/09/2026**:

`mvn -Dtest=RegistrarCuentaPanelTest,CuentasPanelTest,MainFrameMovimientosTest test`

- Tests run: **11**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **05:24 min**
- Finalización: **14:05:26 -03:00**

Detalle: `RegistrarCuentaPanelTest` **5/5**, `CuentasPanelTest` **3/3**, `MainFrameMovimientosTest` **3/3**.

## Pendientes funcionales

No queda pendiente un arreglo dentro de los bloques de alta de movimientos o alta de cuentas que fueron validados.

El próximo trabajo debe definirse como un **nuevo bloque funcional de Fase 8**, sin asumir la funcionalidad siguiente hasta revisar el código actual, clases relacionadas, servicios, repositorios, reglas de negocio y tests.

## Pendientes de validación futura

La suite general debe volver a ejecutarse cuando se cierre un nuevo bloque importante o cuando corresponda por alcance. La última suite general disponible sigue siendo **529/529 tests en verde**, ejecutada el 01/09/2026.

Después de cambios importantes también corresponde revisar `git diff`, `git diff --check` y `git status`.

## Criterio de continuidad

No hacer merge automático a `main`.

No crear nuevas ramas para continuar este trabajo; seguir sobre `feature/swing-shell`.

Ante una nueva sesión, reconstruir el estado desde GitHub: código → tests → commits → `main` → documentación.
