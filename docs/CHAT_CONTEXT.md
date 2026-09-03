# SOFP — Contexto para continuar con ChatGPT

La fuente de verdad es el código, Git y los tests actuales; `docs/` es documentación auxiliar. Antes de proponer cambios, reconstruir siempre el estado desde GitHub.

## Estado verificado — 03/09/2026

**Rama estable:** `main`. Último commit funcional conocido: `a4be859` — `docs: crear contexto de continuidad actualizado`.

**Rama de trabajo:** `feature/swing-shell`. Último commit funcional antes de las actualizaciones documentales: `66b22f3` — `fix: gestionar transaccion al registrar categoria`.

La comparación conocida antes de las actualizaciones documentales era **139 commits por delante y 2 por detrás de `main`**, estado `diverged`. Merge base: `96f3d999`. Los dos commits exclusivos de `main` (`39badd1` y `a4be859`) son documentales.

El usuario confirmó el 03/09/2026 que `git syncsofp` informó `Already up to date` / `Everything up-to-date` y que la validación final con `git diff`, `git diff --check` y `git status` terminó con `nothing to commit, working tree clean`.

## Seguridad

La auditoría transversal de seguridad y aislamiento de datos está completada e integrada en `main`.

Se mantienen protegidos por propietario/usuario perfiles, cuentas, categorías, movimientos, posiciones/cartera y operaciones financieras. También se cerraron caminos internos que podían saltar las validaciones públicas.

## Fase 8 — Shell Swing

Componentes implementados: `MainFrame`, `HeaderPanel`, `SidebarPanel`, `InicioPanel`, `CuentasPanel`, `CategoriasPanel`, `MovimientosPanel`, `InversionesPanel`, `ReportesPanel`, `StatusBarPanel`, `RegistrarCuentaPanel`, `RegistrarMovimientoPanel` y `ar.com.agmilevecich.sofp.ui.Main`.

`MainFrame` usa `CardLayout` y navega entre Inicio, Cuentas, Categorías, Movimientos, Inversiones y Reportes. La UI utiliza los servicios existentes y no duplica reglas de negocio.

## Bloques cerrados

### Alta de movimientos

`RegistrarMovimientoPanel` registra movimientos para la cuenta seleccionada con categoría autorizada y activa, tipo, importe, fecha/hora, descripción y `usuarioId`. `MovimientosPanel` refresca el listado mediante callback.

El campo de fecha utiliza **LGoodDatePicker (`DatePicker`)**, con configuración `es-AR`, domingo como primer día, fecha inicial igual a `LocalDate.now()` y formato `dd/MM/uuuu`.

La hora ya no se ingresa mediante ComboBox. Al registrar, se obtiene con `LocalTime.now()` y se combina con la fecha seleccionada.

Validación conocida del bloque: **57/57 tests en verde**.

### Alta de cuentas

`RegistrarCuentaPanel` permite seleccionar tipo, institución financiera, moneda e identificador externo y delega a `CuentaService.registrar(cuenta, usuarioId)`. `CuentaService` soporta alta con o sin transacción activa. `CuentasPanel` refresca mediante callback. `MainFrame` conserva el constructor histórico por `perfilFinancieroId` y el contextual completo.

### Gestión de categorías

`CategoriasPanel` permite gestionar categorías del perfil del usuario mediante `CategoriaService`.

Validación UI/navegación: **3/3**. Validación del servicio: **22/22**. Total: **25/25 tests en verde**.

`66b22f3` corrigió la persistencia del alta gestionando la transacción y `flush` cuando corresponde, manteniendo la autorización en el servicio.

### Inversiones y reportes

`InversionesPanel` muestra posiciones filtradas por usuario/perfil. `ReportesPanel` usa `CarteraActivoService` para el reporte de movimientos. Ambos están integrados en `MainFrame`.

Validación UI: **5/5**. Validación de servicios: **16/16**. Total: **21/21 tests en verde**.

## Validaciones

- Categorías: **25/25** tests verdes.
- Movimientos: **57/57** tests verdes.
- Inversiones/reportes: **21/21** tests verdes.
- `RegistrarMovimientoPanelTest`: **4/4** en validación individual del selector de fecha/hora.
- Última suite general conocida: **529/529**, ejecutada el 01/09/2026, `BUILD SUCCESS`, 14:25 min, finalización 19:25:53 -03:00.

Las validaciones específicas del 03/09/2026 no sustituyen todavía una nueva suite general.

## Último cambio funcional

`66b22f3` — `fix: gestionar transaccion al registrar categoria`.

Los commits documentales posteriores solo actualizan continuidad y no cambian funcionalidad.

## Incidentes conocidos

Surefire muestra durante algunas pruebas Swing el mensaje `Surefire is going to kill self fork JVM. The exit has elapsed 30 seconds after System.exit(0)`. En las ejecuciones registradas terminó con `BUILD SUCCESS`, sin failures ni errors. No se realiza un cambio especulativo por ese mensaje.

Durante una prueba manual hubo una modificación accidental de `RegistrarMovimientoPanel.java` al escribir sobre el código. El archivo fue corregido y el posterior `git syncsofp` confirmó árbol limpio y sincronizado.

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

Definir el próximo bloque funcional de Fase 8 únicamente después de reconstruir el estado real de `feature/swing-shell`. No asumir que `LoginPanel` es el próximo bloque sin revisar previamente su integración, servicios relacionados y tests.
