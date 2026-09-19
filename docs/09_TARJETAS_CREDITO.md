# SOFP — Diseño y adaptación de tarjetas de crédito

## Estado auditado — 17/09/2026

**Rama:** `feature/swing-shell`

La fuente de verdad es el código y los tests actuales.

## 1. Modelo

Una tarjeta de crédito es una `Cuenta` con `TipoCuenta.TARJETA_CREDITO`. La deuda se representa mediante `Obligacion` y el consumo mediante `Movimiento`.

## 2. Moneda y liquidación histórica — IMPLEMENTADO

La moneda del consumo se conserva en `Movimiento` y como `monedaOriginal` de la obligación. La moneda de la tarjeta/cuenta queda como `monedaLiquidacion`.

`TipoCambio` representa una cotización histórica. `Obligacion.liquidar(TipoCambio)` valida las monedas, calcula `importeLiquidacion`, conserva el tipo de cambio y evita una segunda liquidación. `saldoLiquidacion` representa la deuda en moneda de liquidación y se reduce mediante pagos.

No se realiza conversión automática ni se recalcula una liquidación histórica con una cotización posterior.

## 3. Valorización de cierre — IMPLEMENTADO

`Obligacion` conserva `tipoCambioCierre` e `importeValorizacionCierre` como datos históricos independientes de la liquidación.

`Obligacion.valorarCierre(TipoCambio)` exige cambio no nulo, rechaza segunda valoración y monedas iguales, valida origen/destino y no modifica la deuda original, la liquidación ni el estado.

La valorización de cierre no reemplaza a `liquidar(TipoCambio)`.

## 4. Saldos y fondos — IMPLEMENTADO

La cuenta calcula su saldo usando movimientos de su moneda. `MovimientoService` valida fondos usando la moneda económica del movimiento. ARS y USD no se mezclan implícitamente.

## 5. Pago multidivisa — IMPLEMENTADO EN SERVICIO

`PagoTarjetaService` utiliza `saldoLiquidacion` cuando existe y, en obligaciones no liquidadas, `saldoPendiente`. La cuenta pagadora debe utilizar la moneda exigible en cada etapa. Se cubren pagos parciales y totales.

## 6. Crédito disponible — IMPLEMENTADO

El cálculo de crédito utilizado contempla la etapa de la obligación:

- antes de liquidar, obligación en moneda de la tarjeta → `saldoPendiente`;
- antes de liquidar, obligación multidivisa valorizada → `importeValorizacionCierre` proporcional al saldo original pendiente;
- después de liquidar → `saldoLiquidacion`;
- consumo de tarjeta sin obligación asociada → comportamiento existente.

Ejemplo validado: USD 100 valorizados a ARS 1500 representan ARS 150.000 de crédito. Un pago parcial de USD 40 reduce el crédito utilizado a ARS 90.000. Si los USD 60 restantes se liquidan a ARS 1600 y luego se pagan ARS 96.000, el crédito vuelve al límite disponible completo.

Una obligación multidivisa sin valorización de cierre no recibe una conversión implícita. Su comportamiento futuro debe definirse.

## 7. Ciclo de facturación — IMPLEMENTADO Y CON HISTORIAL PERSISTENTE

`CicloFacturacion` es un objeto de dominio no persistente. `Cuenta.calcularCicloFacturacion(LocalDate)` resuelve cierre, ciclo siguiente, meses cortos, vencimiento y cambio de año. El vencimiento efectivo se desplaza al lunes cuando cae sábado o domingo.

## 8. Pagos y reglas temporales — IMPLEMENTADOS

`PagoTarjetaService` coordina autorización, validaciones, egreso real y aplicación del pago en una operación transaccional. Se admiten pagos parciales o totales y se aplican las reglas temporales vigentes.

## 9. Integridad histórica — IMPLEMENTADA

El movimiento que origina una obligación queda protegido frente a cambios estructurales incompatibles y eliminación.

## 10. Cuotas y financiación

Las cuotas simples sin interés están implementadas y se generan automáticamente. La financiación avanzada sigue pendiente: intereses, CFT, cuotas variables, adelantos, refinanciación, anulaciones/reversiones y ajustes.

