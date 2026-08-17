# SOFP — Tests

Este documento registra la evolución de la batería de tests y los resultados verificados por Build.

## Build 025

Se amplió `Movimiento` y se completaron las nuevas operaciones correspondientes en `MovimientoService`.

En `Movimiento` se agregaron y validaron:

1. Modificación del tipo de movimiento.
2. Modificación del importe.
3. Modificación de la fecha y hora.

En `MovimientoService` se agregaron:

1. `modificarTipoMovimiento(Long movimientoId, TipoMovimiento tipoMovimiento)`.
2. `modificarImporte(Long movimientoId, BigDecimal importe)`.
3. `modificarFechaHora(Long movimientoId, LocalDateTime fechaHora)`.

Se incorporaron 3 nuevos casos en `MovimientoServiceTest`.

Resultado: **121/121 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.

## Build 026

Se incorporó la eliminación de movimientos y se completó la operación correspondiente en el repositorio y servicio.

En `MovimientoRepository` se incorporó `eliminar(Movimiento movimiento)`, con gestión de la entidad antes de `remove()` cuando corresponde.

En `MovimientoService` se incorporó `eliminar(Long movimientoId)`, con validación del ID, verificación de existencia y transacción explícita con `flush()`, `commit()` y `rollback()` ante excepciones.

También se consolidó el nombre `modificarTipoMovimiento(...)` en `Movimiento`.

No se agregaron nuevos tests en este bloque; se ejecutó la batería general como comprobación de regresión.

Resultado: **121/121 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.

## Build 027

Se amplió `CuentaService` para completar las operaciones de modificación y activación/desactivación de cuentas.

Se incorporaron tests en `CuentaServiceTest` para:

1. Modificar el nombre de una cuenta.
2. Modificar el identificador externo.
3. Modificar el tipo de cuenta.
4. Modificar la institución financiera.
5. Modificar la moneda.
6. Activar una cuenta.
7. Desactivar una cuenta.

`CuentaService` pasó a recibir `EntityManager` por constructor para gestionar explícitamente las transacciones de estas operaciones.

Las operaciones nuevas validan los parámetros obligatorios, verifican la existencia de la cuenta y utilizan `begin`, modificación, `flush`, `commit` y `rollback()` ante excepciones.

Resultado al cerrar el Build 027: **128/128 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.

También se ejecutó `git diff --check`, sin errores de formato.

## Cobertura adicional posterior al Build 027

Se agregó un caso en `MovimientoServiceTest` para cubrir explícitamente la eliminación de un movimiento mediante `MovimientoService.eliminar(...)`.

El test registra un movimiento, guarda su ID, ejecuta la eliminación y verifica mediante `buscarPorId(...)` que el movimiento ya no exista.

`MovimientoServiceTest` pasó de 15 a **16 tests**, todos en verde en la ejecución individual.

La batería general pasó de 128 a **129/129 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.

Commit asociado:

- `3e93be2` — `test: cubrir eliminacion de MovimientoService`.

## Build 028 — Ampliación de CategoriaService

Se amplió `CategoriaService` y `CategoriaServiceTest` para completar la gestión de categorías desde la capa de servicio.

Se incorporaron operaciones de modificación y activación/desactivación de categorías, con validación de identificadores y existencia y gestión transaccional mediante `EntityManager`.

`CategoriaServiceTest` fue ampliado para cubrir las nuevas operaciones y comprobar la persistencia de los cambios.

Resultado: **135/135 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.

También se ejecutó `git diff --check`, sin errores de formato.

Commit asociado:

- `b5c200e` — `feat: ampliar CategoriaService`.

## Build 029 — Eliminación en CategoriaRepository

Se incorporó la eliminación de categorías en `CategoriaRepository` mediante `eliminar(Categoria categoria)`.

La operación valida la categoría, comprueba si está gestionada, utiliza `merge(...)` cuando corresponde y ejecuta `remove(...)` sobre la instancia gestionada.

`CategoriaRepositoryTest` incorporó `deberiaEliminarCategoriaExistente()`, verificando que la categoría deje de estar disponible mediante `buscarPorId(...)` después de la eliminación.

Resultado: **136/136 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.

La prueba específica de `CategoriaRepositoryTest` quedó en **5/5 tests en verde**.

También se ejecutó `git diff --check`, sin errores de formato.

Commit asociado:

- `46ad669` — `feat: completar eliminacion de CategoriaRepository`.

## Build 030 — Eliminación en CategoriaService

Se incorporó `CategoriaService.eliminar(Long categoriaId)` para completar la eliminación de categorías desde la capa de servicio.

La operación valida el ID, verifica la existencia mediante `obtenerCategoria(...)`, inicia una transacción explícita, delega la eliminación en `CategoriaRepository`, ejecuta `flush()` y `commit()`, y realiza `rollback()` ante excepciones.

Se incorporaron dos casos en `CategoriaServiceTest`:

1. `deberiaEliminarCategoriaExistente()` — registra una categoría, la elimina mediante el servicio y verifica que `buscarPorId(...)` no la encuentre.
2. `deberiaLanzarExcepcionCuandoSeEliminaUnaCategoriaInexistente()` — verifica que eliminar una categoría inexistente produzca `IllegalArgumentException`.

Resultado: **138/138 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.

También se ejecutó `git diff --check`, sin errores de formato.

Commit asociado:

- `59b9628` — `feat: completar eliminacion de CategoriaService`.

## Regla de cierre

Cada Build debe quedar registrado con cambios principales, tests ejecutados, resultado, commit asociado cuando exista y próximo paso. Los cambios posteriores de cobertura también deben quedar registrados para que la documentación refleje el estado real de los tests.
