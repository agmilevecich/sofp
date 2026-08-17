# SOFP — Historial de Builds

## Build 001

Configuración inicial del proyecto.

## Build 002

Configuración inicial de persistencia y base de datos.

## Build 003

Primeras entidades y consolidación del dominio.

## Build 004

Evolución del modelo de dominio y pruebas.

## Build 005

Diseño de la entidad `Cuenta`.

## Builds posteriores

Se realizaron trabajos de consolidación del dominio, arquitectura, validaciones, instituciones financieras, categorías y cuentas.

## Build 009.1

Implementación de la entidad `Categoria`.

## Build 010

Implementación de la entidad `Movimiento`.

Incluye:

- Entidad `Movimiento`.
- Enum `TipoMovimiento`.
- Validación de importe positivo.
- Relaciones con `Cuenta` y `Categoria`.
- Test unitario.
- Test JPA.

## Build 011 — Aislamiento y estabilización de tests JPA con H2

Se incorporó `JpaTestManager`, se separó la unidad de persistencia de pruebas `sofp-persistence-unit-test` y se configuró H2 en memoria con `create-drop`. Se resolvieron problemas de aislamiento y unicidad de datos y se mantuvo separada la persistencia de producción de la infraestructura de pruebas.

## Build 012 — Repositorios JPA de entidades base

Se incorporaron `UsuarioRepository`, `PerfilFinancieroRepository`, `InstitucionFinancieraRepository`, `MonedaRepository` y sus tests correspondientes.

## Build 013 — Repository JPA de Cuenta

Se incorporó `CuentaRepository` y `CuentaRepositoryTest`, cubriendo guardar/buscar, listar, listar por perfil y actualizar una cuenta existente.

## Build 014 — Repository JPA de Movimiento

Se incorporó `MovimientoRepository` y `MovimientoRepositoryTest`.

El repositorio proporciona guardar, actualizar, buscar por ID, listar todos, listar por cuenta y listar por categoría.

Resultado: **64/64 tests en verde**.

### Commit asociado

- `4f0b20f` — `Build 014 - Implementación de MovimientoRepository`.

## Build 015 — Servicio de saldo de cuentas

Se incorporó `CuentaService` y `CuentaServiceTest` para centralizar el cálculo del saldo a partir de movimientos. `INGRESO` suma, `EGRESO` resta y una cuenta sin movimientos devuelve `BigDecimal.ZERO`.

Resultado: **68/68 tests en verde**.

### Commit asociado

- `4697815` — `feat: implementar servicio de saldo de cuentas`.

## Build 016 — Servicio de movimientos

Se incorporó `MovimientoService` y `MovimientoServiceTest` para registrar, buscar y listar movimientos. El registro utiliza transacciones explícitas, `flush()`, `commit()` y `rollback()`.

Resultado: **74/74 tests en verde**.

### Commit asociado

- `8f8594e` — `feat: implementar servicio de movimientos`.

## Build 017 — Repository JPA de Categoria

Se incorporó `CategoriaRepository` y `CategoriaRepositoryTest`.

### Commit asociado

- `f462b3b` — `feat: implementar CategoriaRepository`.

## Build 018 — Servicio de Categoria

Se incorporó `CategoriaService` y `CategoriaServiceTest` para registrar, buscar, listar y listar por perfil financiero.

Resultado: **82/82 tests en verde**.

### Commit asociado

- `d57e0b4` — `feat: implementar CategoriaService`.

## Build 019 — Servicio de PerfilFinanciero

Se incorporó `PerfilFinancieroService` y `PerfilFinancieroServiceTest` para guardar, buscar, listar por usuario, cambiar descripción, activar y desactivar perfiles.

Resultado: **88/88 tests en verde**.

### Commit asociado

- `1cc00ca` — `feat: implementar PerfilFinancieroService`.

## Build 020 — Servicio de Usuario

Se incorporó `UsuarioService` y `UsuarioServiceTest` para guardar, buscar por ID, buscar por email, listar, activar y desactivar usuarios.

Resultado: **93/93 tests en verde**.

### Commit asociado

- `87786fe` — `feat: implementar UsuarioService`.

## Build 021 — Servicio de InstitucionFinanciera

Se incorporó `InstitucionFinancieraService` y `InstitucionFinancieraServiceTest` para buscar, listar, renombrar, actualizar sitio web y descripción, activar y desactivar instituciones.

