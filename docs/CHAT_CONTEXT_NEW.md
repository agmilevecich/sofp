# SOFP — Contexto para continuar con ChatGPT

## Estado — 15/09/2026

La fuente de verdad es el código, Git y los tests actuales; `docs/` es documentación auxiliar y puede quedar desactualizada. Antes de proponer cambios, reconstruir siempre el estado desde GitHub.

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.
**Último commit de código validado:** `e95585e043290eebb5789f2b628b1edcef8a7344` — `test: cubrir pagos multidivisa en PagoTarjetaService`.

## Validación general

Bloque actual: **19/19**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizado **15/09/2026 18:37:13 -03:00**.

Suite general previa a la integración: **712/712**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, 18:05:46. No asumir que ese resultado incluye los cambios posteriores.

## Estado actual

La Fase Swing está integrada. Gastos con tarjeta generan movimiento + obligación + cuotas. `PagoTarjetaService` coordina pagos reales y la UI de obligaciones está integrada.

La multidivisa general respeta la moneda de cada saldo y movimiento.

El modelo de obligación multidivisa conserva moneda original, moneda de liquidación y `TipoCambio` histórico. `saldoLiquidacion` permite aplicar pagos en la moneda de liquidación sin alterar el saldo original.

`PagoTarjetaService` usa el saldo liquidado cuando existe y exige que la cuenta pagadora esté en la moneda de liquidación. No realiza conversiones implícitas.

## Próximo bloque

Ejecutar suite relacionada y suite general sobre el estado actual; luego revisar diff/diff-check/status. Después definir el impacto de consumos extranjeros sobre crédito disponible y completar persistencia/UI del pago multidivisa.

## Regla de continuidad

Antes de cada cambio: revisar implementación, clases relacionadas, repositorios, tests y reglas de negocio. Luego cambio mínimo → tests específicos → relacionados → suite → diff → diff-check → status → documentación.

No modificar `main`, no asumir resultados locales no informados y no considerar terminado un bloque solo porque compila.
