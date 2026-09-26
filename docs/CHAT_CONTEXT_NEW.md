# SOFP — Contexto para continuar con ChatGPT

## Estado — 15/09/2026

La fuente de verdad es el código, Git y los tests actuales; `docs/` es documentación auxiliar y puede quedar desactualizada. Antes de proponer cambios, reconstruir siempre el estado desde GitHub.

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.
**Último commit de código validado:** `e95585e043290eebb5789f2b628b1edcef8a7344` — `test: cubrir pagos multidivisa en PagoTarjetaService`.

## Validación general

Suite general actual: **718/718**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizada **15/09/2026 20:05:19 -03:00**.

## Estado actual

La Fase Swing está integrada. Gastos con tarjeta generan movimiento + obligación + cuotas. `PagoTarjetaService` coordina pagos reales y la UI de obligaciones está integrada.

La multidivisa general respeta la moneda de cada saldo y movimiento.

El modelo de obligación multidivisa conserva moneda original, moneda de liquidación y `TipoCambio` histórico. `saldoLiquidacion` permite aplicar pagos en la moneda de liquidación sin alterar el saldo original.

`PagoTarjetaService` usa el saldo liquidado cuando existe y exige que la cuenta pagadora esté en la moneda de liquidación. No realiza conversiones implícitas.

## Próximo bloque

1. Revisar `git diff`, `git diff --check` y `git status`.
2. Definir el impacto de consumos extranjeros sobre crédito disponible.
3. Diseñar tests de esa regla antes de modificar el cálculo.
4. Completar persistencia/UI del pago multidivisa.

## Regla de continuidad

Antes de cada cambio: revisar implementación, clases relacionadas, repositorios, tests y reglas de negocio. Luego cambio mínimo → tests específicos → relacionados → suite → diff → diff-check → status → documentación.

No modificar `main`, no asumir resultados locales no informados y no considerar terminado un bloque solo porque compila.


## Actualización de continuidad — cierre 17/09/2026 22:51 -03:00

Esta sección supersede cualquier validación anterior de este documento cuando haya contradicción.

- Rama de trabajo: `feature/swing-shell`.
- HEAD actual: `b4a9bc6b4f64ce191f90eb9e4dd4291356e0574c` — `test: corregir expectativas de valorizacion multidivisa`.
- `main`: `a4be85913847200cb70976d5266d9cbba10b3100`.
- Comparación GitHub: 847 commits por delante de `main`, 0 por detrás. No se realizó merge a `main`.
- Último bloque: corrección de expectativas de tests para reflejar que la valorización de cierre de obligaciones financiadas se almacena en la cuota; no se modificó producción en este último commit.
- Validación específica posterior: 8/8 tests verdes, 0 failures, 0 errors, `BUILD SUCCESS`, informada por el usuario.
- Validación final: `mvn test` con **769/769 tests**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizada **17/09/2026 22:51:16 -03:00**, informada por el usuario.
- Validación Git local final: `git diff` vacío, `git diff --check` sin observaciones y `git status` limpio; rama local al día con `bitbucket/feature/swing-shell`, informado por el usuario.

### Punto exacto para retomar

El cálculo de crédito multidivisa, la valorización histórica de cierre, la liquidación explícita, los pagos antes/después de liquidar y el cierre iniciado desde `ObligacionesPanel` están cubiertos por tests. El siguiente bloque debe comenzar con una revisión de `ObligacionService` y sus clases relacionadas para definir el flujo de cierre de resumen de tarjeta siguiendo reglas bancarias reales. Antes de modificar código se debe contrastar la cotización de cierre de consumos extranjeros con normativa BCRA y documentación vigente de la entidad financiera de referencia. No inventar una regla de negocio por inferencia.

### Regla de continuidad para la próxima sesión

Reconstruir desde GitHub antes de cualquier cambio: rama → últimos commits → comparación con `main` → código relacionado → tests → documentación → último resultado informado → próximo cambio mínimo. No asumir que la documentación histórica representa el estado actual si contradice código o tests.

## ACTUALIZACIÓN DE CONTINUIDAD — 18/09/2026 11:15 -03:00

Esta sección supersede cualquier estado, validación o próximo paso anterior de este documento cuando exista contradicción. La fuente de verdad sigue siendo el código, los tests y GitHub.

### Estado Git

- Rama de trabajo: `feature/swing-shell`.
- `main`: `a4be85913847200cb70976d5266d9cbba10b3100`.
- HEAD de trabajo antes de esta actualización documental: `4a027006e05cfabc1d203dbb84e3712e855aa19d` — `test: corregir fecha histórica de liquidación en sábado`.
- La auditoría de tarjetas quedó validada sin modificar `main`.

### Auditoría de tarjetas de crédito cerrada

La auditoría del flujo de tarjetas se completó respetando la separación entre consumo, valorización de cierre, liquidación y pago.

