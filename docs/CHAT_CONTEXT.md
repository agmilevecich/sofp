# SOFP — Contexto para continuar con ChatGPT

## Estado auditado — 15/09/2026

La fuente de verdad es el código, Git y los tests actuales; `docs/` es documentación auxiliar. Antes de proponer cambios, reconstruir siempre el estado desde GitHub.

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.
**HEAD:** `c74717ab0e97398764a7ef6b4c9cb55cb93f20c5`.

No se realizó merge a `main`.

## Validación más reciente

- `mvn test`: **712/712**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.
- Finalizada: **15/09/2026 18:05:46 -03:00**.
- Bloque de obligaciones/liquidación relacionado: **27/27**.

## Estado consolidado

La Fase Swing está integrada. Gastos con tarjeta generan movimiento + obligación + cuotas. El pago coordinado existe en `PagoTarjetaService` y está integrado en la UI.

`CuentaService` protege la integridad estructural y los saldos respetan la moneda correspondiente. `MovimientoService` valida fondos por moneda.

Las obligaciones conservan historial de ciclo, vencimiento, gracia y cuotas. El movimiento origen está protegido frente a cambios estructurales incompatibles.

## Multidivisa actual

La primera etapa de liquidación histórica está cerrada:

- `Obligacion` separa `monedaOriginal` y `monedaLiquidacion`.
- `TipoCambio` representa una cotización histórica con origen, destino, cotización, fecha/hora y fuente.
- `Obligacion` conserva el `TipoCambio` utilizado.
- `Obligacion.liquidar(TipoCambio)` realiza una conversión explícita, valida monedas y evita doble liquidación.
- `importeLiquidacion` queda separado del importe original.
- No existen conversiones implícitas.

## Pendiente inmediato

Integrar esta liquidación histórica en `PagoTarjetaService` sin romper el flujo actual de pagos en moneda coincidente.

También debe definirse el impacto de un consumo en moneda distinta sobre el límite/crédito disponible de la tarjeta.

## Reglas temporales implementadas

- ciclo histórico de la obligación persistido al crearla;
- fechas de ciclo persistidas en cuotas;
- vencimiento de sábado/domingo desplazado al lunes;
- días de gracia configurables, por defecto 0;
- mora evaluada sobre vencimiento efectivo más gracia;
- pago anterior al consumo rechazado;
- pago futuro rechazado;
- pagos parciales en orden ascendente de cuotas;
- compatibilidad con obligaciones antiguas mediante campos nullable/fallback.

No están implementados calendario de feriados, fecha efectiva separada del movimiento, intereses, punitorios, CFT ni refinanciación.

## Robustez reciente

`Moneda.cantidadDecimales` no admite valores negativos y mantiene el rechazo de `null` mediante `NullPointerException`.

## Protocolo

Antes de cada bloque: reconstruir desde GitHub rama → últimos commits → comparación con `main` → documentación → implementación → clases relacionadas → tests → último resultado informado.

Luego: cambio mínimo → tests específicos → relacionados → suite → diff → diff-check → status → documentación.

No modificar `main`, no asumir tests locales no informados y no considerar cerrada una funcionalidad solo porque compila.
