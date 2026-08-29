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

Suite general ejecutada desde IntelliJ IDEA el **27/08/2026 15:24:11 -03:00**:

- Tests run: **433**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **17:35 min**

**Build 059 queda cerrado y validado.**

## Validaciones posteriores al Build 059

Las etapas posteriores se registran como validaciones y cierres funcionales, sin inventar nueva numeración de Build.

### Identificación de activos por símbolo

Se incorporó identificación por símbolo en `Activo` y `Bono`, junto con búsquedas por símbolo en sus repositorios y cobertura de unicidad en persistencia.

La etapa quedó integrada y validada en `main`.

### Cartera de activos

Se incorporó el listado de movimientos por perfil financiero, agrupación de movimientos por activo y cálculo de posiciones mediante `CalculadorPosicionActivo`.

La etapa quedó integrada y validada en `main` mediante fast-forward.

### Costo promedio de posición activa

Se incorporó en `PosicionActivo` el costo de adquisición acumulado, precio promedio y costo de adquisición remanente después de ventas.

Tests específicos de `PosicionActivoTest`: **8/8 tests en verde**.

Commits principales:

- `da09ef0` — `feat: calcular costo promedio de posicion activa`;
- `6cb038b` — `test: cubrir costo promedio de posicion activa`.

La etapa quedó integrada en `main` mediante fast-forward.

### Valorización de posición activa

Se incorporó `ValorizacionPosicionActivo` para calcular valor actual, ganancia o pérdida y rendimiento porcentual a partir de un precio actual informado.

Tests específicos de `ValorizacionPosicionActivoTest`: **8/8 tests en verde**.

Commits principales:

- `ef19486` — `feat: agregar valorizacion de posicion activa`;
- `7379570` — `test: cubrir valorizacion de posicion activa`.

La etapa quedó integrada en `main` mediante fast-forward.

### Reportes de cartera y evolución histórica de saldos

Se incorporaron posteriormente:

- reporte de cartera de activos;
- detalle de composición de cartera;
- detalle de movimientos de cartera;
- evolución histórica del saldo de una cuenta;
- integración de la evolución histórica en `CuentaService`.

La cobertura específica de estas etapas quedó incorporada en la suite general.

La etapa `feature/reportes-cartera` quedó cerrada y posteriormente integrada en `main` mediante fast-forward.

### Suite general vigente

Suite completa ejecutada desde IntelliJ IDEA el **29/08/2026 13:29:56 -03:00**:

- Tests run: **480**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **12:23 min**

La validación global vigente queda en **480/480 tests en verde**.

## Builds anteriores

Los Builds 001–058 permanecen registrados en el historial previo del proyecto.

## Estado actual

El último Build numerado cerrado es **Build 059**. Las etapas funcionales posteriores también fueron implementadas, validadas e integradas en `main`.

## Próximo paso

Definir la siguiente evolución funcional a partir del estado real de `main`, revisando código, entidades, repositorios, servicios, tests y reglas de negocio. No existe actualmente una feature pendiente de integración.

## Regla de cierre

Cada Build debe quedar registrado con cambios principales, tests ejecutados, resultado, commit asociado cuando exista y próximo paso. Las validaciones posteriores que todavía no constituyen un Build nuevo se registran separadamente y no se inventa numeración.
