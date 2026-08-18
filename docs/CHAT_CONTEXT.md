# SOFP — Contexto para continuar con ChatGPT

Este archivo es el punto de entrada para continuar el proyecto SOFP en una nueva conversación. La fuente permanente de verdad es el repositorio, Git, la documentación y los tests; los chats son sesiones de trabajo.

## Cómo usarlo

Al iniciar una nueva conversación sobre SOFP, indicar:

> Vamos a continuar el proyecto SOFP. Usa `docs/CHAT_CONTEXT.md` como contexto principal y revisa también `docs/00_ESTADO_ACTUAL.md`.

Luego trabajar sobre el estado real del repositorio y no sobre recuerdos de conversaciones anteriores.

## Proyecto

SOFP — Sistema Operativo Financiero Personal.

Aplicación Java de finanzas personales, desarrollada progresivamente con dominio sólido, persistencia JPA, servicios transaccionales y tests automatizados.

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

El código del Build 033 fue publicado en `main` de GitHub y Bitbucket. Esta actualización de continuidad se registra en `docs/continuidad-sofp`.

## Estado actual — 18/08/2026

Último Build funcional confirmado: **Build 033 — Reglas de negocio de Movimiento**.

Último commit de código:

- `b18ca96` — `feat: agregar reglas de negocio a movimientos`

La batería general actual es de **144/144 tests en verde**, con:

- Failures: 0
- Errors: 0
- Skipped: 0
- BUILD SUCCESS

`MovimientoServiceTest` quedó con **18/18 tests en verde**.

Se ejecutó `git diff --check` sin errores antes del commit.

El commit `b18ca96` fue publicado mediante `git pushall` en:

- GitHub `main`: `7e2f4cf` → `b18ca96`
- Bitbucket `main`: `7e2f4cf` → `b18ca96`

## Build 033 — Reglas de negocio de Movimiento

### Objetivo

Incorporar reglas de negocio que aseguren la coherencia entre `Movimiento`, `Cuenta`, `Categoria` y `PerfilFinanciero`, evitando movimientos inconsistentes.

### Regla 033.1 — Cuenta y Categoría del mismo Perfil Financiero

Al registrar un movimiento, `Cuenta` y `Categoria` deben pertenecer al mismo `PerfilFinanciero`.

Se incorporó en `MovimientoService` el método privado:

- `validarPerfilFinanciero(Cuenta cuenta, Categoria categoria)`.

La validación compara los IDs de los perfiles financieros mediante `Objects.equals(...)` y lanza `IllegalArgumentException` cuando son diferentes.

`registrar(...)` ejecuta esta validación antes de crear el `Movimiento`.

Test incorporado:

- `deberiaRechazarMovimientoCuandoCuentaYCategoriaPertenecenADistintosPerfiles()`.

El test crea dos usuarios, dos perfiles, una cuenta del primer perfil y una categoría del segundo perfil y verifica que el registro sea rechazado.

### Regla 033.2 — No registrar movimientos en cuentas desactivadas

`MovimientoService.registrar(...)` ahora verifica `cuenta.isActiva()`.

Si la cuenta está desactivada, lanza:

`IllegalArgumentException("No se puede registrar un movimiento en una cuenta desactivada")`.

Test incorporado:

- `deberiaRechazarMovimientoCuandoLaCuentaEstaDesactivada()`.

El test utiliza `cuenta.desactivar()` y verifica que el registro sea rechazado.

### Regla 033.3 — No cambiar a una categoría de otro perfil

`MovimientoService.cambiarCategoria(...)` ahora valida que la nueva categoría pertenezca al mismo perfil financiero que la cuenta asociada al movimiento.

Se reutiliza `validarPerfilFinanciero(...)` con:

- `movimiento.getCuenta()`
- nueva `categoria`

Test incorporado:

- `deberiaRechazarCambioDeCategoriaCuandoPerteneceAOtroPerfil()`.

El test primero registra un movimiento válido con una categoría del mismo perfil y luego intenta cambiarlo a una categoría de otro perfil.

### Resultado

`MovimientoServiceTest`: **18/18 tests en verde**.

