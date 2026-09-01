# SOFP — Contexto para continuar con ChatGPT

La fuente de verdad es el código, Git y los tests actuales; `docs/` es documentación auxiliar y puede quedar desactualizada. Antes de proponer cambios, reconstruir siempre el estado desde GitHub.

## Estado — 01/09/2026

**Rama estable:** `main`.  
Último commit integrado: `96f3d99` — `docs: cerrar historial de build de seguridad`.  
La rama `feature/seguridad-aislamiento-datos` fue integrada en `main` mediante fast-forward.

**Rama de trabajo:** `feature/swing-shell`.  
**Último commit funcional previo al bloque documental:** `6621615` — `test: cubrir navegacion de reportes`.  
La rama está **52 commits por delante de `main` y 0 commits por detrás**.

## Shell Swing — Fase 8

El bloque actual está implementado en `feature/swing-shell` con `MainFrame`, `HeaderPanel`, `SidebarPanel`, `InicioPanel`, `CuentasPanel`, `MovimientosPanel`, `InversionesPanel`, `ReportesPanel`, `StatusBarPanel` y `ui.Main`.

`MainFrame` usa `CardLayout` y navega entre Inicio, Cuentas, Movimientos, Inversiones y Reportes. La UI integra los servicios existentes respetando el contexto de usuario/perfil y no duplica reglas de negocio. `ReportesPanel` utiliza el reporte de movimientos de inversión existente en `CarteraActivoService`.

## Seguridad

La auditoría transversal de seguridad y aislamiento de datos quedó completada e integrada en `main`, cubriendo perfiles, cuentas, categorías, movimientos, posiciones/cartera y operaciones financieras, con autorización por propietario y cobertura transversal en `AislamientoDatosServiceTest`.

## Tests

Suite general ejecutada localmente el **01/09/2026**:

- Tests run: **529**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **14:25 min**
- Finalización: **19:25:53 -03:00**

Validación específica de reportes: `ReportesPanelTest` **3/3**, `MainFrameReportesTest` **1/1**, total **4/4**. La suite relacionada de UI quedó en **13/13**.

Una ejecución previa había detectado artefactos compilados obsoletos en `target`; la limpieza de Maven permitió la ejecución definitiva de **529/529** sin modificar código ni tests por ese motivo.

Surefire mostró durante ejecuciones de UI un mensaje de espera posterior a `System.exit(0)`, pero el build terminó con `BUILD SUCCESS`, sin failures ni errors. No se modificó código especulativamente por ese mensaje.

## Continuidad

- No hacer merge automático a `main`.
- No crear nuevas ramas para continuar este trabajo; seguir sobre `feature/swing-shell`.
- Antes de modificar una clase, revisar implementación actual, clases relacionadas, servicios, repositorios, tests y reglas de negocio.
- Mantener cambios pequeños y descriptivos.
- No duplicar lógica de negocio en la UI.
- Después de cambios importantes: tests específicos, tests relacionados y suite completa cuando corresponda; revisar diff, `git diff --check` y `git status`.
- Ante una nueva sesión de SOFP, reconstruir el estado desde GitHub: código → tests → commits → `main` → documentación.

## Próximo paso

El alcance actual del shell Swing queda validado. El próximo trabajo debe definirse como un nuevo bloque funcional de Fase 8, partiendo del estado real de `feature/swing-shell`.
