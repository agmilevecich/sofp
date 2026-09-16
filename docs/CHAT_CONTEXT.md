# SOFP — Contexto para continuar con ChatGPT

## Estado auditado — 16/09/2026

La fuente de verdad es el código, Git y los tests actuales; `docs/` es documentación auxiliar. Antes de proponer cambios, reconstruir siempre el estado desde GitHub.

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.
**Último commit de código antes de la actualización documental:** `d61609df65b23c49a81851dc5742c73261a6d924` — `fix: importar Movimiento en test de credito`.

Las actualizaciones documentales de esta etapa se realizan en commits separados posteriores al último commit de código. No se modificó `main`.

## Validación más reciente

- `mvn test`: **723/723**.
- Failures: 0.
- Errors: 0.
- Skipped: 0.
- `BUILD SUCCESS`.
- Finalizada: **16/09/2026 13:11:00 -03:00**.
- Tiempo total: **11:02 min**.

Validaciones específicas recientes:

- `MovimientoCreditoMultimonedaTest`: 1/1.
- `MovimientoServiceTest,MovimientoMultimonedaTest,MovimientoServiceSaldoTest`: 62/62.
- `PagoTarjetaServiceTest,SaldoTarjetaCreditoTest,TarjetaCreditoPagoCreditoTest`: 17/17.
- `MovimientoObligacionIntegridadTest,ObligacionServiceTest`: 14/14.
- `ObligacionJpaTest`: 3/3.
- `ObligacionLiquidacionTest`: 13/13.

## Estado consolidado

La Fase Swing está integrada. Gastos con tarjeta generan movimiento + obligación + cuotas. El pago coordinado existe en `PagoTarjetaService` y está integrado en la UI.

`CuentaService` protege la integridad estructural y los saldos respetan la moneda correspondiente. `MovimientoService` valida fondos por moneda.

Las obligaciones conservan historial de ciclo, vencimiento, gracia y cuotas. El movimiento origen está protegido frente a cambios estructurales incompatibles.

## Multidivisa actual

La integración multidivisa implementada comprende:

- `Obligacion` separa `monedaOriginal` y `monedaLiquidacion`;
- `TipoCambio` representa una cotización histórica;
- la obligación conserva el tipo de cambio de liquidación;
- `saldoLiquidacion` se inicializa al liquidar;
- `PagoTarjetaService` usa `saldoLiquidacion` cuando existe;
- los pagos liquidados se aplican mediante `registrarPagoLiquidacion`;
- la cuenta pagadora debe estar en `monedaLiquidacion`;
- las obligaciones no liquidadas mantienen el flujo basado en `saldoPendiente`;
- `Obligacion` puede conservar `tipoCambioCierre` e `importeValorizacionCierre`;
- la valorización de cierre no modifica la deuda original ni constituye por sí misma una liquidación;
- el crédito utilizado puede usar la valorización de cierre de obligaciones multidivisa ya valorizadas;
- no existen conversiones implícitas.

## Pendiente inmediato

1. Definir el flujo de obtención y registro de la valorización de cierre dentro de la aplicación.
2. Definir el comportamiento de una obligación multidivisa todavía no valorizada al cierre.
3. Revisar el crédito utilizado después de pagos parciales sobre obligaciones valorizadas.
4. Diseñar tests de esas reglas antes de modificar el cálculo.
5. Completar cobertura de persistencia/UI del flujo integral de cierre y pago multidivisa.

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