Batería general: **144/144 tests en verde**.

### Commit asociado

- `b18ca96` — `feat: agregar reglas de negocio a movimientos`

Publicado en GitHub y Bitbucket.

## Decisión de dominio del Build 033

Se decidió que estas tres reglas forman parte de las reglas de negocio de `Movimiento`:

1. Cuenta y Categoría deben pertenecer al mismo Perfil Financiero.
2. No se puede registrar un movimiento sobre una Cuenta desactivada.
3. No se puede cambiar la Categoría de un Movimiento por una categoría perteneciente a otro Perfil Financiero.

La validación que involucra varias entidades se mantiene en `MovimientoService`, no en el constructor de `Movimiento`, porque corresponde a una regla de negocio contextual y el servicio dispone del contexto necesario para comprobar las relaciones.

## Consideración de tests y H2

Los nuevos tests que necesitan una moneda ARS reutilizan la moneda existente mediante una consulta JPQL:

`SELECT m FROM Moneda m WHERE m.codigo = :codigo`

Esto evita intentar insertar repetidamente `ARS`, cuyo código tiene restricción de unicidad en `MONEDAS(CODIGO)`.

Este problema había aparecido como `ConstraintViolationException` de H2 por duplicación de `ARS` y quedó resuelto en los tests nuevos mediante la reutilización de la moneda existente.

## Builds recientes

- Build 023 — Ampliación de `CuentaService`.
- Build 024 — Ampliación de `MovimientoService`.
- Build 025 — Ampliación de `Movimiento` y nuevas operaciones de `MovimientoService`.
- Build 026 — Eliminación de `Movimiento` desde repository y service.
- Build 027 — Ampliación de `CuentaService` con modificaciones y activación/desactivación.
- Cobertura posterior al Build 027 — test específico de eliminación de `MovimientoService`.
- Build 028 — Ampliación de `CategoriaService`.
- Build 029 — Eliminación en `CategoriaRepository`.
- Build 030 — Eliminación en `CategoriaService`.
- Build 031 — Eliminación en `CuentaRepository`.
- Build 032 — Eliminación en `CuentaService`.
- Build 033 — Reglas de negocio de `Movimiento`.

## Builds 029–032

### Build 029 — Eliminación en CategoriaRepository

Se incorporó `CategoriaRepository.eliminar(Categoria categoria)`, comprobando gestión de la entidad con `EntityManager.contains(...)`, utilizando `merge(...)` cuando corresponde y ejecutando `remove(...)` sobre la instancia gestionada.

Resultado: **136/136 tests en verde**.

Commit: `46ad669` — `feat: completar eliminacion de CategoriaRepository`.

### Build 030 — Eliminación en CategoriaService

Se incorporó `CategoriaService.eliminar(Long categoriaId)` con validación, búsqueda, transacción explícita, `flush`, `commit` y `rollback`.

Se agregaron tests para eliminación existente e inexistente.

Resultado: **138/138 tests en verde**.

Commit: `59b9628` — `feat: completar eliminacion de CategoriaService`.

### Build 031 — Eliminación en CuentaRepository

Se incorporó `CuentaRepository.eliminar(Cuenta cuenta)` siguiendo el patrón JPA de los repositorios anteriores.

Resultado: **139/139 tests en verde**.

Commit: `40768d3` — `feat: completar eliminacion de CuentaRepository`.

### Build 032 — Eliminación en CuentaService

Se incorporó `CuentaService.eliminar(Long cuentaId)` con validación, búsqueda, transacción explícita, delegación a repository, `flush`, `commit` y `rollback`.

Se agregaron tests para eliminación existente e inexistente.

Resultado: **141/141 tests en verde**.

Commit: `a1a817d` — `feat: completar eliminacion de CuentaService`.

## Dominio actual

Entidades principales conocidas:

- Usuario
- PerfilFinanciero
- InstitucionFinanciera
- Moneda
- Cuenta
- Categoria
- Movimiento

Enumeraciones conocidas:

- TipoInstitucionFinanciera
- TipoMoneda
- TipoCuenta
- TipoMovimiento