- Día hábil: se utiliza una cotización del mismo día y hasta el instante de liquidación.
- Sábado/domingo: se utiliza la última cotización disponible del viernes anterior.
- Día hábil sin cotización aplicable: no se reutiliza silenciosamente una cotización de días anteriores.
- Los feriados no se infieren: requieren un calendario bancario explícito.
- No se permiten liquidaciones anteriores al consumo ni fechas de liquidación futuras.

La valorización de cierre continúa siendo independiente de la liquidación efectiva. El consumo conserva su moneda original y la obligación conserva además la moneda de liquidación.

### Tests informados por el usuario

- `TipoCambioRepositoryTest` + `ObligacionServiceLiquidacionTest`: **16/16**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizado **18/09/2026 10:58:33 -03:00**.
- Bloque relacionado de tarjetas: **26/26**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizado **18/09/2026 11:01:22 -03:00**.
- `mvn test`: **775/775**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizado **18/09/2026 11:15:25 -03:00**, 12:42 min.
- `git diff`: limpio; `git diff --check`: sin observaciones; `git status`: working tree limpio; rama local al día con `bitbucket/feature/swing-shell`.

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
\n\n## ACTUALIZACIÓN CANÓNICA — 19/09/2026

Rama `feature/swing-shell`, HEAD `bcb994d`. `main` está en `4b8100d` y permanece sin merge. Último resultado local verde: 797/797 sobre `ba2027168`. CI del HEAD actual falla en `Run tests`.

Prioridad: diagnosticar, corregir y validar antes de seguir ampliando Tarjeta de Crédito.

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

### Punto exacto para retomar
La prioridad de validación pendiente indicada en versiones anteriores quedó resuelta localmente: 841/841 verde. Mantener como pendientes únicamente las reglas financieras que requieren definición explícita y la UI/robustez todavía no cerradas.


## ACTUALIZACIÓN CANÓNICA DE CONTINUIDAD — 20/09/2026

Esta sección supersede cualquier estado anterior cuando exista contradicción. Fuente de verdad: código, tests y commits actuales de GitHub.

- Rama: `feature/swing-shell`.
- HEAD validado: `4edd0fd62db75bfab25b3124169d9e43f42ebf88` — `fix: valorizar financiacion multidivisa sin cierre de obligacion`.
- `main`: `4b8100d7242d3cd030d0a903098a93cc5b8e547f`.
- Comparación contra `main`: 1096 commits por delante y 2 por detrás; no se realizó merge.
- Suite completa local: **841 tests**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, 16:40 min, finalizada **20/09/2026 10:19:49 -03:00**.
- Validación Git informada: `git diff` y `git diff --check` limpios; working tree limpio; rama sincronizada con `bitbucket/feature/swing-shell`.

La auditoría de Tarjeta de Crédito avanzó sobre ciclos, cuotas, cierre, valorización histórica, liquidación multidivisa, crédito disponible, pagos, reversiones, financiación, TNA/intereses, cargos, refinanciación y UI. Se agregó además cobertura para financiación multidivisa sin cierre de obligación y límites de financiación en el dominio.

### Punto exacto para retomar
La suite general actual ya está verde. Los próximos pendientes deben tratarse como reglas financieras que requieren definición explícita o como evolución técnica: modalidad de amortización/interés de refinanciación, conversión trazable de financiación multidivisa con liquidación posterior en otra moneda, integración exacta del pago mínimo con punitorios, UI avanzada, calendario bancario de feriados, `Clock` y versionado formal de esquema.


## ACTUALIZACIÓN CANÓNICA DE CONTINUIDAD — 22/09/2026

Esta sección supersede cualquier estado anterior de este documento cuando exista contradicción. La fuente de verdad es el código, los tests y los commits actuales de GitHub.

### Estado actual
- Rama de trabajo: `feature/swing-shell`.
- Último commit: `9fb287b29250e21efc87ac3e0689be682d0b9604` — `docs: cerrar auditoria tecnica de tarjetas`.
- Commit de código/test inmediatamente anterior: `6980f2da230c8fd3f8beabbe459e86454d55d271` — `test: seleccionar pago financiado correcto`.
- Rama estable: `main`.
- No se realizó merge a `main`.
- La comparación vigente con `main` mantiene la rama de trabajo separada y no debe interpretarse como autorización para hacer merge.

### Validación
El usuario informó una ejecución completa de `mvn -e test` sobre el estado sincronizado anterior al cierre documental:

- **848/848 tests**.
- 0 failures.
- 0 errors.
- 0 skipped.
- `BUILD SUCCESS`.
- Finalizada el **22/09/2026 a las 14:32:59 -03:00**.

El fallo intermedio `NonUniqueResult` quedó resuelto exclusivamente en el test: la consulta podía devolver dos pagos para una misma obligación y ahora selecciona determinísticamente el último pago mediante `ORDER BY p.id DESC` y `setMaxResults(1)`. No fue necesario modificar producción.

