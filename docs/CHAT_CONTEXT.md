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

Último Build cerrado: **Build 044 — Ampliación de cobertura de Movimiento**.

Último commit de código confirmado:

- `dca3b80` — `test: corregir datos compartidos de Movimiento`

Este commit modifica únicamente `TestDataFactory`. `crearMovimiento()` ahora construye la cuenta y la categoría utilizando el mismo `PerfilFinanciero`, evitando relaciones incoherentes entre datos de prueba.

Build 044 amplió `MovimientoTest` sin modificar código de producción, pasando de **4 a 27 tests en verde** mediante **23 tests nuevos**.

Resultado específico:

- `MovimientoTest`: **27/27 tests en verde**.

Última suite general registrada:

- Tests run: **282**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Finalizada: **20/08/2026 14:17:58 -03:00**
- Duración: **06:34 min**

Esta suite fue ejecutada antes del commit `dca3b80`; la corrección posterior afecta únicamente datos compartidos de tests y no código de producción.

**Build 044 queda cerrado y validado.**

El commit `dca3b80` ya está publicado en `main` de GitHub y Bitbucket mediante `git pushall`. El working tree quedó limpio.

## Decisión de dominio: transferencias

Las transferencias no se modelarán como un tercer `TipoMovimiento`.

Conceptualmente una transferencia genera un **EGRESO en la cuenta origen** y un **INGRESO en la cuenta destino**. Ambos efectos deberán quedar vinculados posteriormente mediante una `OperacionFinanciera` que represente la operación de transferencia.

La implementación de esta decisión queda pendiente hasta el bloque de dominio correspondiente.

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

La última batería general registrada es de **282/282 tests en verde**.

Conteo confirmado de tests de services:

- `CategoriaServiceTest`: **21**
- `CuentaServiceTest`: **47**
- `InstitucionFinancieraServiceTest`: **26**
- `MonedaServiceTest`: **17**
- `MovimientoServiceTest`: **50**
- `PerfilFinancieroServiceTest`: **13**
- `UsuarioServiceTest`: **15**

Total confirmado: **189 tests de services**.

`MovimientoTest`: **27/27 tests en verde**.

La infraestructura de pruebas utiliza `JpaTestManager`, H2 en memoria y aislamiento entre ejecuciones.

## Próximo paso

Revisar el estado actual del código, tests y documentación para definir el siguiente bloque de trabajo después del cierre de Build 044 y de la corrección de `TestDataFactory`.

Antes de implementar una nueva funcionalidad se deben revisar las clases relacionadas, sus tests y las reglas de negocio documentadas. No asumir estructuras ni comportamientos no presentes en el repositorio.

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
