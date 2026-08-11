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

Último Build confirmado: **Build 015 — Servicio de saldo de cuentas**.

Último commit de código confirmado: `4697815`.

Mensaje: `feat: implementar servicio de saldo de cuentas`.

El commit de Build 015 fue publicado en `main` de GitHub y Bitbucket.

Commits de código recientes:

- `4697815` — `feat: implementar servicio de saldo de cuentas`.
- `4f0b20f` — `Build 014 - Implementación de MovimientoRepository`.
- `140d3eb` — `Build 013 - Implementación de CuentaRepository`.
- `9e1a9c3` — `feat(persistence): agregar repositories de Usuario y PerfilFinanciero`.
- `5a3ebfb` — `Build: agrega repositorios de InstitucionFinanciera y Moneda`.

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

Cada repositorio tiene su test correspondiente.

## Service

Se inició la capa `service` con:

- `CuentaService`

`CuentaService` recibe `MovimientoRepository` por constructor y proporciona `calcularSaldo(Long cuentaId)`.

Reglas actuales del cálculo:

- `INGRESO` suma.
- `EGRESO` resta.
- Sin movimientos → `BigDecimal.ZERO`.
- Se procesan múltiples movimientos de una cuenta.

Test asociado:

- `CuentaServiceTest`

El test cubre cuenta sin movimientos, ingreso, egreso y múltiples movimientos.

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

## Tests

La batería general del proyecto quedó en **68/68 tests en verde** al cerrar Build 015.

La infraestructura de pruebas utiliza `JpaTestManager`, H2 en memoria y `create-drop`. En `CuentaServiceTest` se cierra `JpaTestManager` en el `tearDown()` para garantizar el aislamiento de la base entre tests.

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

Si hubo una decisión arquitectónica o de negocio importante, actualizar también `docs/05_DECISIONES.md`.
