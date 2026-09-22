# SOFP — Continuidad canónica actual

Este es el punto de entrada recomendado para retomar SOFP. La fuente de verdad técnica es siempre el código, los tests y GitHub; los demás documentos conservan detalle histórico o especializado.


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

- Rama: `feature/swing-shell`.
- `main`: rama estable; no se realizó merge.
- HEAD de código antes de esta actualización documental: `ba2027168bcd172517990cd996aefaad5294da76` — `test: corregir saldo total de obligacion`.
- Comparación con `main`: 927 commits por delante, 0 por detrás.
- Suite completa: **797/797**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.
- Finalizada: **19/09/2026 11:44:00 -03:00**, 16:28 min.
- `PagoTarjetaServiceTest`: **15/15** verde.
- `ObligacionServiceCierreTest`: **15/15** verde.
- `git diff`: limpio; `git diff --check`: sin observaciones; `git status`: working tree limpio.

### Bloque cerrado

Queda validado el flujo:

**pago parcial → vencimiento → Financiacion → pago posterior → cancelación de financiación → excedente sobre cuota siguiente cuando corresponde.**

No se implementan todavía intereses, TNA, punitorios, CFT ni refinanciación.

### Pendiente

No existe conversión implícita entre la moneda original de una financiación y una liquidación posterior en otra moneda. Antes de modificar este comportamiento debe definirse explícitamente la regla de conversión, cotización y trazabilidad.

### Próximo paso

Si se continúa con Tarjeta de Crédito, reconstruir nuevamente desde GitHub y revisar código, tests y reglas de negocio del caso multidivisa financiación + liquidación antes de proponer cambios.

\n\n## ESTADO CANÓNICO — 19/09/2026

- Rama: `feature/swing-shell`.
- HEAD: `bcb994d` — `fix: aplicar estado de obligacion a todas las ramas del cierre`.
- `main`: `4b8100d`, sincronizada entre GitHub y Bitbucket según verificación local del usuario.
- No hay merge a `main`.
- Última suite local verde conocida: **797/797** sobre `ba2027168`.
- CI de `bcb994d`: **failure** en `Run tests`.

### Próximo paso único

Diagnosticar la falla de CI del HEAD actual y recuperar una suite verde antes de considerar cerrada Tarjeta de Crédito o avanzar a otra funcionalidad.

## ESTADO CANÓNICO DE CONTINUIDAD — 19/09/2026

Esta sección supersede cualquier estado anterior cuando exista contradicción. La fuente de verdad es el código, los tests y los commits actuales de GitHub.

- Rama de trabajo: `feature/swing-shell`.
- HEAD: `bcb994d` — `fix: aplicar estado de obligacion a todas las ramas del cierre`.
- `main`: `4b8100d`; el usuario verificó que `main`, `github/main` y `bitbucket/main` están sincronizadas en ese commit.
- No se realizó merge a `main`.
- Última suite completa local verde conocida: **797/797**, 0 failures, 0 errors, 0 skipped, sobre `ba2027168`, 19/09/2026 11:44 -03:00.
- Después se agregaron nuevos commits; 797/797 no valida el HEAD actual.
- GitHub Actions sobre `bcb994d`: **failure** en `Run tests`.
- El usuario está ejecutando ahora la suite general local y todavía no informó el resultado final.

### Próximo paso único
Diagnosticar el fallo concreto del HEAD actual, distinguir producción/test/entorno, aplicar el cambio mínimo, ejecutar tests específicos y suite completa, y documentar el resultado real. No considerar cerrada Tarjeta de Crédito ni avanzar a otra funcionalidad hasta recuperar una suite verde.


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


## ESTADO CANÓNICO — AUDITORÍA 100% TARJETA — 19/09/2026

La auditoría integral continúa sobre `feature/swing-shell`. Se revisaron código, tests, comparación con `main`, CI y documentación.

Cambios de esta fase:
- pruebas de TNA con cambio de tasa dentro del período;
- prueba de idempotencia del cálculo de intereses;
- prueba de interés sobre capital luego de pago parcial;
- prueba de doble reversión de pago de refinanciación;
- actualización de documentación de tarjeta y auditoría integral.

