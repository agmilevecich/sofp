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

Las ramas auxiliares creadas accidentalmente durante la implementación de `Bono` fueron eliminadas de los remotos. No forman parte del flujo del proyecto.

## Estado actual — 25/08/2026

Último Build cerrado: **Build 052 — Incorporación de Bono como especialización de Activo**.

`Bono` fue incorporado como primera especialización de `Activo`, deliberadamente mínima y sin atributos financieros específicos adicionales.

`Bono` hereda `nombre` y `Moneda` desde `Activo`.

Se incorporó `BonoRepository` y su cobertura JPA.

Commits del bloque:

- `589acf1` — `feat: incorporar entidad Bono`
- `c11f1f4` — `test: agregar cobertura de Bono`
- `6925447` — `feat: incorporar repositorio de Bono`
- `74b8fff` — `test: agregar cobertura de BonoRepository`
- `678c6ea` — `test: registrar Bono en persistencia JPA`

La persistencia de `Bono` quedó validada mediante el registro explícito de la entidad en la configuración utilizada por los tests.

La rama `feature/operacion-financiera` está destinada a contener todo el código, tests y documentación de continuidad. `main` permanece separado.

## Tests

La última batería general confirmada es de **353/353 tests en verde**.

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
- `ActivoTest`: **8**
- `BonoTest`: **5**

Tests de persistencia destacados:

- `OperacionFinancieraRepositoryTest`: **10**
- `ActivoRepositoryTest`: **6**
- `BonoRepositoryTest`: **6**

Última suite general confirmada:

- Tests run: **353**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Finalizada: **25/08/2026 20:14:39 -03:00**
- Duración: **16:07 min**

## Decisión de dominio: transferencias

Las transferencias no se modelan como un tercer `TipoMovimiento`.

Conceptualmente una transferencia genera un **EGRESO en la cuenta origen** y un **INGRESO en la cuenta destino**. Ambos efectos quedan vinculados mediante una `OperacionFinanciera` que representa la operación de transferencia.

La relación entre la operación y sus movimientos ya está implementada y persistida. Una operación admite como máximo dos movimientos y cada movimiento puede estar asociado a una única operación financiera.

La misma cuenta no puede utilizarse como origen y destino; esta regla pertenece al dominio y está cubierta por los tests del servicio.

## Decisión de dominio: Bono

`Bono` es la primera especialización de `Activo` y se mantiene deliberadamente mínima.

Actualmente no define atributos financieros propios. No se incorporan valor nominal, tasa, vencimiento, cupón, amortización, emisor u otros datos específicos hasta que esas reglas sean definidas explícitamente en el dominio.

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
- Activo
- Bono

`Activo` se mantiene deliberadamente como una entidad mínima. Cantidad, precio, cotización y posición se reservarán para los bloques posteriores de inversiones.

`Bono` hereda de `Activo` y actualmente no agrega atributos financieros propios.

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
- ActivoRepository
- BonoRepository

`MovimientoRepository` permite guardar, actualizar, buscar por ID, listar todos, listar por cuenta, listar por categoría y eliminar movimientos.

`OperacionFinancieraRepository` permite guardar, actualizar, buscar por ID, listar todas, listar por cuenta de origen y listar por cuenta de destino.

`ActivoRepository` permite guardar, actualizar, buscar por ID y listar activos. Su persistencia y asociación obligatoria con `Moneda` están validadas por **6/6 tests**.

`BonoRepository` permite guardar, actualizar, buscar por ID y listar bonos. Su persistencia y herencia desde `Activo` están validadas por **6/6 tests**.

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

Definir la siguiente evolución del bloque de inversiones a partir de reglas de negocio explícitas.

En particular, antes de agregar atributos financieros a `Bono`, definir qué información y comportamientos necesita representar SOFP para los bonos.

Antes de implementar nuevas piezas, revisar el código actual, tests y reglas de negocio relacionados. No asumir estructuras ni comportamientos no presentes en el repositorio.

La funcionalidad de `OperacionFinanciera`, su repositorio, su servicio y su asociación con `Movimiento` ya están implementados y validados. `Activo`, `ActivoRepository`, `Bono` y `BonoRepository` también están implementados y validados.

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
