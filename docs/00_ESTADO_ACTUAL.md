# SOFP — Estado actual

> Documento de continuidad del proyecto. Debe actualizarse al cerrar cada bloque importante.

## Identidad del proyecto

**Nombre:** SOFP — Sistema Operativo Financiero Personal  
**Repositorio:** agmilevecich/sofp  
**Rama principal:** `main`  
**Rama única de trabajo y continuidad:** `feature/operacion-financiera`  

La documentación de continuidad se mantiene en `docs/` dentro de la misma rama de trabajo. La rama `docs/continuidad-sofp` fue eliminada y no forma parte del flujo de continuidad del proyecto.

## Estado funcional actual

**Build 051 — Incorporación de `Activo` y su persistencia JPA — cerrado y validado.**

Se incorporó la entidad `Activo` como base para el futuro bloque de inversiones, con `nombre` y `Moneda` obligatorios, heredando de `EntidadAuditable`.

Se incorporó `ActivoRepository` con operaciones de guardar, buscar por ID, listar y actualizar.

Se incorporó `ActivoRepositoryTest` para validar la persistencia JPA y la relación con `Moneda`.

## Última validación

**Build 051 — cerrado y validado.**

`ActivoTest`: **8/8 tests en verde**.

`ActivoRepositoryTest`: **6/6 tests en verde** en ejecución individual desde IntelliJ. Durante la primera ejecución se detectó la necesidad de registrar `Activo` en la configuración de persistencia utilizada por los tests; una vez corregida esa configuración, los 6 tests quedaron en verde.

Suite general: **342/342 tests en verde**.

- Failures: **0**.
- Errors: **0**.
- Skipped: **0**.
- **BUILD SUCCESS**.
- Última ejecución general confirmada desde IntelliJ: **25/08/2026 16:30:22 -03:00**.
- Duración: **16:51 min**.

## Git

- Rama única de trabajo y continuidad: `feature/operacion-financiera`.
- Commit de incorporación de `Activo`: `0793126` — `feat: incorporar entidad Activo`.
- Commit de tests de `Activo`: `5270e31` — `test: agregar cobertura de Activo`.
- Commit de repositorio de `Activo`: `1624f8c` — `feat: incorporar repositorio de Activo`.
- Commit de tests de repositorio: `70bdbf7` — `test: agregar cobertura de ActivoRepository`.
- GitHub y Bitbucket están sincronizados en `feature/operacion-financiera`.
- Working tree confirmado limpio en la última validación local.
- `git diff --check` confirmado limpio.
- `main` permanece separado y no fue modificado.
- `docs/continuidad-sofp`: **eliminada**.

## Dominio construido

Entidades principales: `Usuario`, `PerfilFinanciero`, `InstitucionFinanciera`, `Moneda`, `Cuenta`, `Categoria`, `Movimiento`, `OperacionFinanciera` y `Activo`.

`Activo` representa la base común para futuros instrumentos financieros y actualmente contiene `nombre` y `moneda` como datos propios del activo.

Enumeraciones principales: `TipoInstitucionFinanciera`, `TipoMoneda`, `TipoCuenta` y `TipoMovimiento`.

## Persistencia

Repositorios JPA: `UsuarioRepository`, `PerfilFinancieroRepository`, `InstitucionFinancieraRepository`, `MonedaRepository`, `CuentaRepository`, `MovimientoRepository`, `CategoriaRepository`, `OperacionFinancieraRepository` y `ActivoRepository`.

`ActivoRepositoryTest`: **6/6 tests en verde**.

La persistencia de `Activo` quedó validada junto con su asociación obligatoria a `Moneda`.

## Services

La capa `service` contiene `CuentaService`, `MovimientoService`, `CategoriaService`, `PerfilFinancieroService`, `UsuarioService`, `InstitucionFinancieraService`, `MonedaService` y `OperacionFinancieraService`.

## Decisiones de dominio relevantes

Las transferencias no se modelan como un tercer `TipoMovimiento`. Una transferencia genera un **EGRESO** en la cuenta origen y un **INGRESO** en la cuenta destino, vinculados mediante una `OperacionFinanciera`.

Una `OperacionFinanciera` puede contener como máximo dos movimientos y cada `Movimiento` puede estar asociado a una única `OperacionFinanciera`.

`Activo` se mantiene deliberadamente como una entidad mínima. Cantidad, precio, cotización y posición se reservarán para los bloques de operaciones y posiciones de inversión.

## Próximo paso

Definir el siguiente bloque de inversiones a partir de las reglas de negocio y documentación arquitectónica, comenzando por la primera especialización de `Activo` únicamente después de revisar nuevamente el modelo actual y sus tests.

Antes de implementar un nuevo componente, revisar el dominio, repositorios, servicios y tests relacionados y mantener el cambio mínimo necesario.

## Regla de continuidad

Cada bloque importante debe terminar con código funcionando, tests en verde, commit identificable cuando corresponda, actualización de documentación y registro del próximo paso. Código, tests y documentación de continuidad deben permanecer en la misma rama `feature/operacion-financiera`.
