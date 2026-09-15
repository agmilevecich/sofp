# SOFP — Tests

## Estado de validación — 15/09/2026

### Suite general más reciente

El usuario ejecutó `mvn test` y obtuvo **712/712**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizado **15/09/2026 18:05:46 -03:00**.

### Bloque multidivisa histórico

- `TipoCambioTest`: **10/10**.
- `TipoCambioJpaTest`: **1/1**.
- `ObligacionTest`: **12/12**.
- `ObligacionJpaTest`: **3/3**.
- `ObligacionLiquidacionTest`: **5/5**.
- `ObligacionTipoCambioJpaTest`: **1/1**.
- Suite relacionada de obligaciones: **27/27**.

Todos los resultados fueron informados por el usuario con 0 failures, 0 errors y 0 skipped.

### Validaciones previas relevantes

- `MonedaTest`: **7/7**.
- `MonedaTest,CuentaTest,CuentaJpaTest,MovimientoTest`: **53/53**.
- `CuentaServiceCoberturaTest`: **35/35**.
- `ObligacionJpaTest` tras reglas temporales: **3/3**.

## Cobertura multidivisa

Ya existe cobertura para:

- saldo de una cuenta separado por moneda;
- rechazo de fondos insuficientes cuando existe saldo en otra moneda;
- validación de fondos usando la moneda económica del movimiento;
- coexistencia de saldos ARS y USD;
- obligación con moneda original distinta de la moneda de liquidación;
- persistencia de ambas monedas;
- cotización histórica persistida con sus monedas;
- liquidación explícita con `TipoCambio` histórico;
- rechazo de cotización con moneda origen/destino incorrectas;
- rechazo de segunda liquidación;
- persistencia de la cotización asociada y del importe liquidado.

Todavía debe cubrirse, una vez integrado el flujo de servicio:

- pago real de una obligación multidivisa mediante `PagoTarjetaService`;
- impacto de consumo extranjero sobre crédito disponible;
- interacción completa entre pago, cuenta pagadora y obligación en monedas distintas.

## Criterio de cierre

Un nuevo bloque debe validarse con tests específicos, tests relacionados y suite general antes de considerarse cerrado. Los resultados locales solo se consideran conocidos cuando son informados por el usuario.
