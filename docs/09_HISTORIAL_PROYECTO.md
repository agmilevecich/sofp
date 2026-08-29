# SOFP — Historial del proyecto

Este documento conserva una línea temporal resumida de decisiones, avances y puntos de continuidad.

## 2026-08-29

### Feature `reportes-cartera` — Cierre e integración

Se completó el bloque funcional de reportes de cartera y evolución histórica de saldos.

Cambios principales:

- reporte de cartera de activos;
- composición valorizada de cartera;
- detalle de movimientos de cartera;
- evolución histórica del saldo de una cuenta;
- integración de la evolución histórica en `CuentaService`;
- cobertura específica de las funcionalidades;
- suite general vigente de **480/480 tests en verde**.

Último commit funcional/documental de la feature antes de la actualización post-merge:

- `0b73e87` — `docs: cerrar pendientes funcionales de reportes de cartera`.

### Integración en `main`

`feature/reportes-cartera` fue integrada en `main` mediante **fast-forward**.

Estado verificado en GitHub:

- `main` y `feature/reportes-cartera` coincidieron en `0b73e87`;
- diferencia funcional: **0 commits adelante / 0 commits detrás**;
- ambas ramas quedaron sobre el mismo estado funcional.

Posteriormente se actualizaron los documentos de continuidad directamente en `main` para registrar el cierre post-merge.

### Validación global

Suite general ejecutada desde IntelliJ IDEA el **29/08/2026 13:29:56 -03:00**:

- **480/480 tests en verde**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración: **12:23 min**.

## 2026-08-28

### Evolución histórica de saldo

- `405cde6` — `feat: agregar punto de evolucion historica de saldo`.
- `b11533b` — `feat: integrar evolucion historica de saldo en CuentaService`.
- `9a03c95` — `test: cubrir evolucion historica de saldo`.
- `3c14bdf` — `docs: completar evolucion historica de saldos en roadmap`.

La funcionalidad permite reconstruir el saldo acumulado después de cada movimiento de una cuenta, respetando el orden cronológico determinista.

## 2026-08-27

### Reportes de movimientos de cartera

Se incorporó el detalle de movimientos de cartera y su integración en `CarteraActivoService`.

### Build 059 — Suite general posterior a venta y posición — Cerrado

- Se completó la validación del bloque de compra y venta de activos.
- `OperacionFinancieraVentaServiceTest`: **13/13 tests en verde**.
- `PosicionActivoServiceTest`: **4/4 tests en verde**.
- Se verificaron las relaciones persistidas de `Movimiento` y `MovimientoActivo` con `OperacionFinanciera` después de recuperar la operación.
- Se agregó una prueba de integración que ejecuta una compra de 100 y una venta de 30 mediante `OperacionFinancieraService`, y verifica una posición final de **70 unidades** mediante `PosicionActivoService`.
- Suite general: **433/433 tests en verde**.
- `Failures: 0`, `Errors: 0`, `Skipped: 0`, `BUILD SUCCESS`.
- Build 059 quedó cerrado y validado.

## Etapas cerradas e integradas

Las siguientes etapas quedaron integradas en `main`:

- `feature/operacion-financiera`;
- `feature/identificacion-activo`;
- `feature/cartera-activos`;
- `feature/costo-promedio-activo`;
- `feature/valorizacion-posicion-activo`;
- `feature/reportes-cartera`.

## Estado actual

`main` es la rama estable. El estado funcional de reportes de cartera quedó integrado mediante fast-forward.

No existe una feature funcional pendiente de integración en este punto.

## Próximo punto de trabajo

Definir la siguiente evolución funcional a partir del código real de `main`, revisando entidades, repositorios, servicios, tests y reglas de negocio antes de crear una nueva rama o modificar funcionalidad.

## Regla histórica

Los hechos importantes se registran con Build y/o commit cuando corresponde. Los chats aportan contexto, pero no sustituyen el estado real del repositorio.
