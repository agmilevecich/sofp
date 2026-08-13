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

### Objetivo

Incorporar la primera capa de repositorios JPA para entidades ya existentes del dominio, manteniendo el desarrollo incremental y verificable mediante tests.

### Cambios principales

Se incorporaron:

- `UsuarioRepository`
- `PerfilFinancieroRepository`
- `InstitucionFinancieraRepository`
- `MonedaRepository`
- Tests correspondientes de cada repositorio.

### Commits asociados

- `9e1a9c3` — `feat(persistence): agregar repositories de Usuario y PerfilFinanciero`.
- `5a3ebfb` — `Build: agrega repositorios de InstitucionFinanciera y Moneda`.

### Resultado

Los tests de los cuatro repositorios fueron ejecutados correctamente y terminaron en verde. Build 012 queda cerrado.

## Build 013 — Repository JPA de Cuenta

### Objetivo

Incorporar la capa de persistencia JPA para la entidad `Cuenta`.

### Cambios principales

Se incorporó `CuentaRepository` y `CuentaRepositoryTest`.

El repositorio proporciona operaciones para guardar, actualizar, buscar por ID, listar todas las cuentas y listar cuentas por `PerfilFinanciero`.

### Tests verificados

`CuentaRepositoryTest` verifica guardar/buscar, listar, listar por perfil y actualizar una cuenta existente.

### Resultado

Todos los tests terminaron en verde y la batería general también quedó completamente en verde.

## Build 014 — Repository JPA de Movimiento

### Objetivo

Incorporar la capa de persistencia JPA para `Movimiento`.

### Cambios principales

Se incorporó:

- `MovimientoRepository`
- `MovimientoRepositoryTest`

El repositorio proporciona guardar, actualizar, buscar por ID, listar todos, listar por cuenta y listar por categoría.

### Resultado

`MovimientoRepositoryTest` terminó en verde y la batería general quedó en **64 tests en verde**.

### Commit asociado

- `4f0b20f` — `Build 014 - Implementación de MovimientoRepository`.

## Build 015 — Servicio de saldo de cuentas

### Objetivo

Iniciar la capa `service` y centralizar el cálculo del saldo de una cuenta a partir de sus movimientos.

### Cambios principales

Se incorporó:

- `CuentaService`
- `CuentaServiceTest`

`CuentaService` recibe `MovimientoRepository` y expone `calcularSaldo(Long cuentaId)`.

Reglas: `INGRESO` suma, `EGRESO` resta, una cuenta sin movimientos devuelve `BigDecimal.ZERO` y se procesan múltiples movimientos.

### Tests verificados

`CuentaServiceTest` verifica saldo cero, ingreso, egreso y múltiples movimientos.

### Resultado

La batería general terminó con **68/68 tests en verde**.

### Commit asociado

- `4697815` — `feat: implementar servicio de saldo de cuentas`.

## Build 016 — Servicio de movimientos

### Objetivo

Ampliar la capa `service` incorporando un servicio para registrar y consultar movimientos financieros.

### Cambios principales

Se incorporó:

- `MovimientoService`
- `MovimientoServiceTest`

El servicio permite registrar, buscar, listar todos, listar por cuenta y listar por categoría. El registro utiliza transacciones explícitas, `flush()`, `commit()` y `rollback()`.

### Tests verificados

`MovimientoServiceTest` verifica 6 casos: ingreso, egreso, listado por cuenta, listado por categoría, listado general y búsqueda por ID.

### Resultado

Los 6 tests terminaron en verde y la batería general quedó en **74/74 tests en verde**.

### Commit asociado

- `8f8594e` — `feat: implementar servicio de movimientos`.

## Build 017 — Repository JPA de Categoria

### Objetivo

Completar la capa de persistencia para `Categoria`.

### Cambios principales

Se incorporó `CategoriaRepository` y `CategoriaRepositoryTest`.

### Resultado

Los tests del repositorio terminaron completamente en verde.

### Commit asociado

- `f462b3b` — `feat: implementar CategoriaRepository`.

## Build 018 — Servicio de Categoria

### Objetivo

Ampliar la capa `service` incorporando el servicio de aplicación para `Categoria`.

### Cambios principales

Se incorporó `CategoriaService` y `CategoriaServiceTest`.

El servicio permite registrar, buscar por ID, listar todas y listar por perfil financiero.

### Tests verificados

Los 4 tests de `CategoriaServiceTest` terminaron en verde.

### Resultado

La batería general quedó en **82/82 tests en verde**.

### Commit asociado

- `d57e0b4` — `feat: implementar CategoriaService`.

## Build 019 — Servicio de PerfilFinanciero

### Objetivo

Ampliar la capa `service` para `PerfilFinanciero`.

### Cambios principales

Se incorporó `PerfilFinancieroService` y `PerfilFinancieroServiceTest`.

El servicio permite guardar, buscar, listar por usuario, cambiar descripción, activar y desactivar perfiles.

### Tests verificados

Los 6 tests de `PerfilFinancieroServiceTest` terminaron en verde.

