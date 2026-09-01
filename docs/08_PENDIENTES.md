# SOFP — Pendientes

## Estado — 01/09/2026

**Rama estable:** `main`.  
**Último commit integrado:** `96f3d99` — `docs: cerrar historial de build de seguridad`.  
**Rama de trabajo:** `feature/swing-shell`.  
**Comparación actual:** **52 commits por delante de `main`, 0 commits por detrás**.

## Seguridad

La etapa de seguridad y aislamiento de datos está **cerrada e integrada en `main`**.

Se completaron:

1. autorización de `OperacionFinancieraService`;
2. lecturas por ID y listados de recursos propios;
3. caminos alternativos de creación de cuentas, categorías, movimientos y perfiles;
4. aislamiento de posición y cartera por perfil/usuario;
5. cierre de caminos internos que podían saltar validaciones públicas;
6. cobertura específica de recursos propios y ajenos mediante `AislamientoDatosServiceTest`.

## Fase 8 — Interfaz de usuario Swing

El bloque actual de `feature/swing-shell` está implementado y validado dentro de su alcance:

- shell principal `MainFrame`;
- `HeaderPanel`;
- `SidebarPanel` con Inicio, Cuentas, Movimientos, Inversiones y Reportes;
- área central con `CardLayout`;
- tarjetas para Inicio, Cuentas, Movimientos, Inversiones y Reportes;
- `StatusBarPanel`;
- punto de entrada `ui.Main`;
- cuentas contextualizadas por perfil/usuario;
- movimientos contextualizados por cuenta/usuario;
- inversiones contextualizadas por perfil/usuario;
- reportes de inversiones conectados a `CarteraActivoService`.

La UI utiliza los servicios existentes y mantiene las reglas de negocio fuera de la interfaz.

## Validación final

Suite general ejecutada localmente el **01/09/2026**:

- **529/529** tests en verde;
- `Failures: 0`;
- `Errors: 0`;
- `Skipped: 0`;
- `BUILD SUCCESS`;
- duración: **14:25 min**;
- finalización: **19:25:53 -03:00**.

Validación específica de reportes:

- `ReportesPanelTest`: **3/3**;
- `MainFrameReportesTest`: **1/1**;
- total reportes: **4/4**;
- suite relacionada de UI: **13/13**.

## Pendientes

No queda pendiente un arreglo dentro del alcance actual del shell Swing.

El próximo trabajo debe definirse como un **nuevo bloque funcional de Fase 8**, partiendo del código real de `feature/swing-shell` y revisando nuevamente clases relacionadas, servicios, repositorios y tests antes de proponer cambios.

No se deben introducir funcionalidades especulativas ni duplicar reglas de negocio en la UI.

## Criterio de continuidad

No asumir que una conversación anterior refleja el estado actual. Ante una nueva sesión de SOFP, reconstruir el estado desde GitHub: código → tests → commits → `main` → documentación.

No hacer merge automático a `main`.
