# SOFP — Historial del proyecto

Este documento conserva una línea temporal resumida de decisiones, avances y puntos de continuidad.

## 2026-08-27

### Build 059 — Suite general posterior a venta y posición — Cerrado

- Se completó la validación del bloque de compra y venta de activos.
- `OperacionFinancieraVentaServiceTest`: **13/13 tests en verde**.
- `PosicionActivoServiceTest`: **4/4 tests en verde**.
- Se verificaron las relaciones persistidas de `Movimiento` y `MovimientoActivo` con `OperacionFinanciera` después de recuperar la operación.
- Se agregó una prueba de integración que ejecuta una compra de 100 y una venta de 30 mediante `OperacionFinancieraService`, y verifica una posición final de **70 unidades** mediante `PosicionActivoService`.
- Suite general ejecutada desde IntelliJ IDEA: **433/433 tests en verde**.
- `Failures: 0`, `Errors: 0`, `Skipped: 0`, `BUILD SUCCESS`.
- Finalizada: **15:24:11 -03:00**.
- Duración: **17:35 min**.
- `main` no fue modificado.
- La documentación de continuidad permanece en `feature/operacion-financiera`.
- **Build 059 queda cerrado y validado.**

### Cierre de la etapa de OperacionFinanciera

- Compra de activo implementada y validada.
- Venta de activo implementada y validada.
- Persistencia de operación y movimientos validada.
- Posición de activo validada.
- Integración compra + venta + posición validada.
- Suite general posterior a los cambios: **433/433**.

## 2026-08-25

### Build 052 — Incorporación de Bono como especialización de Activo — Cerrado

- Se incorporó `Bono` como primera especialización de `Activo`.
- `Bono` hereda de `Activo` y actualmente no agrega atributos financieros específicos.
- Se incorporó `BonoRepository` para persistencia JPA.
- `BonoTest`: **5/5**.
- `BonoRepositoryTest`: **6/6**.
- Suite general: **353/353**, `BUILD SUCCESS`.
- Se mantuvo como única rama de trabajo `feature/operacion-financiera`.
- `main` no fue modificado.

### Build 051 — Incorporación de Activo y persistencia JPA — Cerrado

- Se incorporó `Activo` y `ActivoRepository`.
- `ActivoTest`: **8/8**.
- `ActivoRepositoryTest`: **6/6**.
- Suite general: **342/342**, `BUILD SUCCESS`.

### Build 050 — Ampliación de cobertura de OperacionFinancieraService — Cerrado

- `OperacionFinancieraServiceTest`: **22/22**.
- Suite general: **328/328**, `BUILD SUCCESS`.

## 2026-08-23

### Build 049 — Asociación de Movimiento con OperacionFinanciera — Cerrado

- Se incorporó la relación persistente entre `Movimiento` y `OperacionFinanciera`.
- `OperacionFinancieraTest`: **14/14**.
- Suite general: **326/326**.

### Build 048 — OperacionFinancieraRepository — Cerrado

- Se incorporó `OperacionFinancieraRepository` y su integración con el servicio.
- `OperacionFinancieraRepositoryTest`: **10/10**.
- Suite general: **319/319**.

## 2026-08-21

### Build 047 — Cobertura de OperacionFinancieraService — Cerrado

- `OperacionFinancieraServiceTest`: **20/20**.
- Suite general: **309/309**.

### Build 046 — Implementación de OperacionFinancieraService — Cerrado

- Transferencia coordinada mediante `OperacionFinanciera`, `EGRESO` e `INGRESO`.
- Suite general: **300/300**.

## 2026-08-20

### Build 045 — Dominio de OperacionFinanciera — Cerrado

- Se incorporó `OperacionFinanciera`.
- Suite general: **289/289**.

### Build 044 — Cobertura de Movimiento — Cerrado

- `MovimientoTest`: **27/27**.
- Suite general: **282/282**.

## Estado actual

El último bloque cerrado es **Build 059 — Suite general posterior a venta y posición**.

La suite general más reciente es **433/433 tests en verde**.

La rama de trabajo y continuidad es `feature/operacion-financiera`. La rama `docs/continuidad-sofp` fue eliminada y no debe reincorporarse al flujo.

`main` permanece separado de la feature hasta completar la revisión final y decidir el merge.

## Próximo punto de trabajo

Revisión final de la feature contra `main`, incluyendo:

- commits;
- archivos modificados;
- `git status`;
- `git diff`;
- `git diff --check`;
- coherencia de documentación;
- estrategia de merge.

No realizar el merge automáticamente.

## Regla histórica

Los hechos importantes se registran con Build y/o commit cuando corresponda. Los chats aportan contexto, pero no sustituyen el estado real del repositorio.
