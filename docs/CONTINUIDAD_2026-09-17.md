# SOFP — Continuidad 2026-09-17

## Estado al cierre de la sesión

**Rama de trabajo:** `feature/swing-shell`
**`main`:** `a4be85913847200cb70976d5266d9cbba10b3100`
**HEAD de trabajo antes de esta actualización documental:** `9459357bfbd5665a3f6fd42405c901c70e71b23e`

La rama de trabajo está 816 commits por delante de `main` y 0 por detrás. No se realizó merge a `main`.

## Último bloque cerrado

Se completó y validó el ciclo integral de tarjeta multidivisa:

1. consumo en USD;
2. valorización histórica al cierre;
3. pago parcial en USD;
4. reducción proporcional del crédito utilizado;
5. liquidación del saldo original restante en ARS mediante una cotización histórica distinta;
6. pago de la liquidación en ARS;
7. liberación completa del crédito disponible.

La lógica de negocio ya estaba implementada. Los últimos commits agregaron la prueba de integración y corrigieron únicamente comparaciones `BigDecimal` del test para no depender de la escala.

## Últimos commits de código/test

- `c3bbce49` — `test: cubrir ciclo completo de tarjeta multidivisa`.
- `a440051c` — `fix: comparar saldo de liquidacion sin escala en test`.
- `e6b4993c` — `fix: comparar credito sin escala en test multidivisa`.
- `9459357b` — `fix: comparar credito multidivisa sin escala`.

## Validación informada por el usuario

- `TarjetaCreditoMultidivisaIntegracionTest`: 1/1, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, 17/09/2026 17:29:45 -03:00.
- `TarjetaCreditoPagoCreditoTest` + `ObligacionServiceLiquidacionTest`: 9/9, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, 17/09/2026 17:40:50 -03:00.
- `mvn test`: **761/761**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, 17/09/2026 17:54:49 -03:00.

Además, el usuario informó:

- `git diff` limpio;
- `git diff --check` sin observaciones;
- `git status` limpio;
- rama local alineada con `bitbucket/feature/swing-shell`.

## Reglas multidivisa vigentes

- No hay conversiones implícitas.
- La valorización de cierre es histórica y está separada de la liquidación.
- `Obligacion.liquidar()` convierte únicamente el saldo original pendiente al momento de liquidar.
- Antes de liquidar, el pago multidivisa se realiza en moneda original.
- Después de liquidar, el pago se realiza sobre `saldoLiquidacion` en moneda de liquidación.
- El crédito utilizado antes de liquidar utiliza el saldo original y, cuando corresponde, la valorización de cierre proporcional.
- Después de liquidar, el crédito utilizado se basa en `saldoLiquidacion`.

## Próximo paso

Revisar `ObligacionService` antes de modificar código. Analizar específicamente:

- cierre de resumen;
- fecha de cierre y ciclo aplicable;
- obtención de cotización histórica;
- persistencia de la valorización de cierre;
- consumos extranjeros sin cotización disponible;
- relación entre cierre, liquidación y pago;
- coordinación con `ObligacionesPanel` y los repositorios involucrados.

La definición funcional debe contrastarse con normativa BCRA y documentación vigente de la entidad de referencia antes de implementar cambios.

## Nota documental

La documentación de continuidad fue actualizada después de la suite completa de 761 tests. Los documentos históricos conservan sus hitos anteriores; los documentos de estado actual reflejan la validación más reciente.
