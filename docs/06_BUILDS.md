# SOFP — Historial de Builds

## Build 057 — Integridad entre operación financiera y movimiento de activo

**Estado: COMPLETADO Y VALIDADO.**

Se reforzó `OperacionFinanciera` para validar la coherencia entre `TipoOperacionFinanciera` y `MovimientoActivo`, y se adaptó la prueba de persistencia para representar explícitamente una operación de `COMPRA`.

Se incorporó:

- Validación de que una `COMPRA` solo admita `MovimientoActivo.COMPRA`.
- Validación de que una `VENTA` solo admita `MovimientoActivo.VENTA`.
- Rechazo de movimientos de activo dentro de una `TRANSFERENCIA`.
- Conservación de las validaciones existentes de asociación y duplicación.
- Adaptación de `OperacionFinancieraRepositoryTest` para persistir un movimiento `COMPRA` dentro de una operación `COMPRA`.
- Validación de persistencia de una compra de 100 unidades a $125, con importe de operación de $12.500.

Pruebas específicas:

- `OperacionFinancieraMovimientoActivoIntegridadTest`: **4/4 tests en verde**.
- `OperacionFinancieraRepositoryTest`: **12/12 tests en verde**.
- Total específico verificado en el bloque: **16 tests en verde**.

Suite general:

- Tests run: **406**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Finalización: **26/08/2026 19:53:45 -03:00**
- Duración: **15:42 min**

Posteriormente, tras adaptar la prueba de persistencia, se ejecutó nuevamente la suite completa:

- Tests run: **406**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Finalización: **26/08/2026 20:38:25 -03:00**
- Duración: **13:00 min**

La segunda ejecución es la validación final utilizada para cerrar este Build.

Commits principales:

- `993e278` — `fix: validar tipo de movimiento de activo en operacion`
- `9a719c8` — `test: adaptar persistencia de movimiento activo a compra`

Validación Git posterior al Build:

- `git status`: working tree limpio en la validación local.
- `git diff --check`: sin errores.
- Rama: `feature/operacion-financiera` sincronizada con GitHub y Bitbucket.

## Build 056 — Incorporación del tipo de operación financiera

**Estado: COMPLETADO Y VALIDADO.**

Se incorporó `TipoOperacionFinanciera` para distinguir explícitamente `TRANSFERENCIA`, `COMPRA` y `VENTA`, y se integró el tipo en `OperacionFinanciera` manteniendo compatibilidad con el comportamiento existente de transferencias.

Se incorporó:

- `TipoOperacionFinanciera` con `TRANSFERENCIA`, `COMPRA` y `VENTA`.
- `OperacionFinanciera` con tipo de operación.
- Compatibilidad del constructor existente, que crea una operación de tipo `TRANSFERENCIA`.
- Constructor explícito para indicar el tipo.
- Rechazo de tipo `null`.
- Tests específicos para transferencia por defecto, compra, venta y tipo nulo.

Pruebas específicas:

- `OperacionFinancieraTest`: **17/17 tests en verde**.

Suite general:

- Tests run: **400**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Finalización: **26/08/2026 18:42:57 -03:00**
- Duración: **16:05 min**

Commits principales:

- `21d8ed9` — `feat: agregar tipo de operacion financiera`
- `3025848` — `feat: incorporar tipo a operacion financiera`
- `70e4584` — `test: cubrir tipo de operacion financiera`

Validación Git posterior al Build:

- `git status`: working tree limpio.
- `git diff --check`: sin errores.
- Rama: `feature/operacion-financiera` sincronizada con GitHub y Bitbucket.

## Build 055 — Refuerzo de integridad de OperacionFinanciera

**Estado: COMPLETADO Y VALIDADO.**

Se reforzó la integridad del dominio de `OperacionFinanciera` para impedir asociaciones incoherentes de movimientos monetarios.

Se incorporó:

