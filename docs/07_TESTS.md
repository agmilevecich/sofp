# SOFP — Tests

## Estado de validación — 08/09/2026

### Validación general más reciente

El usuario ejecutó `mvn test` el **08/09/2026 13:27:36 -03:00**.

Resultado:

- Tests run: **618**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración: **21:26 min**.

Esta es la suite general más reciente conocida y posterior a las correcciones de compatibilidad de `MainFrame` y expectativas de tarjeta de crédito.

### Validación focalizada previa

El usuario ejecutó `GastosPanelTest` + `MainFrameMovimientosTest` antes de la suite general.

Resultado: **8/8**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`.

### Correcciones que llevaron a la suite verde

La primera ejecución de la suite general del bloque presentó:

- 1 failure en `GastosPanelTest.deberiaRechazarTarjetaDeCreditoHastaModelarLaObligacion`, porque la implementación vigente produce `IllegalStateException` y el test esperaba `IllegalArgumentException`.
- 2 errors en `MainFrameMovimientosTest`, porque constructores anteriores de `MainFrame` llegaban a `GastoService` sin `ObligacionService` y provocaban `NullPointerException`.

Se corrigió producción en `6c7d70a` — `fix: mantener compatibilidad de MainFrame sin ObligacionService` — y el test en `7f05cd1` — `test: actualizar expectativas de gastos con crédito`.

La suite completa posterior quedó en **618/618**.

### Obligaciones

La cobertura actual incluye:

- `ObligacionTest`: reglas de creación y pagos.
- `ObligacionJpaTest`: persistencia y relación con el movimiento de origen.
- `ObligacionServiceTest`: alta, consulta, pagos parciales, pagos completos, sobrepagos, obligación inexistente e ID nulo.

El dominio `Obligacion` mantiene `importeOriginal`, `saldoPendiente`, `estado` y `movimientoOrigen`.

Los pagos actualizan `PARCIAL` o `PAGADA`, rechazan importes no válidos, sobrepagos y pagos sobre obligaciones ya pagadas.

### Gastos y tarjeta de crédito

`GastoServiceTest` y `GastosPanelTest` cubren el flujo de gastos, forma de pago y el rechazo de tarjeta de crédito cuando el servicio de obligaciones no está disponible.

Con el servicio de obligaciones disponible, `GastoService` crea una obligación asociada al movimiento de egreso.

### Fondos insuficientes y saldo

`MovimientoServiceSaldoTest`: **3/3**.

Casos cubiertos:

1. rechazo de un `EGRESO` que supera el saldo disponible;
2. aceptación de un `EGRESO` exactamente igual al saldo disponible;
3. aceptación del aumento del importe de un `EGRESO` hasta el saldo disponible.

La prueba se corrigió en `06a9fd8` para utilizar la API pública de `MovimientoService` pasando el usuario propietario. No fue necesario modificar producción.

### Movimientos

`MovimientoServiceTest`: **50/50** en la última ejecución relacionada conocida.

La cobertura incluye registro, consultas, modificaciones, eliminación y reglas de negocio de movimientos.

### Gestión de categorías

`CategoriaServiceTest`: **23/23** en la validación conocida.

Una categoría referenciada por movimientos no se elimina físicamente: se conserva y se desactiva.

### FormaPago

La cobertura actual incluye:

- las cinco formas de pago: `EFECTIVO`, `TRANSFERENCIA`, `TARJETA_DEBITO`, `TARJETA_CREDITO` y `QR`;
- construcción y lectura de `Movimiento` con forma de pago;
- modificación de forma de pago;
- compatibilidad del constructor anterior;
- selección desde `GastosPanel`;
- persistencia;
- comportamiento de tarjeta de crédito con y sin `ObligacionService`.

### Inversiones y reportes

Las baterías conocidas continúan integradas en la suite general, incluyendo pruebas de cartera/activos, inversiones y reportes.

### Alta de cuentas

`RegistrarCuentaPanelTest` y `CuentasPanelTest` cubren construcción, dependencias, instituciones activas, monedas, alta, persistencia, identificador externo, listado autorizado, refresco y aislamiento de perfiles.

`RegistrarCuentaPanelTest`: **6/6** en la validación conocida.

### Seguridad

`AislamientoDatosServiceTest`: **7/7** en la validación conocida. La autorización cubre perfiles, cuentas, categorías, movimientos, operaciones financieras y posiciones/cartera.

## Cobertura Swing

Tests relacionados incluyen `MainFrameTest`, `MainFrameLayoutTest`, `MainFrameNavigationTest`, `MainFrameMovimientosTest`, `MainFrameCategoriasTest`, `MainFrameInversionesTest`, `MainFrameReportesTest`, `CuentasPanelTest`, `MovimientosPanelTest`, `CategoriasPanelTest`, `GastosPanelTest`, `InversionesPanelTest`, `ReportesPanelTest`, `RegistrarCuentaPanelTest` y `RegistrarMovimientoPanelTest`.

Validación específica reciente:

- `GastosPanelTest` + `MainFrameMovimientosTest`: **8/8**.

Validaciones específicas UI anteriores conocidas:

- `CuentasPanelTest`: **3/3**;
- `MovimientosPanelTest`: **3/3**;
- `CategoriasPanelTest`: **4/4**;
- total de esas tres: **10/10**.

## Criterio de validación

No considerar una funcionalidad terminada solamente porque compila. Cada nuevo bloque debe validar éxito, null cuando corresponda, entidad inexistente, reglas de negocio, persistencia, relaciones y casos límite relevantes.

Antes del cierre: tests específicos → tests relacionados → suite general cuando corresponda → `git diff` → `git diff --check` → `git status`.

## Próximo bloque de tests

Si se incorpora UI para obligaciones, deberán agregarse tests de Swing para consulta, selección, registro de pagos, errores y refresco del estado de la obligación, sin duplicar reglas que pertenecen a dominio/servicio.