### Resultado

La batería general quedó en **88/88 tests en verde**.

### Commit asociado

- `1cc00ca` — `feat: implementar PerfilFinancieroService`.

## Build 020 — Servicio de Usuario

### Objetivo

Completar progresivamente la capa `service` para `Usuario`.

### Cambios principales

Se incorporó `UsuarioService` y `UsuarioServiceTest`.

El servicio permite guardar, buscar por ID, buscar por email, listar, activar y desactivar usuarios.

### Tests verificados

Los 5 tests de `UsuarioServiceTest` terminaron en verde.

### Resultado

La batería general quedó en **93/93 tests en verde**.

### Commit asociado

- `87786fe` — `feat: implementar UsuarioService`.

## Build 021 — Servicio de InstitucionFinanciera

### Objetivo

Completar progresivamente la capa `service` para `InstitucionFinanciera`.

### Cambios principales

Se incorporó `InstitucionFinancieraService` y `InstitucionFinancieraServiceTest`.

El servicio permite guardar, buscar por ID y nombre, listar, renombrar, actualizar sitio web y descripción, activar y desactivar instituciones.

### Tests verificados

Los 8 tests de `InstitucionFinancieraServiceTest` terminaron en verde. Durante la implementación se corrigió un problema de aislamiento de datos en el test de listado.

### Resultado

La batería general quedó en **101/101 tests en verde**.

### Commit asociado

- `20e21c3` — `feat: implementar InstitucionFinancieraService`.

## Build 022 — Servicio de Moneda

### Objetivo

Completar progresivamente la capa `service` para `Moneda`.

### Cambios principales

Se incorporó `MonedaService` y `MonedaServiceTest`.

El servicio permite guardar, buscar por ID y código, listar, cambiar nombre y cambiar cantidad de decimales.

### Tests verificados

Los 8 tests de `MonedaServiceTest` terminaron en verde. Se corrigió una violación de unicidad de `MONEDAS(CODIGO)` aislando la base H2 de cada test mediante `JpaTestManager.close()`.

### Resultado

La batería general quedó en **109/109 tests en verde**.

### Commit asociado

- `0d0db87` — `feat: implementar MonedaService`.

## Build 023 — Ampliación de CuentaService

### Objetivo

Ampliar `CuentaService` para centralizar las operaciones de aplicación de `Cuenta`, manteniendo el cálculo del saldo basado en movimientos y agregando operaciones básicas de gestión mediante `CuentaRepository`.

### Cambios principales

Se amplió `CuentaService` y `CuentaServiceTest` para incorporar operaciones de gestión de cuentas junto con el cálculo de saldo.

### Tests verificados

`CuentaServiceTest` quedó ampliado a **8 tests**, cubriendo registrar, buscar, listar, listar por perfil y las cuatro verificaciones de saldo.

### Resultado

Los **8/8 tests de `CuentaServiceTest` terminaron en verde** y la batería general quedó en **113/113 tests en verde**.

### Commit asociado

- `ea595d4` — `feat: ampliar CuentaService`.

El commit fue publicado en `main` de GitHub y Bitbucket.

## Build 024 — Ampliación de MovimientoService

### Objetivo

Ampliar `MovimientoService` para centralizar nuevas operaciones de modificación de movimientos y reforzar las validaciones de sus consultas y operaciones de actualización.

### Cambios principales

Se amplió `MovimientoService` para incorporar:

- Validación obligatoria de los identificadores utilizados en búsquedas y listados.
- Modificación de la descripción de un movimiento.
- Modificación de las observaciones.
- Cambio de categoría.
- Validación de existencia del movimiento antes de modificarlo.
- Control transaccional explícito para las modificaciones, con `flush()`, `commit()` y `rollback()`.

Se amplió `MovimientoServiceTest` de 6 a **11 tests**.

### Tests verificados

Los nuevos casos verifican:

1. Modificar la descripción.
2. Modificar las observaciones.
3. Cambiar la categoría.
4. Lanzar excepción al modificar un movimiento inexistente.
5. Lanzar excepción al cambiar la categoría de un movimiento inexistente.

Se mantuvieron y verificaron también los 6 casos existentes de registro, consulta y listado.

### Resultado

Los **11/11 tests de `MovimientoServiceTest` terminaron en verde**.

La batería general del proyecto terminó con **118/118 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.

Build 024 queda cerrado con el servicio ampliado, sus tests verificados y sin regresiones en la batería general.

### Commit asociado

- `110f7d7` — `feat: ampliar MovimientoService`.

El commit fue publicado en `main` de GitHub y Bitbucket.

### Próximo paso

Definir el Build 025 a partir del estado real de la capa `service`, los repositorios disponibles y los casos de uso que todavía deban incorporarse.

## Regla para futuros Builds

Agregar cada Build nuevo inmediatamente después de cerrarlo, incluyendo:

- número y nombre;
- objetivo;
- cambios principales;
- tests ejecutados;
- resultado;
- commit asociado;
- próximo paso.