Resultado: **101/101 tests en verde**.

### Commit asociado

- `20e21c3` — `feat: implementar InstitucionFinancieraService`.

## Build 022 — Servicio de Moneda

Se incorporó `MonedaService` y `MonedaServiceTest` para buscar, listar, cambiar nombre y cambiar cantidad de decimales. Se corrigió el aislamiento de H2 mediante `JpaTestManager.close()`.

Resultado: **109/109 tests en verde**.

### Commit asociado

- `0d0db87` — `feat: implementar MonedaService`.

## Build 023 — Ampliación de CuentaService

Se amplió `CuentaService` y `CuentaServiceTest` para cubrir registrar, buscar, listar, listar por perfil y las verificaciones de saldo.

Resultado: **113/113 tests en verde**.

### Commit asociado

- `ea595d4` — `feat: ampliar CuentaService`.

El commit fue publicado en `main` de GitHub y Bitbucket.

## Build 024 — Ampliación de MovimientoService

Se amplió `MovimientoService` para modificar descripción, observaciones y categoría, además de reforzar validaciones de IDs y existencia del movimiento. `MovimientoServiceTest` pasó de 6 a **11 tests**.

Resultado: **118/118 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.

### Commit asociado

- `110f7d7` — `feat: ampliar MovimientoService`.

El commit fue publicado en `main` de GitHub y Bitbucket.

## Build 025 — Ampliación de Movimiento y nuevas operaciones de MovimientoService

Se amplió `Movimiento` con operaciones para modificar tipo de movimiento, importe y fecha/hora, y se incorporaron las operaciones equivalentes en `MovimientoService`.

Se agregaron 3 nuevos tests a `MovimientoServiceTest`.

Resultado: **121/121 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.

### Commits asociados

- `da3b89d` — `feat: ampliar operaciones de Movimiento`.
- `81883ea` — `feat: completar operaciones de MovimientoService`.

Ambos commits fueron publicados en `main` de GitHub y Bitbucket.

## Build 026 — Eliminación de Movimiento

### Objetivo

Completar la operación de eliminación de movimientos en las capas de persistencia y servicio, manteniendo las reglas de validación y control transaccional utilizadas en `MovimientoService`.

### Cambios principales

En `MovimientoRepository` se incorporó `eliminar(Movimiento movimiento)`, garantizando que la entidad esté gestionada antes de ejecutar `remove()` cuando corresponde.

En `MovimientoService` se incorporó `eliminar(Long movimientoId)`, con validación del ID, verificación de existencia y transacción explícita con `begin`, `flush`, `commit` y `rollback()` ante excepciones.

También se consolidó el nombre `modificarTipoMovimiento(...)` en la entidad `Movimiento`.

Resultado: **121/121 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.

### Commit asociado

- `d386d02` — `feat: completar operaciones de Movimiento`.

El commit fue publicado en `main` de GitHub y Bitbucket.

## Build 027 — Ampliación de CuentaService

### Objetivo

Completar las operaciones de modificación y activación/desactivación de `Cuenta` desde la capa `CuentaService`.

### Cambios principales

Se incorporaron en `CuentaService`:

- `modificarNombre(Long cuentaId, String nombre)`.
- `modificarIdentificadorExterno(Long cuentaId, String identificadorExterno)`.
- `modificarTipoCuenta(Long cuentaId, TipoCuenta tipoCuenta)`.
- `modificarInstitucionFinanciera(Long cuentaId, InstitucionFinanciera institucionFinanciera)`.
- `modificarMoneda(Long cuentaId, Moneda moneda)`.
- `activar(Long cuentaId)`.
- `desactivar(Long cuentaId)`.

El servicio recibe `EntityManager` por constructor y utiliza transacciones explícitas con `begin`, modificación, `flush`, `commit` y `rollback()` ante excepciones.

Se agregó `obtenerCuenta(Long cuentaId)` para centralizar la búsqueda y la validación de cuenta inexistente mediante `IllegalArgumentException`.

### Tests verificados

`CuentaServiceTest` incorporó siete casos nuevos para las operaciones anteriores.

Resultado al cerrar el Build 027: **128/128 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.

También se ejecutó `git diff --check`, sin errores de formato.

### Cobertura posterior del Build 027

