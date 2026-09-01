# SOFP — Contexto para continuar con ChatGPT

La fuente de verdad es el código, Git y los tests actuales; `docs/` es documentación auxiliar.

## Estado — 01/09/2026

Rama estable: `main`.
Último commit integrado: `96f3d99` — `docs: cerrar historial de build de seguridad`.
La rama `feature/seguridad-aislamiento-datos` fue integrada en `main` mediante fast-forward.

Rama de trabajo: `feature/swing-shell`.
Último commit: `ca70f28` — `fix: conservar entrada Swing existente en ui`.
La rama está 46 commits por delante de `main` y 0 por detrás.

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

## Shell Swing

El bloque actual de Fase 8 está implementado en `feature/swing-shell`.

Componentes principales:

- `MainFrame`;
- `HeaderPanel`;
- `SidebarPanel`;
- `InicioPanel`;
- `CuentasPanel`;
- `MovimientosPanel`;
- `InversionesPanel`;
- `StatusBarPanel`;
- `ui.Main`.

La UI integra los servicios existentes respetando el contexto de usuario/perfil. Cuentas, movimientos e inversiones se muestran mediante los servicios autorizados, sin duplicar reglas de negocio en la interfaz.

## Tests

Suite general ejecutada localmente el **01/09/2026** después de limpiar artefactos compilados obsoletos:

- Tests run: **525**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **32:12 min**.

Tests específicos del shell cubren paneles, estructura, layout, navegación e integración de cuentas, movimientos e inversiones.

## Incidente resuelto

Una ejecución previa intentó ejecutar `SwingApplicationTest` desde `target` aunque el test ya no estaba versionado en la rama, produciendo `NoClassDefFoundError` para `ar.com.agmilevecich.sofp.app.SwingApplication`.

Se limpió el proyecto con Maven y se volvió a ejecutar la suite. El resultado definitivo fue **525/525 tests en verde**.

## Continuidad

No hacer merge automático a `main`.

El bloque actual de `feature/swing-shell` está validado dentro de su alcance. El siguiente trabajo debe definirse como un nuevo bloque funcional de Fase 8 y reconstruirse desde GitHub antes de modificar código.
