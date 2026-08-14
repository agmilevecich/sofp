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

Último Build confirmado: **Build 026 — Eliminación de Movimiento**.

Último commit de código confirmado: `d386d02`.

Mensaje: `feat: completar operaciones de Movimiento`.

El commit fue publicado en `main` de GitHub y Bitbucket.

La batería general confirmada es de **121/121 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.

Commits de código recientes:

- `d386d02` — `feat: completar operaciones de Movimiento`.
- `81883ea` — `feat: completar operaciones de MovimientoService`.
- `da3b89d` — `feat: ampliar operaciones de Movimiento`.
- `110f7d7` — `feat: ampliar MovimientoService`.
- `ea595d4` — `feat: ampliar CuentaService`.
- `0d0db87` — `feat: implementar MonedaService`.

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

Cada repositorio tiene su test correspondiente.

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

`CuentaService` recibe `MovimientoRepository` por constructor y proporciona `calcularSaldo(Long cuentaId)`.

Reglas actuales del cálculo:

- `INGRESO` suma.
- `EGRESO` resta.
- Sin movimientos → `BigDecimal.ZERO`.
- Se procesan múltiples movimientos de una cuenta.

`MovimientoService` recibe `EntityManager` y `MovimientoRepository` por constructor y proporciona:

- `registrar(...)`.
- `buscarPorId(Long id)`.
- `listarTodos()`.
- `listarPorCuenta(Long cuentaId)`.
- `listarPorCategoria(Long categoriaId)`.
- `modificarDescripcion(Long movimientoId, String descripcion)`.
- `modificarObservaciones(Long movimientoId, String observaciones)`.
- `cambiarCategoria(Long movimientoId, Categoria categoria)`.
- `modificarTipoMovimiento(Long movimientoId, TipoMovimiento tipoMovimiento)`.
- `modificarImporte(Long movimientoId, BigDecimal importe)`.
- `modificarFechaHora(Long movimientoId, LocalDateTime fechaHora)`.
- `eliminar(Long movimientoId)`.

Las operaciones de registro, modificación y eliminación utilizan transacciones explícitas, `flush()` antes del `commit` y `rollback()` ante excepciones.

`CategoriaService` recibe `CategoriaRepository` por constructor y proporciona:

- `registrar(Categoria categoria)`.
- `buscarPorId(Long id)`.
- `listarTodas()`.
- `listarPorPerfilFinanciero(Long perfilFinancieroId)`.

`PerfilFinancieroService` recibe `PerfilFinancieroRepository` por constructor y proporciona:

- `guardar(PerfilFinanciero perfil)`.
- `buscarPorId(Long id)`.
- `listarTodos()`.
- `listarPorUsuario(Long usuarioId)`.
- `cambiarDescripcion(Long perfilId, String descripcion)`.
- `activar(Long perfilId)`.
- `desactivar(Long perfilId)`.

Tests asociados:

- `CuentaServiceTest`
- `MovimientoServiceTest`
- `CategoriaServiceTest`
- `PerfilFinancieroServiceTest`
- `UsuarioServiceTest`
- `InstitucionFinancieraServiceTest`
- `MonedaServiceTest`

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

La batería general del proyecto quedó en **121/121 tests en verde** al cerrar el Build 026.

La infraestructura de pruebas utiliza `JpaTestManager`, H2 en memoria y `create-drop`. En los tests de servicios se cierra `JpaTestManager` en el `tearDown()` para garantizar el aislamiento de la base entre tests.

## Builds recientes

- Build 023 — Ampliación de `CuentaService`.
- Build 024 — Ampliación inicial de `MovimientoService`.
- Build 025 — Ampliación de `Movimiento` y nuevas operaciones de `MovimientoService`.
- Build 026 — Eliminación de `Movimiento` desde `MovimientoRepository` y `MovimientoService`.

## Próximo paso

Definir el **Build 027** antes de implementar código nuevo, considerando el estado actual del dominio, los repositorios y servicios disponibles y los casos de uso pendientes.

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
