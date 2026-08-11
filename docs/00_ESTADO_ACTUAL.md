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

El último bloque trabajado es **Build 015 — Servicio de saldo de cuentas**.

Se incorporó la primera pieza de la capa de servicios para calcular el saldo de una cuenta a partir de sus movimientos.

Servicio incorporado:

- `CuentaService`

El servicio recibe `MovimientoRepository` por constructor y proporciona:

- `calcularSaldo(Long cuentaId)`.
- `INGRESO` suma al saldo.
- `EGRESO` resta del saldo.
- Una cuenta sin movimientos devuelve `BigDecimal.ZERO`.
- El cálculo soporta múltiples movimientos de la misma cuenta.

Test incorporado:

- `CuentaServiceTest`

El test verifica:

- Cuenta sin movimientos → saldo `0.00`.
- Un ingreso de `10,000.00` → saldo `10,000.00`.
- Un egreso de `3,000.00` → saldo `-3,000.00`.
- Múltiples movimientos (`10,000 + 5,000 - 3,000`) → saldo `12,000.00`.

Durante la implementación se ajustó el `tearDown()` del test para cerrar `JpaTestManager` después de cada prueba y mantener aislada la base H2 en memoria entre tests.

La batería general del proyecto quedó en **68 tests en verde**.

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

La capa `service` ya inició con:

- `CuentaService`

Cada bloque incorporado cuenta con tests correspondientes.

## Últimos Builds / hitos conocidos

- Build 005 — Diseño de la entidad `Cuenta`.
- Build 009.1 — Implementación de `Categoria`.
- Build 010 — Implementación de `Movimiento`.
- Build 011 — Aislamiento y estabilización de tests JPA con H2.
- Build 012 — Repositories JPA de entidades base.
- Build 013 — Repository JPA de `Cuenta`.
- Build 014 — Repository JPA de `Movimiento`.
- Build 015 — Servicio de saldo de cuentas.

## Commits recientes de código

- `4697815` — `feat: implementar servicio de saldo de cuentas`.
- `4f0b20f` — `Build 014 - Implementación de MovimientoRepository`.
- `140d3eb` — `Build 013 - Implementación de CuentaRepository`.
- `9e1a9c3` — `feat(persistence): agregar repositories de Usuario y PerfilFinanciero`.
- `5a3ebfb` — `Build: agrega repositorios de InstitucionFinanciera y Moneda`.

El commit `4697815` fue publicado en `main` de GitHub y Bitbucket.

## Tests

El flujo de desarrollo utiliza tests unitarios y tests JPA. El criterio de avance acordado es que los tests correspondientes al bloque estén en verde antes de considerar cerrado el Build.

En el Build 015 se verificó:

- `CuentaServiceTest` con 4 casos en verde.
- Batería general del proyecto: **68/68 tests en verde**.

## Próximo paso

Definir el **Build 016** a partir del estado real del código, la nueva capa `service`, los repositorios disponibles y los tests existentes.

No avanzar directamente a implementar el Build 016 sin definir primero:

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