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

## Estado funcional actual

El último bloque trabajado es **Build 034 — Ampliación de cobertura de MovimientoService**.

El Build 033 había incorporado las reglas de negocio de `Movimiento` y el Build 034 amplió exclusivamente la cobertura de tests, sin modificar código de producción.

En `MovimientoServiceTest` se agregaron **17 tests**, llevando la clase a **32/32 tests en verde**.

La batería general quedó en **163/163 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`. La ejecución terminó con `BUILD SUCCESS`.

También se ejecutó `git diff --check`, sin errores.

## Commit actual de código

- `4d9dc2a` — `test: ampliar cobertura de MovimientoService`

Este commit contiene únicamente la ampliación de `MovimientoServiceTest` y se encuentra en `main` local.

## Dominio construido

Entidades principales:

- `Usuario`
- `PerfilFinanciero`
- `InstitucionFinanciera`
- `Moneda`
- `Cuenta`
- `Categoria`
- `Movimiento`

Enumeraciones principales:

- `TipoInstitucionFinanciera`
- `TipoMoneda`
- `TipoCuenta`
- `TipoMovimiento`

## Persistencia

Repositorios JPA incorporados:

- `UsuarioRepository`
- `PerfilFinancieroRepository`
- `InstitucionFinancieraRepository`
- `MonedaRepository`
- `CuentaRepository`
- `MovimientoRepository`
- `CategoriaRepository`

`MovimientoRepository` proporciona guardar, actualizar, buscar por ID, listar todos, listar por cuenta, listar por categoría y eliminar movimientos.

## Services

La capa `service` contiene:

- `CuentaService`
- `MovimientoService`
- `CategoriaService`
- `PerfilFinancieroService`
- `UsuarioService`
- `InstitucionFinancieraService`
- `MonedaService`

`MovimientoService` gestiona registro, búsqueda, listados, modificaciones y eliminación mediante transacciones explícitas. Desde Build 033 valida coherencia entre cuenta, categoría y perfil financiero y rechaza movimientos sobre cuentas desactivadas.

## Tests

La batería general confirmada actualmente es de **163/163 tests en verde**.

`MovimientoServiceTest` cuenta con **32/32 tests en verde**.

La infraestructura de pruebas utiliza `JpaTestManager`, H2 en memoria y aislamiento entre ejecuciones.

## Git

El trabajo de código más reciente está en `main` mediante el commit `4d9dc2a`.

La documentación de continuidad se actualiza en `docs/continuidad-sofp`.

## Próximo paso

Cerrar documentalmente el Build 034, sincronizar las ramas/remotos y después definir el siguiente bloque funcional antes de implementar código nuevo.

No comenzar otro bloque de código hasta definir claramente su objetivo y los tests que deben cubrirlo.

## Regla de continuidad

Cada bloque importante debe terminar con código funcionando, tests en verde, commit identificable, actualización de documentación y registro del próximo paso.
