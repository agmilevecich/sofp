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

Se incorporaron los siguientes repositorios:

- `UsuarioRepository`
- `PerfilFinancieroRepository`
- `InstitucionFinancieraRepository`
- `MonedaRepository`

Se incorporaron sus tests correspondientes:

- `UsuarioRepositoryTest`
- `PerfilFinancieroRepositoryTest`
- `InstitucionFinancieraRepositoryTest`
- `MonedaRepositoryTest`

El bloque se apoyó sobre la infraestructura de pruebas JPA aislada establecida en Build 011.

### Commits asociados

- `9e1a9c3` — `feat(persistence): agregar repositories de Usuario y PerfilFinanciero`.
- `5a3ebfb` — `Build: agrega repositorios de InstitucionFinanciera y Moneda`.

### Tests verificados

Los tests de los cuatro repositorios fueron ejecutados correctamente y terminaron en verde.

### Resultado

Build 012 queda cerrado con los repositorios y sus tests incorporados y verificados.

### Próximo paso

Definir el siguiente bloque funcional a partir del estado real del dominio, la persistencia disponible y los tests existentes.

### Regla para futuros Builds

Agregar cada Build nuevo inmediatamente después de cerrarlo, incluyendo:

- número y nombre;
- objetivo;
- cambios principales;
- tests ejecutados;
- resultado;
- commit asociado;
- próximo paso.

## Build 013 — Repository JPA de Cuenta

### Objetivo

Incorporar la capa de persistencia JPA para la entidad `Cuenta`, manteniendo el desarrollo incremental y verificable mediante tests.

### Cambios principales

Se incorporó:

- `CuentaRepository`
- `CuentaRepositoryTest`

El repositorio proporciona operaciones para:

- Guardar cuentas nuevas.
- Actualizar cuentas existentes.
- Buscar una cuenta por ID.
- Listar todas las cuentas.
- Listar cuentas por `PerfilFinanciero`.

El test verifica la integración de `Cuenta` con:

- `Usuario`
- `PerfilFinanciero`
- `InstitucionFinanciera`
- `Moneda`

### Tests verificados

`CuentaRepositoryTest` verifica:

1. Guardar y buscar una cuenta por ID.
2. Listar todas las cuentas.
3. Listar cuentas por perfil financiero.
4. Actualizar una cuenta existente.

Además, se ejecutó la batería general de tests del proyecto.

### Resultado

Todos los tests del `CuentaRepositoryTest` terminaron en verde.

La batería general de tests del proyecto también terminó completamente en verde.

Build 013 queda cerrado con el repositorio y su test incorporados y verificados.

### Próximo paso

Definir el siguiente bloque funcional a partir del estado real del dominio, la persistencia disponible y los tests existentes.

## Build 014 — Repository JPA de Movimiento

### Objetivo

Incorporar la capa de persistencia JPA para la entidad `Movimiento`, utilizando el repositorio para centralizar las operaciones de acceso a datos.

### Cambios principales

Se incorporó:

- `MovimientoRepository`
- `MovimientoRepositoryTest`

El repositorio proporciona operaciones para:

- Guardar movimientos nuevos.
- Actualizar movimientos existentes.
- Buscar movimientos por ID.
- Listar todos los movimientos.
- Listar movimientos por `Cuenta`.
- Listar movimientos por `Categoria`.

El test verifica la integración de `Movimiento` con `Usuario`, `PerfilFinanciero`, `InstitucionFinanciera`, `Moneda`, `Cuenta` y `Categoria`.

### Resultado

`MovimientoRepositoryTest` terminó en verde y la batería general del proyecto quedó en **64 tests en verde**.

Build 014 queda cerrado con el repositorio y su test incorporados y verificados.

### Commit asociado

- `4f0b20f` — `Build 014 - Implementación de MovimientoRepository`.

## Build 015 — Servicio de saldo de cuentas

### Objetivo

Iniciar la capa `service` y centralizar en ella el cálculo del saldo de una cuenta a partir de sus movimientos.

