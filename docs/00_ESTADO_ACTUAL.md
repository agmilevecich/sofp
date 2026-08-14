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

En `MovimientoRepository` se incorporó `eliminar(Movimiento movimiento)`, garantizando que la entidad esté gestionada antes de ejecutar `remove()`.

En `MovimientoService` se incorporó `eliminar(Long movimientoId)`, validando el ID, verificando la existencia del movimiento y ejecutando la eliminación dentro de una transacción explícita con `flush()`, `commit()` y `rollback()` ante excepciones.

También se consolidó el nombre `modificarTipoMovimiento(...)` en la entidad `Movimiento` para mantener coherencia con el servicio.

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

## Tests

La batería general confirmada al cerrar el Build 026 es de **121/121 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.

## Próximo paso

Definir el **Build 027** a partir del estado real del código, la capa `service`, los repositorios disponibles y los casos de uso que todavía deban incorporarse.

No avanzar directamente a implementar el Build 027 sin definir primero qué pieza funcional se va a construir, qué comportamiento debe tener y qué tests deberán cubrirlo.

## Regla de continuidad

Cada bloque importante debe terminar con código funcionando, tests en verde, commit identificable, actualización de documentación y registro del próximo paso.
