# Build 035 — Ampliación de cobertura de CuentaService

## Objetivo

Ampliar la cobertura de `CuentaServiceTest` sobre las validaciones de entrada, cuentas inexistentes y operaciones de modificación, activación, desactivación y eliminación.

## Cambios

Se incorporaron **20 tests nuevos** en `CuentaServiceTest`, cubriendo:

- IDs nulos en cálculo de saldo, búsqueda, listado por perfil y operaciones de modificación/activación/desactivación/eliminación.
- Nombre nulo y cuenta inexistente al modificar el nombre.
- Cuenta inexistente al modificar el identificador externo.
- Tipo de cuenta nulo, ID nulo y cuenta inexistente.
- Institución financiera nula, ID nulo y cuenta inexistente.
- Moneda nula, ID nulo y cuenta inexistente.
- Activación y desactivación con ID nulo o cuenta inexistente.
- Eliminación con ID nulo o cuenta inexistente.

También se simplificó el uso de `assertThrows` mediante el import estático de las aserciones de JUnit.

No se modificó código de producción en este Build.

## Resultado

`CuentaServiceTest`: **40/40 tests en verde**.

Batería general: **186/186 tests en verde**, con:

- Failures: 0
- Errors: 0
- Skipped: 0
- BUILD SUCCESS

También se ejecutó `git diff --check`, sin errores de whitespace. Git mostró únicamente la advertencia habitual de conversión de finales de línea LF/CRLF de Windows.

## Commit asociado

- `57b8ad5` — `test: ampliar cobertura de CuentaService`

El commit fue publicado en `main` tanto en GitHub como en Bitbucket.

## Próximo paso

Revisar los casos de uso pendientes del dominio y definir el siguiente bloque funcional antes de implementar código nuevo.
