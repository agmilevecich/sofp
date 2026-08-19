# Build 039 — Ampliación de cobertura de UsuarioService

## Objetivo

Ampliar la cobertura de `UsuarioServiceTest` y endurecer el contrato de `UsuarioService` para que `activar(...)` y `desactivar(...)` validen explícitamente un ID nulo.

## Cambios

Se incorporaron **10 tests nuevos** en `UsuarioServiceTest`, pasando de 5 a **15 tests**.

La nueva cobertura verifica:

- Búsquedas por ID inexistentes.
- Búsquedas por email inexistentes.
- Listado vacío cuando no existen usuarios.
- Validación de usuario nulo al guardar.
- Validación de ID nulo al buscar por ID.
- Validación de email nulo al buscar por email.
- Validación de ID nulo al activar.
- Validación de ID nulo al desactivar.
- Activación de usuario inexistente.
- Desactivación de usuario inexistente.

En `UsuarioService` se incorporó `Objects.requireNonNull(...)` en `activar(...)` y `desactivar(...)` para mantener un contrato de validación consistente con el resto de la capa `service`.

## Resultado

`UsuarioServiceTest`: **15/15 tests en verde**.

La batería general quedó en **227/227 tests en verde**, con:

- Failures: 0
- Errors: 0
- Skipped: 0
- BUILD SUCCESS

La ejecución general finalizó el 18/08/2026 a las 22:34:24 -03:00.

También se verificó `git diff --check`, sin errores de whitespace, y el working tree quedó limpio después del commit.

## Commit

`0e27dfe` — `test: ampliar cobertura de UsuarioService`

El commit fue publicado en `main` de GitHub y Bitbucket mediante `git pushall`.

## Próximo paso

Mantener sincronizada la documentación de continuidad y definir el siguiente bloque funcional antes de implementar código nuevo.
