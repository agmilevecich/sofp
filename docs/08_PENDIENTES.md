# SOFP — Pendientes

## Estado — 03/09/2026

**Rama estable:** `main` → `a4be859` (`docs: crear contexto de continuidad actualizado`).
**Rama de trabajo:** `feature/swing-shell` → `66b22f3` (`fix: gestionar transaccion al registrar categoria`) antes de esta actualización documental.

Comparación actual conocida: **139 commits por delante y 2 commits por detrás de `main`**, estado `diverged`. Merge base: `96f3d999`.

Los dos commits exclusivos de `main` son documentales: `39badd1` y `a4be859`.

## Seguridad — CERRADA

La etapa de seguridad y aislamiento por usuario está cerrada e integrada en `main`. Se completaron autorizaciones por propietario para perfiles, cuentas, categorías, movimientos, posiciones/cartera y operaciones financieras, además del cierre de caminos internos relevantes.

## Fase 8 — Swing

Implementados: `MainFrame`, `HeaderPanel`, `SidebarPanel`, `InicioPanel`, `CuentasPanel`, `CategoriasPanel`, `MovimientosPanel`, `InversionesPanel`, `ReportesPanel`, `StatusBarPanel`, `RegistrarCuentaPanel`, `RegistrarMovimientoPanel` y `ui.Main`.

`MainFrame` usa `CardLayout` para Inicio, Cuentas, Categorías, Movimientos, Inversiones y Reportes. La UI integra los servicios existentes y no duplica reglas de negocio.

## Bloques cerrados

### Alta de movimientos

Formulario integrado con `MovimientoService.registrar(...)`, categoría autorizada y activa, tipo, importe, fecha/hora, descripción y `usuarioId`. El listado se refresca mediante callback.

El campo de fecha utiliza **LGoodDatePicker**, con domingo como primer día y fecha inicial igual a la fecha del sistema. La hora se toma de `LocalTime.now()` al registrar el movimiento.

Validación conocida: **57/57 tests verdes** en el bloque de integración/servicio.

### Alta de cuentas

`RegistrarCuentaPanel` permite tipo, institución, moneda e identificador externo y delega a `CuentaService.registrar(cuenta, usuarioId)`. Soporta alta con o sin transacción activa. `CuentasPanel` refresca mediante callback. `MainFrame` conserva el constructor histórico por `perfilFinancieroId` y el contextual.

### Gestión de categorías

`CategoriasPanel` permite gestionar categorías del perfil del usuario y delega las reglas a `CategoriaService`.

Validación UI/navegación: **3/3**. Validación de servicio: **22/22**. Total: **25/25 tests verdes**.

`66b22f3` corrigió la persistencia del alta gestionando la transacción y `flush` cuando corresponde, sin duplicar las reglas de autorización.

### Inversiones y reportes

`InversionesPanel` muestra posiciones filtradas por usuario/perfil. `ReportesPanel` usa `CarteraActivoService` para reportes de movimientos. Ambos están integrados en `MainFrame`.

Validación UI: **5/5**. Validación de servicios: **16/16**. Total: **21/21 tests verdes**.

## Pendientes funcionales

No queda pendiente un arreglo dentro de los bloques validados de movimientos, cuentas, categorías, inversiones o reportes.

El próximo trabajo debe definirse como un nuevo bloque funcional de Fase 8, revisando antes el código actual, clases relacionadas, servicios, repositorios, reglas de negocio y tests.

No asumir que `LoginPanel` constituye por sí mismo el próximo bloque: debe analizarse su integración real y la cobertura existente antes de decidir.

## Pendientes de validación

La última suite general conocida continúa siendo **529/529**, ejecutada el 01/09/2026. Antes de cerrar un nuevo bloque: tests específicos → tests relacionados → suite general cuando corresponda → `git diff` → `git diff --check` → `git status`.

## Pendientes de integración

No hacer merge a `main` automáticamente. Antes de considerar una integración, revisar los commits actuales, comparar nuevamente con `main` y tratar explícitamente los dos commits documentales exclusivos de `main`.

## Incidentes conocidos

Surefire muestra un mensaje posterior a `System.exit(0)` durante algunas pruebas Swing, pero las ejecuciones registradas terminan con `BUILD SUCCESS`. No se considera un fallo ni se modifica código especulativamente por ese mensaje.

Durante una prueba manual hubo una modificación accidental de `RegistrarMovimientoPanel.java` al escribir sobre el código. El archivo fue corregido y el posterior `git syncsofp` confirmó árbol limpio y sincronizado.

## Continuidad

`feature/swing-shell` sigue siendo la rama activa. No crear ramas nuevas. En una nueva sesión reconstruir el estado desde GitHub: código → tests → commits → `main` → documentación.
