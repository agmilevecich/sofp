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

Correcciones implementadas hasta el estado actual:

- autorización de operaciones mutables de `CuentaService`;
- adaptación de `CuentaServiceTest` a autorización por propietario;
- autorización de operaciones mutables de `CategoriaService`;
- corrección de rollback/persistencia detectada al adaptar `CategoriaServiceTest`;
- autorización de operaciones mutables de `MovimientoService`;
- adaptación de `MovimientoServiceTest` a las nuevas reglas;
- aislamiento de `PosicionActivoService` por perfil financiero mediante consulta filtrada de `MovimientoActivoRepository`.

Commits relevantes:

- `e22f236` — `fix: autorizar operaciones mutables de cuenta`;
- `346f64c` — `test: adaptar CuentaServiceTest a autorización por propietario`;
- `98509e2` — `fix: autorizar operaciones mutables de categoria`;
- `f628337` — `fix: corregir rollback en CategoriaService`;
- `7a40f16` — `test: adaptar CategoriaServiceTest al aislamiento por usuario`;
- `cb745a3` — `fix: corregir persistencia de perfiles en CategoriaServiceTest`;
- `18b9286` — `fix: autorizar operaciones mutables de movimiento`;
- `c1f635f` — `test: adaptar MovimientoServiceTest al aislamiento por usuario`.

## Suite general vigente

Suite completa ejecutada desde IntelliJ IDEA el **31/08/2026**, finalizada a las **12:46:20 -03:00**:

- Tests run: **503**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **15:01 min**

La validación global vigente es **503/503 tests en verde**.

## Estado actual

La rama `feature/seguridad-aislamiento-datos` está 11 commits por delante de `main` y 0 por detrás. El último commit es `c1f635f`.

La suite general confirma que las correcciones actuales no rompen el comportamiento cubierto por los 503 tests existentes.

La etapa de seguridad transversal todavía no se considera cerrada porque permanecen pendientes `OperacionFinancieraService`, la revisión de aislamiento de lecturas y los caminos alternativos de creación de movimientos.

## Próximo paso

Continuar con los hallazgos restantes de seguridad. No inventar un nuevo Build numerado hasta que exista un bloque funcional que justifique uno; las correcciones de esta feature se registran por commit y validación.

Una vez cerrada la seguridad transversal, revisar nuevamente el código y los tests antes de iniciar la etapa de interfaz Swing.
