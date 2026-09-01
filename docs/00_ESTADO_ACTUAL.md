# SOFP — Estado actual

> Documento de continuidad. El código y los tests actuales son la fuente de verdad técnica.

## Estado verificado — 01/09/2026

**Rama estable:** `main`  
**Último commit integrado:** `96f3d99` — `docs: cerrar historial de build de seguridad`  
**Feature integrada:** `feature/seguridad-aislamiento-datos` mediante fast-forward.

**Rama de trabajo:** `feature/swing-shell`  
**Último commit:** `ca70f28` — `fix: conservar entrada Swing existente en ui`.

La rama de trabajo está **46 commits por delante de `main` y 0 commits por detrás**.

## Validación actual

Suite general ejecutada localmente el **01/09/2026** después de limpiar artefactos compilados obsoletos:

- Tests run: **525**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **32:12 min**

La validación global vigente es **525/525 tests en verde**.

La ejecución previa había mostrado un único fallo de `SwingApplicationTest` por una clase compilada obsoleta en `target`. La limpieza de Maven eliminó ese artefacto y la suite completa quedó completamente verde, sin modificar código ni tests por ese motivo.

## Seguridad implementada

- `PerfilFinancieroService`: lecturas y alta protegidas por usuario propietario.
- `CuentaService`: lecturas por ID, listados por perfil, saldo, evolución y alta protegidos por usuario.
- `CategoriaService`: lectura por ID, listado por perfil y alta protegidos por usuario.
- `MovimientoService`: lectura por ID, listados por cuenta/categoría y alta protegidos por usuario.
- `PosicionActivoService`: consulta pública protegida por propietario del perfil.
- `CarteraActivoService`: posiciones, valorizaciones, reporte, composición y movimientos protegidos por propietario del perfil.
- `OperacionFinancieraService`: transferencia, compra y venta exigen usuario y validan propiedad de los recursos involucrados.
- Se cerraron caminos internos que podían permitir saltar validaciones públicas.
- `AislamientoDatosServiceTest` cubre recursos propios y ajenos y los principales caminos de lectura/creación.

## Estado de la interfaz — Fase 8 / `feature/swing-shell`

El bloque de shell Swing quedó implementado y validado.

Componentes actuales:

- `MainFrame` como ventana principal;
- `HeaderPanel`;
- `SidebarPanel` con cinco botones: Inicio, Cuentas, Movimientos, Inversiones y Reportes;
- área central basada en `CardLayout`;
- tarjetas para Inicio, Cuentas, Movimientos e Inversiones;
- `StatusBarPanel`;
- punto de entrada `ar.com.agmilevecich.sofp.ui.Main`.

La UI ya integra el contexto existente para:

- listar cuentas del perfil/usuario;
- conservar la cuenta seleccionada;
- listar movimientos de la cuenta seleccionada con autorización por usuario;
- mostrar posiciones de inversión del perfil con autorización por usuario;
- navegar entre las tarjetas desde el shell principal.

Tests específicos de UI:

- `CuentasPanelTest`;
- `MovimientosPanelTest`;
- `MainFrameTest`;
- `MainFrameLayoutTest`;
- `MainFrameNavigationTest`;
- `MainFrameMovimientosTest`;
- `InversionesPanelTest`;
- `MainFrameInversionesTest`.

## Criterio de cierre de la feature

La implementación actual de `feature/swing-shell` está funcionalmente cubierta por tests y la suite general está en verde. No se realiza merge automático a `main`.

El siguiente trabajo de Fase 8 deberá definirse como un nuevo bloque funcional, manteniendo el mismo criterio: partir del código existente, cambios pequeños, tests específicos y sin duplicar lógica de negocio.
