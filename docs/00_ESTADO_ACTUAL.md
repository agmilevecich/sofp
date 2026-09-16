# SOFP — Estado actual

> Documento de continuidad. La fuente de verdad técnica es el código, los tests y los commits actuales; `docs/` es documentación auxiliar.

## Estado auditado — 16/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.
**Último commit de código:** `50052cbba18feb0256a0d9688833` — `fix: calcular crédito multidivisa pendiente`.

La rama está 752 commits adelante de `main` y 0 atrás. No se realizó merge.

## Último bloque implementado

### Valorización de cierre e impacto en crédito para obligaciones multidivisa

Se agregó a `Obligacion` una valorización histórica de cierre separada de la liquidación/pago.

- `Obligacion` conserva `monedaOriginal` y `monedaLiquidacion`.
- `TipoCambio` representa la cotización histórica utilizada para la valorización.
- `importeValorizacionCierre` y `tipoCambioCierre` se conservan como datos históricos.
- `valorarCierre(TipoCambio)` valida moneda origen/destino, rechaza segunda valorización y no modifica la deuda original.
- La valorización de cierre no cambia `importeOriginal`, `saldoPendiente`, `importeLiquidacion`, `saldoLiquidacion` ni el estado.
- La liquidación explícita mediante `liquidar(TipoCambio)` continúa separada de la valorización.
- `ObligacionRepository.sumarCreditoUtilizadoPorCuenta(...)` usa `saldoPendiente` para obligaciones en moneda de la tarjeta y una valorización de cierre proporcional al saldo original pendiente para obligaciones multidivisa valorizadas.
- Los consumos sin obligación asociada siguen contemplándose según el comportamiento existente.
- No se realizan conversiones implícitas.

Ejemplo validado: USD 30 con tarjeta ARS y cambio USD→ARS 1500 genera ARS 45.000 de valorización. Con límite ARS 50.000, ARS 5.000 adicionales se aceptan y ARS 5.001 se rechazan.

Para una obligación USD 100 valorizada a ARS 1500, un pago parcial de USD 40 deja crédito utilizado proporcional de ARS 90.000; el pago total deja crédito utilizado en ARS 0.

## Validación más reciente

- `mvn test`: **740/740**.
- Failures: 0.
- Errors: 0.
- Skipped: 0.
- `BUILD SUCCESS`.
- Finalizado: **16/09/2026 15:54:16 -03:00**.
- Tiempo total: **09:50 min**.

Validaciones específicas recientes:

- `TipoCambioRepositoryTest`: 6/6.
- `ObligacionServiceCierreTest`: 4/4.
- `ObligacionRepositoryTest`: 7/7.
- `MovimientoCreditoMultimonedaTest`: 1/1.
- `PagoTarjetaServiceTest`: 10/10.
- `ObligacionLiquidacionTest`: 13/13.

Además se verificó localmente `git diff` vacío, `git diff --check` sin observaciones y `git status` limpio.

## Estado multidivisa

Resuelto:

- saldos y fondos por moneda;
- moneda original y moneda de liquidación;
- `TipoCambio` histórico explícito y persistente;
- liquidación trazable y `saldoLiquidacion`;
- pagos parciales y totales sobre saldo de liquidación;
- valorización histórica de cierre separada de la liquidación;
- utilización de la valorización para crédito disponible;
- ajuste proporcional del crédito utilizado después de pagos parciales sobre obligaciones valorizadas.

Pendiente:

- definir el comportamiento de una obligación multidivisa todavía no valorizada al cierre;
- definir el flujo de obtención/registro de la valorización de cierre dentro de la aplicación;
- completar cobertura/persistencia/UI del flujo integral de cierre y pago multidivisa.

## Pendientes reales

### P0/P1 — Multidivisa de tarjetas

1. Definir el momento y flujo de valorización de cierre dentro de la aplicación.
2. Definir el comportamiento de consumos extranjeros antes de disponer de una valorización de cierre.
3. Completar cobertura de persistencia/UI del cierre y pago multidivisa.

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
