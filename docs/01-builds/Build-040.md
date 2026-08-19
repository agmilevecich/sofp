# Build 040 — Ampliación de cobertura de CategoriaService

## Objetivo

Ampliar la cobertura de `CategoriaServiceTest` sin modificar código de producción, cubriendo explícitamente las validaciones de parámetros nulos ya establecidas por `CategoriaService`.

## Cambios

Se agregaron **9 tests nuevos** a `CategoriaServiceTest`:

- registro de categoría nula;
- búsqueda por ID nulo;
- listado por perfil financiero con ID nulo;
- modificación de nombre con ID nulo;
- modificación de nombre con nombre nulo;
- modificación de descripción con ID nulo;
- activación con ID nulo;
- desactivación con ID nulo;
- eliminación con ID nulo.

Durante la implementación se corrigió el test de nombre nulo para utilizar una categoría existente, respetando el orden real de validación de `CategoriaService.modificarNombre(...)`: primero se obtiene la categoría y luego se valida el nuevo nombre.

No se modificó `CategoriaService.java` ni ningún otro código de producción.

## Resultado de tests

`CategoriaServiceTest` pasó de **12 a 21 tests en verde**.

La batería general pasó de **227 a 236 tests en verde**.

Resultado final:

- Tests run: **236**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`

La ejecución general terminó el **19/08/2026 a las 11:28:34 -03:00**.

`git diff --check` no reportó errores de whitespace. El aviso de conversión LF/CRLF corresponde a la configuración de finales de línea de Git en Windows y no constituye un error de validación.

## Commit

- `9be5972` — `test: ampliar cobertura de CategoriaService`

El commit fue publicado en `main` de GitHub y Bitbucket mediante `git pushall`.

## Estado de cierre

- `main` local, GitHub y Bitbucket quedaron sincronizados en `9be5972`.
- Working tree limpio.
- `CategoriaService.java` sin modificaciones.
- `CategoriaServiceTest.java`: **21 tests**.
- Suite general: **236/236 tests en verde**.

## Próximo paso

Definir el siguiente bloque funcional y sus tests antes de implementar código nuevo.
