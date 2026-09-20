# SOFP — Decisiones

Este documento registra decisiones permanentes del proyecto. Código y tests actuales prevalecen ante contradicciones históricas.

## D-001 a D-044

Se mantienen las decisiones anteriores: repositorio como memoria permanente; desarrollo incremental por Builds; JPA/Hibernate; H2; `BigDecimal`; dominio antes de interfaz; tests como condición de avance; continuidad documental; transferencias mediante `OperacionFinanciera`; paneles especializados sobre `Movimiento`; separación `Cuenta`/`FormaPago`; modelo de activos/pasivos/patrimonio; control de fondos; categorías inactivables; roadmap no equivalente a implementación; gastos e ingresos sobre `Movimiento`; tarjeta de crédito como origen de `Obligacion`; obligación como pasivo especializado; compatibilidad de constructores del shell; pagos autorizados por usuario; refresco de obligaciones; transferencias coordinadas; moneda explícita; UI con moneda; criterio de crédito disponible; ciclos históricos; aislamiento JPA/H2; cuotas generadas por el flujo de gasto; H2 TCP para aplicación; integridad histórica de `Cuenta`; ciclo histórico de obligación; vencimiento de fin de semana; límites temporales de pago; gracia y mora; compatibilidad con datos existentes; multidivisa sin conversiones implícitas; comparabilidad monetaria; financiación avanzada independiente; documentación subordinada al código; `Moneda.cantidadDecimales` no negativa.

## D-045 — Moneda original y moneda de liquidación son conceptos distintos

Un consumo puede tener una moneda económica distinta de la moneda de la tarjeta. `Obligacion` conserva ambas monedas.

## D-046 — La liquidación multidivisa es explícita y trazable

La conversión no se realiza implícitamente al crear la obligación. Una liquidación multidivisa utiliza una cotización histórica explícita y queda asociada a la obligación.

## D-047 — `TipoCambio` representa una cotización histórica

`TipoCambio` conserva moneda origen, moneda destino, cotización, fecha/hora y fuente.

## D-048 — La obligación conserva el tipo de cambio utilizado

`Obligacion` mantiene `tipoCambioLiquidacion` e `importeLiquidacion`. Una obligación ya liquidada no puede liquidarse nuevamente.

## D-049 — Validación de monedas en la liquidación

`Obligacion.liquidar(TipoCambio)` exige coincidencia entre moneda origen/destino del tipo de cambio y las monedas de la obligación.

## D-050 — Los pagos multidivisa se aplican en la moneda de liquidación

Cuando una obligación ya fue liquidada, `PagoTarjetaService` utiliza `saldoLiquidacion` y exige cuenta pagadora en `monedaLiquidacion`. Sin liquidación se conserva el flujo basado en `saldoPendiente`.

## D-051 — La valorización de cierre es histórica y separada de la liquidación

`Obligacion.valorarCierre(TipoCambio)` conserva `tipoCambioCierre` e `importeValorizacionCierre` y no modifica deuda original, saldos ni estado. La valorización sirve para expresar históricamente el consumo en la moneda de la tarjeta; no reemplaza `liquidar(TipoCambio)`.

## D-052 — El crédito utilizado usa la etapa real de la obligación

Antes de liquidar, una obligación en la moneda de la tarjeta utiliza `saldoPendiente`. Una obligación multidivisa pendiente utiliza la valorización de cierre cuando existe, reducida proporcionalmente según el saldo original pendiente. Después de liquidar, el cálculo utiliza `saldoLiquidacion`.

Esto evita doble contabilización del saldo original trasladado a liquidación y permite liberar completamente el crédito cuando la deuda de liquidación queda cancelada.

## D-053 — La valorización de cierre no disponible no se inventa

Una obligación multidivisa sin valorización de cierre no recibe una cotización implícita o posterior solamente para completar el cálculo. El flujo para obtener y registrar esa valorización debe definirse en la aplicación.

## D-054 — El pago posterior y la valorización de cierre siguen siendo conceptos independientes

La valorización histórica de cierre no determina por sí sola la forma de pago futura. Se conservan por separado deuda original, valorización y liquidación explícita.

## D-055 — El cierre de ciclo se inicia desde la UI pero la regla permanece en servicio

`ObligacionesPanel` puede iniciar el cierre mediante `ObligacionService`. La UI no recalcula reglas de negocio ni inventa fechas o cotizaciones; obtiene el ciclo persistido de la obligación y delega la operación.

## D-056 — El crédito se libera con la deuda exigible real