## 11. Integridad estructural de Cuenta — IMPLEMENTADA

Una cuenta con movimientos no puede cambiar de tipo ni de moneda. La API genérica tampoco permite transiciones hacia o desde `TARJETA_CREDITO`.

## 12. UI específica — PENDIENTE

Pendiente una UI completa para consultar límite/disponible, consumos, valorizaciones de cierre, ciclos, vencimientos, deuda y pagos reales de forma específica para tarjetas. La integración actual de pagos continúa disponible mediante la UI existente.

## 13. Compatibilidad histórica

Los nuevos campos se mantienen nullable cuando corresponde y utilizan fallback para datos existentes.

## 14. Validación actual

- `mvn test`: **761/761**.
- Failures: 0.
- Errors: 0.
- Skipped: 0.
- `BUILD SUCCESS`.
- Finalizada: **17/09/2026 17:54:49 -03:00**.

Validaciones específicas del bloque:

- `TarjetaCreditoMultidivisaIntegracionTest`: **1/1**, `BUILD SUCCESS`, 17/09/2026 17:29:45 -03:00.
- Ejecución relacionada `TarjetaCreditoPagoCreditoTest` + `ObligacionServiceLiquidacionTest`: **9/9**, `BUILD SUCCESS`, 17/09/2026 17:40:50 -03:00.

## 15. Orden de trabajo pendiente

1. Revisar `ObligacionService` y el flujo de cierre de resumen.
2. Definir el comportamiento bancario de la cotización de consumos extranjeros al cierre, contrastando normativa BCRA y documentación vigente de la entidad de referencia.
3. Definir obligaciones multidivisa todavía no valorizadas al cierre.
4. Completar persistencia/UI del cierre y pago multidivisa.
5. Financiación avanzada.
6. UI específica de tarjetas.
7. Pasivos/patrimonio y análisis.
8. Gestión de entidades financieras.
9. Pulido de consola.

El calendario de feriados y una fecha efectiva separada requieren decisión de negocio antes de implementarse.


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
## Auditoría BCRA — cancelación de consumos en moneda extranjera — 18/09/2026

Se contrastó el flujo con la Comunicación A 8307 del BCRA y su material vigente sobre consumos en moneda extranjera. Para tarjetas emitidas por entidades financieras, el consumo puede cancelarse en moneda extranjera o en pesos; si se cancela en pesos, el tipo de cambio vendedor aplicable tiene como máximo el del momento de cancelación, o el día hábil inmediato anterior cuando el pago se efectúa en día inhábil. Para débito automático en cuenta de la propia entidad aplica el tipo de cambio vendedor por medios electrónicos del cierre del mismo día hábil del pago. Fuente normativa: Comunicación A 8307 del BCRA.

En SOFP se mantiene separada la valorización de cierre de la liquidación efectiva. La liquidación ya no puede tomar silenciosamente una cotización de un día hábil anterior cuando la cancelación ocurre en un día hábil sin cotización disponible. En sábado/domingo se toma la última cotización del viernes anterior. Los feriados no se infieren mediante calendario de días de semana: requieren un calendario bancario explícito antes de automatizar esa selección.

También se agregó validación para impedir liquidaciones fechadas antes del consumo o en el futuro.

La suite de pruebas de esta modificación todavía debe ejecutarse localmente; no se registra aquí ningún resultado no informado.


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

### Financiación — modelo base implementado

Se agregó `Financiacion` como entidad persistente vinculada a `Obligacion`. El modelo actual representa capital financiado, fecha de inicio y saldo de capital, con pago de capital y estados derivados pendiente/cancelada.

También quedó implementada y validada la persistencia de la relación con la obligación y su cobertura de tests.

`FinanciacionTest`: 9/9, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, informado el 18/09/2026 20:32:49 -03:00.

### Financiación de resumen — todavía pendiente

Todavía NO existe la coordinación automática del flujo:

**pago parcial del resumen → capital impago → Financiacion → siguiente ciclo.**

El próximo trabajo será revisar `PagoTarjetaService`, `Obligacion` y sus tests para implementar solamente esa primera conexión. Intereses, TNA, punitorios, CFT y refinanciación quedan para una etapa posterior y no deben introducirse todavía.

