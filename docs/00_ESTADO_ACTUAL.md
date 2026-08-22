# SOFP — Estado actual

> Documento de continuidad del proyecto. Debe actualizarse al cerrar cada bloque importante de trabajo.

## Identidad del proyecto

**Nombre:** SOFP — Sistema Operativo Financiero Personal  
**Repositorio:** agmilevecich/sofp  
**Rama principal:** `main`  
**Rama de trabajo de la funcionalidad actual:** `feature/operacion-financiera`  
**Rama de documentación:** `docs/continuidad-sofp`

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

**Build 047 — Completar cobertura de `OperacionFinancieraService` está cerrado y validado.**

El servicio permite materializar una transferencia como:

- una `OperacionFinanciera`;
- un `EGRESO` en la cuenta origen;
- un `INGRESO` en la cuenta destino.

La operación y los dos movimientos se persisten dentro de una única transacción, con rollback ante excepciones.

El servicio valida además cuentas activas, coherencia entre cuenta y categoría, moneda común y parámetros obligatorios. La descripción puede ser nula.

## Tests

`OperacionFinancieraServiceTest` quedó en **20/20 tests en verde**.

La cobertura incluye operación exitosa, generación de ambos movimientos, parámetros nulos, importes inválidos, cuentas inactivas, perfiles incompatibles, monedas diferentes, fecha/hora nula, ausencia de persistencia ante operaciones rechazadas y descripción nula.

La suite general del proyecto fue ejecutada mediante Maven y quedó en **309/309 tests en verde**, con `Failures: 0`, `Errors: 0`, `Skipped: 0` y `BUILD SUCCESS`.

La ejecución finalizó el **21/08/2026 a las 20:36:10 -03:00**, con una duración total de **08:14 min**.

## Último commit de código

En la rama `feature/operacion-financiera`:

- `615161c` — `test: completar cobertura de OperacionFinancieraService`

La rama `feature/operacion-financiera` está limpia y sincronizada con GitHub y Bitbucket.

Los commits de código del bloque actual son:

- `a995937` — `feat: implementar servicio de operacion financiera`.
- `2e4b94f` — `fix: permitir descripcion nula en transferencia`.
- `62f2da3` — `fix: validar descripcion en transferencia`.
- `615161c` — `test: completar cobertura de OperacionFinancieraService`.

**Importante:** la funcionalidad continúa aislada de `main`. Build 047 no fue incorporado a `main`.

## Dominio construido

Entidades principales:

- `Usuario`
- `PerfilFinanciero`
- `InstitucionFinanciera`
- `Moneda`
- `Cuenta`
- `Categoria`
- `Movimiento`
- `OperacionFinanciera`

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

Todavía no existe `OperacionFinancieraRepository`.

## Services

La capa `service` contiene:

- `CuentaService`
- `MovimientoService`
- `CategoriaService`
- `PerfilFinancieroService`
- `UsuarioService`
- `InstitucionFinancieraService`
- `MonedaService`
- `OperacionFinancieraService`

`MovimientoService` gestiona registro, búsqueda, listados, modificaciones y eliminación mediante transacciones explícitas. Desde Build 033 valida coherencia entre cuenta, categoría y perfil financiero y rechaza movimientos sobre cuentas desactivadas.

`OperacionFinancieraService` coordina la creación de la operación financiera y sus dos movimientos dentro de una única transacción.

## Tests confirmados

Conteo confirmado de tests de services:

- `CategoriaServiceTest`: **21**
- `CuentaServiceTest`: **47**
- `InstitucionFinancieraServiceTest`: **26**
- `MonedaServiceTest`: **17**
- `MovimientoServiceTest`: **50**
- `PerfilFinancieroServiceTest`: **13**
- `UsuarioServiceTest`: **15**
- `OperacionFinancieraServiceTest`: **20**

Total de tests de services registrados: **209**.

Tests de dominio destacados:

- `MovimientoTest`: **27**
- `OperacionFinancieraTest`: **7**

Suite completa validada: **309/309 tests en verde**.

La infraestructura de pruebas utiliza `JpaTestManager`, H2 en memoria y aislamiento entre ejecuciones.

## Decisiones de dominio relevantes

Las transferencias no se modelan como un tercer `TipoMovimiento`. Conceptualmente una transferencia genera un **EGRESO en la cuenta origen** y un **INGRESO en la cuenta destino**, vinculados mediante una `OperacionFinanciera`.

`OperacionFinancieraService` ya materializa esta operación y coordina su persistencia transaccional.

## Git

Estado de referencia:

- rama de funcionalidad: `feature/operacion-financiera`;
- último commit de código: `615161c` — `test: completar cobertura de OperacionFinancieraService`;
- rama de documentación: `docs/continuidad-sofp`;
- `main`: permanece en `028aaee` y no fue modificado por Build 047.

La documentación de continuidad se mantiene separada de la rama feature, siguiendo el flujo acordado para el proyecto.

## Próximo paso

Definir y comenzar el siguiente bloque funcional en `feature/operacion-financiera`, manteniendo la documentación en `docs/continuidad-sofp` y sin incorporar todavía el trabajo a `main`.

Antes de implementar un nuevo componente, revisar el dominio, repositorios, servicios y tests relacionados y mantener el cambio mínimo necesario.

No implementar todavía `OperacionFinancieraRepository` hasta confirmar si la persistencia de la operación requiere un repositorio independiente.

## Build 047 — Completar cobertura de OperacionFinancieraService

**Fecha:** 21/08/2026  
**Estado:** COMPLETADO

Se completó la cobertura de `OperacionFinancieraServiceTest`.

Se incorporaron pruebas para las validaciones de cuentas inactivas, categorías de otros perfiles, monedas diferentes, fecha/hora nula, descripción nula y ausencia de persistencia de movimientos ante operaciones rechazadas.

Resultado:

- 20 tests de `OperacionFinancieraServiceTest` en verde.
- Suite completa: **309 tests**.
- Failures: **0**.
- Errors: **0**.
- Skipped: **0**.
- **BUILD SUCCESS**.
- Tiempo: **08:14 min**.
- Finalización: **21/08/2026 20:36:10 -03:00**.

El Build 047 queda cerrado y validado.

## Regla de continuidad

Cada bloque importante debe terminar con código funcionando, tests en verde, commit identificable cuando corresponda, actualización de documentación y registro del próximo paso.
