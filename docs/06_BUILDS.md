# SOFP — Historial de Builds

## Build 059 — Suite general posterior a venta y posición

**Estado: COMPLETADO Y VALIDADO.**

Suite general del 27/08/2026: **433/433**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`, 17:35 min.

## Etapa — Seguridad y aislamiento por usuario

La rama `feature/seguridad-aislamiento-datos` completó la auditoría transversal y fue integrada en `main` mediante fast-forward.

Validación del 31/08/2026: `AislamientoDatosServiceTest` **7/7**. Suite general posterior: **512/512**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`, 15:25 min.

## Fase 8 — Interfaz Swing

La rama `feature/swing-shell` desarrolló progresivamente el shell Swing y su integración con cuentas, categorías, movimientos, inversiones y reportes.

Se incorporaron y conectaron `MainFrame`, `HeaderPanel`, `SidebarPanel`, `InicioPanel`, `CuentasPanel`, `CategoriasPanel`, `MovimientosPanel`, `InversionesPanel`, `ReportesPanel`, `StatusBarPanel`, `RegistrarCuentaPanel`, `RegistrarMovimientoPanel` y `ui.Main`.

`MainFrame` utiliza `CardLayout` para Inicio, Cuentas, Categorías, Movimientos, Inversiones y Reportes. La integración mantiene las reglas de negocio en los servicios existentes y pasa el contexto de usuario/perfil a la UI.

## Bloque — Alta de movimientos

`RegistrarMovimientoPanel` se integró al flujo de movimientos. Usa categoría autorizada y activa, tipo, importe, fecha/hora, descripción y `usuarioId`; delega a `MovimientoService.registrar(...)`; y `MovimientosPanel` refresca mediante callback.

El selector de fecha fue migrado a **LGoodDatePicker (`DatePicker`)**, con calendario iniciado en domingo y fecha inicial igual a la fecha del sistema. La hora se obtiene automáticamente mediante `LocalTime.now()` al registrar, sin ComboBox de hora.

Validación específica: `RegistrarMovimientoPanelTest` **4/4**, `BUILD SUCCESS`.

Validación de integración: `MainFrameMovimientosTest` y `RegistrarMovimientoPanelTest` **7/7**, `BUILD SUCCESS`.

Validación del servicio: `MovimientoServiceTest` **50/50**, `BUILD SUCCESS`. Total conocido del bloque: **57/57 tests en verde**.

## Bloque — Alta de cuentas

`RegistrarCuentaPanel` permite seleccionar tipo de cuenta, institución financiera, moneda e identificador externo y delega a `CuentaService.registrar(cuenta, usuarioId)`.

`CuentaService` soporta alta con o sin transacción activa. `CuentasPanel` refresca mediante callback. `MainFrame` conserva el constructor histórico basado en `perfilFinancieroId` y el constructor contextual completo.

## Bloque — Gestión de categorías

`CategoriasPanel` permite registrar, modificar, activar/desactivar y eliminar categorías del perfil del usuario, delegando las reglas a `CategoriaService`.

La persistencia del alta se corrigió en `66b22f3` mediante gestión de transacción y `flush` cuando corresponde, manteniendo la validación de propietario en el servicio.

Validación UI y navegación: **3/3 tests**, `BUILD SUCCESS`, 02:24 min, finalización 13:40:32 -03:00.

Validación del servicio: **22/22 tests**, `BUILD SUCCESS`, 10:33 min, finalización 13:53:50 -03:00.

Total del bloque: **25/25 tests en verde**.

## Bloque — Inversiones y reportes

`InversionesPanel` muestra posiciones filtradas por usuario/perfil. `ReportesPanel` utiliza `CarteraActivoService` para reportes de movimientos. Ambos quedaron integrados y navegables desde `MainFrame`.

Validación UI: `InversionesPanelTest`, `MainFrameInversionesTest` y `MainFrameReportesTest` **5/5**, `BUILD SUCCESS`, 03:08 min, finalización 15:49:46 -03:00.

Validación de servicios: `CarteraActivoServiceTest`, `CarteraActivoServiceComposicionTest` y `CarteraActivoServiceMovimientosTest` **16/16**, `BUILD SUCCESS`, 09:38 min, finalización 16:09:32 -03:00.

Total del bloque: **21/21 tests en verde**.

## Validación relacionada — 03/09/2026

Se registraron además las baterías específicas de Swing ejecutadas durante la jornada, sin sustituir la suite general.

- `MainFrameMovimientosTest` + `RegistrarMovimientoPanelTest`: **7/7**.
- `MovimientoServiceTest`: **50/50**.
- `CategoriasPanelTest` + `MainFrameCategoriasTest`: **3/3**.
- `CategoriaServiceTest`: **22/22**.
- `InversionesPanelTest` + `MainFrameInversionesTest` + `MainFrameReportesTest`: **5/5**.
- `CarteraActivoServiceTest` + `CarteraActivoServiceComposicionTest` + `CarteraActivoServiceMovimientosTest`: **16/16**.

No se suman estos resultados como una única cifra porque algunas baterías pueden compartir clases de prueba con ejecuciones anteriores.

## Validación general

La última suite general conocida sigue siendo la ejecutada el **01/09/2026**: **529/529 tests en verde**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`, 14:25 min, finalización 19:25:53 -03:00.

## Incidentes conocidos

Durante las ejecuciones de UI Surefire muestra un mensaje de espera posterior a `System.exit(0)`. En las ejecuciones registradas el proceso terminó con `BUILD SUCCESS`, sin failures ni errors. No se realizó ningún cambio especulativo.

Una ejecución anterior involucró un `SwingApplicationTest` obsoleto presente en `target`; se resolvió mediante limpieza de Maven, sin modificar código ni tests para ocultar el problema.

## Estado actual

`main`: `a4be859` — `docs: crear contexto de continuidad actualizado`.

`feature/swing-shell`: `66b22f3` — `fix: gestionar transaccion al registrar categoria` antes de esta actualización documental.

Comparación: **139 commits adelante y 2 atrás**, estado `diverged`; merge base `96f3d999`. Los dos commits exclusivos de `main` son documentales (`39badd1` y `a4be859`).

## Próximo paso

Definir el próximo bloque funcional de Fase 8 a partir del código real. No hacer merge automático a `main` ni crear ramas nuevas. Antes de una eventual integración, revisar explícitamente los dos commits documentales exclusivos de `main`.
