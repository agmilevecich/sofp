# SOFP — Historial de Builds

## Estado documental — 15/09/2026

**Rama de trabajo:** `feature/swing-shell`.
**HEAD documental:** `e95585e043290eebb5789f2b628b1edcef8a7344` — `test: cubrir pagos multidivisa en PagoTarjetaService`.

## Última validación conocida

- `PagoTarjetaServiceTest`: 10/10.
- Validación relacionada informada: **19/19**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.
- Finalizada: **15/09/2026 18:37:13 -03:00**.

La última suite completa ejecutada antes de esta integración fue `mvn test`: **712/712**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizada **15/09/2026 18:05:46 -03:00**. Esta cifra todavía debe renovarse sobre el estado actual.

## Último bloque implementado

### Pago de obligaciones multidivisa

Se completó la integración del modelo de liquidación histórica con `PagoTarjetaService`.

`Obligacion` ahora conserva también `saldoLiquidacion`. Al liquidar con `TipoCambio`, el saldo de liquidación se inicializa con el importe convertido.

`PagoTarjetaService`:

- usa `saldoLiquidacion` cuando existe;
- mantiene `saldoPendiente` para obligaciones no liquidadas;
- exige que la cuenta pagadora coincida con `monedaLiquidacion`;
- aplica pagos liquidados mediante `registrarPagoLiquidacion`;
- mantiene el flujo anterior mediante `registrarPago` para obligaciones no liquidadas;
- no realiza conversiones implícitas.

Commits principales del bloque:

- `216b0d3` — `feat: guardar saldo de liquidacion de Obligacion`.
- `8185f83` — `test: cubrir saldo de liquidacion de Obligacion`.
- `a0073af` — `test: persistir saldo de liquidacion de Obligacion`.
- `ab1ca2af` — `feat: usar saldo de liquidacion en PagoTarjetaService`.
- `e95585e` — `test: cubrir pagos multidivisa en PagoTarjetaService`.

## Bloques multidivisa anteriores

- `6e2d38d` — modelo de `TipoCambio` histórico.
- `52a125e` — tests de `TipoCambio`.
- `8cd34fb` / `f76822b` / `fc4a0ff` — persistencia y corrección de escala.
- `506166b` / `bbc4dbb` — monedas original/liquidación de `Obligacion` y compatibilidad histórica.
- `17c81ba` — asociación de `TipoCambio` a `Obligacion`.
- `1ab3d10` / `c74717a` — liquidación y persistencia.

## Estado de la suite

El histórico válido sigue siendo **712/712** y no existe objetivo de recuperar artificialmente el conteo anterior de 704. Después de la integración de `PagoTarjetaService` todavía falta ejecutar la suite relacionada completa y luego `mvn test` sobre el estado actual.

## Próximo bloque

1. Tests relacionados sobre el estado actual.
2. Suite completa.
3. `git diff`, `git diff --check` y `git status`.
4. Actualizar documentación con los resultados reales.
5. Definir impacto de consumos extranjeros sobre límite/crédito disponible.

No se deben introducir conversiones implícitas y no se modificó `main`.
