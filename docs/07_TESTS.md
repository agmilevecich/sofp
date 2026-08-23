# SOFP — Tests

Este documento registra la evolución de la batería de tests y los resultados verificados por Build.

## Estado actual

### Build 049 — Asociación de Movimiento con OperacionFinanciera — Cerrado

Se incorporó la relación persistente entre `Movimiento` y `OperacionFinanciera` y se completaron las pruebas de asociación y consistencia de la relación.

`OperacionFinancieraTest` pasó de **7 a 14 tests en verde**, incorporando pruebas para:

- creación de la operación sin movimientos iniciales;
- asociación de un movimiento;
- asociación de dos movimientos;
- rechazo de movimiento nulo;
- rechazo de un tercer movimiento;
- rechazo de un movimiento repetido;
- rechazo de un movimiento ya asociado a otra operación.

Resultado específico: **14/14 tests en verde**.

La suite completa quedó en **326/326 tests en verde**, con `Failures: 0`, `Errors: 0`, `Skipped: 0` y `BUILD SUCCESS`.

Finalización: **2026-08-23 16:59:40 -03:00**. Duración: **17:23 min**.

La ejecución emitió una advertencia de Surefire relacionada con el cierre de la JVM del fork, pero el resultado final fue exitoso.

Commit asociado:

- `11dc0ae` — `feat: asociar movimientos a operacion financiera`

### Build 048 — Implementación de OperacionFinancieraRepository — Cerrado

Se incorporó `OperacionFinancieraRepository` y se integró en `OperacionFinancieraService`.

`OperacionFinancieraRepositoryTest`: **10/10 tests en verde**.

`OperacionFinancieraServiceTest`: **20/20 tests en verde**.

`OperacionFinancieraTest`: **7/7 tests en verde**.

Suite completa: **319/319 tests en verde**, con `Failures: 0`, `Errors: 0`, `Skipped: 0` y `BUILD SUCCESS`.

Finalización: **2026-08-23 12:57:51 -03:00**. Duración: **09:07 min**.

### Build 047 — Completar cobertura de OperacionFinancieraService — Cerrado

Se completaron las validaciones de cuentas inactivas, categorías de otro perfil, monedas diferentes, fecha/hora nula, descripción nula y ausencia de persistencia ante operaciones rechazadas.

Suite general: **309/309 tests en verde**.

Finalización: **2026-08-21 20:36:10 -03:00**.

Commit: `615161c` — `test: completar cobertura de OperacionFinancieraService`.

### Build 046 — Implementación de OperacionFinancieraService — Cerrado

Se incorporó `OperacionFinancieraService` y se amplió su cobertura hasta **20 tests en verde**.

Suite general: **300/300 tests en verde**.

Commits asociados: `a995937`, `2e4b94f` y `62f2da3`.

### Build 045 — Implementación del dominio de OperacionFinanciera — Cerrado

`OperacionFinancieraTest`: **7/7 tests en verde**.

Suite general: **289/289 tests en verde**.

Commit: `1f650dc` — `feat: implementar dominio de operacion financiera`.

### Builds anteriores

Los Builds 001–044 y sus resultados permanecen registrados en el historial del proyecto.

## Conteo actual por test de service

- `CategoriaServiceTest`: **21**
- `CuentaServiceTest`: **47**
- `InstitucionFinancieraServiceTest`: **26**
- `MonedaServiceTest`: **17**
- `MovimientoServiceTest`: **50**
- `PerfilFinancieroServiceTest`: **13**
- `UsuarioServiceTest`: **15**
- `OperacionFinancieraServiceTest`: **20**

Total confirmado de tests de services: **209**.

Tests de dominio destacados:

- `MovimientoTest`: **27**
- `OperacionFinancieraTest`: **14**

Tests de persistencia destacados:

- `OperacionFinancieraRepositoryTest`: **10**

Suite general actual: **326/326 tests en verde**, con `Failures: 0`, `Errors: 0`, `Skipped: 0` y `BUILD SUCCESS`.

## Regla de cierre

Cada Build debe quedar registrado con cambios principales, tests ejecutados, resultado, commit asociado cuando exista y próximo paso. La documentación debe reflejar el estado real de la rama antes de iniciar un nuevo bloque.
