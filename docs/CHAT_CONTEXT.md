# SOFP — Contexto para continuar con ChatGPT

Este archivo es el punto de entrada para continuar SOFP en una nueva conversación. La fuente de verdad es el código, Git y los tests actuales; `docs/` es documentación auxiliar.

## Proyecto

SOFP — Sistema Operativo Financiero Personal.

Aplicación Java de finanzas personales con dominio, persistencia JPA, servicios y tests automatizados.

## Tecnologías

- Java / JDK 23.0.1
- IntelliJ IDEA Community 2026.1.1
- Maven
- Jakarta Persistence / JPA
- Hibernate 6.6.18.Final
- H2 2.3.232
- JUnit 5.11.4
- BigDecimal para dinero

## Estado actual — 30/08/2026

`main` es la rama estable. El último commit del repositorio es:

- `1a2c3c1` — `docs: actualizar estado final de continuidad`.

El último commit funcional/documental de cierre de la feature de seguridad es `7d6632f` — `docs: actualizar revision final de seguridad de perfil`.

`feature/seguridad-perfil-financiero` fue integrada en `main` mediante **fast-forward** y queda como rama histórica.

## Features cerradas e integradas

- `feature/operacion-financiera`;
- `feature/identificacion-activo`;
- `feature/cartera-activos`;
- `feature/costo-promedio-activo`;
- `feature/valorizacion-posicion-activo`;
- `feature/reportes-cartera`;
- `feature/seguridad-perfil-financiero`.

## Seguridad de PerfilFinanciero

`PerfilFinancieroService` verifica el propietario para las operaciones de cambio de descripción, activación y desactivación.

`PerfilFinancieroServiceTest`: **19/19 tests en verde**.

La feature quedó integrada en `main` mediante fast-forward.

## Validación global vigente

Suite completa ejecutada desde IntelliJ IDEA el **29/08/2026 20:00:23 -03:00**:

- **486 tests**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- Duración: **15:50 min**.

## Reportes y evolución histórica

Se implementaron y validaron:

- reporte de cartera de activos;
- composición valorizada de cartera;
- detalle de movimientos de cartera;
- evolución histórica del saldo de una cuenta;
- integración de la evolución histórica en `CuentaService`.

## Dominio actual

Entidades principales conocidas:

- `Usuario`;
- `PerfilFinanciero`;
- `InstitucionFinanciera`;
- `Moneda`;
- `Cuenta`;
- `Categoria`;
- `Movimiento`;
- `OperacionFinanciera`;
- `Activo`;
- `Bono`.

Compra y venta de activos están implementadas y validadas. `PosicionActivo` calcula la posición a partir de movimientos persistidos.

## Persistencia

Repositorios relevantes:

- `UsuarioRepository`;
- `PerfilFinancieroRepository`;
- `InstitucionFinancieraRepository`;
- `MonedaRepository`;
- `CuentaRepository`;
- `MovimientoRepository`;
- `CategoriaRepository`;
- `OperacionFinancieraRepository`;
- `ActivoRepository`;
- `BonoRepository`;
- `MovimientoActivoRepository`.

## Git y continuidad

Se mantienen dos remotos (`github` y `bitbucket`) como referencia y recuperación ante errores accidentales.

`main` es estable. Las nuevas funcionalidades se desarrollan en ramas propias. La integración de features se realiza mediante `git merge --ff-only`, sin merge commit.

Antes de cualquier nuevo cambio:

1. reconstruir el estado desde GitHub;
2. revisar rama y últimos commits;
3. comparar con `main`;
4. revisar código, tests y reglas de negocio relacionadas;
5. seleccionar el cambio mínimo;
6. ejecutar tests específicos y la suite general cuando corresponda;
7. revisar diff, `git diff --check` y `git status`;
8. actualizar la documentación al cerrar etapas importantes.

## Próximo paso

No existe actualmente una feature pendiente de integración. El próximo trabajo debe definirse a partir del código real de `main`, revisando entidades, repositorios, servicios, tests y reglas de negocio antes de crear la siguiente rama.

La interfaz gráfica continúa como evolución posterior, apoyándose sobre el backend estabilizado.

## Documentación de continuidad

- `docs/00_ESTADO_ACTUAL.md`;
- `docs/06_BUILDS.md`;
- `docs/07_TESTS.md`;
- `docs/08_PENDIENTES.md`;
- `docs/09_HISTORIAL_PROYECTO.md`;
- `docs/10_SEGURIDAD_PERFIL_FINANCIERO.md`;
- `docs/CHAT_CONTEXT.md`.

No registrar resultados de tests o builds que no hayan sido realmente ejecutados.
