# SOFP — Tests

Este documento registra la evolución de la batería de tests y los resultados verificados por Build.

## Estado actual

### Build 058 — Caso de uso de compra de activo — Cerrado

Se implementó y validó `OperacionFinancieraService.comprarActivo(...)`.

Pruebas específicas del bloque:

- `OperacionFinancieraCompraServiceTest`: **13/13 tests en verde**.

Suite general final:

- Tests run: **419**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Finalizada: **27/08/2026 12:45:13 -03:00**
- Duración: **15:09 min**

Durante la prueba de persistencia se detectaron diferencias de escala de `BigDecimal` al recuperar valores desde H2. Las comparaciones afectadas fueron adaptadas mediante `compareTo()`, sin modificar producción.

### Build 057 — Integridad entre operación financiera y movimiento de activo — Cerrado

Pruebas específicas:

- `OperacionFinancieraMovimientoActivoIntegridadTest`: **4/4**.
- `OperacionFinancieraRepositoryTest`: **12/12**.
- Total específico: **16/16**.

Suite general: **406/406**, `BUILD SUCCESS`.

### Build 056 — Incorporación del tipo de operación financiera — Cerrado

Pruebas específicas: `OperacionFinancieraTest` **17/17**.

Suite general: **400/400**, `BUILD SUCCESS`.

### Build 055 — Refuerzo de integridad de OperacionFinanciera — Cerrado

Pruebas específicas: **18/18**.

Suite general: **397/397**, `BUILD SUCCESS`.

### Build 054 — Incorporación del servicio de posición de activo — Cerrado

Se incorporó `PosicionActivoService`, junto con la búsqueda de movimientos de un activo y el calculador de posición.

### Build 053 — Incorporación de MovimientoActivo — Cerrado

Pruebas específicas: **17**.

Suite general: **370/370**, `BUILD SUCCESS`.

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

- `OperacionFinancieraRepositoryTest`: **12**
- `ActivoRepositoryTest`: **6**
- `BonoRepositoryTest`: **6**
- `MovimientoActivoRepositoryTest`: **6**

## Última suite general confirmada

**419/419 tests en verde**, con `Failures: 0`, `Errors: 0`, `Skipped: 0` y `BUILD SUCCESS`.

La última ejecución general confirmada se realizó el **27/08/2026 a las 12:45:13 -03:00** y finalizó correctamente, con una duración de **15:09 min**.

## Próximo bloque

Implementar y probar la **venta de activo**, incluyendo el movimiento monetario `INGRESO`, `MovimientoActivo.VENTA`, cálculo de importe y validaciones específicas. La validación contra la posición disponible se incorporará como control posterior del caso de uso.

## Regla de cierre

Cada Build debe quedar registrado con cambios principales, tests ejecutados, resultado, commit asociado cuando exista y próximo paso. La documentación debe reflejar el estado real de `feature/operacion-financiera` antes de iniciar un nuevo bloque.
