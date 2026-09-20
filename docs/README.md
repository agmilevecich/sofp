# SOFP — Documentación

La documentación acompaña al código, pero la fuente de verdad es siempre el estado actual de Git, el código y los tests.

## Continuidad actual

- `CHAT_CONTEXT.md`: contexto completo para nuevas conversaciones.
- `CHAT_CONTEXT_FINAL.md`: contexto compacto de continuidad.
- `CHAT_CONTEXT_NEW.md`: contexto actualizado para iniciar una nueva conversación.
- `00_ESTADO_ACTUAL.md`: estado funcional y técnico vigente.
- `05_DECISIONES.md`: decisiones arquitectónicas y de negocio permanentes.
- `06_BUILDS.md`: historial de Builds y validaciones.
- `07_TESTS.md`: estado y cobertura de tests.
- `08_PENDIENTES.md`: pendientes reales y orden de ejecución.
- `09_HISTORIAL_PROYECTO.md`: evolución e hitos.
- `09_TARJETAS_CREDITO.md`: diseño, estado y auditoría de tarjetas.
- `CONTINUIDAD_2026-09-15.md`: corte de continuidad más reciente.
- `CONTINUIDAD_2026-09-12.md` y cortes anteriores: antecedentes históricos.

## Estado vigente — 15/09/2026

Rama de trabajo: `feature/swing-shell`.

Último commit de código validado: `e95585e043290eebb5789f2b628b1edcef8a7344` — `test: cubrir pagos multidivisa en PagoTarjetaService`.

`main` → `a4be85913847200cb70976d5266d9cbba10b3100`.

Suite general actual: **718/718**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizada **15/09/2026 20:05:19 -03:00**.

## Estado multidivisa

La integración del pago multidivisa está implementada:

- `Obligacion` separa moneda original y moneda de liquidación;
- `TipoCambio` representa cotización histórica;
- `Obligacion` conserva la cotización utilizada;
- `saldoLiquidacion` representa la deuda en moneda de liquidación;
- `PagoTarjetaService` paga ese saldo cuando existe;
- la cuenta pagadora debe estar en la moneda de liquidación;
- no hay conversiones implícitas.

Queda pendiente definir el impacto de consumos extranjeros sobre el límite/crédito disponible y completar la cobertura de persistencia/UI del pago multidivisa.

## Regla de continuidad

Antes de cualquier cambio reconstruir desde GitHub: rama → commits → comparación con `main` → documentación → código → tests → último resultado conocido → próximo paso.

No modificar `main`, no asumir resultados locales no informados y no considerar cerrado un bloque solamente porque compila.


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


## Estado canónico de continuidad — 20/09/2026

Para retomar SOFP, la referencia vigente es la documentación de continuidad actualizada junto con el estado de GitHub de la rama `feature/swing-shell`.

- HEAD validado: `4edd0fd62db75bfab25b3124169d9e43f42ebf88`.
- Suite general: **841/841**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.
- No se realizó merge a `main`.
- Las notas fechadas anteriores son historial y no sustituyen al código, tests y commits actuales.

Documentos principales para continuidad: `CONTINUIDAD_ACTUAL.md`, `00_ESTADO_ACTUAL.md`, `08_PENDIENTES.md`, `09_TARJETAS_CREDITO.md`, `11_AUDITORIA_INTEGRAL.md`, `06_BUILDS.md` y `07_TESTS.md`.
