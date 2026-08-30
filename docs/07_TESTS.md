# SOFP — Tests

Este documento registra la evolución de la batería de tests y los resultados realmente verificados.

## Validación global vigente

Suite completa ejecutada desde IntelliJ IDEA el **29/08/2026 20:00:23 -03:00**:

- Tests run: **486**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- Resultado: **BUILD SUCCESS**
- Duración: **15:50 min**

**486/486 tests en verde.** Esta es la validación global vigente.

## Seguridad de PerfilFinanciero

`PerfilFinancieroServiceTest`: **19/19 tests en verde**.

La cobertura específica incluye operaciones permitidas al propietario, rechazo de usuario no propietario, identificadores nulos y perfiles inexistentes para las operaciones protegidas.

La feature `feature/seguridad-perfil-financiero` quedó integrada en `main` mediante fast-forward.

## Reportes de cartera y evolución histórica

Cobertura específica incorporada:

- `DetalleMovimientoCarteraActivoTest`;
- `ReporteCarteraActivoTest`;
- `CarteraActivoServiceComposicionTest`;
- `CarteraActivoServiceMovimientosTest`;
- `CuentaServiceEvolucionSaldoTest`.

`CuentaServiceEvolucionSaldoTest`: **5/5 tests en verde**.

La feature `feature/reportes-cartera` quedó integrada en `main` mediante fast-forward.

## Valorización de posición activa

`ValorizacionPosicionActivoTest`: **8/8 tests en verde**.

Commits principales:

- `ef19486` — `feat: agregar valorizacion de posicion activa`;
- `7379570` — `test: cubrir valorizacion de posicion activa`.

## Costo promedio de posición activa

`PosicionActivoTest`: **8/8 tests en verde**.

Commits principales:

- `da09ef0` — `feat: calcular costo promedio de posicion activa`;
- `6cb038b` — `test: cubrir costo promedio de posicion activa`.

## Cartera de activos

`CarteraActivoServiceTest`: **5/5 tests en verde**.

## Identificación por símbolo

Se validaron las búsquedas por símbolo de `ActivoRepository` y `BonoRepository`, el rechazo de `null` y la unicidad de símbolos en persistencia.

## Historial

Los Builds 001–059 permanecen registrados en el historial del proyecto. La suite global pasó de 480 a **486 tests** con la incorporación de la cobertura de seguridad de `PerfilFinanciero`.

## Regla de cierre

No registrar resultados no ejecutados. Cada funcionalidad debe contar con cobertura específica y, cuando corresponda, validación de la suite general.

## Próximo bloque

Definir la siguiente evolución funcional a partir del estado real de `main`, revisando código, tests y reglas de negocio antes de iniciar una nueva feature.
