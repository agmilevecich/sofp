# SOFP — Tests

## Estado de validación — 09/09/2026

### Validación general más reciente

El usuario ejecutó `mvn test` el **09/09/2026 13:15:48 -03:00**.

Resultado:

- Tests run: **642**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración: **09:43 min**.

Esta es la suite general completa más reciente y valida la integración existente sin regresiones.

### Obligaciones con moneda

El usuario ejecutó `mvn test -Dtest=ObligacionesPanelTest` el **09/09/2026 13:05:03 -03:00**.

Resultado: **4/4**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`, duración **01:20 min**.

La cobertura incluye la visualización del código de moneda de la obligación tanto para el importe original como para el saldo pendiente.

## Moneda en obligaciones

`ObligacionesPanelTest` verifica una obligación originada por un gasto con `TARJETA_CREDITO` en USD y comprueba que el renderer muestre el importe y saldo pendiente con `USD`.

La cobertura valida indirectamente que la obligación expone la moneda del movimiento de origen y que la UI no pierde esa información.

El formato esperado utiliza punto decimal estable (`120.50 USD`), independientemente del locale del entorno.

## Transferencias

`TransferenciasPanelTest` cubre construcción del formulario del shell, filtrado de cuentas y categorías activas, persistencia de una transferencia mediante `OperacionFinancieraService`, operación financiera con movimientos `EGRESO`/`INGRESO`, fecha, descripción y dependencias obligatorias.

La navegación hacia el panel se valida mediante `MainFrameNavigationTest`.

## Ingresos

La cobertura incluye `IngresoServiceTest`, `IngresosPanelTest` y `MainFrameNavigationTest`.

El flujo probado es `IngresosPanel → IngresoService → MovimientoService → Movimiento INGRESO`.

## Obligaciones

La cobertura incluye `ObligacionTest`, `ObligacionJpaTest`, `ObligacionServiceTest`, `ObligacionesPanelTest` y `MainFrameObligacionesTest`, además de navegación.

Los pagos autorizados por usuario utilizan el servicio y no duplican reglas de dominio en Swing.

## Gastos y tarjeta de crédito

`GastoServiceTest` y `GastosPanelTest` cubren gastos, forma de pago y el comportamiento de tarjeta de crédito con y sin `ObligacionService`.

## Fondos insuficientes y saldo

`MovimientoServiceSaldoTest`: **3/3** en la validación conocida.

Casos cubiertos:

1. rechazo de `EGRESO` superior al saldo disponible;
2. aceptación de `EGRESO` exactamente igual al saldo disponible;
3. aceptación del aumento de un `EGRESO` hasta el saldo disponible.

## Movimientos y categorías

`MovimientoServiceTest` y `CategoriaServiceTest` continúan cubriendo las reglas centrales de movimientos y categorías, incluyendo conservación/desactivación de categorías con movimientos.

## FormaPago

La cobertura incluye `EFECTIVO`, `TRANSFERENCIA`, `TARJETA_DEBITO`, `TARJETA_CREDITO` y `QR`, persistencia, modificación, selección desde `GastosPanel` y obligaciones derivadas de tarjeta de crédito.

## Inversiones y reportes

Las baterías existentes continúan integradas en la suite general, incluyendo cartera/activos, inversiones y reportes.

## Alta de cuentas

`RegistrarCuentaPanelTest` y `CuentasPanelTest` cubren construcción, dependencias, instituciones activas, monedas, alta, persistencia, identificador externo, listado autorizado, refresco y aislamiento.

## Seguridad

`AislamientoDatosServiceTest`: **7/7** en la validación conocida. La autorización cubre perfiles, cuentas, categorías, movimientos, operaciones financieras y posiciones/cartera.

## Cobertura Swing

La suite incluye los tests de `MainFrame`, layout, navegación, movimientos, categorías, inversiones, reportes, obligaciones, cuentas y los paneles especializados de gastos, ingresos, inversiones, reportes, obligaciones y transferencias.

## Evolución de la suite

La suite general pasó de **634 a 642 tests** en las validaciones posteriores a la incorporación de la cobertura de moneda en obligaciones.

El bloque de moneda añadió un test específico de `ObligacionesPanelTest` y mantuvo la suite completa en **642/642**, sin fallos ni errores.

## Criterio de validación

No considerar una funcionalidad terminada solamente porque compila. Cada nuevo bloque debe validar éxito, null cuando corresponda, entidad inexistente, reglas de negocio, persistencia, relaciones y casos límite relevantes.

Las comprobaciones locales `git diff`, `git diff --check` y `git status` deben ser informadas desde el entorno local; no se asumen desde GitHub.

## Próximo bloque de tests

La suite general y la cobertura de moneda en obligaciones están validadas. El próximo bloque de tests deberá acompañar la siguiente funcionalidad que se implemente.