La suite completa más reciente informada: 779/779, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, 18/09/2026 15:04:44 -03:00. La prueba específica de financiación posterior fue 9/9; no se registra una nueva suite completa posterior.

### Punto exacto para mañana

1. Reconstruir estado desde GitHub.
2. Revisar `PagoTarjetaService` y tests de pagos parciales.
3. Determinar cómo queda el saldo de la obligación después del pago.
4. Definir el punto mínimo para crear la financiación.
5. Tests: pago parcial crea financiación; pago total no crea financiación; saldo financiado correcto; persistencia si corresponde.
6. Implementar y ejecutar tests específicos antes de la suite general.


## ACTUALIZACIÓN DE CONTINUIDAD — 19/09/2026

### Financiación — conexión con vencimiento implementada

El modelo base de Financiacion ahora tiene su primera conexión funcional con el ciclo de tarjeta:

**pago parcial → llega el vencimiento → se determina saldo impago del ciclo → se crea Financiacion → la obligación vuelve a aparecer en el ciclo siguiente.**

La financiación no se crea en el momento del pago parcial. Cuando existen cuotas, solamente se toma el saldo de la cuota vencida; las cuotas futuras quedan fuera.

También se agregó idempotencia para no crear dos financiaciones del mismo ciclo y cobertura de persistencia.

Los nuevos tests todavía no fueron ejecutados localmente; el siguiente paso de validación es ObligacionServiceCierreTest, luego PagoTarjetaServiceTest y finalmente la suite completa.

Pendiente inmediato: pagos posteriores al vencimiento sobre la financiación. Después se abordarán intereses/TNA/punitorios/CFT según las decisiones de negocio ya registradas.


## ACTUALIZACIÓN DE CONTINUIDAD — 19/09/2026 11:00 -03:00

### Financiación — pago posterior conectado

Ya está conectado el pago posterior al vencimiento con Financiacion:

- se identifica la financiación pendiente por fecha;
- el pago se aplica primero al capital financiado;
- la deuda original y la financiación se mantienen sincronizadas;
- un excedente puede aplicarse a la siguiente cuota si ambas deudas están en la misma moneda;
- no se mezclan automáticamente la moneda original de una financiación y una liquidación posterior en otra moneda.

Tests agregados para pago sobre financiación y excedente sobre cuota siguiente.

La validación local todavía está pendiente.


## ACTUALIZACIÓN DE CONTINUIDAD — 19/09/2026 11:44 -03:00

### Financiación — validación completa cerrada

Quedó validado el bloque de pagos posteriores al vencimiento sobre financiación.

Resultados informados por el usuario:

- `PagoTarjetaServiceTest`: **15/15**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizado **19/09/2026 11:13:29 -03:00**.
- `ObligacionServiceCierreTest`: **15/15**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizado **19/09/2026 11:26:06 -03:00**.
- Suite completa `mvn test`: **797/797**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizada **19/09/2026 11:44:00 -03:00**.

La suite completa confirma además el comportamiento de regresión del resto del sistema.

### Validación Git local

Después de la suite:

- `git diff`: limpio.
- `git diff --check`: sin observaciones.
- `git status`: working tree limpio.
- rama local: `feature/swing-shell`, al día con `bitbucket/feature/swing-shell`.

### Estado funcional

El bloque queda cerrado en cuanto a la coordinación básica de financiación:

**pago parcial → vencimiento → creación de Financiacion → pago posterior → cancelación de financiación → excedente sobre cuota siguiente cuando corresponde.**

No se implementan todavía intereses, TNA, punitorios, CFT ni refinanciación.

También queda explícito que no existe conversión implícita entre la moneda original de una financiación y una liquidación posterior en otra moneda. Ese caso requiere una decisión de negocio antes de modificar el comportamiento.

### Próximo paso

Si se continúa con Tarjeta de Crédito, el próximo trabajo debe comenzar reconstruyendo nuevamente el estado desde GitHub y revisando las reglas pendientes, especialmente el comportamiento multidivisa de una obligación financiada y posteriormente liquidada. No introducir conversiones por inferencia.
