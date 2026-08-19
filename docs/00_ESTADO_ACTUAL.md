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

El último bloque trabajado es **Build 041 — Reforzamiento de validaciones de servicios y dominio**.

Se reforzaron validaciones de parámetros nulos en `InstitucionFinanciera`, `MonedaService` y `PerfilFinancieroService`, y se amplió `InstitucionFinancieraServiceTest` de **23 a 26 tests**.

En `InstitucionFinanciera` se estableció que el constructor, `renombrar(...)`, `actualizarSitioWeb(...)` y `actualizarDescripcion(...)` rechacen valores nulos mediante `NullPointerException`.

En `MonedaService` se incorporó validación explícita del ID en las operaciones de modificación.

En `PerfilFinancieroService` se incorporaron validaciones explícitas de IDs y descripción y se centralizó la obtención de perfiles existentes mediante `obtenerPorId(...)`.

La batería general quedó en **239/239 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`. La ejecución terminó con `BUILD SUCCESS` el 19/08/2026 a las 15:09:48 -03:00.

## Commit actual de código

El commit de código de Build 041 ya fue registrado y publicado:

- `a9de29c` — `feat: reforzar validaciones de servicios y dominio`

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

La batería general confirmada actualmente es de **239/239 tests en verde**.

Clases de servicio y cantidad actual de tests:

- `CategoriaServiceTest`: **21**
- `CuentaServiceTest`: **40**
- `InstitucionFinancieraServiceTest`: **26**
- `MonedaServiceTest`: **17**
- `MovimientoServiceTest`: **37**
- `PerfilFinancieroServiceTest`: **13**
- `UsuarioServiceTest`: **15**

Total de tests de services: **169**.

La infraestructura de pruebas utiliza `JpaTestManager`, H2 en memoria y aislamiento entre ejecuciones.

## Git

El último commit de código confirmado en `main` es `a9de29c` — `feat: reforzar validaciones de servicios y dominio`.

El commit fue publicado tanto en GitHub como en Bitbucket y el working tree quedó limpio después de `git pushall`.

La documentación de continuidad se mantiene en la rama `docs/continuidad-sofp`.

## Próximo paso

Definir claramente el siguiente bloque funcional y sus tests antes de implementar código nuevo.

No hay cambios de código pendientes de publicar en Build 041.

## Regla de continuidad

Cada bloque importante debe terminar con código funcionando, tests en verde, commit identificable cuando corresponda, actualización de documentación y registro del próximo paso.
