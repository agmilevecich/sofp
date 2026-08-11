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

El último bloque trabajado es **Build 014 — Repository JPA de Movimiento**.

Se incorporó y verificó la capa de persistencia para la entidad `Movimiento`.

Repositorio incorporado:

- `MovimientoRepository`

Test incorporado:

- `MovimientoRepositoryTest`

El repositorio permite:

- Guardar movimientos nuevos.
- Actualizar movimientos existentes.
- Buscar movimientos por ID.
- Listar todos los movimientos.
- Listar movimientos por `Cuenta`.
- Listar movimientos por `Categoria`.

El test verifica la integración de `Movimiento` con:

- `Usuario`
- `PerfilFinanciero`
- `InstitucionFinanciera`
- `Moneda`
- `Cuenta`
- `Categoria`

Durante la implementación también se ajustó `JpaTestManager` para mantener el aislamiento de las pruebas JPA con H2 en memoria.

El test `MovimientoRepositoryTest` quedó en verde.

Se ejecutó la batería general del proyecto y los **64 tests quedaron en verde**.

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

Cada repositorio incorporado cuenta con sus tests correspondientes.

## Últimos Builds / hitos conocidos

- Build 005 — Diseño de la entidad `Cuenta`.
- Build 009.1 — Implementación de `Categoria`.
- Build 010 — Implementación de `Movimiento`.
- Build 011 — Aislamiento y estabilización de tests JPA con H2.
- Build 012 — Repositorios JPA de entidades base.
- Build 013 — Repository JPA de `Cuenta`.
- Build 014 — Repository JPA de `Movimiento`.

## Commits recientes de código

- `4f0b20f` — `Build 014 - Implementación de MovimientoRepository`.
- `140d3eb` — `Build 013 - Implementación de CuentaRepository`.
- `9e1a9c3` — `feat(persistence): agregar repositories de Usuario y PerfilFinanciero`.
- `5a3ebfb` — `Build: agrega repositorios de InstitucionFinanciera y Moneda`.

## Tests

El flujo de desarrollo utiliza tests unitarios y tests JPA. El criterio de avance acordado es que los tests correspondientes al bloque estén en verde antes de considerar cerrado el Build.

En el Build 014 se verificó:

- `MovimientoRepositoryTest`.
- Batería general de tests del proyecto.

La batería general quedó en **64/64 tests en verde**.

## Próximo paso

Comenzar a definir el **Build 015** a partir del estado real del código, los repositorios ya disponibles y los tests existentes.

No avanzar directamente a implementar el Build 015 sin definir primero:

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