# SOFP — Estado actual

> Documento de continuidad del proyecto. Debe actualizarse al cerrar cada bloque importante de trabajo.

## Identidad del proyecto

**Nombre:** SOFP — Sistema Operativo Financiero Personal  
**Repositorio:** agmilevecich/sofp  
**Rama principal:** `main`  
**Rama de trabajo de la funcionalidad actual:** `feature/operacion-financiera`  
**Rama de documentación:** `docs/continuidad-sofp`

## Estado funcional actual

**Build 049 — Asociación de `Movimiento` con `OperacionFinanciera` está cerrado y validado.**

Se incorporó la relación persistente entre `Movimiento` y `OperacionFinanciera` mediante `ManyToOne` en `Movimiento` y `OneToMany` en `OperacionFinanciera`.

`OperacionFinanciera` mantiene una colección de movimientos protegida mediante una vista no modificable y permite asociar como máximo dos movimientos. La asociación es bidireccional: al agregar un movimiento a una operación, el movimiento queda vinculado a esa operación.

El dominio rechaza movimientos nulos, movimientos repetidos, un tercer movimiento y movimientos que ya pertenecen a otra operación financiera. La regla de asociación impide reasignar un movimiento a una operación diferente.

`OperacionFinancieraService` fue actualizado para asociar el `EGRESO` y el `INGRESO` a la operación antes de persistirlos.

## Tests

- `OperacionFinancieraServiceTest`: **20/20**.
- `OperacionFinancieraRepositoryTest`: **10/10**.
- `OperacionFinancieraTest`: **14/14**.
- `MovimientoTest`: **27/27**.
- Suite general: **326/326 tests en verde**.
- Failures: **0**.
- Errors: **0**.
- Skipped: **0**.
- **BUILD SUCCESS**.
- Finalización: **23/08/2026 16:59:40 -03:00**.
- Duración: **17:23 min**.

La ejecución emitió una advertencia de Surefire relacionada con el cierre de la JVM del fork, pero el resultado final fue `BUILD SUCCESS` con 0 failures y 0 errors.

## Último commit de código

- `11dc0ae` — `feat: asociar movimientos a operacion financiera`.

El commit fue publicado en GitHub y Bitbucket sobre `feature/operacion-financiera` mediante `git pushall`. La rama de funcionalidad quedó sincronizada con ambos remotos y el working tree quedó limpio.

La funcionalidad continúa aislada de `main`.

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

## Git

- Rama de funcionalidad: `feature/operacion-financiera`.
- Último commit de código: `11dc0ae` — `feat: asociar movimientos a operacion financiera`.
- Rama de documentación: `docs/continuidad-sofp`.
- `main`: permanece sin incorporar el trabajo de `feature/operacion-financiera`.

## Próximo paso

Definir el siguiente bloque funcional de `feature/operacion-financiera` a partir de las reglas de negocio y pendientes arquitectónicos documentados.

Antes de implementar un nuevo componente, revisar el dominio, repositorios, servicios y tests relacionados y mantener el cambio mínimo necesario.

## Regla de continuidad

Cada bloque importante debe terminar con código funcionando, tests en verde, commit identificable cuando corresponda, actualización de documentación y registro del próximo paso.
