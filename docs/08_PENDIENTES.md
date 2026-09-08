# SOFP — Pendientes

## Estado — 08/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

HEAD funcional documentado: `87052df953dbd282a43c5647d05b68d1854c4f17`.
No se realizó merge a `main`.

## Último bloque cerrado

### Reglas de saldo de movimientos

**Completado y validado.**

`MovimientoService` rechaza `EGRESO` superior al saldo disponible, permite el egreso igual al saldo y aplica la regla también a modificaciones de importe y tipo.

`MovimientoServiceSaldoTest`: **3/3** en la validación conocida.

### FormaPago

**Completado y validado.**

`FormaPago` está integrada en `Movimiento`, `MovimientoService`, `GastoService` y `GastosPanel`, con cinco opciones: `EFECTIVO`, `TRANSFERENCIA`, `TARJETA_DEBITO`, `TARJETA_CREDITO` y `QR`.

### Obligaciones — dominio, servicio y UI Swing

**Completado en el bloque actual; tests focalizados verdes.**

El modelo incluye:

- `EstadoObligacion`;
- `Obligacion`;
- `ObligacionRepository`;
- `ObligacionService`;
- persistencia JPA;
- pagos parciales y completos;
- estados `PENDIENTE`, `PARCIAL` y `PAGADA`;
- autorización del pago por usuario;
- `ObligacionesPanel`;
- integración de navegación en `MainFrame`/`SidebarPanel`;
- tests de servicio, panel e integración/navegación.

`ObligacionesPanel` lista obligaciones del usuario, muestra importe original, saldo, estado y fecha de origen, permite registrar pagos y refresca la información conservando la selección.

## Pendiente inmediato

### 1. Suite general posterior a la UI

**Pendiente de ejecución.**

La última suite general conocida fue `mvn test` con **618/618**, ejecutada antes de la incorporación de la UI de obligaciones.

Debe ejecutarse nuevamente sobre el estado actual.

### 2. Evolución de ingresos y transferencias

**Pendiente.**

Continuar consolidando ingresos y transferencias mediante el núcleo financiero común basado en `Movimiento` y `OperacionFinanciera`.

### 3. Pasivos y patrimonio neto

**Pendiente.**

Las obligaciones son una primera representación de pasivos. Falta evolucionar el modelo para obtener una visión más completa de pasivos y patrimonio neto.

### 4. Análisis y dashboard

**Pendiente.**

Evolucionar progresivamente resúmenes mensuales/históricos, distribución por categoría/tipo, evolución patrimonial, vencimientos y dashboard.

### 5. Pulido de consola

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

## Validación actual

### Suite general

Ejecutada e informada por el usuario el **08/09/2026 13:27:36 -03:00**:

- `mvn test`;
- **618/618**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración: **21:26 min**.

Esta ejecución es anterior a la UI de obligaciones y, por lo tanto, no sustituye una nueva suite completa.

### Suite focalizada actual

El usuario ejecutó el **08/09/2026 14:14:14 -03:00**:

`mvn -Dtest=MainFrameNavigationTest,MainFrameObligacionesTest,ObligacionesPanelTest,ObligacionServiceTest test`

Resultado: **14/14**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`, duración **01:48 min**.

`ObligacionesPanelTest`: **3/3**.

## Integración

No hacer merge a `main` automáticamente.

No crear ramas nuevas salvo indicación explícita.

Antes de una eventual integración revisar commits, comparación con `main`, tests, `git diff`, `git diff --check`, `git status` y documentación.

## Continuidad

Ante una nueva sesión reconstruir el estado desde GitHub priorizando código → tests → commits → `main` → documentación.

La documentación debe reflejar el código actual y nunca declarar implementada una funcionalidad únicamente porque esté escrita aquí.