### Cambios principales

Se incorporó:

- `CuentaService`
- `CuentaServiceTest`

`CuentaService` recibe `MovimientoRepository` por constructor y expone `calcularSaldo(Long cuentaId)`.

Reglas implementadas:

- `INGRESO` suma al saldo.
- `EGRESO` resta al saldo.
- Una cuenta sin movimientos devuelve `BigDecimal.ZERO`.
- Se procesan correctamente múltiples movimientos de la misma cuenta.

### Tests verificados

`CuentaServiceTest` verifica:

1. Cuenta sin movimientos → `0.00`.
2. Un ingreso de `10,000.00` → `10,000.00`.
3. Un egreso de `3,000.00` → `-3,000.00`.
4. Múltiples movimientos (`10,000 + 5,000 - 3,000`) → `12,000.00`.

La batería general del proyecto terminó con **68/68 tests en verde**.

Durante los tests se ajustó el `tearDown()` para cerrar `JpaTestManager` y mantener aislada la base H2 en memoria entre pruebas.

### Resultado

Build 015 queda cerrado con la primera pieza de la capa `service`, su comportamiento verificado y la batería general en verde.

### Commit asociado

- `4697815` — `feat: implementar servicio de saldo de cuentas`.

### Próximo paso

Definir el Build 016 a partir del estado real de la capa `service`, los repositorios disponibles y los casos de uso que deban incorporarse.

## Build 016 — Servicio de movimientos

### Objetivo

Ampliar la capa `service` incorporando un servicio para registrar y consultar movimientos financieros, utilizando `MovimientoRepository` y control explícito de transacciones JPA.

### Cambios principales

Se incorporó:

- `MovimientoService`
- `MovimientoServiceTest`

`MovimientoService` recibe `EntityManager` y `MovimientoRepository` por constructor y proporciona:

- Registro de movimientos.
- Búsqueda de movimientos por ID.
- Listado general de movimientos.
- Listado de movimientos por cuenta.
- Listado de movimientos por categoría.

El registro utiliza una transacción explícita, realiza `flush()` antes del `commit` y ejecuta `rollback()` ante excepciones cuando la transacción continúa activa.

### Tests verificados

`MovimientoServiceTest` verifica:

1. Registrar un `INGRESO`.
2. Registrar un `EGRESO`.
3. Listar movimientos por cuenta.
4. Listar movimientos por categoría.
5. Listar todos los movimientos.
6. Buscar un movimiento por ID.

Durante la implementación se resolvieron incidencias relacionadas con entidades transitorias, aislamiento de la base H2 y ausencia de transacción activa durante el `flush`.

### Resultado

Los 6 tests de `MovimientoServiceTest` terminaron en verde.

La batería general del proyecto terminó con **74/74 tests en verde**.

Build 016 queda cerrado con el servicio de movimientos incorporado, su comportamiento verificado y sin regresiones en la batería general.

### Commit asociado

- `8f8594e` — `feat: implementar servicio de movimientos`.

### Próximo paso

Definir el Build 017 a partir del estado real de la capa `service`, los repositorios disponibles y los casos de uso que todavía deban incorporarse.

## Build 017 — Repository JPA de Categoria

### Objetivo

Completar la capa de persistencia para la entidad `Categoria`, incorporando su repositorio JPA y los tests correspondientes antes de continuar ampliando la capa `service`.

### Cambios principales

Se incorporó:

- `CategoriaRepository`
- `CategoriaRepositoryTest`

El repositorio proporciona las operaciones de persistencia necesarias para `Categoria`, siguiendo el mismo patrón utilizado en los repositorios JPA existentes.

### Tests verificados

`CategoriaRepositoryTest` verifica las operaciones principales del repositorio y terminó completamente en verde.

Los tests correspondientes al nuevo bloque fueron ejecutados correctamente, sin incidencias pendientes.

### Resultado

