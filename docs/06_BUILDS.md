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


## Actualización de continuidad — cierre 17/09/2026 22:51 -03:00

Esta sección supersede cualquier validación anterior de este documento cuando haya contradicción.

- Rama de trabajo: `feature/swing-shell`.
- HEAD actual: `b4a9bc6b4f64ce191f90eb9e4dd4291356e0574c` — `test: corregir expectativas de valorizacion multidivisa`.
- `main`: `a4be85913847200cb70976d5266d9cbba10b3100`.
- Comparación GitHub: `feature/swing-shell` está 847 commits por delante de `main` y 0 por detrás.
- No se realizó merge a `main`.
- Último bloque: corrección de expectativas de tests para reflejar que la valorización de cierre de obligaciones financiadas se almacena en la cuota; no se modificó producción en este último commit.
- Validación específica posterior: 8/8 tests verdes, 0 failures, 0 errors, `BUILD SUCCESS`, informada por el usuario.
- Validación final: `mvn test` con **769/769 tests**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizada **17/09/2026 22:51:16 -03:00**, informada por el usuario.
- Validación Git local final: `git diff` vacío, `git diff --check` sin observaciones y `git status` limpio; rama local al día con `bitbucket/feature/swing-shell`, informado por el usuario.

### Punto exacto para retomar

El cálculo de crédito multidivisa, la valorización histórica de cierre, la liquidación explícita, los pagos antes/después de liquidar y el cierre iniciado desde `ObligacionesPanel` están cubiertos por tests. El siguiente bloque debe comenzar con una revisión de `ObligacionService` y de sus clases relacionadas para definir el flujo de cierre de resumen de tarjeta siguiendo reglas bancarias reales. Antes de modificar código se debe contrastar la cotización de cierre de consumos extranjeros con normativa BCRA y documentación vigente de la entidad financiera de referencia. No inventar una regla de negocio por inferencia.

### Regla de continuidad para la próxima sesión

Reconstruir desde GitHub antes de cualquier cambio: rama → últimos commits → comparación con `main` → código relacionado → tests → documentación → último resultado informado → próximo cambio mínimo. No asumir que la documentación histórica representa el estado actual si contradice código o tests.