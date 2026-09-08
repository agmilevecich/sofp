# SOFP — Pendientes

## Estado — 08/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell` → `7f05cd1ef48576e0bc59a3828cd254e0bd4a9d9b` antes de la actualización documental.

La comparación verificada con `main` indica que `feature/swing-shell` está **326 commits por delante y 0 por detrás**, con merge-base `a4be85913847200cb70976d5266d9cbba10b3100`. No se realizó merge.

## Último bloque cerrado

### Reglas de saldo de movimientos

**Completado y validado.**

`MovimientoService` rechaza `EGRESO` superior al saldo disponible, permite el egreso igual al saldo y aplica la regla también a modificaciones de importe y tipo.

`MovimientoServiceSaldoTest`: **3/3**.

### FormaPago

**Completado y validado.**

`FormaPago` está integrada en `Movimiento`, `MovimientoService`, `GastoService` y `GastosPanel`, con cinco opciones: `EFECTIVO`, `TRANSFERENCIA`, `TARJETA_DEBITO`, `TARJETA_CREDITO` y `QR`.

### Obligaciones — dominio y servicio

**Completado y validado.**

Ya existe el modelo de obligaciones para compras con tarjeta de crédito:

- `EstadoObligacion`;
- `Obligacion`;
- `ObligacionRepository`;
- `ObligacionService`;
- persistencia JPA;
- registro de pagos parciales y completos;
- actualización de estado a `PARCIAL` o `PAGADA`;
- validación de sobrepagos y obligaciones ya pagadas.

No queda pendiente el modelado básico de obligaciones. La documentación anterior que indicaba lo contrario estaba desactualizada.

## Pendiente inmediato

### 1. UI de obligaciones y pagos

**Pendiente.**

No existe todavía un panel Swing específico para:

- listar obligaciones del perfil/usuario;
- visualizar importe original, saldo pendiente, estado y fecha de origen;
- seleccionar una obligación;
- registrar un pago;
- refrescar el estado después del pago;
- informar errores de forma amigable.

La nueva UI debe utilizar `ObligacionService` y no duplicar reglas de dominio.

### 2. Integración del shell con obligaciones

**Pendiente.**

Debe definirse dónde se accede a las obligaciones dentro de `MainFrame`/`SidebarPanel` y cómo se refrescan los datos después de registrar un gasto con tarjeta de crédito o un pago.

### 3. Evolución de ingresos y transferencias

**Pendiente.**

Continuar consolidando ingresos y transferencias mediante el núcleo financiero común basado en `Movimiento` y `OperacionFinanciera`.

### 4. Pasivos y patrimonio neto

**Pendiente.**

Las obligaciones son ahora una primera representación de pasivos. Falta evolucionar el modelo para obtener una visión más completa de pasivos y patrimonio neto.

### 5. Análisis y dashboard

**Pendiente.**

Evolucionar progresivamente resúmenes mensuales/históricos, distribución por categoría/tipo, evolución patrimonial, vencimientos y dashboard.

### 6. Pulido de consola

**Pendiente de baja prioridad.**

Limpiar la salida de consola de la aplicación sin eliminar la posibilidad de diagnóstico ni alterar innecesariamente la configuración de logging.

## Bloques ya cerrados

- Seguridad y aislamiento de datos.
- Categorías con movimientos.
- Fondos insuficientes y reglas de saldo.
- Primer corte funcional de Gastos.
- FormaPago.
- Modelo de obligaciones y pagos en dominio/persistencia/servicio.
- Pulido visual inicial de Cuentas, Movimientos y Categorías.
- Compatibilidad de constructores de `MainFrame` sin `ObligacionService`.

## Validación actual

Suite general ejecutada e informada por el usuario el **08/09/2026 13:27:36 -03:00**:

- `mvn test`;
- **618/618**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración: **21:26 min**.

Validación focalizada previa: `GastosPanelTest` + `MainFrameMovimientosTest` **8/8**.

Validación final local posterior: `git diff`, `git diff --check` y `git status` limpios; rama sincronizada con `github/feature/swing-shell` y working tree limpio.

## Integración

No hacer merge a `main` automáticamente.

No crear ramas nuevas salvo indicación explícita.

Antes de una eventual integración revisar commits, comparación con `main`, tests, `git diff`, `git diff --check`, `git status` y documentación.

## Continuidad

Ante una nueva sesión reconstruir el estado desde GitHub priorizando código → tests → commits → `main` → documentación.

La documentación debe reflejar el código actual y nunca declarar implementada una funcionalidad únicamente porque esté escrita aquí.
