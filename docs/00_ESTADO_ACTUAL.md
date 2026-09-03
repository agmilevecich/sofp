# SOFP — Estado actual

> Documento de continuidad. La fuente de verdad técnica es el código, los tests y los commits actuales; `docs/` es documentación auxiliar y puede quedar desactualizada.

## Estado verificado — 03/09/2026

**Rama estable:** `main` → `a4be859` (`docs: crear contexto de continuidad actualizado`).  
**Rama de trabajo:** `feature/swing-shell` → `f56074f` (`docs: actualizar contexto de ChatGPT`).

El último cambio de código/test antes de la actualización documental fue `29b5e11` — `test: corregir expectativa de habilitacion del alta de cuentas`.

La comparación actual contra `main` debe interpretarse como una rama divergente: los commits exclusivos de `main` son documentales y no se incorporan automáticamente.

## Estado Git local verificado — 03/09/2026

El usuario ejecutó `git syncsofp` y obtuvo `Already up to date` / `Everything up-to-date`.

`git status` informó rama `feature/swing-shell` y `nothing to commit, working tree clean` antes de esta actualización documental.

## Validación vigente

Última suite general conocida, 01/09/2026: **529/529 tests**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`, 14:25 min, finalización 19:25:53 -03:00.

Última batería relacionada de Swing, 03/09/2026:

`mvn -Dtest=InversionesPanelTest,ReportesPanelTest,MainFrameInversionesTest,MainFrameReportesTest,MainFrameMovimientosTest,CuentasPanelTest,RegistrarCuentaPanelTest test`

- **20/20 tests en verde**;
- Failures: 0;
- Errors: 0;
- Skipped: 0;
- `BUILD SUCCESS`;
- Duración: **09:43 min**;
- Finalización: **10:55:56 -03:00**.

Validación individual adicional: `MainFrameMovimientosTest` **3/3**, `BUILD SUCCESS`, 02:12 min, finalización 10:45:01 -03:00.

Surefire mostró el mensaje posterior a `System.exit(0)`, pero las ejecuciones terminaron con `BUILD SUCCESS`; no se realizó un cambio especulativo.

## Seguridad implementada

La auditoría transversal de seguridad y aislamiento de datos quedó completada e integrada en `main`. Se mantienen protegidos por propietario/usuario perfiles, cuentas, categorías, movimientos, posiciones/cartera y operaciones financieras, y se cerraron caminos internos relevantes que podían saltar validaciones públicas.

## Fase 8 — Interfaz de usuario Swing

Implementados y conectados: `MainFrame`, `HeaderPanel`, `SidebarPanel`, `InicioPanel`, `CuentasPanel`, `MovimientosPanel`, `InversionesPanel`, `ReportesPanel`, `StatusBarPanel` y `ar.com.agmilevecich.sofp.ui.Main`.

`MainFrame` usa `CardLayout` para Inicio, Cuentas, Movimientos, Inversiones y Reportes. La UI integra los servicios existentes y no duplica reglas de negocio.

### Bloques cerrados

**Alta de movimientos:** `RegistrarMovimientoPanel` delega a `MovimientoService.registrar(...)` y `MovimientosPanel` refresca mediante callback. Validación histórica: **10/10**.

**Alta de cuentas:** `RegistrarCuentaPanel` permite tipo, institución financiera, moneda e identificador externo; delega a `CuentaService.registrar(cuenta, usuarioId)`; `CuentaService` soporta transacción activa o propia; `CuentasPanel` refresca mediante callback; `MainFrame` conserva constructor histórico por `perfilFinancieroId` y contextual.

**Inversiones y reportes:** `InversionesPanel` muestra posiciones filtradas por usuario/perfil y `ReportesPanel` usa `CarteraActivoService` para reportes de movimientos. Ambos están integrados en `MainFrame`.

## Últimos cambios de código/test

- `7ac8f99` — `fix: integrar inversiones y reportes en MainFrame`;
- `c7cca8f` — `fix: conservar perfil en constructores de MainFrame`;
- `29b5e11` — `test: corregir expectativa de habilitacion del alta de cuentas`.

El último cambio de código/test fue exclusivamente de test: se introdujo un nombre válido antes de comprobar la habilitación del botón de alta de cuentas, respetando la regla existente del formulario.

## Criterio de estado

Los bloques de alta de movimientos y cuentas están cerrados dentro de sus alcances validados. La integración de inversiones y reportes está cubierta por la batería relacionada de Swing.

No se realizó merge a `main` y no se crean ramas nuevas.

## Próximo paso

Definir el próximo bloque funcional de Fase 8 a partir del código real de `feature/swing-shell`. Antes de una eventual integración, revisar explícitamente los commits documentales exclusivos de `main` y ejecutar las validaciones correspondientes.
