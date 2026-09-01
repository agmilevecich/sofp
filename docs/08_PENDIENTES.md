# SOFP — Pendientes

## Estado — 01/09/2026

**Rama estable:** `main`.  
**Último commit integrado:** `96f3d99`.  
**Rama de trabajo:** `feature/swing-shell`.  
**Comparación:** 46 commits por delante de `main`, 0 por detrás.

## Seguridad

La etapa de seguridad y aislamiento de datos está **cerrada e integrada en `main`**.

Se completaron:

1. autorización de `OperacionFinancieraService`;
2. lecturas por ID y listados de recursos propios;
3. caminos alternativos de creación de cuentas, categorías, movimientos y perfiles;
4. aislamiento de posición y cartera por perfil/usuario;
5. cierre de caminos internos que podían saltar validaciones públicas;
6. cobertura específica de recursos propios y ajenos.

## Fase 8 — Interfaz de usuario Swing

El bloque actual de `feature/swing-shell` está implementado y validado dentro de su alcance:

- shell principal `MainFrame`;
- header, sidebar y barra de estado;
- navegación por tarjetas;
- cuentas contextualizadas por perfil/usuario;
- movimientos contextualizados por cuenta/usuario;
- inversiones contextualizadas por perfil/usuario;
- punto de entrada `ui.Main`;
- cobertura específica de los paneles e integraciones principales.

## Validación final

Suite general ejecutada localmente el **01/09/2026**:

- `525/525` tests en verde;
- `Failures: 0`;
- `Errors: 0`;
- `Skipped: 0`;
- `BUILD SUCCESS`;
- duración: **32:12 min**.

La ejecución se realizó después de limpiar artefactos compilados obsoletos que habían provocado un fallo ajeno al código versionado.

## Próximo bloque

No queda pendiente un arreglo dentro del alcance actual del shell Swing. El próximo trabajo debe definirse como un **nuevo bloque funcional de Fase 8**, a partir del código real de la rama y sin introducir funcionalidades especulativas.

## Criterio de continuidad

No asumir que una conversación anterior refleja el estado actual. Ante una nueva sesión de SOFP, reconstruir el estado desde GitHub: código → tests → commits → `main` → documentación.
