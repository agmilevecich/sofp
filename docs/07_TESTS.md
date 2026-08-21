# SOFP — Tests

Este documento registra la evolución de la batería de tests y los resultados verificados por Build.

## Estado actual

### Build 046 — Implementación de OperacionFinancieraService — Validado

Se amplió `OperacionFinancieraServiceTest` hasta **20 tests en verde**.

La cobertura incluye:

- registro de una transferencia;
- creación de `EGRESO` en la cuenta origen;
- creación de `INGRESO` en la cuenta destino;
- misma fecha/hora para ambos movimientos;
- parámetros obligatorios nulos;
- importes nulo, cero y negativo;
- cuentas desactivadas;
- categorías pertenecientes a otro perfil;
- cuentas con monedas diferentes;
- ausencia de persistencia de movimientos cuando la operación es rechazada.

Resultado específico verificado localmente:

- Tests run: **20**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- **20/20 tests en verde**

Durante la validación inicial se detectó que la descripción es obligatoria en `OperacionFinancieraService`. El test de descripción nula fue corregido para esperar `NullPointerException`, reflejando el contrato real del servicio.

Commit de producción asociado:

- `a995937` — `feat: implementar servicio de operacion financiera`

Los tests ampliados se encuentran en el trabajo local y deben quedar incorporados al siguiente commit de la rama `feature/operacion-financiera`.

### Build 045 — Implementación del dominio de OperacionFinanciera — Cerrado

Se incorporó `OperacionFinancieraTest` con **7 tests en verde**.

La cobertura incluye creación válida, cuenta origen obligatoria, cuenta destino obligatoria, importe nulo, importe cero, importe negativo y rechazo de la misma cuenta como origen y destino.

La suite general posterior al cambio quedó en **289/289 tests en verde**, con `Failures: 0`, `Errors: 0`, `Skipped: 0` y `BUILD SUCCESS`.

La ejecución terminó el **20/08/2026 a las 21:46:51 -03:00**, con una duración total de **08:47 min**.

Commit asociado:

- `1f650dc` — `feat: implementar dominio de operacion financiera`

El commit está en la rama `feature/operacion-financiera`.

### Build 044 — Ampliación de cobertura de Movimiento — Cerrado

Se amplió `MovimientoTest`, pasando de **4 a 27 tests en verde**.

Se incorporaron **23 tests nuevos** para cubrir validaciones del constructor y operaciones de modificación de `Movimiento`, incluyendo cuenta, categoría, tipo, importe, fecha/hora, descripción, observaciones y valores inválidos.

No se modificó código de producción.

La clase `MovimientoTest` quedó en **27/27 tests en verde**.

La suite general posterior a esta ampliación quedó en **282/282 tests en verde**, con `Failures: 0`, `Errors: 0`, `Skipped: 0` y `BUILD SUCCESS`.

La ejecución terminó el **20/08/2026 a las 13:19:27 -03:00**, con una duración total de **07:21 min**.

Commit asociado:

- `6f53f79` — `test: ampliar cobertura de Movimiento`

### Build 043 — Ampliación de cobertura de MovimientoService

Se amplió `MovimientoServiceTest`, pasando de **37 a 50 tests en verde**.

La batería general quedó en **259/259 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.

La ejecución terminó con `BUILD SUCCESS` el 19/08/2026 a las 19:51:15 -03:00.

Commit asociado:

- `b6384f0` — `test: ampliar cobertura de MovimientoService`

### Build 042 — Ampliación de cobertura de CuentaService

Se amplió `CuentaServiceTest`, pasando de **40 a 47 tests en verde**.

La batería general quedó en **246/246 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.

La ejecución terminó con `BUILD SUCCESS` el 19/08/2026 a las 16:46:27 -03:00.

Commit asociado:

- `526b378` — `test: ampliar cobertura de CuentaService`

### Build 041 — Reforzamiento de validaciones de servicios y dominio

Se amplió `InstitucionFinancieraServiceTest`, pasando de **23 a 26 tests en verde**.

La batería general quedó en **239/239 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.

Commit asociado:

- `a9de29c` — `feat: reforzar validaciones de servicios y dominio`

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

**Total general confirmado:** **302 tests** cuando se incorporen los 13 tests nuevos de `OperacionFinancieraServiceTest` respecto de la suite de 289 tests de Build 045.

## Histórico de cobertura

Los Builds anteriores y sus resultados permanecen registrados en el historial del proyecto. Build 046 agrega la cobertura específica de `OperacionFinancieraServiceTest`, actualmente verificada con **20/20 tests en verde**.

## Regla de cierre

Cada Build debe quedar registrado con cambios principales, tests ejecutados, resultado, commit asociado cuando exista y próximo paso. Los cambios posteriores de cobertura también deben quedar registrados para que la documentación refleje el estado real de los tests.
