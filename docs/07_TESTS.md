# SOFP — Tests

Este documento registra la evolución de la batería de tests y los resultados realmente verificados.

## Validación global vigente

Suite completa ejecutada desde IntelliJ IDEA el **31/08/2026**, finalizada a las **12:46:20 -03:00**:

- Tests run: **503**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- Resultado: **BUILD SUCCESS**
- Duración: **15:01 min**

**503/503 tests en verde.** Esta es la validación global vigente.

## Seguridad de PerfilFinanciero

`PerfilFinancieroServiceTest`: **19/19 tests en verde**.

La cobertura específica incluye operaciones permitidas al propietario, rechazo de usuario no propietario, identificadores nulos y perfiles inexistentes para las operaciones protegidas.

La feature `feature/seguridad-perfil-financiero` quedó integrada en `main` mediante fast-forward.

## Seguridad y aislamiento por perfil — feature actual

La rama `feature/seguridad-aislamiento-datos` incorporó cobertura y adaptaciones para:

- autorización por propietario en operaciones mutables de `CuentaService`;
- adaptación de `CuentaServiceTest` a la autorización por propietario;
- autorización por propietario en operaciones mutables de `CategoriaService`;
- corrección de persistencia/rollback detectada durante la adaptación de `CategoriaServiceTest`;
- autorización por propietario en operaciones mutables de `MovimientoService`;
- adaptación de `MovimientoServiceTest` a las firmas protegidas;
- aislamiento de `PosicionActivoService` por perfil financiero.

La validación global de **503/503** confirma que estas correcciones mantienen verdes los tests existentes.

## Posición de activo y aislamiento

`PosicionActivoService` obtiene ahora la posición mediante `activoId + perfilFinancieroId` y utiliza `MovimientoActivoRepository.listarPorActivoYPerfilFinanciero(...)`.

La cobertura específica de aislamiento de posición se incorporó en `PosicionActivoServiceTest`.

## Reportes de cartera y evolución histórica

Cobertura específica incorporada anteriormente:

- `DetalleMovimientoCarteraActivoTest`;
- `ReporteCarteraActivoTest`;
- `CarteraActivoServiceComposicionTest`;
- `CarteraActivoServiceMovimientosTest`;
- `CuentaServiceEvolucionSaldoTest`.

`CuentaServiceEvolucionSaldoTest`: **5/5 tests en verde**.

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

Los Builds 001–059 permanecen registrados en el historial del proyecto. La suite global evolucionó posteriormente hasta **503 tests** con la incorporación de cobertura y adaptaciones de seguridad.

## Pendientes de cobertura

La suite global está verde, pero todavía deben agregarse/verificarse tests específicos para cerrar completamente la etapa de seguridad transversal:

- autorización explícita en `OperacionFinancieraService`;
- lectura de recursos propios frente a recursos ajenos;
- aislamiento de consultas por ID/listados que crucen la frontera de casos de uso;
- caminos alternativos de creación de movimientos;
- casos límite de recursos compartidos entre perfiles.

## Regla de cierre

No registrar resultados no ejecutados. Cada funcionalidad debe contar con cobertura específica y, cuando corresponda, validación de la suite general.

## Próximo bloque

Continuar con los tests de los hallazgos restantes de seguridad. Una vez cerrada esa etapa, realizar una nueva suite general y preparar la transición a Swing.
