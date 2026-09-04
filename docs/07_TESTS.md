# SOFP — Tests

## Estado de validación — 04/09/2026

### Suite general

Última ejecución general registrada en `feature/swing-shell`:

- comando: `mvn clean test`;
- Tests run: **577**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración: **11:16 min**;
- finalización: **04/09/2026 18:54:20 -03:00**.

Esta es la validación general vigente. No asumir ejecuciones posteriores sin resultado informado por el usuario.

### Fondos insuficientes

`MovimientoFondosInsuficientesTest`: **6/6 tests en verde**.

Casos cubiertos:

1. egreso menor al saldo disponible;
2. egreso igual al saldo disponible, dejando saldo cero;
3. egreso mayor al saldo, rechazado;
4. modificación de egreso dentro del saldo disponible;
5. modificación que produciría saldo negativo, rechazada;
6. cambio de `INGRESO` a `EGRESO` cuando no existen fondos suficientes, rechazado.

La regla se valida en el servicio y contempla también modificaciones de movimientos.

### Movimientos

Validación posterior a la incorporación de fondos insuficientes:

- `MovimientoServiceTest`: **57/57**;
- `RegistrarMovimientoPanelTest`: **4/4**.

El caso `MovimientoServiceTest.deberiaModificarTipoMovimiento` fue adaptado con un ingreso previo independiente para que la conversión de un movimiento de 50.000 a egreso sea válida bajo la nueva regla de fondos. El cambio está en `2b2bf3e` y conserva la intención del test.

### Gestión de categorías

`CategoriasPanelTest`: **3/3 tests en verde** en la validación conocida anterior.

El caso agregado cubre el rechazo de registro de una categoría sin nombre mediante `IllegalArgumentException`.

### Inversiones y reportes

Las baterías conocidas continúan validadas:

- `InversionesPanelTest` + `MainFrameInversionesTest` + `MainFrameReportesTest`: **5/5**;
- `CarteraActivoServiceTest` + `CarteraActivoServiceComposicionTest` + `CarteraActivoServiceMovimientosTest`: **16/16**;
- total conocido del bloque: **21/21**.

### Alta de cuentas

`RegistrarCuentaPanelTest` y `CuentasPanelTest` cubren construcción, dependencias, instituciones activas, monedas, alta, persistencia, identificador externo vacío, listado autorizado, refresco y rechazo de perfil ajeno.

## Próxima cobertura necesaria

### Categorías con movimientos

Cuando se resuelva la eliminación de categorías, deberán cubrirse como mínimo:

- categoría sin movimientos;
- categoría con movimientos;
- conservación del historial;
- rechazo del borrado físico cuando corresponda;
- desactivación de la categoría;
- respuesta amigable de la UI ante la regla de negocio;
- comportamiento ante categoría inexistente y casos límite relevantes.

### FormaPago

Cuando se incorpore `FormaPago`, deberán cubrirse sus relaciones con el movimiento y la distinción entre cuenta afectada y medio de pago, incluyendo los casos particulares de tarjeta de crédito.

## Cobertura Swing

Tests relacionados: `MainFrameTest`, `MainFrameLayoutTest`, `MainFrameNavigationTest`, `MainFrameMovimientosTest`, `MainFrameCategoriasTest`, `MainFrameInversionesTest`, `MainFrameReportesTest`, `CuentasPanelTest`, `MovimientosPanelTest`, `CategoriasPanelTest`, `InversionesPanelTest`, `ReportesPanelTest`, `RegistrarCuentaPanelTest` y `RegistrarMovimientoPanelTest`.

## Seguridad

`AislamientoDatosServiceTest`: **7/7** en verde. La seguridad cubre autorización por propietario en perfiles, cuentas, categorías, movimientos, operaciones financieras y posiciones/cartera.

## Criterio de validación

No considerar una funcionalidad terminada solamente porque compila. Cada nuevo bloque debe validar éxito, null cuando corresponda, entidad inexistente, reglas de negocio, persistencia, relaciones y casos límite relevantes.

Antes del cierre: tests específicos → tests relacionados → suite general cuando corresponda → `git diff` → `git diff --check` → `git status`.
