# SOFP — Estado actual

> Documento de continuidad. La fuente de verdad técnica es el código, los tests y los commits actuales; `docs/` es documentación auxiliar.

## Actualización de continuidad — 18/09/2026 20:35 -03:00

Esta sección supersede cualquier estado, validación o próximo paso anterior cuando exista contradicción.

### Estado Git

- Rama de trabajo: `feature/swing-shell`.
- Rama estable: `main`.
- No se realizó merge a `main`.
- Último commit de código/documentación funcional antes de esta actualización: `0da28cc` — `test: cubrir estado de financiacion`.
- La rama contiene el bloque de financiación de tarjeta implementado y validado.

### Último bloque implementado — Financiación

Se incorporó la entidad persistente `Financiacion`, relacionada con `Obligacion`.

Implementado:

- `Financiacion` con obligación, fecha de inicio, capital original y saldo de capital.
- Validación de obligación, fecha e importe positivo.
- Registro de pagos sobre el capital sin permitir superar el saldo.
- Estados derivados `estaPendiente()` y `estaCancelada()`.
- Relación `Obligacion -> financiaciones` con cascade y orphan removal.
- Persistencia y recuperación de financiaciones.
- Inclusión de `Financiacion` en la unidad de persistencia de tests.
- Cobertura específica de la entidad y de su persistencia.
- Cobertura de los estados pendiente/cancelada.

Commits del bloque:

- `8252cff` — `feat: agregar relación de financiaciones a obligaciones`.
- `7994754` — `feat: agregar entidad de financiacion de tarjeta`.
- `f001f19` — `test: cubrir entidad de financiacion de tarjeta`.
- `a76a949` — `test: cubrir persistencia de financiacion`.
- `7c0c4aa` — `fix: importar financiacion en test de obligaciones`.
- `1a6a071` — `fix: incluir financiacion en unidad de persistencia de tests`.
- `8a4cd0a` — `feat: exponer estado de financiacion`.
- `0da28cc` — `test: cubrir estado de financiacion`.

### Validación más reciente informada por el usuario

`FinanciacionTest`:

- 9/9 tests.
- 0 failures.
- 0 errors.
- 0 skipped.
- `BUILD SUCCESS`.
- Finalizado: 18/09/2026 20:32:49 -03:00.

Suite completa más reciente conocida:

- `mvn test`: 779/779.
- 0 failures.
- 0 errors.
- 0 skipped.
- `BUILD SUCCESS`.
- Finalizada: 18/09/2026 15:04:44 -03:00.

La suite completa de 779 fue ejecutada antes de la validación específica de `FinanciacionTest`. No se registra una nueva suite completa posterior como resultado informado.

### Aislamiento JPA/H2

Se corrigió el aislamiento de `ObligacionRepositoryTest`: los tests cierran ahora el contexto JPA mediante `JpaTestManager.close()`, evitando reutilizar un `EntityManagerFactory` entre tests y los consiguientes conflictos de claves únicas en H2.

### Estado funcional de tarjetas

Resuelto y validado:

- consumos con obligación;
- ciclos, cuotas, vencimiento y gracia;
- valorización histórica de cierre;
- liquidación histórica explícita;
- pagos parciales y totales;
- tarjetas multidivisa;
- cálculo y liberación de crédito utilizado;
- selección histórica de cotización para liquidación;
- persistencia de financiación como modelo de dominio.

Todavía NO está implementado el flujo completo de negocio:

**pago parcial del resumen → creación automática de financiación → incorporación al siguiente resumen → intereses/cargos.**

### Decisiones vigentes para financiación

Por ahora `Financiacion` representa únicamente capital financiado. No se implementaron todavía intereses, TNA, punitorios, CFT ni refinanciación.

La regla de negocio acordada para el próximo bloque es: si un pago de tarjeta deja capital impago del resumen, esa parte podrá convertirse en financiación para el siguiente ciclo. La fecha de inicio propuesta es el día siguiente al vencimiento, pero debe verificarse contra el flujo actual de `PagoTarjetaService` antes de codificarla.

No inventar tasas ni fórmulas. La financiación de intereses se diseñará posteriormente con histórico de tasas, base diaria, fechas efectivas y redondeo explícitos.

### Próximo paso exacto

Antes de modificar código:

