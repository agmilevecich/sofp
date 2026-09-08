# SOFP — Continuidad 2026-09-08

## Estado verificado

Este documento registra el corte de continuidad del proyecto al 08/09/2026. La fuente de verdad es el código, los tests y los commits actuales; esta documentación sirve para recuperar contexto y no reemplaza la verificación en GitHub.

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

Último commit funcional antes del bloque documental: `87052df953dbd282a43c5647d05b68d1854c4f17` — `test: cubrir navegacion hacia obligaciones`.

Los commits posteriores son exclusivamente documentales y actualizan el estado de continuidad.

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

El modelado básico de obligaciones está implementado.

Existen `EstadoObligacion`, `Obligacion`, `ObligacionRepository` y `ObligacionService`, con persistencia JPA y estados `PENDIENTE`, `PARCIAL` y `PAGADA`.

`GastoService`, al registrar `TARJETA_CREDITO` con `ObligacionService`, crea el movimiento de egreso y la obligación asociada. Sin el servicio de obligaciones, el uso de tarjeta de crédito se rechaza con `IllegalStateException`.

Los pagos autorizados se realizan mediante el servicio con `usuarioId`, manteniendo el aislamiento por propietario.

`ObligacionesPanel` ya está integrado al shell. Lista obligaciones del usuario, muestra importe original, saldo pendiente, estado y fecha de origen, permite registrar pagos, maneja errores y refresca la lista conservando la selección.

## Reglas financieras vigentes

- Un `EGRESO` superior al saldo disponible se rechaza.
- Un `EGRESO` igual al saldo disponible está permitido y deja saldo cero.
- Las modificaciones de importe y tipo respetan fondos disponibles.
- Categorías con movimientos se conservan y se desactivan en lugar de eliminarse físicamente.
- Cuenta y forma de pago son conceptos distintos.
- Una compra con tarjeta de crédito genera un egreso y una obligación; no simula el pago inmediato de la cuenta.
- Transferencias entre cuentas propias no son ingresos ni gastos y se modelan mediante `OperacionFinanciera`.

## Tests

### Suite general

El usuario ejecutó:

`mvn test`

Resultado el **08/09/2026 13:27:36 -03:00**:

- Tests run: **618**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración: **21:26 min**.

Esta ejecución es anterior a la UI de obligaciones y sigue siendo la última suite general completa conocida.

### Tests focalizados de obligaciones/UI

El usuario ejecutó el **08/09/2026 14:14:14 -03:00**:

`mvn -Dtest=MainFrameNavigationTest,MainFrameObligacionesTest,ObligacionesPanelTest,ObligacionServiceTest test`

Resultado:

- Tests run: **14**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración: **01:48 min**.

`ObligacionesPanelTest`: **3/3**.

La primera ejecución del panel tuvo un fallo por pérdida de selección durante el refresco. `166b5f0` corrigió ese comportamiento y la ejecución posterior quedó verde.

## Último resultado de tests conocido

**14/14 — BUILD SUCCESS** para el bloque focalizado de obligaciones/UI.

**618/618 — BUILD SUCCESS** para la última suite general, pero anterior a la UI de obligaciones.

No debe asumirse ninguna ejecución posterior hasta que el usuario la informe o GitHub/CI la confirme.

## Próximo paso

Ejecutar la suite completa `mvn test` sobre el estado actual y luego revisar:

1. `git diff`;
2. `git diff --check`;
3. `git status`;
4. comparación de `feature/swing-shell` con `main`.

Después continuar con ingresos/transferencias, pasivos y patrimonio neto, análisis histórico, vencimientos y dashboard.

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
