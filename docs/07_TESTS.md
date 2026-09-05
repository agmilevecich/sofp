# SOFP — Tests

## Estado de validación — 05/09/2026

### Suite general

Última ejecución general registrada e informada por el usuario antes de los commits de `FormaPago`:

- comando: `mvn clean test`;
- Tests run: **580**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración: **10:58 min**;
- finalización: **05/09/2026 09:49:58 -03:00**.

Esta es la validación general vigente. No asumir ejecuciones posteriores sin un nuevo resultado informado.

### Fondos insuficientes

`MovimientoFondosInsuficientesTest`: **6/6 tests en verde**.

Casos cubiertos:

1. egreso menor al saldo disponible;
2. egreso igual al saldo disponible, dejando saldo cero;
3. egreso mayor al saldo, rechazado;
4. modificación de egreso dentro del saldo disponible;
5. modificación que produciría saldo negativo, rechazada;
6. cambio de `INGRESO` a `EGRESO` cuando no existen fondos suficientes, rechazado.

### Movimientos

- `MovimientoServiceTest`: **57/57**;
- `RegistrarMovimientoPanelTest`: **4/4**.

El caso `MovimientoServiceTest.deberiaModificarTipoMovimiento` fue adaptado con un ingreso previo independiente para que la conversión sea válida bajo la regla de fondos. Commit: `2b2bf3e`.

### Gestión de categorías

`CategoriaServiceTest`: **23/23 tests en verde**.

La cobertura confirma que una categoría con movimientos asociados no se elimina físicamente, se conserva el historial y se desactiva. También se cubre el comportamiento de la UI mediante `CategoriasPanelTest`.

Durante la suite se detectó un problema de aislamiento por reutilización del contexto de persistencia y moneda `ARS`. Se corrigió el `setUp()` de `CategoriaServiceTest` cerrando `JpaTestManager` antes de crear el `EntityManager`. Commit: `85b767c`.

### Inversiones y reportes

Las baterías conocidas continúan validadas:

- `InversionesPanelTest` + `MainFrameInversionesTest` + `MainFrameReportesTest`: **5/5**;
- `CarteraActivoServiceTest` + `CarteraActivoServiceComposicionTest` + `CarteraActivoServiceMovimientosTest`: **16/16**;
- total conocido del bloque: **21/21**.

### Alta de cuentas

`RegistrarCuentaPanelTest` y `CuentasPanelTest` cubren construcción, dependencias, instituciones activas, monedas, alta, persistencia, identificador externo vacío, listado autorizado, refresco y rechazo de perfil ajeno.

Validación relacionada: `RegistrarCuentaPanelTest` **6/6**.

## FormaPago

`FormaPagoTest` fue agregado mediante el commit `4ae0a27` para cubrir la definición actual de cinco formas de pago.

**Estado: pendiente de ejecución informada.**

No se debe considerar `FormaPagoTest` en verde hasta que el usuario informe su resultado.

## Próximo bloque de tests

El próximo desarrollo será `GastosPanel`. Su cobertura deberá verificar como mínimo la construcción del formulario, validaciones de datos obligatorios, selección de cuenta/categoría autorizadas y activas, registro del egreso mediante el servicio existente, persistencia y reflejo posterior en el historial de `Movimientos`, además de los casos límite relevantes.

## Cobertura Swing

Tests relacionados: `MainFrameTest`, `MainFrameLayoutTest`, `MainFrameNavigationTest`, `MainFrameMovimientosTest`, `MainFrameCategoriasTest`, `MainFrameInversionesTest`, `MainFrameReportesTest`, `CuentasPanelTest`, `MovimientosPanelTest`, `CategoriasPanelTest`, `InversionesPanelTest`, `ReportesPanelTest`, `RegistrarCuentaPanelTest` y `RegistrarMovimientoPanelTest`.

## Seguridad

`AislamientoDatosServiceTest`: **7/7** en verde. La seguridad cubre autorización por propietario en perfiles, cuentas, categorías, movimientos, operaciones financieras y posiciones/cartera.

## Criterio de validación

No considerar una funcionalidad terminada solamente porque compila. Cada nuevo bloque debe validar éxito, null cuando corresponda, entidad inexistente, reglas de negocio, persistencia, relaciones y casos límite relevantes.

Antes del cierre: tests específicos → tests relacionados → suite general cuando corresponda → `git diff` → `git diff --check` → `git status`.
