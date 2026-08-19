# SOFP — Contexto para continuar con ChatGPT

Este archivo es el punto de entrada para continuar el proyecto SOFP en una nueva conversación. La fuente permanente de verdad es el repositorio, Git, la documentación y los tests.

## Proyecto

SOFP — Sistema Operativo Financiero Personal.

Aplicación Java de finanzas personales, desarrollada progresivamente con dominio, persistencia JPA, servicios transaccionales y tests automatizados.

## Tecnologías

- Java / JDK 23.0.1
- IntelliJ IDEA Community 2026.1.1
- Maven
- Jakarta Persistence / JPA
- Hibernate 6.6.18.Final
- H2 2.3.232
- JUnit 5.11.4
- BigDecimal para dinero

## Repositorio y ramas

- GitHub: `agmilevecich/sofp`
- Bitbucket: `agmilevecich/sofp`
- Rama principal de trabajo: `main`
- Rama de documentación/continuidad: `docs/continuidad-sofp`

## Estado actual — 19/08/2026

Último Build: **Build 041 — Reforzamiento de validaciones de servicios y dominio**.

Último commit de código confirmado:

- `a9de29c` — `feat: reforzar validaciones de servicios y dominio`

Build 041 reforzó validaciones de parámetros nulos en `InstitucionFinanciera`, `MonedaService` y `PerfilFinancieroService`, y amplió `InstitucionFinancieraServiceTest` de **23 a 26 tests en verde**.

Resultado:

- `CategoriaServiceTest`: **21**
- `CuentaServiceTest`: **40**
- `InstitucionFinancieraServiceTest`: **26**
- `MonedaServiceTest`: **17**
- `MovimientoServiceTest`: **37**
- `PerfilFinancieroServiceTest`: **13**
- `UsuarioServiceTest`: **15**
- Total de tests de services: **169**.
- Batería general: **239/239 tests en verde**.
- Failures: 0.
- Errors: 0.
- Skipped: 0.
- BUILD SUCCESS.

La ejecución general de Build 041 finalizó el 19/08/2026 a las 15:09:48 -03:00.

El commit de código de Build 041 ya está registrado y publicado en `main` de GitHub y Bitbucket mediante `git pushall`.

## Build 041

Se reforzó `InstitucionFinanciera` para rechazar valores nulos en constructor y operaciones de modificación.

En `MonedaService` se incorporó validación explícita de IDs nulos en `cambiarNombre(...)` y `cambiarCantidadDecimales(...)`.

En `PerfilFinancieroService` se incorporaron validaciones explícitas de IDs y descripción y se centralizó la obtención de perfiles existentes mediante `obtenerPorId(...)`.

`InstitucionFinancieraServiceTest` incorporó 3 tests nuevos para nombre nulo, sitio web nulo y descripción nula.

La documentación específica quedó registrada en `docs/01-builds/Build-041.md`.

## Build 040

Se amplió `CategoriaServiceTest`, pasando de **12 a 21 tests en verde**, con 9 tests nuevos para cubrir validaciones de parámetros nulos. No se modificó código de producción.

La batería general quedó en **236/236 tests en verde**.

Commit: `9be5972` — `test: ampliar cobertura de CategoriaService`.

## Build 039

Se amplió `UsuarioServiceTest`, pasando de **5 a 15 tests en verde**, y se agregó validación explícita de IDs nulos en `UsuarioService.activar(...)` y `UsuarioService.desactivar(...)`.

La batería general quedó en **227/227 tests en verde**.

Commit: `0e27dfe` — `test: ampliar cobertura de UsuarioService`.

## Build 038

Se amplió `PerfilFinancieroServiceTest`, pasando de **6 a 13 tests en verde**.

La batería general quedó en **217/217 tests en verde**.

Commit: `e2d3268` — `test: ampliar cobertura de PerfilFinancieroService`.

## Build 037

