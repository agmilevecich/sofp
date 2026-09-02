# SOFP — Contexto para continuar con ChatGPT

La fuente de verdad es el código, Git y los tests actuales; `docs/` es documentación auxiliar y puede quedar desactualizada. Antes de proponer cambios, reconstruir siempre el estado desde GitHub.

## Estado — 02/09/2026

**Rama estable:** `main`.  
Último commit integrado conocido: `a4be859` — `docs: crear contexto de continuidad actualizado`.  
La rama `feature/seguridad-aislamiento-datos` fue integrada en `main` mediante fast-forward.

**Rama de trabajo:** `feature/swing-shell`.  
**Último commit funcional:** `e8c9c50` — `test: cubrir alta real de movimientos desde formulario`.

La rama de trabajo continúa separada de `main` y está **73 commits por delante y 2 commits por detrás**. Los 2 commits que están por delante en `main` corresponden a documentación posterior y no se incorporan automáticamente.

No se crean ramas nuevas para continuar este trabajo.

## Seguridad

La auditoría transversal de seguridad y aislamiento de datos quedó completada e integrada en `main`.

Implementado:

- autorización de operaciones financieras;
- aislamiento de cuentas, categorías y movimientos;
- lecturas por ID y listados con usuario propietario;
- altas protegidas de cuentas, categorías, movimientos y perfiles;
- aislamiento de posiciones y cartera por usuario/perfil;
- cierre de caminos internos que podían saltar validaciones públicas;
- cobertura transversal en `AislamientoDatosServiceTest`.

## Shell Swing — Fase 8

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

`MainFrame` utiliza `CardLayout` y navega entre Inicio, Cuentas, Movimientos, Inversiones y Reportes. La UI integra los servicios existentes respetando el contexto de usuario/perfil y no duplica reglas de negocio.

## Bloque cerrado — Alta de movimientos

`RegistrarMovimientoPanel` permite registrar movimientos para la cuenta seleccionada utilizando categoría autorizada y activa, tipo de movimiento, importe, fecha/hora, descripción y `usuarioId`.

El alta se delega a `MovimientoService.registrar(...)`. Después de una registración exitosa, `MovimientosPanel` recibe un callback y actualiza el listado.

Se separó `registrarMovimiento()` para probar el alta real sin abrir diálogos Swing, manteniendo la interacción visual en `registrar()`.

La prueba `deberiaRegistrarMovimientoYNotificarAlContenedor` confirma persistencia en H2, datos principales y notificación al contenedor.

## Tests

Última suite general disponible, ejecutada el **01/09/2026**:

- **529/529 tests en verde**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración: **14:25 min**;
- finalización: **19:25:53 -03:00**.

Suite específica del bloque de alta, ejecutada el **02/09/2026**:

`mvn -Dtest=RegistrarMovimientoPanelTest,MovimientosPanelTest,MainFrameMovimientosTest,MainFrameNavigationTest test`

- **10/10 tests en verde**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración: **04:38 min**;
- finalización: **12:26:39 -03:00**.

`RegistrarMovimientoPanelTest`: **4/4**.

Surefire mostró el mensaje conocido de espera posterior a `System.exit(0)`, pero la ejecución terminó correctamente. No se modificó código especulativamente por ese mensaje.

## Continuidad

- No hacer merge automático a `main`.
- No crear nuevas ramas; continuar sobre `feature/swing-shell`.
- Antes de modificar una clase, revisar implementación actual, clases relacionadas, servicios, repositorios, tests y reglas de negocio.
- Mantener cambios pequeños y descriptivos.
- No duplicar lógica de negocio en la UI.
- Después de cambios importantes: tests específicos, tests relacionados y suite completa cuando corresponda; revisar `git diff`, `git diff --check` y `git status`.
- Ante una nueva sesión de SOFP, reconstruir el estado desde GitHub: código → tests → commits → `main` → documentación.

## Próximo paso

El bloque de alta de movimientos está cerrado dentro de su alcance. El próximo trabajo debe definirse como un nuevo bloque funcional de Fase 8, partiendo del estado real de `feature/swing-shell`.
