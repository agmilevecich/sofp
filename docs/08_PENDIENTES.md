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


## ACTUALIZACIÓN DE CONTINUIDAD — 19/09/2026 11:44 -03:00

Esta sección supersede las validaciones anteriores cuando exista contradicción.

### Cierre del bloque de financiación de tarjeta

- Rama: `feature/swing-shell`.
- HEAD: `ba2027168bcd172517990cd996aefaad5294da76` — `test: corregir saldo total de obligacion`.
- Comparación con `main`: **927 commits por delante, 0 por detrás**.
- `PagoTarjetaServiceTest`: **15/15** verde.
- `ObligacionServiceCierreTest`: **15/15** verde.
- Suite completa: **797/797** verde, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.
- Validación Git local: `git diff` limpio, `git diff --check` sin observaciones y `git status` limpio.

El bloque de financiación básica de tarjeta queda validado.

### Pendiente funcional inmediato

No se deben agregar todavía intereses, TNA, punitorios, CFT ni refinanciación.

El próximo análisis, si se continúa con tarjetas, debe concentrarse en el caso multidivisa en el que una obligación ya tiene una financiación en moneda original y posteriormente existe una liquidación en otra moneda. Antes de modificar código debe definirse explícitamente la regla de conversión y su trazabilidad; no se debe asumir una conversión implícita.

### Continuidad

Para la próxima sesión: reconstruir nuevamente desde GitHub antes de cualquier cambio y priorizar código y tests sobre documentación histórica.


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
\n\n## PENDIENTES REALES — 19/09/2026

1. Diagnosticar la falla de CI del HEAD `bcb994d`.
2. Revalidar cierre de ciclos/cuotas tras los últimos cambios de estado de obligación.
3. Revalidar financiación multidivisa y valorización.
4. Revalidar TNA/intereses, punitorios y cargos.
5. Revalidar refinanciación y cancelación anticipada.
6. Revalidar pagos, reversiones y trazabilidad.
7. Revalidar panel Swing e integración de tarjetas.
8. Ejecutar suite completa verde y documentar el resultado.

Estos puntos son pendientes de **validación**; no implican que la funcionalidad esté ausente del código.

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

### Pendientes reales después de la validación
1. Definir modalidad de amortización/interés periódico de refinanciación.
2. Definir conversión, cotización y trazabilidad para financiación multidivisa seguida de liquidación en otra moneda.
3. Integrar explícitamente el cumplimiento del pago mínimo con la generación de punitorios.
4. Completar UI avanzada de financiación/refinanciación.
5. Incorporar calendario bancario de feriados si se requiere ese comportamiento.
6. Introducir `Clock` para determinismo temporal.
7. Evaluar migraciones/versionado formal de esquema.
8. Estabilización de arranque/parada H2 antes del futuro fast-forward a `main`.

Estos puntos no implican fallos en la suite actual; son decisiones o evoluciones todavía no cerradas.


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

