# SOFP — Historial del proyecto

## 2026-08-31 — Cierre de seguridad y aislamiento de datos

La feature `feature/seguridad-aislamiento-datos` completó la revisión transversal de seguridad y aislamiento de recursos por usuario/perfil y fue integrada en `main` mediante fast-forward.

Validación final de seguridad:

- `AislamientoDatosServiceTest`: **7/7 en verde**;
- suite general: **512/512 tests en verde**;
- Failures: 0;
- Errors: 0;
- Skipped: 0;
- `BUILD SUCCESS`.

## 2026-08-31 / 2026-09-01 — Fase 8: shell Swing

La rama `feature/swing-shell` desarrolló el primer bloque funcional de interfaz Swing sobre la base estable de seguridad.

Se incorporaron y conectaron progresivamente:

- `MainFrame`;
- `HeaderPanel`;
- `SidebarPanel`;
- `InicioPanel`;
- `CuentasPanel`;
- `MovimientosPanel`;
- `InversionesPanel`;
- `StatusBarPanel`;
- `ui.Main` como punto de entrada.

La integración mantiene las reglas de negocio en los servicios existentes y pasa el contexto de usuario/perfil a la UI.

También se agregó cobertura para estructura, layout, navegación, cuentas, movimientos e inversiones.

## Validación final del bloque Swing

Suite general ejecutada localmente el **01/09/2026** después de limpiar artefactos compilados obsoletos:

- Tests run: **525**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **32:12 min**.

La ejecución previa había fallado únicamente por un `SwingApplicationTest` compilado en `target` que ya no formaba parte de los tests versionados. La limpieza de Maven eliminó el artefacto obsoleto y la suite completa quedó verde sin cambios adicionales de código.

## Estado actual

`main` permanece como rama estable.

`feature/swing-shell` es la rama activa y se encuentra **46 commits por delante de `main` y 0 por detrás**.

Último commit: `ca70f28` — `fix: conservar entrada Swing existente en ui`.

El bloque actual de shell Swing queda validado dentro de su alcance. El siguiente avance corresponde a un nuevo bloque funcional de Fase 8.
