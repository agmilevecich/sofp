# SOFP — Historial de Builds

## Build 059 — Suite general posterior a venta y posición

**Estado: COMPLETADO Y VALIDADO.**

Suite general del 27/08/2026: **433/433**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`, 17:35 min.

## Etapa — Seguridad y aislamiento por usuario

La rama `feature/seguridad-aislamiento-datos` completó la auditoría transversal y fue integrada en `main` mediante fast-forward.

Validación del 31/08/2026: `AislamientoDatosServiceTest` **7/7**. Suite general posterior: **512/512**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`, 15:25 min.

## Fase 8 — Interfaz Swing

La rama `feature/swing-shell` desarrolló progresivamente el shell Swing y su integración con cuentas, movimientos, inversiones y reportes.

Se incorporaron y conectaron `MainFrame`, `HeaderPanel`, `SidebarPanel`, `InicioPanel`, `CuentasPanel`, `MovimientosPanel`, `InversionesPanel`, `ReportesPanel`, `StatusBarPanel` y `ui.Main`.

`MainFrame` utiliza `CardLayout` para Inicio, Cuentas, Movimientos, Inversiones y Reportes. La integración mantiene las reglas de negocio en los servicios existentes y pasa el contexto de usuario/perfil a la UI.

## Bloque — Alta de movimientos

`RegistrarMovimientoPanel` se integró al flujo de movimientos. Usa categoría autorizada y activa, tipo, importe, fecha/hora, descripción y `usuarioId`; delega a `MovimientoService.registrar(...)`; y `MovimientosPanel` refresca mediante callback.

Validación histórica del bloque: **10/10 tests en verde**.

## Bloque — Alta de cuentas

`RegistrarCuentaPanel` permite seleccionar tipo de cuenta, institución financiera, moneda e identificador externo y delega a `CuentaService.registrar(cuenta, usuarioId)`.

`CuentaService` soporta alta con o sin transacción activa. `CuentasPanel` refresca mediante callback. `MainFrame` conserva el constructor histórico basado en `perfilFinancieroId` y el constructor contextual completo.

Commits principales del bloque:

- `d5674aa` — `feat: agregar formulario de alta de cuentas`;
- `34d4d7d` — `fix: completar transaccion de alta de cuentas`;
- `19b2988` — `fix: permitir alta de cuentas dentro de transaccion existente`;
- `731e520` — `feat: integrar alta de cuentas en el panel`;
- `76cc4b0` — `fix: corregir contexto del panel de cuentas`;
- `a8ae7f9` — `feat: conectar alta de cuentas al shell`;
- `c5d9098` — `test: cubrir alta de cuentas desde el shell`;
- `0919a8e` — `fix: corregir orden de servicios en MainFrame`;
- `0d7769e` — `fix: conservar listado de cuentas con perfil id`;
- `7ac8f99` — `fix: integrar inversiones y reportes en MainFrame`;
- `c7cca8f` — `fix: conservar perfil en constructores de MainFrame`;
- `29b5e11` — `test: corregir expectativa de habilitacion del alta de cuentas`.

## Inversiones y reportes

`InversionesPanel` muestra posiciones filtradas por usuario/perfil. `ReportesPanel` utiliza `CarteraActivoService` para reportes de movimientos. Ambos quedaron integrados y navegables desde `MainFrame`.

## Validación relacionada — 03/09/2026

Comando:

`mvn -Dtest=InversionesPanelTest,ReportesPanelTest,MainFrameInversionesTest,MainFrameReportesTest,MainFrameMovimientosTest,CuentasPanelTest,RegistrarCuentaPanelTest test`

Resultado:

- **20/20 tests en verde**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- Duración: **09:43 min**;
- Finalización: **10:55:56 -03:00**.

Validación individual adicional: `MainFrameMovimientosTest` **3/3**, `BUILD SUCCESS`, finalización **10:45:01 -03:00**.

## Validación general

La última suite general conocida sigue siendo la ejecutada el **01/09/2026**:

- **529/529 tests en verde**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- Duración: **14:25 min**;
- Finalización: **19:25:53 -03:00**.

Los 20/20 del 03/09 corresponden a la batería relacionada de Swing y no sustituyen la suite general.

## Incidentes conocidos

Durante las ejecuciones de UI Surefire muestra un mensaje de espera posterior a `System.exit(0)`. En las ejecuciones registradas el proceso terminó con `BUILD SUCCESS`, sin failures ni errors. No se realizó ningún cambio especulativo.

Una ejecución anterior involucró un `SwingApplicationTest` obsoleto presente en `target`; se resolvió mediante limpieza de Maven, sin modificar código ni tests para ocultar el problema.

## Estado actual

`main`: `a4be859` — `docs: crear contexto de continuidad actualizado`.

`feature/swing-shell`: `29b5e11` — `test: corregir expectativa de habilitacion del alta de cuentas`.

Comparación: **122 commits adelante y 2 atrás**, estado `diverged`; merge base `96f3d999`. Los dos commits exclusivos de `main` son documentales (`39badd1` y `a4be859`).

## Próximo paso

Definir el próximo bloque funcional de Fase 8 a partir del código real. No hacer merge automático a `main` ni crear ramas nuevas. Antes de una eventual integración, revisar explícitamente los dos commits documentales exclusivos de `main`.
