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

El último bloque trabajado es **Build 019 — Servicio de PerfilFinanciero**.

Se completó la capa `service` de `PerfilFinanciero` con:

- `PerfilFinancieroService`
- `PerfilFinancieroServiceTest`

`PerfilFinancieroService` recibe `PerfilFinancieroRepository` por constructor y proporciona:

- Guardado de perfiles financieros.
- Búsqueda por ID.
- Listado general.
- Listado por usuario.
- Cambio de descripción.
- Activación.
- Desactivación.

El bloque fue validado mediante sus tests correspondientes y mediante la batería general del proyecto.

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

## Service

La capa `service` actualmente contiene:

- `CuentaService`
- `MovimientoService`
- `CategoriaService`
- `PerfilFinancieroService`

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

El registro de movimientos utiliza una transacción explícita, `flush()` antes del `commit` y `rollback()` ante excepciones.

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
- Build 012 — Repositories JPA de entidades base.
- Build 013 — Repository JPA de `Cuenta`.
- Build 014 — Repository JPA de `Movimiento`.
- Build 015 — Servicio de saldo de cuentas.
- Build 016 — Servicio de movimientos.
- Build 017 — Repository JPA de `Categoria`.
- Build 018 — Servicio de `Categoria`.
- Build 019 — Servicio de `PerfilFinanciero`.

## Commits recientes de código

- `1cc00ca` — `feat: implementar PerfilFinancieroService`.
- `d57e0b4` — `feat: implementar CategoriaService`.
- `f462b3b` — `feat: implementar CategoriaRepository`.
- `8f8594e` — `feat: implementar servicio de movimientos`.
- `4697815` — `feat: implementar servicio de saldo de cuentas`.
- `4f0b20f` — `Build 014 - Implementación de MovimientoRepository`.

El commit `1cc00ca` fue publicado en `main` de GitHub y Bitbucket.

## Tests

El flujo de desarrollo utiliza tests unitarios y tests JPA. El criterio de avance acordado es que los tests correspondientes al bloque estén en verde antes de considerar cerrado el Build.

En el Build 019 se verificó:

- `PerfilFinancieroServiceTest` con sus 6 casos en verde.
- La batería general del proyecto terminó con **88/88 tests en verde**.
- No se registran incidencias pendientes para este bloque.

## Próximo paso

Definir el **Build 020** a partir del estado real del código, la capa `service`, los repositorios disponibles y los casos de uso que todavía deban incorporarse.

El siguiente candidato funcional es `UsuarioService`, para completar progresivamente la capa de servicios alrededor de las entidades principales.

No avanzar directamente a implementar el Build 020 sin definir primero:

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
