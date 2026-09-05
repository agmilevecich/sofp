# SOFP — Tests

## Estado de validación — 05/09/2026

### Suite general vigente

Última ejecución general informada por el usuario mediante `mvn test`:

- Tests run: **590**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración: **11:29 min**;
- finalización: **05/09/2026 13:04:09 -03:00**.

Esta es la ejecución más reciente conocida y mantiene toda la suite en verde después de integrar `FormaPago`.

### Fondos insuficientes

`MovimientoFondosInsuficientesTest`: **6/6**.

Casos cubiertos: egreso menor al saldo, egreso igual al saldo, egreso superior rechazado, modificación válida de egreso, modificación que produciría saldo negativo y cambio de `INGRESO` a `EGRESO` sin fondos.

### Movimientos

- `MovimientoServiceTest`: **57/57**;
- `RegistrarMovimientoPanelTest`: **4/4**.

La regla de fondos se mantiene en registro y modificaciones.

### Gestión de categorías

`CategoriaServiceTest`: **23/23**.

La cobertura confirma que una categoría con movimientos no se elimina físicamente y se desactiva para conservar el historial. También se cubre la interfaz mediante `CategoriasPanelTest`.

El aislamiento de persistencia de `CategoriaServiceTest` se corrigió en `85b767c`.

### Gastos

`GastosPanelTest` cubre construcción, dependencias, cuentas y categorías activas, registro exitoso, persistencia como `EGRESO`, importe, descripción, fecha y reflejo en el historial común.

El fixture utiliza un ingreso previo de $1.000 y registra un gasto de $100 para validar la regla real de fondos. El ajuste corresponde a `98dead73`.

### FormaPago

La cobertura actual incluye:

- definición de las cinco formas de pago;
- construcción de `Movimiento` con forma de pago;
- lectura mediante `getFormaPago()`;
- modificación mediante `cambiarFormaPago()`;
- compatibilidad del constructor anterior;
- selección de forma de pago en `GastosPanel`;
- persistencia de la forma de pago;
- rechazo de `TARJETA_CREDITO` en `GastoService` mientras no exista el modelo de obligaciones/pasivos.

El nombre de la prueba de dominio fue corregido en `6cdc3736128718b8c8ca803928c31c5b190a2866`.

### Inversiones y reportes

Las baterías conocidas continúan validadas:

- `InversionesPanelTest` + `MainFrameInversionesTest` + `MainFrameReportesTest`: **5/5**;
- `CarteraActivoServiceTest` + `CarteraActivoServiceComposicionTest` + `CarteraActivoServiceMovimientosTest`: **16/16**;
- total conocido del bloque: **21/21**.

### Alta de cuentas

`RegistrarCuentaPanelTest` y `CuentasPanelTest` cubren construcción, dependencias, instituciones activas, monedas, alta, persistencia, identificador externo, listado autorizado, refresco y aislamiento de perfiles.

`RegistrarCuentaPanelTest`: **6/6**.

### Seguridad

`AislamientoDatosServiceTest`: **7/7**. La autorización cubre perfiles, cuentas, categorías, movimientos, operaciones financieras y posiciones/cartera.

## Cobertura Swing

Tests relacionados: `MainFrameTest`, `MainFrameLayoutTest`, `MainFrameNavigationTest`, `MainFrameMovimientosTest`, `MainFrameCategoriasTest`, `MainFrameInversionesTest`, `MainFrameReportesTest`, `CuentasPanelTest`, `MovimientosPanelTest`, `CategoriasPanelTest`, `GastosPanelTest`, `InversionesPanelTest`, `ReportesPanelTest`, `RegistrarCuentaPanelTest` y `RegistrarMovimientoPanelTest`.

## Criterio de validación

No considerar una funcionalidad terminada solamente porque compila. Cada nuevo bloque debe validar éxito, null cuando corresponda, entidad inexistente, reglas de negocio, persistencia, relaciones y casos límite relevantes.

Antes del cierre: tests específicos → tests relacionados → suite general cuando corresponda → `git diff` → `git diff --check` → `git status`.

## Próximo bloque de tests

El siguiente bloque deberá cubrir las reglas de obligaciones/pasivos si se habilita el tratamiento de tarjeta de crédito. No debe anticiparse esa funcionalidad ni modificar tests para forzarla.
