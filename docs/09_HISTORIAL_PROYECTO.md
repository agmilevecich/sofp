# SOFP — Historial del proyecto

Este documento conserva una línea temporal resumida de decisiones, avances y puntos de continuidad.

## 2026-08-25

### Build 051 — Incorporación de Activo y persistencia JPA — Cerrado

- Se incorporó `Activo` como entidad base para el futuro bloque de inversiones.
- `Activo` hereda de `EntidadAuditable` y contiene actualmente `nombre` y `Moneda` obligatorios.
- Se incorporaron métodos de dominio para cambiar nombre y moneda.
- Se incorporó `ActivoRepository` para persistencia JPA.
- `ActivoTest`: **8/8 tests en verde**.
- `ActivoRepositoryTest`: **6/6 tests en verde**.
- La primera ejecución de `ActivoRepositoryTest` detectó la necesidad de registrar `Activo` en la configuración de persistencia utilizada por los tests; corregida esa configuración, los 6 tests quedaron en verde.
- La suite general quedó en **342/342 tests en verde**, con `Failures: 0`, `Errors: 0`, `Skipped: 0` y `BUILD SUCCESS`.
- La ejecución general finalizó a las **16:30:22 -03:00** y tuvo una duración de **16:51 min**.
- Commits del bloque: `0793126`, `5270e31`, `1624f8c` y `70bdbf7`.
- El bloque quedó publicado y sincronizado en GitHub y Bitbucket.
- Working tree quedó limpio.
- `main` no fue modificado.
- **Build 051 queda cerrado y validado.**

### Build 050 — Ampliación de cobertura de OperacionFinancieraService — Cerrado

- Se agregaron dos pruebas específicas a `OperacionFinancieraServiceTest`.
- Se verificó que los dos movimientos generados por una transferencia quedan asociados a la misma `OperacionFinanciera`.
- Se verificó el rechazo de la misma cuenta como origen y destino y la ausencia de movimientos persistidos ante el rechazo.
- `OperacionFinancieraServiceTest` pasó de **20/20 a 22/22 tests en verde**.
- La suite general quedó en **328/328 tests en verde**, con `Failures: 0`, `Errors: 0`, `Skipped: 0` y `BUILD SUCCESS`.
- La ejecución general finalizó a las **10:47:02 -03:00** y tuvo una duración de **12:04 min**.
- Commit: `1f306e4` — `test: ampliar cobertura de OperacionFinancieraService`.
- **Build 050 queda cerrado y validado.**

## 2026-08-23

### Build 049 — Asociación de Movimiento con OperacionFinanciera — Cerrado

- Se incorporó la relación persistente entre `Movimiento` y `OperacionFinanciera`.
- `Movimiento` incorpora `@ManyToOne` y la columna `operacion_financiera_id`.
- `OperacionFinanciera` incorpora `@OneToMany(mappedBy = "operacionFinanciera")`.
- La colección de movimientos se expone como una lista no modificable.
- Una `OperacionFinanciera` admite como máximo dos movimientos.
- Se rechazan movimientos nulos, repetidos y movimientos que ya pertenecen a otra operación financiera.
- `OperacionFinancieraService` asocia el `EGRESO` y el `INGRESO` a la operación antes de persistirlos.
- `OperacionFinancieraTest` pasó de **7 a 14 tests en verde**.
- La suite general quedó en **326/326 tests en verde**.
- Commit funcional: `0f64fa9` — `feat: asociar movimientos a operacion financiera`.
- **Build 049 queda cerrado y validado.**

### Build 048 — Implementación de OperacionFinancieraRepository — Cerrado

- Se incorporó `OperacionFinancieraRepository`.
- Se integró el repositorio en `OperacionFinancieraService`.
- `OperacionFinancieraRepositoryTest`: **10/10 tests en verde**.
- `OperacionFinancieraServiceTest`: **20/20 tests en verde**.
- `OperacionFinancieraTest`: **7/7 tests en verde**.
- La suite general quedó en **319/319 tests en verde**.
- **Build 048 queda cerrado y validado.**

## 2026-08-21

### Build 047 — Completar cobertura de OperacionFinancieraService — Cerrado

- Se completó la cobertura de `OperacionFinancieraServiceTest`.
- Se incorporaron pruebas para cuentas inactivas, categorías pertenecientes a otros perfiles, monedas diferentes, fecha/hora nula, rechazo de descripción nula y ausencia de persistencia de movimientos ante operaciones rechazadas.
- `OperacionFinancieraServiceTest` quedó en **20/20 tests en verde**.
- La suite completa quedó en **309/309 tests en verde**.
- Commit: `615161c` — `test: completar cobertura de OperacionFinancieraService`.
- **Build 047 queda cerrado y validado.**

### Build 046 — Implementación de OperacionFinancieraService — Cerrado

- Se incorporó `OperacionFinancieraService`.
- El servicio materializa una transferencia mediante una `OperacionFinanciera`, un `EGRESO` en la cuenta origen y un `INGRESO` en la cuenta destino.
- La persistencia se coordina dentro de una única transacción.
- `OperacionFinancieraServiceTest`: **20/20 tests en verde**.
- La suite completa quedó en **300/300 tests en verde**.
- **Build 046 queda cerrado y validado.**

## 2026-08-20

### Build 045 — Implementación del dominio de OperacionFinanciera — Cerrado

- Se incorporó `OperacionFinanciera` como entidad de dominio para representar una transferencia entre cuenta origen y cuenta destino.
- Se incorporó la regla de negocio que impide utilizar la misma cuenta como origen y destino.
- `OperacionFinancieraTest`: **7/7 tests en verde**.
- La suite general quedó en **289/289 tests en verde**.
- Commit: `1f650dc` — `feat: implementar dominio de operacion financiera`.
- **Build 045 queda cerrado y validado.**

### Build 044 — Ampliación de cobertura de Movimiento — Cerrado

- Se amplió `MovimientoTest` hasta **27/27 tests en verde**.
- La suite general quedó en **282/282 tests en verde**.
- Commit: `6f53f79` — `test: ampliar cobertura de Movimiento`.
- **Build 044 queda cerrado y validado.**

## Estado actual

El último bloque trabajado es **Build 051 — Incorporación de Activo y persistencia JPA — cerrado**.

`ActivoTest`: **8/8 tests en verde**.

`ActivoRepositoryTest`: **6/6 tests en verde**.

La suite completa está en **342/342 tests en verde**, con `Failures: 0`, `Errors: 0`, `Skipped: 0` y `BUILD SUCCESS`.

La rama `feature/operacion-financiera` está sincronizada entre GitHub y Bitbucket.

La documentación de continuidad se mantiene exclusivamente en `feature/operacion-financiera`. La rama `docs/continuidad-sofp` fue eliminada.

`main` permanece separado de estos cambios.

## Próximo punto de trabajo

Definir la primera especialización de `Activo` para el bloque de inversiones, revisando previamente las reglas de negocio y el modelo arquitectónico actual.

## Regla histórica

Los hechos importantes del proyecto deben registrarse con Build y/o commit cuando exista. Las conversaciones pueden aportar contexto narrativo, pero no sustituyen este historial.
