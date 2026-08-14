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

### Objetivo

Separar la base H2 utilizada por los tests de la base H2 de desarrollo y conseguir que la batería completa de tests pueda ejecutarse de forma repetible, sin depender de borrar manualmente la base de datos.

### Cambios principales

- Se incorporó `JpaTestManager` para la infraestructura específica de pruebas.
- Los tests JPA utilizan la unidad de persistencia `sofp-persistence-unit-test`.
- Los tests utilizan H2 en memoria con `hibernate.hbm2ddl.auto=create-drop`.
- Se evitó compartir los datos persistidos entre ejecuciones de tests mediante una base de test aislada.
- Los tests JPA cierran correctamente el `EntityManager` y `JpaTestManager`.
- Se resolvió el conflicto de unicidad del email de `Usuario` que aparecía al ejecutar la batería completa.
- Se mantuvo separada la persistencia de producción (`JpaManager`) de la infraestructura de pruebas (`JpaTestManager`).

### Tests verificados

- `JpaManagerTest`
- `UsuarioJpaTest`
- `CategoriaJpaTest`
- `CuentaJpaTest`
- `MovimientoJpaTest`
- Batería general de tests del proyecto.

### Resultado

Todos los tests de la batería general terminaron en verde. Los tests JPA también funcionan individualmente sin necesidad de borrar manualmente `database/sofp.mv.db`.

## Build 012 — Repositorios JPA de entidades base

Se incorporaron `UsuarioRepository`, `PerfilFinancieroRepository`, `InstitucionFinancieraRepository`, `MonedaRepository` y sus tests correspondientes. Los tests terminaron en verde.

## Build 013 — Repository JPA de Cuenta

Se incorporó `CuentaRepository` y `CuentaRepositoryTest`, cubriendo guardar/buscar, listar, listar por perfil y actualizar una cuenta existente. Todos los tests terminaron en verde.

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

Se incorporó `CategoriaRepository` y `CategoriaRepositoryTest`. Los tests terminaron en verde.

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

Completar la operación de eliminación de movimientos en las capas de persistencia y servicio, manteniendo las mismas reglas de validación y control transaccional utilizadas en el resto de `MovimientoService`.

### Cambios principales

Se incorporó en `MovimientoRepository`:

- `eliminar(Movimiento movimiento)`.
- Validación de que el movimiento sea obligatorio.
- Garantía de que la entidad esté gestionada mediante `contains()`/`merge()` antes de ejecutar `remove()` cuando es necesario.

Se incorporó en `MovimientoService`:

- `eliminar(Long movimientoId)`.
- Validación obligatoria del ID.
- Verificación de existencia mediante `obtenerMovimiento(...)`.
- Transacción explícita con `begin`, eliminación, `flush`, `commit` y `rollback` ante excepciones.

También se consolidó el nombre `modificarTipoMovimiento(...)` en la entidad `Movimiento` para mantener coherencia con la operación equivalente del servicio.

### Tests verificados

Se ejecutó la batería general del proyecto después de los cambios.

Resultado: **121/121 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.

### Commit asociado

- `d386d02` — `feat: completar operaciones de Movimiento`.

El commit fue publicado en `main` de GitHub y Bitbucket.

### Próximo paso

Definir el Build 027 a partir del estado real del dominio, repositorios, servicios y casos de uso pendientes.

## Build 025 — Ampliación de Movimiento y nuevas operaciones de MovimientoService

### Objetivo

Ampliar la entidad `Movimiento` y completar las operaciones de modificación de tipo, importe y fecha/hora en `MovimientoService`.

### Cambios principales

En `Movimiento` se incorporaron:

- `modificarTipoMovimiento(TipoMovimiento tipoMovimiento)`.
- `cambiarImporte(BigDecimal importe)`.
- `cambiarFechaHora(LocalDateTime fechaHora)`.

En `MovimientoService` se incorporaron:

- `modificarTipoMovimiento(Long movimientoId, TipoMovimiento tipoMovimiento)`.
- `modificarImporte(Long movimientoId, BigDecimal importe)`.
- `modificarFechaHora(Long movimientoId, LocalDateTime fechaHora)`.

Se mantuvo el patrón transaccional de validación, búsqueda, `begin`, modificación, `flush`, `commit` y `rollback` ante errores.

### Tests verificados

Se agregaron 3 casos nuevos en `MovimientoServiceTest` para modificar tipo, importe y fecha/hora.

### Resultado

Los **3 nuevos tests terminaron en verde** y la batería general terminó con **121/121 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.

### Commits asociados

- `da3b89d` — `feat: ampliar operaciones de Movimiento`.
- `81883ea` — `feat: completar operaciones de MovimientoService`.

Ambos commits fueron publicados en `main` de GitHub y Bitbucket.

## Build 026 — Completar operaciones de Movimiento

### Objetivo

Completar la gestión de movimientos incorporando la eliminación desde las capas de persistencia y servicio y consolidando los nombres de las operaciones de modificación.

### Cambios principales

Se incorporó en `MovimientoRepository`:

- `eliminar(Movimiento movimiento)`.
- La operación asegura que el movimiento esté gestionado por el `EntityManager` antes de ejecutar `remove`.

Se incorporó en `MovimientoService`:

- `eliminar(Long movimientoId)`.
- Validación del identificador.
- Búsqueda y validación de existencia del movimiento.
- Transacción explícita con `begin`, eliminación, `flush`, `commit` y `rollback` ante errores.

En `Movimiento` se consolidó el nombre de la operación de modificación de tipo como `modificarTipoMovimiento(...)`, manteniendo la validación del tipo obligatorio.

### Tests verificados

Se ejecutó la batería general del proyecto después de los cambios.

### Resultado

La batería general terminó con **121/121 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.

No se registran regresiones ni incidencias pendientes para este bloque.

### Commit asociado

- `d386d02` — `feat: completar operaciones de Movimiento`.

El commit fue publicado en `main` de GitHub y Bitbucket.

### Próximo paso

Definir el Build 027 a partir del estado real de los servicios, repositorios y casos de uso restantes.

## Regla para futuros Builds

Agregar cada Build nuevo inmediatamente después de cerrarlo, incluyendo:

- número y nombre;
- objetivo;
- cambios principales;
- tests ejecutados;
- resultado;
- commit asociado;
- próximo paso.
