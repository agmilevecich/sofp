# SOFP — Pendientes

## Estado — 08/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

Último cambio funcional verificado: `f9339db2a500d5530eea95dc39e14e2725f4eb8f` — `test: cubrir navegacion hacia ingresos`.
No se realizó merge a `main`.

## Último bloque cerrado

### Ingresos — formulario e integración al shell

**Completado y validado.**

`IngresosPanel` utiliza `IngresoService`, que delega en `MovimientoService` para registrar un `Movimiento` de tipo `INGRESO`.

El formulario permite cuenta, categoría, importe, fecha y descripción y utiliza cuentas/categorías activas del perfil/usuario autorizado.

La navegación hacia Ingresos está integrada en `SidebarPanel`/`MainFrame` y tiene cobertura específica.

Commits del bloque:

- `d99cc6a` — `feat: agregar formulario de ingresos`.
- `2977f36` — `test: cubrir formulario de ingresos`.
- `4e6b363` — `feat: integrar ingresos al shell`.
- `cd781a1` — `feat: agregar ingresos a la navegacion`.
- `f9339db` — `test: cubrir navegacion hacia ingresos`.

Validaciones: **5/5** focalizada, **18/18** relacionada y **630/630** suite general.

### Reglas de saldo de movimientos

**Completado y validado.**

`MovimientoService` rechaza `EGRESO` superior al saldo disponible, permite el egreso igual al saldo y aplica la regla también a modificaciones de importe y tipo.

`MovimientoServiceSaldoTest`: **3/3** en la validación conocida.

### FormaPago

**Completado y validado.**

`FormaPago` está integrada en `Movimiento`, `MovimientoService`, `GastoService` y `GastosPanel`, con cinco opciones: `EFECTIVO`, `TRANSFERENCIA`, `TARJETA_DEBITO`, `TARJETA_CREDITO` y `QR`.

### Obligaciones — dominio, servicio y UI Swing

**Completado y validado.**

El modelo incluye `EstadoObligacion`, `Obligacion`, `ObligacionRepository`, `ObligacionService`, persistencia JPA, pagos parciales y completos, autorización por usuario, `ObligacionesPanel` e integración de navegación en `MainFrame`/`SidebarPanel`.

`ObligacionesPanel` lista obligaciones del usuario, muestra importe original, saldo, estado y fecha de origen, permite registrar pagos y refresca la información conservando la selección.

La validación focalizada quedó en **14/14** y las validaciones posteriores del bloque se integran en la suite general de **630/630**.

## Pendiente inmediato

### 1. Evolución de transferencias

**Pendiente.**

Continuar consolidando las transferencias entre cuentas propias mediante `OperacionFinanciera`, manteniéndolas diferenciadas de ingresos y gastos.

### 2. Pasivos y patrimonio neto

**Pendiente.**

Las obligaciones son una primera representación de pasivos. Falta evolucionar el modelo para obtener una visión más completa de pasivos y patrimonio neto.

### 3. Análisis y dashboard

**Pendiente.**

Evolucionar progresivamente resúmenes mensuales/históricos, distribución por categoría/tipo, evolución patrimonial, vencimientos y dashboard.

### 4. Pulido de consola

**Pendiente de baja prioridad.**

Limpiar la salida de consola de la aplicación sin eliminar la posibilidad de diagnóstico ni alterar innecesariamente la configuración de logging.

## Bloques ya cerrados

- Seguridad y aislamiento de datos.
- Categorías con movimientos.
- Fondos insuficientes y reglas de saldo.
- Primer corte funcional de Gastos.
- FormaPago.
- Modelo de obligaciones y pagos en dominio/persistencia/servicio.
- Autorización de pagos de obligaciones por usuario.
- UI Swing de obligaciones y pagos.
- Navegación de obligaciones desde el shell.
- Corrección de conservación de selección al refrescar obligaciones.
- Pulido visual inicial de Cuentas, Movimientos y Categorías.
- Compatibilidad de constructores de `MainFrame` sin `ObligacionService`.
- Formulario e integración de Ingresos en el shell.
- Navegación hacia Ingresos.
- Suite general posterior a Ingresos: **630/630**.

## Validación actual

### Suite general

Ejecutada e informada por el usuario el **08/09/2026 15:16:17 -03:00**:

- `mvn test`;
- **630/630**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración: **11:55 min**.

Esta es la validación general completa más reciente y confirma la integración de Ingresos sin regresiones en la suite.

### Suite focalizada de Ingresos/navegación

El usuario ejecutó el **08/09/2026 14:55:35 -03:00**:

`mvn -Dtest=IngresosPanelTest,MainFrameNavigationTest test`

Resultado: **5/5**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`, duración **01:21 min**.

### Suite relacionada

El usuario ejecutó el **08/09/2026 14:58:53 -03:00**:

`mvn -Dtest=IngresoServiceTest,IngresosPanelTest,MainFrameNavigationTest,MainFrameObligacionesTest,ObligacionesPanelTest,GastosPanelTest test`

Resultado: **18/18**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`, duración **01:31 min**.

## Integración

No hacer merge a `main` automáticamente.

No crear ramas nuevas salvo indicación explícita.

Antes de una eventual integración revisar commits, comparación con `main`, tests, `git diff`, `git diff --check`, `git status` y documentación.

## Continuidad

Ante una nueva sesión reconstruir el estado desde GitHub priorizando código → tests → commits → `main` → documentación.

La documentación debe reflejar el código actual y nunca declarar implementada una funcionalidad únicamente porque esté escrita aquí.
