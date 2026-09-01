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
- `ReportesPanel`;
- `StatusBarPanel`;
- `ui.Main` como punto de entrada.

La integración mantiene las reglas de negocio en los servicios existentes y pasa el contexto de usuario/perfil a la UI.

También se agregó cobertura para estructura, layout, navegación, cuentas, movimientos, inversiones y reportes.

## Bloque de reportes de inversiones

Se incorporó `ReportesPanel` utilizando la funcionalidad de reporte ya existente en `CarteraActivoService`. El panel fue integrado al `MainFrame` y la navegación del `SidebarPanel` quedó conectada a la tarjeta `REPORTES`.

No se agregó lógica de negocio nueva al panel: la UI utiliza los servicios existentes y conserva el aislamiento por usuario/perfil.

Validación específica:

- `ReportesPanelTest`: **3/3 tests en verde**;
- `MainFrameReportesTest`: **1/1 test en verde**;
- total: **4/4 tests en verde**.

La suite relacionada de UI quedó en **13/13 tests en verde**.

## Validación general final del bloque Swing

Suite general ejecutada localmente el **01/09/2026**:

- Tests run: **529**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **14:25 min**
- Finalización: **19:25:53 -03:00**.

La validación global vigente es **529/529 tests en verde**.

La ejecución anterior había fallado por un `SwingApplicationTest` compilado previamente en `target`, que ya no formaba parte de los tests versionados. La limpieza de Maven eliminó el artefacto obsoleto y permitió ejecutar la suite completa correctamente, sin cambios adicionales de código ni tests por ese motivo.

Durante ejecuciones de tests de UI Surefire mostró un mensaje indicando que esperaba más de 30 segundos después de `System.exit(0)`. Las ejecuciones terminaron con `BUILD SUCCESS`, sin failures ni errors. No se realizó ningún cambio especulativo por ese mensaje.

## Estado actual

`main` permanece como rama estable.

`feature/swing-shell` es la rama activa y se encuentra **52 commits por delante de `main` y 0 commits por detrás** según la comparación actual con GitHub.

Último commit funcional antes del bloque documental: `6621615` — `test: cubrir navegacion de reportes`.

La documentación de continuidad se actualiza mediante commits separados posteriores a la validación general.

## Próximo avance

El bloque actual de `feature/swing-shell` queda validado dentro de su alcance. El siguiente avance corresponde a un nuevo bloque funcional de Fase 8.

No se realiza merge automático a `main`.
