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
- Rama de funcionalidad actual: `feature/operacion-financiera`
- Rama de documentación/continuidad: `docs/continuidad-sofp`

## Estado actual — 20/08/2026

Último Build cerrado: **Build 045 — Implementación del dominio de OperacionFinanciera**.

Último commit de la funcionalidad actual:

- `1f650dc` — `feat: implementar dominio de operacion financiera`

El commit está en `feature/operacion-financiera` y el working tree quedó limpio. Todavía no se incorporó a `main`.

Build 045 incorporó la entidad `OperacionFinanciera` con cuenta origen, cuenta destino e importe, más las reglas de cuenta/importe obligatorios y la prohibición de utilizar la misma cuenta como origen y destino.

Tests específicos:

- `OperacionFinancieraTest`: **7/7 tests en verde**.

Última suite general registrada sobre la rama de funcionalidad:

- Tests run: **289**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Finalizada: **20/08/2026 21:46:51 -03:00**
- Duración: **08:47 min**

**Build 045 queda cerrado y validado.**

## Decisión de dominio: transferencias

Las transferencias no se modelarán como un tercer `TipoMovimiento`.

Conceptualmente una transferencia genera un **EGRESO en la cuenta origen** y un **INGRESO en la cuenta destino**. Ambos efectos deben quedar vinculados mediante una `OperacionFinanciera` que representa la operación de transferencia.

La entidad `OperacionFinanciera` ya fue implementada y validada. Todavía falta definir y construir el mecanismo que materialice la operación como movimientos y determine su persistencia y coordinación transaccional.

## Dominio actual

Entidades principales:

- Usuario
- PerfilFinanciero
- InstitucionFinanciera
- Moneda
- Cuenta
- Categoria
- Movimiento
- OperacionFinanciera

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

Todavía no existe `OperacionFinancieraRepository`.

## Services

La capa `service` contiene actualmente:

- CuentaService
- MovimientoService
- CategoriaService
- PerfilFinancieroService
- UsuarioService
- InstitucionFinancieraService
- MonedaService

Todavía no existe `OperacionFinancieraService`.

`MovimientoService` utiliza `EntityManager` y `MovimientoRepository`, mantiene transacciones explícitas y centraliza las reglas contextuales de cuenta, categoría y perfil financiero.

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

Total confirmado: **189 tests de services**.

Tests de dominio destacados:

- `MovimientoTest`: **27**
- `OperacionFinancieraTest`: **7**

## Próximo paso

Revisar el estado actual de `Movimiento`, `MovimientoService`, `MovimientoRepository` y sus tests antes de implementar nuevas clases.

El objetivo del siguiente bloque es definir cómo `OperacionFinanciera` materializa una transferencia como un **EGRESO** en origen y un **INGRESO** en destino, cómo se vinculan ambos movimientos con la operación y dónde debe residir la coordinación transaccional.

No asumir estructuras ni comportamientos no presentes en el repositorio.

## Forma de trabajo acordada

1. Definir qué vamos a construir.
2. Revisar el código, tests y reglas de negocio existentes.
3. Implementar una pieza concreta.
4. Ejecutar tests específicos.
5. Ejecutar la suite general.
6. Confirmar que quedan en verde.
7. Revisar `git diff`, `git diff --check` y `git status`.
8. Hacer commit.
9. Publicar en los remotos cuando corresponda.
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
