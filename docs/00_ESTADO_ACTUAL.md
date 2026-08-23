# SOFP — Estado actual

> Documento de continuidad del proyecto. Debe actualizarse al cerrar cada bloque importante de trabajo.

## Identidad del proyecto

**Nombre:** SOFP — Sistema Operativo Financiero Personal  
**Repositorio:** agmilevecich/sofp  
**Rama principal:** `main`  
**Rama de trabajo de la funcionalidad actual:** `feature/operacion-financiera`  
**Rama de documentación:** `docs/continuidad-sofp`

## Estado funcional actual

**Build 048 — Implementación de `OperacionFinancieraRepository` está cerrado y validado.**

`OperacionFinancieraService` materializa una transferencia como una `OperacionFinanciera`, un `EGRESO` en la cuenta origen y un `INGRESO` en la cuenta destino, persistidos dentro de una única transacción.

El servicio valida cuentas activas, coherencia entre cuenta y categoría, moneda común y parámetros obligatorios. La descripción es obligatoria y su ausencia se rechaza con `NullPointerException`.

## Tests

- `OperacionFinancieraServiceTest`: **20/20**.
- `OperacionFinancieraRepositoryTest`: **10/10**.
- `OperacionFinancieraTest`: **7/7**.
- Suite general: **319/319 tests en verde**.
- Failures: **0**.
- Errors: **0**.
- Skipped: **0**.
- **BUILD SUCCESS**.
- Finalización: **23/08/2026 12:57:51 -03:00**.
- Duración: **09:07 min**.

## Último commit de código

- `3d0543c` — `feat: implementar repositorio de operacion financiera`.

La funcionalidad continúa aislada de `main`.

## Dominio construido

Entidades principales: `Usuario`, `PerfilFinanciero`, `InstitucionFinanciera`, `Moneda`, `Cuenta`, `Categoria`, `Movimiento` y `OperacionFinanciera`.

Enumeraciones principales: `TipoInstitucionFinanciera`, `TipoMoneda`, `TipoCuenta` y `TipoMovimiento`.

## Persistencia

Repositorios JPA: `UsuarioRepository`, `PerfilFinancieroRepository`, `InstitucionFinancieraRepository`, `MonedaRepository`, `CuentaRepository`, `MovimientoRepository`, `CategoriaRepository` y `OperacionFinancieraRepository`.

`OperacionFinancieraRepository` proporciona `guardar(...)`, `buscarPorId(...)`, `listarTodas()`, `listarPorCuentaOrigen(...)` y `listarPorCuentaDestino(...)`, con validación de parámetros obligatorios mediante `NullPointerException`.

## Services

La capa `service` contiene `CuentaService`, `MovimientoService`, `CategoriaService`, `PerfilFinancieroService`, `UsuarioService`, `InstitucionFinancieraService`, `MonedaService` y `OperacionFinancieraService`.

## Decisiones de dominio relevantes

Las transferencias no se modelan como un tercer `TipoMovimiento`. Una transferencia genera un **EGRESO** en la cuenta origen y un **INGRESO** en la cuenta destino, vinculados conceptualmente mediante una `OperacionFinanciera`.

## Git

- Rama de funcionalidad: `feature/operacion-financiera`.
- Último commit de código: `3d0543c`.
- Rama de documentación: `docs/continuidad-sofp`.
- `main`: permanece sin incorporar el trabajo de `feature/operacion-financiera`.

## Próximo paso

Definir el siguiente bloque funcional de `feature/operacion-financiera` a partir de las reglas de negocio y pendientes arquitectónicos documentados.

Antes de implementar un nuevo componente, revisar el dominio, repositorios, servicios y tests relacionados y mantener el cambio mínimo necesario.

## Regla de continuidad

Cada bloque importante debe terminar con código funcionando, tests en verde, commit identificable cuando corresponda, actualización de documentación y registro del próximo paso.