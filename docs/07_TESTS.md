# SOFP — Tests

Este documento registra la evolución de la batería de tests y los resultados verificados por Build.

## Estado actual

### Build 053 — Incorporación de MovimientoActivo — Cerrado

Se incorporó el modelo de movimientos específicos de activos para representar el efecto de una operación sobre la tenencia de un activo, separado del efecto monetario representado por `Movimiento`.

- `MovimientoActivoTest`: **11/11 tests en verde**.
- `MovimientoActivoRepositoryTest`: **6/6 tests en verde**.
- Total de tests nuevos del bloque: **17**.
- La persistencia JPA de `MovimientoActivo` quedó validada después de registrarlo explícitamente en la configuración de persistencia utilizada por los tests.
- Se corrigieron las comparaciones de valores `BigDecimal` en los tests de persistencia para no depender de la escala decimal devuelta por JPA/H2.

Suite general:

- Tests run: **370**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Finalizada: **26/08/2026 10:57:39 -03:00**
- Duración: **24:34 min**

Commits principales del bloque:

- `4be2264` — `feat: agregar tipo de movimiento de activo`
- `51d3860` — `feat: incorporar movimiento de activo`
- `381f3a6` — `feat: incorporar repositorio de movimiento de activo`
- `7a54b20` — `test: agregar cobertura de MovimientoActivo`
- `eb57063` — `test: agregar cobertura de MovimientoActivoRepository`
- `d14d114` — `test: registrar MovimientoActivo en persistencia JPA`
- `a579d4e8` — `test: corregir comparacion decimal en MovimientoActivoRepositoryTest`

### Build 052 — Incorporación de Bono como especialización de Activo — Cerrado

Se incorporaron las pruebas de la nueva especialización `Bono` y de su repositorio JPA.

- `BonoTest`: **5/5 tests en verde**.
- `BonoRepositoryTest`: **6/6 tests en verde**.
- La persistencia JPA de `Bono` quedó validada después de registrarlo explícitamente en la configuración de persistencia utilizada por los tests.

Suite general:

- Tests run: **353**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Finalizada: **25/08/2026 20:14:39 -03:00**
- Duración: **16:07 min**

Commits del bloque:

- `589acf1` — `feat: incorporar entidad Bono`
- `c11f1f4` — `test: agregar cobertura de Bono`
- `6925447` — `feat: incorporar repositorio de Bono`
- `74b8fff` — `test: agregar cobertura de BonoRepository`
- `678c6ea` — `test: registrar Bono en persistencia JPA`

### Build 051 — Incorporación de Activo y persistencia JPA — Cerrado

Se incorporaron las pruebas de la nueva entidad `Activo` y de su repositorio JPA.

- `ActivoTest`: **8/8 tests en verde**.
- `ActivoRepositoryTest`: **6/6 tests en verde**.
- La primera ejecución del repositorio detectó la necesidad de registrar `Activo` en la configuración de persistencia utilizada por los tests; corregida esa configuración, los 6 tests quedaron en verde.

Suite general:

- Tests run: **342**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Finalizada: **25/08/2026 16:30:22 -03:00**
- Duración: **16:51 min**

Commits del bloque:

- `0793126` — `feat: incorporar entidad Activo`
- `5270e31` — `test: agregar cobertura de Activo`
- `1624f8c` — `feat: incorporar repositorio de Activo`
- `70bdbf7` — `test: agregar cobertura de ActivoRepository`

### Builds anteriores

Los Builds 001–050 y sus resultados permanecen registrados en el historial del proyecto.

## Conteo actual por test de service

- `CategoriaServiceTest`: **21**
- `CuentaServiceTest`: **47**
- `InstitucionFinancieraServiceTest`: **26**
- `MonedaServiceTest`: **17**
- `MovimientoServiceTest`: **50**
- `PerfilFinancieroServiceTest`: **13**
- `UsuarioServiceTest`: **15**
- `OperacionFinancieraServiceTest`: **22**

Total confirmado de tests de services: **211**.

Tests de dominio destacados:

- `MovimientoTest`: **27**
- `OperacionFinancieraTest`: **14**
- `ActivoTest`: **8**
- `BonoTest`: **5**
- `MovimientoActivoTest`: **11**

Tests de persistencia destacados:

- `OperacionFinancieraRepositoryTest`: **10**
- `ActivoRepositoryTest`: **6**
- `BonoRepositoryTest`: **6**
- `MovimientoActivoRepositoryTest`: **6**

Suite general actual: **370/370 tests en verde**, con `Failures: 0`, `Errors: 0`, `Skipped: 0` y `BUILD SUCCESS`.

La última ejecución general confirmada se realizó el **26/08/2026 a las 10:57:39 -03:00** y finalizó correctamente, con una duración de **24:34 min**.

## Regla de cierre

Cada Build debe quedar registrado con cambios principales, tests ejecutados, resultado, commit asociado cuando exista y próximo paso. La documentación debe reflejar el estado real de `feature/operacion-financiera` antes de iniciar un nuevo bloque.
