# SOFP — Decisiones

Este documento registra decisiones permanentes del proyecto. Código y tests actuales prevalecen ante contradicciones históricas.

## D-001 a D-044

Se mantienen las decisiones anteriores: repositorio como memoria permanente; desarrollo incremental por Builds; JPA/Hibernate; H2; `BigDecimal`; dominio antes de interfaz; tests como condición de avance; continuidad documental; transferencias mediante `OperacionFinanciera`; paneles especializados sobre `Movimiento`; separación `Cuenta`/`FormaPago`; modelo de activos/pasivos/patrimonio; control de fondos; categorías inactivables; roadmap no equivalente a implementación; gastos e ingresos sobre `Movimiento`; tarjeta de crédito como origen de `Obligacion`; obligación como pasivo especializado; compatibilidad de constructores del shell; pagos autorizados por usuario; refresco de obligaciones; transferencias coordinadas; moneda explícita; UI con moneda; criterio de crédito disponible; ciclos históricos; aislamiento JPA/H2; cuotas generadas por el flujo de gasto; H2 TCP para aplicación; integridad histórica de `Cuenta`; ciclo histórico de obligación; vencimiento de fin de semana; límites temporales de pago; gracia y mora; compatibilidad con datos existentes; multidivisa sin conversiones implícitas; comparabilidad monetaria; financiación avanzada independiente; documentación subordinada al código; `Moneda.cantidadDecimales` no negativa.

## D-045 — Moneda original y moneda de liquidación son conceptos distintos

Un consumo puede tener una moneda económica distinta de la moneda de la tarjeta. `Obligacion` conserva ambas monedas: la original corresponde al consumo y la de liquidación corresponde a la cuenta/tarjeta que debe cancelar la deuda.

## D-046 — La liquidación multidivisa es explícita y trazable

La conversión no se realiza implícitamente al crear la obligación. Una liquidación multidivisa debe utilizar una cotización histórica explícita y quedar asociada a la obligación.

## D-047 — `TipoCambio` representa una cotización histórica

`TipoCambio` conserva moneda origen, moneda destino, cotización, fecha/hora y fuente. La cotización se considera histórica y no se reemplaza retroactivamente por una cotización posterior.

## D-048 — La obligación conserva el tipo de cambio utilizado

`Obligacion` mantiene la asociación `tipoCambioLiquidacion` y el `importeLiquidacion`. Una obligación ya liquidada no puede liquidarse nuevamente.

## D-049 — Validación de monedas en la liquidación

`Obligacion.liquidar(TipoCambio)` exige que la moneda origen del tipo de cambio coincida con `monedaOriginal` y que la moneda destino coincida con `monedaLiquidacion`. Una discrepancia es error de negocio.

## D-050 — No se modifica todavía `PagoTarjetaService` para multidivisa

La integración con el flujo real de pago se realizará después de cerrar el modelo de liquidación histórica y sus tests. El siguiente cambio debe ser mínimo y preservar el comportamiento existente de pagos en moneda coincidente.

## Actualización — 15/09/2026

El bloque de liquidación histórica multidivisa de obligaciones quedó implementado y validado.

- `TipoCambioTest`: 10/10.
- `TipoCambioJpaTest`: 1/1.
- `ObligacionLiquidacionTest`: 5/5.
- `ObligacionTipoCambioJpaTest`: 1/1.
- Suite completa: **712/712**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.
- Finalizada: **15/09/2026 18:05:46 -03:00**.

Próximo bloque: integración controlada en `PagoTarjetaService` y definición del impacto sobre crédito disponible cuando consumo y tarjeta usan monedas distintas.
