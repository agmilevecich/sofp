# SOFP — Pendientes

## Estado auditado — 17/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell` → `9459357bfbd5665a3f6fd42405c901c70e71b23e`.

La rama de trabajo está 816 commits por delante de `main` y 0 por detrás. No se realizó merge a `main`.

Última validación informada: **761/761**, 0 fallos, 0 errores, 0 omitidos, `BUILD SUCCESS`, finalizada **17/09/2026 17:54:49 -03:00**.

## Bloques cerrados

- Shell Swing y navegación.
- Cuentas, categorías, ingresos, gastos, movimientos e inversiones.
- Obligaciones y pagos de tarjeta desde UI.
- Autorización de pagos por usuario.
- Integridad estructural e histórica.
- Ciclos, cuotas, vencimientos, gracia y mora.
- Aislamiento JPA/H2 para tests.
- Saldos y fondos separados por moneda.
- `Moneda.cantidadDecimales` no negativa.
- `TipoCambio` histórico.
- Registro de cotizaciones históricas desde la aplicación.
- Persistencia transaccional del registro de cotizaciones.
- Formulario Swing para registrar cotización histórica.
- Moneda original y moneda de liquidación de `Obligacion`.
- Liquidación histórica explícita y trazable.
- `saldoLiquidacion` y pagos parciales/totales.
- Valorización histórica de cierre separada de la liquidación.
- Obtención de la cotización histórica necesaria al cerrar el ciclo.
- Uso de la valorización para crédito disponible.
- Corrección proporcional del crédito después de pagos parciales.
- Cierre de ciclo iniciado desde `ObligacionesPanel`.
- Pago multidivisa en moneda original antes de la liquidación.
- Rechazo de pago en moneda de liquidación antes de liquidar una obligación multidivisa.
- Liquidación multidivisa sobre el saldo original restante después de pagos parciales.
- Flujo integral de pago parcial en moneda original → saldo original restante → liquidación → pago posterior en moneda de liquidación.
- Semántica de `estado` después de una liquidación parcial: `saldoPendiente` puede conservar el saldo original ya trasladado a `saldoLiquidacion`; el pago de la liquidación lleva el estado a `PAGADA` sin descontar nuevamente ese saldo original.
- Cálculo de crédito utilizado sobre `saldoLiquidacion` después de liquidar.
- Liberación completa del crédito después de cancelar la deuda de liquidación.
- Validación de regresión completa con 761 tests.

## Decisiones multidivisa vigentes

- `Obligacion.liquidar()` convierte únicamente el `saldoPendiente` original que permanece pendiente al momento de liquidar.
- No se introducen conversiones implícitas.
- La cotización utilizada para la liquidación debe ser histórica, explícita y trazable.
- Antes de la liquidación, una obligación multidivisa se paga en su moneda original.
- Después de la liquidación, el saldo a pagar queda expresado en la moneda de liquidación mediante `saldoLiquidacion`.
- El crédito utilizado antes de liquidar usa el saldo original y la valorización de cierre proporcional cuando corresponde.
- El crédito utilizado después de liquidar usa `saldoLiquidacion`, evitando que el saldo original trasladado siga consumiendo crédito una vez cancelada la liquidación.
- `estado` representa la deuda que permanece exigible; por eso el pago de la liquidación puede llevar el estado a `PAGADA` aunque `saldoPendiente` conserve el importe original trasladado.

## Próximo paso lógico

Revisar `ObligacionService` y definir el comportamiento de cierre de resumen de tarjeta siguiendo el flujo de una entidad financiera: fecha de cierre, obtención y persistencia de la cotización histórica, valorización de consumos extranjeros, tratamiento de obligaciones sin cotización disponible y relación entre cierre, liquidación y pago.

La regla de negocio debe contrastarse con normativa BCRA y documentación vigente de la entidad tomada como referencia antes de modificar el modelo.

## P2 — Robustez

- Política de eliminación de cuentas con historial financiero.
- Abstracción `Clock`.
- Migraciones/versionado formal de esquema para una futura etapa no local.

