# SOFP — Contexto para continuar con ChatGPT

## Estado auditado — 16/09/2026

La fuente de verdad es el código, Git y los tests actuales; `docs/` es documentación auxiliar. Antes de proponer cambios, reconstruir siempre el estado desde GitHub.

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell` → último commit de código `48cf588`.

No se realizó merge a `main`.

## Último bloque

`ObligacionesPanel` incorpora `Cerrar ciclo`, delegando en `ObligacionService` con el ciclo persistido de la obligación. La UI refresca después del cierre.

Commits:

- `a5e6a86` — `feat: permitir cerrar ciclo desde obligaciones`.
- `48cf588` — `test: cubrir cierre de ciclo desde obligaciones`.

## Validación

- `mvn test`: **744/744**.
- Failures: 0.
- Errors: 0.
- Skipped: 0.
- `BUILD SUCCESS`.
- Finalizada: **16/09/2026 18:47:51 -03:00**.
- Tiempo: **10:32 min**.
- `ObligacionesPanelTest`: **6/6**, `BUILD SUCCESS`, finalizada **18:22:54 -03:00**.

El usuario informó working tree limpio y `git diff --check` sin observaciones. Local estaba alineado con GitHub y Bitbucket.

## Estado consolidado

La Fase Swing está integrada. Gastos con tarjeta generan movimiento + obligación + cuotas. El pago coordinado existe y está integrado en la UI.

`CuentaService` protege integridad estructural y saldos por moneda. `MovimientoService` valida fondos por moneda. Las obligaciones conservan ciclo, vencimiento, gracia, cuotas y movimiento origen.

## Multidivisa actual

- moneda original y de liquidación separadas;
- `TipoCambio` histórico explícito;
- liquidación y `saldoLiquidacion` separados del saldo original;
- pagos multidivisa coordinados mediante `PagoTarjetaService`;
- valorización de cierre histórica separada de liquidación;
- crédito utilizado basado en valorización histórica cuando existe;
- reducción proporcional después de pagos parciales;
- sin conversiones implícitas;
- falta de cotización histórica necesaria para cierre produce error y rollback;
- cierre de ciclo ya puede iniciarse desde `ObligacionesPanel`.

## Próximo trabajo

1. Diseñar el flujo completo de obtención/registro de valorización de cierre dentro de la aplicación.
2. Definir obligaciones multidivisa todavía no valorizadas al cierre.
3. Completar persistencia/UI del flujo integral de cierre y pago multidivisa.
4. Revisar consumos extranjeros sobre crédito antes de disponer de valorización.

## Estabilización futura — no implementar todavía

Antes del fast-forward a `main`:

- iniciar H2 automáticamente desde Java;
- detener H2 limpiamente al salir;
- ocultar salida técnica de consola;
- guardar detalle técnico en archivo de log;
- informar fallos de conexión con la base y otros errores de arranque mediante `JOptionPane`;
- evitar mostrar una ventana parcialmente inicializada si el arranque falla.

## Reglas temporales implementadas

- ciclo histórico de la obligación persistido al crearla;
- fechas de ciclo persistidas;
- vencimiento de sábado/domingo desplazado al lunes;
- días de gracia configurables, por defecto 0;
- mora sobre vencimiento efectivo más gracia;
- límites temporales de pago;
- pagos parciales en orden ascendente de cuotas;
- compatibilidad con obligaciones antiguas mediante campos nullable/fallback.

No están implementados calendario de feriados, fecha efectiva separada del movimiento, intereses, punitorios, CFT ni refinanciación.

## Protocolo

Antes de cada bloque: reconstruir GitHub → rama → commits → comparación con `main` → documentación → código relacionado → tests → último resultado informado.

Después: cambio mínimo → tests específicos → relacionados → suite → diff → diff-check → status → documentación.

No modificar `main`, no asumir tests locales no informados y no considerar cerrada una funcionalidad solo porque compila.
