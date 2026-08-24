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
- Rama principal de trabajo del repositorio: `main`
- Rama única de funcionalidad y continuidad actual: `feature/operacion-financiera`
- Rama `docs/continuidad-sofp`: **eliminada**.

Todo el código, tests y documentación de continuidad actual se mantiene dentro de `feature/operacion-financiera`.

## Estado actual — 23/08/2026

Último Build cerrado: **Build 049 — Asociación de Movimiento con OperacionFinanciera**.

Último commit funcional:

- `0f64fa9` — `feat: asociar movimientos a operacion financiera`

Los commits posteriores al funcional corresponden exclusivamente a sincronización y corrección de documentación.

La rama `feature/operacion-financiera` está separada de `main` y sincronizada entre GitHub y Bitbucket.

Build 049 incorporó la relación persistente entre `Movimiento` y `OperacionFinanciera` y completó las reglas de asociación en el dominio.

La relación se implementa mediante `@ManyToOne` en `Movimiento` y `@OneToMany(mappedBy = "operacionFinanciera")` en `OperacionFinanciera`, con la columna `operacion_financiera_id` en `Movimiento`.

Una `OperacionFinanciera` admite como máximo dos movimientos. Un movimiento no puede pertenecer a dos operaciones diferentes. La asociación se realiza mediante `OperacionFinanciera.agregarMovimiento(...)`, que mantiene ambos lados de la relación y rechaza movimientos nulos, repetidos o ya asociados a otra operación.

`OperacionFinancieraService` asocia el `EGRESO` y el `INGRESO` a la operación antes de persistirlos.

## Tests

La última batería general confirmada es de **326/326 tests en verde**.

Conteo confirmado de tests de services:

- `CategoriaServiceTest`: **21**
- `CuentaServiceTest`: **47**
- `InstitucionFinancieraServiceTest`: **26**
- `MonedaServiceTest`: **17**
- `MovimientoServiceTest`: **50**
- `PerfilFinancieroServiceTest`: **13**
- `UsuarioServiceTest`: **15**
- `OperacionFinancieraServiceTest`: **20**

Total confirmado: **209 tests de services**.

Tests de dominio destacados:

- `MovimientoTest`: **27**
- `OperacionFinancieraTest`: **14**

Tests de persistencia destacados:

- `OperacionFinancieraRepositoryTest`: **10**

Última suite general confirmada desde IntelliJ:

- Tests run: **326**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Finalizada: **23/08/2026 19:07:25 -03:00**
- Duración: **12:30 min**

## Decisión de dominio: transferencias

Las transferencias no se modelan como un tercer `TipoMovimiento`.

Conceptualmente una transferencia genera un **EGRESO en la cuenta origen** y un **INGRESO en la cuenta destino**. Ambos efectos quedan vinculados mediante una `OperacionFinanciera` que representa la operación de transferencia.

La relación entre la operación y sus movimientos ya está implementada y persistida. Una operación admite como máximo dos movimientos y cada movimiento puede estar asociado a una única operación financiera.

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
- OperacionFinancieraRepository

`MovimientoRepository` permite guardar, actualizar, buscar por ID, listar todos, listar por cuenta, listar por categoría y eliminar movimientos.

`OperacionFinancieraRepository` permite guardar, actualizar, buscar por ID, listar todas, listar por cuenta de origen y listar por cuenta de destino.

## Services

La capa `service` contiene actualmente:

- CuentaService
- MovimientoService
- CategoriaService
- PerfilFinancieroService
- UsuarioService
- InstitucionFinancieraService
- MonedaService
- OperacionFinancieraService

`MovimientoService` utiliza `EntityManager` y `MovimientoRepository`, mantiene transacciones explícitas y centraliza las reglas contextuales de cuenta, categoría y perfil financiero.

`OperacionFinancieraService` coordina la creación de la operación financiera y sus dos movimientos dentro de una única transacción y asocia ambos movimientos a la operación antes de persistirlos.

## Próximo paso

Definir el siguiente bloque funcional de `feature/operacion-financiera`.

Antes de implementar nuevas piezas, revisar el código actual, tests y reglas de negocio relacionados. No asumir estructuras ni comportamientos no presentes en el repositorio.

La asociación entre `Movimiento` y `OperacionFinanciera` ya está implementada y no debe figurar como pendiente. `OperacionFinancieraRepository` también está implementado y no debe figurar como pendiente.

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
