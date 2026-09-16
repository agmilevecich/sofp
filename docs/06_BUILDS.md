# SOFP — Historial de Builds

## Estado documental — 15/09/2026

**Rama de trabajo:** `feature/swing-shell`.
**Último commit de código:** `e95585e043290eebb5789f2b628b1edcef8a7344` — `test: cubrir pagos multidivisa en PagoTarjetaService`.

## Última validación conocida

- `mvn test`: **718/718**.
- Failures: 0.
- Errors: 0.
- Skipped: 0.
- `BUILD SUCCESS`.
- Finalizada: **15/09/2026 20:05:19 -03:00**.
- Tiempo total: **09:04 min**.

## Último bloque implementado

### Pago de obligaciones multidivisa

Se completó la integración del modelo de liquidación histórica con `PagoTarjetaService`.

`Obligacion` conserva `saldoLiquidacion`, que se inicializa con el importe convertido al liquidar con `TipoCambio`.

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

## Estado de la suite

La suite completa actual es **718/718**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`. Esta ejecución incluye la integración actual de `PagoTarjetaService`.

No existe objetivo de recuperar artificialmente el conteo anterior de 704.

## Próximo bloque

1. Revisar `git diff`.
2. Revisar `git diff --check`.
3. Revisar `git status`.
4. Definir impacto de consumos extranjeros sobre límite/crédito disponible.
5. Diseñar tests antes de modificar ese cálculo.

No se deben introducir conversiones implícitas y no se modificó `main`.
