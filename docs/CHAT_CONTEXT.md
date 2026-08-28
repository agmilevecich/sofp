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
- `main`: rama estable actual, actualmente en `d9e7849`.
- `feature/operacion-financiera`: etapa integrada en `main`.
- `feature/identificacion-activo`: rama activa de desarrollo.
- `docs/continuidad-sofp`: no forma parte del flujo actual.

## Estado actual — 2026-08-28

La etapa `feature/operacion-financiera` está cerrada e integrada en `main`.

La rama activa `feature/identificacion-activo` trabaja sobre la identificación funcional de activos mediante símbolo.

`Activo` actualmente tiene `nombre`, `simbolo` obligatorio y único, y `moneda`. `Bono` hereda de `Activo` y utiliza `nombre + simbolo + moneda`.

Se implementaron:

- `ActivoRepository.buscarPorSimbolo(String)`;
- `BonoRepository.buscarPorSimbolo(String)`;
- retorno mediante `Optional`;
- rechazo de `null` en ambas búsquedas;
- adaptación de constructores y tests existentes al símbolo obligatorio.

Commits funcionales recientes:

- `3f6c776` — `feat: agregar busqueda de activo por simbolo`
- `6179f2d` — `test: cubrir busqueda de activo por simbolo`
- `354e0b3` — `feat: agregar busqueda de bono por simbolo`
- `976aff7` — `test: cubrir busqueda de bono por simbolo`

## Tests

Suite general más reciente conocida:

- **435 tests**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Finalizada: **27/08/2026 19:52:48 -03:00**
- Duración: **19:08 min**

Posteriormente se ejecutaron los tests específicos de los cambios de búsqueda por símbolo y fueron informados como verdes.

## Git y continuidad

Comparación actual reconstruida desde GitHub:

- `feature/identificacion-activo`: **19 commits por delante de `main`**.
- `feature/identificacion-activo`: **0 commits por detrás de `main`**.
- `main`: `d9e7849`.
- Último commit funcional de la feature antes de la actualización documental: `976aff7`.
- Las actualizaciones de continuidad de esta sesión se realizan sobre `feature/identificacion-activo`.
- `main` no debe modificarse automáticamente.

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

Cubrir la regla de unicidad del símbolo en persistencia mediante un test específico, verificando primero la implementación actual. Mantener el cambio mínimo y no modificar producción si la restricción existente ya es suficiente.

## Forma de trabajo

1. Reconstruir el estado desde GitHub antes de cambiar.
2. Revisar código, tests y reglas de negocio relacionadas.
3. Hacer el cambio mínimo en la rama activa.
4. Ejecutar tests específicos desde IntelliJ IDEA.
5. Ejecutar suite general cuando corresponda.
6. Confirmar el resultado real.
7. Revisar diff, `diff --check` y status local.
8. Crear commit pequeño y descriptivo.
9. Actualizar continuidad en la rama activa.
10. Antes del merge, comparar la feature contra `main` y validar pendientes.
11. No hacer merge a `main` automáticamente.

## Al cerrar una sesión

Actualizar, cuando corresponda:

- `docs/00_ESTADO_ACTUAL.md`
- `docs/06_BUILDS.md`
- `docs/07_TESTS.md`
- `docs/08_PENDIENTES.md`
- `docs/09_HISTORIAL_PROYECTO.md`
- `docs/CHAT_CONTEXT.md`

No registrar resultados de tests o builds que no hayan sido realmente ejecutados.
