# Build 043 — Ampliación de cobertura de MovimientoService

**Fecha:** 19/08/2026

## Objetivo

Ampliar la cobertura de `MovimientoServiceTest` sobre los métodos y validaciones existentes de `MovimientoService`, sin modificar código de producción.

## Cambios

Se incorporó cobertura adicional en `MovimientoServiceTest` para operaciones de registro, modificación, eliminación, búsqueda/listados y validaciones de parámetros nulos y entidades inexistentes.

Durante la ejecución inicial se detectaron dos expectativas incorrectas en tests de registro: `Movimiento` ya valida `importe` y `descripcion` mediante `Validaciones`, devolviendo `IllegalArgumentException` para valores nulos. Los tests fueron corregidos para reflejar el contrato real del dominio.

`MovimientoServiceTest` pasó de **37 a 50 tests**.

## Validación

Se ejecutó la batería general del proyecto.

Resultado:

- Tests run: **259**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`

La ejecución terminó el **19/08/2026 a las 19:51:15 -03:00**.

Antes del commit:

- `git diff --check`: sin errores.
- `git diff --stat`: únicamente `MovimientoServiceTest.java`, con 224 líneas agregadas.
- Working tree limpio después del commit.

## Commit

`b6384f0` — `test: ampliar cobertura de MovimientoService`

El commit fue publicado en `main` de GitHub y Bitbucket mediante `git pushall`.

## Estado

Build 043 queda cerrado con la suite general completamente verde.

Conteo actual de tests de services:

- `CategoriaServiceTest`: **21**
- `CuentaServiceTest`: **47**
- `InstitucionFinancieraServiceTest`: **26**
- `MonedaServiceTest`: **17**
- `MovimientoServiceTest`: **50**
- `PerfilFinancieroServiceTest`: **13**
- `UsuarioServiceTest`: **15**

Total: **189 tests de services**.

El siguiente bloque debe definirse antes de incorporar nuevo código de producción.
