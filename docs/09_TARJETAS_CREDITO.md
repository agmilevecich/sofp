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


## ACTUALIZACIÓN DE CONTINUIDAD — 19/09/2026 11:44 -03:00

Esta actualización supersede cualquier validación anterior cuando exista contradicción. La fuente de verdad sigue siendo el código, los tests y GitHub.

### Estado actual confirmado

- Rama de trabajo: `feature/swing-shell`.
- Rama estable: `main`.
- No se realizó merge a `main`.
- HEAD previo al cierre documental: `ba2027168bcd172517990cd996aefaad5294da76` — `test: corregir saldo total de obligacion`.
- Comparación con `main`: 927 commits por delante, 0 por detrás.
- Suite completa: **797/797**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.
- Suite finalizada: **19/09/2026 11:44:00 -03:00**, 16:28 min.
- `PagoTarjetaServiceTest`: **15/15**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.
- `ObligacionServiceCierreTest`: **15/15**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.
- Validación Git local informada por el usuario: `git diff` limpio, `git diff --check` sin observaciones y `git status` limpio.

### Bloque funcional cerrado

Queda validado el flujo básico de financiación de tarjeta:

**pago parcial → vencimiento → creación de Financiacion → pago posterior → cancelación de financiación → excedente sobre cuota siguiente cuando corresponde.**

La coordinación entre financiación y obligación está cubierta por tests y la suite completa no presenta regresiones.

No se implementan todavía intereses, TNA, punitorios, CFT ni refinanciación.

### Regla multidivisa pendiente

No existe conversión implícita entre la moneda original de una financiación y una liquidación posterior en otra moneda. Antes de modificar este comportamiento debe definirse explícitamente la regla de conversión, la cotización aplicable y su trazabilidad. No inventar una conversión por inferencia.

### Próximo paso

Si se continúa con Tarjeta de Crédito, primero reconstruir el estado desde GitHub y revisar código, tests y reglas de negocio relacionadas con el caso multidivisa financiación + liquidación. El siguiente cambio debe ser mínimo y comenzar por tests de la regla de negocio definida.

### Continuidad

No asumir resultados locales posteriores a esta actualización. Después de sincronizar la rama, el usuario debe ejecutar nuevamente los tests solo cuando exista un cambio de código que lo justifique.
\n\n## ESTADO CANÓNICO DE TARJETA DE CRÉDITO — 19/09/2026

El código actual contiene el bloque avanzado de tarjeta: ciclos/cuotas, cierre y valorización, liquidación multidivisa, crédito disponible, financiación, pago mínimo, TNA histórica, interés financiero, punitorio, cargos, refinanciación, cancelación anticipada, pagos/reversiones con trazabilidad y UI Swing específica.

Los commits recientes también corrigen transacciones, financiación multidivisa, reversión y aplicación del estado de obligación durante el cierre.

**Estado de cierre:** todavía no se puede declarar terminada. El HEAD `bcb994d` tiene GitHub Actions fallido en `Run tests`. El próximo trabajo es diagnóstico y validación, no agregar funcionalidad por inercia.

## ESTADO CANÓNICO DE CONTINUIDAD — 19/09/2026

Esta sección supersede cualquier estado anterior cuando exista contradicción. La fuente de verdad es el código, los tests y los commits actuales de GitHub.

### Git
- Rama de trabajo: `feature/swing-shell`.
- HEAD: `bcb994d` — `fix: aplicar estado de obligacion a todas las ramas del cierre`.
- Rama estable: `main`.
- `main`: `4b8100d` — `fix: retirar servicio de financiacion agregado accidentalmente en main`.
- El usuario verificó localmente que `main`, `github/main` y `bitbucket/main` apuntan al mismo commit `4b8100d`.
- No se realizó merge de `feature/swing-shell` a `main`.

### Estado de Tarjeta de Crédito
El código actual contiene ciclos/cuotas, cierre y valorización histórica, liquidación multidivisa explícita, crédito, financiación, pago mínimo, TNA/interés financiero, punitorios, cargos financieros, refinanciación, cancelación anticipada, pagos, reversiones y trazabilidad, además de integración Swing. La existencia de estos componentes no equivale a validación final del HEAD.

### Validación
- Última suite completa local verde informada: **797/797**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, 19/09/2026 11:44 -03:00, sobre `ba2027168`.
- Después de esa suite se incorporaron nuevos commits; por tanto 797/797 no valida `bcb994d`.
- GitHub Actions sobre `bcb994d` terminó con **failure** en `Run tests`; Checkout y Setup Java finalizaron correctamente.
- El usuario está ejecutando ahora la suite general local sobre el estado actual y todavía no informó el resultado final.

