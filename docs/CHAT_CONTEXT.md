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
- Rama principal del repositorio: `main`
- Rama única de funcionalidad y continuidad actual: `feature/operacion-financiera`
- Rama `docs/continuidad-sofp`: **eliminada**.

Todo el código, tests y documentación de continuidad actual se mantiene dentro de `feature/operacion-financiera`.

## Estado actual — 25/08/2026

Último Build cerrado: **Build 050 — Ampliación de cobertura de OperacionFinancieraService**.

Último commit funcional:

- `0f64fa9` — `feat: asociar movimientos a operacion financiera`

Último commit de trabajo:

- `1f306e4` — `test: ampliar cobertura de OperacionFinancieraService`

Build 050 agregó dos pruebas específicas al servicio: asociación de ambos movimientos a la misma operación financiera y rechazo de la misma cuenta como origen y destino con ausencia de persistencia de movimientos.

La rama `feature/operacion-financiera` está sincronizada entre GitHub y Bitbucket. `main` permanece separado.

## Tests

La última batería general confirmada es de **328/328 tests en verde**.

Conteo confirmado de tests de services:

- `CategoriaServiceTest`: **21**
- `CuentaServiceTest`: **47**
- `InstitucionFinancieraServiceTest`: **26**
- `MonedaServiceTest`: **17**
- `MovimientoServiceTest`: **50**
- `PerfilFinancieroServiceTest`: **13**
- `UsuarioServiceTest`: **15**
- `OperacionFinancieraServiceTest`: **22**

Total confirmado: **211 tests de services**.

Tests de dominio destacados:

- `MovimientoTest`: **27**
- `OperacionFinancieraTest`: **14**

Tests de persistencia destacados:

- `OperacionFinancieraRepositoryTest`: **10**

Última suite general confirmada desde IntelliJ:

- Tests run: **328**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Finalizada: **25/08/2026 10:47:02 -03:00**
- Duración: **12:04 min**

## Decisión de dominio: transferencias

Las transferencias no se modelan como un tercer `TipoMovimiento`.

Conceptualmente una transferencia genera un **EGRESO en la cuenta origen** y un **INGRESO en la cuenta destino**. Ambos efectos quedan vinculados mediante una `OperacionFinanciera` que representa la operación de transferencia.

La relación entre la operación y sus movimientos ya está implementada y persistida. Una operación admite como máximo dos movimientos y cada movimiento puede estar asociado a una única operación financiera.

La misma cuenta no puede utilizarse como origen y destino; esta regla pertenece al dominio y está cubierta por los tests del servicio.

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

`OperacionFinancieraService` coordina la creación de la operación financiera y sus dos movimientos dentro de una única transacción y asocia ambos movimientos a la operación antes de persistirlos.

## Próximo paso

Definir el siguiente bloque funcional de `feature/operacion-financiera`.

Antes de implementar nuevas piezas, revisar el código actual, tests y reglas de negocio relacionados. No asumir estructuras ni comportamientos no presentes en el repositorio.

La funcionalidad de `OperacionFinanciera`, su repositorio, su servicio y su asociación con `Movimiento` ya están implementados y validados. No deben figurar como pendientes de implementación.

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

Si hubo una decisión arquitectónica o de negocio importante, actualizar la documentación correspondiente de arquitectura.
