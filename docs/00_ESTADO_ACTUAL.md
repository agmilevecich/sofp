# SOFP — Estado actual

> Documento de continuidad. La fuente de verdad técnica es el código, los tests y los commits actuales; `docs/` es documentación auxiliar.

## Estado auditado — 17/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell` → `9459357bfbd5665a3f6fd42405c901c70e71b23e`.

La rama de trabajo está 816 commits por delante de `main` y 0 por detrás. No se realizó merge a `main`.

## Último bloque implementado

### Crédito de tarjeta y ciclo multidivisa

Se corrigió el cálculo del crédito utilizado después de liquidar una obligación multidivisa. Una obligación ya liquidada utiliza `saldoLiquidacion`; una obligación todavía no liquidada utiliza su saldo pendiente y, cuando corresponde, la valorización histórica de cierre proporcional.

Se agregó cobertura del flujo completo: consumo en USD → valorización al cierre → pago parcial en USD → liquidación del saldo restante a ARS con otra cotización → pago completo de la liquidación → liberación total del crédito.

Los últimos commits del bloque fueron:

- `c3bbce49` — `test: cubrir ciclo completo de tarjeta multidivisa`.
- `a440051c` — `fix: comparar saldo de liquidacion sin escala en test`.
- `e6b4993c` — `fix: comparar credito sin escala en test multidivisa`.
- `9459357b` — `fix: comparar credito multidivisa sin escala`.

Los dos últimos cambios son exclusivamente ajustes de aserciones `BigDecimal` en el test de integración; no modifican reglas de negocio.

## Validación más reciente informada por el usuario

### Suite específica multidivisa

- `TarjetaCreditoMultidivisaIntegracionTest`: **1/1**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizada **17/09/2026 17:29:45 -03:00**.

### Suite relacionada

- `TarjetaCreditoPagoCreditoTest`: **5/5**.
- `ObligacionServiceLiquidacionTest`: **4/4**.
- Total ejecución relacionada: **9/9**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizada **17/09/2026 17:40:50 -03:00**.

### Suite completa

- `mvn test`: **761/761**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.
- Finalizada **17/09/2026 17:54:49 -03:00**.
- El usuario informó además `git diff` limpio, `git diff --check` sin observaciones, `git status` limpio y rama local alineada con `bitbucket/feature/swing-shell`.

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

El próximo paso es revisar `ObligacionService` y el flujo de cierre de resumen de tarjeta antes de modificar código.

La revisión debe abarcar: fecha de cierre, selección y persistencia de la cotización histórica, valorización de consumos extranjeros, obligaciones sin cotización disponible al cierre y relación entre cierre, liquidación y pago. La regla de negocio debe mantenerse separada entre valorización de cierre y liquidación real, y debe contrastarse con normativa BCRA y documentación vigente de la entidad tomada como referencia.

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

## ACTUALIZACIÓN DE CONTINUIDAD — 18/09/2026 11:15 -03:00

Esta sección supersede cualquier estado, validación o próximo paso anterior de este documento cuando exista contradicción. La fuente de verdad sigue siendo el código, los tests y GitHub.

### Estado Git

- Rama de trabajo: `feature/swing-shell`.
- `main`: `a4be85913847200cb70976d5266d9cbba10b3100`.
- HEAD de trabajo antes de esta actualización documental: `4a027006e05cfabc1d203dbb84e3712e855aa19d` — `test: corregir fecha histórica de liquidación en sábado`.
- Comparación actual con GitHub: `feature/swing-shell` está **870 commits por delante de main y 0 por detrás**.
- No se realizó merge a `main`.

### Auditoría de tarjetas de crédito cerrada

La auditoría del flujo de tarjetas se completó respetando la separación entre consumo, valorización de cierre, liquidación y pago.

Se corrigió la selección histórica de cotizaciones para liquidaciones:

- en día hábil se utiliza una cotización del mismo día y hasta el instante de liquidación;
- sábado y domingo utilizan la última cotización disponible del viernes anterior;
- un día hábil sin cotización aplicable no reutiliza silenciosamente una cotización de días anteriores;
- no se infieren feriados: para ello será necesario un calendario bancario explícito;
- no se permiten liquidaciones anteriores al consumo ni fechas de liquidación futuras.

La valorización de cierre continúa siendo independiente de la liquidación efectiva. El consumo conserva su moneda original y la obligación conserva además la moneda de liquidación.

### Tests informados por el usuario

1. Bloque específico de cotización y liquidación:
   - `TipoCambioRepositoryTest` + `ObligacionServiceLiquidacionTest`: **16/16**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.
   - Finalizado: **18/09/2026 10:58:33 -03:00**.

2. Bloque relacionado de tarjetas:
   - `ObligacionServiceCuotasTest`, `PagoTarjetaServiceTest`, `TarjetaCreditoMultidivisaIntegracionTest`, `MovimientoCreditoMultimonedaTest`, `ObligacionesPanelTest`: **26/26**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.
   - Finalizado: **18/09/2026 11:01:22 -03:00**.

3. Suite completa:
   - `mvn test`: **775/775**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.
   - Tiempo: **12:42 min**.
   - Finalizado: **18/09/2026 11:15:25 -03:00**.

4. Validación Git local informada por el usuario después de la suite:
   - `git diff`: limpio.
   - `git diff --check`: sin observaciones.
   - `git status`: working tree limpio.
   - rama local: `feature/swing-shell`, al día con `bitbucket/feature/swing-shell`.

### Punto exacto para retomar

La auditoría de tarjetas está cerrada y validada. No hay un fallo pendiente en el comportamiento auditado. El próximo trabajo funcional, si se continúa con tarjetas, debe partir de una revisión del flujo de cierre de resumen y de cualquier regla de negocio todavía no implementada, manteniendo la normativa BCRA como referencia y sin inventar reglas por inferencia.

Pendientes conocidos: calendario bancario/feriados, fecha efectiva separada del movimiento, financiación avanzada, UI específica de tarjetas, política de eliminación de cuentas con historial, `Clock`, migraciones formales y estabilización de arranque H2 antes del futuro fast-forward a `main`.

### Regla de continuidad

En la próxima sesión reconstruir nuevamente desde GitHub: rama → últimos commits → comparación con `main` → código relacionado → tests → documentación → último resultado informado → próximo cambio mínimo. No asumir que una documentación histórica representa el estado actual si contradice código o tests.
