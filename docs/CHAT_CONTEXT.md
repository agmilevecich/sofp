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

Último Build: **Build 042 — Ampliación de cobertura de CuentaService**.

Último commit de código confirmado:

- `526b378` — `test: ampliar cobertura de CuentaService`

Build 042 amplió `CuentaServiceTest` sin modificar código de producción, pasando de **40 a 47 tests en verde**.

Resultado:

- `CategoriaServiceTest`: **21**
- `CuentaServiceTest`: **47**
- `InstitucionFinancieraServiceTest`: **26**
- `MonedaServiceTest`: **17**
- `MovimientoServiceTest`: **37**
- `PerfilFinancieroServiceTest`: **13**
- `UsuarioServiceTest`: **15**
- Total de tests de services: **176**.
- Batería general: **246/246 tests en verde**.
- Failures: 0.
- Errors: 0.
- Skipped: 0.
- BUILD SUCCESS.

La ejecución general de Build 042 finalizó el 19/08/2026 a las 16:46:27 -03:00.

El commit de código de Build 042 ya está registrado y publicado en `main` de GitHub y Bitbucket mediante `git pushall`.

## Build 042

Se amplió `CuentaServiceTest` con la cobertura acordada durante la revisión de `CuentaService`.

No se modificó código de producción.

La documentación específica quedó registrada en `docs/01-builds/Build-042.md`.

## Build 041

Se reforzó `InstitucionFinanciera` para rechazar valores nulos en constructor y operaciones de modificación.

En `MonedaService` se incorporó validación explícita de IDs nulos en las operaciones de modificación.

En `PerfilFinancieroService` se incorporaron validaciones explícitas de IDs y descripción y se centralizó la obtención de perfiles existentes mediante `obtenerPorId(...)`.

`InstitucionFinancieraServiceTest` incorporó 3 tests nuevos.

La batería general quedó en **239/239 tests en verde**.

Commit: `a9de29c` — `feat: reforzar validaciones de servicios y dominio`.

## Build 040

Se amplió `CategoriaServiceTest`, pasando de **12 a 21 tests en verde**, con 9 tests nuevos para cubrir validaciones de parámetros nulos.

La batería general quedó en **236/236 tests en verde**.

Commit: `9be5972` — `test: ampliar cobertura de CategoriaService`.

## Builds 039–034

Los Builds 039 a 034 ampliaron progresivamente la cobertura de `UsuarioService`, `PerfilFinancieroService`, `MonedaService`, `InstitucionFinancieraService`, `CuentaService` y `MovimientoService`, manteniendo la batería general en verde. El detalle completo permanece en `docs/06_BUILDS.md`, `docs/07_TESTS.md` y `docs/09_HISTORIAL_PROYECTO.md`.

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

La batería general actual está en **246/246 tests en verde**.

Conteo actual de tests de services:

- `CategoriaServiceTest`: **21**
- `CuentaServiceTest`: **47**
- `InstitucionFinancieraServiceTest`: **26**
- `MonedaServiceTest`: **17**
- `MovimientoServiceTest`: **37**
- `PerfilFinancieroServiceTest`: **13**
- `UsuarioServiceTest`: **15**

Total: **176 tests de services**.

La infraestructura de pruebas utiliza `JpaTestManager`, H2 en memoria y aislamiento entre ejecuciones.

## Próximo paso

Definir claramente el siguiente bloque funcional y sus tests antes de implementar código nuevo.

No hay cambios de código pendientes de publicar en Build 042.

## Forma de trabajo acordada

1. Definir qué vamos a construir.
2. Revisar el código, tests y reglas de negocio existentes.
3. Implementar una pieza concreta.
4. Ejecutar tests específicos.
5. Ejecutar la suite general.
6. Confirmar que quedan en verde.
7. Revisar `git diff`, `git diff --check` y `git status`.
8. Hacer commit.
9. Publicar en los remotos.
10. Actualizar documentación de continuidad.
11. Definir el siguiente paso.

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
