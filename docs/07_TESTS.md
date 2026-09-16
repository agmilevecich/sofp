# SOFP — Tests

## Estado de validación — 16/09/2026

### Validación más reciente

- `mvn test`: **740/740**.
- Failures: 0.
- Errors: 0.
- Skipped: 0.
- `BUILD SUCCESS`.
- Finalizada: **16/09/2026 15:54:16 -03:00**.
- Tiempo total: **09:50 min**.

El resultado corresponde al estado de `feature/swing-shell` después de corregir el crédito utilizado de obligaciones multidivisa con pagos parciales.

### Validaciones específicas de la etapa

- `TipoCambioRepositoryTest`: **6/6**.
- `ObligacionServiceCierreTest`: **4/4**.
- `ObligacionRepositoryTest`: **7/7**.
- `MovimientoCreditoMultimonedaTest`: **1/1**.
- `PagoTarjetaServiceTest`: **10/10**.
- `ObligacionLiquidacionTest`: **13/13**.

La cobertura confirma valorización histórica, crédito con obligación multidivisa, proporcionalidad después de pagos parciales y compatibilidad con liquidación/pago existentes.

### Pendiente de cobertura

- flujo de obtención y registro de valorización de cierre dentro de la aplicación;
- comportamiento de crédito para obligaciones multidivisa todavía no valorizadas;
- persistencia/UI del flujo integral de cierre y pago multidivisa.

## Criterio de cierre

Tests específicos → relacionados → suite general → `git diff` → `git diff --check` → `git status` → documentación. Los resultados locales solo se consideran conocidos cuando son informados por el usuario.