- Validación de que el primer movimiento pertenezca a `cuentaOrigen`.
- Validación de que el primer movimiento sea un `EGRESO`.
- Validación de que el segundo movimiento pertenezca a `cuentaDestino`.
- Validación de que el segundo movimiento sea un `INGRESO`.
- Conservación de las reglas existentes de máximo dos movimientos, no duplicación y asociación exclusiva.
- Nuevos tests específicos mediante `OperacionFinancieraIntegridadTest`.
- Adaptación de `OperacionFinancieraTest` a las nuevas reglas de integridad.

Pruebas específicas:

- `OperacionFinancieraTest` + `OperacionFinancieraIntegridadTest`: **18/18 tests en verde**.

Suite general:

- Tests run: **397**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Finalización: **26/08/2026 16:48:26 -03:00**
- Duración: **20:54 min**

Commits principales:

- `3dcadc7` — `fix: reforzar integridad de movimientos de operacion financiera`
- `31277fe` — `test: reforzar integridad de movimientos de operacion financiera`
- `e7c567b` — `test: adaptar OperacionFinancieraTest a reglas de integridad`

Validación Git posterior al Build:

- `git status`: working tree limpio.
- `git diff --check`: sin errores.
- Rama: `feature/operacion-financiera` sincronizada con GitHub y Bitbucket.

## Build 054 — Incorporación del servicio de posición de activo

**Estado: COMPLETADO Y VALIDADO.**

Se incorporó `PosicionActivoService` como coordinación entre `Activo`, `MovimientoActivoRepository` y `CalculadorPosicionActivo`.

La evolución incluyó además la búsqueda de movimientos de un activo y el calculador de posición.

## Build 053 — Incorporación de MovimientoActivo

**Estado: COMPLETADO Y VALIDADO.**

Se incorporó el modelo de movimientos específicos de activos para separar el efecto sobre la tenencia del efecto monetario de `Movimiento`.

Se incorporaron:

- `TipoMovimientoActivo` con `COMPRA` y `VENTA`.
- `MovimientoActivo` con referencia a `Activo`, tipo de movimiento, cantidad y precio unitario.
- `MovimientoActivoRepository`.
- Persistencia JPA de `MovimientoActivo` en la unidad utilizada por los tests.

La cantidad se almacena como valor positivo y el dominio determina su efecto sobre la tenencia: compra positiva y venta negativa.

Pruebas específicas:

- `MovimientoActivoTest`: **11/11 tests en verde**.
- `MovimientoActivoRepositoryTest`: **6/6 tests en verde**.
- Total de tests nuevos: **17**.

Suite general:

- Tests run: **370**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Finalización: **26/08/2026 10:57:39 -03:00**
- Duración: **24:34 min**

## Build 052 — Incorporación de Bono como especialización de Activo

**Estado: COMPLETADO Y VALIDADO.**

Se incorporó `Bono` como primera especialización de `Activo`, manteniendo deliberadamente una definición mínima y sin atributos financieros adicionales.

Pruebas específicas: `BonoTest` **5/5** y `BonoRepositoryTest` **6/6**.

Suite general: **353/353 tests en verde**, `BUILD SUCCESS`.

## Builds anteriores

Los Builds 001–051 permanecen registrados en el historial previo de este documento.

## Estado actual

El último Build cerrado es **Build 057**. La última suite confirmada es **406/406 tests en verde**.

La documentación de continuidad se mantiene exclusivamente en `feature/operacion-financiera`.

## Próximo paso

Implementar progresivamente el caso de uso de compra de activo, comenzando por sus reglas de dominio y tests, manteniendo las transferencias existentes estables. La validación de importe como `cantidad × precioUnitario` deberá integrarse en el núcleo de compra antes de abordar la venta de activos.

## Regla de cierre

Cada Build debe quedar registrado con cambios principales, tests ejecutados, resultado, commit asociado cuando exista y próximo paso. La documentación debe mantenerse sincronizada con el código real y permanecer en la misma rama de trabajo.
