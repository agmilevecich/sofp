# SOFP — Seguridad de PerfilFinanciero

## Estado

Feature: `feature/seguridad-perfil-financiero`

Estado: **IMPLEMENTADA Y VALIDADA**.

La feature parte de `main` en `ce7c8af` y contiene dos commits propios:

- `6478262` — `feat: proteger operaciones de perfil por propietario`;
- `8d3c775` — `test: cubrir autorizacion de operaciones de perfil`.

La comparación contra `main` confirma: **2 commits adelante, 0 detrás**.

## Cambio funcional

`PerfilFinancieroService` protege las operaciones que modifican el estado o la descripción de un perfil financiero.

Las operaciones protegidas son:

- `cambiarDescripcion(Long perfilId, Long usuarioId, String descripcion)`;
- `activar(Long perfilId, Long usuarioId)`;
- `desactivar(Long perfilId, Long usuarioId)`.

Antes de ejecutar la operación, el servicio valida los identificadores y verifica que el `usuarioId` recibido corresponda al usuario propietario del perfil.

Reglas verificadas:

- `perfilId` nulo → `NullPointerException`;
- `usuarioId` nulo → `NullPointerException`;
- perfil inexistente → `IllegalArgumentException`;
- usuario que no es propietario → `IllegalArgumentException`;
- usuario propietario → operación permitida.

La autorización se mantiene en la capa de servicio porque requiere coordinar la relación entre `PerfilFinanciero` y `Usuario`.

## Cobertura específica

`PerfilFinancieroServiceTest`: **19/19 tests en verde**.

La cobertura incluye los casos exitosos, usuario no propietario, identificadores nulos y entidad inexistente para las operaciones protegidas.

## Suite general

Suite completa ejecutada desde IntelliJ IDEA el **29/08/2026 20:00:23 -03:00**:

- Tests run: **486**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- Resultado: **BUILD SUCCESS**
- Duración: **15:50 min**

Resultado: **486/486 tests en verde**.

Esta es la validación global más reciente confirmada por el usuario.

## Estado Git

La rama de trabajo está sincronizada con sus dos remotos y el último estado local informado fue:

- `git status` → working tree clean;
- `git diff --check` → sin observaciones;
- `HEAD` → `8d3c775`;
- `main` → `ce7c8af`;
- `github/feature/seguridad-perfil-financiero` → `8d3c775`;
- `bitbucket/feature/seguridad-perfil-financiero` → `8d3c775`.

`main` en GitHub y Bitbucket quedó nuevamente alineado en `ce7c8af` antes de continuar esta feature.

## Próximo paso

La implementación y las validaciones están completas.

Antes de integrar la feature en `main` mediante **fast-forward** corresponde realizar la comprobación final:

1. actualizar referencias de ambos remotos;
2. comprobar que la feature sigue 2 commits adelante y 0 detrás de `main`;
3. verificar que no existan cambios locales;
4. revisar el diff final;
5. confirmar `git diff --check`;
6. confirmar el estado de ambos remotos;
7. integrar mediante fast-forward, sin merge commit.

No modificar `main` durante estas comprobaciones.
