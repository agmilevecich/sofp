# SOFP — Estado actual

> Documento de continuidad del proyecto. Debe actualizarse al cerrar cada bloque importante de trabajo.

## Identidad del proyecto

**Nombre:** SOFP — Sistema Operativo Financiero Personal
**Repositorio:** agmilevecich/sofp
**Rama principal:** `main`
**Rama de trabajo actual para esta documentación:** `docs/continuidad-sofp`

## Objetivo

Construir una aplicación Java de finanzas personales, preparada para múltiples usuarios, con persistencia JPA/Hibernate, base H2 y una interfaz que se incorporará progresivamente.

## Stack conocido

- Java / JDK 23.0.1
- IntelliJ IDEA Community 2026.1.1
- Maven
- JPA / Jakarta Persistence
- Hibernate 6.6.18.Final
- H2 2.3.232
- JUnit 5.11.4
- BigDecimal para importes monetarios

## Arquitectura conocida

El proyecto está organizado alrededor del dominio y capas previstas para evolución posterior. Se han utilizado paquetes como `domain`, `config`, `util`, `persistence`, `service`, `dto`, `enums`, `app` y `ui`.

La persistencia de producción utiliza la unidad `sofp-persistence-unit` y una base H2 en archivo.

Para pruebas JPA existe una infraestructura separada mediante `JpaTestManager` y la unidad `sofp-persistence-unit-test`, utilizando H2 en memoria y `create-drop`.

## Estado funcional actual

El último bloque trabajado es **Build 026 — Eliminación de movimientos**.

Se completó la gestión de `Movimiento` incorporando la eliminación desde las capas de persistencia y servicio, manteniendo el patrón transaccional y las validaciones existentes.

En `Movimiento` se mantiene la operación:

- `modificarTipoMovimiento(TipoMovimiento tipoMovimiento)`.
- `cambiarImporte(BigDecimal importe)`.
- `cambiarFechaHora(LocalDateTime fechaHora)`.
- `cambiarDescripcion(String descripcion)`.
- `cambiarObservaciones(String observaciones)`.
- `cambiarCategoria(Categoria categoria)`.

En `MovimientoRepository` se incorporó:

- `eliminar(Movimiento movimiento)`.

El repositorio garantiza que la entidad esté gestionada antes de ejecutar `remove()`.

En `MovimientoService` se incorporó:

- `eliminar(Long movimientoId)`.

La eliminación valida el ID, verifica que el movimiento exista, inicia una transacción, delega la eliminación al repositorio, ejecuta `flush()`, confirma con `commit()` y realiza `rollback()` ante excepciones.

## Dominio construido hasta ahora

Entre las entidades/enumeraciones ya trabajadas se encuentran:

- `Usuario`
- `PerfilFinanciero`
- `InstitucionFinanciera`
- `Moneda`
- `Cuenta`
- `Categoria`
- `Movimiento`
- `TipoInstitucionFinanciera`
- `TipoMoneda`
- `TipoCuenta`
- `TipoMovimiento`

También existe infraestructura de auditoría y utilidades de validación.

## Persistencia actual

Repositorios JPA incorporados:

- `UsuarioRepository`
- `PerfilFinancieroRepository`
- `InstitucionFinancieraRepository`
- `MonedaRepository`
- `CuentaRepository`
- `MovimientoRepository`
- `CategoriaRepository`

`MovimientoRepository` proporciona guardar, actualizar, buscar por ID, listar todos, listar por cuenta, listar por categoría y eliminar movimientos.

## Service

La capa `service` actualmente contiene:

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

El registro, las modificaciones y la eliminación de movimientos utilizan transacciones explícitas, `flush()` antes del `commit` y `rollback()` ante excepciones.

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

## Últimos Builds / hitos conocidos

- Build 005 — Diseño de la entidad `Cuenta`.
- Build 009.1 — Implementación de `Categoria`.
- Build 010 — Implementación de `Movimiento`.
- Build 011 — Aislamiento y estabilización de tests JPA con H2.
- Build 012 — Repository JPA de entidades base.
- Build 013 — Repository JPA de `Cuenta`.
- Build 014 — Repository JPA de `Movimiento`.
- Build 015 — Servicio de saldo de cuentas.
- Build 016 — Servicio de movimientos.
- Build 017 — Repository JPA de `Categoria`.
- Build 018 — Servicio de `Categoria`.
- Build 019 — Servicio de `PerfilFinanciero`.
- Build 020 — Servicio de `Usuario`.
- Build 021 — Servicio de `InstitucionFinanciera`.
- Build 022 — Servicio de `Moneda`.
- Build 023 — Ampliación de `CuentaService`.
- Build 024 — Ampliación inicial de `MovimientoService`.
- Build 025 — Ampliación de `Movimiento` y nuevas operaciones de `MovimientoService`.
- Build 026 — Eliminación de `Movimiento` desde repositorio y servicio.

## Commits recientes de código

- `d386d02` — `feat: completar operaciones de Movimiento`.
- `81883ea` — `feat: completar operaciones de MovimientoService`.
- `da3b89d` — `feat: ampliar operaciones de Movimiento`.
- `110f7d7` — `feat: ampliar MovimientoService`.
- `ea595d4` — `feat: ampliar CuentaService`.
- `0d0db87` — `feat: implementar MonedaService`.

Los commits recientes fueron publicados en `main` de GitHub y Bitbucket.

## Tests

El flujo de desarrollo utiliza tests unitarios y tests JPA. El criterio de avance acordado es que los tests correspondientes al bloque estén en verde antes de considerar cerrado el Build.

En el Build 026 se verificó nuevamente la batería general del proyecto:

- **121/121 tests en verde**.
- `Failures: 0`.
- `Errors: 0`.
- `Skipped: 0`.

No se registran incidencias pendientes para este bloque.

## Próximo paso

Definir el **Build 027** a partir del estado real del código, la capa `service`, los repositorios disponibles y los casos de uso que todavía deban incorporarse.

No avanzar directamente a implementar el Build 027 sin definir primero:

1. Qué pieza funcional se va a construir.
2. Qué comportamiento debe tener.
3. Qué tests deberán cubrirlo.

## Regla de continuidad

Cada bloque importante debe terminar con:

1. Código funcionando.
2. Tests en verde.
3. Commit identificable.
4. Actualización de documentación.
5. Registro del próximo paso.
