# SOFP — Estado actual

> Documento de continuidad. La fuente de verdad técnica es el código, los tests y los commits actuales; `docs/` es documentación auxiliar.

## Estado auditado — 15/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.
**HEAD:** `e95585e043290eebb5789f2b628b1edcef8a7344` — `test: cubrir pagos multidivisa en PagoTarjetaService`.

La rama de trabajo continúa separada de `main`. No se realizó merge.

## Último bloque implementado

### Integración multidivisa en `PagoTarjetaService`

La liquidación histórica ya no queda solamente en el dominio: el servicio de pago la utiliza cuando la obligación tiene `saldoLiquidacion`.

- `Obligacion` conserva moneda original y moneda de liquidación.
- `TipoCambio` representa la cotización histórica utilizada.
- `importeLiquidacion` y `saldoLiquidacion` quedan separados del importe/saldo original.
- `PagoTarjetaService` toma como saldo a pagar `saldoLiquidacion` cuando existe; en obligaciones no liquidadas conserva el flujo anterior mediante `saldoPendiente`.
- En obligaciones liquidadas, el pago se aplica mediante `registrarPagoLiquidacion`.
- La cuenta pagadora debe estar en la moneda de liquidación.
- No se realiza conversión implícita ni se modifica la cotización histórica.

Ejemplo validado: consumo USD 100, tarjeta ARS, tipo de cambio histórico USD→ARS 1500; saldo de liquidación ARS 150.000. Un pago de ARS 50.000 deja ARS 100.000 de liquidación y conserva USD 100 como saldo original.

## Validación más reciente del cambio

- `PagoTarjetaServiceTest`: **10/10**.
- Comando relacionado informado: **19/19**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.
- Finalizado: **15/09/2026 18:37:13 -03:00**.

La última suite completa informada **antes de esta integración** fue `mvn test`: **712/712**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizada **15/09/2026 18:05:46 -03:00**. Esa suite no incluye todavía la modificación de `PagoTarjetaService`.

## Estado multidivisa

Resuelto:

- saldos de cuenta por moneda;
- fondos disponibles por moneda;
- consumo con moneda económica propia;
- moneda original y moneda de liquidación en `Obligacion`;
- `TipoCambio` histórico explícito y persistente;
- liquidación trazable;
- saldo de liquidación;
- pagos parciales y totales sobre saldo de liquidación desde `PagoTarjetaService`.

Pendiente:

- definir y cubrir el impacto de consumos en moneda distinta sobre límite/crédito disponible;
- validar la integración completa con persistencia/UI;
- ejecutar nuevamente la suite general después de esta integración.

## Integridad y tarjetas

La integridad estructural de `Cuenta` está protegida. El movimiento origen de una obligación queda protegido frente a cambios estructurales incompatibles y eliminación. Ciclos, vencimientos de fin de semana, gracia, mora, cuotas, autorización y pagos coordinados están implementados.

## Pendientes reales

### P0/P1 — Multidivisa de tarjetas

1. Definir impacto de consumos extranjeros sobre límite/crédito disponible.
2. Cubrir persistencia/UI del pago multidivisa.
3. Ejecutar suite relacionada y suite completa sobre el estado actual.

### P2 — Robustez

1. Política de eliminación de cuentas con historial financiero.
2. Abstracción `Clock` para determinismo temporal.
3. Migraciones/versionado formal de esquema para una futura etapa no local.

### P3 — Evolución

1. Financiación avanzada.
2. UI específica de tarjetas.
3. Pasivos, patrimonio y análisis.
4. Gestión de entidades financieras.
5. Pulido de consola.

### Fuera de alcance actual

Calendario de feriados, fecha efectiva separada del movimiento e intereses/punitorios/CFT/refinanciación requieren decisiones de negocio antes de implementarse.

## Protocolo de continuidad

Ante una nueva sesión: rama → últimos commits → comparación con `main` → documentación → código relacionado → tests → último resultado informado → próximo paso.

No modificar `main` automáticamente. No asumir resultados locales no informados. Antes de considerar cerrado un bloque: tests específicos → relacionados → suite → diff → diff-check → status → documentación.
