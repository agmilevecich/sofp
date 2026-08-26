# SOFP — Tests

Este documento registra la evolución de la batería de tests y los resultados verificados por Build.

## Estado actual

### Build 056 — Incorporación del tipo de operación financiera — Cerrado

Se incorporó `TipoOperacionFinanciera` con `TRANSFERENCIA`, `COMPRA` y `VENTA`, y se integró el tipo en `OperacionFinanciera` manteniendo la compatibilidad de las transferencias existentes.

Pruebas específicas del bloque:

- `OperacionFinancieraTest`: **17/17 tests en verde**.

Suite general:

- Tests run: **400**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Finalizada: **26/08/2026 18:42:57 -03:00**
- Duración: **16:05 min**

Commits principales del bloque:

- `21d8ed9` — `feat: agregar tipo de operacion financiera`
- `3025848` — `feat: incorporar tipo a operacion financiera`
- `70e4584` — `test: cubrir tipo de operacion financiera`

Validación Git posterior al Build:

- `git status`: working tree limpio.
- `git diff --check`: sin errores.
- Rama `feature/operacion-financiera` sincronizada con GitHub y Bitbucket.

### Build 055 — Refuerzo de integridad de OperacionFinanciera — Cerrado

Se reforzó la integridad del dominio de `OperacionFinanciera` para impedir asociaciones incoherentes de movimientos monetarios.

Pruebas específicas del bloque:

- `OperacionFinancieraTest`: **14/14 tests en verde**.
- `OperacionFinancieraIntegridadTest`: **4/4 tests en verde**.
- Total específico del bloque: **18/18 tests en verde**.

Suite general:

- Tests run: **397**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Finalizada: **26/08/2026 16:48:26 -03:00**
- Duración: **20:54 min**

### Build 054 — Incorporación del servicio de posición de activo — Cerrado

Se incorporó `PosicionActivoService` como coordinación entre `Activo`, `MovimientoActivoRepository` y `CalculadorPosicionActivo`.

También se incorporaron la búsqueda de movimientos de un activo y el calculador de posición.

### Build 053 — Incorporación de MovimientoActivo — Cerrado

Se incorporó el modelo de movimientos específicos de activos para representar el efecto sobre la tenencia de un activo separado del efecto monetario de `Movimiento`.

- `MovimientoActivoTest`: **11/11**.
- `MovimientoActivoRepositoryTest`: **6/6**.
- Total del bloque: **17 tests nuevos**.

Suite general: **370/370**, `BUILD SUCCESS`, finalizada el **26/08/2026 10:57:39 -03:00**, duración **24:34 min**.

## Builds anteriores

Los Builds 001–052 y sus resultados permanecen registrados en el historial del proyecto.

## Conteo de tests de services registrado

- `CategoriaServiceTest`: **21**
- `CuentaServiceTest`: **47**
- `InstitucionFinancieraServiceTest`: **26**
- `MonedaServiceTest`: **17**
- `MovimientoServiceTest`: **50**
- `PerfilFinancieroServiceTest`: **13**
- `UsuarioServiceTest`: **15**
- `OperacionFinancieraServiceTest`: **22**

Total registrado de tests de services: **211**.

## Tests de dominio destacados

- `MovimientoTest`: **27**
- `OperacionFinancieraTest`: **17**
- `OperacionFinancieraIntegridadTest`: **4**
- `ActivoTest`: **8**
- `BonoTest`: **5**
- `MovimientoActivoTest`: **11**

## Tests de persistencia destacados

- `OperacionFinancieraRepositoryTest`: **10**
- `ActivoRepositoryTest`: **6**
- `BonoRepositoryTest`: **6**
- `MovimientoActivoRepositoryTest`: **6**

## Última suite general confirmada

**400/400 tests en verde**, con `Failures: 0`, `Errors: 0`, `Skipped: 0` y `BUILD SUCCESS`.

La última ejecución general confirmada se realizó el **26/08/2026 a las 18:42:57 -03:00** y finalizó correctamente, con una duración de **16:05 min**.

## Regla de cierre

Cada Build debe quedar registrado con cambios principales, tests ejecutados, resultado, commit asociado cuando exista y próximo paso. La documentación debe reflejar el estado real de `feature/operacion-financiera` antes de iniciar un nuevo bloque.
