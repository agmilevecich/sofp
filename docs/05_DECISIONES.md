# SOFP — Decisiones

Este documento registra decisiones permanentes del proyecto. Código y tests actuales prevalecen ante contradicciones históricas.

## D-001 a D-044

Se mantienen las decisiones anteriores: repositorio como memoria permanente; desarrollo incremental por Builds; JPA/Hibernate; H2; `BigDecimal`; dominio antes de interfaz; tests como condición de avance; continuidad documental; transferencias mediante `OperacionFinanciera`; paneles especializados sobre `Movimiento`; separación `Cuenta`/`FormaPago`; modelo de activos/pasivos/patrimonio; control de fondos; categorías inactivables; roadmap no equivalente a implementación; gastos e ingresos sobre `Movimiento`; tarjeta de crédito como origen de `Obligacion`; obligación como pasivo especializado; compatibilidad de constructores del shell; pagos autorizados por usuario; refresco de obligaciones; transferencias coordinadas; moneda explícita; UI con moneda; criterio de crédito disponible; ciclos históricos; aislamiento JPA/H2; cuotas generadas por el flujo de gasto; H2 TCP para aplicación; integridad histórica de `Cuenta`; ciclo histórico de obligación; vencimiento de fin de semana; límites temporales de pago; gracia y mora; compatibilidad con datos existentes; multidivisa sin conversiones implícitas; comparabilidad monetaria; financiación avanzada independiente; documentación subordinada al código; `Moneda.cantidadDecimales` no negativa.

## D-045 — Moneda original y moneda de liquidación son conceptos distintos

Un consumo puede tener una moneda económica distinta de la moneda de la tarjeta. `Obligacion` conserva ambas monedas: la original corresponde al consumo y la de liquidación corresponde a la cuenta/tarjeta que debe cancelar la deuda.

## D-046 — La liquidación multidivisa es explícita y trazable

La conversión no se realiza implícitamente al crear la obligación. Una liquidación multidivisa utiliza una cotización histórica explícita y queda asociada a la obligación.

## D-047 — `TipoCambio` representa una cotización histórica

`TipoCambio` conserva moneda origen, moneda destino, cotización, fecha/hora y fuente. La cotización es histórica y no se reemplaza retroactivamente por una cotización posterior.

## D-048 — La obligación conserva el tipo de cambio utilizado

`Obligacion` mantiene `tipoCambioLiquidacion` e `importeLiquidacion`. Una obligación ya liquidada no puede liquidarse nuevamente.

## D-049 — Validación de monedas en la liquidación

`Obligacion.liquidar(TipoCambio)` exige que la moneda origen coincida con `monedaOriginal` y que la moneda destino coincida con `monedaLiquidacion`. Una discrepancia es error de negocio.

## D-050 — Los pagos multidivisa se aplican en la moneda de liquidación

Cuando una obligación ya fue liquidada, `PagoTarjetaService` utiliza `saldoLiquidacion` como deuda pagable y exige que la cuenta pagadora utilice `monedaLiquidacion`. El pago se aplica mediante `registrarPagoLiquidacion`.

Cuando la obligación no fue liquidada, se conserva el flujo existente basado en `saldoPendiente` y `registrarPago`.

Esta decisión evita conversiones implícitas durante el pago y mantiene separadas la deuda económica original y la deuda efectivamente liquidada.

## D-051 — El impacto de moneda extranjera sobre el crédito disponible queda pendiente

Todavía no se define la regla definitiva para expresar límite/crédito disponible cuando el consumo está en una moneda distinta de la moneda de la tarjeta. Esa decisión debe cerrarse antes de modificar ese cálculo.

## Actualización — 15/09/2026

La integración de `PagoTarjetaService` quedó implementada y validada.

- `PagoTarjetaServiceTest`: 10/10.
- Validación relacionada: 19/19, 0 failures, 0 errors, 0 skipped.
- Suite general actual: **718/718**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.
- Finalizada: **15/09/2026 20:05:19 -03:00**.

La suite general confirma el estado actual después de la integración. No existe objetivo de recuperar artificialmente el conteo histórico de 704 tests.
