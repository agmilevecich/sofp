# SOFP — Contexto para continuar con ChatGPT

## Estado auditado — 15/09/2026

La fuente de verdad es el código, Git y los tests actuales; `docs/` es documentación auxiliar. Antes de proponer cambios, reconstruir siempre el estado desde GitHub.

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.
**Último commit de código validado:** `e95585e043290eebb5789f2b628b1edcef8a7344` — `test: cubrir pagos multidivisa en PagoTarjetaService`.

La rama continúa separada de `main`; no se realizó merge.

## Validación más reciente

- `PagoTarjetaServiceTest`: **10/10**.
- Validación relacionada informada: **19/19**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.
- Finalizada: **15/09/2026 18:37:13 -03:00**.
- Última suite completa previa: **712/712**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, 18:05:46.

## Estado consolidado

La Fase Swing está integrada. Gastos con tarjeta generan movimiento + obligación + cuotas. El pago coordinado existe en `PagoTarjetaService` y está integrado en la UI.

`CuentaService` protege la integridad estructural y los saldos respetan la moneda correspondiente. `MovimientoService` valida fondos por moneda.

Las obligaciones conservan historial de ciclo, vencimiento, gracia y cuotas. El movimiento origen está protegido frente a cambios estructurales incompatibles.

## Multidivisa actual

La integración de pago multidivisa ya está implementada:

- `Obligacion` separa `monedaOriginal` y `monedaLiquidacion`;
- `TipoCambio` representa una cotización histórica;
- la obligación conserva el tipo de cambio utilizado;
- `saldoLiquidacion` se inicializa al liquidar;
- `PagoTarjetaService` usa `saldoLiquidacion` cuando existe;
- los pagos liquidados se aplican mediante `registrarPagoLiquidacion`;
- la cuenta pagadora debe estar en `monedaLiquidacion`;
- las obligaciones no liquidadas mantienen el flujo basado en `saldoPendiente`;
- no existen conversiones implícitas.

## Pendiente inmediato

1. Ejecutar suite relacionada completa y `mvn test` sobre el estado actual.
2. Revisar `git diff`, `git diff --check` y `git status`.
3. Definir el impacto de consumos en moneda distinta sobre el límite/crédito disponible.
4. Completar cobertura de persistencia/UI del pago multidivisa.

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

## Protocolo

Antes de cada bloque: reconstruir desde GitHub rama → últimos commits → comparación con `main` → documentación → implementación → clases relacionadas → tests → último resultado informado.

Luego: cambio mínimo → tests específicos → relacionados → suite → diff → diff-check → status → documentación.

No modificar `main`, no asumir tests locales no informados y no considerar cerrada una funcionalidad solo porque compila.
