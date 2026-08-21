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

Se cerró **Build 045 — Implementación del dominio de OperacionFinanciera** en la rama `feature/operacion-financiera`.

Se incorporó la entidad `OperacionFinanciera` con:

- cuenta de origen obligatoria;
- cuenta de destino obligatoria;
- importe positivo obligatorio;
- prohibición de utilizar la misma cuenta como origen y destino;
- persistencia JPA mediante la tabla `operaciones_financieras`.

Se incorporó `OperacionFinancieraTest` con **7 tests en verde**, cubriendo creación válida y las validaciones anteriores.

La suite general posterior al cambio confirmó **289/289 tests en verde**, con `Failures: 0`, `Errors: 0`, `Skipped: 0` y `BUILD SUCCESS`.

La ejecución terminó el **20/08/2026 a las 21:46:51 -03:00**, con una duración total de **08:47 min**.

## Último commit de código

En la rama `feature/operacion-financiera`:

- `1f650dc` — `feat: implementar dominio de operacion financiera`

El commit contiene únicamente:

- `src/main/java/ar/com/agmilevecich/sofp/domain/OperacionFinanciera.java`
- `src/test/java/ar/com/agmilevecich/sofp/domain/OperacionFinancieraTest.java`

La rama quedó con `working tree clean` después del commit.

**Importante:** este commit pertenece a `feature/operacion-financiera`; todavía no se registró como commit de `main`.

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

Todavía no existe `OperacionFinancieraService`.

`MovimientoService` gestiona registro, búsqueda, listados, modificaciones y eliminación mediante transacciones explícitas. Desde Build 033 valida coherencia entre cuenta, categoría y perfil financiero y rechaza movimientos sobre cuentas desactivadas.

## Tests

La última batería general confirmada es de **289/289 tests en verde**.

Conteo confirmado de tests de services:

- `CategoriaServiceTest`: **21**
- `CuentaServiceTest`: **47**
- `InstitucionFinancieraServiceTest`: **26**
- `MonedaServiceTest`: **17**
- `MovimientoServiceTest`: **50**
- `PerfilFinancieroServiceTest`: **13**
- `UsuarioServiceTest`: **15**

Total confirmado de tests de services: **189**.

`MovimientoTest`: **27**.

`OperacionFinancieraTest`: **7**.

La infraestructura de pruebas utiliza `JpaTestManager`, H2 en memoria y aislamiento entre ejecuciones.

## Decisiones de dominio relevantes

Las transferencias no se modelarán como un tercer `TipoMovimiento`. Conceptualmente una transferencia genera un **EGRESO en la cuenta origen** y un **INGRESO en la cuenta destino**, vinculados mediante una `OperacionFinanciera`.

La entidad `OperacionFinanciera` ya está implementada y validada. Todavía falta definir y construir el mecanismo que materialice la operación como movimientos y determine su persistencia y coordinación transaccional.

## Git

Último commit de código de la funcionalidad actual:

- rama `feature/operacion-financiera`
- `1f650dc` — `feat: implementar dominio de operacion financiera`

Último commit de código confirmado anteriormente en `main`:

- `dca3b80` — `test: corregir datos compartidos de Movimiento`

La documentación de continuidad se mantiene en `docs/continuidad-sofp`.

## Próximo paso

Antes de implementar nuevas clases, revisar el estado actual de `Movimiento`, `MovimientoService`, `MovimientoRepository` y sus tests para determinar cómo debe materializarse una transferencia: un **EGRESO** en la cuenta origen y un **INGRESO** en la cuenta destino, vinculados a `OperacionFinanciera`.

No implementar todavía `OperacionFinancieraRepository` ni `OperacionFinancieraService` hasta confirmar las reglas de negocio y el diseño con las clases existentes.

## Regla de continuidad

Cada bloque importante debe terminar con código funcionando, tests en verde, commit identificable cuando corresponda, actualización de documentación y registro del próximo paso.
