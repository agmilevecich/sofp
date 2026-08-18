# SOFP — Tests

Este documento registra la evolución de la batería de tests y los resultados verificados por Build.

## Estado actual

### Build 037 — Ampliación de cobertura de MonedaService

Se amplió `MonedaServiceTest`, pasando de **8 a 17 tests en verde**.

Se revisaron `MonedaService`, `Moneda` y `MonedaRepository` para mantener la cobertura alineada con el comportamiento real del servicio.

La batería general quedó en **210/210 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.

La ejecución terminó con `BUILD SUCCESS` el 18/08/2026 a las 18:52:19 -03:00.

El commit específico de código de Build 037 todavía no está registrado.

### Build 036 — Ampliación de cobertura de InstitucionFinancieraService

Se ampliaron los tests de `InstitucionFinancieraServiceTest` sin modificar código de producción.

Se agregaron **15 tests**, cubriendo validaciones de parámetros nulos, búsquedas inexistentes y operaciones de modificación, activación y desactivación sobre instituciones inexistentes.

`InstitucionFinancieraServiceTest` quedó con **23/23 tests en verde**.

La batería general quedó en **201/201 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.

La ejecución terminó con `BUILD SUCCESS` y `git diff --check` no reportó errores de whitespace.

Commit asociado:

- `bd7f4bd` — `test: ampliar cobertura de InstitucionFinancieraService`

### Build 035 — Ampliación de cobertura de CuentaService

Se ampliaron los tests de `CuentaServiceTest` sin modificar código de producción.

Se agregaron **20 tests**, cubriendo validaciones de parámetros nulos, cuentas inexistentes y operaciones de modificación, activación, desactivación y eliminación.

`CuentaServiceTest` quedó con **40/40 tests en verde**.

La batería general quedó en **186/186 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.

Commit asociado:

- `57b8ad5` — `test: ampliar cobertura de CuentaService`

### Build 034 — Ampliación de cobertura de MovimientoService

Se ampliaron los tests de `MovimientoServiceTest` sin modificar código de producción.

Se agregaron **17 tests**, cubriendo validaciones de parámetros nulos, búsquedas inexistentes y operaciones de modificación/eliminación sobre movimientos inexistentes.

`MovimientoServiceTest` quedó con **32/32 tests en verde**.

La batería general quedó en **163/163 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.

Commit asociado:

- `4d9dc2a` — `test: ampliar cobertura de MovimientoService`

## Conteo actual por test de service

- `CategoriaServiceTest`: **12**
- `CuentaServiceTest`: **40**
- `InstitucionFinancieraServiceTest`: **23**
- `MonedaServiceTest`: **17**
- `MovimientoServiceTest`: **37**
- `PerfilFinancieroServiceTest`: **6**
- `UsuarioServiceTest`: **5**

Total de tests de services: **140**.

La batería general del proyecto es de **210/210 tests en verde**.

## Histórico de cobertura

Los Builds anteriores y sus resultados permanecen registrados en el historial del proyecto. El punto de control vigente es Build 037 con **210 tests en verde**.

## Regla de cierre

Cada Build debe quedar registrado con cambios principales, tests ejecutados, resultado, commit asociado cuando exista y próximo paso. Los cambios posteriores de cobertura también deben quedar registrados para que la documentación refleje el estado real de los tests.