Se amplió `MonedaServiceTest`, pasando de **8 a 17 tests en verde**.

La batería general quedó en **210/210 tests en verde**.

La documentación específica quedó registrada en `docs/01-builds/Build-037.md`.

## Build 036

Se ampliaron los tests de `InstitucionFinancieraServiceTest`, alcanzando **23/23 tests en verde** y una batería general de **201/201 tests en verde**.

Commit: `bd7f4bd` — `test: ampliar cobertura de InstitucionFinancieraService`.

## Build 035

Se ampliaron los tests de `CuentaServiceTest`, alcanzando **40/40 tests en verde** y una batería general de **186/186 tests en verde**.

Commit: `57b8ad5` — `test: ampliar cobertura de CuentaService`.

## Build 034

Se ampliaron los tests de `MovimientoServiceTest`, alcanzando **32/32 tests en verde** y una batería general de **163/163 tests en verde**.

Commit: `4d9dc2a` — `test: ampliar cobertura de MovimientoService`.

## Dominio actual

Entidades principales:

- Usuario
- PerfilFinanciero
- InstitucionFinanciera
- Moneda
- Cuenta
- Categoria
- Movimiento

Enumeraciones:

- TipoInstitucionFinanciera
- TipoMoneda
- TipoCuenta
- TipoMovimiento

## Persistencia

Repositorios JPA conocidos:

- UsuarioRepository
- PerfilFinancieroRepository
- InstitucionFinancieraRepository
- MonedaRepository
- CuentaRepository
- MovimientoRepository
- CategoriaRepository

`MovimientoRepository` permite guardar, actualizar, buscar por ID, listar todos, listar por cuenta, listar por categoría y eliminar movimientos.

## Services

La capa `service` contiene actualmente:

- CuentaService
- MovimientoService
- CategoriaService
- PerfilFinancieroService
- UsuarioService
- InstitucionFinancieraService
- MonedaService

`MovimientoService` utiliza `EntityManager` y `MovimientoRepository`, mantiene transacciones explícitas y centraliza las reglas contextuales de cuenta, categoría y perfil financiero.

## Tests

La batería general actual está en **239/239 tests en verde**.

Conteo actual de tests de services:

- `CategoriaServiceTest`: **21**
- `CuentaServiceTest`: **40**
- `InstitucionFinancieraServiceTest`: **26**
- `MonedaServiceTest`: **17**
- `MovimientoServiceTest`: **37**
- `PerfilFinancieroServiceTest`: **13**
- `UsuarioServiceTest`: **15**

Total: **169 tests de services**.

La infraestructura de pruebas utiliza `JpaTestManager`, H2 en memoria y aislamiento entre ejecuciones.

## Próximo paso

Definir claramente el siguiente bloque funcional y sus tests antes de implementar código nuevo.

No hay cambios de código pendientes de publicar en Build 041.

## Forma de trabajo acordada

1. Definir qué vamos a construir.
2. Implementar una pieza concreta.
3. Ejecutar tests.
4. Confirmar que quedan en verde.
5. Revisar regresiones.
6. Registrar el Build.
7. Hacer commit.
8. Actualizar documentación.
9. Definir el siguiente paso.

## Fuente de verdad

La fuente permanente es:

1. Código del repositorio.
2. Git / historial de commits.
3. Documentación de `docs/`.
4. Tests automatizados.

Los chats son sesiones de trabajo que consultan y actualizan esa fuente.

## Al cerrar una sesión

Actualizar como mínimo:

- `docs/00_ESTADO_ACTUAL.md`
- `docs/06_BUILDS.md`
- `docs/07_TESTS.md`
- `docs/08_PENDIENTES.md`
- `docs/09_HISTORIAL_PROYECTO.md`
- `docs/CHAT_CONTEXT.md`

Si hubo una decisión arquitectónica o de negocio importante, actualizar también `docs/05_DECISIONES.md`.
