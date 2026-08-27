# SOFP — Historial de Builds

## Build 059 — Suite general posterior a venta y posición

**Estado: COMPLETADO Y VALIDADO.**

Se ejecutó la suite general después de completar la cobertura de venta de activo, persistencia de relaciones e integración con posición.

Pruebas específicas previas:

- `OperacionFinancieraTest`: **17/17 tests en verde**.
- `OperacionFinancieraServiceTest`: **22/22 tests en verde**.
- `OperacionFinancieraCompraServiceTest`: **13/13 tests en verde**.
- `OperacionFinancieraVentaServiceTest`: **13/13 tests en verde**.
- `PosicionActivoServiceTest`: **4/4 tests en verde**.

Validaciones destacadas:

- Venta produce `VENTA` + `INGRESO` + `MovimientoActivo.VENTA`.
- Las relaciones de `Movimiento` y `MovimientoActivo` con `OperacionFinanciera` se conservan después de recuperar desde persistencia.
- Compra de 100 unidades seguida de venta de 30 produce posición final de **70 unidades** mediante los servicios reales.

Suite general ejecutada desde IntelliJ IDEA el **27/08/2026 15:24:11 -03:00**:

- Tests run: **433**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **17:35 min**

**Build 059 queda cerrado y validado.**

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

El último Build cerrado es **Build 059**.

La última suite general confirmada es **433/433 tests en verde**, con `Failures: 0`, `Errors: 0`, `Skipped: 0` y `BUILD SUCCESS`.

La documentación de continuidad se mantiene en `feature/operacion-financiera`.

## Próximo paso

Revisión final de `feature/operacion-financiera` contra `main`, incluyendo commits, archivos modificados y diferencia funcional. Luego determinar si la feature está lista para preparar el merge, sin modificar `main` automáticamente.

## Regla de cierre

Cada Build debe quedar registrado con cambios principales, tests ejecutados, resultado, commit asociado cuando exista y próximo paso. La documentación debe mantenerse sincronizada con el código real y permanecer en la misma rama de trabajo.