La estructura Java actual utiliza el paquete `ar.com.agmilevecich.sofp.domain` para las entidades del dominio.

## Persistencia

Repositorios JPA conocidos:

- `UsuarioRepository`
- `PerfilFinancieroRepository`
- `InstitucionFinancieraRepository`
- `MonedaRepository`
- `CuentaRepository`
- `MovimientoRepository`
- `CategoriaRepository`

`CuentaRepository` permite guardar, buscar por ID, listar todas, listar por perfil financiero y eliminar cuentas.

`MovimientoRepository` proporciona guardar, actualizar, buscar por ID, listar todos, listar por cuenta, listar por categoría y eliminar movimientos.

`CategoriaRepository` proporciona guardar, buscar por ID, listar todas, listar por perfil financiero y eliminar categorías.

## Services

La capa `service` contiene actualmente:

- `CuentaService`
- `MovimientoService`
- `CategoriaService`
- `PerfilFinancieroService`
- `UsuarioService`
- `InstitucionFinancieraService`
- `MonedaService`

`MovimientoService` utiliza `EntityManager` y `MovimientoRepository` y mantiene transacciones explícitas para registro, modificaciones y eliminación. Desde Build 033 también centraliza la validación de coherencia entre cuenta, categoría y perfil financiero.

`CategoriaService` utiliza `EntityManager` y `CategoriaRepository` y mantiene el patrón transaccional para registro, modificaciones, activación/desactivación y eliminación.

`CuentaService` cubre registro, búsqueda, listados, saldo, modificaciones, activación/desactivación y eliminación.

## Movimiento

`Movimiento` representa un movimiento financiero asociado a una `Cuenta` y una `Categoria`.

Campos relevantes:

- cuenta
- categoria
- tipoMovimiento
- importe
- fechaHora
- descripcion
- observaciones

`TipoMovimiento` contiene `INGRESO` y `EGRESO`.

El importe se valida como positivo mediante `Validaciones.importePositivo`.

`Movimiento` permite modificar tipo de movimiento, importe, fecha/hora, descripción, observaciones y categoría.

## Tests

La batería general actual está en **144/144 tests en verde**.

La infraestructura de pruebas utiliza `JpaTestManager`, H2 en memoria y `create-drop`. En los tests de servicios se cierra `JpaTestManager` en `tearDown()` para garantizar el aislamiento de la base entre tests.

## Próximo paso

El Build 033 está cerrado y publicado. Antes de implementar el siguiente Build, revisar los casos de uso pendientes del dominio y definir la siguiente pieza funcional de forma incremental.

No comenzar otro bloque de código hasta definir claramente su objetivo y los tests que deben cubrirlo.

## Forma de trabajo acordada

Trabajar por Builds pequeños y verificables:

1. Explicar qué vamos a construir.
2. Implementar una pieza concreta.
3. Ejecutar los tests correspondientes.
4. Confirmar que quedan en verde.
5. Revisar que no se rompa lo anterior.
6. Registrar el Build.
7. Hacer commit.
8. Actualizar documentación.
9. Definir el siguiente paso.

No avanzar acumulando cambios sin verificar.

## Regla permanente sobre la fuente de verdad

La conversación de ChatGPT NO es la fuente permanente de verdad del proyecto.

La fuente permanente es:

1. Código del repositorio.
2. Git / historial de commits.
3. Documentación de `docs/`.
4. Tests automatizados.

Los chats son sesiones de trabajo que consultan y actualizan esa fuente.

## Al comenzar una nueva sesión

Primero comprobar:

- Estado actual.
- Último commit.
- Último Build.
- Tests existentes.
- Decisiones importantes.
- Pendientes.

Después continuar desde ahí.

## Al cerrar una sesión

Actualizar como mínimo:

- `docs/00_ESTADO_ACTUAL.md`
- `docs/06_BUILDS.md`
- `docs/07_TESTS.md`
- `docs/08_PENDIENTES.md`
- `docs/09_HISTORIAL_PROYECTO.md`
- `docs/CHAT_CONTEXT.md`

Si hubo una decisión arquitectónica o de negocio importante, actualizar también `docs/05_DECISIONES.md`.
