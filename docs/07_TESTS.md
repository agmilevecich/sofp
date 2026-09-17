# SOFP — Tests

## Estado de validación — 17/09/2026

### Validación más reciente

- `mvn test`: **761/761**.
- Failures: 0.
- Errors: 0.
- Skipped: 0.
- `BUILD SUCCESS`.
- Finalizada: **17/09/2026 17:54:49 -03:00**.

### Validaciones específicas del bloque multidivisa

`TarjetaCreditoMultidivisaIntegracionTest`: **1/1**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizada **17/09/2026 17:29:45 -03:00**.

`TarjetaCreditoPagoCreditoTest`: **5/5**.

`ObligacionServiceLiquidacionTest`: **4/4**.

Ejecución conjunta de las dos suites relacionadas: **9/9**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizada **17/09/2026 17:40:50 -03:00**.

La cobertura incorpora el ciclo completo de una obligación multidivisa: consumo en moneda original, valorización de cierre, pago parcial en moneda original, liquidación del saldo restante en moneda de liquidación y pago total posterior, verificando la liberación completa del crédito.

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

La prueba de integración fue incorporada y posteriormente se corrigieron únicamente las aserciones `BigDecimal` para comparar valores monetarios sin depender de la escala. La lógica de negocio no fue modificada por esos commits correctivos.

### Pendiente de cobertura/diseño

- flujo completo de obtención y registro de valorización de cierre dentro de la aplicación;
- comportamiento de crédito para obligaciones multidivisa todavía no valorizadas;
- persistencia/UI del flujo integral de cierre y pago multidivisa;
- estabilización de arranque H2, logging y manejo de errores de la aplicación estable.

## Criterio de cierre

Tests específicos → relacionados → suite general → `git diff` → `git diff --check` → `git status` → documentación. Los resultados locales solo se consideran conocidos cuando son informados por el usuario.
