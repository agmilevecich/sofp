# SOFP — Historial del proyecto

Este documento conserva una línea temporal resumida de decisiones, avances y puntos de continuidad.

## 2026-08-28

### Feature `identificacion-activo` — Identificación por símbolo

Se incorporó el símbolo como identificador funcional de los activos.

Cambios principales:

- `Activo` ahora requiere `nombre`, `simbolo` y `moneda`.
- `simbolo` es obligatorio y único en persistencia.
- `Bono` utiliza el constructor `nombre + simbolo + moneda`.
- `ActivoRepository.buscarPorSimbolo(String)` fue implementado.
- `BonoRepository.buscarPorSimbolo(String)` fue implementado.
- Las búsquedas devuelven `Optional` y rechazan `null`.
- Se adaptaron los tests y constructores afectados por el nuevo atributo.

Commits funcionales y de cobertura:

- `3f6c776` — `feat: agregar busqueda de activo por simbolo`
- `6179f2d` — `test: cubrir busqueda de activo por simbolo`
- `354e0b3` — `feat: agregar busqueda de bono por simbolo`
- `976aff7` — `test: cubrir busqueda de bono por simbolo`

### Validación posterior

Suite general ejecutada desde IntelliJ IDEA el **27/08/2026 19:52:48 -03:00**:

- **435/435 tests en verde**;
- `Failures: 0`;
- `Errors: 0`;
- `Skipped: 0`;
- `BUILD SUCCESS`;
- duración: **19:08 min**.

Después de esa suite se ejecutaron los tests específicos de los cambios de identificación por símbolo y fueron informados como verdes.

### Estado Git de la feature

- HEAD funcional antes de esta actualización documental: `976aff7`.
- `feature/identificacion-activo` está 19 commits por delante de `main` y 0 por detrás.
- `main` permanece en `d9e7849`.
- `main` no fue modificado.

## 2026-08-27

### Build 059 — Suite general posterior a venta y posición — Cerrado

- Se completó la validación del bloque de compra y venta de activos.
- `OperacionFinancieraVentaServiceTest`: **13/13 tests en verde**.
- `PosicionActivoServiceTest`: **4/4 tests en verde**.
- Se verificaron las relaciones persistidas de `Movimiento` y `MovimientoActivo` con `OperacionFinanciera` después de recuperar la operación.
- Se agregó una prueba de integración que ejecuta una compra de 100 y una venta de 30 mediante `OperacionFinancieraService`, y verifica una posición final de **70 unidades** mediante `PosicionActivoService`.
- Suite general: **433/433 tests en verde**.
- `Failures: 0`, `Errors: 0`, `Skipped: 0`, `BUILD SUCCESS`.
- Build 059 quedó cerrado y validado.

### Integración en main

- `d39632b` — `Merge branch 'feature/operacion-financiera'`.
- La etapa `feature/operacion-financiera` quedó integrada en `main`.
- Posteriormente `main` quedó en `d9e7849` tras actualizar la documentación de continuidad.

## Estado actual

El bloque de operaciones financieras está cerrado y la evolución activa corresponde a `feature/identificacion-activo`.

La feature actual incorpora identificación de activos por símbolo y ya cuenta con búsquedas por símbolo para `Activo` y `Bono`.

## Próximo punto de trabajo

Cubrir la regla de unicidad del símbolo en persistencia mediante un test específico, sin modificar producción si la restricción existente ya es suficiente.

Después continuar con la siguiente necesidad real de identificación de instrumentos.

## Regla histórica

Los hechos importantes se registran con Build y/o commit cuando corresponda. Los chats aportan contexto, pero no sustituyen el estado real del repositorio.
