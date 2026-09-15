# SOFP — Tests

## Estado de validación — 15/09/2026

### Validación más reciente del bloque actual

- `PagoTarjetaServiceTest`: **10/10**.
- Validación relacionada informada: **19/19**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.
- Finalizada: **15/09/2026 18:37:13 -03:00**.

### Suite general

La última suite completa informada **antes de la integración multidivisa de `PagoTarjetaService`** fue:

- `mvn test`: **712/712**.
- Failures: 0.
- Errors: 0.
- Skipped: 0.
- `BUILD SUCCESS`.
- Finalizada: **15/09/2026 18:05:46 -03:00**.

Debe ejecutarse nuevamente la suite general sobre el estado actual antes de considerar cerrado este bloque.

## Cobertura multidivisa actual

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
- descuento del importe pagado desde la cuenta pagadora.

## Validaciones previas relevantes

- `TipoCambioTest`: **10/10**.
- `TipoCambioJpaTest`: **1/1**.
- `ObligacionTest`: **12/12**.
- `ObligacionJpaTest`: **3/3**.
- `ObligacionLiquidacionTest`: **5/5**.
- `ObligacionTipoCambioJpaTest`: **1/1**.
- `MonedaTest`: **7/7**.
- `MonedaTest,CuentaTest,CuentaJpaTest,MovimientoTest`: **53/53**.
- `CuentaServiceCoberturaTest`: **35/35**.

## Pendiente de cobertura

- impacto de consumos extranjeros sobre límite/crédito disponible;
- persistencia completa del pago multidivisa en el estado actual;
- integración UI del pago multidivisa;
- suite relacionada completa y suite general después de la integración.

## Criterio de cierre

Tests específicos → relacionados → suite general → `git diff` → `git diff --check` → `git status` → documentación. Los resultados locales solo se consideran conocidos cuando son informados por el usuario.