Build 017 queda cerrado con `CategoriaRepository` y `CategoriaRepositoryTest` incorporados y verificados.

### Commit asociado

- `f462b3b` — `feat: implementar CategoriaRepository`.

El commit fue publicado en `main` de GitHub y Bitbucket.

### Próximo paso

Definir el Build 018 a partir del estado real del dominio, la capa `service`, los repositorios disponibles y los casos de uso que todavía deban incorporarse.

## Build 018 — Servicio de Categoria

### Objetivo

Ampliar la capa `service` incorporando el servicio de aplicación para `Categoria`, utilizando `CategoriaRepository` y manteniendo el desarrollo incremental y verificable mediante tests.

### Cambios principales

Se incorporó:

- `CategoriaService`
- `CategoriaServiceTest`

`CategoriaService` recibe `CategoriaRepository` por constructor y proporciona:

- Registrar una categoría.
- Buscar una categoría por ID.
- Listar todas las categorías.
- Listar categorías por perfil financiero.

El servicio mantiene separada la lógica de aplicación de las operaciones de persistencia realizadas por `CategoriaRepository`.

### Tests verificados

`CategoriaServiceTest` verifica:

1. Registrar una categoría.
2. Buscar una categoría por ID.
3. Listar todas las categorías.
4. Listar categorías por perfil financiero.

Los 4 tests de `CategoriaServiceTest` terminaron en verde.

Además, se ejecutó la batería general del proyecto y los **82/82 tests terminaron en verde**.

### Resultado

Build 018 queda cerrado con `CategoriaService` y `CategoriaServiceTest` incorporados, verificados y sin regresiones en la batería general.

### Commit asociado

- `d57e0b4` — `feat: implementar CategoriaService`.

El commit fue publicado en `main` de GitHub y Bitbucket.

### Próximo paso

Definir el Build 019 a partir del estado real del dominio, la capa `service`, los repositorios disponibles y los casos de uso que todavía deban incorporarse.

## Build 019 — Servicio de PerfilFinanciero

### Objetivo

Ampliar la capa `service` incorporando el servicio de aplicación para `PerfilFinanciero`, utilizando `PerfilFinancieroRepository` y manteniendo el desarrollo incremental y verificable mediante tests.

### Cambios principales

Se incorporó:

- `PerfilFinancieroService`
- `PerfilFinancieroServiceTest`

`PerfilFinancieroService` recibe `PerfilFinancieroRepository` por constructor y proporciona:

- Guardar un perfil financiero.
- Buscar un perfil por ID.
- Listar todos los perfiles.
- Listar perfiles por usuario.
- Cambiar la descripción de un perfil.
- Activar un perfil.
- Desactivar un perfil.

El servicio mantiene separada la lógica de aplicación de las operaciones de persistencia realizadas por `PerfilFinancieroRepository`.

### Tests verificados

`PerfilFinancieroServiceTest` verifica:

1. Guardar y buscar un perfil por ID.
2. Listar todos los perfiles.
3. Listar perfiles por usuario.
4. Cambiar la descripción.
5. Desactivar un perfil.
6. Activar un perfil.

Los 6 tests de `PerfilFinancieroServiceTest` terminaron en verde.

Además, se ejecutó la batería general del proyecto y los **88/88 tests terminaron en verde**.

### Resultado

Build 019 queda cerrado con `PerfilFinancieroService` y `PerfilFinancieroServiceTest` incorporados, verificados y sin regresiones en la batería general.

### Commit asociado

- `1cc00ca` — `feat: implementar PerfilFinancieroService`.

El commit fue publicado en `main` de GitHub y Bitbucket.

### Próximo paso

Definir el Build 020 a partir del estado real de la capa `service`, los repositorios disponibles y los casos de uso que todavía deban incorporarse.

El siguiente candidato funcional es `UsuarioService`, para completar progresivamente la capa de servicios alrededor de las entidades principales.

## Build 020 — Servicio de Usuario

### Objetivo

