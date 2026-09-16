# SOFP — Estado actual

> Documento de continuidad. La fuente de verdad técnica es el código, los tests y los commits actuales; `docs/` es documentación auxiliar.

## Estado auditado — 16/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.
**Último commit de código:** `d61609df65b23c49a81851dc5742c73261a6d924` — `fix: importar Movimiento en test de credito`.

La rama de trabajo continúa separada de `main`. No se realizó merge.

## Último bloque implementado

### Valorización de cierre e impacto en crédito para obligaciones multidivisa

Se agregó a `Obligacion` una valorización histórica de cierre separada de la liquidación/pago.

- `Obligacion` conserva `monedaOriginal` y `monedaLiquidacion`.
- `TipoCambio` representa la cotización histórica utilizada para la valorización.
- `importeValorizacionCierre` y `tipoCambioCierre` se conservan como datos históricos.
- `valorarCierre(TipoCambio)` valida moneda origen/destino, rechaza segunda valorización y no modifica la deuda original.
- La valorización de cierre no cambia `importeOriginal`, `saldoPendiente`, `importeLiquidacion`, `saldoLiquidacion` ni el estado de la obligación.
- La liquidación explícita mediante `liquidar(TipoCambio)` continúa existiendo y no fue reinterpretada como valorización.
- `ObligacionRepository.sumarCreditoUtilizadoPorCuenta(...)` usa el saldo pendiente para obligaciones en la moneda de la tarjeta y la valorización de cierre para obligaciones multidivisa ya valorizadas.
- Los consumos de tarjeta sin obligación asociada siguen contemplándose en el crédito utilizado.
- `MovimientoService.calcularCreditoUtilizado(...)` utiliza el nuevo cálculo del repositorio.
- No se realizan conversiones implícitas al registrar un consumo extranjero; la valorización para crédito se realiza con una cotización de cierre explícita.

Ejemplo validado: consumo USD 30 con tarjeta ARS y tipo de cambio de cierre USD→ARS 1500 genera una valorización histórica de ARS 45.000. Con límite ARS 50.000, un nuevo consumo ARS 5.000 es aceptado y ARS 5.001 es rechazado.

### Pago multidivisa previamente implementado

La liquidación histórica continúa conectada al pago cuando la obligación tiene `saldoLiquidacion`.

- `PagoTarjetaService` toma `saldoLiquidacion` cuando existe; en obligaciones no liquidadas usa `saldoPendiente`.
- En obligaciones liquidadas, el pago se aplica mediante `registrarPagoLiquidacion`.
- La cuenta pagadora debe estar en la moneda de liquidación.
- No se modifica la cotización histórica durante el pago.

## Validación más reciente

- `mvn test`: **723/723**.
- Failures: 0.
- Errors: 0.
- Skipped: 0.
- `BUILD SUCCESS`.
- Finalizado: **16/09/2026 13:11:00 -03:00**.
- Tiempo total: 11:02 min.

Validaciones específicas recientes:

- `MovimientoCreditoMultimonedaTest`: **1/1**.
- `MovimientoServiceTest,MovimientoMultimonedaTest,MovimientoServiceSaldoTest`: **62/62**.
- `PagoTarjetaServiceTest,SaldoTarjetaCreditoTest,TarjetaCreditoPagoCreditoTest`: **17/17**.
- `MovimientoObligacionIntegridadTest,ObligacionServiceTest`: **14/14**.
- `ObligacionJpaTest`: **3/3**.
- `ObligacionLiquidacionTest`: **13/13**.

Además, antes de cerrar la etapa se verificó localmente:

- `git diff`: sin salida.
- `git diff --check`: sin observaciones.
- `git status`: working tree limpio y rama sincronizada con `bitbucket/feature/swing-shell`.

## Estado multidivisa

Resuelto:

- saldos de cuenta por moneda;
- fondos disponibles por moneda;
- consumo con moneda económica propia;
- moneda original y moneda de liquidación en `Obligacion`;
- `TipoCambio` histórico explícito y persistente;
- liquidación trazable;
- saldo de liquidación;
- pagos parciales y totales sobre saldo de liquidación;
- valorización histórica de cierre separada de la liquidación;
- utilización de la valorización de cierre para el crédito disponible de consumos multidivisa ya valorizados.

Pendiente:

- definir el comportamiento del crédito para una obligación multidivisa todavía no valorizada al cierre;
- definir cómo debe ajustarse el crédito cuando una obligación valorizada recibe pagos parciales, para evitar sobreestimaciones si el modelo futuro lo requiere;
- completar cobertura de persistencia/UI del pago y cierre multidivisa.

## Pendientes reales

### P0/P1 — Multidivisa de tarjetas

1. Definir el momento y flujo de valorización de cierre dentro de la aplicación.
2. Definir el comportamiento de consumos extranjeros antes de disponer de una valorización de cierre.
3. Revisar el impacto de pagos parciales sobre el crédito utilizado de obligaciones valorizadas.
4. Completar cobertura de persistencia/UI del cierre y pago multidivisa.

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
