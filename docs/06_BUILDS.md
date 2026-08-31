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

### Valorización de posición activa

Se incorporó `ValorizacionPosicionActivo` para valor actual, ganancia/pérdida y rendimiento porcentual.

`ValorizacionPosicionActivoTest`: **8/8 tests en verde**.

Commits principales:

- `ef19486` — `feat: agregar valorizacion de posicion activa`;
- `7379570` — `test: cubrir valorizacion de posicion activa`.

### Reportes, evolución histórica y seguridad de perfil

Posteriormente se incorporaron y validaron:

- reporte de cartera de activos;
- composición valorizada;
- detalle de movimientos;
- evolución histórica del saldo de una cuenta;
- integración de la evolución histórica en `CuentaService`;
- seguridad de operaciones de `PerfilFinanciero` mediante validación de propietario.

Las features `feature/reportes-cartera` y `feature/seguridad-perfil-financiero` quedaron integradas mediante fast-forward.

## Etapa posterior — Seguridad y aislamiento por perfil

Se creó la rama `feature/seguridad-aislamiento-datos` para corregir los hallazgos de la auditoría transversal de seguridad.

Se implementaron y validaron:

- autorización de operaciones mutables de `CuentaService`;
- autorización de operaciones mutables de `CategoriaService`;
- autorización de operaciones mutables de `MovimientoService`;
- autorización de `OperacionFinancieraService` por usuario solicitante;
- lecturas por ID y listados protegidos por propietario;
- cálculo de saldo y evolución contextualizados por usuario;
- altas de cuentas, categorías, movimientos y perfiles con validación de propietario;
- aislamiento de posiciones y cartera por usuario/perfil;
- cierre de caminos internos que podían saltar las validaciones públicas;
- cobertura transversal mediante `AislamientoDatosServiceTest`.

## Validación final de seguridad

El **31/08/2026** se ejecutó primero `AislamientoDatosServiceTest`: **7/7 tests en verde**.

Luego se ejecutó la suite general:

- Tests run: **512**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **15:25 min**

La validación global vigente es **512/512 tests en verde**.

Durante la primera ejecución del test específico hubo 7 fallos por un fixture que generaba un código de moneda de 17 caracteres para una columna de máximo 10. Se corrigió el fixture sin alterar código de negocio y la segunda ejecución quedó completamente verde.

## Estado actual

La implementación y los tests de la auditoría transversal de seguridad están completados y validados. La rama `feature/seguridad-aislamiento-datos` permanece separada de `main`.

## Próximo paso

Realizar el cierre técnico del repositorio (`git status`, `git diff --check` y comparación final contra `main`). No hacer merge a `main` automáticamente.
