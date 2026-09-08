# SOFP — Continuidad 2026-09-08

## Estado verificado

Este documento registra el corte de continuidad del proyecto al 08/09/2026. La fuente de verdad es el código, los tests y los commits actuales; esta documentación sirve para recuperar contexto y no reemplaza la verificación en GitHub.

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.
**Último commit funcional:** `aa29d44` — `test: cubrir formulario de transferencias`.

La comparación verificada en GitHub antes de esta actualización indicó **377 commits adelante y 0 atrás** respecto de `main`. No se realizó merge a `main`.

## Último bloque funcional cerrado

El bloque de Transferencias quedó completado, integrado al shell Swing y validado.

El flujo es:

**`TransferenciasPanel` → `OperacionFinancieraService` → `OperacionFinanciera` + `Movimiento` `EGRESO`/`INGRESO`.**

`TransferenciasPanel` permite seleccionar cuentas y categorías activas, informar importe, fecha y descripción y registrar la transferencia con el usuario autorizado.

Una transferencia entre cuentas propias genera una operación financiera con un `EGRESO` en origen y un `INGRESO` en destino; no se trata como ingreso o gasto independiente.

Commits funcionales del bloque:

- `1753074` — `feat: agregar formulario de transferencias`.
- `aa29d44` — `test: cubrir formulario de transferencias`.

## Estado funcional acumulado

La Fase 8 integra mediante `MainFrame` y `CardLayout` los paneles de Inicio, Cuentas, Categorías, Ingresos, Gastos, Movimientos, Inversiones, Reportes, Obligaciones y Transferencias.

Arquitectura acordada:

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

Gastos utiliza `GastoService` sobre `Movimiento` como núcleo. Ingresos utiliza `IngresoService` sobre `Movimiento`. Transferencias utilizan `OperacionFinanciera` para coordinar los movimientos de origen y destino.

## Obligaciones y tarjeta de crédito

El modelado de obligaciones está implementado con `EstadoObligacion`, `Obligacion`, `ObligacionRepository` y `ObligacionService`, persistencia JPA y estados `PENDIENTE`, `PARCIAL` y `PAGADA`.

`GastoService`, al registrar `TARJETA_CREDITO` con `ObligacionService`, crea el movimiento de egreso y la obligación asociada. Sin el servicio de obligaciones, el uso de tarjeta de crédito se rechaza con `IllegalStateException`.

Los pagos autorizados se realizan mediante el servicio con `usuarioId`.

`ObligacionesPanel` está integrado al shell, permite registrar pagos y refresca la lista conservando la selección.

## Reglas financieras vigentes

- Un `EGRESO` superior al saldo disponible se rechaza.
- Un `EGRESO` igual al saldo disponible está permitido y deja saldo cero.
- Las modificaciones de importe y tipo respetan fondos disponibles.
- Categorías con movimientos se conservan y se desactivan en lugar de eliminarse físicamente.
- Cuenta y forma de pago son conceptos distintos.
- Una compra con tarjeta de crédito genera un egreso y una obligación; no simula el pago inmediato de la cuenta.
- Transferencias entre cuentas propias no son ingresos ni gastos y se modelan mediante `OperacionFinanciera`.

## Tests

### Suite general más reciente

El usuario ejecutó `mvn test` el **08/09/2026 18:13:55 -03:00**.

Resultado:

- Tests run: **634**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración: **11:52 min**.

Esta ejecución es posterior a la incorporación de Transferencias y constituye la validación general completa más reciente.

### Tests focalizados de Transferencias

El usuario ejecutó el **08/09/2026 17:59:29 -03:00**:

`mvn -Dtest=TransferenciasPanelTest test`

Resultado: **4/4**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`, duración **01:16 min**.

### Tests de Transferencias y navegación

El usuario ejecutó el **08/09/2026 18:01:19 -03:00**:

`mvn -Dtest=TransferenciasPanelTest,MainFrameNavigationTest test`

Resultado: **5/5**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`, duración **39 s**.

## Último resultado de tests conocido

**634/634 — BUILD SUCCESS** para la suite general completa.

**5/5 — BUILD SUCCESS** para Transferencias y navegación.

**4/4 — BUILD SUCCESS** para Transferencias.

La suite general pasó de **630 a 634 tests** al incorporar los cuatro tests de `TransferenciasPanelTest`, sin fallos ni errores.

## Estado Git verificado

`main` permanece en `a4be85913847200cb70976d5266d9cbba10b3100`.

La comparación verificada antes de esta actualización indicó que `feature/swing-shell` estaba **377 commits adelante y 0 atrás** respecto de `main`, con merge base en `main`.

Los cambios posteriores al último commit funcional son documentales y no agregan comportamiento funcional.

El estado local de `git diff`, `git diff --check` y `git status` no se asume; debe verificarse en el entorno local.

## Próximo paso

El bloque de Transferencias está implementado, integrado y validado. El siguiente bloque funcional recomendado es **ampliar pasivos y patrimonio neto**, partiendo del modelo de obligaciones ya existente.

Después pueden abordarse análisis históricos, vencimientos, evolución patrimonial y dashboard.

Como tarea de pulido posterior queda limpiar la salida de consola de la aplicación sin eliminar la posibilidad de diagnóstico.

## Protocolo permanente de continuidad

Ante una nueva sesión:

1. revisar la rama de trabajo;
2. revisar últimos commits;
3. comparar con `main`;
4. revisar README y documentación de continuidad;
5. revisar archivos modificados recientemente;
6. revisar tests relacionados;
7. identificar último cambio, último test conocido y próximo paso.

Prioridad:

**código → tests → commits → `main` → documentación → conversaciones anteriores.**

No modificar `main`, no crear ramas nuevas salvo indicación explícita, no asumir sincronizaciones ni resultados de tests y no considerar implementada una funcionalidad únicamente porque esté documentada.
