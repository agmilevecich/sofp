# SOFP — Tests

## Estado de validación — 10/09/2026

### Suite general más reciente conocida

El usuario ejecutó `mvn test` el **10/09/2026 10:39:38 -03:00**.

Resultado:

- Tests run: **671**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`.

Esta es la suite general más reciente conocida.

### Última validación relacionada con cuotas

El usuario ejecutó:

`mvn -Dtest=GastosPanelTest,GastoServiceTest,ObligacionTest,ObligacionCuotasTest,PagoTarjetaServiceTest test`

el **10/09/2026 13:14:29 -03:00**.

Resultado:

- Tests run: **32**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración **01:19 min**.

Validaciones focalizadas:

- `PagoTarjetaServiceTest`: **4/4**, `BUILD SUCCESS`, 13:08:50 -03:00.
- `GastosPanelTest`: **6/6**, `BUILD SUCCESS`.

## Cuotas

La cobertura actual verifica la generación de cuotas al registrar gastos con tarjeta y el tratamiento de las cuotas por `PagoTarjetaService`.

`GastosPanelTest` cubre actualmente el registro de un gasto con tres cuotas. El test utiliza el flujo real de `GastoService`, que genera automáticamente las cuotas.

`PagoTarjetaServiceTest.deberiaAplicarPagoDeTarjetaSobreLasCuotasEnOrden` fue ajustado para registrar el gasto con `cantidadCuotas = 3` y ya no genera cuotas manualmente. Esto refleja el comportamiento productivo actual y evita la excepción `La obligación ya tiene cuotas generadas`.

## Crédito y tarjetas

La cobertura incluye límites, crédito disponible, consumo parcial, límite exacto, exceso, monedas diferentes, liberación mediante pagos y consumos sin obligación sin doble contabilización.

## Ciclos de facturación

`CicloFacturacionTest` contiene **9 tests** y cubre cierre exacto, ciclo siguiente, meses cortos, febrero, vencimiento posterior al cierre, cambio de año y fecha nula.

## Obligaciones y moneda

La cobertura incluye `ObligacionTest`, `ObligacionJpaTest`, `ObligacionServiceTest`, `ObligacionesPanelTest` y navegación. Las obligaciones conservan la moneda económica del movimiento de origen.

## Otros bloques

Continúan integrados en la suite: seguridad/aislamiento, cuentas, categorías, movimientos, fondos disponibles, Gastos, Ingresos, Transferencias, Inversiones, Reportes y shell Swing.

## Criterio de cierre

No considerar una funcionalidad terminada solamente porque compila. Cada bloque debe contemplar éxito, null cuando corresponda, entidad inexistente, reglas de negocio, persistencia, relaciones y casos límite relevantes.

Después de cambios importantes: tests específicos → relacionados → suite general cuando corresponda → `git diff` → `git diff --check` → `git status`.

Los estados locales de Git solo se consideran confirmados cuando el usuario los informa o se verifican en el entorno correspondiente.
