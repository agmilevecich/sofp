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