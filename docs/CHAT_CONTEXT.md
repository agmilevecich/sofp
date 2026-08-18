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

Último Build: **Build 034 — Ampliación de cobertura de MovimientoService**.

Último commit de código:

- `4d9dc2a` — `test: ampliar cobertura de MovimientoService`

El Build 034 agregó **17 tests** a `MovimientoServiceTest`.

Resultado:

- `MovimientoServiceTest`: **32/32 tests en verde**.
- Batería general: **163/163 tests en verde**.
- Failures: 0.
- Errors: 0.
- Skipped: 0.
- BUILD SUCCESS.

`git diff --check` no reportó errores.

## Build 034

El objetivo fue ampliar la cobertura de `MovimientoService` sin modificar código de producción.

Se cubrieron:

- IDs nulos en búsqueda, listados, modificaciones y eliminación.
- Descripción, categoría, tipo, importe y fecha/hora nulos.
- Búsqueda de movimientos inexistentes.
- Modificación de tipo, importe y fecha/hora de movimientos inexistentes.
- Eliminación de movimientos inexistentes.

La documentación específica quedó registrada en `docs/01-builds/Build-034.md`.

## Build 033

El Build 033 incorporó tres reglas de negocio en `MovimientoService`:

1. Cuenta y Categoría deben pertenecer al mismo Perfil Financiero.
2. No se puede registrar un movimiento sobre una Cuenta desactivada.
3. No se puede cambiar la Categoría de un Movimiento por una categoría de otro Perfil Financiero.

Commit: `b18ca96` — `feat: agregar reglas de negocio a movimientos`.

Resultado al cerrar ese Build: **144/144 tests en verde**.

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

La batería general actual está en **163/163 tests en verde**.

La infraestructura de pruebas utiliza `JpaTestManager`, H2 en memoria y aislamiento entre ejecuciones.

## Próximo paso

Sincronizar la documentación y el código con los remotos y, antes de implementar el siguiente Build, revisar los casos de uso pendientes del dominio y definir la próxima pieza funcional.

No comenzar otro bloque de código hasta definir claramente su objetivo y los tests que deben cubrirlo.

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
