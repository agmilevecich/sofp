# SOFP — Contexto para continuar con ChatGPT

La fuente de verdad es el código, Git y los tests actuales; `docs/` es documentación auxiliar y puede quedar desactualizada. Antes de proponer cambios, reconstruir siempre el estado desde GitHub.

## Estado — 02/09/2026

**Rama estable:** `main`.  
Último commit integrado: `75d0a18` — `docs: actualizar contexto tras cierre de seguridad`.

**Rama de trabajo:** `feature/swing-shell`.  
**Último commit actual:** `0d7769e` — `fix: conservar listado de cuentas con perfil id`.

La comparación verificada es **90 commits por delante y 2 commits por detrás de `main`**. La feature permanece separada de `main` y los commits de documentación de `main` no se incorporan automáticamente.

No se crean ramas nuevas para continuar este trabajo.

## Seguridad

La auditoría transversal de seguridad y aislamiento de datos quedó completada e integrada en `main`.

Implementado y validado:

- autorización de operaciones financieras;
- aislamiento de cuentas, categorías y movimientos;
- lecturas por ID y listados con usuario propietario;
- altas protegidas de cuentas, categorías, movimientos y perfiles;
- aislamiento de posiciones y cartera por usuario/perfil;
- cierre de caminos internos que podían saltar validaciones públicas;
- cobertura transversal en `AislamientoDatosServiceTest`.

## Shell Swing — Fase 8

El shell Swing está implementado en `feature/swing-shell` con `MainFrame`, `HeaderPanel`, `SidebarPanel`, `InicioPanel`, `CuentasPanel`, `MovimientosPanel`, `InversionesPanel`, `ReportesPanel`, `StatusBarPanel` y `ui.Main`.

`MainFrame` utiliza `CardLayout` y navega entre Inicio, Cuentas, Movimientos, Inversiones y Reportes. La UI integra los servicios existentes respetando el contexto de usuario/perfil y no duplica reglas de negocio.

## Bloques funcionales cerrados

### Alta de movimientos

`RegistrarMovimientoPanel` permite registrar movimientos para la cuenta seleccionada con categoría autorizada y activa, tipo, importe, fecha/hora, descripción y `usuarioId`. `MovimientosPanel` refresca el listado mediante callback después de un alta exitosa.

Validación específica: **10/10 tests en verde**.

### Alta de cuentas

`RegistrarCuentaPanel` permite registrar una cuenta con tipo, institución financiera, moneda e identificador externo y delega a `CuentaService.registrar(cuenta, usuarioId)`.

`CuentasPanel` refresca el listado mediante callback. `MainFrame` conserva el constructor histórico basado en `perfilFinancieroId` y dispone de un constructor contextual completo con `PerfilFinanciero`, `InstitucionFinancieraService` y `MonedaService`.

`CuentaService` soporta llamadas con o sin una transacción activa.

## Tests

Última suite general disponible, ejecutada el **01/09/2026**:

- **529/529 tests en verde**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración: **14:25 min**;
- finalización: **19:25:53 -03:00**.

Última suite específica, ejecutada el **02/09/2026**:

`mvn -Dtest=RegistrarCuentaPanelTest,CuentasPanelTest,MainFrameMovimientosTest test`

- **11/11 tests en verde**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración: **05:24 min**;
- finalización: **14:05:26 -03:00**.

Detalle: `RegistrarCuentaPanelTest` **5/5**, `CuentasPanelTest` **3/3**, `MainFrameMovimientosTest` **3/3**.

La ejecución específica confirmó alta real de cuentas, persistencia, callback de refresco y compatibilidad del constructor histórico de `MainFrame`.

## Incidentes conocidos

Una ejecución anterior falló por un `SwingApplicationTest` obsoleto presente en `target`; la limpieza de Maven eliminó el artefacto sin modificar código ni tests para hacer pasar la suite.

Durante las ejecuciones de UI Surefire mostró un mensaje de espera posterior a `System.exit(0)`. Las ejecuciones terminaron con `BUILD SUCCESS`, sin failures ni errors, por lo que no se realizó un cambio especulativo.

## Reglas de continuidad

- No hacer merge automático a `main`.
- No crear nuevas ramas; continuar sobre `feature/swing-shell`.
- Antes de modificar una clase, revisar implementación actual, clases relacionadas, servicios, repositorios, tests y reglas de negocio.
- Mantener cambios pequeños y descriptivos.
- No duplicar lógica de negocio en la UI.
- Después de cambios importantes: tests específicos, tests relacionados y suite completa cuando corresponda; revisar `git diff`, `git diff --check` y `git status`.
- Ante una nueva sesión, reconstruir el estado desde GitHub: código → tests → commits → `main` → documentación.

## Próximo paso

Definir el próximo bloque funcional de Fase 8 únicamente a partir del estado real de `feature/swing-shell`, sin asumir funcionalidades no implementadas.
