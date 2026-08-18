# SOFP — Tests

Este documento registra la evolución de la batería de tests y los resultados verificados por Build.

## Estado actual

### Build 035 — Ampliación de cobertura de CuentaService

Se ampliaron los tests de `CuentaServiceTest` sin modificar código de producción.

Se agregaron **20 tests**, cubriendo validaciones de parámetros nulos, cuentas inexistentes y operaciones de modificación, activación, desactivación y eliminación.

`CuentaServiceTest` quedó con **40/40 tests en verde**.

La batería general quedó en **186/186 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.

La ejecución terminó con `BUILD SUCCESS` y `git diff --check` no reportó errores de whitespace.

Commit asociado:

- `57b8ad5` — `test: ampliar cobertura de CuentaService`

### Build 034 — Ampliación de cobertura de MovimientoService

Se ampliaron los tests de `MovimientoServiceTest` sin modificar código de producción.

Se agregaron **17 tests**, cubriendo validaciones de parámetros nulos, búsquedas inexistentes y operaciones de modificación/eliminación sobre movimientos inexistentes.

`MovimientoServiceTest` quedó con **32/32 tests en verde**.

La batería general quedó en **163/163 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.

La ejecución terminó con `BUILD SUCCESS` y `git diff --check` no reportó errores.

Commit asociado:

- `4d9dc2a` — `test: ampliar cobertura de MovimientoService`

## Builds recientes

### Build 033

Se incorporaron tres reglas de negocio en `MovimientoService`: cuenta y categoría deben pertenecer al mismo perfil financiero; no se registran movimientos sobre cuentas desactivadas; y no se permite cambiar a una categoría de otro perfil.

Resultado: **144/144 tests en verde**.

Commit: `b18ca96` — `feat: agregar reglas de negocio a movimientos`.

### Build 032

Se completó la eliminación de cuentas desde `CuentaService`.

Resultado: **141/141 tests en verde**.

Commit: `a1a817d` — `feat: completar eliminacion de CuentaService`.

## Histórico de cobertura

Los Builds anteriores y sus resultados permanecen registrados en el historial del proyecto. El punto de control vigente es Build 035 con **186 tests en verde**.

## Regla de cierre

Cada Build debe quedar registrado con cambios principales, tests ejecutados, resultado, commit asociado cuando exista y próximo paso. Los cambios posteriores de cobertura también deben quedar registrados para que la documentación refleje el estado real de los tests.
