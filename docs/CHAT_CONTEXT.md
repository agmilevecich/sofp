# SOFP — Contexto para continuar con ChatGPT

## Estado auditado — 15/09/2026

La fuente de verdad es el código, Git y los tests actuales; `docs/` es documentación auxiliar. Antes de proponer cambios, reconstruir siempre el estado desde GitHub.

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.
**Último commit de código validado:** `e95585e043290eebb5789f2b628b1edcef8a7344` — `test: cubrir pagos multidivisa en PagoTarjetaService`.

La rama continúa separada de `main`; no se realizó merge.

## Validación más reciente

- `mvn test`: **718/718**.
- Failures: 0.
- Errors: 0.
- Skipped: 0.
- `BUILD SUCCESS`.
- Finalizada: **15/09/2026 20:05:19 -03:00**.
- Tiempo total: 09:04 min.

## Estado consolidado

La Fase Swing está integrada. Gastos con tarjeta generan movimiento + obligación + cuotas. El pago coordinado existe en `PagoTarjetaService` y está integrado en la UI.

`CuentaService` protege la integridad estructural y los saldos respetan la moneda correspondiente. `MovimientoService` valida fondos por moneda.

Las obligaciones conservan historial de ciclo, vencimiento, gracia y cuotas. El movimiento origen está protegido frente a cambios estructurales incompatibles.

## Multidivisa actual

La integración de pago multidivisa está implementada:

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

1. Revisar `git diff`, `git diff --check` y `git status`.
2. Definir el impacto de consumos en moneda distinta sobre el límite/crédito disponible.
3. Diseñar tests de esa regla antes de modificar el cálculo.
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