### Punto exacto de continuidad
Primero obtener el fallo concreto de la suite local/CI; identificar si corresponde a producción, test o entorno; corregir con el cambio mínimo; ejecutar tests específicos y suite completa; revisar `git diff`, `git diff --check` y `git status`; y actualizar nuevamente esta documentación con el resultado real.

No asumir que la suite local falla por el mismo motivo que CI hasta disponer del stack trace o resultado concreto. No considerar terminada Tarjeta de Crédito ni avanzar a otra funcionalidad hasta recuperar una suite verde sobre el HEAD actual.

### Regla permanente
Reconstruir el estado desde GitHub antes de cada modificación. Prioridad: código actual → tests → commits → `main` → documentación. No modificar `main` automáticamente.


## AUDITORÍA INTEGRAL DE TARJETA — 19/09/2026 18:57 -03:00

La auditoría del 100% del bloque de tarjeta se realizó sobre el código, tests, commits y documentación de `feature/swing-shell`, sin tomar la documentación histórica como fuente de verdad.

### Correcciones realizadas durante la auditoría

- El crédito utilizado ahora incluye el saldo pendiente de una `Refinanciacion`.
- Una financiación asociada a una obligación ya `REFINANCIADA` deja de contarse por separado para evitar doble contabilización.
- El pago de una refinanciación pasó a formar parte del flujo real de `PagoTarjetaService`, con movimiento financiero y trazabilidad en `PagoTarjeta`.
- Se incorporó la referencia a la refinanciación y su importe dentro de `PagoTarjeta`.
- Se implementó la reversión de pagos de refinanciación, restaurando cuotas, saldo del plan y crédito disponible, junto con el movimiento compensatorio.
- Se agregaron pruebas de crédito pendiente de refinanciación, pago, reversión y liberación/restauración del crédito.

### Regla consolidada

Una deuda refinanciada continúa consumiendo el límite de la tarjeta mientras el plan tenga saldo pendiente. El pago reduce ese consumo y su reversión lo restaura. La obligación original permanece como historial `REFINANCIADA`; el saldo económico activo reside en el plan de refinanciación.

### Cobertura auditada

Se revisaron ciclos, cuotas, cierres, valorización, liquidación multidivisa, crédito disponible, financiación, intereses TNA, punitorios, pago mínimo, pagos parciales/totales, reversión, refinanciación, cuotas refinanciadas, estados, trazabilidad, fechas, moneda y UI Swing.

### Gaps funcionales que permanecen abiertos

1. La refinanciación todavía almacena `tasaAnual`, pero no tiene un motor propio de intereses periódicos/TNA ni punitorios equivalentes al de `Financiacion`.
2. `FinanciacionService.cancelarAnticipadamente()` modifica la deuda directamente sin registrar por sí mismo un movimiento de salida de fondos. El flujo financiero canónico debe pasar por `PagoTarjetaService`.
3. El cálculo de punitorios no está vinculado de forma explícita al cumplimiento del pago mínimo; la regla de mora/punitorio debe quedar integrada con el historial de pagos del resumen.
4. `TarjetasCreditoPanel` muestra límite, disponible, consumido, ciclo, vencimiento, obligaciones, pago mínimo y financiaciones, pero todavía no expone de forma completa las operaciones de crear financiación/refinanciación ni el detalle completo de cuotas refinanciadas, TNA, punitorios y cargos.
5. El calendario de días inhábiles sigue limitado a sábado/domingo; los feriados requieren un calendario bancario explícito.
6. `LocalDateTime.now()` continúa utilizándose en validaciones de fechas futuras; una abstracción `Clock` mejoraría determinismo.
7. No existe todavía versionado formal de esquema de base de datos.
8. La suite completa sobre el estado posterior a estas correcciones no está validada por el asistente. GitHub Actions de los commits de esta auditoría termina en `Run tests = failure`; el detalle de log excede el límite de recuperación disponible. Por lo tanto, no se declara verde ni cerrada la tarjeta.

### Estado Git

- Rama: `feature/swing-shell`.
- HEAD auditado: `45ece8797094df0ac5506808cfae2939ca66ae03` — `fix: ajustar prueba de pago refinanciado`.
- `main`: `4b8100d7242d3cd030d0a903098a93cc5b8e547f`.
- No se realizó merge a `main`.

### Criterio de cierre

Tarjeta de Crédito no debe considerarse cerrada hasta que los gaps anteriores estén cubiertos o explícitamente documentados como fuera de alcance, y exista una suite completa verde sobre el HEAD final.


