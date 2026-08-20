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

El último bloque confirmado es **Build 043 — Ampliación de cobertura de MovimientoService**.

Se inició el siguiente bloque de pruebas, **Build 044 — Ampliación de cobertura de Movimiento**, sobre `MovimientoTest`. Se agregaron **23 tests**, pasando la clase de **4 a 27 tests en verde**. No se modificó código de producción.

La suite general de Build 043 permanece confirmada en **259/259 tests en verde**. La suite general posterior a los nuevos 23 tests todavía debe ejecutarse para cerrar Build 044.

## Último commit de código

El último commit de código publicado es:

- `6f53f79` — `test: ampliar cobertura de Movimiento`

Fue publicado en `main` de GitHub y Bitbucket mediante `git pushall`. El working tree quedó limpio.

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

La última batería general confirmada es de **259/259 tests en verde**, correspondiente a Build 043.

Después de la ampliación de `MovimientoTest`, la clase quedó en **27/27 tests en verde**. La ejecución de la suite general posterior a este cambio queda como siguiente verificación.

Conteo confirmado de tests de services al cierre de Build 043:

- `CategoriaServiceTest`: **21**
- `CuentaServiceTest`: **47**
- `InstitucionFinancieraServiceTest`: **26**
- `MonedaServiceTest`: **17**
- `MovimientoServiceTest`: **50**
- `PerfilFinancieroServiceTest`: **13**
- `UsuarioServiceTest`: **15**

Total confirmado de tests de services: **189**.

La infraestructura de pruebas utiliza `JpaTestManager`, H2 en memoria y aislamiento entre ejecuciones.

## Git

Último commit de código confirmado en `main`:

- `6f53f79` — `test: ampliar cobertura de Movimiento`

El commit fue publicado tanto en GitHub como en Bitbucket y el working tree quedó limpio después de `git pushall`.

La documentación de continuidad se mantiene en la rama `docs/continuidad-sofp`.

## Próximo paso

Ejecutar la suite general después de la ampliación de `MovimientoTest`. Si todos los tests quedan verdes, cerrar Build 044, registrar el resultado y actualizar nuevamente la continuidad.

No implementar código de producción nuevo hasta definir el siguiente bloque después de cerrar esta verificación.

## Regla de continuidad

Cada bloque importante debe terminar con código funcionando, tests en verde, commit identificable cuando corresponda, actualización de documentación y registro del próximo paso.
