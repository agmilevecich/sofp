# SOFP — Tests

Este documento registra la evolución de la batería de tests y los resultados verificados por Build.

## Estado actual

### Build 047 — Completar cobertura de OperacionFinancieraService — Cerrado

Se completó la cobertura de `OperacionFinancieraServiceTest`.

Se incorporaron pruebas para:

- rechazo de cuenta origen inactiva;
- rechazo de cuenta destino inactiva;
- rechazo de categoría origen perteneciente a otro perfil;
- rechazo de categoría destino perteneciente a otro perfil;
- rechazo de cuentas con monedas diferentes;
- rechazo de fecha/hora nula;
- rechazo de descripción nula;
- verificación de que no se persisten movimientos cuando la cuenta origen está inactiva;
- verificación de que no se persisten movimientos cuando las monedas de las cuentas son diferentes.

`OperacionFinancieraServiceTest` quedó con **20 tests en verde**.

Suite completa del proyecto:

- Tests run: **309**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- Resultado: **BUILD SUCCESS**
- Duración: **08:14 min**
- Finalización: **2026-08-21 20:36:10 -03:00**

**Build 047 queda cerrado con 309/309 tests en verde.**

### Build 046 — Implementación de OperacionFinancieraService — Cerrado

Se amplió `OperacionFinancieraServiceTest` hasta **20 tests en verde**.

La cobertura incluye registro de una transferencia, creación de `EGRESO` en la cuenta origen, creación de `INGRESO` en la cuenta destino, misma fecha/hora para ambos movimientos, parámetros obligatorios nulos, importes inválidos, cuentas desactivadas, categorías pertenecientes a otro perfil, cuentas con monedas diferentes, ausencia de persistencia ante operaciones rechazadas y rechazo de descripción nula mediante `deberiaRechazarDescripcionNula()`.

Durante la validación se confirmó que la descripción debe ser obligatoria para una transferencia. La corrección intermedia `2e4b94f`, que permitía descripción nula, fue posteriormente revertida mediante `62f2da3` para restablecer la validación. El comportamiento definitivo quedó cubierto por `deberiaRechazarDescripcionNula()`.

Commits asociados:

- `a995937` — `feat: implementar servicio de operacion financiera`
- `2e4b94f` — `fix: permitir descripcion nula en transferencia`
- `62f2da3` — `fix: validar descripcion en transferencia`
- `615161c` — `test: completar cobertura de OperacionFinancieraService`

Suite general de Build 046: **300/300 tests en verde**.

### Build 045 — Implementación del dominio de OperacionFinanciera — Cerrado

Se incorporó `OperacionFinancieraTest` con **7 tests en verde**.

La suite general posterior al cambio quedó en **289/289 tests en verde**, con `Failures: 0`, `Errors: 0`, `Skipped: 0` y `BUILD SUCCESS`.

Commit asociado: `1f650dc` — `feat: implementar dominio de operacion financiera`.

### Builds anteriores

Los Builds anteriores y sus resultados permanecen registrados en el historial del proyecto.

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
- `OperacionFinancieraTest`: **7**

Suite general actual: **309/309 tests en verde**, con `Failures: 0`, `Errors: 0`, `Skipped: 0` y `BUILD SUCCESS`.

## Regla de cierre

Cada Build debe quedar registrado con cambios principales, tests ejecutados, resultado, commit asociado cuando exista y próximo paso. Los cambios posteriores de cobertura también deben quedar registrados para que la documentación refleje el estado real de los tests.