Se agregó un test en `MovimientoServiceTest` para cubrir explícitamente la eliminación de un movimiento mediante `MovimientoService.eliminar(...)` y verificar que el movimiento deje de estar disponible mediante `buscarPorId(...)`.

Resultado actualizado: **129/129 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.

### Commits asociados

- `00d862c` — `feat: ampliar CuentaService`.
- `3e93be2` — `test: cubrir eliminacion de MovimientoService`.

Ambos cambios se encuentran publicados en `main` de GitHub y Bitbucket.

## Build 028 — Ampliación de CategoriaService

### Objetivo

Completar las operaciones de la capa `CategoriaService` para que el servicio no se limite a registrar, buscar y listar categorías, sino que también permita gestionar su estado y sus datos modificables desde la capa de servicio.

### Cambios principales

Se amplió `CategoriaService` y se actualizó `CategoriaServiceTest`.

El servicio pasó a utilizar `EntityManager` junto con `CategoriaRepository` para gestionar explícitamente las operaciones transaccionales de modificación y estado, manteniendo el patrón utilizado en los servicios anteriores.

Se incorporaron las operaciones de modificación y activación/desactivación disponibles en la entidad `Categoria`, junto con la validación de los identificadores y de la existencia de la categoría.

### Tests verificados

`CategoriaServiceTest` fue ampliado para cubrir las nuevas operaciones del servicio.

Resultado de la batería general: **135/135 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.

También se ejecutó `git diff --check`, sin errores de formato.

### Commit asociado

- `b5c200e` — `feat: ampliar CategoriaService`.

El commit de código corresponde al estado local de `main` y queda registrado aquí como referencia para sincronizar la continuidad.

## Build 029 — Eliminación en CategoriaRepository

### Objetivo

Completar la operación de eliminación de categorías en la capa de persistencia, manteniendo el patrón JPA utilizado en los repositorios anteriores y cubriendo la operación con una prueba específica.

### Cambios principales

En `CategoriaRepository` se incorporó `eliminar(Categoria categoria)`.

La operación:

- valida que la categoría no sea `null`;
- comprueba si la entidad está gestionada mediante `EntityManager.contains(...)`;
- utiliza `EntityManager.merge(...)` cuando la entidad no está gestionada;
- ejecuta `EntityManager.remove(...)` sobre la instancia gestionada.

En `CategoriaRepositoryTest` se incorporó `deberiaEliminarCategoriaExistente()`, verificando que una categoría persistida deje de estar disponible mediante `buscarPorId(...)` después de la eliminación.

### Tests verificados

La prueba específica de `CategoriaRepositoryTest` quedó en **5/5 tests en verde**.

La batería general quedó en **136/136 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.

También se ejecutó `git diff --check`, sin errores de formato.

### Commit asociado

- `46ad669` — `feat: completar eliminacion de CategoriaRepository`.

El commit fue publicado en `main` de GitHub y Bitbucket.

## Build 030 — Eliminación en CategoriaService

### Objetivo

Completar la operación de eliminación de categorías desde la capa de servicio, delegando la eliminación en `CategoriaRepository` y manteniendo el patrón transaccional utilizado en `MovimientoService`.

### Cambios principales

En `CategoriaService` se incorporó `eliminar(Long categoriaId)`.

La operación:

- valida que el ID no sea `null`;
- obtiene la categoría mediante `obtenerCategoria(...)`;
- lanza `IllegalArgumentException` cuando la categoría no existe;
- inicia una transacción explícita;
- delega la eliminación en `CategoriaRepository.eliminar(...)`;
- ejecuta `flush()`;
- realiza `commit()`;
- ejecuta `rollback()` ante excepciones.

En `CategoriaServiceTest` se incorporaron:

- `deberiaEliminarCategoriaExistente()`;
- `deberiaLanzarExcepcionCuandoSeEliminaUnaCategoriaInexistente()`.

### Tests verificados

La batería general quedó en **138/138 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.

También se ejecutó `git diff --check`, sin errores de formato.

### Commit asociado

- `59b9628` — `feat: completar eliminacion de CategoriaService`.

El commit fue publicado en `main` de GitHub y Bitbucket.

### Próximo paso

Definir el siguiente bloque funcional antes de implementar código nuevo.

## Regla para futuros Builds

Agregar cada Build nuevo inmediatamente después de verificar código, tests y commit, manteniendo sincronizados los documentos de continuidad.
