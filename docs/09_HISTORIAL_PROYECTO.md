# SOFP — Historial del proyecto

Este documento conserva una línea temporal resumida de avances y puntos de continuidad.

## 2026-08-30

### Estado post-integración de seguridad de PerfilFinanciero

La feature `feature/seguridad-perfil-financiero` quedó integrada en `main` mediante **fast-forward**.

Commit final de la feature:

- `7d6632f` — `docs: actualizar revision final de seguridad de perfil`.

El estado verificado quedó con `main`, `github/main`, `bitbucket/main` y la rama histórica de seguridad en `7d6632f` según la última comprobación local informada por el usuario.

### Validación global

Suite general ejecutada desde IntelliJ IDEA el **29/08/2026 20:00:23 -03:00**:

- **486/486 tests en verde**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración: **15:50 min**.

## 2026-08-29

### Seguridad de PerfilFinanciero

Se implementó autorización por propietario en `PerfilFinancieroService` para:

- `cambiarDescripcion`;
- `activar`;
- `desactivar`.

La cobertura específica quedó en **19/19 tests en verde**.

Commits funcionales y de cobertura:

- `6478262` — `feat: proteger operaciones de perfil por propietario`;
- `8d3c775` — `test: cubrir autorizacion de operaciones de perfil`.

Commits documentales:

- `c07393e` — `docs: documentar seguridad de perfil financiero`;
- `b6194ae` — `docs: actualizar estado final de seguridad de perfil`;
- `b2fbcfb` — `docs: corregir conteo de commits de seguridad de perfil`;
- `7d6632f` — `docs: actualizar revision final de seguridad de perfil`.

La feature quedó cerrada e integrada mediante fast-forward.

### Feature `reportes-cartera` — Cierre e integración

Se completó el bloque funcional de reportes de cartera y evolución histórica de saldos.

Incluye:

- reporte de cartera de activos;
- composición valorizada de cartera;
- detalle de movimientos de cartera;
- evolución histórica del saldo de una cuenta;
- integración de la evolución histórica en `CuentaService`.

La feature `feature/reportes-cartera` quedó cerrada e integrada mediante fast-forward.

La suite general anterior fue de **480/480 tests en verde**.

## 2026-08-28

### Evolución histórica de saldo

- `405cde6` — `feat: agregar punto de evolucion historica de saldo`;
- `b11533b` — `feat: integrar evolucion historica de saldo en CuentaService`;
- `9a03c95` — `test: cubrir evolucion historica de saldo`;
- `3c14bdf` — `docs: completar evolucion historica de saldos en roadmap`.

La funcionalidad reconstruye el saldo acumulado después de cada movimiento respetando el orden cronológico determinista.

## 2026-08-27

### Build 059 — Suite general posterior a venta y posición

- Se completó la validación del bloque de compra y venta de activos.
- `OperacionFinancieraVentaServiceTest`: **13/13 tests en verde**.
- `PosicionActivoServiceTest`: **4/4 tests en verde**.
- Se verificaron relaciones persistidas de `Movimiento` y `MovimientoActivo` con `OperacionFinanciera`.
- Se agregó una prueba de integración de compra y venta con posición final de 70 unidades.
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

`main` es la rama estable. No existe una feature pendiente de integración.

## Próximo punto de trabajo

Definir la siguiente evolución funcional a partir del código real de `main`, revisando entidades, repositorios, servicios, tests y reglas de negocio antes de crear una nueva rama.

## Regla histórica

Los hechos importantes se registran con Build y/o commit cuando corresponde. Los chats aportan contexto, pero no sustituyen el estado real del repositorio.
