# Build 041 — Reforzamiento de validaciones de servicios y dominio

## Objetivo

Reforzar el contrato de validación de parámetros nulos en servicios y dominio, manteniendo el criterio del proyecto: parámetros obligatorios `null` deben provocar `NullPointerException`, y entidades inexistentes deben provocar `IllegalArgumentException` cuando corresponda.

## Cambios

Se modificaron cuatro archivos:

- `src/main/java/ar/com/agmilevecich/sofp/domain/InstitucionFinanciera.java`
- `src/main/java/ar/com/agmilevecich/sofp/service/MonedaService.java`
- `src/main/java/ar/com/agmilevecich/sofp/service/PerfilFinancieroService.java`
- `src/test/java/ar/com/agmilevecich/sofp/service/InstitucionFinancieraServiceTest.java`

### InstitucionFinanciera

Se reforzó la validación mediante `Objects.requireNonNull(...)` en:

- constructor: `nombre` y `tipo`;
- `renombrar(...)`: nuevo nombre;
- `actualizarSitioWeb(...)`: sitio web;
- `actualizarDescripcion(...)`: descripción.

### MonedaService

Se incorporó validación explícita de `monedaId` en:

- `cambiarNombre(...)`;
- `cambiarCantidadDecimales(...)`.

La búsqueda posterior mantiene el comportamiento de lanzar `IllegalArgumentException` cuando la moneda no existe.

### PerfilFinancieroService

Se incorporaron validaciones explícitas de:

- `perfilId` en las operaciones de cambio de descripción, activación y desactivación;
- `descripcion` en `cambiarDescripcion(...)`.

Además, se centralizó la obtención de perfiles existentes mediante el método privado `obtenerPorId(...)`, evitando duplicar la misma lógica de búsqueda y excepción.

### Tests

`InstitucionFinancieraServiceTest` pasó de **23 a 26 tests**.

Se agregaron tests para:

- renombrar una institución con nombre nulo;
- actualizar sitio web con valor nulo;
- actualizar descripción con valor nulo.

## Resultado de tests

La batería general pasó de **236 a 239 tests en verde**.

Resultado final:

- Tests run: **239**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`

La ejecución general terminó el **19/08/2026 a las 15:09:48 -03:00**.

`git diff --check` no reportó errores de whitespace.

## Commit

- `a9de29c` — `feat: reforzar validaciones de servicios y dominio`

El commit fue publicado en `main` de GitHub y Bitbucket mediante `git pushall`.

## Estado de cierre

- `main` local, GitHub y Bitbucket quedaron sincronizados en `a9de29c` antes de la actualización documental.
- Working tree local quedó limpio.
- `InstitucionFinancieraServiceTest`: **26 tests**.
- Suite general: **239/239 tests en verde**.

## Próximo paso

Definir el siguiente bloque funcional y sus tests antes de implementar código nuevo.
