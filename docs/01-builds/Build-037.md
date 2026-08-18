# Build 037 — Ampliación de cobertura de MonedaService

## Objetivo

Ampliar la cobertura de `MonedaServiceTest` sobre validaciones de entrada, búsquedas inexistentes y operaciones de modificación sobre monedas inexistentes.

## Cambios

Se incorporaron **9 tests nuevos** en `MonedaServiceTest`, pasando de 8 a **17 tests**.

La revisión se realizó sobre `MonedaService`, `Moneda` y `MonedaRepository` para mantener los tests alineados con el comportamiento real del servicio.

Se verificó la cobertura existente de:

- Guardado y búsqueda por ID.
- Búsqueda por código.
- Listado de monedas.
- Cambio de nombre.
- Cambio de cantidad de decimales.
- Operaciones sobre monedas inexistentes.

## Resultado

`MonedaServiceTest`: **17/17 tests en verde**.

La batería general quedó en **210/210 tests en verde**, con:

- Failures: 0
- Errors: 0
- Skipped: 0
- BUILD SUCCESS

La ejecución general finalizó el 18/08/2026 a las 18:52:19 -03:00.

## Estado del código

En este punto se verificó el comportamiento mediante tests. El commit específico de los cambios de `MonedaServiceTest` todavía no está registrado en esta documentación; no se debe inventar un SHA hasta que el commit exista.

## Próximo paso

Revisar el estado de Git de los cambios de `MonedaServiceTest`, ejecutar las verificaciones finales (`git diff --check`, `git status`) y, si corresponde, realizar el commit de código antes de publicar la actualización definitiva de continuidad.
