# SOFP — Estado actual

> Documento de continuidad. La fuente de verdad técnica es el código, los tests y los commits actuales; `docs/` es documentación auxiliar.

## Estado auditado — 15/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.
**HEAD:** `c74717ab0e97398764a7ef6b4c9cb55cb93f20c5` — `test: persistir liquidacion historica de Obligacion`.

La rama de trabajo está 677 commits adelante de `main` y 0 atrás. No se realizó merge a `main`.

## Último bloque funcional cerrado

### Liquidación multidivisa histórica de `Obligacion`

La primera parte de la solución multidivisa de tarjetas quedó implementada de forma explícita y trazable.

- `Movimiento` conserva la moneda económica del consumo.
- `Obligacion` conserva `monedaOriginal` y `monedaLiquidacion`.
- `importeLiquidacion` es independiente del importe original y permanece nulo hasta liquidar.
- `TipoCambio` representa una cotización histórica con moneda origen, moneda destino, cotización, fecha/hora y fuente.
- `Obligacion` conserva el `TipoCambio` utilizado para liquidar.
- `Obligacion.liquidar(TipoCambio)` valida las monedas, impide una segunda liquidación y calcula el importe en la moneda de liquidación mediante la cotización histórica.
- No hay conversión implícita ni recálculo histórico con cotizaciones posteriores.

Ejemplo validado: USD 100 de consumo con liquidación ARS a cotización histórica 1500 → ARS 150.000,00.

### Validaciones del bloque

- `TipoCambioTest`: 10/10.
- `TipoCambioJpaTest`: 1/1.
- `ObligacionTest`: 12/12.
- `ObligacionJpaTest`: 3/3.
- `ObligacionLiquidacionTest`: 5/5.
- `ObligacionTipoCambioJpaTest`: 1/1.
- Bloque relacionado: 27/27.
- Suite general `mvn test`: **712/712**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.
- Finalizada: **15/09/2026 18:05:46 -03:00**.

## Multidivisa — estado actual

Ya están resueltos los saldos y fondos por moneda. No se mezclan ARS y USD en esos cálculos.

También está resuelta la representación de una obligación cuyo consumo y liquidación utilizan monedas distintas, incluyendo la cotización histórica asociada.

Queda pendiente integrar esta liquidación explícita en `PagoTarjetaService` y definir el impacto de consumos en moneda distinta sobre el crédito disponible de la tarjeta.

## Integridad y tarjetas

La integridad estructural de `Cuenta` está protegida: una cuenta con movimientos no puede cambiar de tipo ni moneda y la API genérica no permite transiciones hacia o desde `TARJETA_CREDITO`.

El movimiento origen de una obligación queda protegido frente a cambios estructurales incompatibles y eliminación. Las obligaciones, cuotas, pagos coordinados, autorización, ciclos históricos, vencimientos de fin de semana, días de gracia y mora están implementados.

## Pendientes reales

### P0/P1 — Multidivisa de tarjetas

1. Revisar `PagoTarjetaService` y su API actual.
2. Integrar liquidación explícita con `TipoCambio` histórico sin romper pagos en moneda coincidente.
3. Definir y cubrir el impacto de consumos extranjeros sobre el límite/crédito disponible.
4. Cubrir con tests específicos, relacionados y de persistencia.

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
