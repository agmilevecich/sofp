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

El proyecto está organizado alrededor del dominio y capas previstas para evolución posterior. Se han utilizado paquetes como `domain`, `config`, `util`, `repository`, `service`, `dto`, `enums`, `app` y `ui`.

La persistencia utiliza la unidad `sofp-persistence-unit` y una base H2 en archivo.

## Estado funcional al último registro

El último commit confirmado en GitHub es:

`ce3383cc9cc83725eaf3895c533e85af2820c709`

**Build 010 — Implementación de la entidad Movimiento**

En este Build se incorporaron:

- Entidad `Movimiento`.
- Enum `TipoMovimiento` con `INGRESO` y `EGRESO`.
- Validación de importes positivos.
- Relaciones de `Movimiento` con `Cuenta` y `Categoria`.
- Test unitario `MovimientoTest`.
- Test JPA `MovimientoJpaTest`.

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

## Últimos Builds / hitos conocidos

- Build 005 — Diseño de la entidad `Cuenta`.
- Build 009.1 — Implementación de `Categoria`.
- Build 010 — Implementación de `Movimiento`.

Entre estos hitos también se consolidó el dominio, se definieron principios de arquitectura, se agregó `TipoCuenta`, utilidades de validación e `InstitucionFinanciera`.

## Tests

El flujo de desarrollo utiliza tests unitarios y tests JPA. El criterio de avance acordado es que los tests correspondientes al bloque estén en verde antes de considerar cerrado el Build.

## Próximo paso

Antes de comenzar una nueva funcionalidad, revisar este archivo, `docs/05_DECISIONES.md`, `docs/06_BUILDS.md`, `docs/07_TESTS.md` y `docs/08_PENDIENTES.md`.

El siguiente trabajo funcional debe definirse a partir del estado real del código y los tests, no suponerse por el número del Build.

## Regla de continuidad

Cada bloque importante debe terminar con:

1. Código funcionando.
2. Tests en verde.
3. Commit identificable.
4. Actualización de documentación.
5. Registro del próximo paso.
