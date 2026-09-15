# SOFP — Contexto para continuar con ChatGPT

## Estado — 15/09/2026

La fuente de verdad es el código, Git y los tests actuales; `docs/` es documentación auxiliar y puede quedar desactualizada. Antes de proponer cambios, reconstruir siempre el estado desde GitHub.

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.
**HEAD:** `c74717ab0e97398764a7ef6b4c9cb55cb93f20c5`.

## Validación general

`mvn test`: **712/712**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.
Finalizada: **15/09/2026 18:05:46 -03:00**.

## Estado actual

La Fase Swing está integrada. Gastos con tarjeta generan movimiento + obligación + cuotas. `PagoTarjetaService` coordina pagos reales y la UI de obligaciones está integrada.

La multidivisa general respeta la moneda de cada saldo y movimiento.

El modelo de obligación multidivisa ya conserva moneda original, moneda de liquidación y `TipoCambio` histórico. La liquidación es explícita, trazable y persistente.

## Próximo bloque

Integrar liquidación multidivisa en `PagoTarjetaService`, preservando pagos de moneda coincidente y definiendo el impacto de consumos extranjeros sobre crédito disponible.

## Regla de continuidad

Antes de cada cambio: revisar implementación, clases relacionadas, repositorios, tests y reglas de negocio. Luego cambio mínimo → tests específicos → relacionados → suite → diff → diff-check → status → documentación.

No modificar `main`, no asumir resultados locales no informados y no considerar terminado un bloque solo porque compila.
