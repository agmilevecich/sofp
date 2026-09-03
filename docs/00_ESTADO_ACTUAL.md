# SOFP — Estado actual

> Documento de continuidad. La fuente de verdad técnica es el código, los tests y los commits actuales; `docs/` es documentación auxiliar y puede quedar desactualizada.

## Estado verificado — 03/09/2026

**Rama estable:** `main`  
**Último commit de `main`:** `a4be859` — `docs: crear contexto de continuidad actualizado`.  
**Feature integrada previamente:** `feature/seguridad-aislamiento-datos`, mediante fast-forward.

**Rama de trabajo:** `feature/swing-shell`  
**Último commit:** `29b5e11` — `test: corregir expectativa de habilitacion del alta de cuentas`.

La comparación actual contra `main` es **122 commits por delante y 2 commits por detrás** (`diverged`). El punto de bifurcación es `96f3d999`. Los dos commits que están en `main` y no en la feature son commits de documentación (`39badd1` y `a4be859`); no se incorporan automáticamente mientras la feature continúa separada.

## Estado Git local verificado — 03/09/2026

El usuario ejecutó `git syncsofp` y obtuvo `Already up to date` / `Everything up-to-date`.

`git status` informó:

- rama `feature/swing-shell`;
- `nothing to commit, working tree clean`.

El último historial local visible confirma `29b5e11`, `c7cca8f` y `7ac8f99` como los tres commits más recientes de la feature.

## Validación vigente

La última suite general conocida sigue siendo la ejecutada el **01/09/2026**:

- Tests run: **529**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **14:25 min**
- Finalización: **19:25:53 -03:00**

La validación relacionada de Swing más reciente se ejecutó el **03/09/2026**:

`mvn -Dtest=InversionesPanelTest,ReportesPanelTest,MainFrameInversionesTest,MainFrameReportesTest,MainFrameMovimientosTest,CuentasPanelTest,RegistrarCuentaPanelTest test`

Resultado:

- **20/20 tests en verde**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **09:43 min**
- Finalización: **10:55:56 -03:00**

Validación individual adicional del mismo día: `MainFrameMovimientosTest` **3/3**, `BUILD SUCCESS`, finalización **10:45:01 -03:00**.

Durante estas ejecuciones Surefire mostró el mensaje posterior a `System.exit(0)`, pero las ejecuciones terminaron correctamente con `BUILD SUCCESS`. No se realizó ningún cambio especulativo por ese mensaje.

## Seguridad implementada

La auditoría transversal de seguridad y aislamiento de datos quedó completada e integrada en `main`.

Se mantienen protegidos por propietario/usuario los recursos y operaciones de perfiles, cuentas, categorías, movimientos, posiciones/cartera y operaciones financieras. También se cerraron caminos internos relevantes que podían saltar las validaciones públicas.

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

## Bloques funcionales cerrados

### Alta de movimientos

`RegistrarMovimientoPanel` permite registrar movimientos para la cuenta seleccionada utilizando categoría autorizada y activa, tipo, importe, fecha/hora, descripción y `usuarioId`. `MovimientosPanel` refresca el listado mediante callback después de un alta exitosa.

Validación histórica del bloque: **10/10 tests en verde**.

### Alta de cuentas

`RegistrarCuentaPanel` permite seleccionar tipo de cuenta, institución financiera, moneda e identificador externo y delega el alta a `CuentaService.registrar(cuenta, usuarioId)`.

`CuentasPanel` refresca el listado mediante callback. `MainFrame` conserva el constructor histórico basado en `perfilFinancieroId` y dispone de un constructor contextual completo con `PerfilFinanciero`, `InstitucionFinancieraService` y `MonedaService`.

`CuentaService` soporta el alta tanto sin transacción activa como dentro de una transacción existente.

### Inversiones y reportes

`InversionesPanel` muestra posiciones filtradas por usuario/perfil. `ReportesPanel` utiliza la funcionalidad de reporte existente en `CarteraActivoService`. Ambos están integrados en `MainFrame` y su navegación está conectada a las tarjetas correspondientes.

La integración de ambos paneles fue corregida para conservar el `PerfilFinanciero` en los constructores de `MainFrame` (`c7cca8f`).

## Últimos cambios relevantes

- `7ac8f99` — `fix: integrar inversiones y reportes en MainFrame`;
- `c7cca8f` — `fix: conservar perfil en constructores de MainFrame`;
- `29b5e11` — `test: corregir expectativa de habilitacion del alta de cuentas`.

El último cambio fue exclusivamente de test: `MainFrameMovimientosTest` ahora completa el nombre de cuenta antes de verificar que el botón de alta quede habilitado, respetando la regla ya cubierta por `RegistrarCuentaPanelTest`.

## Criterio de estado

Los bloques de alta de movimientos y alta de cuentas están cerrados dentro de sus alcances validados. La integración de inversiones y reportes está cubierta por la batería relacionada de Swing.

No se realizó merge a `main` y no se crean ramas nuevas para continuar.

## Próximo paso

Antes de iniciar otro bloque funcional, revisar nuevamente el código actual de `feature/swing-shell`, sus clases relacionadas, servicios, repositorios, reglas de negocio y tests. Luego decidir si corresponde incorporar los dos commits documentales de `main` mediante una estrategia explícita o continuar la feature separada.
