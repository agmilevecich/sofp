# SOFP — Tests

## Estado de validación — 17/09/2026

### Validación más reciente

- `mvn test`: **756/756**.
- Failures: 0.
- Errors: 0.
- Skipped: 0.
- `BUILD SUCCESS`.
- Finalizada: **17/09/2026 15:50:11 -03:00**.

### Validaciones específicas del bloque de crédito

`CuentaServiceCreditoTest`: **3/3**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizada **17/09/2026 15:33:38 -03:00**.

`TarjetaCreditoPagoCreditoTest`: **5/5**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizada **17/09/2026 15:36:06 -03:00**.

La cobertura incorpora el caso de una obligación multidivisa pagada parcialmente en moneda original, luego liquidada y finalmente cancelada en moneda de liquidación, verificando que el crédito se libere por completo.

### Validaciones específicas relevantes anteriores

- `TipoCambioRepositoryTest`: 6/6.
- `ObligacionServiceCierreTest`: 4/4.
- `ObligacionRepositoryTest`: 7/7.
- `MovimientoCreditoMultimonedaTest`: 1/1.
- `PagoTarjetaServiceTest`: 10/10.
- `ObligacionLiquidacionTest`: 13/13.
- `ObligacionesPanelTest`: 6/6.

La cobertura actual confirma valorización histórica, crédito multidivisa, proporcionalidad después de pagos parciales, liquidación/pago, liberación de crédito y cierre iniciado desde UI.

### Evolución del último bloque

La nueva prueba inicialmente encontró que el `TipoCambio` de liquidación debía estar persistido antes de asociarlo a una obligación. Se corrigió el test persistiendo explícitamente la cotización histórica y la suite completa quedó en **756/756**.

### Pendiente de cobertura/diseño

- flujo completo de obtención y registro de valorización de cierre dentro de la aplicación;
- comportamiento de crédito para obligaciones multidivisa todavía no valorizadas;
- persistencia/UI del flujo integral de cierre y pago multidivisa;
- estabilización de arranque H2, logging y manejo de errores de la aplicación estable.

## Criterio de cierre

Tests específicos → relacionados → suite general → `git diff` → `git diff --check` → `git status` → documentación. Los resultados locales solo se consideran conocidos cuando son informados por el usuario.
