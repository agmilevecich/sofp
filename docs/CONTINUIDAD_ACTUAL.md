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
