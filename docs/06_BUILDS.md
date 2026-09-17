# SOFP — Historial de Builds

## Estado documental — 17/09/2026

**Rama de trabajo:** `feature/swing-shell`.
**Último commit de código/test:** `9459357b` — `fix: comparar credito multidivisa sin escala`.
**Último commit documental:** `b8b54da` — `docs: actualizar estado de continuidad`.

## Validación más reciente

- `mvn test`: **761/761**.
- Failures: 0.
- Errors: 0.
- Skipped: 0.
- `BUILD SUCCESS`.
- Finalizado: **17/09/2026 17:54:49 -03:00**.

## Validaciones específicas posteriores al bloque de crédito

- `TarjetaCreditoMultidivisaIntegracionTest`: **1/1**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizada **17/09/2026 17:29:45 -03:00**.
- `TarjetaCreditoPagoCreditoTest`: **5/5**.
- `ObligacionServiceLiquidacionTest`: **4/4**.
- Ejecución relacionada: **9/9**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizada **17/09/2026 17:40:50 -03:00**.

## Último bloque implementado

### Ciclo integral de tarjeta multidivisa

La integración quedó cubierta mediante el flujo: consumo en USD, valorización al cierre, pago parcial en USD, liquidación del saldo restante en ARS con una cotización posterior y pago completo de la liquidación. La finalización de la liquidación libera el crédito utilizado.

`ObligacionRepository` calcula el crédito utilizado con `saldoLiquidacion` después de liquidar y con `saldoPendiente`/valorización proporcional antes de liquidar.

Los commits de código/test más recientes fueron:

- `c3bbce49` — `test: cubrir ciclo completo de tarjeta multidivisa`.
- `a440051c` — `fix: comparar saldo de liquidacion sin escala en test`.
- `e6b4993c` — `fix: comparar credito sin escala en test multidivisa`.
- `9459357b` — `fix: comparar credito multidivisa sin escala`.

Los tres últimos commits correctivos ajustan comparaciones `BigDecimal` del test y no modifican la lógica de negocio.

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
- integración completa del ciclo parcial → liquidación → pago.

No se realizan conversiones implícitas.

## Próximo bloque

1. Revisar `ObligacionService` y el flujo de cierre de resumen.
2. Contrastar con normativa BCRA y documentación vigente de la entidad de referencia cómo se obtiene y aplica la cotización de cierre para consumos extranjeros.
3. Definir obligaciones multidivisa todavía no valorizadas al cierre.
4. Completar, si corresponde, persistencia/UI del flujo de cierre y pago multidivisa.
5. Revisar consumos extranjeros sobre crédito antes de disponer de valorización.

## Estabilización futura

Antes del fast-forward a `main`, y no como parte del bloque actual: arranque automático de H2 desde Java, cierre limpio de H2, consola silenciosa, logging técnico a archivo y errores de arranque/conexión informados mediante `JOptionPane`.

No se modificó `main`.
