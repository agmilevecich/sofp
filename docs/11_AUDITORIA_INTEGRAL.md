# SOFP — Auditoría integral del estado técnico

## Estado — 17/09/2026

Esta auditoría reconstruye el estado de `feature/swing-shell` desde GitHub. La fuente de verdad es el código y los tests; la documentación se utiliza para contrastar continuidad y detectar información obsoleta.

## 1. Estado de Git y continuidad

- Rama estable: `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
- Rama de trabajo: `feature/swing-shell` → último commit `2a789c10e5399afc799d9f2cc5477e74ef799413`.
- La rama de trabajo continúa separada de `main`; no se realizó merge.
- Últimos commits funcionales del bloque: `c592cbc` (`fix: calcular credito sobre saldo de liquidacion`), `20bb282` (`test: cubrir credito liberado tras liquidacion multidivisa`) y `2a789c1` (`test: persistir tipo de cambio de liquidacion`).

## 2. Validación actual

- `CuentaServiceCreditoTest`: **3/3**, 0 failures, 0 errors, 0 skipped.
- `TarjetaCreditoPagoCreditoTest`: **5/5**, 0 failures, 0 errors, 0 skipped.
- `mvn test`: **756/756**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.
- Suite finalizada: **17/09/2026 15:50:11 -03:00**.

La auditoría registra resultados informados por el usuario; no vuelve a ejecutar tests.

## 3. Arquitectura actual

La estructura real contiene dominio, persistencia, servicios y Swing. La separación general observada es:

`UI → servicios → dominio/repositorios → JPA/H2`.

Los servicios coordinan operaciones transaccionales y autorización; las entidades conservan reglas propias del dominio.

No se observa una necesidad de rehacer la arquitectura para el próximo bloque.

## 4. Dominio financiero

El núcleo actual incluye `Usuario`, `PerfilFinanciero`, `Moneda`, `Cuenta`, `Movimiento`, categorías, operaciones financieras, posiciones de activos, obligaciones y cuotas.

La integridad histórica de cuentas y de movimientos origen de obligaciones está protegida por servicio y cubierta por tests.

## 5. Moneda y multidivisa

`Movimiento` conserva una moneda económica explícita y puede diferir de la moneda de la cuenta. La solución actual mantiene:

1. `CuentaService` calcula el saldo de una cuenta usando la moneda de la cuenta.
2. `MovimientoService` valida disponibilidad de fondos usando la moneda del movimiento.
3. `Obligacion` separa moneda original y moneda de liquidación.
4. `TipoCambio` permite valorización de cierre y liquidación histórica explícita y trazable.
5. `saldoLiquidacion` representa la deuda en moneda de liquidación.
6. `PagoTarjetaService` aplica pagos a ese saldo cuando corresponde.
7. El crédito utilizado distingue entre saldo pendiente antes de liquidar y saldo de liquidación después de liquidar.

No se deben introducir conversiones implícitas. La liquidación conserva la cotización histórica utilizada.

## 6. Pagos de tarjeta

`PagoTarjetaService` coordina en una transacción la autorización, cuenta pagadora, categoría, moneda, saldo, fondos, fecha y registro del egreso/pago.

Para obligaciones liquidadas exige que la cuenta pagadora coincida con `monedaLiquidacion` y aplica el pago mediante `registrarPagoLiquidacion`. Para obligaciones no liquidadas conserva el flujo de `saldoPendiente`/`registrarPago`.

## 7. Crédito de tarjeta

El cálculo de crédito utilizado contempla la etapa real de la obligación:

- obligación en moneda de la tarjeta antes de liquidar → `saldoPendiente`;
- obligación multidivisa antes de liquidar → valorización de cierre proporcional al saldo original pendiente, cuando existe;
- obligación después de liquidar → `saldoLiquidacion`;
- consumo sin obligación asociada → comportamiento existente.

Esto evita doble contabilización del saldo original trasladado a liquidación y permite liberar completamente el crédito cuando la deuda de liquidación queda cancelada.

## 8. Tarjetas, ciclos y temporalidad

El bloque temporal está cerrado:

- ciclo histórico persistido en `Obligacion`;
- fechas históricas de `Cuota` persistidas;
- vencimiento de sábado/domingo desplazado al lunes;
- días de gracia;
- mora basada en vencimiento efectivo más gracia;
- rechazo de pagos anteriores al consumo;
- rechazo de pagos futuros;
- pagos parciales en orden de cuotas;
- compatibilidad de registros antiguos mediante nullable/fallback.

No están implementados feriados, fecha efectiva separada ni intereses/punitorios/CFT/refinanciación.

## 9. Integridad histórica

Una cuenta con movimientos no puede cambiar de tipo ni moneda. La API genérica tampoco permite convertir una cuenta existente hacia/desde `TARJETA_CREDITO`.

El movimiento origen de una obligación queda protegido frente a modificaciones estructurales incompatibles y eliminación.

Sigue pendiente definir una política explícita para eliminar cuentas que ya poseen historial financiero.

## 10. Robustez de Moneda

`Moneda.cantidadDecimales` valida:

- `null` → `NullPointerException`;
- valor negativo → `IllegalArgumentException`;
- valor no negativo → permitido.

No se agregó un límite superior arbitrario.

## 11. Autorización y aislamiento

La autorización por `usuarioId` y el aislamiento por perfil/usuario permanecen integrados en las operaciones financieras sensibles. No se identificó un nuevo bypass en el trabajo actual.

## 12. Persistencia

La aplicación utiliza JPA/Hibernate con H2 TCP y `hibernate.hbm2ddl.auto=update`. Los tests utilizan un contexto separado con H2 en memoria.

`update` sigue siendo adecuado para desarrollo actual, pero no constituye un mecanismo formal de migraciones/versionado para una futura etapa de distribución.

## 13. UI Swing

La UI contiene shell y paneles para cuentas, categorías, ingresos, gastos, movimientos, inversiones y obligaciones. El cierre de ciclo desde `ObligacionesPanel` y el pago de tarjeta están integrados y cubiertos por tests.

Pendiente: UI específica de tarjetas para límite/disponible, consumos, valorizaciones de cierre, ciclos, cierres, vencimientos, deuda y pagos.

## 14. Financiación

Las cuotas simples sin interés están implementadas. Siguen fuera del alcance actual intereses, CFT, cuotas variables, adelantos, refinanciación, anulaciones/reversiones y ajustes.

## 15. Determinismo temporal

`PagoTarjetaService` utiliza `LocalDateTime.now()` para rechazar fechas futuras. La regla es correcta, pero una futura abstracción `Clock` permitiría tests más deterministas.

## 16. Clasificación actual

### Bloque actual — cierre y crédito multidivisa

El cálculo de crédito después de liquidación ya está implementado y validado. El siguiente trabajo es de definición de negocio y flujo de cierre de resumen, no una corrección pendiente del cálculo ya probado.

### P2 — robustez

- política de eliminación de cuentas con historial;
- `Clock`;
- migraciones/versionado formal de esquema.

### P3 — evolución

- financiación avanzada;
- UI específica de tarjetas;
- pasivos/patrimonio/análisis;
- gestión de entidades financieras;
- pulido de consola.

## 17. Próximo paso

Revisar `ObligacionService` y reconstruir el flujo de cierre de resumen de tarjeta. Antes de cambiar código, contrastar el comportamiento buscado con normativa BCRA y documentación vigente de la entidad financiera de referencia, especialmente respecto de la cotización aplicada a consumos extranjeros al cierre y la relación entre valorización, liquidación y pago.

No fijar una nueva regla de negocio por inferencia: primero documentar la regla y luego cubrirla con tests antes de implementarla.

## 18. Conclusión

La auditoría actual confirma que el núcleo multidivisa está consolidado: moneda original y de liquidación separadas, cotizaciones históricas trazables, valorización de cierre independiente, liquidación explícita, pagos en la moneda correspondiente y cálculo de crédito coherente antes y después de liquidar.

La suite completa conocida y validada es **756/756**, con 0 failures, 0 errors y 0 skipped. El próximo bloque es definir y modelar correctamente el comportamiento de cierre de resumen de tarjeta, comenzando por `ObligacionService`.


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

## Actualización — auditoría BCRA de tarjetas — 18/09/2026

Se revisó nuevamente el flujo de tarjeta de crédito con foco en consumos en moneda extranjera, cierre, liquidación, pagos, crédito disponible, ciclos y temporalidad.

### Hallazgo corregido

`TipoCambioRepository.buscarPorMonedasYFechaHora` antes podía reutilizar silenciosamente una cotización de días anteriores cuando no existía una cotización del día de cancelación. Eso no representa correctamente la regla BCRA para un día hábil: la referencia es el momento de cancelación. Ahora:

- día hábil: solo se acepta una cotización del mismo día y hasta la hora de cancelación;
- sábado/domingo: se utiliza la última cotización del viernes anterior;
- si no existe una cotización aplicable, la liquidación se rechaza en lugar de inventar/reutilizar una cotización anterior;
- las liquidaciones anteriores al consumo y futuras son rechazadas;
- la valorización de cierre sigue separada de la liquidación efectiva.

La Comunicación A 8307 del BCRA permite cancelar consumos en moneda extranjera en esa moneda o en pesos y, cuando se cancela en pesos, establece como máximo el tipo de cambio vendedor correspondiente al momento de cancelación, o al día hábil inmediato anterior cuando el pago ocurre en un día inhábil. Para débito automático en cuentas de la propia entidad existe una regla específica de cierre del día hábil del pago.

### Cobertura agregada

Se agregaron pruebas para cancelación en sábado, ausencia de cotización del día hábil y validaciones temporales. La ejecución local de estas pruebas y de la suite completa queda pendiente de ser informada; no se registra ningún resultado no ejecutado.

### Limitación explícita

El código no infiere feriados argentinos a partir del calendario semanal. Para automatizar correctamente cualquier día inhábil distinto de sábado/domingo deberá incorporarse un calendario bancario explícito y actualizado. Hasta entonces, la operación se rechaza si no existe una cotización aplicable al día seleccionado.


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


## ACTUALIZACIÓN — AUDITORÍA 100% TARJETA DE CRÉDITO — 19/09/2026

La revisión integral actual confirma que el núcleo de tarjeta está implementado en consumo, obligación, ciclos, cuotas, valorización de cierre, liquidación multidivisa, pagos, financiación, refinanciación, crédito disponible y UI de consulta.

Durante la auditoría se agregaron pruebas de TNA para cambios de tasa dentro del mismo período, límites de vigencia, cálculo sobre capital parcialmente pagado y rechazo de recalcular dos veces el mismo período. También se agregó una prueba de protección contra doble reversión de un pago de refinanciación.

La ejecución remota que falló sobre el commit anterior fue diagnosticada: GitHub Actions no llegó a ejecutar tests; falló en compilación porque ese commit tenía una llamada a `PagoTarjeta` incompatible con el constructor vigente. Los commits posteriores de `feature/swing-shell` ya contienen la corrección de esa llamada. La nueva ejecución de CI correspondiente al estado actualizado quedó iniciada y debe ser tomada como fuente de validación, no la ejecución fallida anterior.

### Hallazgos abiertos

- Refinanciación sin motor de intereses/TNA periódico: falta definir modalidad de amortización.
- Punitorios no condicionados explícitamente al cumplimiento del pago mínimo. La normativa vigente establece que no corresponde aplicar punitorios cuando se efectuó el pago mínimo en la fecha correspondiente. citeturn5search2
- Caso financiación multidivisa seguida de liquidación en otra moneda: falta una regla explícita de conversión, cotización y trazabilidad.
- Cancelación anticipada de financiación y pago directo de refinanciación todavía tienen APIs que pueden modificar deuda sin movimiento financiero si se invocan fuera del flujo canónico.
- Cargos de financiación: revisar su impacto en crédito disponible y su valorización cuando la financiación es multidivisa.
- UI específica: faltan operaciones de alta de financiación/refinanciación y detalle completo de cargos/TNA/punitorios/cuotas refinanciadas.
- Calendario bancario de feriados, `Clock` y versionado formal de esquema siguen pendientes.

### Conclusión

No se declara cerrada Tarjeta de Crédito todavía. No se requiere modificar `main`. Los hallazgos de reglas financieras quedan documentados sin inventar una política contable; los hallazgos puramente técnicos continúan cerrándose sobre `feature/swing-shell`.


## ACTUALIZACIÓN — AUDITORÍA 100% — CIERRE TÉCNICO DE FINANCIACIÓN — 19/09/2026

La revisión sobre el HEAD actual agregó una validación de integridad que faltaba en el modelo: una obligación no puede acumular capital financiado por encima de su saldo no financiado pendiente. Esto evita sobre-financiación y doble contabilización desde la API de dominio, no solamente desde el flujo de servicio.

### Cambio realizado
- Obligacion.crearFinanciacion(...) ahora exige capital positivo y limita el capital nuevo al saldo no financiado pendiente.
- Se mantienen las reglas específicas para financiación sobre liquidación.
- Se agregaron pruebas para capital nulo/cero y para intento de segunda financiación que supera el saldo disponible.

### Estado de la auditoría
El núcleo funcional auditado comprende consumo, límite, crédito disponible, ciclos, cuotas, cierre, valorización, liquidación multidivisa, pagos, reversión, financiación, cargos financieros, TNA, punitorios, pago mínimo, refinanciación y UI de consulta. Los flujos financieros canónicos de pago/reversión permanecen en PagoTarjetaService.

### Pendientes que no se resuelven por inferencia
1. Refinanciación: falta definir modalidad de amortización/interés periódico. La entidad almacena tasaAnual, pero no alcanza para inferir si corresponde sistema francés, interés simple u otra modalidad.
2. Financiación multidivisa seguida de liquidación posterior en otra moneda: falta una regla explícita de conversión, cotización y trazabilidad.
3. Punitorios: el sistema todavía debe integrar el cumplimiento del pago mínimo del resumen con la generación de punitorios. La información oficial argentina indica que pagar al menos el mínimo evita la mora/punitorios, mientras el saldo restante puede generar intereses compensatorios. Fuente: Ley 25.065 / Argentina.gob.ar.
4. UI avanzada: la pantalla de tarjetas permite consulta y pago, pero no expone todavía de forma completa la creación/detalle avanzado de financiación y refinanciación.
5. Calendario bancario de feriados, Clock y migraciones formales siguen siendo mejoras técnicas pendientes.

### Validación
El último resultado local verde informado anteriormente no corresponde al HEAD actual. GitHub Actions sobre el estado previo falló en Run tests; se relanzó esa ejecución para separar fallo transitorio de fallo reproducible. Después de los commits de esta auditoría debe existir una nueva ejecución de CI antes de declarar la rama verde.

### Criterio de cierre
Tarjeta de Crédito no se declara cerrada hasta disponer de suite completa verde sobre el HEAD final y resolver o documentar explícitamente los tres puntos financieros que requieren decisión de negocio: modalidad de refinanciación, conversión de financiación multidivisa y regla exacta de punitorios/pago mínimo.


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


## ACTUALIZACIÓN CANÓNICA — CIERRE TÉCNICO DE AUDITORÍA DE TARJETAS — 22/09/2026

Esta sección supersede las conclusiones anteriores cuando exista contradicción y toma como fuente de verdad el código, los tests y los commits de `feature/swing-shell`.

### Estado Git

- Rama de trabajo: `feature/swing-shell`.
- Último commit verificado: `6980f2da230c8fd3f8beabbe459e86454d55d271` — `test: seleccionar pago financiado correcto`.
- `main`: la comparación vigente muestra que la rama de trabajo continúa separada de `main`; no se realizó merge.
- El commit final verificado no modifica producción: hace determinista la consulta del test seleccionando el último `PagoTarjeta` mediante `ORDER BY p.id DESC` y `setMaxResults(1)`.

### Validación final informada por el usuario

- `mvn -e test`: **848/848 tests**, 0 failures, 0 errors, 0 skipped.
- `BUILD SUCCESS`.
- Finalizado: **22/09/2026 14:32:59 -03:00**.
- La ejecución anterior de 848 tests que falló por `NonUniqueResult` quedó explicada por el propio test: la obligación tenía dos pagos y la consulta usaba `getSingleResult()` sin seleccionar cuál correspondía. La corrección posterior no cambia la regla financiera; solamente hace explícita la selección del pago financiado recién registrado.

### Cierre técnico

Con la suite completa verde sobre el HEAD final, quedan cerrados los hallazgos técnicos tratados en esta auditoría: trazabilidad de pagos financiados, centralización de las mutaciones financieras en `PagoTarjetaService`, validaciones temporales de reversión, reglas de cuenta pagadora, fechas de refinanciación, punitorios desde la mora, límites de financiación y separación de capital/valorización en financiaciones multidivisa.

Por lo tanto, **la auditoría técnica del núcleo de Tarjeta de Crédito queda cerrada y validada** sobre `feature/swing-shell`.

### Decisiones de negocio que permanecen como evolución futura

El cierre técnico no implica que todas las funcionalidades financieras posibles estén definidas. Permanecen como trabajo futuro, y no deben resolverse por inferencia:

1. modalidad de amortización e intereses periódicos de refinanciación;
2. regla explícita de conversión, cotización y trazabilidad cuando una financiación multidivisa se liquida posteriormente en otra moneda;
3. integración detallada del pago mínimo con punitorios e intereses compensatorios;
4. calendario bancario de feriados más allá de sábado/domingo;
5. UI avanzada para alta y detalle de financiación/refinanciación;
6. adopción de `Clock` y versionado formal del esquema.

Estos puntos no se consideran fallos del núcleo auditado mientras no exista una regla de negocio definida que el código contradiga.

### Próximo paso

No realizar cambios adicionales de Tarjeta de Crédito por la sola auditoría. El siguiente trabajo debe ser una funcionalidad nueva o una de las decisiones de negocio pendientes, con regla explícita, tests y cambio mínimo. 


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

## Implementación de amortización de refinanciación — 22/09/2026

Se implementó sobre la rama `feature/swing-shell` la primera etapa de las reglas financieras definidas para refinanciación:

- sistema de amortización francés;
- `tasaAnual` interpretada como TNA;
- periodicidad mensual;
- tasa periódica = TNA / 12;
- interés de cada período calculado sobre el saldo de capital al inicio del período;
- primer período desde `fechaInicio` hasta `fechaInicio + 1 mes`;
- redondeo monetario a 2 decimales;
- ajuste de la última cuota para cancelar exactamente el capital pendiente;
- las cuotas conservan el desglose entre interés y capital amortizado.

Para esta implementación, el saldo financiado del sistema francés es `totalPlan`, que actualmente se compone de capital original + interés inicial + cargos iniciales. Esto significa que esos importes forman parte del saldo sobre el cual se genera el plan francés.

Ejemplo de referencia implementado en tests: total plan $127.000, TNA 24%, 3 cuotas mensuales:
- cuota 1: $44.037,84 = $2.540,00 de interés + $41.497,84 de capital;
- cuota 2: $44.037,84 = $1.710,04 de interés + $42.327,80 de capital;
- cuota 3: $44.037,85 = $863,49 de interés + $43.174,36 de capital.

La implementación agrega el desglose `interes` y `capitalAmortizado` a `CuotaRefinanciacion` y exige TNA al momento de generar las cuotas.

Pendiente de auditoría posterior: definir y probar explícitamente el tratamiento de pagos anticipados sobre el plan francés, pagos vencidos y eventuales intereses/cargos por mora. Esta implementación no introduce reglas de mora ni cambia todavía la política de pagos/reversiones existente.



## ACTUALIZACIÓN DE CONTINUIDAD — CIERRE 23/09/2026

Esta sección supersede las validaciones anteriores cuando exista contradicción. La fuente de verdad continúa siendo el código, los tests y GitHub.

### Estado Git
- Rama de trabajo: `feature/swing-shell`.
- HEAD: `0550d6248b12ecf2a8019d1328a8def430062c2b` — `test: corregir credito esperado de pago de refinanciacion`.
- `main`: `a23d3a5c0658ffbca93391c34f79ad8bc37fdc10`.
- La comparación reconstruida previamente desde GitHub indica que la feature está 23 commits por delante de `main` y 0 por detrás.
- No se realizó merge a `main`.

### Último bloque cerrado
La semántica actual de `Refinanciacion.saldoPlan` incluye el interés programado de las cuotas generadas. Por ello se corrigieron únicamente expectativas de tests que todavía utilizaban el total inicial del plan como si fuera el saldo posterior a generar cuotas.

Cambios de tests realizados:
- `ObligacionRepositoryFinanciacionCreditoTest`: expectativa inicial del crédito de refinanciación ajustada a `114429.04`.
- `PagoTarjetaServiceTest`: expectativas ajustadas de `424000.00` a `418926.73` y de `374000.00` a `368926.73`.
- No se modificó código de producción en este bloque.

### Validación
- Test específico de `ObligacionRepositoryFinanciacionCreditoTest`: **1/1**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.
- Bloque relacionado de persistencia/financiación/refinanciación: **24/24**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.
- `PagoTarjetaServiceTest`: **23/23**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.
- Suite completa `mvn test`: **861/861**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.
- Suite completa finalizada: **23/09/2026 20:29:58 -03:00**.

### Artefactos de depuración eliminados
El archivo local `salida-refinanciacion.txt` y la rama `debug/refinanciacion-errores` fueron utilizados exclusivamente para diagnosticar los fallos de refinanciación y posteriormente eliminados. No forman parte del trabajo pendiente ni deben recuperarse.

### Punto exacto para retomar
La suite completa está verde. El siguiente trabajo sigue siendo cerrar el invariante de refinanciación entre `saldoPlan` y el estado/saldo de sus cuotas, especialmente para pagos parciales, pago total, reversión y cancelación anticipada. Antes de modificar producción debe revisarse nuevamente el código actual y los tests relacionados desde GitHub.

### Regla de continuidad
La próxima sesión debe reconstruir desde GitHub: rama → últimos commits → comparación con `main` → código relacionado → tests → documentación → último resultado informado → cambio mínimo. No asumir que documentación histórica representa el estado actual si contradice código o tests.


## ACTUALIZACIÓN DE CONTINUIDAD — 24/09/2026 12:46 -03:00

Esta sección supersede cualquier estado anterior de este documento cuando exista contradicción. La fuente de verdad continúa siendo el código, los tests y GitHub.

### Estado Git reconstruido

- Rama de trabajo: `feature/swing-shell`.
- HEAD: `dcb85822199639819a02d81a5d55753f292e4617` — `fix: bloquear toda mutacion independiente de operaciones financieras`.
- `main`: `a23d3a5c0658ffbca93391c34f79ad8bc37fdc10`.
- Comparación actual desde GitHub: la feature está **35 commits por delante de `main` y 0 por detrás**.
- No se realizó merge a `main`.

### Cierre del bloque de operaciones financieras coordinadas

Se completó la protección contra mutación independiente de movimientos pertenecientes a una `OperacionFinanciera`.

La protección quedó aplicada en dos niveles:

- `Movimiento` rechaza modificaciones estructurales de importe y tipo cuando está asociado a una operación financiera.
- `MovimientoActivo` rechaza modificaciones de tipo, cantidad y precio unitario cuando está asociado a una operación financiera.
- `MovimientoService` rechaza modificación o eliminación independiente de un movimiento asociado a una operación financiera, incluyendo descripción, observaciones, categoría, tipo, importe y fecha.

La intención es impedir que una sola pata de una operación coordinada quede modificada o eliminada aisladamente y rompa la consistencia de la operación completa.

### Validación informada por el usuario

Después de los últimos cambios se ejecutaron:

- `MovimientoServiceTest`: **55/55**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.
- Bloque conjunto `OperacionFinancieraTest`, `OperacionFinancieraServiceTest` y `MovimientoServiceTest`: **101/101**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.
- Última ejecución finalizada: **24/09/2026 12:46:37 -03:00**.

Estos resultados son ejecuciones locales informadas por el usuario y no deben confundirse con una ejecución de GitHub Actions.

### Refinanciación

La refinanciación queda con cobertura funcional relevante, incluyendo generación de cuotas, sistema francés, pagos, reversiones, persistencia y protección de precondiciones. El bloque específico de persistencia/financiación/refinanciación informado previamente quedó verde y la suite completa conocida llegó a **861/861** el 23/09/2026.

La documentación histórica contiene distintas descripciones de la semántica de `saldoPlan`. Para continuidad, prevalece la implementación y los tests actuales. Cualquier cambio adicional sobre la semántica financiera debe partir de una nueva inspección del código actual y de los invariantes cubiertos por tests.

### Próximo bloque de auditoría

El siguiente hallazgo prioritario es **validar la posición disponible de un activo antes de permitir una venta**. Antes de modificar producción deben revisarse desde GitHub `OperacionFinancieraService`, `MovimientoActivo`, `PosicionActivo`, repositorios relacionados y sus tests para identificar la fórmula de posición que ya utiliza SOFP.

Después quedan como pendientes de alta prioridad:

1. compatibilidad de moneda entre cuenta y activo/precio en compras;
2. disponibilidad histórica de una cuenta considerando únicamente movimientos hasta la fecha de la operación;
3. suite completa después de cerrar estos invariantes;
4. patrimonio neto y reportes consolidados;
5. UI avanzada y endurecimiento técnico.

### Regla de continuidad

Cada nueva sesión debe reconstruir desde GitHub: rama → últimos commits → comparación con `main` → código relacionado → tests → documentación → último resultado informado → próximo cambio mínimo. La documentación es auxiliar y nunca reemplaza al código ni a los tests.


## ACTUALIZACIÓN CANÓNICA — CIERRE DE CONTINUIDAD — 24/09/2026 21:11 -03:00

Esta sección supersede cualquier estado anterior de este documento cuando exista contradicción. La fuente de verdad es el código y los tests actuales de `feature/swing-shell`; la documentación histórica queda como registro.

### Estado Git reconstruido desde GitHub

- Rama de trabajo: `feature/swing-shell`.
- HEAD verificado: `b57cb2709262352f492ad81c75cde7f6a879df27` — `test: cubrir saldo historico por la ruta publica`.
- `main`: `a23d3a5c0658ffbca93391c34f79ad8bc37fdc10`.
- Comparación GitHub: `feature/swing-shell` está **61 commits por delante de `main` y 0 por detrás**.
- No se realizó merge a `main`.

### Bloques técnicos cerrados desde la actualización anterior

Durante esta etapa se cerraron y validaron los siguientes invariantes:

1. **Operaciones financieras coordinadas.**
   Los movimientos asociados a una `OperacionFinanciera` ya no pueden modificarse ni eliminarse de forma independiente desde `MovimientoService`. También se protegieron las modificaciones estructurales de `Movimiento` y `MovimientoActivo` asociadas a operaciones coordinadas.

2. **Venta de activos.**
   La venta valida la posición disponible antes de registrar la operación. Los escenarios de venta requieren una posición previa coherente y están cubiertos por tests.

3. **Compatibilidad de moneda en operaciones de inversión.**
   Las operaciones de compra y venta validan la compatibilidad entre la moneda de la cuenta y la del activo. No se introduce conversión implícita entre monedas.

4. **Saldo histórico de cuentas.**
   La validación de disponibilidad para una operación fechada utiliza únicamente los movimientos de la cuenta hasta la fecha/hora de esa operación. Se agregó `MovimientoRepository.listarPorCuentaHastaFecha(...)` y la validación correspondiente en `MovimientoService`.
   
   Caso regresivo cubierto: un ingreso posterior no puede financiar retrospectivamente un egreso histórico.

5. **Refinanciación.**
   Continúa cubierta la generación de cuotas, amortización definida, pagos, reversiones, persistencia y efectos sobre crédito. Los últimos ajustes de esta etapa fueron sobre expectativas y escenarios de saldo/crédito; no se declara una nueva regla financiera sin una decisión explícita.

### Validación actual

La ejecución completa más reciente informada por el usuario es:

- `mvn test`
- **883 tests**
- **0 failures**
- **0 errors**
- **0 skipped**
- **BUILD SUCCESS**
- duración: **31:37 min**
- finalización: **24/09/2026 21:11:38 -03:00**

Esta ejecución fue local e informada por el usuario. No debe confundirse con una ejecución de GitHub Actions.

Validaciones relacionadas inmediatamente anteriores:

- refinanciación: **34/34** verdes;
- `MovimientoRepositoryTest` + `MovimientoServiceTest`: **62/62** verdes;
- operaciones financieras relacionadas: **78/78** verdes.

El resultado 883/883 es actualmente el último resultado de suite completa conocido y sustituye los resultados históricos 860/860, 861/861, 841/841, 848/848 y anteriores.

### Estado de documentación

La documentación histórica conserva las auditorías y decisiones anteriores para trazabilidad. Esta sección es la referencia de continuidad vigente y debe prevalecer sobre cifras, HEADs o pendientes contradichos por el código/tests actuales.

No se debe interpretar que todos los pendientes históricos siguen abiertos: los invariantes de operaciones coordinadas, posición de activos, moneda de inversión y saldo histórico ya fueron implementados y validados.

### Pendientes reales para la próxima etapa

Quedan como líneas de evolución, sujetas a revisión del código actual antes de cada cambio:

1. patrimonio neto y reportes financieros consolidados;
2. ampliar la UI Swing para exponer funcionalidades avanzadas que ya existen en dominio/servicios;
3. continuar la auditoría de temporalidad de posiciones de activos, si la regla de negocio exige una posición histórica a una fecha determinada;
4. revisar escenarios multidivisa que todavía requieran una regla financiera explícita;
5. endurecimiento técnico: `Clock`, calendario bancario de feriados y migraciones/versionado formal de esquema;
6. mejoras de rendimiento de operaciones JPA ejecutadas desde Swing.

Los puntos financieros que no estén definidos explícitamente no deben resolverse por inferencia.

### Próximo criterio de trabajo

Antes de cualquier nuevo cambio:

`GitHub → rama → últimos commits → comparación con main → código relacionado → tests → documentación → último resultado → cambio mínimo`

Después de cambios importantes:

`tests específicos → tests relacionados → suite completa → git diff → git diff --check → git status → documentación`

La rama de trabajo continúa siendo `feature/swing-shell`; no se debe modificar ni mergear `main` automáticamente.
