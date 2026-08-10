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

El último bloque trabajado es **Build 013 — Repository JPA de Cuenta**.

Se incorporó y verificó la capa de persistencia para la entidad `Cuenta`.

Repositorio incorporado:

- `CuentaRepository`

Test incorporado:

- `CuentaRepositoryTest`

El repositorio permite:

- Guardar cuentas nuevas.
- Actualizar cuentas existentes.
- Buscar cuentas por ID.
- Listar todas las cuentas.
- Listar cuentas por `PerfilFinanciero`.

El test verifica la integración de `Cuenta` con:

- `Usuario`
- `PerfilFinanciero`
- `InstitucionFinanciera`
- `Moneda`

Todos los tests correspondientes al Build 013 quedaron en verde.

También se ejecutó la batería general de tests del proyecto y todos los tests terminaron en verde.

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

Cada repositorio incorporado cuenta con sus tests correspondientes.

## Últimos Builds / hitos conocidos

- Build 005 — Diseño de la entidad `Cuenta`.
- Build 009.1 — Implementación de `Categoria`.
- Build 010 — Implementación de `Movimiento`.
- Build 011 — Aislamiento y estabilización de tests JPA con H2.
- Build 012 — Repositorios JPA de entidades base.
- Build 013 — Repository JPA de `Cuenta`.

## Commits recientes de código

- `9e1a9c3` — `feat(persistence): agregar repositories de Usuario y PerfilFinanciero`.
- `5a3ebfb` — `Build: agrega repositorios de InstitucionFinanciera y Moneda`.

## Tests

El flujo de desarrollo utiliza tests unitarios y tests JPA. El criterio de avance acordado es que los tests correspondientes al bloque estén en verde antes de considerar cerrado el Build.

En el Build 013 se verificó:

- `CuentaRepositoryTest`.
- Batería general de tests del proyecto.

Todos terminaron en verde.

## Próximo paso

Definir el siguiente bloque funcional a partir del estado real del código, los repositorios ya disponibles y los tests existentes. No avanzar sin mantener la regla de tests en verde y documentación actualizada.

## Regla de continuidad

Cada bloque importante debe terminar con:

1. Código funcionando.
2. Tests en verde.
3. Commit identificable.
4. Actualización de documentación.
5. Registro del próximo paso.