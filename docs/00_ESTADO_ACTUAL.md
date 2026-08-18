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

El último bloque trabajado es **Build 036 — Ampliación de cobertura de InstitucionFinancieraService**.

El Build 035 había ampliado la cobertura de `CuentaServiceTest` y el Build 036 continúa esa línea ampliando la cobertura de `InstitucionFinancieraServiceTest`, sin modificar código de producción.

En `InstitucionFinancieraServiceTest` se agregaron **15 tests**, llevando la clase a **23/23 tests en verde**.

La batería general quedó en **201/201 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`. La ejecución terminó con `BUILD SUCCESS`.

También se ejecutó `git diff --check`, sin errores de whitespace. Las líneas en blanco accidentales que aparecieron en `InstitucionFinancieraService.java` fueron descartadas antes del commit, por lo que el commit de Build 036 contiene únicamente cambios de tests.

## Commit actual de código

- `bd7f4bd` — `test: ampliar cobertura de InstitucionFinancieraService`

Este commit contiene la ampliación de `InstitucionFinancieraServiceTest` y está publicado en `main` de GitHub y Bitbucket.

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

La batería general confirmada actualmente es de **201/201 tests en verde**.

Clases de servicio y cantidad actual de tests:

- `CategoriaServiceTest`: **12**
- `CuentaServiceTest`: **40**
- `InstitucionFinancieraServiceTest`: **23**
- `MonedaServiceTest`: **8**
- `MovimientoServiceTest`: **37**
- `PerfilFinancieroServiceTest`: **6**
- `UsuarioServiceTest`: **5**

La infraestructura de pruebas utiliza `JpaTestManager`, H2 en memoria y aislamiento entre ejecuciones.

## Git

El trabajo de código más reciente está en `main` mediante el commit `bd7f4bd`.

La documentación de continuidad se actualiza en `docs/continuidad-sofp`.

## Próximo paso

Revisar los casos de uso pendientes del dominio y definir el siguiente bloque funcional antes de implementar código nuevo.

No comenzar otro bloque de código hasta definir claramente su objetivo y los tests que deben cubrirlo.

## Regla de continuidad

Cada bloque importante debe terminar con código funcionando, tests en verde, commit identificable, actualización de documentación y registro del próximo paso.
