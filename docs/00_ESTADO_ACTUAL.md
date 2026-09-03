# SOFP — Estado actual

> Documento de continuidad. La fuente de verdad técnica es el código, los tests y los commits actuales; `docs/` es documentación auxiliar y puede quedar desactualizada.

## Estado verificado — 03/09/2026

**Rama estable:** `main` → `a4be859` (`docs: crear contexto de continuidad actualizado`).
**Rama de trabajo:** `feature/swing-shell` → `e873832` (`test: verificar fecha del sistema en selector de movimientos`).

La rama de trabajo está **139 commits por delante y 2 por detrás de `main`**, estado `diverged`. Merge base: `96f3d999`. Los dos commits exclusivos de `main` (`39badd1` y `a4be859`) son documentales y no se incorporan automáticamente.

## Estado Git local verificado — 03/09/2026

El usuario ejecutó `git syncsofp` y obtuvo `Already up to date` / `Everything up-to-date`.

`git status` informó `On branch feature/swing-shell` y `nothing to commit, working tree clean`.

## Validación vigente

### Selector de fecha y hora de movimientos

`RegistrarMovimientoPanel` utiliza **LGoodDatePicker (`DatePicker`)** para la fecha. El calendario comienza en **domingo**, la fecha inicial es la fecha del sistema y el formato visual es `dd/MM/uuuu`.

La hora ya no se ingresa mediante ComboBox: al registrar, se combina la fecha seleccionada con `LocalTime.now()` del sistema.

Validación individual más reciente:

`mvn -Dtest=RegistrarMovimientoPanelTest test`

- **4/4 tests en verde**;
- Failures: 0;
- Errors: 0;
- Skipped: 0;
- `BUILD SUCCESS`;
- Duración: **03:27 min**;
- Finalización: **12:39:51 -03:00**.

Surefire mostró el mensaje posterior a `System.exit(0)`, pero la ejecución terminó correctamente con `BUILD SUCCESS`; no se considera un fallo.

### Batería relacionada de Swing

03/09/2026: **20/20 tests**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`, 09:43 min, finalización 10:55:56 -03:00.

### Suite general

Última suite general conocida: 01/09/2026, **529/529 tests**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`, 14:25 min, finalización 19:25:53 -03:00.

## Seguridad implementada

La auditoría transversal de seguridad y aislamiento de datos quedó completada e integrada en `main`. Se mantienen protegidos por propietario/usuario perfiles, cuentas, categorías, movimientos, posiciones/cartera y operaciones financieras, y se cerraron caminos internos relevantes que podían saltar validaciones públicas.

## Fase 8 — Interfaz de usuario Swing

Implementados y conectados: `MainFrame`, `HeaderPanel`, `SidebarPanel`, `InicioPanel`, `CuentasPanel`, `MovimientosPanel`, `InversionesPanel`, `ReportesPanel`, `StatusBarPanel` y `ar.com.agmilevecich.sofp.ui.Main`.

`MainFrame` usa `CardLayout` para Inicio, Cuentas, Movimientos, Inversiones y Reportes. La UI integra los servicios existentes y no duplica reglas de negocio.

### Bloques cerrados

**Alta de movimientos:** formulario integrado con `MovimientoService.registrar(...)`, categoría autorizada y activa, tipo, importe, fecha/hora, descripción y `usuarioId`; refresco del listado mediante callback. El selector de fecha/hora quedó actualizado con LGoodDatePicker y hora automática del sistema.

**Alta de cuentas:** `RegistrarCuentaPanel` permite tipo, institución financiera, moneda e identificador externo; delega a `CuentaService.registrar(cuenta, usuarioId)`; `CuentaService` soporta transacción activa o propia; `CuentasPanel` refresca mediante callback; `MainFrame` conserva constructor histórico por `perfilFinancieroId` y contextual.

**Inversiones y reportes:** `InversionesPanel` muestra posiciones filtradas por usuario/perfil y `ReportesPanel` usa `CarteraActivoService` para reportes de movimientos. Ambos están integrados en `MainFrame`.

## Últimos cambios de código/test

- `688d8b0` — `feat: registrar hora del sistema en movimientos`;
- `1363c0a` — `test: corregir comparacion de hora del sistema`;
- `f820dff` — `feat: mostrar fecha del sistema en selector de movimientos`;
- `e873832` — `test: verificar fecha del sistema en selector de movimientos`.

También se incorporó la dependencia LGoodDatePicker y se cubrieron los cambios del selector mediante tests específicos.

## Incidente local resuelto

Durante una ejecución de la aplicación el usuario modificó accidentalmente `RegistrarMovimientoPanel.java` al escribir sobre el código. El archivo fue corregido localmente y `git syncsofp` confirmó posteriormente que no había cambios pendientes y que la copia estaba sincronizada con GitHub.

## Criterio de estado

Los bloques de alta de movimientos, alta de cuentas, inversiones y reportes están cerrados dentro de sus alcances validados. El selector de fecha/hora de movimientos también está validado individualmente.

No se realizó merge a `main` y no se crean ramas nuevas.

## Próximo paso

Definir el próximo bloque funcional de Fase 8 a partir del código real de `feature/swing-shell`. Antes de modificar código, revisar implementación, clases relacionadas, servicios, repositorios, reglas de negocio y tests. Antes de una eventual integración, revisar explícitamente los dos commits documentales exclusivos de `main`.
