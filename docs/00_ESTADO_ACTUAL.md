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

El proyecto está organizado alrededor del dominio y capas previstas para evolución posterior. Se utilizan paquetes como `domain`, `config`, `util`, `persistence`, `service`, `dto`, `enums`, `app` y `ui`.

La persistencia de producción utiliza la unidad `sofp-persistence-unit` y una base H2 en archivo.

Para pruebas JPA existe una infraestructura separada mediante `JpaTestManager` y la unidad `sofp-persistence-unit-test`, utilizando H2 en memoria y `create-drop`.

## Estado funcional actual

El último bloque trabajado es **Build 027 — Ampliación de CuentaService**.

Se amplió `CuentaService` para completar las operaciones de modificación y activación/desactivación de cuentas, manteniendo el patrón transaccional utilizado por los servicios del proyecto.

En `CuentaService` se incorporaron:

- `modificarNombre(Long cuentaId, String nombre)`.
- `modificarIdentificadorExterno(Long cuentaId, String identificadorExterno)`.
- `modificarTipoCuenta(Long cuentaId, TipoCuenta tipoCuenta)`.
- `modificarInstitucionFinanciera(Long cuentaId, InstitucionFinanciera institucionFinanciera)`.
- `modificarMoneda(Long cuentaId, Moneda moneda)`.
- `activar(Long cuentaId)`.
- `desactivar(Long cuentaId)`.

El servicio recibe `EntityManager` por constructor para gestionar explícitamente las transacciones. Las operaciones validan los identificadores y los parámetros obligatorios, buscan la cuenta y ejecutan `begin`, modificación, `flush`, `commit` y `rollback()` ante excepciones.

También se incorporó la validación de cuenta inexistente mediante `IllegalArgumentException` en el método privado `obtenerCuenta(...)`.

`CuentaServiceTest` fue ampliado con casos para las siete operaciones nuevas. La batería general del proyecto quedó en **128/128 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.

Antes de registrar el bloque se verificó además `git diff --check`, sin salida ni errores de formato.

El código de este Build todavía debe quedar asociado a su commit de código en `main`; la documentación de continuidad se actualiza en esta rama de documentación.

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

`CuentaRepository` proporciona guardar, buscar por ID, listar todas y listar por perfil financiero.

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

`CuentaService` recibe `MovimientoRepository` y `EntityManager` por constructor y proporciona:

- `registrar(Cuenta cuenta)`.
- `buscarPorId(Long id)`.
- `listarTodas()`.
- `listarPorPerfilFinanciero(Long perfilFinancieroId)`.
- `calcularSaldo(Long cuentaId)`.
- `modificarNombre(...)`.
- `modificarIdentificadorExterno(...)`.
- `modificarTipoCuenta(...)`.
- `modificarInstitucionFinanciera(...)`.
- `modificarMoneda(...)`.
- `activar(...)`.
- `desactivar(...)`.

Reglas actuales del cálculo:

- `INGRESO` suma.
- `EGRESO` resta.
- Sin movimientos → `BigDecimal.ZERO`.
- Se procesan múltiples movimientos de una cuenta.

`MovimientoService` recibe `EntityManager` y `MovimientoRepository` por constructor y proporciona las operaciones de registro, búsqueda, listado, modificación y eliminación de movimientos.

El registro, las modificaciones y la eliminación de movimientos utilizan transacciones explícitas, `flush()` antes del `commit` y `rollback()` ante excepciones.

## Tests

La batería general confirmada al cerrar el **Build 027** es de **128/128 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.

## Próximo paso

Definir el **Build 028** a partir del estado real del código, la capa `service`, los repositorios disponibles y los casos de uso que todavía deban incorporarse.

No avanzar directamente a implementar el Build 028 sin definir primero qué pieza funcional se va a construir, qué comportamiento debe tener y qué tests deberán cubrirlo.

## Regla de continuidad

Cada bloque importante debe terminar con código funcionando, tests en verde, commit identificable, actualización de documentación y registro del próximo paso.
