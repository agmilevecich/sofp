# SOFP — Estado actual

> Documento de continuidad del proyecto. Debe actualizarse al cerrar cada bloque importante de trabajo.

## Identidad del proyecto

**Nombre:** SOFP — Sistema Operativo Financiero Personal  
**Repositorio:** agmilevecich/sofp  
**Rama principal:** `main`  
**Rama única de trabajo y continuidad:** `feature/operacion-financiera`  

La documentación de continuidad se mantiene en `docs/` dentro de la misma rama de trabajo. La rama `docs/continuidad-sofp` ya fue eliminada y no forma parte del flujo de continuidad del proyecto.

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
- Última ejecución confirmada: **23/08/2026 19:07:25 -03:00**.
- Duración: **12:30 min**.

Posteriormente, esta documentación fue sincronizada con la decisión de mantener toda la continuidad en `feature/operacion-financiera`.

## Último commit de continuidad documental

- `378f3c8` — `docs: unificar pendientes en rama de funcionalidad`.

Este commit representa el último estado de sincronización documental conocido antes de esta corrección.

## Último commit de código

- `0f64fa9` — `feat: asociar movimientos a operacion financiera`.

El commit funcional y la documentación de continuidad permanecen en la misma línea histórica de `feature/operacion-financiera`.

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

- Rama única de trabajo y continuidad: `feature/operacion-financiera`.
- Último commit de código: `0f64fa9` — `feat: asociar movimientos a operacion financiera`.
- Último bloque de continuidad: `378f3c8` — `docs: unificar pendientes en rama de funcionalidad`.
- `main`: permanece sin incorporar el trabajo de `feature/operacion-financiera`.
- `docs/continuidad-sofp`: **eliminada**. No debe volver a utilizarse para documentación de continuidad.

## Próximo paso

Definir el siguiente bloque funcional de `feature/operacion-financiera` a partir de las reglas de negocio y pendientes arquitectónicos documentados.

Antes de implementar un nuevo componente, revisar el dominio, repositorios, servicios y tests relacionados y mantener el cambio mínimo necesario.

## Regla de continuidad

Cada bloque importante debe terminar con código funcionando, tests en verde, commit identificable cuando corresponda, actualización de documentación y registro del próximo paso. Código, tests y documentación de continuidad deben permanecer en la misma rama `feature/operacion-financiera`.