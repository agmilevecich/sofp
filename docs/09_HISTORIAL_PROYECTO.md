# SOFP — Historial del proyecto

Este documento conserva una línea temporal resumida de avances y puntos de continuidad.

## 2026-08-31

### Feature `feature/seguridad-aislamiento-datos` — correcciones de seguridad

A partir de la auditoría transversal de seguridad se creó la rama `feature/seguridad-aislamiento-datos`.

En el último commit funcional/test de esta etapa, `c1f635f`, la rama estaba **11 commits por delante de `main` y 0 por detrás**. Los commits posteriores son únicamente actualizaciones documentales.

Correcciones implementadas:

- autorización de operaciones mutables en `CuentaService`;
- adaptación de `CuentaServiceTest` a autorización por propietario;
- autorización de operaciones mutables en `CategoriaService`;
- corrección de rollback/persistencia detectada en `CategoriaServiceTest`;
- adaptación de `CategoriaServiceTest` al aislamiento por usuario;
- autorización de operaciones mutables en `MovimientoService`;
- adaptación de `MovimientoServiceTest` al aislamiento por usuario;
- aislamiento de posiciones de activos por perfil mediante `MovimientoActivoRepository` y `PosicionActivoService`.

Commits funcionales y de tests:

- `e22f236` — `fix: autorizar operaciones mutables de cuenta`;
- `346f64c` — `test: adaptar CuentaServiceTest a autorización por propietario`;
- `98509e2` — `fix: autorizar operaciones mutables de categoria`;
- `f628337` — `fix: corregir rollback en CategoriaService`;
- `7a40f16` — `test: adaptar CategoriaServiceTest al aislamiento por usuario`;
- `cb745a3` — `fix: corregir persistencia de perfiles en CategoriaServiceTest`;
- `573f3a0` — `test: cubrir aislamiento de posicion de activo por perfil`;
- `18b9286` — `fix: autorizar operaciones mutables de movimiento`;
- `c1f635f` — `test: adaptar MovimientoServiceTest al aislamiento por usuario`.

### Validación global

Suite general ejecutada desde IntelliJ IDEA el **31/08/2026**, finalizada a las **12:46:20 -03:00**:

- **503/503 tests en verde**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración: **15:01 min**.

Este resultado reemplaza como referencia global a las validaciones anteriores de 486, 480 y 433 tests.

### Estado de seguridad

La corrección de aislamiento está avanzada, pero la etapa transversal todavía no está cerrada. Permanecen pendientes `OperacionFinancieraService`, la revisión de lecturas por ID/listados, los caminos alternativos de creación de movimientos y la cobertura específica asociada.

### Actualización documental

Después de la validación se actualizaron los documentos de continuidad de la rama para registrar el estado real, la suite de 503 tests, las correcciones de seguridad realizadas y los pendientes restantes.

## 2026-08-30

### Estado post-integración de seguridad de PerfilFinanciero

La feature `feature/seguridad-perfil-financiero` quedó integrada en `main` mediante **fast-forward**.

Commit final de la feature:

- `7d6632f` — `docs: actualizar revision final de seguridad de perfil`.

## 2026-08-29

### Seguridad de PerfilFinanciero

Se implementó autorización por propietario en `PerfilFinancieroService` para `cambiarDescripcion`, `activar` y `desactivar`.

Cobertura específica: **19/19 tests en verde**.

Commits principales:

- `6478262` — `feat: proteger operaciones de perfil por propietario`;
- `8d3c775` — `test: cubrir autorizacion de operaciones de perfil`.

La feature quedó cerrada e integrada mediante fast-forward.

### Feature `reportes-cartera` — cierre e integración

Se completó el bloque funcional de reportes de cartera y evolución histórica de saldos, incluyendo reporte consolidado, composición valorizada, detalle de movimientos y evolución histórica de una cuenta.

La feature `feature/reportes-cartera` quedó cerrada e integrada mediante fast-forward.

Suite general anterior: **480/480 tests en verde**.

## 2026-08-28

### Evolución histórica de saldo

- `405cde6` — `feat: agregar punto de evolucion historica de saldo`;
- `b11533b` — `feat: integrar evolucion historica de saldo en CuentaService`;
- `9a03c95` — `test: cubrir evolucion historica de saldo`;
- `3c14bdf` — `docs: completar evolucion historica de saldos en roadmap`.

## 2026-08-27

### Build 059 — Suite general posterior a venta y posición

- Se completó la validación del bloque de compra y venta de activos.
- `OperacionFinancieraVentaServiceTest`: **13/13 tests en verde**.
- `PosicionActivoServiceTest`: **4/4 tests en verde**.
- Suite general: **433/433 tests en verde**.
- Build 059 quedó cerrado y validado.

## Etapas cerradas e integradas

- `feature/operacion-financiera`;
- `feature/identificacion-activo`;
- `feature/cartera-activos`;
- `feature/costo-promedio-activo`;
- `feature/valorizacion-posicion-activo`;
- `feature/reportes-cartera`;
- `feature/seguridad-perfil-financiero`.

## Estado actual

`main` es la rama estable. `feature/seguridad-aislamiento-datos` es la rama de trabajo actual y contiene las correcciones de seguridad indicadas arriba.

## Próximo punto de trabajo

Completar los hallazgos restantes de seguridad. Una vez cerrada la etapa, realizar la validación final contra `main` y preparar la siguiente etapa, que será la interfaz Swing.

## Regla histórica

Los hechos importantes se registran con Build y/o commit cuando corresponde. Los chats aportan contexto, pero no sustituyen el estado real del repositorio.