## AUDITORÍA INTEGRAL — 19/09/2026

Esta sección supersede las conclusiones anteriores cuando exista contradicción. La auditoría se realizó sobre el código y tests actuales de `feature/swing-shell`, y se contrastó la normativa vigente relevante del BCRA para pagos de consumos en moneda extranjera y punitorios.

### Estado técnico verificado

- Consumo con tarjeta: implementado mediante `Movimiento` + `Obligacion`.
- Límite y crédito disponible: implementados y cubiertos por pruebas de moneda original, liquidación, valorización de cierre, financiación y refinanciación.
- Ciclos y cuotas: implementados, con cierre histórico y protección contra segundo cierre.
- Liquidación multidivisa: explícita y trazable mediante `TipoCambio`; no se reutiliza silenciosamente una cotización de otro día hábil.
- Pagos parciales/totales: implementados mediante `PagoTarjetaService`.
- Financiación: creación al vencimiento, pago posterior, cargos financieros, reversión de pagos y efecto sobre el crédito disponible.
- Refinanciación: plan, cuotas, pago mediante `PagoTarjetaService`, reversión y efecto sobre el crédito disponible.
- TNA histórica: cálculo diario simple sobre 365 días; se agregaron pruebas para cambios de tasa dentro del período, límite de vigencia y no duplicación del mismo período.
- UI: existe `TarjetasCreditoPanel` con consulta de límite, disponible, consumido, ciclo, vencimiento, obligaciones, pago mínimo y financiaciones.

### Hallazgos que no deben considerarse cerrados

1. Refinanciación: `tasaAnual` se almacena pero no existe todavía un motor de amortización/interés periódico propio. No se debe inventar si la tasa representa cuota francesa, interés simple u otra modalidad.
2. Punitorios: el cálculo de `FinanciacionService` no está conectado todavía con el cumplimiento del pago mínimo del resumen. La Ley 25.065 establece que no corresponde aplicar punitorios cuando se efectuó el pago mínimo en la fecha correspondiente. citeturn5search2
3. Financiación multidivisa + liquidación posterior: el modelo no convierte implícitamente una financiación en moneda original a otra moneda de liquidación. La conversión y cotización aplicables a ese caso deben quedar explícitamente trazadas antes de automatizarla.
4. Cancelación anticipada de financiación: la operación directa quedó restringida a API interna de paquete. El flujo público de pago continúa siendo `PagoTarjetaService`, que exige cuenta pagadora, categoría y movimiento financiero.
5. `RefinanciacionService.registrarPago()` quedó restringido a API interna de paquete; el flujo público de pago de refinanciación continúa en `PagoTarjetaService`.
6. Crédito por cargos financieros: los cargos pendientes en la misma moneda de la tarjeta ahora consumen crédito y se liberan al pagarse/revertirse. En financiación multidivisa todavía falta definir una valorización trazable de cargos posteriores.
7. UI específica: todavía faltan operaciones de crear financiación/refinanciación y una vista completa de cuotas refinanciadas, TNA, punitorios y cargos.
8. Calendario bancario: sábado/domingo están tratados; feriados requieren una fuente/calendario bancario explícito.
9. Determinismo temporal: persiste `LocalDateTime.now()` en validaciones; conviene introducir `Clock`.
10. Esquema: no existe versionado formal de base de datos.

### Criterio de cierre

La tarjeta no se declara cerrada todavía. Los puntos 1, 2, 3 y 4 afectan reglas contables/financieras que deben quedar determinadas y trazables; el resto son tareas técnicas que pueden seguir cerrándose sin modificar `main`.



## AUDITORÍA INTEGRAL — ACTUALIZACIÓN 19/09/2026

Se revisó nuevamente el código actual de feature/swing-shell, tests, comparación con main, servicios, dominio, persistencia y UI.

### Corrección técnica incorporada
Obligacion.crearFinanciacion(...) ahora impide capital nulo/no positivo y evita que varias financiaciones superen el saldo no financiado pendiente. Esto protege el dominio incluso cuando la financiación se crea fuera de ObligacionService.financiarSaldoImpago(...).

### Regla vigente
Una financiación pendiente continúa consumiendo crédito. Los pagos reducen el consumo; las reversiones lo restauran. Una refinanciación pendiente continúa consumiendo crédito mientras su plan tenga saldo. La obligación origen queda como historial REFINANCIADA.

