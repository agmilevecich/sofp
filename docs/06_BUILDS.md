# SOFP — Historial de Builds

## Build 058 — Caso de uso de compra de activo

**Estado: COMPLETADO Y VALIDADO.**

Se implementó el caso de uso de compra de activo mediante `OperacionFinancieraService.comprarActivo(...)`.

Se incorporó y validó:

- creación de una `OperacionFinanciera` de tipo `COMPRA`;
- movimiento monetario `EGRESO` en la cuenta de origen;
- movimiento `MovimientoActivo.COMPRA` asociado al activo;
- cálculo del importe como `cantidad × precioUnitario`;
- validaciones de parámetros obligatorios;
- validaciones de cantidad y precio unitario positivos;
- validación de cuenta de origen activa;
- validación de pertenencia de la categoría al perfil correspondiente;
- persistencia y recuperación de la operación y sus movimientos.

Pruebas específicas:

- `OperacionFinancieraCompraServiceTest`: **13/13 tests en verde**.

Suite general:

- Tests run: **419**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Finalización: **27/08/2026 12:45:13 -03:00**
- Duración: **15:09 min**

Durante la validación de persistencia se detectó una diferencia de escala de `BigDecimal` al recuperar valores desde H2. Se ajustaron las comparaciones del test con `compareTo()`, sin modificar la lógica de producción.

El test específico quedó en **13/13 verde** y la suite completa en **419/419 verde**.

## Build 057 — Integridad entre operación financiera y movimiento de activo

**Estado: COMPLETADO Y VALIDADO.**

Se reforzó `OperacionFinanciera` para validar la coherencia entre `TipoOperacionFinanciera` y `MovimientoActivo`, y se adaptó la prueba de persistencia para representar explícitamente una operación de `COMPRA`.

Pruebas específicas: `OperacionFinancieraMovimientoActivoIntegridadTest` **4/4** y `OperacionFinancieraRepositoryTest` **12/12**.

Suite general final: **406/406**, `BUILD SUCCESS`.

## Build 056 — Incorporación del tipo de operación financiera

**Estado: COMPLETADO Y VALIDADO.**

Se incorporó `TipoOperacionFinanciera` para distinguir `TRANSFERENCIA`, `COMPRA` y `VENTA`, y se integró el tipo en `OperacionFinanciera`.

Pruebas específicas: `OperacionFinancieraTest` **17/17**.

Suite general: **400/400**, `BUILD SUCCESS`.

## Build 055 — Refuerzo de integridad de OperacionFinanciera

**Estado: COMPLETADO Y VALIDADO.**

Se reforzó la integridad del dominio para impedir asociaciones incoherentes de movimientos monetarios.

Pruebas específicas: **18/18**.

Suite general: **397/397**, `BUILD SUCCESS`.

## Build 054 — Incorporación del servicio de posición de activo

**Estado: COMPLETADO Y VALIDADO.**

Se incorporó `PosicionActivoService`, junto con la búsqueda de movimientos de un activo y el calculador de posición.

## Build 053 — Incorporación de MovimientoActivo

**Estado: COMPLETADO Y VALIDADO.**

Se incorporó el modelo de movimientos específicos de activos, `MovimientoActivoRepository` y su persistencia JPA.

Pruebas específicas: **17**.

Suite general: **370/370**, `BUILD SUCCESS`.

## Builds anteriores

Los Builds 001–052 permanecen registrados en el historial previo del proyecto.

## Estado actual

El último Build cerrado es **Build 058**.

La última suite general confirmada es **419/419 tests en verde**, con `Failures: 0`, `Errors: 0`, `Skipped: 0` y `BUILD SUCCESS`.

La documentación de continuidad se mantiene en `feature/operacion-financiera`.

## Próximo paso

Implementar progresivamente el caso de uso de **venta de activo**, reutilizando la estructura de compra y manteniendo las transferencias existentes estables. Posteriormente se validará la venta contra la posición disponible.

## Regla de cierre

Cada Build debe quedar registrado con cambios principales, tests ejecutados, resultado, commit asociado cuando exista y próximo paso. La documentación debe mantenerse sincronizada con el código real y permanecer en la misma rama de trabajo.
