# SOFP — Tests

Este documento registra la evolución de la batería de tests y los resultados verificados por Build.

## Estado actual

### Build 044 — Ampliación de cobertura de Movimiento

Se amplió `MovimientoTest`, pasando de **4 a 27 tests en verde**.

Se incorporaron **23 tests nuevos** para cubrir validaciones del constructor y operaciones de modificación de `Movimiento`, incluyendo cuenta, categoría, tipo, importe, fecha/hora, descripción, observaciones y valores inválidos.

No se modificó código de producción.

La clase `MovimientoTest` quedó en **27/27 tests en verde**.

La suite general posterior a esta ampliación todavía no fue ejecutada. Como referencia, la última suite general confirmada en Build 043 fue de **259/259 tests en verde**.

Commit asociado:

- `6f53f79` — `test: ampliar cobertura de Movimiento`

### Build 043 — Ampliación de cobertura de MovimientoService

Se amplió `MovimientoServiceTest`, pasando de **37 a 50 tests en verde**.

Se incorporó cobertura adicional para registro, modificaciones, eliminación, búsquedas/listados y validaciones de parámetros nulos y entidades inexistentes.

La batería general quedó en **259/259 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.

La ejecución terminó con `BUILD SUCCESS` el 19/08/2026 a las 19:51:15 -03:00.

`git diff --check` no reportó errores de whitespace.

Commit asociado:

- `b6384f0` — `test: ampliar cobertura de MovimientoService`

### Build 042 — Ampliación de cobertura de CuentaService

Se amplió `CuentaServiceTest`, pasando de **40 a 47 tests en verde**.

Se incorporó cobertura adicional para los casos definidos durante la revisión de `CuentaService`, sin modificar código de producción.

La batería general quedó en **246/246 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.

La ejecución terminó con `BUILD SUCCESS` el 19/08/2026 a las 16:46:27 -03:00.

`git diff --check` no reportó errores de whitespace.

Commit asociado:

- `526b378` — `test: ampliar cobertura de CuentaService`

### Build 041 — Reforzamiento de validaciones de servicios y dominio

Se amplió `InstitucionFinancieraServiceTest`, pasando de **23 a 26 tests en verde**.

Se incorporaron **3 tests nuevos**, cubriendo nombre nulo al renombrar, sitio web nulo y descripción nula en las operaciones de modificación de `InstitucionFinanciera`.

Se reforzó `InstitucionFinanciera` para rechazar valores nulos en constructor y operaciones de modificación, se agregaron validaciones explícitas de IDs en `MonedaService` y se reforzaron las validaciones de `PerfilFinancieroService`.

La batería general quedó en **239/239 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.

La ejecución terminó con `BUILD SUCCESS` el 19/08/2026 a las 15:09:48 -03:00.

Commit asociado:

- `a9de29c` — `feat: reforzar validaciones de servicios y dominio`

### Conteo actual por test de service

- `CategoriaServiceTest`: **21**
- `CuentaServiceTest`: **47**
- `InstitucionFinancieraServiceTest`: **26**
- `MonedaServiceTest`: **17**
- `MovimientoServiceTest`: **50**
- `PerfilFinancieroServiceTest`: **13**
- `UsuarioServiceTest`: **15**

Total confirmado de tests de services: **189**.

La última batería general confirmada es de **259/259 tests en verde**.

La clase `MovimientoTest` cuenta ahora con **27/27 tests en verde** como parte del bloque de cobertura en curso.

## Histórico de cobertura

Los Builds anteriores y sus resultados permanecen registrados en el historial del proyecto. El último Build cerrado es Build 043 con **259 tests en verde**. Build 044 queda pendiente de la ejecución de la suite general para su cierre.

## Regla de cierre

Cada Build debe quedar registrado con cambios principales, tests ejecutados, resultado, commit asociado cuando exista y próximo paso. Los cambios posteriores de cobertura también deben quedar registrados para que la documentación refleje el estado real de los tests.
