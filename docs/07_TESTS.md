# SOFP — Tests

Este documento registra la evolución de la batería de tests y los resultados verificados.

## Validación global más reciente

Suite completa ejecutada desde IntelliJ IDEA el **28/08/2026 19:56:00 -03:00**.

- Tests run: **455**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- Resultado: **BUILD SUCCESS**
- Duración: **11:24 min**

Esta es la suite general más reciente confirmada y debe considerarse la validación global vigente.

## Valorización de posición activa

Se incorporó `ValorizacionPosicionActivo` para calcular el valor actual, la ganancia o pérdida y el rendimiento porcentual de una posición a partir de un precio actual informado.

Tests específicos de `ValorizacionPosicionActivoTest`:

- cálculo de valor actual, ganancia y rendimiento;
- cálculo de pérdida;
- posición cerrada;
- precio actual cero;
- posición nula;
- precio actual nulo;
- precio actual negativo;
- ausencia de costo de adquisición.

Resultado: **8/8 tests en verde**.

Commits:

- `ef19486` — `feat: agregar valorizacion de posicion activa`
- `7379570` — `test: cubrir valorizacion de posicion activa`

La feature fue integrada en `main` mediante fast-forward.

## Costo promedio de posición activa

Se incorporó el cálculo del costo de adquisición acumulado, precio promedio y costo remanente después de ventas.

Tests específicos de `PosicionActivoTest`: **8/8 tests en verde**.

Commits principales:

- `da09ef0` — `feat: calcular costo promedio de posicion activa`
- `6cb038b` — `test: cubrir costo promedio de posicion activa`

La feature fue integrada en `main` mediante fast-forward.

## Cartera de activos

Tests específicos de `CarteraActivoServiceTest`: **5/5 tests en verde**.

La etapa quedó integrada en `main` mediante fast-forward.

## Identificación por símbolo

Se incorporó búsqueda de activos por símbolo y posteriormente búsqueda de bonos por símbolo.

- `ActivoRepository` — búsqueda por símbolo implementada y cubierta por test.
- `BonoRepository` — búsqueda por símbolo implementada y cubierta por test.
- Los tests cubren el rechazo de `null` en las búsquedas por símbolo.
- La persistencia rechaza símbolos duplicados tanto para `Activo` como para `Bono`.

La etapa quedó integrada en `main` y validada.

## Adaptación al símbolo obligatorio

Se adaptaron los constructores y tests afectados por el nuevo atributo identificador. La cobertura quedó integrada en la suite general.

## Builds anteriores

Los Builds 001–059 permanecen registrados en el historial del proyecto. Build 059 fue el cierre de la etapa de compra, venta y posición de activos.

Las validaciones posteriores se registran en este documento sin inventar numeración de Build cuando no corresponde.

## Regla de cierre

No registrar resultados de tests que no hayan sido realmente ejecutados. Cada cambio funcional debe contar con cobertura específica y, cuando corresponda, validación de la suite general.

## Próximo bloque

Definir la siguiente evolución funcional del backend a partir del código actual, entidades, repositorios, servicios, tests y reglas de negocio. No iniciar una nueva funcionalidad hasta revisar primero el estado real de `main`.
