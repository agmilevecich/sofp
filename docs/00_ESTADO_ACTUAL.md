# SOFP — Estado actual

> Documento de continuidad. La fuente de verdad técnica es el código, los tests y los commits actuales; `docs/` es documentación auxiliar.

## Estado auditado — 17/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell` → `2a789c10e5399afc799d9f2cc5477e74ef799413`.

No se realizó merge a `main`.

## Último bloque implementado

### Crédito de tarjeta y liquidación multidivisa

Se corrigió el cálculo del crédito utilizado después de liquidar una obligación multidivisa. Una obligación ya liquidada utiliza `saldoLiquidacion`; una obligación todavía no liquidada utiliza su saldo pendiente y, cuando corresponde, la valorización histórica de cierre proporcional.

El filtro del cálculo contempla tanto `saldoPendiente` como `saldoLiquidacion`, evitando que una obligación cuyo saldo original queda trasladado a liquidación siga consumiendo crédito después de pagar completamente la liquidación.

Se agregó cobertura específica para el caso: pago parcial en moneda original → liquidación del saldo restante → pago completo en moneda de liquidación → liberación total del crédito.

Commits recientes:

- `c592cbc` — `fix: calcular credito sobre saldo de liquidacion`.
- `20bb282` — `test: cubrir credito liberado tras liquidacion multidivisa`.
- `2a789c1` — `test: persistir tipo de cambio de liquidacion`.

## Validación más reciente informada por el usuario

- `CuentaServiceCreditoTest`: **3/3**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.
- `TarjetaCreditoPagoCreditoTest`: **5/5**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.
- `mvn test`: **756/756**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.
- Finalizada: **17/09/2026 15:50:11 -03:00**.

El usuario informó además `git diff` limpio, `git diff --check` sin observaciones, `git status` limpio y rama local alineada con `bitbucket/feature/swing-shell`.

## Estado consolidado

La Fase Swing está integrada. Gastos con tarjeta generan movimiento + obligación + cuotas. El pago coordinado existe y está integrado en la UI.

`CuentaService` protege integridad estructural y saldos por moneda. `MovimientoService` valida fondos por moneda. Las obligaciones conservan ciclo, vencimiento, gracia, cuotas y movimiento origen.

El modelo multidivisa conserva moneda original, moneda de liquidación, cotizaciones históricas, valorización histórica de cierre, liquidación explícita y `saldoLiquidacion`.

## Multidivisa actual

Resuelto y validado:

- saldos y fondos por moneda;
- moneda original y moneda de liquidación;
- `TipoCambio` histórico explícito y persistente;
- liquidación trazable y `saldoLiquidacion`;
- pagos parciales y totales sobre saldo de liquidación;
- valorización histórica de cierre separada de la liquidación;
- utilización de la valorización para crédito disponible;
- ajuste proporcional del crédito después de pagos parciales;
- liberación del crédito después de cancelar la liquidación;
- cierre de ciclo iniciado desde `ObligacionesPanel`;
- pago multidivisa en moneda original antes de liquidar;
- pago posterior en moneda de liquidación después de liquidar.

Reglas vigentes:

- no hay conversiones implícitas;
- `Obligacion.liquidar()` convierte únicamente el saldo original pendiente al momento de liquidar;
- la cotización utilizada para liquidación es histórica, explícita y trazable;
- la valorización de cierre es independiente de la liquidación;
- antes de liquidar, el pago multidivisa se aplica en moneda original;
- después de liquidar, el pago se aplica sobre `saldoLiquidacion` en la moneda de liquidación;
- el crédito utilizado se calcula sobre la deuda actualmente exigible: saldo pendiente antes de liquidación o saldo de liquidación después de ella.

## Próximo bloque

El próximo paso debe definirse a partir del comportamiento bancario que se quiere reproducir para el cierre de resumen de tarjeta. Antes de modificar código, revisar `ObligacionService`, `ObligacionesPanel`, el modelo de ciclos y las reglas de cotización, contrastándolos con la normativa BCRA y la documentación vigente de la entidad tomada como referencia.

En particular, todavía debe definirse y luego implementar el flujo completo de obtención/registro de la valorización de cierre, qué ocurre con consumos extranjeros sin cotización disponible al cierre y cómo se representa en UI la secuencia cierre → liquidación → pago.

## P2 — Robustez

1. Política de eliminación de cuentas con historial financiero.
2. Abstracción `Clock` para determinismo temporal.
3. Migraciones/versionado formal de esquema para una futura etapa no local.

## P3 — Evolución

1. Financiación avanzada.
2. UI específica de tarjetas.
3. Pasivos, patrimonio y análisis.
4. Gestión de entidades financieras.
5. Pulido de consola.

## Estabilización futura — antes del fast-forward a main

No implementar todavía. Para la versión estable se deberá:

- iniciar H2 automáticamente desde Java;
- detener H2 limpiamente al salir;
- ocultar la salida técnica de consola;
- conservar detalle técnico en archivo de log;
- informar fallos de conexión con la base y otros errores de arranque mediante `JOptionPane`;
- evitar mostrar una ventana parcialmente inicializada si el arranque falla.

## Fuera de alcance actual

Calendario de feriados, fecha efectiva separada del movimiento e intereses/punitorios/CFT/refinanciación requieren decisiones de negocio antes de implementarse.

## Protocolo de continuidad

Ante una nueva sesión: rama → últimos commits → comparación con `main` → documentación → código relacionado → tests → último resultado informado → próximo paso.

No modificar `main` automáticamente. No asumir resultados locales no informados. Antes de considerar cerrado un bloque: tests específicos → relacionados → suite → diff → diff-check → status → documentación.
