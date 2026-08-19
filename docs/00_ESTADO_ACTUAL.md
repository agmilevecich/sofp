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

El último bloque trabajado es **Build 039 — Ampliación de cobertura de UsuarioService**.

Se amplió `UsuarioServiceTest`, pasando de **5 a 15 tests en verde**, y se endureció el contrato de `UsuarioService` para validar explícitamente IDs nulos en `activar(...)` y `desactivar(...)`.

La batería general quedó en **227/227 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`. La ejecución terminó con `BUILD SUCCESS` el 18/08/2026 a las 22:34:24 -03:00.

## Commit actual de código

El commit de código de Build 039 ya fue registrado y publicado:

- `0e27dfe` — `test: ampliar cobertura de UsuarioService`

Fue publicado en `main` de GitHub y Bitbucket mediante `git pushall`.

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

La batería general confirmada actualmente es de **227/227 tests en verde**.

Clases de servicio y cantidad actual de tests:

- `CategoriaServiceTest`: **12**
- `CuentaServiceTest`: **40**
- `InstitucionFinancieraServiceTest`: **23**
- `MonedaServiceTest`: **17**
- `MovimientoServiceTest`: **37**
- `PerfilFinancieroServiceTest`: **13**
- `UsuarioServiceTest`: **15**

Total de tests de services: **157**.

La infraestructura de pruebas utiliza `JpaTestManager`, H2 en memoria y aislamiento entre ejecuciones.

## Git

El último commit de código confirmado en `main` es `0e27dfe` — `test: ampliar cobertura de UsuarioService`.

El commit fue publicado tanto en GitHub como en Bitbucket y el working tree quedó limpio después de `git pushall`.

La documentación de continuidad se mantiene en la rama `docs/continuidad-sofp`.

## Próximo paso

Definir claramente el siguiente bloque funcional y sus tests antes de implementar código nuevo.

No hay cambios de código pendientes de publicar en Build 039.

## Regla de continuidad

Cada bloque importante debe terminar con código funcionando, tests en verde, commit identificable cuando corresponda, actualización de documentación y registro del próximo paso.
