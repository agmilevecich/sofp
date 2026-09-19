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

### Ejemplo

Consumo USD 100 con tarjeta ARS y cotización histórica USD→ARS 1500: deuda liquidada ARS 150.000. Un pago ARS 50.000 deja saldo de liquidación ARS 100.000 y mantiene saldo original USD 100.

## Validación del estado actual

- `mvn test`: **718/718**.
- Failures: 0.
- Errors: 0.
- Skipped: 0.
- `BUILD SUCCESS`.
- Finalizada: **15/09/2026 20:05:19 -03:00**.
- Tiempo total: **09:04 min**.

También se validó `PagoTarjetaServiceTest`: 10/10, y la validación relacionada anterior fue 19/19.

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

1. Revisar `git diff`, `git diff --check` y `git status`.
2. Definir el impacto de consumos en moneda distinta sobre el límite/crédito disponible.
3. Diseñar tests de esa regla antes de modificar el cálculo.
4. Completar cobertura de persistencia/UI del pago multidivisa.

## Próximo paso

Cerrar la etapa documental y comenzar el análisis del cálculo de límite/crédito disponible para consumos en moneda extranjera. Primero se debe entender el cálculo actual y definir la regla de negocio; recién después corresponde modificar código y tests.
