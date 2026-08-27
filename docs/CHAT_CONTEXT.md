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
- `feature/operacion-financiera`: etapa integrada en `main`; no es la rama activa de desarrollo.
- `docs/continuidad-sofp`: no forma parte del flujo actual.

El desarrollo de una nueva funcionalidad debe comenzar desde `main` creando una nueva rama `feature/...` cuando corresponda.

## Estado actual — 27/08/2026

Último Build cerrado: **Build 059 — Suite general posterior a venta y posición**.

Resultado: **433/433 tests en verde**, `Failures: 0`, `Errors: 0`, `Skipped: 0`, `BUILD SUCCESS`.

Finalizada: **27/08/2026 15:24:11 -03:00**. Duración: **17:35 min**.

La etapa cerrada de `OperacionFinanciera` incluye:

- `TRANSFERENCIA`;
- `COMPRA` de activos;
- `VENTA` de activos;
- movimientos monetarios y `MovimientoActivo`;
- persistencia de las relaciones con `OperacionFinanciera`;
- cálculo de posición de activos.

Validaciones recientes:

- `OperacionFinancieraTest`: **17/17**.
- `OperacionFinancieraServiceTest`: **22/22**.
- `OperacionFinancieraCompraServiceTest`: **13/13**.
- `OperacionFinancieraVentaServiceTest`: **13/13**.
- `PosicionActivoServiceTest`: **4/4**.

La integración real valida `COMPRA 100 + VENTA 30 = POSICION 70` mediante los servicios.

## Git y merge

La etapa `feature/operacion-financiera` fue integrada en `main` mediante:

```text
d39632b Merge branch 'feature/operacion-financiera'
```

`main` local, `github/main` y `bitbucket/main` quedaron alineados en `d39632b` antes de la actualización documental.

La divergencia histórica de GitHub que contenía únicamente la creación y eliminación de `docs/00_ESTADO_ACTUAL.md.tmp` fue resuelta mediante `--force-with-lease`.

La documentación posterior al merge se actualiza directamente en `main`.

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

`Activo` y `Bono` se mantienen deliberadamente mínimos hasta definir reglas financieras específicas.

`OperacionFinanciera` agrupa la operación y sus movimientos. Una compra genera `EGRESO` + `MovimientoActivo.COMPRA`; una venta genera `INGRESO` + `MovimientoActivo.VENTA`.

Una venta superior a la posición disponible se rechaza en el dominio. Las posiciones short quedan para una decisión futura explícita.

Las comisiones y gastos quedan para una etapa posterior.

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

Revisar el dominio actual y definir la siguiente funcionalidad de inversiones a partir de reglas de negocio explícitas.

Antes de implementar, revisar código, tests y documentación relacionada y crear una nueva rama `feature/...` desde `main` cuando corresponda.

## Forma de trabajo

1. Revisar código, tests y GitHub antes de cambiar.
2. Hacer el cambio mínimo.
3. Ejecutar tests específicos desde IntelliJ IDEA.
4. Ejecutar suite general cuando corresponda.
5. Confirmar el resultado real.
6. Revisar diff, `diff --check` y status local.
7. Commit pequeño y descriptivo.
8. Actualizar continuidad en la rama activa.
9. Antes del merge, revisar la feature contra `main`.
10. Tras el merge, actualizar la continuidad en `main`.

## Al cerrar una sesión

Actualizar, cuando corresponda:

- `docs/00_ESTADO_ACTUAL.md`
- `docs/06_BUILDS.md`
- `docs/07_TESTS.md`
- `docs/08_PENDIENTES.md`
- `docs/09_HISTORIAL_PROYECTO.md`
- `docs/CHAT_CONTEXT.md`

No registrar resultados de tests o builds que no hayan sido realmente ejecutados.
