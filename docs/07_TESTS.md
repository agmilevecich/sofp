# SOFP — Tests

## Estado de validación — 08/09/2026

### Validación general más reciente

El usuario ejecutó `mvn test` el **08/09/2026 14:41:08 -03:00**.

Resultado:

- Tests run: **626**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración: **12:35 min**.

Esta es la suite general completa más reciente y valida la integración de la UI de obligaciones y pagos.

### Validación focalizada de obligaciones/UI

El usuario ejecutó:

`mvn -Dtest=MainFrameNavigationTest,MainFrameObligacionesTest,ObligacionesPanelTest,ObligacionServiceTest test`

Resultado informado el **08/09/2026 14:14:14 -03:00**:

- Tests run: **14**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración: **01:48 min**.

Cobertura específica: `ObligacionesPanelTest` **3/3**, además de servicio, integración de `MainFrame` y navegación.

### Corrección de UI validada

La primera ejecución del bloque Swing tuvo 1 failure en `ObligacionesPanelTest.deberiaRegistrarPagoYRefrescarSaldo`: después de refrescar, la lista perdía la selección y el botón quedaba deshabilitado.

Se corrigió en `166b5f0` — `fix: conservar seleccion al refrescar obligaciones`, haciendo que `ObligacionesPanel` conserve y restaure la obligación seleccionada al recargar la lista.

La ejecución focalizada posterior quedó en **14/14** y la suite general posterior quedó en **626/626**.

## Obligaciones

La cobertura actual incluye:

- `ObligacionTest`: reglas de creación y pagos.
- `ObligacionJpaTest`: persistencia y relación con el movimiento de origen.
- `ObligacionServiceTest`: alta, consulta, aislamiento por usuario, pagos parciales, pagos completos, sobrepagos, obligación inexistente e ID nulo.
- `ObligacionesPanelTest`: construcción, listado, selección, registro de pago, refresco y comportamiento del botón.
- `MainFrameObligacionesTest`: integración del panel con el shell.
- `MainFrameNavigationTest`: navegación hacia Obligaciones desde el shell.

Los pagos autorizados por usuario utilizan el servicio y no duplican las reglas de dominio en Swing.

## Gastos y tarjeta de crédito

`GastoServiceTest` y `GastosPanelTest` cubren el flujo de gastos, forma de pago y el rechazo de tarjeta de crédito cuando el servicio de obligaciones no está disponible.

Con el servicio de obligaciones disponible, `GastoService` crea una obligación asociada al movimiento de egreso.

## Fondos insuficientes y saldo

`MovimientoServiceSaldoTest`: **3/3**.

Casos cubiertos:

1. rechazo de un `EGRESO` que supera el saldo disponible;
2. aceptación de un `EGRESO` exactamente igual al saldo disponible;
3. aceptación del aumento del importe de un `EGRESO` hasta el saldo disponible.

## Movimientos

`MovimientoServiceTest`: **50/50** en la última ejecución relacionada conocida.

La cobertura incluye registro, consultas, modificaciones, eliminación y reglas de negocio de movimientos.

## Gestión de categorías

`CategoriaServiceTest`: **23/23** en la validación conocida.

Una categoría referenciada por movimientos no se elimina físicamente: se conserva y se desactiva.

## FormaPago

La cobertura actual incluye las cinco formas de pago: `EFECTIVO`, `TRANSFERENCIA`, `TARJETA_DEBITO`, `TARJETA_CREDITO` y `QR`, además de persistencia, modificación, selección desde `GastosPanel` y comportamiento de tarjeta de crédito con y sin `ObligacionService`.

## Inversiones y reportes

Las baterías conocidas continúan integradas en la suite general, incluyendo pruebas de cartera/activos, inversiones y reportes.

## Alta de cuentas

`RegistrarCuentaPanelTest` y `CuentasPanelTest` cubren construcción, dependencias, instituciones activas, monedas, alta, persistencia, identificador externo, listado autorizado, refresco y aislamiento de perfiles.

`RegistrarCuentaPanelTest`: **6/6** en la validación conocida.

## Seguridad

`AislamientoDatosServiceTest`: **7/7** en la validación conocida. La autorización cubre perfiles, cuentas, categorías, movimientos, operaciones financieras y posiciones/cartera.

## Cobertura Swing

Tests relacionados incluyen `MainFrameTest`, `MainFrameLayoutTest`, `MainFrameNavigationTest`, `MainFrameMovimientosTest`, `MainFrameCategoriasTest`, `MainFrameInversionesTest`, `MainFrameReportesTest`, `MainFrameObligacionesTest`, `CuentasPanelTest`, `MovimientosPanelTest`, `CategoriasPanelTest`, `GastosPanelTest`, `InversionesPanelTest`, `ReportesPanelTest`, `RegistrarCuentaPanelTest` y `RegistrarMovimientoPanelTest`.

Validación específica de obligaciones/UI: **14/14**.

Validación general posterior a la UI: **626/626**.

## Criterio de validación

No considerar una funcionalidad terminada solamente porque compila. Cada nuevo bloque debe validar éxito, null cuando corresponda, entidad inexistente, reglas de negocio, persistencia, relaciones y casos límite relevantes.

El bloque actual ya completó: tests específicos → tests relacionados → suite general → `git diff` → `git diff --check` → `git status`.

## Próximo bloque de tests

La suite general del estado actual está validada. El próximo bloque de tests deberá acompañar la siguiente funcionalidad que se implemente.
