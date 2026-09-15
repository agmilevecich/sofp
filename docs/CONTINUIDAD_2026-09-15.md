# SOFP — Continuidad 2026-09-15

## Estado auditado

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.
**HEAD actual:** `c74717ab0e97398764a7ef6b4c9cb55cb93f20c5` — `test: persistir liquidacion historica de Obligacion`.

La rama de trabajo está 677 commits adelante de `main` y 0 atrás. No se realizó merge a `main`.

La fuente de verdad es código, tests y commits actuales. Esta documentación es auxiliar.

## Último bloque cerrado

Se completó la primera implementación trazable de liquidación multidivisa de obligaciones.

### Modelo

- `Movimiento` conserva la moneda económica original del consumo.
- `Obligacion` conserva `monedaOriginal` y `monedaLiquidacion`.
- `importeLiquidacion` queda separado del importe original y es inicialmente nulo.
- `TipoCambio` representa una cotización histórica inmutable: moneda origen, moneda destino, cotización, fecha/hora y fuente.
- `Obligacion` conserva el `TipoCambio` utilizado para su liquidación.
- `Obligacion.liquidar(TipoCambio)` valida monedas, evita una segunda liquidación y calcula el importe de liquidación mediante la cotización histórica.
- No existe conversión implícita ni dependencia de una cotización actual para modificar una deuda histórica.

Ejemplo soportado: consumo de USD 100 con tarjeta cuya moneda de liquidación es ARS; con una cotización histórica USD→ARS de 1500, la liquidación queda en ARS 150.000,00.

## Validación del bloque

- `ObligacionLiquidacionTest`: **5/5**.
- `ObligacionTipoCambioJpaTest`: **1/1**.
- Bloque relacionado de obligaciones: **27/27**.
- Suite completa `mvn test`: **712/712**.
- Failures: 0.
- Errors: 0.
- Skipped: 0.
- `BUILD SUCCESS`.
- Finalizada: **15/09/2026 18:05:46 -03:00**.

También se validaron previamente:

- `TipoCambioTest`: **10/10**.
- `TipoCambioJpaTest`: **1/1**.
- `ObligacionTest`: **12/12**.
- `ObligacionJpaTest`: **3/3**.
- `MonedaTest`: **7/7**.
- Suite relacionada `MonedaTest,CuentaTest,CuentaJpaTest,MovimientoTest`: **53/53**.

## Decisiones multidivisa vigentes

1. La moneda del consumo es la moneda económica del `Movimiento`.
2. La moneda de la tarjeta/cuenta es la moneda de liquidación.
3. La obligación conserva ambas monedas.
4. La conversión histórica debe ser explícita y trazable.
5. La cotización utilizada queda asociada a la obligación.
6. No se deben introducir conversiones implícitas.
7. No se debe recalcular una liquidación histórica con una cotización posterior.

## Pendiente real

El modelo de liquidación histórica ya está preparado, pero todavía falta integrar esta regla en el flujo de pago real de `PagoTarjetaService`.

Antes de modificarlo hay que revisar su implementación actual y sus tests, especialmente la validación que hoy exige coincidencia entre moneda de la obligación y moneda de la cuenta pagadora.

También queda por definir y cubrir el impacto de consumos en moneda distinta sobre el crédito disponible de la tarjeta.

## Próximo paso

Revisar `PagoTarjetaService`, `PagoTarjetaServiceTest` y las pruebas de UI relacionadas para diseñar el cambio mínimo que permita liquidar obligaciones multidivisa de forma explícita, usando `TipoCambio` histórico, sin romper el comportamiento de moneda coincidente.

Después del cambio: tests específicos → relacionados → suite completa → diff → diff-check → status → documentación.
