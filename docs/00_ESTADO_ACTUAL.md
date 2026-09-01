# SOFP — Estado actual

> Documento de continuidad. La fuente de verdad técnica es el código y los tests actuales; `docs/` es documentación auxiliar.

## Estado verificado — 01/09/2026

**Rama estable:** `main`  
**Último commit integrado:** `96f3d99` — `docs: cerrar historial de build de seguridad`  
**Feature integrada:** `feature/seguridad-aislamiento-datos` mediante fast-forward.

**Rama de trabajo:** `feature/swing-shell`  
**Último commit:** `6621615` — `test: cubrir navegacion de reportes`.

La rama de trabajo está **52 commits por delante de `main` y 0 commits por detrás**.

## Validación actual

Suite general ejecutada localmente el **01/09/2026**, después de limpiar artefactos compilados obsoletos:

- Tests run: **529**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **14:25 min**
- Finalización: **19:25:53 -03:00**

La validación global vigente es **529/529 tests en verde**.

La ejecución anterior había mostrado un fallo de `SwingApplicationTest` provocado por artefactos compilados obsoletos en `target`. La limpieza de Maven eliminó ese artefacto y la suite completa volvió a quedar verde, sin modificar código ni tests por ese motivo.

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

El bloque actual del shell Swing está implementado y validado dentro de su alcance.

Componentes actuales:

- `MainFrame` como ventana principal;
- `HeaderPanel`;
- `SidebarPanel` con cinco botones: Inicio, Cuentas, Movimientos, Inversiones y Reportes;
- área central basada en `CardLayout`;
- tarjetas para Inicio, Cuentas, Movimientos, Inversiones y Reportes;
- `StatusBarPanel`;
- punto de entrada `ar.com.agmilevecich.sofp.ui.Main`.

La UI integra el contexto existente para:

- listar cuentas del perfil/usuario;
- conservar la cuenta seleccionada;
- listar movimientos de la cuenta seleccionada con autorización por usuario;
- mostrar posiciones de inversión del perfil con autorización por usuario;
- mostrar el reporte de movimientos de inversión mediante `CarteraActivoService`;
- navegar entre Inicio, Cuentas, Movimientos, Inversiones y Reportes desde el shell principal.

La UI utiliza los servicios existentes y no duplica reglas de negocio.

## Tests específicos de UI

La cobertura del shell incluye:

- `CuentasPanelTest`;
- `MovimientosPanelTest`;
- `MainFrameTest`;
- `MainFrameLayoutTest`;
- `MainFrameNavigationTest`;
- `MainFrameMovimientosTest`;
- `InversionesPanelTest`;
- `MainFrameInversionesTest`;
- `ReportesPanelTest`;
- `MainFrameReportesTest`.

Validación específica más reciente de reportes:

- `ReportesPanelTest`: **3/3**;
- `MainFrameReportesTest`: **1/1**;
- total: **4/4 tests en verde**.

La suite relacionada de UI posterior quedó en **13/13 tests en verde**.

## Criterio de cierre de la feature

El alcance actual de `feature/swing-shell` está implementado y cubierto por tests. La suite general está en **529/529 verde**.

No se realiza merge automático a `main`.

El siguiente trabajo de Fase 8 deberá definirse como un nuevo bloque funcional, partiendo nuevamente del código real de la rama, manteniendo cambios pequeños, tests específicos y sin duplicar lógica de negocio.
