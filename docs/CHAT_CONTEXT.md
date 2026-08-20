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

## Estado actual — 20/08/2026

Último Build cerrado: **Build 043 — Ampliación de cobertura de MovimientoService**.

Está en curso **Build 044 — Ampliación de cobertura de Movimiento**.

Último commit de código confirmado:

- `6f53f79` — `test: ampliar cobertura de Movimiento`

Build 044 amplió `MovimientoTest` sin modificar código de producción, pasando de **4 a 27 tests en verde** mediante **23 tests nuevos**.

Resultado específico confirmado:

- `MovimientoTest`: **27/27 tests en verde**.
- Failures: 0 en la ejecución específica.

La suite general posterior a Build 044 todavía debe ejecutarse. La última suite general confirmada corresponde a Build 043: **259/259 tests en verde**, `Failures: 0`, `Errors: 0`, `Skipped: 0`, `BUILD SUCCESS`.

El commit de código de Build 044 ya está registrado y publicado en `main` de GitHub y Bitbucket mediante `git pushall`. El working tree quedó limpio.

## Build 044 — En curso

Se amplió `MovimientoTest` con 23 tests nuevos.

La cobertura agregada incluye validaciones del constructor y de las operaciones de modificación de `Movimiento`, incluyendo cuenta, categoría, tipo, importe, fecha/hora, descripción y observaciones.

No se modificó código de producción.

La documentación de continuidad registra Build 044 como bloque abierto hasta ejecutar la suite general y confirmar su resultado.

## Build 043

Se amplió `MovimientoServiceTest` con la cobertura acordada durante la revisión de `MovimientoService`.

No se modificó código de producción.

La batería general quedó en **259/259 tests en verde**.

Commit: `b6384f0` — `test: ampliar cobertura de MovimientoService`.

## Build 042

Se amplió `CuentaServiceTest`, pasando de **40 a 47 tests en verde**, sin modificar código de producción.

La batería general quedó en **246/246 tests en verde**.

Commit: `526b378` — `test: ampliar cobertura de CuentaService`.

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

La última batería general confirmada es de **259/259 tests en verde**.

Conteo confirmado de tests de services al cierre de Build 043:

- `CategoriaServiceTest`: **21**
- `CuentaServiceTest`: **47**
- `InstitucionFinancieraServiceTest`: **26**
- `MonedaServiceTest`: **17**
- `MovimientoServiceTest`: **50**
- `PerfilFinancieroServiceTest`: **13**
- `UsuarioServiceTest`: **15**

Total confirmado: **189 tests de services**.

`MovimientoTest` cuenta ahora con **27/27 tests en verde** dentro del Build 044 en curso.

La infraestructura de pruebas utiliza `JpaTestManager`, H2 en memoria y aislamiento entre ejecuciones.

## Próximo paso

Ejecutar la suite general después de la ampliación de `MovimientoTest`. Si todos los tests quedan verdes, cerrar Build 044, registrar el resultado definitivo y definir el siguiente bloque.

No implementar código nuevo hasta cerrar esta verificación.

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
