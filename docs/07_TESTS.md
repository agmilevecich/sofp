# SOFP — Tests

## Estado de validación — 16/09/2026

### Validación más reciente

- `mvn test`: **723/723**.
- Failures: 0.
- Errors: 0.
- Skipped: 0.
- `BUILD SUCCESS`.
- Finalizada: **16/09/2026 13:11:00 -03:00**.
- Tiempo total: **11:02 min**.

El resultado corresponde al estado actual de `feature/swing-shell` después de incorporar la valorización de cierre y su uso en el cálculo del crédito disponible.

### Cobertura multidivisa actual

Ya existe cobertura para:

- saldo de una cuenta separado por moneda;
- fondos disponibles según moneda económica del movimiento;
- coexistencia de saldos ARS/USD sin mezcla;
- obligación con moneda original distinta de la moneda de liquidación;
- persistencia de ambas monedas;
- cotización histórica persistida;
- liquidación explícita con `TipoCambio`;
- rechazo de monedas incompatibles;
- rechazo de segunda liquidación;
- persistencia de la cotización asociada;
- saldo de liquidación de la obligación;
- pago parcial sobre saldo de liquidación;
- pago total sobre saldo de liquidación;
- conservación del saldo original al pagar una obligación liquidada;
- descuento del importe pagado desde la cuenta pagadora;
- valorización de cierre separada de la liquidación;
- persistencia de la valorización de cierre;
- uso de la valorización de cierre para crédito disponible;
- rechazo de segunda valorización de cierre;
- rechazo de valorización de cierre para monedas iguales;
- validación de tipo de cambio nulo para valorización de cierre.

### Validaciones relevantes de la etapa

- `MovimientoCreditoMultimonedaTest`: **1/1**.
- `MovimientoServiceTest,MovimientoMultimonedaTest,MovimientoServiceSaldoTest`: **62/62**.
- `PagoTarjetaServiceTest,SaldoTarjetaCreditoTest,TarjetaCreditoPagoCreditoTest`: **17/17**.
- `MovimientoObligacionIntegridadTest,ObligacionServiceTest`: **14/14**.
- `ObligacionJpaTest`: **3/3**.
- `ObligacionLiquidacionTest`: **13/13**.

### Resultado de suite general

`mvn test` ejecutó **723 tests** con:

- Failures: 0.
- Errors: 0.
- Skipped: 0.
- `BUILD SUCCESS`.

La ejecución finalizó el 16/09/2026 a las 13:11:00 -03:00 y duró 11:02 min.

## Pendiente de cobertura

- flujo de obtención y registro de valorización de cierre dentro de la aplicación;
- comportamiento de crédito para obligaciones multidivisa todavía no valorizadas;
- crédito utilizado después de pagos parciales sobre obligaciones valorizadas;
- persistencia/UI del flujo integral de cierre y pago multidivisa.

## Criterio de cierre

Tests específicos → relacionados → suite general → `git diff` → `git diff --check` → `git status` → documentación. Los resultados locales solo se consideran conocidos cuando son informados por el usuario.