La ejecución CI anterior sobre `45ece879` falló en compilación, no en una prueba funcional. El error concreto fue una llamada de `PagoTarjetaService` que no coincidía con el constructor vigente de `PagoTarjeta`. La rama actual está tres commits por delante de ese SHA y ya contiene la corrección. La nueva ejecución CI fue iniciada sobre el estado actualizado.

### Gaps que permanecen

1. Motor de intereses/TNA propio de refinanciación: requiere definir modalidad de amortización.
2. Punitorios ligados al pago mínimo: debe integrarse con el historial de pagos; no corresponde aplicar punitorios si el mínimo fue abonado en la fecha correspondiente.
3. Financiación multidivisa seguida de liquidación en otra moneda: falta regla explícita de conversión/cotización.
4. Cancelación anticipada de financiación y API directa de pago de refinanciación pueden omitir el movimiento financiero canónico.
5. Cargos financieros y crédito disponible: falta completar la valorización trazable de cargos posteriores en financiación multidivisa.
6. UI: faltan operaciones de alta y detalle avanzado de financiación/refinanciación.
7. Calendario bancario de feriados, `Clock` y migraciones de esquema.

No se modifica `main` y no se inventan reglas contables donde falta una decisión financiera material.


## ESTADO CANÓNICO — AUDITORÍA 100% TARJETA — 19/09/2026

HEAD posterior a la corrección de integridad: ff2103e381854b165de3f6fa23e5c41510541248 — test: cubrir limites de financiacion de obligacion.

### Corrección más reciente
Se endureció Obligacion.crearFinanciacion(...): capital obligatorio y positivo, y nunca superior al saldo no financiado pendiente. Se agregaron pruebas de nulo/cero y sobre-financiación.

### Estado
La auditoría técnica del módulo continúa sin modificar main. Los flujos de crédito, financiación, refinanciación, pagos y reversiones tienen cobertura específica.

### Pendientes materiales
- modalidad de intereses/amortización de refinanciación;
- conversión trazable de financiación multidivisa cuando la liquidación posterior utiliza otra moneda;
- integración exacta del pago mínimo con punitorios;
- UI avanzada para alta/detalle de financiación y refinanciación.

No se inventa ninguna de estas reglas. Se continúa cerrando todo lo que puede determinarse objetivamente desde el modelo y las reglas ya existentes.

### Validación
La suite completa verde conocida sigue siendo anterior al HEAD actual. GitHub Actions del estado anterior fue relanzado; además, los nuevos commits deberán generar una ejecución propia. La tarjeta no se declara cerrada hasta recuperar BUILD SUCCESS sobre el HEAD final.


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
La suite general ya está verde sobre el estado actual. El próximo paso sigue siendo cerrar la auditoría funcional de Tarjeta de Crédito sin inventar reglas financieras: modalidad de amortización/interés de refinanciación, conversión trazable de financiación multidivisa cuando existe una liquidación posterior en otra moneda, y relación exacta entre pago mínimo y punitorios. También permanece la UI avanzada de financiación/refinanciación y el calendario bancario de feriados.


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


## Auditoría funcional adicional — 20/09/2026

Durante la auditoría se detectaron y corrigieron dos reglas de integridad concretas:

1. **Punitorios:** antes podían comenzar a calcularse desde la fecha de inicio de la financiación, incluso antes del vencimiento de la obligación. Ahora el primer día computable es el día posterior a la fecha límite de pago; un cálculo en la fecha límite se rechaza por no existir mora.
2. **Cuenta pagadora:** una tarjeta de crédito no puede utilizarse como cuenta pagadora de otra obligación de tarjeta. La regla quedó en servicio y el selector Swing también excluye tarjetas de crédito.
3. **Refinanciación:** la fecha de inicio no puede ser anterior a la fecha de origen de la obligación.

Se agregaron tests específicos para las tres reglas. La validación CI de GitHub correspondiente al último cambio estaba **en curso** al momento de esta actualización; por lo tanto no se declara todavía una nueva suite verde posterior a estos cambios.

