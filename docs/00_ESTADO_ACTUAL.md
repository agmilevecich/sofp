# SOFP — Estado actual

> Documento de continuidad del proyecto. Debe actualizarse al cerrar cada bloque importante.

## Identidad del proyecto

**Nombre:** SOFP — Sistema Operativo Financiero Personal  
**Repositorio:** agmilevecich/sofp  
**Rama principal:** `main`  
**Rama única de trabajo y continuidad:** `feature/operacion-financiera`  

La documentación de continuidad se mantiene en `docs/` dentro de la misma rama de trabajo. La rama `docs/continuidad-sofp` fue eliminada y no forma parte del flujo de continuidad del proyecto.

## Estado funcional actual

**Build 049 — Asociación de `Movimiento` con `OperacionFinanciera` está cerrado y validado.**

La relación persistente entre `Movimiento` y `OperacionFinanciera` está implementada mediante `ManyToOne` en `Movimiento` y `OneToMany` en `OperacionFinanciera`.

`OperacionFinanciera` mantiene una colección de movimientos protegida mediante una vista no modificable y permite asociar como máximo dos movimientos. La asociación es bidireccional.

`OperacionFinancieraService` asocia el `EGRESO` y el `INGRESO` a la operación antes de persistirlos.

El dominio rechaza movimientos nulos, movimientos repetidos, un tercer movimiento y movimientos que ya pertenecen a otra operación financiera. También impide utilizar la misma cuenta como origen y destino.

## Última validación

**Build 050 — Ampliación de cobertura de `OperacionFinancieraService` — cerrado y validado.**

Se agregaron dos pruebas al servicio:

- asociación de ambos movimientos a la misma `OperacionFinanciera`;
- rechazo de la misma cuenta como origen y destino, verificando además que no se persistan movimientos.

`OperacionFinancieraServiceTest`: **22/22 tests en verde**.

`OperacionFinancieraRepositoryTest`: **10/10 tests en verde** en ejecución individual desde IntelliJ.

Suite general: **328/328 tests en verde**.

- Failures: **0**.
- Errors: **0**.
- Skipped: **0**.
- **BUILD SUCCESS**.
- Última ejecución general confirmada desde IntelliJ: **25/08/2026 10:47:02 -03:00**.
- Duración: **12:04 min**.

La prueba individual de `OperacionFinancieraRepositoryTest` fue confirmada posteriormente con sus **10/10 tests en verde**.

## Git

- Rama única de trabajo y continuidad: `feature/operacion-financiera`.
- Último commit: `1f306e4` — `test: ampliar cobertura de OperacionFinancieraService`.
- Último commit funcional: `0f64fa9` — `feat: asociar movimientos a operacion financiera`.
- GitHub y Bitbucket están sincronizados en `feature/operacion-financiera`.
- Working tree confirmado limpio en la última validación local.
- `git diff --check` confirmado limpio.
- `main` permanece separado y no fue modificado.
- `docs/continuidad-sofp`: **eliminada**.

## Dominio construido

Entidades principales: `Usuario`, `PerfilFinanciero`, `InstitucionFinanciera`, `Moneda`, `Cuenta`, `Categoria`, `Movimiento` y `OperacionFinanciera`.

Enumeraciones principales: `TipoInstitucionFinanciera`, `TipoMoneda`, `TipoCuenta` y `TipoMovimiento`.

## Persistencia

Repositorios JPA: `UsuarioRepository`, `PerfilFinancieroRepository`, `InstitucionFinancieraRepository`, `MonedaRepository`, `CuentaRepository`, `MovimientoRepository`, `CategoriaRepository` y `OperacionFinancieraRepository`.

`OperacionFinancieraRepository` proporciona `guardar(...)`, `buscarPorId(...)`, `listarTodas()`, `listarPorCuentaOrigen(...)` y `listarPorCuentaDestino(...)`.

La relación entre `OperacionFinanciera` y `Movimiento` queda persistida mediante la columna `operacion_financiera_id` en `Movimiento`.

## Services

La capa `service` contiene `CuentaService`, `MovimientoService`, `CategoriaService`, `PerfilFinancieroService`, `UsuarioService`, `InstitucionFinancieraService`, `MonedaService` y `OperacionFinancieraService`.

`OperacionFinancieraService` materializa una transferencia como una operación financiera con un `EGRESO` en la cuenta origen y un `INGRESO` en la cuenta destino, coordinando su persistencia dentro de una única transacción.

## Decisiones de dominio relevantes

Las transferencias no se modelan como un tercer `TipoMovimiento`. Una transferencia genera un **EGRESO** en la cuenta origen y un **INGRESO** en la cuenta destino, vinculados mediante una `OperacionFinanciera`.

Una `OperacionFinanciera` puede contener como máximo dos movimientos y cada `Movimiento` puede estar asociado a una única `OperacionFinanciera`.

## Próximo paso

Definir el siguiente bloque funcional de `feature/operacion-financiera` a partir de las reglas de negocio y pendientes arquitectónicos documentados.

Antes de implementar un nuevo componente, revisar el dominio, repositorios, servicios y tests relacionados y mantener el cambio mínimo necesario.

## Regla de continuidad

Cada bloque importante debe terminar con código funcionando, tests en verde, commit identificable cuando corresponda, actualización de documentación y registro del próximo paso. Código, tests y documentación de continuidad deben permanecer en la misma rama `feature/operacion-financiera`.
