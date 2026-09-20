# SOFP — Tests

## Estado de validación — 17/09/2026

### Validación más reciente

- `mvn test`: **761/761**.
- Failures: 0.
- Errors: 0.
- Skipped: 0.
- `BUILD SUCCESS`.
- Finalizada: **17/09/2026 17:54:49 -03:00**.

### Validaciones específicas del bloque multidivisa

`TarjetaCreditoMultidivisaIntegracionTest`: **1/1**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizada **17/09/2026 17:29:45 -03:00**.

`TarjetaCreditoPagoCreditoTest`: **5/5**.

`ObligacionServiceLiquidacionTest`: **4/4**.

Ejecución conjunta de las dos suites relacionadas: **9/9**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizada **17/09/2026 17:40:50 -03:00**.

La cobertura incorpora el ciclo completo de una obligación multidivisa: consumo en moneda original, valorización de cierre, pago parcial en moneda original, liquidación del saldo restante en moneda de liquidación y pago total posterior, verificando la liberación completa del crédito.

### Validaciones específicas relevantes anteriores

- `TipoCambioRepositoryTest`: 6/6.
- `ObligacionServiceCierreTest`: 4/4.
- `ObligacionRepositoryTest`: 7/7.
- `MovimientoCreditoMultimonedaTest`: 1/1.
- `PagoTarjetaServiceTest`: 10/10.
- `ObligacionLiquidacionTest`: 13/13.
- `ObligacionesPanelTest`: 6/6.

La cobertura actual confirma valorización histórica, crédito multidivisa, proporcionalidad después de pagos parciales, liquidación/pago, liberación de crédito y cierre iniciado desde UI.

### Evolución del último bloque

La prueba de integración fue incorporada y posteriormente se corrigieron únicamente las aserciones `BigDecimal` para comparar valores monetarios sin depender de la escala. La lógica de negocio no fue modificada por esos commits correctivos.

### Pendiente de cobertura/diseño

- flujo completo de obtención y registro de valorización de cierre dentro de la aplicación;
- comportamiento de crédito para obligaciones multidivisa todavía no valorizadas;
- persistencia/UI del flujo integral de cierre y pago multidivisa;
- estabilización de arranque H2, logging y manejo de errores de la aplicación estable.

## Criterio de cierre

Tests específicos → relacionados → suite general → `git diff` → `git diff --check` → `git status` → documentación. Los resultados locales solo se consideran conocidos cuando son informados por el usuario.


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
\n\n## ESTADO DE TESTS — 19/09/2026

Último resultado local verde informado: `mvn test` = **797/797**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, 19/09/2026 11:44 -03:00, sobre `ba2027168`.

Desde entonces la rama llegó a `bcb994d`. GitHub Actions falló en `Run tests` tanto por push como por pull request. No debe reutilizarse 797/797 como validación del HEAD actual.

Próxima validación: diagnosticar CI; ejecutar cierre/financiación/pagos/reversiones/refinanciación; luego `mvn test` completo; finalmente `git diff`, `git diff --check` y `git status`.

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

### Validación de tests
La suite general actual es **841/841**, completamente verde. La cobertura reciente incluye financiación multidivisa, límites de financiación, TNA/intereses históricos, pagos y reversiones de tarjeta, además de los bloques de ciclos, liquidación y crédito ya existentes.
