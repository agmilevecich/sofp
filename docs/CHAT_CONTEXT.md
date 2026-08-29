# SOFP — Contexto para continuar con ChatGPT

Este archivo es el punto de entrada para continuar SOFP en una nueva conversación. La fuente de verdad es el código, Git y los tests actuales; `docs/` resume el estado y puede quedar temporalmente desactualizada.

## Proyecto

SOFP — Sistema Operativo Financiero Personal.

Aplicación Java de finanzas personales con dominio, persistencia JPA, servicios transaccionales y tests automatizados.

## Tecnologías

- Java / JDK 23.0.1
- IntelliJ IDEA Community 2026.1.1
- Maven
- Jakarta Persistence / JPA
- Hibernate 6.6.18.Final
- H2 2.3.232
- JUnit 5.11.4
- BigDecimal para dinero

## Ramas

- Repositorio: `agmilevecich/sofp`
- `main`: rama estable actual.
- `feature/operacion-financiera`: etapa integrada en `main`.
- `feature/identificacion-activo`: etapa integrada en `main`.
- `feature/cartera-activos`: etapa integrada en `main`.
- `feature/costo-promedio-activo`: etapa integrada en `main`.
- `feature/valorizacion-posicion-activo`: etapa integrada en `main`.
- `feature/reportes-cartera`: etapa cerrada e integrada en `main` mediante fast-forward.
- `docs/continuidad-sofp`: no forma parte del flujo actual.

## Estado actual — 2026-08-29

El bloque de operaciones financieras está cerrado e integrado en `main`.

La identificación funcional de activos mediante símbolo está cerrada e integrada en `main`.

La cartera de activos, costo promedio y valorización de posiciones están cerrados e integrados en `main`.

La etapa `feature/reportes-cartera` también está cerrada e integrada en `main` mediante fast-forward.

La integración funcional se verificó cuando `main` y `feature/reportes-cartera` coincidían en `0b73e87`, con **0 commits adelante / 0 commits detrás**.

Las actualizaciones documentales posteriores se realizaron sobre `main` para registrar correctamente el estado post-merge.

## Reportes y evolución histórica

Se implementaron y validaron:

- reporte de cartera de activos;
- composición valorizada de cartera;
- detalle de movimientos de cartera;
- evolución histórica del saldo de una cuenta;
- integración de la evolución histórica en `CuentaService`.

`CuentaService.obtenerEvolucionSaldo(Long)` genera puntos históricos con el saldo acumulado después de cada movimiento, respetando el orden cronológico de `MovimientoRepository.listarPorCuenta(Long)`.

## Tests

Suite general más reciente conocida y confirmada por el usuario:

- **480 tests**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Finalizada: **29/08/2026 13:29:56 -03:00**
- Duración: **12:23 min**

Tests específicos conocidos:

- `CuentaServiceEvolucionSaldoTest`: **5/5 tests en verde**;
- `PosicionActivoTest`: **8/8 tests en verde**;
- `ValorizacionPosicionActivoTest`: **8/8 tests en verde**;
- `CarteraActivoServiceTest`: **5/5 tests en verde**.

La suite general vigente queda en **480/480 tests en verde**.

## Git y continuidad

Estado funcional reconstruido desde GitHub después de la integración:

- `main` y `feature/reportes-cartera` coincidieron en `0b73e87`;
- diferencia funcional: **0 commits adelante / 0 commits detrás**;
- integración: **fast-forward**.

El usuario confirmó localmente antes de las actualizaciones documentales:

- `git syncsofp` → Already up to date / Everything up-to-date;
- `git status` → working tree clean;
- `git diff --check` → sin observaciones.

## Dominio actual

Entidades principales:

- Usuario
- PerfilFinanciero
- InstitucionFinanciera
- Moneda
- Cuenta
- Categoria
- Movimiento
- OperacionFinanciera
- Activo
- Bono

`OperacionFinanciera` agrupa operaciones y movimientos. Compra y venta de activos están implementadas y validadas. `PosicionActivo` calcula la posición a partir de movimientos persistidos mediante los servicios reales.

## Persistencia

Repositorios relevantes:

- UsuarioRepository
- PerfilFinancieroRepository
- InstitucionFinancieraRepository
- MonedaRepository
- CuentaRepository
- MovimientoRepository
- CategoriaRepository
- OperacionFinancieraRepository
- ActivoRepository
- BonoRepository
- MovimientoActivoRepository

## Próximo paso

Definir la siguiente evolución funcional a partir del estado real de `main`.

Antes de implementar:

1. revisar código relacionado;
2. revisar tests;
3. revisar reglas de negocio;
4. seleccionar el cambio mínimo;
5. crear o utilizar una rama de feature sin modificar `main` directamente;
6. ejecutar tests específicos y, cuando corresponda, la suite general;
7. revisar diff, `git diff --check` y `git status`;
8. registrar el cierre en la documentación.

## Forma de trabajo

1. Reconstruir el estado desde GitHub antes de cambiar.
2. Código y tests prevalecen sobre la documentación.
3. Hacer el cambio mínimo en la rama activa.
4. No asumir resultados de tests no informados.
5. Crear commits pequeños y descriptivos.
6. No hacer merge a `main` automáticamente.
7. Al cerrar etapas importantes, actualizar la documentación de continuidad.

## Al cerrar una sesión

Actualizar, cuando corresponda:

- `docs/00_ESTADO_ACTUAL.md`
- `docs/06_BUILDS.md`
- `docs/07_TESTS.md`
- `docs/08_PENDIENTES.md`
- `docs/09_HISTORIAL_PROYECTO.md`
- `docs/CHAT_CONTEXT.md`

No registrar resultados de tests o builds que no hayan sido realmente ejecutados.
