# SOFP — Pendientes

## Estado — 08/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell` → `aa29d44` como último commit funcional.

La comparación verificada en GitHub indica **377 commits adelante y 0 atrás** respecto de `main`. No se realizó merge a `main`.

## Último bloque cerrado

### Transferencias — formulario e integración al shell

**Completado y validado.**

`TransferenciasPanel` utiliza `OperacionFinancieraService` para registrar transferencias entre cuentas propias mediante `OperacionFinanciera`. La transferencia no se modela como ingreso o gasto independiente.

El formulario permite cuenta origen, cuenta destino, categoría, importe, fecha y descripción, utilizando cuentas/categorías activas del perfil/usuario autorizado.

Cada transferencia genera una operación financiera con dos movimientos: `EGRESO` en origen e `INGRESO` en destino.

Commits funcionales:

- `1753074` — `feat: agregar formulario de transferencias`.
- `aa29d44` — `test: cubrir formulario de transferencias`.

Validaciones:

- **4/4** `TransferenciasPanelTest`.
- **5/5** `TransferenciasPanelTest,MainFrameNavigationTest`.
- **634/634** suite general.

### Ingresos

**Completado y validado.**

`IngresosPanel` → `IngresoService` → `MovimientoService` → `Movimiento` `INGRESO`.

### Reglas de saldo

**Completado y validado.**

`MovimientoService` rechaza `EGRESO` superior al saldo disponible, permite el egreso igual al saldo y aplica la regla a modificaciones.

### FormaPago

**Completado y validado.**

`FormaPago` está integrada en `Movimiento`, `MovimientoService`, `GastoService` y `GastosPanel`.

### Obligaciones — dominio, servicio y UI Swing

**Completado y validado.**

El modelo incluye `EstadoObligacion`, `Obligacion`, `ObligacionRepository`, `ObligacionService`, pagos autorizados por usuario, `ObligacionesPanel` e integración con `MainFrame`/`SidebarPanel`.

## Pendiente inmediato

### 1. Ampliar pasivos y patrimonio neto

**Pendiente.**

Las obligaciones son una primera representación de pasivos. Falta evolucionar el modelo para obtener una visión más completa de pasivos y patrimonio neto.

### 2. Análisis y dashboard

**Pendiente.**

Evolucionar progresivamente resúmenes mensuales/históricos, distribución por categoría/tipo, evolución patrimonial, vencimientos y dashboard.

### 3. Pulido de consola

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
- Formulario e integración de Ingresos.
- Navegación hacia Ingresos.
- Formulario e integración de Transferencias.
- Navegación hacia Transferencias.
- Suite general posterior a Transferencias: **634/634**.

## Validación actual

### Suite general

Ejecutada e informada por el usuario el **08/09/2026 18:13:55 -03:00**:

- `mvn test`;
- **634/634**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración **11:52 min**.

### Suite focalizada de Transferencias

Ejecutada e informada el **08/09/2026 17:59:29 -03:00**:

`mvn -Dtest=TransferenciasPanelTest test` → **4/4**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`, duración **01:16 min**.

### Suite de Transferencias y navegación

Ejecutada e informada el **08/09/2026 18:01:19 -03:00**:

`mvn -Dtest=TransferenciasPanelTest,MainFrameNavigationTest test` → **5/5**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`, duración **39 s**.

## Integración

No hacer merge a `main` automáticamente.

No crear ramas nuevas salvo indicación explícita.

Antes de una eventual integración revisar commits, comparación con `main`, tests, `git diff`, `git diff --check`, `git status` y documentación.

## Continuidad

Ante una nueva sesión reconstruir el estado desde GitHub priorizando código → tests → commits → `main` → documentación.

La documentación debe reflejar el código actual y nunca declarar implementada una funcionalidad únicamente porque esté escrita aquí.
