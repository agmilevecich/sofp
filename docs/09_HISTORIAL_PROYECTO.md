# SOFP — Historial del proyecto

Este documento conserva una línea temporal resumida de decisiones, avances y puntos de continuidad.

## 2026-08-21

### Build 047 — Completar cobertura de OperacionFinancieraService — Cerrado

- Se completó la cobertura de `OperacionFinancieraServiceTest`.
- Se incorporaron pruebas para cuentas inactivas, categorías pertenecientes a otros perfiles, monedas diferentes, fecha/hora nula, rechazo de descripción nula y ausencia de persistencia de movimientos ante operaciones rechazadas.
- `OperacionFinancieraServiceTest` quedó en **20/20 tests en verde**.
- La suite completa del proyecto quedó en **309/309 tests en verde**, con `Failures: 0`, `Errors: 0`, `Skipped: 0` y `BUILD SUCCESS`.
- La ejecución general finalizó a las **20:36:10 -03:00** y tuvo una duración de **08:14 min**.
- Commit: `615161c` — `test: completar cobertura de OperacionFinancieraService`.
- `main` no fue modificado por este Build.
- **Build 047 queda cerrado y validado.**

### Build 046 — Implementación de OperacionFinancieraService — Cerrado

- Se incorporó `OperacionFinancieraService` en la rama `feature/operacion-financiera`.
- El servicio materializa una transferencia mediante una `OperacionFinanciera`, un `EGRESO` en la cuenta origen y un `INGRESO` en la cuenta destino.
- La persistencia de la operación y ambos movimientos se coordina dentro de una única transacción con rollback ante excepciones.
- Se incorporaron validaciones de cuentas activas, coherencia entre cuenta y categoría, moneda común y parámetros obligatorios.
- La descripción es obligatoria y su ausencia es rechazada con `NullPointerException`. El comportamiento definitivo quedó validado por `OperacionFinancieraServiceTest`, mediante `deberiaRechazarDescripcionNula()`.
- `OperacionFinancieraServiceTest` quedó en **20/20 tests en verde**.
- La suite completa del proyecto quedó en **300/300 tests en verde**.
- Commits: `a995937`, `2e4b94f` y `62f2da3`.
- **Build 046 queda cerrado y validado.**

## 2026-08-20

### Build 045 — Implementación del dominio de OperacionFinanciera — Cerrado

- Se incorporó `OperacionFinanciera` como entidad de dominio para representar una transferencia entre cuenta origen y cuenta destino.
- Se incorporaron validaciones de cuenta origen y destino obligatorias.
- Se incorporó validación de importe obligatorio y positivo.
- Se incorporó la regla de negocio que impide utilizar la misma cuenta como origen y destino.
- Se incorporó `OperacionFinancieraTest` con **7 tests en verde**.
- La suite general quedó en **289/289 tests en verde**.
- Commit: `1f650dc` — `feat: implementar dominio de operacion financiera`.
- **Build 045 queda cerrado y validado.**

### Build 044 — Ampliación de cobertura de Movimiento — Cerrado

- Se amplió `MovimientoTest` sin modificar código de producción.
- Se incorporaron **23 tests nuevos**.
- `MovimientoTest` pasó de **4 a 27 tests en verde**.
- La suite general quedó en **282/282 tests en verde**.
- Commit: `6f53f79` — `test: ampliar cobertura de Movimiento`.
- **Build 044 queda cerrado y validado.**

### Builds 043–034

Los Builds 043 a 034 ampliaron progresivamente la cobertura y las validaciones de los servicios y del dominio. Los resultados completos permanecen registrados en `docs/06_BUILDS.md` y `docs/07_TESTS.md`.

## Estado actual

El último bloque trabajado es **Build 047 — Completar cobertura de OperacionFinancieraService — cerrado**.

La batería específica de `OperacionFinancieraServiceTest` está en **20/20 tests en verde**.

La suite completa está en **309/309 tests en verde**, con `BUILD SUCCESS`.

El último commit de código es `615161c` — `test: completar cobertura de OperacionFinancieraService`, en `feature/operacion-financiera`.

La documentación de continuidad se mantiene en la rama `docs/continuidad-sofp`.

`main` permanece en `028aaee` y no recibió los cambios de Build 047.

## Próximo punto de trabajo

Definir el siguiente bloque funcional y continuar el desarrollo en `feature/operacion-financiera`, manteniendo la documentación separada en `docs/continuidad-sofp`.

## Builds recientes

- Build 029 — Eliminación en `CategoriaRepository`.
- Build 030 — Eliminación en `CategoriaService`.
- Build 031 — Eliminación en `CuentaRepository`.
- Build 032 — Eliminación en `CuentaService`.
- Build 033 — Reglas de negocio de `Movimiento`.
- Build 034 — Ampliación de cobertura de `MovimientoServiceTest`.
- Build 035 — Ampliación de cobertura de `CuentaServiceTest`.
- Build 036 — Ampliación de cobertura de `InstitucionFinancieraServiceTest`.
- Build 037 — Ampliación de cobertura de `MonedaServiceTest`.
- Build 038 — Ampliación de cobertura de `PerfilFinancieroServiceTest`.
- Build 039 — Ampliación de cobertura de `UsuarioServiceTest`.
- Build 040 — Ampliación de cobertura de `CategoriaServiceTest`.
- Build 041 — Reforzamiento de validaciones de servicios y dominio.
- Build 042 — Ampliación de cobertura de `CuentaServiceTest`.
- Build 043 — Ampliación de cobertura de `MovimientoServiceTest`.
- Build 044 — Ampliación de cobertura de `MovimientoTest` — cerrado con 282/282 tests en verde.
- Build 045 — Implementación del dominio de `OperacionFinanciera` — cerrado con 289/289 tests en verde.
- Build 046 — Implementación de `OperacionFinancieraService` — cerrado con 20/20 tests específicos y 300/300 tests en la suite general.
- Build 047 — Completar cobertura de `OperacionFinancieraService` — cerrado con 20/20 tests específicos y 309/309 tests en la suite general.

## Regla histórica

Los hechos importantes del proyecto deben registrarse con Build y/o commit cuando exista. Las conversaciones pueden aportar contexto narrativo, pero no sustituyen este historial.