1. revisar el estado actual de `PagoTarjetaService`;
2. revisar `Obligacion`, `Financiacion` y los tests de pagos parciales;
3. determinar exactamente cómo queda `saldoPendiente) después de un pago parcial;
4. definir el punto mínimo donde el servicio crea la financiación;
5. agregar primero tests para pago parcial → financiación;
6. agregar test de pago total → sin financiación;
7. validar persistencia si corresponde;
8. ejecutar tests específicos y relacionados antes de una nueva suite completa.

El próximo cambio debe ser mínimo y no debe introducir todavía intereses ni tasas.

### Regla de continuidad

En la próxima sesión reconstruir nuevamente desde GitHub: rama → últimos commits → comparación con `main` → código relacionado → tests → documentación → último resultado informado → próximo cambio mínimo.

No asumir resultados locales no informados. No modificar `main` automáticamente.


## ACTUALIZACIÓN DE CONTINUIDAD — 19/09/2026

### Primer bloque de financiación conectado

Se implementó el primer flujo funcional de financiación sin introducir intereses ni tasas:

- Obligacion calcula el saldo pendiente correspondiente a un ciclo concreto.
- ObligacionService.financiarSaldoImpago(...) crea la financiación únicamente después del vencimiento efectivo.
- Con cuotas, se financia solamente el saldo impago de la cuota vencida; no se incluyen cuotas futuras.
- La fecha de inicio de la financiación es el día siguiente al vencimiento del ciclo.
- La operación es idempotente para el mismo ciclo.
- Las financiaciones pasan a ser visibles en el ciclo siguiente mediante ObligacionRepository.listarPorCuentaYCierreCiclo(...).

Tests agregados en ObligacionServiceCierreTest para creación, pago total, fecha de vencimiento, cuotas futuras, persistencia, idempotencia y aparición en el ciclo siguiente.

No se registra todavía una ejecución local de estos nuevos tests. Deben ejecutarse después de sincronizar la rama.

### Próximo bloque

El flujo todavía no está completo como medio de pago: PagoTarjetaService aún no deriva los pagos posteriores al vencimiento hacia Financiacion. Tampoco están implementados intereses, TNA, punitorios, CFT ni refinanciación.


## ACTUALIZACIÓN DE CONTINUIDAD — 19/09/2026 11:00 -03:00

### Pagos sobre financiación conectados

Se completó la siguiente parte del flujo:

**financiación vencida → pago posterior → reducción del saldo de financiación y de la deuda subyacente.**

PagoTarjetaService ahora detecta una financiación pendiente cuya fecha de inicio ya alcanzó la fecha del pago. En moneda original:

- el pago se aplica primero a la financiación;
- si sobra importe, continúa sobre la siguiente deuda/cuota;
- la cuota que originó la financiación queda actualizada junto con el saldo de la obligación;
- si la obligación ya tiene liquidación en otra moneda, el pago queda limitado al saldo de la financiación en su moneda original para evitar mezclar monedas.

Se agregaron tests para pago sobre financiación y distribución del excedente sobre la cuota siguiente.

La validación local de estos cambios todavía no fue ejecutada.


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

Esta sección supersede estados anteriores cuando exista contradicción. La fuente de verdad es código, tests y commits actuales de GitHub.

- Rama: `feature/swing-shell`.
- HEAD: `bcb994d` — `fix: aplicar estado de obligacion a todas las ramas del cierre`.
- `main`: `4b8100d`, sincronizada entre GitHub y Bitbucket según verificación local del usuario.
- No se realizó merge a `main`.
- Última suite completa local verde informada: **797/797**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, 19/09/2026 11:44 -03:00, sobre el commit anterior `ba2027168`.
- Después de esa suite se agregaron nuevos commits; por tanto 797/797 no valida el HEAD actual.
- GitHub Actions sobre `bcb994d` (Maven Test run #86 por push y #87 por pull request) terminó en **failure** en `Run tests`. Checkout y Setup Java fueron exitosos.

### Estado funcional actual

El código contiene ciclos/cuotas, cierre y valorización, liquidación multidivisa, financiación, pago mínimo, TNA/intereses, punitorios, cargos, refinanciación, cancelación anticipada, pagos y reversiones con trazabilidad, crédito y UI Swing de tarjetas. Esto describe implementación existente, no validación final del HEAD.

### Próximo paso

Diagnosticar la falla de CI, ejecutar tests específicos de cierre/financiación/pagos/reversiones/refinanciación, luego suite completa y validación Git local. No considerar Tarjeta de Crédito terminada hasta recuperar una suite verde sobre el HEAD actual.

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

### Estado de Tarjeta de Crédito
El HEAD actual tiene cobertura de consumo, ciclos, cuotas, cierre, valorización, liquidación multidivisa, crédito, financiación, TNA/intereses, cargos, refinanciación, pagos y reversiones. La suite de 841 tests valida el estado actual localmente.


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