Una vez liquidada una obligación multidivisa, el saldo que determina el crédito utilizado es `saldoLiquidacion`. El hecho de que `saldoPendiente` conserve el importe original trasladado no implica que ese importe deba volver a contabilizarse para crédito.

## Actualización — 17/09/2026

El cálculo de crédito posterior a la liquidación quedó corregido y validado.

- `c592cbc` — `fix: calcular credito sobre saldo de liquidacion`.
- `20bb282` — `test: cubrir credito liberado tras liquidacion multidivisa`.
- `2a789c1` — `test: persistir tipo de cambio de liquidacion`.
- `CuentaServiceCreditoTest`: 3/3.
- `TarjetaCreditoPagoCreditoTest`: 5/5.
- Suite general `mvn test`: **756/756**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.
- Finalizada: **17/09/2026 15:50:11 -03:00**.

La validación local final informada dejó `git diff` vacío, `git diff --check` sin observaciones y working tree limpio.

## Decisiones de negocio todavía abiertas

No se fija todavía una regla de negocio sobre la cotización bancaria concreta utilizada para consumos extranjeros al cierre de resumen. Esa decisión debe contrastarse con normativa BCRA y documentación vigente de la entidad financiera de referencia antes de modificar el modelo.

## Decisiones de estabilización futura

No se implementan todavía. Para la versión estable, antes del fast-forward a `main`, se deberá diseñar el arranque automático de H2 desde Java, su cierre limpio, consola silenciosa, logging técnico a archivo y manejo de fallos de conexión/arranque mediante `JOptionPane`.


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
\n\n## DECISIONES DE CONTINUIDAD — 19/09/2026

- La documentación nunca declara validada una funcionalidad solamente por existir en código.
- La última suite local verde conocida es 797/797 sobre `ba2027168`; no valida los commits posteriores.
- El HEAD `bcb994d` tiene CI fallido en el paso `Run tests`; el estado actual es implementación + validación pendiente.
- Ciclos y cuotas conservan fechas históricas y la valorización de cierre permanece separada de la liquidación.
- Financiaciones conservan moneda y valorización explícitas; no hay conversión implícita entre monedas.
- Capital, intereses, punitorios y cargos se mantienen separados y trazables.
- Tarjeta de Crédito no se considera cerrada hasta tener tests específicos, relacionados y suite completa verdes sobre el HEAD actual.

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


## ACTUALIZACIÓN CANÓNICA DE CONTINUIDAD — 20/09/2026

Esta sección supersede cualquier estado anterior de este documento cuando exista contradicción. La fuente de verdad es el código, los tests y los commits actuales de GitHub.

### Estado Git
- Rama de trabajo: `feature/swing-shell`.
- HEAD validado: `4edd0fd62db75bfab25b3124169d9e43f42ebf88` — `fix: valorizar financiacion multidivisa sin cierre de obligacion`.
- Rama estable: `main`.
- `main`: `4b8100d7242d3cd030d0a903098a93cc5b8e547f`.
- Comparación contra `main`: la rama de trabajo está 1096 commits por delante y 2 por detrás; no se realizó merge a `main`.

### Validación real del estado actual
- Suite completa local: `mvn test` → **841 tests**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.
- Duración: 16:40 min.
- Finalizada: **20/09/2026 10:19:49 -03:00**.
- Validación Git informada: `git diff` y `git diff --check` limpios; working tree limpio; rama sincronizada con `bitbucket/feature/swing-shell`.

### Estado funcional
La auditoría de Tarjeta de Crédito avanzó sobre ciclos, cuotas, cierre, valorización histórica, liquidación multidivisa, crédito disponible, pagos, reversiones, financiación, TNA/intereses, cargos, refinanciación y UI. Se incorporaron además controles sobre financiación multidivisa sin cierre de obligación y límites de financiación en el dominio.

### Regla de continuidad
No tomar resultados anteriores de CI o suites históricas como validación del HEAD actual. Antes de cualquier cambio: reconstruir rama → commits → comparación con `main` → código relacionado → tests → documentación. No modificar `main` automáticamente.

### Decisiones vigentes
- No se agregan conversiones implícitas de moneda.
- La financiación multidivisa conserva trazabilidad de moneda y valorización.
- La validación del dominio impide sobre-financiar una obligación.
- Los flujos financieros canónicos de pago y reversión permanecen coordinados por `PagoTarjetaService`.
- Las reglas no determinadas por el modelo se mantienen como decisiones pendientes y no se inventan por inferencia.
