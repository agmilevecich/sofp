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

Durante la implementación se resolvieron incidencias relacionadas con entidades transitorias, aislamiento de la base H2 y ausencia de transacción activa durante el `flush()`.

### Resultado

Los 6 tests de `MovimientoServiceTest` terminaron en verde.

La batería general del proyecto terminó con **74/74 tests en verde**.

Build 016 queda cerrado con el servicio de movimientos incorporado, su comportamiento verificado y sin regresiones en la batería general.

### Commit asociado

- `8f8594e` — `feat: implementar servicio de movimientos`.

### Próximo paso

Definir el Build 017 a partir del estado real de la capa `service`, los repositorios disponibles y los casos de uso que todavía deban incorporarse.
