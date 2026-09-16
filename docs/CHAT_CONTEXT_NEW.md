# SOFP — Contexto para continuar con ChatGPT

## Estado — 15/09/2026

La fuente de verdad es el código, Git y los tests actuales; `docs/` es documentación auxiliar y puede quedar desactualizada. Antes de proponer cambios, reconstruir siempre el estado desde GitHub.

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.
**Último commit de código validado:** `e95585e043290eebb5789f2b628b1edcef8a7344` — `test: cubrir pagos multidivisa en PagoTarjetaService`.

## Validación general

Suite general actual: **718/718**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizada **15/09/2026 20:05:19 -03:00**.

## Estado actual

La Fase Swing está integrada. Gastos con tarjeta generan movimiento + obligación + cuotas. `PagoTarjetaService` coordina pagos reales y la UI de obligaciones está integrada.

La multidivisa general respeta la moneda de cada saldo y movimiento.

El modelo de obligación multidivisa conserva moneda original, moneda de liquidación y `TipoCambio` histórico. `saldoLiquidacion` permite aplicar pagos en la moneda de liquidación sin alterar el saldo original.

`PagoTarjetaService` usa el saldo liquidado cuando existe y exige que la cuenta pagadora esté en la moneda de liquidación. No realiza conversiones implícitas.

## Próximo bloque

1. Revisar `git diff`, `git diff --check` y `git status`.
2. Definir el impacto de consumos extranjeros sobre crédito disponible.
3. Diseñar tests de esa regla antes de modificar el cálculo.
4. Completar persistencia/UI del pago multidivisa.

## Regla de continuidad

Antes de cada cambio: revisar implementación, clases relacionadas, repositorios, tests y reglas de negocio. Luego cambio mínimo → tests específicos → relacionados → suite → diff → diff-check → status → documentación.

No modificar `main`, no asumir resultados locales no informados y no considerar terminado un bloque solo porque compila.
