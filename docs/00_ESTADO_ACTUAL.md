# SOFP — Estado actual

> Documento de continuidad del proyecto. Debe actualizarse al cerrar cada bloque importante de trabajo.

## Identidad del proyecto

**Nombre:** SOFP — Sistema Operativo Financiero Personal  
**Repositorio:** agmilevecich/sofp  
**Rama principal:** `main`  
**Rama de trabajo actual para esta documentación:** `docs/continuidad-sofp`

## Objetivo

Construir una aplicación Java de finanzas personales, preparada para múltiples usuarios, con persistencia JPA/Hibernate, base H2 y una interfaz que se incorporará progresivamente.

## Stack conocido

- Java / JDK 23.0.1
- IntelliJ IDEA Community 2026.1.1
- Maven
- JPA / Jakarta Persistence
- Hibernate 6.6.18.Final
- H2 2.3.232
- JUnit 5.11.4
- BigDecimal para importes monetarios

## Arquitectura conocida

El proyecto está organizado alrededor del dominio y capas previstas para evolución posterior. Se utilizan paquetes como `domain`, `config`, `util`, `persistence`, `service`, `dto`, `enums`, `app` y `ui`.

La persistencia de producción utiliza la unidad `sofp-persistence-unit` y una base H2 en archivo.

Para pruebas JPA existe una infraestructura separada mediante `JpaTestManager` y la unidad `sofp-persistence-unit-test`, utilizando H2 en memoria y `create-drop`.

## Estado funcional actual

El último bloque funcional trabajado es **Build 029 — Eliminación en CategoriaRepository**.

Se incorporó `CategoriaRepository.eliminar(Categoria categoria)` para completar la eliminación de categorías en la capa de persistencia. La operación valida la categoría, comprueba si está gestionada mediante `EntityManager.contains(...)`, utiliza `merge(...)` cuando corresponde y ejecuta `remove(...)` sobre la instancia gestionada.

`CategoriaRepositoryTest` fue ampliado con `deberiaEliminarCategoriaExistente()`, verificando que la categoría deje de estar disponible mediante `buscarPorId(...)` después de la eliminación.

La prueba específica de `CategoriaRepositoryTest` quedó en **5/5 tests en verde**.

La batería general quedó en **136/136 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.

También se ejecutó `git diff --check`, sin errores de formato.

El commit de código del Build 029 es `46ad669` — `feat: completar eliminacion de CategoriaRepository`.

El commit fue publicado en `main` de GitHub y Bitbucket.

## Dominio construido hasta ahora

Entre las entidades/enumeraciones ya trabajadas se encuentran:

- `Usuario`
- `PerfilFinanciero`
- `InstitucionFinanciera`
- `Moneda`
- `Cuenta`
- `Categoria`
- `Movimiento`
- `TipoInstitucionFinanciera`
- `TipoMoneda`
- `TipoCuenta`
- `TipoMovimiento`

También existe infraestructura de auditoría y utilidades de validación.

## Persistencia actual

Repositorios JPA incorporados:

- `UsuarioRepository`
- `PerfilFinancieroRepository`
- `InstitucionFinancieraRepository`
- `MonedaRepository`
- `CuentaRepository`
- `MovimientoRepository`
- `CategoriaRepository`

`CuentaRepository` proporciona guardar, buscar por ID, listar todas y listar por perfil financiero.

`MovimientoRepository` proporciona guardar, actualizar, buscar por ID, listar todos, listar por cuenta, listar por categoría y eliminar movimientos.

`CategoriaRepository` proporciona guardar, buscar por ID, listar todas, listar por perfil financiero y eliminar categorías.

## Service

La capa `service` actualmente contiene:

- `CuentaService`
- `MovimientoService`
- `CategoriaService`
- `PerfilFinancieroService`
- `UsuarioService`
- `InstitucionFinancieraService`
- `MonedaService`

`CuentaService` recibe `MovimientoRepository` y `EntityManager` por constructor y proporciona operaciones de registro, búsqueda, listado, saldo, modificación y activación/desactivación.

`MovimientoService` recibe `EntityManager` y `MovimientoRepository` por constructor y proporciona las operaciones de registro, búsqueda, listado, modificación y eliminación de movimientos.

`CategoriaService` recibe `CategoriaRepository` y `EntityManager` y proporciona registro, búsqueda, listados, modificaciones y activación/desactivación de categorías. Las operaciones transaccionales utilizan `begin`, modificación, `flush`, `commit` y `rollback()` ante excepciones.

## Tests

La batería general confirmada actualmente es de **136/136 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.

La cobertura de `CategoriaRepositoryTest` fue ampliada en el Build 029 y se verificó tanto individualmente como mediante la batería general.

## Estado de Git

El commit de referencia del Build 029 es:

- `46ad669` — `feat: completar eliminacion de CategoriaRepository`

El commit fue publicado en `main` de GitHub y Bitbucket.

La rama `docs/continuidad-sofp` contiene la actualización documental correspondiente al Build 029, pendiente de sincronizarse con `main` cuando se cierre el bloque documental.

## Próximo paso

Evaluar la incorporación de la operación de eliminación en `CategoriaService`, siguiendo el patrón aplicado anteriormente en `MovimientoService`, junto con sus validaciones, control transaccional y tests correspondientes.

## Regla de continuidad

Cada bloque importante debe terminar con código funcionando, tests en verde, commit identificable, actualización de documentación y registro del próximo paso.
