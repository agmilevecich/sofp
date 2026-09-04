# SOFP — Tests

## Estado de validación — 04/09/2026

### Gestión de categorías

`CategoriasPanelTest`: **3/3 tests en verde**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`.

El último caso agregado verifica que registrar una categoría sin nombre sea rechazado con `IllegalArgumentException` y que no se agregue a la lista. citeturn59file0

### Movimientos

Las baterías conocidas del bloque continúan validadas:

- `RegistrarMovimientoPanelTest` + `MainFrameMovimientosTest`: **7/7**;
- `MovimientoServiceTest`: **50/50**;
- total conocido del bloque: **57/57**.

El selector de fecha utiliza LGoodDatePicker y la hora se obtiene mediante `LocalTime.now()` al registrar.

### Inversiones y reportes

Las baterías conocidas continúan validadas:

- `InversionesPanelTest` + `MainFrameInversionesTest` + `MainFrameReportesTest`: **5/5**;
- `CarteraActivoServiceTest` + `CarteraActivoServiceComposicionTest` + `CarteraActivoServiceMovimientosTest`: **16/16**;
- total conocido del bloque: **21/21**.

### Alta de cuentas

`RegistrarCuentaPanelTest` y `CuentasPanelTest` cubren construcción, dependencias, instituciones activas, monedas, alta, persistencia, identificador externo vacío, listado autorizado, refresco y rechazo de perfil ajeno.

## Nuevas reglas que deberán cubrirse

Cuando se implemente el control de fondos insuficientes, los tests deberán contemplar como mínimo: egreso menor al saldo, egreso igual al saldo, egreso mayor al saldo, importe inválido/null cuando corresponda y modificaciones que puedan dejar saldo negativo.

Cuando se resuelva la eliminación de categorías, deberán cubrirse categoría sin movimientos, categoría con movimientos y conservación del historial, además de la respuesta de la UI ante la regla de negocio.

Cuando se incorpore `FormaPago`, deberán cubrirse sus relaciones con el movimiento y la distinción entre cuenta afectada y medio de pago.

## Cobertura Swing

Tests relacionados: `MainFrameTest`, `MainFrameLayoutTest`, `MainFrameNavigationTest`, `MainFrameMovimientosTest`, `MainFrameCategoriasTest`, `MainFrameInversionesTest`, `MainFrameReportesTest`, `CuentasPanelTest`, `MovimientosPanelTest`, `CategoriasPanelTest`, `InversionesPanelTest`, `ReportesPanelTest`, `RegistrarCuentaPanelTest` y `RegistrarMovimientoPanelTest`.

## Seguridad

`AislamientoDatosServiceTest`: **7/7** en verde. La seguridad cubre autorización por propietario en perfiles, cuentas, categorías, movimientos, operaciones financieras y posiciones/cartera.

## Última suite general conocida

**568/568 tests en verde**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`.

Esta cifra es la última conocida y no debe actualizarse con una ejecución no informada por el usuario.

## Criterio de validación

No considerar una funcionalidad terminada solamente porque compila. Cada nuevo bloque debe validar éxito, null cuando corresponda, entidad inexistente, reglas de negocio, persistencia, relaciones y casos límite relevantes.

Antes del cierre: tests específicos → tests relacionados → suite general cuando corresponda → `git diff` → `git diff --check` → `git status`.
