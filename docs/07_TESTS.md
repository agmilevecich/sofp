# SOFP — Tests

Este documento registra la evolución de la batería de tests y los resultados verificados.

## Validación global más reciente

Suite completa ejecutada desde IntelliJ IDEA el **27/08/2026 19:52:48 -03:00**.

- Tests run: **435**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- Resultado: **BUILD SUCCESS**
- Duración: **19:08 min**

Esta es la suite general más reciente confirmada y debe considerarse la validación global vigente.

## Validaciones de identificación por símbolo

Se incorporó búsqueda de activos por símbolo y posteriormente búsqueda de bonos por símbolo.

- `ActivoRepository` — búsqueda por símbolo implementada y cubierta por test.
- `BonoRepository` — búsqueda por símbolo implementada y cubierta por test.
- Los tests específicos ejecutados desde IntelliJ fueron informados como verdes.
- Los tests también cubren el rechazo de `null` en las búsquedas por símbolo.

Commits:

- `3f6c776` — `feat: agregar busqueda de activo por simbolo`
- `6179f2d` — `test: cubrir busqueda de activo por simbolo`
- `354e0b3` — `feat: agregar busqueda de bono por simbolo`
- `976aff7` — `test: cubrir busqueda de bono por simbolo`

## Adaptación al símbolo obligatorio

Antes de incorporar las búsquedas se adaptaron los constructores y tests afectados por el nuevo atributo identificador:

- `OperacionFinancieraMovimientoActivoIntegridadTest`
- `MovimientoActivoRepositoryTest`
- `PosicionActivoTest`
- `OperacionFinancieraRepositoryTest`
- `MovimientoActivoTest`
- `ActivoRepositoryTest`
- `OperacionFinancieraTest`
- `OperacionFinancieraVentaServiceTest`
- `CalculadorPosicionActivoTest`
- `OperacionFinancieraCompraServiceTest`
- `PosicionActivoServiceTest`

La suite general posterior quedó en **435/435**.

## Builds anteriores

Los Builds 001–059 permanecen registrados en el historial del proyecto. Build 059 fue el cierre de la etapa de compra, venta y posición de activos.

## Regla de cierre

No registrar resultados de tests que no hayan sido realmente ejecutados. Cada cambio funcional debe contar con cobertura específica y, cuando corresponda, validación de la suite general.

## Próximo bloque

Cubrir la regla de unicidad del símbolo en persistencia, si la implementación actual mantiene dicha restricción, y continuar luego con el siguiente caso de uso de identificación de instrumentos.