### Resultado de auditoría
No se detectó otra inconsistencia técnica inequívoca que pueda corregirse sin introducir una política financiera no especificada. Quedan identificados como decisiones materiales la modalidad de amortización de refinanciaciones, la conversión de financiación multidivisa cuando posteriormente se liquida en otra moneda y la integración exacta del pago mínimo con punitorios.

### Referencia normativa utilizada
Argentina.gob.ar, Ley 25.065 / material oficial sobre tarjetas: el pago mínimo evita la mora y el saldo restante puede generar interés compensatorio; los punitorios corresponden cuando no se paga al menos el mínimo. La normativa también establece que los punitorios no son capitalizables.

### Validación pendiente del HEAD
El último mvn test verde informado corresponde a un commit anterior. La CI del estado auditado debe terminar verde antes del cierre definitivo.


## ACTUALIZACIÓN CANÓNICA — 20/09/2026

### Estado validado
- Rama: `feature/swing-shell`.
- HEAD: `4edd0fd62db75bfab25b3124169d9e43f42ebf88` — `fix: valorizar financiacion multidivisa sin cierre de obligacion`.
- `main`: `4b8100d7242d3cd030d0a903098a93cc5b8e547f`; no se realizó merge. Comparación: 1096 commits por delante y 2 por detrás.
- `mvn test`: **841/841**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, 16:40 min, finalizado 20/09/2026 10:19:49 -03:00.
- `git diff` y `git diff --check`: limpios; working tree limpio; rama sincronizada con `bitbucket/feature/swing-shell`.

### Avances registrados
El núcleo de Tarjeta de Crédito mantiene cobertura de consumo, ciclos, cuotas, cierre, valorización histórica, liquidación multidivisa, crédito, pagos y reversiones. La auditoría agregó financiación, TNA/intereses, cargos, refinanciación y controles de integridad, incluyendo financiación multidivisa sin cierre de obligación y límites de financiación.

### Pendientes que requieren decisión explícita
1. Modalidad de amortización/interés periódico de refinanciación.
2. Conversión, cotización y trazabilidad de financiación multidivisa cuando la liquidación posterior usa otra moneda.
3. Integración exacta del pago mínimo con punitorios.
4. UI avanzada de financiación/refinanciación.
5. Calendario bancario de feriados.
6. `Clock` y versionado formal de esquema.

La suite verde actual valida el estado técnico; no sustituye la definición de estas reglas financieras.


## Auditoría funcional adicional — 20/09/2026

Durante la auditoría se detectaron y corrigieron dos reglas de integridad concretas:

1. **Punitorios:** antes podían comenzar a calcularse desde la fecha de inicio de la financiación, incluso antes del vencimiento de la obligación. Ahora el primer día computable es el día posterior a la fecha límite de pago; un cálculo en la fecha límite se rechaza por no existir mora.
2. **Cuenta pagadora:** una tarjeta de crédito no puede utilizarse como cuenta pagadora de otra obligación de tarjeta. La regla quedó en servicio y el selector Swing también excluye tarjetas de crédito.
3. **Refinanciación:** la fecha de inicio no puede ser anterior a la fecha de origen de la obligación.

Se agregaron tests específicos para las tres reglas. La validación CI de GitHub correspondiente al último cambio estaba **en curso** al momento de esta actualización; por lo tanto no se declara todavía una nueva suite verde posterior a estos cambios.

El alcance de la auditoría mantiene como decisiones de negocio pendientes la modalidad de interés/amortización de refinanciación, la regla exacta de punitorios respecto del pago mínimo y la conversión de una financiación multidivisa seguida de liquidación en otra moneda. No se inventaron esas reglas.


## Actualización — 20/09/2026: pago mínimo y punitorios

Se corrigió una inconsistencia detectada en la auditoría: la financiación del saldo impago podía quedar habilitada para generar punitorios aunque el titular hubiera abonado el pago mínimo antes del vencimiento.

La financiación ahora conserva explícitamente si el punitorio está habilitado. Al crear la financiación desde el saldo impago se calcula el total de pagos activos de la obligación realizados hasta la fecha límite y se compara con el pago mínimo calculado. Si el mínimo fue cumplido, el punitorio queda deshabilitado; el interés financiero del saldo financiado continúa siendo independiente.

FinanciacionService.calcularPunitorio(...) rechaza la generación de punitorios para una financiación cuyo indicador está deshabilitado.

Se agregó una prueba de integración que paga el 10% mínimo antes del vencimiento, financia el saldo restante y verifica que no se pueda generar punitorio.

Validación pendiente: estos cambios fueron publicados directamente sobre feature/swing-shell; todavía debe ejecutarse la suite Maven sobre este HEAD antes de declarar el estado verde.
