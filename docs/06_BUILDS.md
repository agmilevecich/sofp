# SOFP — Historial de Builds

## Estado documental — 17/09/2026

**Rama de trabajo:** `feature/swing-shell`.
**Último commit de código/test:** `9459357b` — `fix: comparar credito multidivisa sin escala`.
**Último commit documental:** `b8b54da` — `docs: actualizar estado de continuidad`.

## Validación más reciente

- `mvn test`: **761/761**.
- Failures: 0.
- Errors: 0.
- Skipped: 0.
- `BUILD SUCCESS`.
- Finalizado: **17/09/2026 17:54:49 -03:00**.

## Validaciones específicas posteriores al bloque de crédito

- `TarjetaCreditoMultidivisaIntegracionTest`: **1/1**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizada **17/09/2026 17:29:45 -03:00**.
- `TarjetaCreditoPagoCreditoTest`: **5/5**.
- `ObligacionServiceLiquidacionTest`: **4/4**.
- Ejecución relacionada: **9/9**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizada **17/09/2026 17:40:50 -03:00**.

## Último bloque implementado

### Ciclo integral de tarjeta multidivisa

La integración quedó cubierta mediante el flujo: consumo en USD, valorización al cierre, pago parcial en USD, liquidación del saldo restante en ARS con una cotización posterior y pago completo de la liquidación. La finalización de la liquidación libera el crédito utilizado.

`ObligacionRepository` calcula el crédito utilizado con `saldoLiquidacion` después de liquidar y con `saldoPendiente`/valorización proporcional antes de liquidar.

Los commits de código/test más recientes fueron:

- `c3bbce49` — `test: cubrir ciclo completo de tarjeta multidivisa`.
- `a440051c` — `fix: comparar saldo de liquidacion sin escala en test`.
- `e6b4993c` — `fix: comparar credito sin escala en test multidivisa`.
- `9459357b` — `fix: comparar credito multidivisa sin escala`.

Los tres últimos commits correctivos ajustan comparaciones `BigDecimal` del test y no modifican la lógica de negocio.

## Bloques multidivisa cerrados

- `TipoCambio` histórico.
- moneda original y moneda de liquidación.
- liquidación explícita y trazable.
- `saldoLiquidacion` y pagos parciales/totales.
- valorización histórica de cierre separada de liquidación.
- crédito disponible basado en valorización histórica.
- reducción proporcional del crédito después de pagos parciales.
- liberación del crédito después de pagar completamente la liquidación.
- cierre de ciclo iniciado desde UI.
- pagos en moneda original antes de liquidación y en moneda de liquidación después de liquidación.
- integración completa del ciclo parcial → liquidación → pago.

No se realizan conversiones implícitas.

## Próximo bloque

1. Revisar `ObligacionService` y el flujo de cierre de resumen.
2. Contrastar con normativa BCRA y documentación vigente de la entidad de referencia cómo se obtiene y aplica la cotización de cierre para consumos extranjeros.
3. Definir obligaciones multidivisa todavía no valorizadas al cierre.
4. Completar, si corresponde, persistencia/UI del flujo de cierre y pago multidivisa.
5. Revisar consumos extranjeros sobre crédito antes de disponer de valorización.

## Estabilización futura

Antes del fast-forward a `main`, y no como parte del bloque actual: arranque automático de H2 desde Java, cierre limpio de H2, consola silenciosa, logging técnico a archivo y errores de arranque/conexión informados mediante `JOptionPane`.

No se modificó `main`.


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
\n\n## BUILD CANÓNICO — 19/09/2026

Último build local verde conocido: **797/797** sobre `ba2027168`, 19/09/2026 11:44 -03:00.

HEAD actual: `bcb994d`. GitHub Actions Maven Test run #86 (push) y #87 (pull request) terminaron en failure durante `Run tests`. Por lo tanto el build actual de la feature **no está verde** y debe diagnosticarse antes de seguir ampliando funcionalidad.

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

### Build de continuidad
El resultado más reciente informado y registrado es **841/841**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, 20/09/2026 10:19:49 -03:00. Este resultado corresponde al estado actual de la rama y supersede las validaciones históricas anteriores.


## ACTUALIZACIÓN CANÓNICA — 20/09/2026

Esta sección supersede cualquier estado anterior cuando exista contradicción. Rama `feature/swing-shell`; HEAD validado `4edd0fd62db75bfab25b3124169d9e43f42ebf88` (`fix: valorizar financiacion multidivisa sin cierre de obligacion`). `main` permanece en `4b8100d7242d3cd030d0a903098a93cc5b8e547f`, sin merge; comparación: 1096 commits por delante y 2 por detrás.

