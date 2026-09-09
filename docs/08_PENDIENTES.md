# SOFP — Pendientes

## Estado — 09/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell` → `13a68fb8429d930b2137b9c9e78f7b884077f33e` como último commit funcional; los commits documentales posteriores no cambian el comportamiento.

La comparación verificada en GitHub indica **411 commits adelante y 0 atrás** respecto de `main`. No se realizó merge a `main`.

## Último bloque cerrado

### Moneda en movimientos y obligaciones

**Completado y validado.**

Los movimientos admiten moneda explícita. Una compra con tarjeta de crédito conserva la moneda económica informada en el movimiento de origen y la obligación expone esa moneda.

Una compra en USD genera una obligación en USD y una compra en ARS una obligación en ARS. No se realiza conversión automática al crear la obligación.

`ObligacionesPanel` muestra importe original y saldo pendiente junto con el código de moneda. El formato numérico utiliza `Locale.ROOT` para que el separador decimal no dependa del locale del entorno.

Commits funcionales:

- `9b92eac` — `feat: exponer moneda de la obligacion`.
- `47ced65` — `feat: mostrar moneda en obligaciones`.
- `fe0aa71` — `test: verificar moneda en obligaciones`.
- `13a68fb` — `fix: estabilizar formato de moneda en obligaciones`.

Validaciones informadas por el usuario:

- **4/4** `ObligacionesPanelTest`.
- **642/642** suite general.
- `git diff`, `git diff --check`, `git status` → working tree limpio y rama sincronizada.

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
- Integración de Transferencias en `Main` y `MainFrame`.
- Navegación hacia Transferencias.
- Moneda explícita en movimientos.
- Conservación de moneda en obligaciones.
- Visualización de moneda en obligaciones.
- Suite general posterior al bloque de moneda: **642/642**.

## Validación actual

### Suite general

Ejecutada e informada por el usuario el **09/09/2026 13:15:48 -03:00**:

- `mvn test`;
- **642/642**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración **09:43 min**.

### ObligacionesPanelTest

Ejecutada e informada el **09/09/2026 13:05:03 -03:00**:

`mvn test -Dtest=ObligacionesPanelTest` → **4/4**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`, duración **01:20 min**.

## Integración

No hacer merge a `main` automáticamente.

No crear ramas nuevas salvo indicación explícita.

Antes de una eventual integración revisar commits, comparación con `main`, tests, `git diff`, `git diff --check`, `git status` y documentación.

## Continuidad

Ante una nueva sesión reconstruir el estado desde GitHub priorizando código → tests → commits → `main` → documentación.

La documentación debe reflejar el código actual y nunca declarar implementada una funcionalidad únicamente porque esté escrita aquí.