Completar progresivamente la capa `service` incorporando el servicio de aplicación para `Usuario`, utilizando `UsuarioRepository` y manteniendo el desarrollo incremental y verificable mediante tests.

### Cambios principales

Se incorporó:

- `UsuarioService`
- `UsuarioServiceTest`

`UsuarioService` recibe `UsuarioRepository` por constructor y proporciona las operaciones de aplicación necesarias para:

- Guardar un usuario.
- Buscar un usuario por ID.
- Buscar un usuario por email.
- Listar todos los usuarios.
- Activar un usuario.
- Desactivar un usuario.

El servicio mantiene separada la lógica de aplicación de las operaciones de persistencia realizadas por `UsuarioRepository`.

### Tests verificados

`UsuarioServiceTest` verifica:

1. Guardar y buscar un usuario por ID.
2. Buscar un usuario por email.
3. Listar todos los usuarios.
4. Activar un usuario.
5. Desactivar un usuario.

Los 5 tests de `UsuarioServiceTest` terminaron en verde.

Además, se ejecutó la batería general del proyecto y los **93/93 tests terminaron en verde**.

### Resultado

Build 020 queda cerrado con `UsuarioService` y `UsuarioServiceTest` incorporados, verificados y sin regresiones en la batería general.

### Commit asociado

- `87786fe` — `feat: implementar UsuarioService`.

El commit fue publicado en `main` de GitHub y Bitbucket.

### Próximo paso

Definir el Build 021 a partir del estado real de la capa `service`, los repositorios disponibles y los casos de uso que todavía deban incorporarse.

## Build 021 — Servicio de InstitucionFinanciera

### Objetivo

Completar progresivamente la capa `service` incorporando el servicio de aplicación para `InstitucionFinanciera`, utilizando `InstitucionFinancieraRepository` y manteniendo el desarrollo incremental y verificable mediante tests.

### Cambios principales

Se incorporó:

- `InstitucionFinancieraService`
- `InstitucionFinancieraServiceTest`

`InstitucionFinancieraService` recibe `InstitucionFinancieraRepository` por constructor y proporciona las operaciones de aplicación necesarias para:

- Guardar una institución financiera.
- Buscar una institución por ID.
- Buscar una institución por nombre.
- Listar todas las instituciones.
- Renombrar una institución.
- Actualizar el sitio web.
- Actualizar la descripción.
- Activar una institución.
- Desactivar una institución.

El servicio mantiene separada la lógica de aplicación de las operaciones de persistencia realizadas por `InstitucionFinancieraRepository`.

### Tests verificados

`InstitucionFinancieraServiceTest` verifica:

1. Guardar y buscar una institución por ID.
2. Buscar una institución por nombre.
3. Listar todas las instituciones.
4. Renombrar una institución.
5. Actualizar el sitio web.
6. Actualizar la descripción.
7. Activar una institución.
8. Desactivar una institución.

Los 8 tests de `InstitucionFinancieraServiceTest` terminaron en verde.

Además, se ejecutó la batería general del proyecto y los **101/101 tests terminaron en verde**.

### Resultado

Build 021 queda cerrado con `InstitucionFinancieraService` y `InstitucionFinancieraServiceTest` incorporados, verificados y sin regresiones en la batería general.

### Incidencia durante las pruebas

Durante la ejecución inicial de `debeListarTodasLasInstituciones` se produjo una diferencia entre la cantidad esperada y la cantidad real de registros:

- Esperado: 2
- Actual: 7

La causa fue la existencia de registros persistidos previamente en el contexto de prueba. El test fue corregido para aislar correctamente los datos utilizados y posteriormente los 8 tests terminaron en verde.

### Commit asociado

- `20e21c3` — `feat: implementar InstitucionFinancieraService`.

El commit fue publicado en `main` de GitHub y Bitbucket.

### Próximo paso

Definir el Build 022 a partir del estado real de la capa `service`, los repositorios disponibles y los casos de uso que todavía deban incorporarse.

