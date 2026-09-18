# SOFP — Contexto para continuar con ChatGPT

## Estado auditado — 17/09/2026

La fuente de verdad es el código, Git y los tests actuales; `docs/` es documentación auxiliar. Antes de proponer cambios, reconstruir siempre el estado desde GitHub.

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell` → último commit `2a789c10e5399afc799d9f2cc5477e74ef799413`.

No se realizó merge a `main`.

## Último bloque

Se corrigió el cálculo del crédito utilizado después de liquidar una obligación multidivisa. Una obligación liquidada consume crédito según `saldoLiquidacion`; antes de liquidar se utiliza `saldoPendiente` y, para moneda distinta de la tarjeta, la valorización histórica de cierre proporcional cuando existe.

El cambio evita que el saldo original trasladado a liquidación siga consumiendo crédito una vez cancelada la deuda de liquidación.

Commits:

- `c592cbc` — `fix: calcular credito sobre saldo de liquidacion`.
- `20bb282` — `test: cubrir credito liberado tras liquidacion multidivisa`.
- `2a789c1` — `test: persistir tipo de cambio de liquidacion`.

## Validación

- `CuentaServiceCreditoTest`: **3/3**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizada **17/09/2026 15:33:38 -03:00`.
- `TarjetaCreditoPagoCreditoTest`: **5/5**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizada **17/09/2026 15:36:06 -03:00`.
- `mvn test`: **756/756**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizada **17/09/2026 15:50:11 -03:00`.

El usuario informó working tree limpio, `git diff` sin cambios, `git diff --check` sin observaciones y rama local alineada con `bitbucket/feature/swing-shell`.

## Estado consolidado

La Fase Swing está integrada. Gastos con tarjeta generan movimiento + obligación + cuotas. El pago coordinado existe y está integrado en la UI.

`CuentaService` protege integridad estructural y saldos por moneda. `MovimientoService` valida fondos por moneda. Las obligaciones conservan ciclo, vencimiento, gracia, cuotas y movimiento origen.

`Obligacion` conserva moneda original, moneda de liquidación, cotización de cierre, cotización de liquidación y saldo de liquidación. La valorización de cierre y la liquidación son conceptos independientes.

## Multidivisa actual

- moneda original y de liquidación separadas;
- `TipoCambio` histórico explícito y persistente;
- liquidación y `saldoLiquidacion` separados del saldo original;
- pagos multidivisa coordinados mediante `PagoTarjetaService`;
- valorización de cierre histórica separada de liquidación;
- crédito utilizado basado en valorización histórica cuando corresponde;
- reducción proporcional después de pagos parciales;
- crédito basado en `saldoLiquidacion` después de liquidar;
- liberación completa del crédito después de cancelar la liquidación;
- sin conversiones implícitas;
- cierre de ciclo iniciado desde `ObligacionesPanel`.

## Próximo trabajo

1. Revisar `ObligacionService` y reconstruir el flujo de cierre de resumen.
2. Contrastar el comportamiento buscado con normativa BCRA y documentación vigente de la entidad financiera de referencia.
3. Definir cómo se obtiene y registra la cotización histórica de cierre.
4. Definir el comportamiento de consumos extranjeros todavía no valorizados al cierre.
5. Completar persistencia/UI del flujo integral de cierre, liquidación y pago multidivisa.

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


## Actualización de continuidad — cierre 17/09/2026 22:51 -03:00

Esta sección supersede cualquier validación anterior de este documento cuando haya contradicción.

- Rama de trabajo: `feature/swing-shell`.
- HEAD actual: `b4a9bc6b4f64ce191f90eb9e4dd4291356e0574c` — `test: corregir expectativas de valorizacion multidivisa`.
- `main`: `a4be85913847200cb70976d5266d9cbba10b3100`.
- Comparación GitHub: `feature/swing-shell` está 847 commits por delante de `main` y 0 por detrás.
- No se realizó merge a `main`.
- Último bloque: corrección de expectativas de tests para reflejar que la valorización de cierre de obligaciones financiadas se almacena en la cuota; no se modificó producción en este último commit.
- Validación específica posterior: 8/8 tests verdes, 0 failures, 0 errors, `BUILD SUCCESS`, informada por el usuario.
- Validación final: `mvn test` con **769/769 tests**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizada **17/09/2026 22:51:16 -03:00**, informada por el usuario.
- Validación Git local final: `git diff` vacío, `git diff --check` sin observaciones y `git status` limpio; rama local al día con `bitbucket/feature/swing-shell`, informado por el usuario.

### Punto exacto para retomar

El cálculo de crédito multidivisa, la valorización histórica de cierre, la liquidación explícita, los pagos antes/después de liquidar y el cierre iniciado desde `ObligacionesPanel` están cubiertos por tests. El siguiente bloque debe comenzar con una revisión de `ObligacionService` y de sus clases relacionadas para definir el flujo de cierre de resumen de tarjeta siguiendo reglas bancarias reales. Antes de modificar código se debe contrastar la cotización de cierre de consumos extranjeros con normativa BCRA y documentación vigente de la entidad financiera de referencia. No inventar una regla de negocio por inferencia.

### Regla de continuidad para la próxima sesión

Reconstruir desde GitHub antes de cualquier cambio: rama → últimos commits → comparación con `main` → código relacionado → tests → documentación → último resultado informado → próximo cambio mínimo. No asumir que la documentación histórica representa el estado actual si contradice código o tests.