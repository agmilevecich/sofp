# SOFP — Pendientes

## Estado — 02/09/2026

**Rama estable:** `main`.  
**Último commit integrado conocido:** `a4be859` — `docs: crear contexto de continuidad actualizado`.  
**Rama de trabajo:** `feature/swing-shell`.  
**Último commit funcional:** `e8c9c50` — `test: cubrir alta real de movimientos desde formulario`.

La rama de trabajo continúa separada de `main` y la comparación actual es **73 commits por delante y 2 commits por detrás**. Los commits que están por delante de la feature en `main` corresponden a documentación posterior y no se incorporan automáticamente.

## Seguridad

La etapa de seguridad y aislamiento de datos está **cerrada e integrada en `main`**.

Se completaron autorizaciones por usuario/propietario para perfiles, cuentas, categorías, movimientos, posiciones/cartera y operaciones financieras, además del cierre de caminos internos que podían saltar las validaciones públicas.

## Fase 8 — Interfaz de usuario Swing

El shell Swing está implementado en `feature/swing-shell` con:

- `MainFrame`;
- `HeaderPanel`;
- `SidebarPanel`;
- `InicioPanel`;
- `CuentasPanel`;
- `MovimientosPanel`;
- `InversionesPanel`;
- `ReportesPanel`;
- `StatusBarPanel`;
- `ui.Main`.

La interfaz usa los servicios existentes y mantiene las reglas de negocio fuera de la UI.

## Bloque cerrado — Alta de movimientos

El formulario `RegistrarMovimientoPanel` permite registrar movimientos para la cuenta seleccionada utilizando categoría autorizada y activa, tipo, importe, fecha/hora, descripción y `usuarioId`.

El alta se delega a `MovimientoService.registrar(...)`. Después de una registración exitosa, `MovimientosPanel` recibe una notificación y refresca el listado.

Este bloque está **implementado y cerrado dentro de su alcance**.

## Validación

Suite específica ejecutada localmente el **02/09/2026**:

- Tests run: **10**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **04:38 min**
- Finalización: **12:26:39 -03:00**

La última suite general disponible sigue siendo la del **01/09/2026**, con **529/529 tests en verde**, `BUILD SUCCESS`, 0 failures, 0 errors y 0 skipped.

## Pendientes funcionales

No queda pendiente un arreglo dentro del bloque de alta de movimientos.

El próximo trabajo debe definirse como un **nuevo bloque funcional de Fase 8**. No se debe asumir cuál será la funcionalidad siguiente hasta revisar el estado real de la rama, las clases relacionadas, servicios, repositorios, reglas de negocio y tests.

## Pendientes de validación futura

Cuando se complete un nuevo bloque funcional importante, corresponde ejecutar los tests específicos, tests relacionados y la suite general cuando resulte apropiado, además de revisar `git diff`, `git diff --check` y `git status`.

## Criterio de continuidad

No hacer merge automático a `main`.

No crear nuevas ramas para continuar este trabajo; seguir sobre `feature/swing-shell`.

Ante una nueva sesión, reconstruir el estado desde GitHub: código → tests → commits → `main` → documentación.
