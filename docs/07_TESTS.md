# SOFP — Tests

Este documento registra la evolución de la batería de tests y los resultados verificados por Build.

## Estado actual

### Build 043 — Ampliación de cobertura de MovimientoService

Se amplió `MovimientoServiceTest`, pasando de **37 a 50 tests en verde**.

Se incorporó cobertura adicional para registro, modificaciones, eliminación, búsquedas/listados y validaciones de parámetros nulos y entidades inexistentes.

Durante la ejecución inicial se detectaron dos expectativas incorrectas para valores nulos de `importe` y `descripcion`. El dominio `Movimiento` los valida mediante `Validaciones` y devuelve `IllegalArgumentException`, por lo que los tests fueron corregidos para reflejar el contrato existente.

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

### Build 040 — Ampliación de cobertura de CategoriaService

Se amplió `CategoriaServiceTest`, pasando de **12 a 21 tests en verde**.

Se incorporaron **9 tests nuevos**, cubriendo validaciones de parámetros nulos en registro, búsqueda, listado por perfil, modificación de nombre y descripción, activación, desactivación y eliminación.

La batería general quedó en **236/236 tests en verde**.

Commit asociado:

- `9be5972` — `test: ampliar cobertura de CategoriaService`

### Build 039 — Ampliación de cobertura de UsuarioService

Se amplió `UsuarioServiceTest`, pasando de **5 a 15 tests en verde**.

Se incorporaron **10 tests nuevos** y se reforzaron validaciones de IDs nulos en `UsuarioService`.

La batería general quedó en **227/227 tests en verde**.

Commit asociado:

- `0e27dfe` — `test: ampliar cobertura de UsuarioService`

### Build 038 — Ampliación de cobertura de PerfilFinancieroService

Se amplió `PerfilFinancieroServiceTest`, pasando de **6 a 13 tests en verde**.

La batería general quedó en **217/217 tests en verde**.

Commit asociado:

- `e2d3268` — `test: ampliar cobertura de PerfilFinancieroService`

### Build 037 — Ampliación de cobertura de MonedaService

Se amplió `MonedaServiceTest`, pasando de **8 a 17 tests en verde**.

La batería general quedó en **210/210 tests en verde**.

### Build 036 — Ampliación de cobertura de InstitucionFinancieraService

Se amplió `InstitucionFinancieraServiceTest` con **15 tests nuevos**.

`InstitucionFinancieraServiceTest` quedó con **23/23 tests en verde** y la batería general en **201/201 tests en verde**.

Commit asociado:

- `bd7f4bd` — `test: ampliar cobertura de InstitucionFinancieraService`

### Build 035 — Ampliación de cobertura de CuentaService

Se ampliaron los tests de `CuentaServiceTest` con **20 tests nuevos**.

`CuentaServiceTest` quedó con **40/40 tests en verde** y la batería general en **186/186 tests en verde**.

Commit asociado:

- `57b8ad5` — `test: ampliar cobertura de CuentaService`

### Build 034 — Ampliación de cobertura de MovimientoService

Se ampliaron los tests de `MovimientoServiceTest` con **17 tests nuevos**.

`MovimientoServiceTest` quedó con **32/32 tests en verde** y la batería general en **163/163 tests en verde**.

Commit asociado:

- `4d9dc2a` — `test: ampliar cobertura de MovimientoService`.

## Conteo actual por test de service

- `CategoriaServiceTest`: **21**
- `CuentaServiceTest`: **47**
- `InstitucionFinancieraServiceTest`: **26**
- `MonedaServiceTest`: **17**
- `MovimientoServiceTest`: **50**
- `PerfilFinancieroServiceTest`: **13**
- `UsuarioServiceTest`: **15**

Total de tests de services: **189**.

La batería general del proyecto es de **259/259 tests en verde**.

## Histórico de cobertura

Los Builds anteriores y sus resultados permanecen registrados en el historial del proyecto. El punto de control vigente es Build 043 con **259 tests en verde**.

## Regla de cierre

Cada Build debe quedar registrado con cambios principales, tests ejecutados, resultado, commit asociado cuando exista y próximo paso. Los cambios posteriores de cobertura también deben quedar registrados para que la documentación refleje el estado real de los tests.
