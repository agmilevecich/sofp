# SOFP — Contexto para continuar con ChatGPT

La fuente de verdad es el código, Git y los tests actuales; `docs/` es documentación auxiliar. Antes de proponer cambios, reconstruir siempre el estado desde GitHub.

## Estado verificado — 03/09/2026

**Rama estable:** `main`. Último commit: `a4be859` — `docs: crear contexto de continuidad actualizado`.

**Rama de trabajo:** `feature/swing-shell`. Último commit de código/test antes de esta actualización documental: `29b5e11` — `test: corregir expectativa de habilitacion del alta de cuentas`.

Comparación actual: **122 commits por delante y 2 commits por detrás de `main`**, estado `diverged`. Merge base: `96f3d999`. Los dos commits exclusivos de `main` (`39badd1` y `a4be859`) son documentales.

El estado local fue verificado el 03/09/2026: `git syncsofp` informó `Already up to date` / `Everything up-to-date` y `git status` informó `nothing to commit, working tree clean` antes de iniciar esta actualización documental.

## Seguridad

La auditoría transversal de seguridad y aislamiento de datos está completada e integrada en `main`.

Se mantienen protegidos por propietario/usuario perfiles, cuentas, categorías, movimientos, posiciones/cartera y operaciones financieras. También se cerraron caminos internos que podían saltar las validaciones públicas.

## Fase 8 — Shell Swing

Componentes implementados: `MainFrame`, `HeaderPanel`, `SidebarPanel`, `InicioPanel`, `CuentasPanel`, `MovimientosPanel`, `InversionesPanel`, `ReportesPanel`, `StatusBarPanel` y `ar.com.agmilevecich.sofp.ui.Main`.

`MainFrame` usa `CardLayout` y navega entre Inicio, Cuentas, Movimientos, Inversiones y Reportes. La UI utiliza los servicios existentes y no duplica reglas de negocio.

## Bloques cerrados

### Alta de movimientos

`RegistrarMovimientoPanel` registra movimientos para la cuenta seleccionada con categoría autorizada y activa, tipo, importe, fecha/hora, descripción y `usuarioId`. `MovimientosPanel` refresca el listado mediante callback.

Validación histórica: **10/10 tests en verde**.

### Alta de cuentas

`RegistrarCuentaPanel` permite seleccionar tipo, institución financiera, moneda e identificador externo y delega a `CuentaService.registrar(cuenta, usuarioId)`. `CuentaService` soporta alta con o sin transacción activa. `CuentasPanel` refresca mediante callback. `MainFrame` conserva el constructor histórico por `perfilFinancieroId` y el contextual completo.

### Inversiones y reportes

`InversionesPanel` muestra posiciones filtradas por usuario/perfil. `ReportesPanel` usa `CarteraActivoService` para el reporte de movimientos. Ambos están integrados en `MainFrame`.

La integración fue corregida en `c7cca8f` para conservar el `PerfilFinanciero` al delegar desde los constructores públicos de `MainFrame`.

## Última validación específica

El **03/09/2026** se ejecutó:

`mvn -Dtest=InversionesPanelTest,ReportesPanelTest,MainFrameInversionesTest,MainFrameReportesTest,MainFrameMovimientosTest,CuentasPanelTest,RegistrarCuentaPanelTest test`

Resultado: **20/20 tests en verde**, Failures **0**, Errors **0**, Skipped **0**, `BUILD SUCCESS`, duración **09:43 min**, finalización **10:55:56 -03:00**.

`MainFrameMovimientosTest` también fue ejecutado individualmente: **3/3**, `BUILD SUCCESS`, duración **02:12 min**, finalización **10:45:01 -03:00**.

Última suite general: **529/529**, ejecutada el 01/09/2026, `BUILD SUCCESS`, 14:25 min.

## Últimos cambios

- `7ac8f99` — `fix: integrar inversiones y reportes en MainFrame`;
- `c7cca8f` — `fix: conservar perfil en constructores de MainFrame`;
- `29b5e11` — `test: corregir expectativa de habilitacion del alta de cuentas`.

El último cambio de código/test fue exclusivamente de test: la prueba de alta de cuentas completa el nombre antes de comprobar que el botón se habilita, respetando la regla existente del formulario.

## Incidentes conocidos

Surefire muestra durante las pruebas Swing un mensaje de espera posterior a `System.exit(0)`. En las ejecuciones registradas terminó con `BUILD SUCCESS`, sin failures ni errors. No se realiza un cambio especulativo por ese mensaje.

Una ejecución anterior involucró un `SwingApplicationTest` obsoleto presente en `target`; se resolvió limpiando Maven, sin modificar código ni tests para ocultar el problema.

## Reglas de continuidad

- No hacer merge automático a `main`.
- No crear nuevas ramas; continuar sobre `feature/swing-shell`.
- Antes de modificar una clase, revisar implementación, clases relacionadas, servicios, repositorios, tests y reglas de negocio.
- Mantener cambios pequeños y descriptivos.
- No duplicar lógica de negocio en la UI.
- Validar con tests específicos, relacionados y suite general cuando corresponda.
- Revisar `git diff`, `git diff --check` y `git status` después de cambios importantes.
- Ante una nueva sesión: código → tests → commits → `main` → documentación.

## Próximo paso

Definir el próximo bloque funcional de Fase 8 únicamente después de reconstruir el estado real de `feature/swing-shell`. Antes de una eventual integración, revisar explícitamente los dos commits documentales exclusivos de `main`.
