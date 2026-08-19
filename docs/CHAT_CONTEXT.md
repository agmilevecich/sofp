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

## Estado actual — 18/08/2026

Último Build: **Build 039 — Ampliación de cobertura de UsuarioService**.

Último commit de código confirmado:

- `0e27dfe` — `test: ampliar cobertura de UsuarioService`

Build 039 amplió `UsuarioServiceTest`, pasando de **5 a 15 tests en verde**, y agregó validación explícita de IDs nulos en `UsuarioService.activar(...)` y `UsuarioService.desactivar(...)`.

Resultado:

- `CategoriaServiceTest`: **12**
- `CuentaServiceTest`: **40**
- `InstitucionFinancieraServiceTest`: **23**
- `MonedaServiceTest`: **17**
- `MovimientoServiceTest`: **37**
- `PerfilFinancieroServiceTest`: **13**
- `UsuarioServiceTest`: **15**
- Total de tests de services: **157**.
- Batería general: **227/227 tests en verde**.
- Failures: 0.
- Errors: 0.
- Skipped: 0.
- BUILD SUCCESS.

La ejecución general de Build 039 finalizó el 18/08/2026 a las 22:34:24 -03:00.

El commit de código de Build 039 ya está registrado y publicado en `main` de GitHub y Bitbucket mediante `git pushall`.

## Build 039

Se amplió `UsuarioServiceTest` con 10 tests nuevos, cubriendo búsquedas inexistentes, listado vacío, parámetros nulos y operaciones de activación/desactivación sobre usuarios inexistentes.

Se modificó `UsuarioService` para validar explícitamente IDs nulos en `activar(...)` y `desactivar(...)`, manteniendo un contrato consistente con el resto de la capa `service`.

La documentación específica quedó registrada en `docs/01-builds/Build-039.md`.

## Build 038

Se amplió `PerfilFinancieroServiceTest`, pasando de **6 a 13 tests en verde**.

La batería general quedó en **217/217 tests en verde**.

Commit: `e2d3268` — `test: ampliar cobertura de PerfilFinancieroService`.

## Build 037

Se revisaron `MonedaService`, `Moneda` y `MonedaRepository` y se amplió `MonedaServiceTest` para aumentar la cobertura del servicio.

La clase pasó de **8 a 17 tests en verde** y la batería general pasó de **201 a 210 tests en verde**.

La documentación específica quedó registrada en `docs/01-builds/Build-037.md`.

## Build 036

El Build 036 agregó **15 tests** a `InstitucionFinancieraServiceTest`, alcanzando **23/23 tests en verde** y una batería general de **201/201 tests en verde**.

Commit: `bd7f4bd` — `test: ampliar cobertura de InstitucionFinancieraService`.

## Build 035

El Build 035 agregó 20 tests a `CuentaServiceTest`, alcanzando **40/40 tests en verde** y una batería general de **186/186 tests en verde**.

Commit: `57b8ad5` — `test: ampliar cobertura de CuentaService`.

## Build 034

El Build 034 incorporó 17 tests en `MovimientoServiceTest` para ampliar la cobertura sin modificar código de producción.

Resultado al cerrar ese Build: **163/163 tests en verde**.

Commit: `4d9dc2a` — `test: ampliar cobertura de MovimientoService`.

## Build 033

El Build 033 incorporó tres reglas de negocio en `MovimientoService`:

1. Cuenta y Categoría deben pertenecer al mismo Perfil Financiero.
2. No se puede registrar un movimiento sobre una Cuenta desactivada.
3. No se puede cambiar la Categoría de un Movimiento por una categoría de otro Perfil Financiero.

Resultado al cerrar ese Build: **144/144 tests en verde**.

Commit: `b18ca96` — `feat: agregar reglas de negocio a movimientos`.

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

La batería general actual está en **227/227 tests en verde**.

Conteo actual de tests de services:

- `CategoriaServiceTest`: **12**
- `CuentaServiceTest`: **40**
- `InstitucionFinancieraServiceTest`: **23**
- `MonedaServiceTest`: **17**
- `MovimientoServiceTest`: **37**
- `PerfilFinancieroServiceTest`: **13**
- `UsuarioServiceTest`: **15**

Total: **157 tests de services**.

La infraestructura de pruebas utiliza `JpaTestManager`, H2 en memoria y aislamiento entre ejecuciones.

## Próximo paso

Definir claramente el siguiente bloque funcional y sus tests antes de implementar código nuevo.

No hay cambios de código pendientes de publicar en Build 039.

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