La suite completa local actual fue **841/841**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, 16:40 min, finalizada 20/09/2026 10:19:49 -03:00. El usuario informó además `git diff` y `git diff --check` limpios, working tree limpio y rama sincronizada con `bitbucket/feature/swing-shell`.

El estado funcional incluye ciclos/cuotas, cierre, valorización histórica, liquidación multidivisa, crédito disponible, pagos/reversiones, financiación, TNA/intereses, cargos, refinanciación y UI; se añadió cobertura para financiación multidivisa sin cierre y límites de financiación.


## Auditoría funcional adicional — 20/09/2026

Durante la auditoría se detectaron y corrigieron dos reglas de integridad concretas:

1. **Punitorios:** antes podían comenzar a calcularse desde la fecha de inicio de la financiación, incluso antes del vencimiento de la obligación. Ahora el primer día computable es el día posterior a la fecha límite de pago; un cálculo en la fecha límite se rechaza por no existir mora.
2. **Cuenta pagadora:** una tarjeta de crédito no puede utilizarse como cuenta pagadora de otra obligación de tarjeta. La regla quedó en servicio y el selector Swing también excluye tarjetas de crédito.
3. **Refinanciación:** la fecha de inicio no puede ser anterior a la fecha de origen de la obligación.

Se agregaron tests específicos para las tres reglas. La validación CI de GitHub correspondiente al último cambio estaba **en curso** al momento de esta actualización; por lo tanto no se declara todavía una nueva suite verde posterior a estos cambios.

El alcance de la auditoría mantiene como decisiones de negocio pendientes la modalidad de interés/amortización de refinanciación, la regla exacta de punitorios respecto del pago mínimo y la conversión de una financiación multidivisa seguida de liquidación en otra moneda. No se inventaron esas reglas.

## ACTUALIZACIÓN CANÓNICA DE CONTINUIDAD — 26/09/2026

Esta sección supersede cualquier estado anterior de este documento cuando exista contradicción. La fuente de verdad continúa siendo el código, los tests y GitHub. Las secciones históricas anteriores se conservan como trazabilidad y no deben utilizarse para describir el estado actual si difieren de esta sección.

### Estado Git actual

- Rama de trabajo: `feature/swing-shell`.
- Rama estable: `main`.
- `main`: `a23d3a5c0658ffbca93391c34f79ad8bc37fdc10`.
- Último commit de código validado: `a95976d5f829cdc2b11529127fb9fb18e5978c94` — `fix: consolidar inversiones antes de convertir moneda`.
- Comparación GitHub al cierre de esta etapa: `feature/swing-shell` está **74 commits por delante de `main` y 0 por detrás**.
- No se realizó merge a `main`.
- El working tree local fue informado por el usuario como limpio: `git status` limpio, `git diff --check` sin observaciones y `git diff` vacío.

### Último bloque cerrado: patrimonio financiero y error de redondeo

Se corrigió `PatrimonioFinancieroService.calcularActivosInversiones()` para consolidar primero los importes de inversión por moneda de origen, convertir una sola vez cada total por moneda y recién entonces sumar los importes convertidos.

La regla validada es:

**consolidar por moneda → convertir → redondear según la moneda de presentación → sumar.**

Esto evita acumulaciones de centavos producidas por redondear cada posición de inversión por separado. El cambio se limita a activos de inversión; no modifica la conversión individual de cuentas monetarias ni de pasivos de tarjeta.

`TipoCambio.convertir()` no fue modificado: conserva su contrato de redondear al número de decimales de la moneda destino.

### Tests y validación final informada por el usuario

- `PatrimonioFinancieroServiceTest`: **4/4**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.
- `CarteraActivoServiceTest` + `TipoCambioTest`: **20/20**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.
- Suite completa `mvn test`: **887/887**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.
- Suite completa finalizada: **26/09/2026 13:48:40 -03:00**.
- Duración de la suite completa: **21:12 min**.
- La validación Git posterior informada por el usuario confirmó working tree limpio.

Este resultado **887/887 sustituye como última suite completa conocida** a los resultados históricos anteriores de 883/883, 861/861, 841/841, 848/848 y anteriores.

### Decisión contable cerrada

Para la valorización patrimonial de inversiones, cuando varias posiciones están expresadas en la misma moneda de origen, no se redondea cada posición convertida individualmente. Se consolida el importe de origen y se convierte/redondea una única vez por moneda.

Caso regresivo que motivó la corrección:

- dos posiciones de 1,00 USD;
- cotización: 100,0049 ARS/USD;
- conversión individual: 100,00 + 100,00 = 200,00;
- consolidación previa: 2,00 × 100,0049 = 200,0098 → **200,01 ARS**.

