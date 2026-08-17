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

El último bloque funcional trabajado es **Build 032 — Eliminación en CuentaService**.

Se incorporó `CuentaService.eliminar(Long cuentaId)` para completar la eliminación de cuentas desde la capa de servicio. La operación valida el ID, verifica la existencia mediante `obtenerCuenta(...)`, inicia una transacción explícita, delega la eliminación en `CuentaRepository`, ejecuta `flush()` y `commit()`, y realiza `rollback()` ante excepciones.

`CuentaServiceTest` fue ampliado con `deberiaEliminarCuentaExistente()` y `deberiaLanzarExcepcionCuandoSeEliminaUnaCuentaInexistente()`.

La batería general quedó en **141/141 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.

También se ejecutó `git diff --check`, sin errores de formato.

El commit de código del Build 032 es `a1a817d` — `feat: completar eliminacion de CuentaService`.

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

`CuentaRepository` proporciona guardar, buscar por ID, listar todas, listar por perfil financiero y eliminar cuentas.

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

`CuentaService` recibe `CuentaRepository`, `MovimientoRepository` y `EntityManager` por constructor y proporciona operaciones de registro, búsqueda, listado, saldo, modificación, activación/desactivación y eliminación de cuentas. La eliminación valida la existencia y utiliza una transacción explícita con `flush()`, `commit()` y `rollback()` ante excepciones.

`MovimientoService` recibe `EntityManager` y `MovimientoRepository` por constructor y proporciona las operaciones de registro, búsqueda, listado, modificación y eliminación de movimientos.

`CategoriaService` recibe `CategoriaRepository` y `EntityManager` y proporciona registro, búsqueda, listados, modificaciones, activación/desactivación y eliminación de categorías. Las operaciones transaccionales utilizan `begin`, modificación, `flush`, `commit` y `rollback()` ante excepciones.

## Tests

La batería general confirmada actualmente es de **141/141 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.

`CuentaRepositoryTest` cubre ahora guardar/buscar, listados, actualización y eliminación de cuentas.

`CuentaServiceTest` cubre registro, búsqueda, listados, saldo, modificaciones, activación/desactivación, eliminación y validación de eliminación de cuenta inexistente.

`CategoriaServiceTest` cubre registro, búsqueda, listados, modificaciones, activación/desactivación, eliminación y validación de eliminación de categoría inexistente.

## Estado de Git

El commit de referencia del Build 032 es:

- `a1a817d` — `feat: completar eliminacion de CuentaService`

El commit fue publicado en `main` de GitHub y Bitbucket.

La rama `docs/continuidad-sofp` contiene la actualización documental correspondiente al Build 032.

## Próximo paso

Definir el siguiente bloque funcional antes de implementar código nuevo, revisando los casos de uso pendientes del dominio y determinando si corresponde continuar con otra pieza de `service` o persistencia.

## Regla de continuidad

Cada bloque importante debe terminar con código funcionando, tests en verde, commit identificable, actualización de documentación y registro del próximo paso.
