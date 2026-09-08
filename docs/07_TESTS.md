# SOFP — Tests

## Estado de validación — 08/09/2026

### Validación general más reciente

El usuario ejecutó `mvn test` el **08/09/2026 18:13:55 -03:00**.

Resultado:

- Tests run: **634**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración: **11:52 min**.

Esta es la suite general completa más reciente y valida la incorporación de Transferencias sin regresiones.

### Validación focalizada de Transferencias

El usuario ejecutó `mvn -Dtest=TransferenciasPanelTest test` el **08/09/2026 17:59:29 -03:00**.

Resultado: **4/4**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`, duración **01:16 min**.

### Validación de Transferencias y navegación

El usuario ejecutó `mvn -Dtest=TransferenciasPanelTest,MainFrameNavigationTest test` el **08/09/2026 18:01:19 -03:00**.

Resultado: **5/5**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`, duración **39 s**.

## Transferencias

`TransferenciasPanelTest` cubre:

- construcción del formulario del shell sin contexto;
- fecha inicial actual y botón deshabilitado sin contexto;
- filtrado de cuentas y categorías activas del perfil/usuario autorizado;
- registro persistente de una transferencia mediante `OperacionFinancieraService`;
- una operación con cuenta origen, cuenta destino, importe y dos movimientos (`EGRESO`/`INGRESO`);
- fecha y descripción de la operación;
- rechazo de dependencias obligatorias nulas.

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

La suite general pasó de **630 a 634 tests** con la incorporación de los cuatro tests de `TransferenciasPanelTest`, manteniendo **0 failures, 0 errors y 0 skipped**.

## Criterio de validación

No considerar una funcionalidad terminada solamente porque compila. Cada nuevo bloque debe validar éxito, null cuando corresponda, entidad inexistente, reglas de negocio, persistencia, relaciones y casos límite relevantes.

Las comprobaciones locales `git diff`, `git diff --check` y `git status` deben ser informadas desde el entorno local; no se asumen desde GitHub.

## Próximo bloque de tests

La suite general del estado actual está validada. El próximo bloque de tests deberá acompañar la siguiente funcionalidad que se implemente.
