# SOFP — Continuidad 2026-09-08

## Estado verificado

Este documento registra el corte de continuidad del proyecto al 08/09/2026. La fuente de verdad es el código, los tests y los commits actuales; esta documentación sirve para recuperar contexto y no reemplaza la verificación en GitHub.

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

Último cambio funcional verificado: `f9339db2a500d5530eea95dc39e14e2725f4eb8f` — `test: cubrir navegacion hacia ingresos`.

Los commits posteriores son documentales y actualizan el estado de continuidad. No se realizó merge a `main`.

## Último bloque funcional cerrado

El bloque de Ingresos quedó completado e integrado en el shell Swing.

El flujo es:

**`IngresosPanel` → `IngresoService` → `MovimientoService` → `Movimiento` `INGRESO` → `Movimientos`.**

`IngresosPanel` permite seleccionar cuentas y categorías activas, informar importe, fecha y descripción y registrar el ingreso con el usuario autorizado.

Commits funcionales del bloque:

- `d99cc6a` — `feat: agregar formulario de ingresos`.
- `2977f36` — `test: cubrir formulario de ingresos`.
- `4e6b363` — `feat: integrar ingresos al shell`.
- `cd781a1` — `feat: agregar ingresos a la navegacion`.
- `f9339db` — `test: cubrir navegacion hacia ingresos`.

## Estado funcional acumulado

La Fase 8 integra mediante `MainFrame` y `CardLayout` los paneles de Inicio, Cuentas, Categorías, Ingresos, Gastos, Movimientos, Inversiones, Reportes y Obligaciones.

Arquitectura acordada:

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

Gastos utiliza:

**`GastosPanel` → `GastoService` → `MovimientoService` → `Movimiento` `EGRESO` → `Movimientos`.**

Ingresos utiliza el mismo núcleo con `TipoMovimiento.INGRESO`.

## Obligaciones y tarjeta de crédito

El modelado de obligaciones está implementado con `EstadoObligacion`, `Obligacion`, `ObligacionRepository` y `ObligacionService`, persistencia JPA y estados `PENDIENTE`, `PARCIAL` y `PAGADA`.

`GastoService`, al registrar `TARJETA_CREDITO` con `ObligacionService`, crea el movimiento de egreso y la obligación asociada. Sin el servicio de obligaciones, el uso de tarjeta de crédito se rechaza con `IllegalStateException`.

Los pagos autorizados se realizan mediante el servicio con `usuarioId`, manteniendo el aislamiento por propietario.

`ObligacionesPanel` está integrado al shell. Lista obligaciones del usuario, muestra importe original, saldo pendiente, estado y fecha de origen, permite registrar pagos, maneja errores y refresca la lista conservando la selección.

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

El usuario ejecutó `mvn test` el **08/09/2026 15:16:17 -03:00**.

Resultado:

- Tests run: **630**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración: **11:55 min**.

Esta ejecución es posterior a la incorporación de Ingresos y constituye la validación general completa más reciente.

### Tests focalizados de Ingresos/navegación

El usuario ejecutó el **08/09/2026 14:55:35 -03:00**:

`mvn -Dtest=IngresosPanelTest,MainFrameNavigationTest test`

Resultado: **5/5**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`, duración **01:21 min**.

### Tests relacionados

El usuario ejecutó el **08/09/2026 14:58:53 -03:00**:

`mvn -Dtest=IngresoServiceTest,IngresosPanelTest,MainFrameNavigationTest,MainFrameObligacionesTest,ObligacionesPanelTest,GastosPanelTest test`

Resultado: **18/18**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`, duración **01:31 min**.

La batería relacionada confirma que Ingresos no introduce regresiones en Gastos, Obligaciones ni navegación.

## Último resultado de tests conocido

**630/630 — BUILD SUCCESS** para la suite general completa.

**18/18 — BUILD SUCCESS** para la batería relacionada de Ingresos.

**5/5 — BUILD SUCCESS** para la batería focalizada de Ingresos/navegación.

La suite general pasó de **626 a 630 tests** después de incorporar el bloque de Ingresos, sin fallos ni errores.

## Estado Git verificado

`main` permanece en `a4be85913847200cb70976d5266d9cbba10b3100`.

La comparación verificada en GitHub indica que `feature/swing-shell` está **366 commits adelante y 0 atrás** respecto de `main`.

Los últimos cambios funcionales son los cinco commits del bloque de Ingresos indicados arriba. Los commits documentales posteriores actualizan continuidad y validaciones.

El estado local de `git diff`, `git diff --check` y `git status` no se asume; debe verificarse en el entorno local.

## Próximo paso

El bloque de Ingresos está implementado, integrado y validado. El siguiente paso funcional puede ser evolucionar transferencias mediante `OperacionFinanciera`, manteniéndolas diferenciadas de ingresos y gastos.

Después pueden abordarse pasivos y patrimonio neto, análisis históricos, vencimientos y dashboard.

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
