# SOFP — Continuidad 2026-09-08

## Estado verificado

Este documento registra el corte de continuidad del proyecto al 08/09/2026. La fuente de verdad es el código, los tests y los commits actuales; esta documentación sirve para recuperar contexto y no reemplaza la verificación en GitHub.

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

Último cambio funcional: `87052df953dbd282a43c5647d05b68d1854c4f17` — `test: cubrir navegacion hacia obligaciones`.

El HEAD actual contiene commits documentales posteriores, siendo el más reciente `92ac4d0af4005458d41d4c3c4eb8d98064016c3d` — `docs: cerrar validacion de obligaciones`.

No se realizó merge a `main`.

## Últimos cambios funcionales

El bloque de obligaciones quedó completado en dominio, persistencia, servicio, autorización y UI Swing.

Commits funcionales recientes:

- `43cfd9c` — autorización de pagos.
- `456fbfb` — tests de autorización.
- `f20023d` — `ObligacionesPanel`.
- `264dd54` — navegación/sidebar.
- `87b8e46` — integración en `MainFrame`.
- `7194a5d` — tests del panel.
- `029de48` — test de navegación.
- `166b5f0` — conservar selección al refrescar obligaciones.
- `87052df` — cubrir navegación hacia obligaciones.

## Estado funcional

La Fase 8 integra mediante `MainFrame` y `CardLayout` los paneles de Inicio, Cuentas, Categorías, Gastos, Movimientos, Inversiones, Reportes y Obligaciones.

Arquitectura acordada:

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

Gastos utiliza:

**`GastosPanel` → `GastoService` → `MovimientoService` → `Movimiento` `EGRESO` → `Movimientos`.**

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

El usuario ejecutó `mvn test` el **08/09/2026 14:41:08 -03:00**.

Resultado:

- Tests run: **626**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración: **12:35 min**.

Esta ejecución valida la integración de la UI de obligaciones y pagos con la suite completa.

### Tests focalizados de obligaciones/UI

El usuario ejecutó el **08/09/2026 14:14:14 -03:00**:

`mvn -Dtest=MainFrameNavigationTest,MainFrameObligacionesTest,ObligacionesPanelTest,ObligacionServiceTest test`

Resultado: **14/14**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`, duración **01:48 min**.

`ObligacionesPanelTest`: **3/3**.

La primera ejecución del panel tuvo un fallo por pérdida de selección durante el refresco. `166b5f0` corrigió ese comportamiento y la ejecución posterior quedó verde.

## Último resultado de tests conocido

**626/626 — BUILD SUCCESS** para la suite general completa.

**14/14 — BUILD SUCCESS** para el bloque focalizado de obligaciones/UI.

El incremento desde la suite general anterior fue de **618 a 626 tests**, sin fallos ni errores.

## Estado Git verificado

El usuario ejecutó localmente `git diff`, `git diff --check` y `git status` sobre `feature/swing-shell`.

Resultado informado:

- rama actual: `feature/swing-shell`;
- sincronizada con `github/feature/swing-shell`;
- `nothing to commit, working tree clean`.

En GitHub, la rama `feature/swing-shell` está **356 commits adelante y 0 atrás** respecto de `main`, cuyo HEAD es `a4be859...`.

## Próximo paso

La integración de obligaciones/UI está validada por tests focalizados y por la suite general completa, y el árbol de trabajo local está limpio.

El siguiente paso funcional puede ser evolucionar ingresos/transferencias, pasivos y patrimonio neto, análisis históricos, vencimientos y dashboard.

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

No modificar `main`, no crear ramas nuevas salvo indicación explícita, no asumir sincronizaciones ni resultados de tests y no considerar implementada una funcionalidad solamente porque aparezca documentada.
