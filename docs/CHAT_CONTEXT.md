# SOFP — Contexto para continuar con ChatGPT

Este archivo está pensado como punto de entrada para cualquier nueva conversación sobre SOFP.

## Cómo usarlo

Al iniciar una nueva conversación, indicar:

> Vamos a continuar el proyecto SOFP. Usa `docs/CHAT_CONTEXT.md` como contexto principal y revisa también `docs/00_ESTADO_ACTUAL.md`.

Luego trabajar sobre el estado real del repositorio y no sobre recuerdos de conversaciones anteriores.

## Proyecto

SOFP — Sistema Operativo Financiero Personal.

Es una aplicación Java de finanzas personales. El objetivo es construirla progresivamente, manteniendo un dominio sólido, persistencia JPA y una arquitectura preparada para crecer.

## Tecnologías

- Java / JDK 23.0.1
- IntelliJ IDEA Community 2026.1.1
- Maven
- Jakarta Persistence / JPA
- Hibernate 6.6.18.Final
- H2 2.3.232
- JUnit 5.11.4
- BigDecimal para dinero

## Repositorio

GitHub: `agmilevecich/sofp`

Rama principal: `main`

Rama de documentación/continuidad: `docs/continuidad-sofp`

## Estado de referencia

Último Build funcional confirmado: **Build 028 — Ampliación de CategoriaService**.

La batería general confirmada actualmente es de **135/135 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.

El commit de código del Build 028 es `b5c200e` — `feat: ampliar CategoriaService`.

## Build 027

`CuentaService` fue ampliado con:

- `registrar(Cuenta cuenta)`.
- `buscarPorId(Long id)`.
- `listarTodas()`.
- `listarPorPerfilFinanciero(Long perfilFinancieroId)`.
- `calcularSaldo(Long cuentaId)`.
- `modificarNombre(Long cuentaId, String nombre)`.
- `modificarIdentificadorExterno(Long cuentaId, String identificadorExterno)`.
- `modificarTipoCuenta(Long cuentaId, TipoCuenta tipoCuenta)`.
- `modificarInstitucionFinanciera(Long cuentaId, InstitucionFinanciera institucionFinanciera)`.
- `modificarMoneda(Long cuentaId, Moneda moneda)`.
- `activar(Long cuentaId)`.
- `desactivar(Long cuentaId)`.

El constructor recibe `CuentaRepository`, `MovimientoRepository` y `EntityManager`.

Las operaciones de modificación y activación/desactivación validan los parámetros obligatorios, buscan la cuenta, ejecutan transacciones explícitas y utilizan `flush()` antes del `commit`, con `rollback()` ante excepciones.

`obtenerCuenta(Long cuentaId)` centraliza la búsqueda y lanza `IllegalArgumentException` si la cuenta no existe.

`CuentaServiceTest` incorporó siete casos nuevos para las operaciones de modificación y estado.

Se verificó además `git diff --check`, sin errores.

## Cobertura posterior al Build 027

Se agregó un caso en `MovimientoServiceTest` para cubrir explícitamente la eliminación de un movimiento mediante `MovimientoService.eliminar(...)`.

El test registra un movimiento, guarda su ID, ejecuta la eliminación y verifica mediante `buscarPorId(...)` que el movimiento ya no exista.

`MovimientoServiceTest` pasó de 15 a **16 tests**, todos en verde en la ejecución individual.

La batería general pasó de 128 a **129/129 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.

Commit: `3e93be2` — `test: cubrir eliminacion de MovimientoService`.

## Build 028 — Ampliación de CategoriaService

`CategoriaService` fue ampliado para completar la gestión de categorías desde la capa de servicio.

Se incorporaron operaciones de modificación y activación/desactivación, con validación de identificadores y existencia de la categoría.

El servicio utiliza `CategoriaRepository` y `EntityManager`, manteniendo el patrón de transacciones explícitas utilizado en los servicios anteriores.

`CategoriaServiceTest` fue ampliado para cubrir las nuevas operaciones y comprobar los cambios persistidos.

Resultado de la batería general: **135/135 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.

Se ejecutó `git diff --check`, sin errores.

Commit: `b5c200e` — `feat: ampliar CategoriaService`.

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

## Persistencia

Se incorporaron repositorios JPA para:

- `UsuarioRepository`
- `PerfilFinancieroRepository`
- `InstitucionFinancieraRepository`
- `MonedaRepository`
- `CuentaRepository`
- `MovimientoRepository`
- `CategoriaRepository`

`CuentaRepository` permite guardar, buscar por ID, listar todas y listar por perfil financiero.

`MovimientoRepository` proporciona guardar, actualizar, buscar por ID, listar todos, listar por cuenta, listar por categoría y eliminar movimientos.

## Service

La capa `service` contiene actualmente:

- `CuentaService`
- `MovimientoService`
- `CategoriaService`
- `PerfilFinancieroService`
- `UsuarioService`
- `InstitucionFinancieraService`
- `MonedaService`

`MovimientoService` utiliza `EntityManager` y `MovimientoRepository` y mantiene transacciones explícitas para registro, modificaciones y eliminación.

`CategoriaService` utiliza `EntityManager` y `CategoriaRepository` y mantiene el mismo patrón transaccional para las nuevas operaciones de modificación y estado.

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

La batería general del proyecto está en **135/135 tests en verde**.

La infraestructura de pruebas utiliza `JpaTestManager`, H2 en memoria y `create-drop`. En los tests de servicios se cierra `JpaTestManager` en el `tearDown()` para garantizar el aislamiento de la base entre tests.

## Builds recientes

- Build 023 — Ampliación de `CuentaService`.
- Build 024 — Ampliación inicial de `MovimientoService`.
- Build 025 — Ampliación de `Movimiento` y nuevas operaciones de `MovimientoService`.
- Build 026 — Eliminación de `Movimiento` desde `MovimientoRepository` y `MovimientoService`.
- Build 027 — Ampliación de `CuentaService` con modificaciones y activación/desactivación.
- Cobertura posterior al Build 027 — test específico de eliminación de `MovimientoService`.
- Build 028 — Ampliación de `CategoriaService`.

## Próximo paso

Definir el siguiente bloque funcional antes de implementar código nuevo, considerando el estado actual del dominio, los repositorios y servicios disponibles y los casos de uso pendientes.

## Forma de trabajo acordada

Trabajar por Builds pequeños y verificables.

Para cada Build:

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

## Regla importante sobre la memoria

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
