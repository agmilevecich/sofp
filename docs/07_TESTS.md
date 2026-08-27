# SOFP — Tests

Este documento registra la evolución de la batería de tests y los resultados verificados.

## Estado actual

### Build 059 — Suite general posterior a venta y posición — Cerrado

Suite completa ejecutada desde IntelliJ IDEA el **27/08/2026 15:24:11 -03:00**.

- Tests run: **433**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- Resultado: **BUILD SUCCESS**
- Duración: **17:35 min**

Los 433 tests fueron ejecutados después de los cambios recientes de venta, persistencia de relaciones e integración con posición. Todos finalizaron correctamente.

### Validaciones específicas previas

- `OperacionFinancieraTest`: **17/17 tests en verde**.
- `OperacionFinancieraServiceTest`: **22/22 tests en verde**.
- `OperacionFinancieraCompraServiceTest`: **13/13 tests en verde**.
- `OperacionFinancieraVentaServiceTest`: **13/13 tests en verde**.
- Total de los cuatro archivos: **65/65 tests en verde**.
- `PosicionActivoServiceTest`: **4/4 tests en verde**.

`OperacionFinancieraVentaServiceTest` verifica también las relaciones de `Movimiento` y `MovimientoActivo` después de recuperar la operación desde persistencia.

`PosicionActivoServiceTest` valida mediante los servicios reales una compra de 100 unidades seguida de una venta de 30, obteniendo una posición final de **70 unidades**.

## Última suite general confirmada

**433/433 tests en verde**, con `Failures: 0`, `Errors: 0`, `Skipped: 0` y `BUILD SUCCESS`.

Esta es la suite general más reciente y debe considerarse la validación global vigente de la feature.

### Build 058 — Caso de uso de compra de activo — Cerrado

Se implementó y validó `OperacionFinancieraService.comprarActivo(...)`.

Prueba específica:

- `OperacionFinancieraCompraServiceTest`: **13/13 tests en verde**.

Suite general:

- Tests run: **419**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Finalizada: **27/08/2026 12:45:13 -03:00**
- Duración: **15:09 min**

Durante la prueba de persistencia se detectaron diferencias de escala de `BigDecimal` al recuperar valores desde H2. Las comparaciones afectadas fueron adaptadas mediante `compareTo()`, sin modificar producción.

### Builds anteriores

Los Builds 001–057 y sus resultados permanecen registrados en el historial del proyecto.

## Conteo registrado de tests de services

- `CategoriaServiceTest`: **21**
- `CuentaServiceTest`: **47**
- `InstitucionFinancieraServiceTest`: **26**
- `MonedaServiceTest`: **17**
- `MovimientoServiceTest`: **50**
- `PerfilFinancieroServiceTest`: **13**
- `UsuarioServiceTest`: **15**
- `OperacionFinancieraServiceTest`: **22**

Total registrado de tests de services: **211**.

## Regla de cierre

Cada Build debe quedar registrado con cambios principales, tests ejecutados, resultado, commit asociado cuando exista y próximo paso. La documentación debe reflejar el estado real de `feature/operacion-financiera` antes de iniciar un nuevo bloque.

## Próximo bloque

Revisión final de `feature/operacion-financiera` contra `main`, incluyendo commits, archivos modificados y diferencia funcional. Luego determinar si la feature está lista para preparar el merge, sin modificar `main` automáticamente.
