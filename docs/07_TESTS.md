# SOFP — Tests

## Estado de validación — 16/09/2026

### Validación más reciente

- `mvn test`: **744/744**.
- Failures: 0.
- Errors: 0.
- Skipped: 0.
- `BUILD SUCCESS`.
- Finalizada: **16/09/2026 18:47:51 -03:00**.
- Tiempo total: **10:32 min**.

### Validación específica de cierre desde UI

`ObligacionesPanelTest`: **6/6**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizada **16/09/2026 18:22:54 -03:00**.

Los dos casos nuevos cubren cierre desde el panel de una obligación multidivisa con valorización histórica y fallo/rollback cuando falta la cotización histórica.

### Validaciones específicas relevantes

- `TipoCambioRepositoryTest`: 6/6.
- `ObligacionServiceCierreTest`: 4/4.
- `ObligacionRepositoryTest`: 7/7.
- `MovimientoCreditoMultimonedaTest`: 1/1.
- `PagoTarjetaServiceTest`: 10/10.
- `ObligacionLiquidacionTest`: 13/13.

La cobertura actual confirma valorización histórica, crédito multidivisa, proporcionalidad después de pagos parciales, liquidación/pago y cierre iniciado desde UI.

### Pendiente de cobertura/diseño

- flujo completo de obtención y registro de valorización de cierre dentro de la aplicación;
- comportamiento de crédito para obligaciones multidivisa todavía no valorizadas;
- persistencia/UI del flujo integral de cierre y pago multidivisa;
- estabilización de arranque H2, logging y manejo de errores de la aplicación estable.

## Criterio de cierre

Tests específicos → relacionados → suite general → `git diff` → `git diff --check` → `git status` → documentación. Los resultados locales solo se consideran conocidos cuando son informados por el usuario.
