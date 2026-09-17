# SOFP — Historial de Builds

## Estado documental — 17/09/2026

**Rama de trabajo:** `feature/swing-shell`.
**Último commit:** `2a789c1` — `test: persistir tipo de cambio de liquidacion`.

## Validación más reciente

- `mvn test`: **756/756**.
- Failures: 0.
- Errors: 0.
- Skipped: 0.
- `BUILD SUCCESS`.
- Finalizado: **17/09/2026 15:50:11 -03:00**.

Validaciones específicas posteriores al bloque de crédito:

- `CuentaServiceCreditoTest`: **3/3**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizada **17/09/2026 15:33:38 -03:00**.
- `TarjetaCreditoPagoCreditoTest`: **5/5**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizada **17/09/2026 15:36:06 -03:00**.

## Último bloque implementado

### Crédito de tarjeta después de liquidación multidivisa

`ObligacionRepository` ahora calcula el crédito utilizado usando `saldoLiquidacion` cuando la obligación ya fue liquidada. Antes de la liquidación mantiene el cálculo sobre `saldoPendiente`, usando la valorización histórica de cierre proporcional cuando corresponde.

También se ajustó el filtro para considerar obligaciones con saldo pendiente o saldo de liquidación positivo. Esto permite liberar el crédito cuando se paga completamente la deuda de liquidación, aunque `saldoPendiente` conserve el importe original trasladado.

Cobertura agregada para el flujo completo de pago parcial en moneda original, liquidación del saldo restante y pago total posterior en moneda de liquidación.

Commits del bloque:

- `c592cbc` — `fix: calcular credito sobre saldo de liquidacion`.
- `20bb282` — `test: cubrir credito liberado tras liquidacion multidivisa`.
- `2a789c1` — `test: persistir tipo de cambio de liquidacion`.

## Bloques multidivisa cerrados

- `TipoCambio` histórico.
- moneda original y moneda de liquidación.
- liquidación explícita y trazable.
- `saldoLiquidacion` y pagos parciales/totales.
- valorización histórica de cierre separada de liquidación.
- crédito disponible basado en valorización histórica.
- reducción proporcional del crédito después de pagos parciales.
- liberación del crédito después de pagar completamente la liquidación.
- cierre de ciclo iniciado desde UI.
- pagos en moneda original antes de liquidación y en moneda de liquidación después de liquidación.

No se realizan conversiones implícitas.

## Próximo bloque

1. Revisar `ObligacionService` y el flujo de cierre de resumen.
2. Definir el comportamiento bancario de la valorización de consumos extranjeros al cierre, contrastando normativa BCRA y documentación vigente de la entidad de referencia.
3. Definir el flujo completo de obtención/registro de la cotización histórica de cierre.
4. Completar persistencia/UI de cierre, liquidación y pago multidivisa.
5. Revisar el comportamiento de consumos extranjeros sobre crédito antes de disponer de valorización.

## Estabilización futura

Antes del fast-forward a `main`, y no como parte del bloque actual: arranque automático de H2 desde Java, cierre limpio de H2, consola silenciosa, logging técnico a archivo y errores de arranque/conexión informados mediante `JOptionPane`.

No se modificó `main`.
