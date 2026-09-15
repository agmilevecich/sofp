# SOFP — Contexto final de continuidad

## Estado auditado — 15/09/2026

La fuente de verdad es el código, Git y los tests actuales; `docs/` es documentación auxiliar.

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.
**Último commit de código validado:** `e95585e043290eebb5789f2b628b1edcef8a7344`.

No se realizó merge a `main`.

## Validación

Bloque actual: **19/19**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, 15/09/2026 18:37:13 -03:00.

Suite general previa: **712/712**, 0 failures, 0 errors, 0 skipped, 15/09/2026 18:05:46 -03:00. Debe renovarse sobre el estado actual.

## Estado actual

Liquidación histórica multidivisa implementada y conectada al pago:

- moneda original y de liquidación separadas;
- `TipoCambio` histórico asociado;
- `importeLiquidacion` y `saldoLiquidacion` separados del saldo original;
- `PagoTarjetaService` usa saldo de liquidación cuando corresponde;
- cuenta pagadora en moneda de liquidación;
- pagos parciales y totales cubiertos;
- sin conversiones implícitas.

## Próximo paso

Ejecutar suite relacionada y suite general sobre el estado actual. Después revisar diff/diff-check/status y actualizar documentación con esos resultados. Luego definir el impacto de consumos extranjeros sobre el crédito disponible.

## Protocolo

Antes de cada cambio: rama → commits → comparación con `main` → documentación → código → tests → último resultado informado. Después: cambio mínimo → tests específicos → relacionados → suite → diff → diff-check → status → documentación.
