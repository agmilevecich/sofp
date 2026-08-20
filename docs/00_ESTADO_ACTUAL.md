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

El último bloque confirmado es **Build 044 — Ampliación de cobertura de Movimiento**.

Se amplió `MovimientoTest` sin modificar código de producción, incorporando **23 tests nuevos** y pasando de **4 a 27 tests en verde** en la clase.

La suite general posterior a la ampliación confirmó **282/282 tests en verde**, con `Failures: 0`, `Errors: 0`, `Skipped: 0` y `BUILD SUCCESS`.

Build 044 queda cerrado y validado.

Posteriormente se realizó una corrección de datos de prueba en `TestDataFactory`: `crearMovimiento()` ahora construye la `Cuenta` y la `Categoria` a partir del mismo `PerfilFinanciero`, evitando relaciones de prueba incoherentes.

## Último commit de código

El último commit de código publicado es:

- `dca3b80` — `test: corregir datos compartidos de Movimiento`

Este commit modifica únicamente `src/test/java/ar/com/agmilevecich/sofp/support/TestDataFactory.java`. Fue publicado en `main` de GitHub y Bitbucket mediante `git pushall`. El working tree quedó limpio.

El commit anterior fue:

- `6f53f79` — `test: ampliar cobertura de Movimiento`

## Última suite general registrada

- Tests run: **282**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Última ejecución registrada: **20/08/2026 14:17:58 -03:00**
- Tiempo total: **06:34 min**

Esta suite corresponde a la validación realizada antes de registrar el commit `dca3b80`; la corrección posterior es exclusivamente de datos compartidos de tests.

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

La última batería general registrada es de **282/282 tests en verde**.

Conteo confirmado de tests de services:

- `CategoriaServiceTest`: **21**
- `CuentaServiceTest`: **47**
- `InstitucionFinancieraServiceTest`: **26**
- `MonedaServiceTest`: **17**
- `MovimientoServiceTest`: **50**
- `PerfilFinancieroServiceTest`: **13**
- `UsuarioServiceTest`: **15**

Total confirmado de tests de services: **189**.

`MovimientoTest`: **27/27 tests en verde**.

La infraestructura de pruebas utiliza `JpaTestManager`, H2 en memoria y aislamiento entre ejecuciones.

## Decisiones de dominio relevantes

Las transferencias no se modelarán como un tercer `TipoMovimiento`. Conceptualmente una transferencia genera un **EGRESO en la cuenta origen** y un **INGRESO en la cuenta destino**, vinculados posteriormente por una `OperacionFinanciera`.

## Git

Último commit de código confirmado en `main`:

- `dca3b80` — `test: corregir datos compartidos de Movimiento`

La documentación de continuidad se mantiene en la rama `docs/continuidad-sofp`.

## Próximo paso

Con Build 044 cerrado y la fábrica de datos de Movimiento corregida, revisar nuevamente las clases, repositorios, servicios y tests relacionados para definir el siguiente bloque de trabajo. No implementar código nuevo hasta confirmar las reglas de negocio y el diseño de la próxima funcionalidad.

## Regla de continuidad

Cada bloque importante debe terminar con código funcionando, tests en verde, commit identificable cuando corresponda, actualización de documentación y registro del próximo paso.
