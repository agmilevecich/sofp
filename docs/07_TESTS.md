# SOFP — Tests

Este documento registra la evolución de la batería de tests y los resultados verificados por Build.

## Estado actual

### Build 050 — Ampliación de cobertura de OperacionFinancieraService — Cerrado

Se agregaron dos pruebas específicas a `OperacionFinancieraServiceTest`:

- asociación de ambos movimientos generados a la misma `OperacionFinanciera`;
- rechazo de la misma cuenta como origen y destino, verificando además que no se persistan movimientos.

`OperacionFinancieraServiceTest`: **22/22 tests en verde**.

Suite general: **328/328 tests en verde**, con `Failures: 0`, `Errors: 0`, `Skipped: 0` y `BUILD SUCCESS`.

La suite completa fue confirmada desde IntelliJ el **25/08/2026 10:47:02 -03:00**, con una duración de **12:04 min**.

Commit:

- `1f306e4` — `test: ampliar cobertura de OperacionFinancieraService`

### Build 049 — Asociación de Movimiento con OperacionFinanciera — Cerrado

Se incorporó la relación persistente entre `Movimiento` y `OperacionFinanciera` y se completaron las pruebas de asociación y consistencia de la relación.

`OperacionFinancieraTest`: **14/14 tests en verde**.

La suite completa quedó en **326/326 tests en verde**.

### Build 048 — Implementación de OperacionFinancieraRepository — Cerrado

`OperacionFinancieraRepositoryTest`: **10/10 tests en verde**.

`OperacionFinancieraServiceTest`: **20/20 tests en verde**.

`OperacionFinancieraTest`: **7/7 tests en verde**.

Suite completa: **319/319 tests en verde**.

### Build 047 — Completar cobertura de OperacionFinancieraService — Cerrado

Se completaron las validaciones de cuentas inactivas, categorías de otro perfil, monedas diferentes, fecha/hora nula, descripción nula y ausencia de persistencia ante operaciones rechazadas.

Suite general: **309/309 tests en verde**.

### Build 046 — Implementación de OperacionFinancieraService — Cerrado

Se incorporó `OperacionFinancieraService` y se amplió su cobertura hasta **20 tests en verde**.

Suite general: **300/300 tests en verde**.

### Build 045 — Implementación del dominio de OperacionFinanciera — Cerrado

`OperacionFinancieraTest`: **7/7 tests en verde**.

Suite general: **289/289 tests en verde**.

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
- `OperacionFinancieraServiceTest`: **22**

Total confirmado de tests de services: **211**.

Tests de dominio destacados:

- `MovimientoTest`: **27**
- `OperacionFinancieraTest`: **14**

Tests de persistencia destacados:

- `OperacionFinancieraRepositoryTest`: **10**

Suite general actual: **328/328 tests en verde**, con `Failures: 0`, `Errors: 0`, `Skipped: 0` y `BUILD SUCCESS`.

La última ejecución confirmada se realizó desde IntelliJ el **25/08/2026 a las 10:47:02 -03:00** y finalizó correctamente.

## Regla de cierre

Cada Build debe quedar registrado con cambios principales, tests ejecutados, resultado, commit asociado cuando exista y próximo paso. La documentación debe reflejar el estado real de `feature/operacion-financiera` antes de iniciar un nuevo bloque.