El alcance de la auditoría mantiene como decisiones de negocio pendientes la modalidad de interés/amortización de refinanciación, la regla exacta de punitorios respecto del pago mínimo y la conversión de una financiación multidivisa seguida de liquidación en otra moneda. No se inventaron esas reglas.


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


## AUDITORÍA FUNCIONAL DE REFINANCIACIÓN — 22/09/2026

### Alcance y estado
Se auditó el módulo de refinanciación sobre el código actual de `feature/swing-shell`, incluyendo `Refinanciacion`, `CuotaRefinanciacion`, `RefinanciacionService`, `PagoTarjetaService`, `Obligacion`, `PagoTarjeta`, financiación/cargos, crédito y los tests relacionados.

### Comportamiento actualmente definido
- La refinanciación toma como capital el valor devuelto por `Obligacion.getDeudaParaPagoMinimo()` en el momento de creación.
- `interesInicial` y `cargosIniciales` forman parte del `totalPlan` desde la creación.
- Las cuotas se generan sobre `totalPlan), con importe a dos decimales y diferencia de redondeo acumulada en la última cuota.
- Los vencimientos se generan sumando meses a `fechaInicio`.
- Los pagos se imputan de la cuota más antigua a la más nueva.
- Las reversiones se imputan desde la cuota más reciente afectada, coherente con la reversión del último pago registrado por `PagoTarjetaService`.
- El pago de refinanciación queda trazado en `PagoTarjeta` y en un `Movimiento` de egreso; su reversión genera el movimiento compensatorio y marca el pago como reversado.
- La obligación de origen pasa a estado `REFINANCIADA` y queda excluida de los cálculos de crédito que ya contemplan el saldo del plan de refinanciación.

### Hallazgo técnico corregido
Se detectó que la entidad permitía llamar a `registrarPago` antes de generar las cuotas. En ese estado el saldo del plan se reducía aunque ninguna cuota recibiera el pago, dejando una inconsistencia interna entre `saldoPlan` y las cuotas. Se corrigió el dominio para exigir cuotas generadas antes de registrar o revertir pagos y se agregaron dos pruebas específicas.

El cambio no introduce ninguna regla financiera nueva: protege una precondición estructural ya necesaria para el flujo existente.

### Intereses, tasa y amortización
La entidad almacena `tasaAnual`, pero no existe actualmente en refinanciación un motor que convierta esa tasa en intereses periódicos ni un campo que permita identificar un sistema de amortización. Por lo tanto, el código actual no permite afirmar que exista sistema francés, alemán, americano u otro.

No se modificó esta parte. Elegir la semántica de `tasaAnual`, la periodicidad, la fórmula de interés o el sistema de amortización requeriría una decisión de negocio explícita.

### Pagos anticipados, parciales y cuotas vencidas
El código existente permite pagos parciales y pagos que abarcan más de una cuota, siempre dentro del saldo total del plan. No existe una política financiera explícita distinta para pago anticipado de cuotas futuras. Tampoco existe en refinanciación una regla propia de punitorios/intereses por vencimiento.

No se agregó una política de imputación o mora nueva para evitar inventar reglas financieras.

### Redondeo
Los importes persistidos de refinanciación y cuotas utilizan `BigDecimal` con escala monetaria de dos decimales. La generación de cuotas evita redondear cada cuota independientemente: divide con `RoundingMode.DOWN` y lleva la diferencia a la última cuota. No se detectó uso de `double` o `float` en el núcleo de refinanciación auditado.

### Decisiones de negocio pendientes
1. Sistema de amortización.
2. Semántica de `tasaAnual` (TNA, TEA u otra).
3. Periodicidad y fórmula de intereses.
4. Tratamiento financiero de pagos anticipados y pagos parciales cuando se defina amortización/interés periódico.
5. Regla específica para cuotas vencidas, si corresponde.
6. Política de cargos adicionales futuros.

### Estado de implementación
La infraestructura actual queda preparada para incorporar esas reglas sin cambiar el flujo transaccional de `PagoTarjetaService`: la refinanciación posee plan, cuotas, saldo, estado y trazabilidad de pagos/reversiones. La implementación financiera avanzada debe agregarse únicamente después de definir las reglas pendientes.
