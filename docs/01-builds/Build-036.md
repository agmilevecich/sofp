# Build 036 — Ampliación de cobertura de InstitucionFinancieraService

## Objetivo

Ampliar la cobertura de `InstitucionFinancieraServiceTest` sobre validaciones de entrada, búsquedas inexistentes y operaciones sobre instituciones financieras inexistentes.

## Cambios

Se incorporaron **15 tests nuevos** en `InstitucionFinancieraServiceTest`, cubriendo:

- Institución financiera nula al guardar.
- ID nulo al buscar por ID.
- Búsqueda por ID inexistente.
- Nombre nulo al buscar por nombre.
- Búsqueda por nombre inexistente.
- ID nulo al renombrar.
- Renombrado de institución inexistente.
- ID nulo al actualizar el sitio web.
- Actualización del sitio web de una institución inexistente.
- ID nulo al actualizar la descripción.
- Actualización de la descripción de una institución inexistente.
- ID nulo al activar.
- Activación de una institución inexistente.
- ID nulo al desactivar.
- Desactivación de una institución inexistente.

No se modificó código de producción en este Build. La única diferencia inicialmente detectada en `InstitucionFinancieraService.java` correspondía a líneas en blanco accidentales y fue descartada antes del commit.

## Resultado

`InstitucionFinancieraServiceTest`: **23/23 tests en verde**.

La batería general quedó en **201/201 tests en verde**, con:

- Failures: 0
- Errors: 0
- Skipped: 0
- BUILD SUCCESS

También se ejecutó `git diff --check`, sin errores de whitespace.

## Commit asociado

- `bd7f4bd` — `test: ampliar cobertura de InstitucionFinancieraService`

El commit fue publicado en `main` tanto en GitHub como en Bitbucket.

## Estado de Git

El árbol de trabajo quedó limpio después del commit y los remotos quedaron sincronizados con `main`.

## Próximo paso

Revisar los casos de uso pendientes del dominio y definir el siguiente bloque funcional antes de implementar código nuevo.