## P3 — Evolución

- Financiación avanzada.
- UI específica de tarjetas.
- Pasivos, patrimonio y análisis.
- Gestión de entidades financieras.
- Pulido de consola.

## Estabilización futura — previa al fast-forward a main

Separada del desarrollo funcional actual:

- iniciar H2 automáticamente desde Java al arrancar SOFP;
- detener H2 limpiamente al cerrar;
- ocultar la salida técnica de consola;
- conservar detalle técnico mediante logging a archivo;
- informar fallos de conexión con la base y otros errores de arranque mediante `JOptionPane`;
- evitar mostrar una ventana parcialmente inicializada si el arranque falla.

## Fuera de alcance actual

Calendario de feriados, fecha efectiva separada del movimiento e intereses/punitorios/CFT/refinanciación requieren decisiones de negocio antes de implementarse.

## Regla de cierre

Tests específicos → relacionados → suite general → `git diff` → `git diff --check` → `git status` → documentación.


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


## ACTUALIZACIÓN DE CONTINUIDAD — 18/09/2026 20:35 -03:00

Esta sección supersede cualquier estado anterior cuando exista contradicción.

### Financiación de tarjeta — bloque iniciado

Implementado y validado el modelo persistente de capital financiado:

- entidad `Financiacion`;
- relación `Obligacion -> financiaciones`;
- fecha de inicio, capital original y saldo de capital;
- estados pendiente/cancelada;
- pago sobre capital sin superar el saldo;
- persistencia y tests de aislamiento JPA;
- `FinanciacionTest`: 9/9, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, informado por el usuario el 18/09/2026 20:32:49 -03:00.

Commits del bloque: `8252cff`, `7994754`, `f001f19`, `a76a949`, `7c0c4aa`, `1a6a071`, `8a4cd0a`, `0da28cc`.

### Próximo cambio

Conectar el pago parcial de tarjeta con la creación de una `Financiacion` por el capital impago. Antes de modificar `PagoTarjetaService` se debe revisar su flujo actual y los tests existentes. No implementar todavía intereses, TNA, punitorios ni CFT.

La suite completa más reciente informada sigue siendo 779/779, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, 18/09/2026 15:04:44 -03:00. No se registra una suite completa posterior como ejecutada.


## ACTUALIZACIÓN DE CONTINUIDAD — 19/09/2026

### Financiación de resumen — primer paso implementado

Se corrigió el diseño para que la financiación no nazca al registrar un pago parcial. La financiación se crea al procesar un ciclo ya vencido, por el saldo que quedó impago en ese ciclo.

Implementado:

- cálculo del saldo pendiente del ciclo;
- financiación del primer ciclo vencido con saldo;
- financiación únicamente de la cuota vencida cuando existen cuotas;
- no financiación antes del vencimiento;
- no financiación cuando el ciclo quedó totalmente pagado;
- persistencia;
- idempotencia;
- inclusión de la obligación financiada en el ciclo siguiente.

Pendiente inmediato: hacer que PagoTarjetaService aplique correctamente los pagos posteriores al vencimiento al saldo de Financiacion, sin confundirlos con cuotas futuras.

No implementar todavía intereses, TNA, punitorios, CFT ni refinanciación.


## ACTUALIZACIÓN DE CONTINUIDAD — 19/09/2026 11:00 -03:00

### Financiación — pagos posteriores implementados

PagoTarjetaService ya reconoce financiaciones pendientes y aplica los pagos posteriores al vencimiento sobre ellas. El pago reduce simultáneamente la financiación y la deuda subyacente; un excedente puede continuar sobre la siguiente cuota cuando la obligación todavía no fue liquidada.

Pendiente inmediato: completar y validar el comportamiento multidivisa cuando una obligación financiada también tiene liquidación en otra moneda. No asumir conversiones implícitas.

Siguen fuera de este bloque intereses, TNA, punitorios, CFT y refinanciación.
