# SOFP — Tests

## Estado de validación — 07/09/2026

### Validación general

Última ejecución general informada por el usuario mediante `mvn test`:

- Tests run: **602**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración: **10:54 min**;
- finalización: **07/09/2026 14:59:12 -03:00**.

Esta suite general no fue repetida después de los últimos cambios de cobertura de saldo.

### Validación relacionada más reciente

El usuario ejecutó el 07/09/2026 a las **20:12:52 -03:00**:

`mvn -Dtest=MovimientoServiceSaldoTest,MovimientoServiceTest,IngresoServiceTest,GastoServiceTest test`

Resultado:

- Tests run: **61**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración: **03:09 min**.

Detalle conocido: `MovimientoServiceTest` **50/50** y `MovimientoServiceSaldoTest` **3/3**; `IngresoServiceTest` y `GastoServiceTest` quedaron incluidos en la misma ejecución sin fallos.

### Fondos insuficientes y saldo

El bloque específico de reglas de saldo quedó completado y validado.

`MovimientoServiceSaldoTest`: **3/3**.

Casos cubiertos:

1. rechazo de un `EGRESO` que supera el saldo disponible;
2. aceptación de un `EGRESO` exactamente igual al saldo disponible;
3. aceptación del aumento del importe de un `EGRESO` hasta el saldo disponible.

El primer intento del nuevo test utilizaba accidentalmente el overload interno de `MovimientoService.registrar`, que no aplica la validación de saldo. Se corrigió el fixture para utilizar la API pública con `usuario.getId()`. El cambio quedó registrado en `06a9fd8`.

No fue necesario modificar producción para resolver este fallo de test.

### Movimientos

`MovimientoServiceTest`: **50/50** en la última ejecución relacionada.

La cobertura existente incluye registro, consultas, modificaciones, eliminación y reglas de negocio de movimientos. El bloque adicional de saldo aporta cobertura específica sin duplicar el caso de cambio de `INGRESO` a `EGRESO` ya existente.

### Ingresos y gastos

`IngresoServiceTest` y `GastoServiceTest` participaron de la ejecución relacionada más reciente, dentro del total **61/61**.

`GastoService` continúa rechazando `TARJETA_CREDITO` hasta disponer del modelo de obligaciones/pasivos.

### Gestión de categorías

`CategoriaServiceTest`: **23/23**.

La cobertura confirma que una categoría con movimientos no se elimina físicamente y se desactiva para conservar el historial. También se cubre la interfaz mediante `CategoriasPanelTest`.

El aislamiento de persistencia de `CategoriaServiceTest` se corrigió en `85b767c`.

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

Pruebas específicas recientes de UI conocidas:

- `CuentasPanelTest`: **3/3**;
- `MovimientosPanelTest`: **3/3**;
- `CategoriasPanelTest`: **4/4**.

Total de esas tres pruebas específicas: **10/10**.

## Criterio de validación

No considerar una funcionalidad terminada solamente porque compila. Cada nuevo bloque debe validar éxito, null cuando corresponda, entidad inexistente, reglas de negocio, persistencia, relaciones y casos límite relevantes.

Antes del cierre: tests específicos → tests relacionados → suite general cuando corresponda → `git diff` → `git diff --check` → `git status`.

## Próximo bloque de tests

El siguiente bloque deberá cubrir las reglas de obligaciones/pasivos si se habilita el tratamiento de tarjeta de crédito. No debe anticiparse esa funcionalidad ni modificar tests para forzarla.