## Build 022 — Servicio de Moneda

### Objetivo

Completar progresivamente la capa `service` incorporando el servicio de aplicación para `Moneda`, utilizando `MonedaRepository` y manteniendo el desarrollo incremental y verificable mediante tests.

### Cambios principales

Se incorporó:

- `MonedaService`
- `MonedaServiceTest`

`MonedaService` recibe `MonedaRepository` por constructor y proporciona las operaciones de aplicación necesarias para:

- Guardar una moneda.
- Buscar una moneda por ID.
- Buscar una moneda por código.
- Listar todas las monedas.
- Cambiar el nombre de una moneda.
- Cambiar la cantidad de decimales.

El servicio mantiene separada la lógica de aplicación de las operaciones de persistencia realizadas por `MonedaRepository`.

### Tests verificados

`MonedaServiceTest` verifica:

1. Guardar y buscar una moneda por ID.
2. Buscar una moneda por código.
3. Listar todas las monedas.
4. Cambiar el nombre de una moneda.
5. Cambiar la cantidad de decimales.
6. Lanzar excepción al intentar modificar el nombre de una moneda inexistente.
7. Lanzar excepción al intentar modificar los decimales de una moneda inexistente.
8. Verificar el comportamiento inicial de una moneda creada.

Los 8 tests de `MonedaServiceTest` terminaron en verde.

Además, se ejecutó la batería general del proyecto y los **109/109 tests terminaron en verde**.

### Resultado

Build 022 queda cerrado con `MonedaService` y `MonedaServiceTest` incorporados, verificados y sin regresiones en la batería general.

### Incidencia durante las pruebas

Durante la ejecución inicial de los tests se produjo una violación de unicidad sobre `MONEDAS(CODIGO)` al intentar insertar nuevamente la moneda `ARS`.

La causa fue la reutilización del mismo `EntityManagerFactory` y, por lo tanto, de la base H2 en memoria entre tests.

La solución consistió en cerrar `JpaTestManager` en el `tearDown()` de `MonedaServiceTest`, garantizando una nueva base H2 aislada para cada test.

### Commit asociado

- `0d0db87` — `feat: implementar MonedaService`.

El commit fue publicado en `main` de GitHub y Bitbucket.

### Próximo paso

Definir el Build 023 a partir del estado real de la capa `service`, los repositorios disponibles y los casos de uso que todavía deban incorporarse.

## Build 023 — Ampliación de CuentaService

### Objetivo

Ampliar `CuentaService` para centralizar las operaciones de aplicación de `Cuenta`, manteniendo el cálculo del saldo basado en movimientos y agregando las operaciones básicas de gestión de cuentas mediante `CuentaRepository`.

### Cambios principales

Se modificó:

- `CuentaService`
- `CuentaServiceTest`

`CuentaService` incorpora el uso de:

- `CuentaRepository` para las operaciones de persistencia de cuentas.
- `MovimientoRepository` para el cálculo del saldo.

El servicio proporciona:

- Registrar una cuenta.
- Buscar una cuenta por ID.
- Listar todas las cuentas.
- Listar cuentas por perfil financiero.
- Calcular el saldo de una cuenta a partir de sus movimientos.

Se mantiene la regla de saldo:

- `INGRESO` suma al saldo.
- `EGRESO` resta al saldo.

### Tests verificados

`CuentaServiceTest` quedó ampliado a **8 tests**, verificando tanto las operaciones de gestión de cuentas como el cálculo del saldo.

La batería general del proyecto terminó con **113/113 tests en verde**.

### Resultado

Build 023 queda cerrado con la ampliación de `CuentaService`, sus tests correspondientes y sin regresiones en la batería general.

### Commit asociado

- `ea595d4` — `feat: ampliar CuentaService`.

El commit fue publicado en `main` de GitHub y Bitbucket.

### Próximo paso

Definir el Build 024 a partir del estado real de la capa `service`, los repositorios disponibles y los casos de uso que todavía deban incorporarse.
