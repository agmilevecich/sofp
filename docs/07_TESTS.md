# SOFP — Tests

## Estado de validación — 08/09/2026

### Validación general más reciente

El usuario ejecutó `mvn test` el **08/09/2026 15:16:17 -03:00**.

Resultado:

- Tests run: **630**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración: **11:55 min**.

Esta es la suite general completa más reciente y valida la integración de Ingresos y navegación sin regresiones en los bloques anteriores.

### Validación focalizada de Ingresos y navegación

El usuario ejecutó:

`mvn -Dtest=IngresosPanelTest,MainFrameNavigationTest test`

Resultado informado el **08/09/2026 14:55:35 -03:00**:

- Tests run: **5**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración: **01:21 min**.

### Validación relacionada de Ingresos

El usuario ejecutó el **08/09/2026 14:58:53 -03:00**:

`mvn -Dtest=IngresoServiceTest,IngresosPanelTest,MainFrameNavigationTest,MainFrameObligacionesTest,ObligacionesPanelTest,GastosPanelTest test`

Resultado:

- Tests run: **18**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración: **01:31 min**.

Esta batería valida Ingresos junto con gastos, obligaciones y navegación del shell.

## Ingresos

La cobertura actual incluye:

- `IngresoServiceTest`: registro mediante el núcleo financiero común.
- `IngresosPanelTest`: construcción sin contexto, filtrado de cuentas/categorías activas, registro persistente y dependencias obligatorias.
- `MainFrameNavigationTest`: navegación hacia Ingresos desde el shell.

El flujo probado es `IngresosPanel → IngresoService → MovimientoService → Movimiento INGRESO`.

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

Tests relacionados incluyen `MainFrameTest`, `MainFrameLayoutTest`, `MainFrameNavigationTest`, `MainFrameMovimientosTest`, `MainFrameCategoriasTest`, `MainFrameInversionesTest`, `MainFrameReportesTest`, `MainFrameObligacionesTest`, `CuentasPanelTest`, `MovimientosPanelTest`, `CategoriasPanelTest`, `GastosPanelTest`, `IngresosPanelTest`, `InversionesPanelTest`, `ReportesPanelTest`, `RegistrarCuentaPanelTest` y `RegistrarMovimientoPanelTest`.

Validación específica de Ingresos/navegación: **5/5**.

Validación relacionada: **18/18**.

Validación general posterior a Ingresos: **630/630**.

## Criterio de validación

No considerar una funcionalidad terminada solamente porque compila. Cada nuevo bloque debe validar éxito, null cuando corresponda, entidad inexistente, reglas de negocio, persistencia, relaciones y casos límite relevantes.

El bloque de Ingresos completó: tests específicos → tests relacionados → suite general. Las comprobaciones locales `git diff`, `git diff --check` y `git status` deben ser informadas por el usuario; no se asumen desde GitHub.

## Próximo bloque de tests

La suite general del estado actual está validada. El próximo bloque de tests deberá acompañar la siguiente funcionalidad que se implemente.
