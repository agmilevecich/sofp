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

## D-051 — La valorización de cierre es histórica y separada de la liquidación

Para una obligación multidivisa puede registrarse una valorización de cierre mediante `Obligacion.valorarCierre(TipoCambio)`. Esta operación conserva `tipoCambioCierre` e `importeValorizacionCierre` y no modifica `importeOriginal`, `saldoPendiente`, `importeLiquidacion`, `saldoLiquidacion` ni el estado de la obligación.

La valorización de cierre no reemplaza a `liquidar(TipoCambio)`: son conceptos distintos. La primera permite expresar históricamente el consumo en la moneda de la tarjeta para fines como el crédito disponible; la segunda determina una liquidación explícita y pagable en la moneda de liquidación.

## D-052 — El crédito utilizado usa la valorización histórica de cierre

Para calcular el crédito utilizado de una tarjeta, una obligación pendiente en la moneda de la tarjeta utiliza su `saldoPendiente`. Una obligación pendiente cuya moneda original difiere de la moneda de la tarjeta utiliza la valorización de cierre cuando existe. En este último caso, el valor se reduce proporcionalmente según la relación entre `saldoPendiente` e `importeOriginal`.

Los consumos de tarjeta sin obligación asociada continúan considerándose según el comportamiento existente. No se realiza una conversión implícita al registrar el consumo extranjero.

## D-053 — La valorización de cierre no disponible no se inventa

Una obligación multidivisa sin `importeValorizacionCierre` no recibe una cotización implícita o posterior solamente para completar el cálculo. El momento y mecanismo mediante el cual la aplicación obtendrá la valorización de cierre forman parte del siguiente diseño de negocio.

## D-054 — El pago posterior y la valorización de cierre siguen siendo conceptos independientes

La valorización histórica de cierre no determina por sí sola la forma de pago futura. El modelo conserva por separado deuda original, valorización de cierre y, cuando corresponde, liquidación explícita. El comportamiento futuro de pagos multidivisa sin liquidación previa deberá definirse antes de alterar estas reglas.

## Actualización — 16/09/2026

La valorización de cierre, su utilización para crédito disponible y el ajuste proporcional después de pagos parciales quedaron implementados y validados.

- `TipoCambioRepositoryTest`: 6/6.
- `ObligacionServiceCierreTest`: 4/4.
- `ObligacionRepositoryTest`: 7/7.
- `MovimientoCreditoMultimonedaTest`: 1/1.
- `PagoTarjetaServiceTest`: 10/10.
- `ObligacionLiquidacionTest`: 13/13.
- Suite general `mvn test`: **740/740**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.
- Finalizada: **16/09/2026 15:54:16 -03:00**.

La validación local final dejó `git diff` vacío, `git diff --check` sin observaciones y working tree limpio.
