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

El último bloque funcional trabajado es **Build 033 — Reglas de negocio de Movimiento**.

Se incorporaron en `MovimientoService` tres reglas de negocio para asegurar la coherencia entre `Movimiento`, `Cuenta`, `Categoria` y `PerfilFinanciero`:

1. Al registrar un movimiento, `Cuenta` y `Categoria` deben pertenecer al mismo `PerfilFinanciero`.
2. No se pueden registrar movimientos en una cuenta desactivada.
3. No se puede cambiar un movimiento a una categoría perteneciente a otro perfil financiero.

La validación contextual entre cuenta, categoría y perfil financiero se mantiene en `MovimientoService`, donde se reutiliza el método privado `validarPerfilFinanciero(...)`.

`MovimientoServiceTest` quedó con **18/18 tests en verde**.

La batería general quedó en **144/144 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.

También se ejecutó `git diff --check`, sin errores de formato.

El commit de código del Build 033 es `b18ca96` — `feat: agregar reglas de negocio a movimientos`.

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

`MovimientoService` recibe `EntityManager` y `MovimientoRepository` por constructor y proporciona operaciones de registro, búsqueda, listado, modificación y eliminación de movimientos. Desde Build 033 también centraliza reglas de coherencia entre cuenta, categoría y perfil financiero y rechaza movimientos sobre cuentas desactivadas.

`CategoriaService` recibe `CategoriaRepository` y `EntityManager` y proporciona registro, búsqueda, listados, modificaciones, activación/desactivación y eliminación de categorías. Las operaciones transaccionales utilizan `begin`, modificación, `flush`, `commit` y `rollback()` ante excepciones.

## Tests

La batería general confirmada actualmente es de **144/144 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.

`CuentaRepositoryTest` cubre guardar/buscar, listados, actualización y eliminación de cuentas.

`CuentaServiceTest` cubre registro, búsqueda, listados, saldo, modificaciones, activación/desactivación, eliminación y validación de eliminación de cuenta inexistente.

`CategoriaServiceTest` cubre registro, búsqueda, listados, modificaciones, activación/desactivación, eliminación y validación de eliminación de categoría inexistente.

`MovimientoServiceTest` cubre registro, búsqueda, listados, modificaciones, eliminación y las reglas de negocio incorporadas en Build 033.

## Estado de Git

El commit de referencia del Build 033 es:

- `b18ca96` — `feat: agregar reglas de negocio a movimientos`

El commit fue publicado en `main` de GitHub y Bitbucket.

La documentación de continuidad registra el estado correspondiente al Build 033.

## Próximo paso

Definir el siguiente bloque funcional antes de implementar código nuevo, revisando los casos de uso pendientes del dominio y determinando si corresponde continuar con otra pieza de `service` o persistencia.

No comenzar otro bloque de código hasta definir claramente su objetivo y los tests que deben cubrirlo.

## Regla de continuidad

Cada bloque importante debe terminar con código funcionando, tests en verde, commit identificable, actualización de documentación y registro del próximo paso.
