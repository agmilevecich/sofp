# SOFP — Estado actual

> Documento de continuidad. La fuente de verdad técnica es el código y los tests actuales; `docs/` es documentación auxiliar.

## Estado verificado — 02/09/2026

**Rama estable:** `main`  
**Último commit integrado conocido:** `a4be859` — `docs: crear contexto de continuidad actualizado`  
**Feature integrada:** `feature/seguridad-aislamiento-datos` mediante fast-forward.

**Rama de trabajo:** `feature/swing-shell`  
**Último commit:** `e8c9c50` — `test: cubrir alta real de movimientos desde formulario`.

La rama de trabajo continúa separada de `main`; la comparación actual es **73 commits por delante y 2 commits por detrás**. Los 2 commits de diferencia de `main` corresponden a documentación posterior y no se incorporan automáticamente a la feature.

## Validación vigente

La última suite general ejecutada localmente fue el **01/09/2026**:

- Tests run: **529**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **14:25 min**
- Finalización: **19:25:53 -03:00**

La validación global vigente es **529/529 tests en verde**.

Posteriormente se ejecutó la suite específica del bloque de alta de movimientos desde Swing el **02/09/2026**:

- `RegistrarMovimientoPanelTest`: **4/4**
- `MovimientosPanelTest`: incluido en la suite relacionada
- `MainFrameMovimientosTest`: incluido en la suite relacionada
- `MainFrameNavigationTest`: incluido en la suite relacionada
- Total: **10/10 tests en verde**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **04:38 min**
- Finalización: **12:26:39 -03:00**

La validación específica confirmó el alta real, persistencia y notificación para refrescar el listado.

## Seguridad implementada

La auditoría transversal de seguridad y aislamiento de datos quedó completada e integrada en `main`.

Se mantienen protegidos por propietario/usuario los recursos y operaciones de perfiles, cuentas, categorías, movimientos, posiciones/cartera y operaciones financieras. Los caminos internos relevantes fueron cerrados para evitar saltar las validaciones públicas.

## Estado de la interfaz — Fase 8 / `feature/swing-shell`

El shell Swing está implementado e integrado con los servicios existentes.

Componentes actuales:

- `MainFrame`;
- `HeaderPanel`;
- `SidebarPanel` con Inicio, Cuentas, Movimientos, Inversiones y Reportes;
- área central basada en `CardLayout`;
- `InicioPanel`;
- `CuentasPanel`;
- `MovimientosPanel`;
- `InversionesPanel`;
- `ReportesPanel`;
- `StatusBarPanel`;
- punto de entrada `ar.com.agmilevecich.sofp.ui.Main`.

La UI integra el contexto existente para:

- listar cuentas del perfil/usuario;
- conservar la cuenta seleccionada;
- listar movimientos de la cuenta seleccionada con autorización por usuario;
- registrar movimientos desde Swing mediante `MovimientoService.registrar(...)`;
- refrescar automáticamente el listado después de un alta exitosa;
- mostrar posiciones de inversión del perfil con autorización por usuario;
- mostrar el reporte de movimientos de inversión mediante `CarteraActivoService`;
- navegar entre Inicio, Cuentas, Movimientos, Inversiones y Reportes.

La UI utiliza los servicios existentes y no duplica reglas de negocio.

## Alta de movimientos desde Swing

El bloque incorporado en `feature/swing-shell` agrega `RegistrarMovimientoPanel` al flujo de movimientos de una cuenta.

El formulario utiliza categoría autorizada y activa, tipo de movimiento, importe, fecha y hora, descripción y usuario propietario de la cuenta. El alta se delega a `MovimientoService` y `MovimientosPanel` recibe un callback para actualizar el listado después de una registración exitosa.

La prueba `deberiaRegistrarMovimientoYNotificarAlContenedor` verifica alta real en H2, persistencia, datos principales del movimiento y notificación al contenedor.

## Criterio de cierre del bloque actual

El bloque de alta de movimientos desde Swing queda **implementado y validado dentro de su alcance**, con **10/10 tests relacionados en verde**.

No se realiza merge automático a `main` y no se crean ramas nuevas para continuar.

## Próximo paso

Definir un nuevo bloque funcional de Fase 8 partiendo del código real de `feature/swing-shell`, revisando nuevamente clases relacionadas, servicios, repositorios, reglas de negocio y tests antes de modificar código.
