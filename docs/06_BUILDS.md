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

El selector de fecha fue migrado a **LGoodDatePicker (`DatePicker`)**, con calendario iniciado en domingo y fecha inicial igual a la fecha del sistema. La hora se obtiene automáticamente mediante `LocalTime.now()` al registrar, sin ComboBox de hora.

Validación histórica del bloque: **10/10 tests en verde**.

Validación específica más reciente del selector: **4/4 tests en verde**, `BUILD SUCCESS`, 03:27 min, finalización 12:39:51 -03:00.

## Bloque — Alta de cuentas

`RegistrarCuentaPanel` permite seleccionar tipo de cuenta, institución financiera, moneda e identificador externo y delega a `CuentaService.registrar(cuenta, usuarioId)`.

`CuentaService` soporta alta con o sin transacción activa. `CuentasPanel` refresca mediante callback. `MainFrame` conserva el constructor histórico basado en `perfilFinancieroId` y el constructor contextual completo.

## Inversiones y reportes

`InversionesPanel` muestra posiciones filtradas por usuario/perfil. `ReportesPanel` utiliza `CarteraActivoService` para reportes de movimientos. Ambos quedaron integrados y navegables desde `MainFrame`.

## Commits recientes — fecha y hora de movimientos

- `bd436d4` — `feat: agregar dependencia LGoodDatePicker`;
- `418c8f4` — `feat: incorporar selector de fecha y hora en movimientos`;
- `fcf5f69` — `test: cubrir selector de fecha y hora de movimientos`;
- `bdd03b3` — `test: ajustar prueba del selector de fecha y hora`;
- `688d8b0` — `feat: registrar hora del sistema en movimientos`;
- `53f138c` — `test: validar hora del sistema en movimientos`;
- `0fc7309` — `test: ajustar tolerancia de hora del sistema`;
- `1363c0a` — `test: corregir comparacion de hora del sistema`;
- `f820dff` — `feat: mostrar fecha del sistema en selector de movimientos`;
- `e873832` — `test: verificar fecha del sistema en selector de movimientos`.

## Validación relacionada — 03/09/2026

Comando:

`mvn -Dtest=InversionesPanelTest,ReportesPanelTest,MainFrameInversionesTest,MainFrameReportesTest,MainFrameMovimientosTest,CuentasPanelTest,RegistrarCuentaPanelTest test`

Resultado: **20/20 tests en verde**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`, 09:43 min, finalización 10:55:56 -03:00.

Validación individual adicional: `MainFrameMovimientosTest` **3/3**, `BUILD SUCCESS`, finalización 10:45:01 -03:00.

## Validación general

La última suite general conocida sigue siendo la ejecutada el **01/09/2026**: **529/529 tests en verde**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`, 14:25 min, finalización 19:25:53 -03:00.

Los 20/20 del 03/09 y los 4/4 de `RegistrarMovimientoPanelTest` son validaciones específicas/relacionadas y no sustituyen la suite general.

## Incidentes conocidos

Durante las ejecuciones de UI Surefire muestra un mensaje de espera posterior a `System.exit(0)`. En las ejecuciones registradas el proceso terminó con `BUILD SUCCESS`, sin failures ni errors. No se realizó ningún cambio especulativo.

Una ejecución anterior involucró un `SwingApplicationTest` obsoleto presente en `target`; se resolvió mediante limpieza de Maven, sin modificar código ni tests para ocultar el problema.

## Estado actual

`main`: `a4be859` — `docs: crear contexto de continuidad actualizado`.

`feature/swing-shell`: `e873832` — `test: verificar fecha del sistema en selector de movimientos` antes de esta actualización documental.

Comparación: **139 commits adelante y 2 atrás**, estado `diverged`; merge base `96f3d999`. Los dos commits exclusivos de `main` son documentales (`39badd1` y `a4be859`).

## Próximo paso

Definir el próximo bloque funcional de Fase 8 a partir del código real. No hacer merge automático a `main` ni crear ramas nuevas. Antes de una eventual integración, revisar explícitamente los dos commits documentales exclusivos de `main`.
