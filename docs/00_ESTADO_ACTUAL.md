# SOFP — Estado actual

> Documento de continuidad. La fuente de verdad técnica es el código y los tests actuales; `docs/` es documentación auxiliar.

## Estado verificado — 02/09/2026

**Rama estable:** `main`  
**Último commit integrado:** `75d0a18` — `docs: actualizar contexto tras cierre de seguridad`.  
**Feature integrada:** `feature/seguridad-aislamiento-datos` mediante fast-forward.

**Rama de trabajo:** `feature/swing-shell`  
**Último commit:** `0d7769e` — `fix: conservar listado de cuentas con perfil id`.

La rama de trabajo continúa separada de `main`. La comparación verificada antes de esta actualización es **90 commits por delante y 2 commits por detrás**. Los commits que existen en `main` y no en la feature corresponden a documentación posterior y no se incorporan automáticamente.

## Validación vigente

La última suite general ejecutada localmente fue el **01/09/2026**:

- Tests run: **529**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **14:25 min**
- Finalización: **19:25:53 -03:00**

La última validación global disponible sigue siendo **529/529 tests en verde**. No se ejecutó una nueva suite general después de los cambios posteriores.

La validación específica más reciente fue ejecutada el **02/09/2026** después de corregir la integración de cuentas en `MainFrame`:

- `RegistrarCuentaPanelTest`: **5/5**
- `CuentasPanelTest`: **3/3**
- `MainFrameMovimientosTest`: **3/3**
- Total: **11/11 tests en verde**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **05:24 min**
- Finalización: **14:05:26 -03:00**

Esta validación confirmó la integración de alta de cuentas, la navegación/listado de movimientos y la compatibilidad del shell con el contexto por `perfilFinancieroId`.

## Seguridad implementada

La auditoría transversal de seguridad y aislamiento de datos quedó completada e integrada en `main`.

Se mantienen protegidos por propietario/usuario los recursos y operaciones de perfiles, cuentas, categorías, movimientos, posiciones/cartera y operaciones financieras. Los caminos internos relevantes fueron cerrados para evitar saltar las validaciones públicas.

## Fase 8 — Interfaz de usuario Swing

El shell Swing está implementado en `feature/swing-shell` e integrado con los servicios existentes.

Componentes actuales:

- `MainFrame`;
- `HeaderPanel`;
- `SidebarPanel`;
- `InicioPanel`;
- `CuentasPanel`;
- `MovimientosPanel`;
- `InversionesPanel`;
- `ReportesPanel`;
- `StatusBarPanel`;
- punto de entrada `ar.com.agmilevecich.sofp.ui.Main`.

`MainFrame` utiliza `CardLayout` y navega entre Inicio, Cuentas, Movimientos, Inversiones y Reportes. La UI utiliza los servicios existentes, respeta el contexto de usuario/perfil y no duplica reglas de negocio.

La integración actual incluye:

- listado de cuentas por perfil/usuario;
- alta de cuentas desde Swing con instituciones y monedas existentes;
- conservación de la cuenta seleccionada;
- listado de movimientos de la cuenta seleccionada con autorización por usuario;
- alta de movimientos mediante `MovimientoService.registrar(...)`;
- refresco automático del listado después de un alta exitosa;
- posiciones de inversión filtradas por usuario/perfil;
- reporte de movimientos de inversión;
- navegación entre las tarjetas del shell.

## Alta de cuentas desde Swing

`RegistrarCuentaPanel` permite seleccionar tipo de cuenta, institución financiera, moneda e identificador externo y delega el alta a `CuentaService.registrar(cuenta, usuarioId)`.

El alta usa una transacción compatible tanto con llamadas sin transacción activa como con los tests que ya ejecutan dentro de una transacción existente. `CuentasPanel` refresca su listado mediante callback después de un alta exitosa.

`MainFrame` conserva además el constructor histórico basado en `perfilFinancieroId` para el listado de cuentas y utiliza el constructor contextual completo cuando dispone de `PerfilFinanciero`, instituciones y monedas.

## Alta de movimientos desde Swing

`RegistrarMovimientoPanel` permite registrar movimientos para la cuenta seleccionada utilizando categoría autorizada y activa, tipo, importe, fecha/hora, descripción y `usuarioId`.

La operación se delega a `MovimientoService`. `MovimientosPanel` recibe un callback después de una registración exitosa y actualiza el listado.

Se mantiene `registrarMovimiento()` separado de la interacción visual para permitir probar el alta real sin bloquear las pruebas con diálogos Swing.

## Criterio de estado

El bloque de alta de movimientos está cerrado dentro de su alcance. El bloque de alta de cuentas también está integrado y validado dentro de su alcance, incluyendo persistencia real y refresco del listado.

No se realizó merge a `main` y no se crean ramas nuevas para continuar.

## Próximo paso

Definir el próximo bloque funcional de Fase 8 únicamente después de revisar nuevamente el código actual de `feature/swing-shell`, clases relacionadas, servicios, repositorios, reglas de negocio y tests.
