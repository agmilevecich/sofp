# SOFP — Continuidad 2026-09-15

## Estado auditado

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.
**Último commit de código validado:** `e95585e043290eebb5789f2b628b1edcef8a7344` — `test: cubrir pagos multidivisa en PagoTarjetaService`.

La rama de trabajo continúa separada de `main`. No se realizó merge.

## Último bloque implementado

Se completó la integración del pago de obligaciones multidivisa en `PagoTarjetaService`.

### Modelo

- `Movimiento` conserva la moneda económica del consumo.
- `Obligacion` conserva `monedaOriginal` y `monedaLiquidacion`.
- `TipoCambio` conserva la cotización histórica utilizada.
- `importeLiquidacion` se calcula explícitamente.
- `saldoLiquidacion` representa la deuda en moneda de liquidación.
- `PagoTarjetaService` utiliza `saldoLiquidacion` cuando existe.
- La cuenta pagadora debe estar en `monedaLiquidacion`.
- Los pagos liquidados se aplican mediante `registrarPagoLiquidacion`.
- Las obligaciones no liquidadas mantienen el flujo de `saldoPendiente`/`registrarPago`.
- No existen conversiones implícitas.

### Ejemplo validado

Consumo USD 100 con tarjeta ARS y cotización histórica USD→ARS 1500: deuda liquidada ARS 150.000. Un pago ARS 50.000 deja saldo de liquidación ARS 100.000 y mantiene saldo original USD 100.

## Validación del bloque

- `PagoTarjetaServiceTest`: **10/10**.
- Validación relacionada informada: **19/19**.
- Failures: 0.
- Errors: 0.
- Skipped: 0.
- `BUILD SUCCESS`.
- Finalizada: **15/09/2026 18:37:13 -03:00**.

La última suite completa conocida antes de esta integración fue `mvn test`: **712/712**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizada **15/09/2026 18:05:46 -03:00**. Ese resultado no incluye los cambios posteriores de `PagoTarjetaService`.

## Decisiones multidivisa vigentes

1. La moneda del consumo es la moneda económica del `Movimiento`.
2. La moneda de la tarjeta/cuenta es la moneda de liquidación.
3. La obligación conserva ambas monedas.
4. La conversión histórica es explícita y trazable.
5. La cotización utilizada queda asociada a la obligación.
6. El pago de una obligación liquidada se realiza en moneda de liquidación.
7. No se deben introducir conversiones implícitas.
8. No se debe recalcular una liquidación histórica con una cotización posterior.

## Pendiente real

1. Ejecutar suite relacionada completa sobre el estado actual.
2. Ejecutar `mvn test` sobre el estado actual.
3. Revisar `git diff`, `git diff --check` y `git status`.
4. Definir el impacto de consumos en moneda distinta sobre el límite/crédito disponible.
5. Completar cobertura de persistencia/UI del pago multidivisa.

## Próximo paso

Validar el estado completo después de la integración de `PagoTarjetaService`. Una vez renovada la suite, actualizar nuevamente esta documentación con el resultado real.