La regla queda cubierta por test y por la suite completa.

### Estado funcional al cierre

El bloque de patrimonio financiero queda validado en su implementación actual. La rama contiene además los bloques previamente auditados de operaciones financieras coordinadas, posiciones de activos, compatibilidad de moneda, saldos históricos de cuentas y refinanciación. La documentación histórica conserva el detalle de esas auditorías.

No se considera que una cifra histórica de tests valide el HEAD actual si existe una suite posterior; para continuidad se toma **887/887 sobre el estado actual informado**.

### Próximo paso

El siguiente bloque funcional natural es **patrimonio neto y reportes financieros consolidados**, pero antes de modificar código debe reconstruirse nuevamente el estado desde GitHub y revisarse la implementación actual de `ResumenPatrimonial`, `PatrimonioFinancieroService`, reportes, repositorios y tests relacionados.

No inventar reglas contables. Si el próximo bloque requiere definir qué componentes integran patrimonio neto, resultado acumulado, resultado del período o reportes consolidados, primero debe identificarse la regla explícita existente y, si no existe, documentar la decisión antes de implementarla.

### Regla permanente de continuidad

Antes de cualquier cambio:

`GitHub → rama → últimos commits → comparación con main → código relacionado → repositorios → tests → reglas de negocio → documentación → último resultado → cambio mínimo`

Después de cambios importantes:

`tests específicos → tests relacionados → suite completa cuando corresponda → git diff → git diff --check → git status → documentación`

No modificar ni mergear `main` automáticamente. La documentación es auxiliar: código actual y tests prevalecen sobre cualquier nota histórica.


## ACTUALIZACIÓN CANÓNICA DE CONTINUIDAD — 26/09/2026 18:19 -03:00

Esta sección supersede cualquier estado anterior de este documento cuando exista contradicción. La fuente de verdad continúa siendo el código, los tests y GitHub.

### Cierre de etapa: patrimonio financiero, patrimonio neto y reportes consolidados

- Rama de trabajo: `feature/swing-shell`.
- Rama estable: `main`.
- Comparación GitHub: `feature/swing-shell` está **98 commits por delante de `main` y 0 por detrás**.
- No se realizó merge a `main`.
- El working tree local fue verificado por el usuario como limpio: `git status` sin cambios, `git diff` vacío y `git diff --check` sin observaciones.

### Validación final

La suite general ejecutada por el usuario después de sincronizar la rama quedó:

- `mvn test`: **892/892**.
- Failures: **0**.
- Errors: **0**.
- Skipped: **0**.
- **BUILD SUCCESS**.
- Duración: **22:40 min**.
- Finalizada: **26/09/2026 18:19:48 -03:00**.

Además, antes de la suite completa se validó el bloque de movimientos:

- `MovimientoServiceTest` + `MovimientoRepositoryTest`: **62/62**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.
- `MovimientosPanelTest`: **3/3**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.

El test de `MovimientosPanelTest` fue hecho determinista sustituyendo fechas basadas en `LocalDateTime.now()` por fechas fijas, sin modificar la regla temporal de `MovimientoService`.

### Estado funcional

Queda validado el bloque de patrimonio financiero y su integración con el shell de reportes, incluyendo:

- cálculo de activos monetarios e inversiones;
- conversión multidivisa a moneda de presentación;
- consolidación de inversiones por moneda antes de convertir y redondear;
- pasivos contemplados por `ResumenPatrimonial`;
- cálculo de patrimonio neto según el modelo actual;
- presentación consolidada en `ReportesPanel`;
- integración de `PatrimonioFinancieroService` en `Main` y `MainFrame`.

La suite completa de 892 tests valida el estado actual después de los últimos ajustes de refinanciación, patrimonio y determinismo temporal de movimientos.

### Pendientes

No se deben inventar reglas contables para completar funcionalidades que todavía no tienen semántica explícita. En particular, antes de ampliar resultados financieros consolidados debe definirse, si corresponde, la semántica de resultado del período, resultado acumulado y otros componentes patrimoniales que no estén determinados por el modelo actual.

También permanecen como evolución técnica/funcional los pendientes ya documentados de refinanciación avanzada, calendario bancario de feriados, `Clock`, migraciones formales y UI avanzada donde corresponda.

### Próximo paso

La etapa queda **validada y cerrada técnicamente** con suite completa verde.

El siguiente trabajo debe comenzar reconstruyendo nuevamente el estado desde GitHub y, antes de modificar código, inspeccionar la implementación actual relacionada con el próximo bloque funcional. No modificar ni mergear `main` automáticamente.

