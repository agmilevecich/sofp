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

Suite general ejecutada el **27/08/2026 15:24:11 -03:00**:

- Tests run: **433**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **17:35 min**

## Validaciones posteriores al Build 059

### Identificación por símbolo

Se incorporó identificación por símbolo en `Activo` y `Bono`, búsquedas por símbolo y cobertura de unicidad en persistencia. Etapa integrada y validada en `main`.

### Cartera de activos

Se incorporó el listado de movimientos por perfil financiero, agrupación por activo y cálculo de posiciones mediante `CalculadorPosicionActivo`. Etapa integrada y validada mediante fast-forward.

### Costo promedio de posición activa

Se incorporó costo de adquisición acumulado, precio promedio y costo remanente después de ventas.

`PosicionActivoTest`: **8/8 tests en verde**.

Commits principales:

- `da09ef0` — `feat: calcular costo promedio de posicion activa`;
- `6cb038b` — `test: cubrir costo promedio de posicion activa`.

Etapa integrada mediante fast-forward.

### Valorización de posición activa

Se incorporó `ValorizacionPosicionActivo` para valor actual, ganancia/pérdida y rendimiento porcentual.

`ValorizacionPosicionActivoTest`: **8/8 tests en verde**.

Commits principales:

- `ef19486` — `feat: agregar valorizacion de posicion activa`;
- `7379570` — `test: cubrir valorizacion de posicion activa`.

Etapa integrada mediante fast-forward.

### Reportes, evolución histórica y seguridad de perfil

Posteriormente se incorporaron y validaron:

- reporte de cartera de activos;
- composición valorizada;
- detalle de movimientos;
- evolución histórica del saldo de una cuenta;
- integración de la evolución histórica en `CuentaService`;
- seguridad de operaciones de `PerfilFinanciero` mediante validación de propietario.

La feature `feature/reportes-cartera` quedó integrada mediante fast-forward.

La feature `feature/seguridad-perfil-financiero` también quedó integrada mediante fast-forward.

## Suite general vigente

Suite completa ejecutada desde IntelliJ IDEA el **29/08/2026 20:00:23 -03:00**:

- Tests run: **486**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **15:50 min**

La validación global vigente es **486/486 tests en verde**.

## Builds anteriores

Los Builds 001–058 permanecen registrados en el historial previo del proyecto. Build 059 continúa siendo el último Build numerado cerrado; las validaciones posteriores se registran como etapas funcionales y no se inventa numeración.

## Estado actual

Las funcionalidades posteriores al Build 059 fueron implementadas, validadas e integradas en `main`. La seguridad de `PerfilFinanciero` es el último bloque funcional cerrado.

## Próximo paso

Definir la siguiente evolución funcional a partir del estado real de `main`, revisando código, entidades, repositorios, servicios, tests y reglas de negocio.

## Regla de cierre

Cada Build debe registrar objetivo, cambios, tests y resultado. Las validaciones posteriores se registran separadamente cuando no constituyen un Build nuevo.