### Estado de la auditoría de Tarjeta de Crédito
La auditoría técnica del núcleo de tarjetas queda **cerrada y validada** sobre `feature/swing-shell`. Se consideran cubiertos los bloques auditados de consumo, ciclos, cuotas, cierre y valorización, liquidación multidivisa, crédito disponible, pagos y reversiones, financiación, TNA/intereses, cargos, refinanciación, punitorios, reglas de cuenta pagadora, fechas y trazabilidad financiera.

El cierre técnico no significa que el producto tenga implementadas todas las evoluciones posibles. Permanecen como trabajo futuro, sin inventar reglas de negocio: modalidad de amortización/interés periódico de refinanciación, conversión y trazabilidad de financiación multidivisa con liquidación posterior en otra moneda, integración detallada del pago mínimo con punitorios/intereses, UI avanzada de financiación/refinanciación, calendario bancario de feriados, `Clock`, migraciones/versionado formal de esquema y estabilización de H2 antes de un eventual fast-forward a `main`.

### Punto exacto para continuar
No reabrir la auditoría técnica por los resultados históricos de 797/797, 841/841 ni por el CI fallido de commits anteriores. El siguiente trabajo debe ser una funcionalidad nueva o una de las decisiones pendientes, partiendo siempre del código actual y agregando tests antes de considerar cerrado el cambio.

### Regla permanente
Antes de cualquier modificación: revisar rama, últimos commits, comparación con `main`, implementación relacionada, repositorios, tests y reglas de negocio. Mantener cambios mínimos, commits pequeños y descriptivos. Después de cambios importantes: tests específicos, tests relacionados, suite completa cuando corresponda, `git diff`, `git diff --check` y `git status`. No modificar ni mergear `main` automáticamente.

## ACTUALIZACIÓN CANÓNICA DE CONTINUIDAD — 26/09/2026

Esta sección supersede cualquier estado anterior de este documento cuando exista contradicción. La fuente de verdad continúa siendo el código, los tests y GitHub.

### Estado Git actual

- Rama de trabajo: `feature/swing-shell`.
- Rama estable: `main`.
- `main`: `a23d3a5c0658ffbca93391c34f79ad8bc37fdc10`.
- Último commit de código validado: `a95976d5f829cdc2b11529127fb9fb18e5978c94` — `fix: consolidar inversiones antes de convertir moneda`.
- Comparación GitHub al cierre del bloque de código: **74 commits por delante de `main`, 0 por detrás**.
- No se realizó merge a `main`.
- El usuario informó working tree limpio: `git status` limpio, `git diff --check` sin observaciones y `git diff` vacío.

### Bloque cerrado: patrimonio financiero y redondeo de inversiones

`PatrimonioFinancieroService.calcularActivosInversiones()` ahora consolida primero los importes de inversión por moneda de origen, convierte una sola vez cada total y luego suma los importes convertidos.

Regla validada: **consolidar por moneda → convertir → redondear según la moneda de presentación → sumar**.

El cambio evita acumulaciones de centavos por redondeo individual de posiciones y no modifica `TipoCambio.convertir()`, ni la conversión individual de cuentas monetarias o pasivos de tarjeta.

### Validación final

- `PatrimonioFinancieroServiceTest`: **4/4** verde.
- `CarteraActivoServiceTest` + `TipoCambioTest`: **20/20** verde.
- `mvn test`: **887/887**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.
- Suite completa finalizada **26/09/2026 13:48:40 -03:00**, duración **21:12 min**.
- La validación Git posterior fue limpia.

El resultado **887/887** sustituye como última suite completa conocida a todos los resultados históricos anteriores.

### Regla contable cerrada

Dos posiciones de 1,00 USD con cotización 100,0049 ARS/USD producen 200,01 ARS porque primero se consolidan en 2,00 USD, se convierte 2,00 × 100,0049 = 200,0098 y se redondea una sola vez. La prueba regresiva cubre este comportamiento.

### Próximo bloque

El siguiente bloque funcional natural es **patrimonio neto y reportes financieros consolidados**. Antes de modificar código debe reconstruirse nuevamente el estado desde GitHub y revisarse `ResumenPatrimonial`, `PatrimonioFinancieroService`, reportes, repositorios y tests relacionados.

No inventar reglas contables: si patrimonio neto, resultado acumulado, resultado del período o reportes consolidados requieren una definición que no exista en el modelo actual, primero se documenta la regla y luego se implementa.

### Regla permanente

Antes de cualquier cambio: `GitHub → rama → últimos commits → comparación con main → código → repositorios → tests → reglas de negocio → documentación → último resultado → cambio mínimo`.

Después de cambios importantes: `tests específicos → tests relacionados → suite completa → git diff → git diff --check → git status → documentación`.

No modificar ni mergear `main` automáticamente.
