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
- `main`: rama estable; no modificar durante el desarrollo de la feature.
- `feature/operacion-financiera`: rama única de trabajo y continuidad actual.
- `docs/continuidad-sofp`: eliminada.

Todo código, tests y documentación de continuidad de esta etapa se mantiene en `feature/operacion-financiera`.

## Estado actual — 27/08/2026

Último Build cerrado: **Build 059 — Suite general posterior a venta y posición**.

Resultado: **433/433 tests en verde**, `Failures: 0`, `Errors: 0`, `Skipped: 0`, `BUILD SUCCESS`.

Finalizada: **27/08/2026 15:24:11 -03:00**. Duración: **17:35 min**.

La etapa actual de `OperacionFinanciera` incluye:

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

Rama activa: `feature/operacion-financiera`.

La comparación actual contra `main` muestra que la feature está por delante y que `main` tiene dos commits que no están en la feature. Esos dos commits corresponden únicamente a la creación y eliminación de `docs/00_ESTADO_ACTUAL.md.tmp`; no contienen cambios funcionales que deban incorporarse a la feature.

No se debe integrar `docs/continuidad-sofp`.

Antes del merge se debe hacer la revisión final local:

```text
git status
git diff
git diff --check
git log --oneline --decorate --graph --all
git diff main...feature/operacion-financiera --stat
```

Después evaluar la estrategia de merge. No modificar `main` automáticamente.

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

## Forma de trabajo

1. Revisar código, tests y GitHub antes de cambiar.
2. Hacer el cambio mínimo.
3. Ejecutar tests específicos desde IntelliJ IDEA.
4. Ejecutar suite general.
5. Confirmar resultado.
6. Revisar diff, `diff --check` y status local.
7. Commit pequeño y descriptivo.
8. Actualizar continuidad en esta misma rama.
9. Definir el siguiente paso.

## Al cerrar una sesión

Actualizar, cuando corresponda:

- `docs/00_ESTADO_ACTUAL.md`
- `docs/06_BUILDS.md`
- `docs/07_TESTS.md`
- `docs/08_PENDIENTES.md`
- `docs/09_HISTORIAL_PROYECTO.md`
- `docs/CHAT_CONTEXT.md`

No registrar resultados de tests o builds que no hayan sido realmente ejecutados.
