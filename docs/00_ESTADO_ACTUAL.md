# SOFP — Estado actual

> Documento de continuidad del proyecto. El código y los tests actuales son la fuente de verdad técnica.

## Identidad del proyecto

**Nombre:** SOFP — Sistema Operativo Financiero Personal  
**Repositorio:** agmilevecich/sofp  
**Rama principal:** `main`  
**Estado:** `main` contiene las etapas `operacion-financiera`, `identificacion-activo` y `cartera-activos` integradas y validadas. La feature `costo-promedio-activo` está desarrollada y validada, pendiente de integración.

## Estado funcional actual

El bloque de operaciones financieras quedó cerrado e integrado en `main`.

El bloque de identificación de activos mediante símbolo quedó cerrado e integrado en `main`.

El bloque de cartera de activos quedó cerrado e integrado en `main`.

La feature `costo-promedio-activo` implementó el cálculo del costo de adquisición remanente y del precio promedio de una posición activa.

`Activo` posee:
- `nombre`;
- `simbolo` obligatorio;
- `moneda` obligatoria.

El símbolo está definido como único en persistencia (`unique = true`). `Bono` hereda de `Activo` y utiliza el constructor identificable `nombre + simbolo + moneda`.

## Cartera de activos

Implementado y validado:

- listado de movimientos de activos por perfil financiero;
- agrupación de movimientos por activo;
- cálculo de posiciones mediante `CalculadorPosicionActivo`;
- exclusión de posiciones cuya cantidad final es cero;
- separación de movimientos entre perfiles financieros;
- consideración correcta de compras y ventas al consultar movimientos por perfil;
- cobertura específica mediante `CarteraActivoServiceTest`.

La feature `cartera-activos` fue integrada en `main` mediante fast-forward.

## Costo promedio de posición activa

Implementado y validado en `feature/costo-promedio-activo`:

- acumulación del costo de adquisición de las compras;
- cálculo del precio promedio de la posición;
- mantenimiento del costo de adquisición remanente después de ventas;
- reinicio del costo de adquisición al cerrar completamente la posición;
- rechazo de ventas superiores a la cantidad disponible;
- rechazo de movimientos de otro activo;
- rechazo de movimientos nulos;
- cobertura específica mediante `PosicionActivoTest` con **8/8 tests en verde**.

La feature se encuentra 2 commits por delante de `main` y 0 commits por detrás. Los cambios funcionales están concentrados en `PosicionActivo` y su test específico.

## Identificación y búsqueda

Implementado y validado:

- `ActivoRepository.buscarPorSimbolo(String)` devuelve `Optional<Activo>`;
- `BonoRepository.buscarPorSimbolo(String)` devuelve `Optional<Bono>`;
- ambos rechazan `null` mediante `NullPointerException`;
- los tests cubren la búsqueda por símbolo;
- la persistencia rechaza símbolos duplicados tanto para `Activo` como para `Bono`;
- la restricción de unicidad se verifica mediante tests específicos de repositorio.

## Última validación global conocida

Suite general ejecutada desde IntelliJ IDEA el **28/08/2026 19:12:22 -03:00**:

- Tests run: **447**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- Resultado: **BUILD SUCCESS**
- Duración: **12:27 min**

Además, los tests específicos de `PosicionActivoTest` fueron ejecutados durante el desarrollo de la feature y resultaron en **8/8 tests en verde**.

## Git

`main` contiene las etapas funcionales cerradas de operaciones financieras, identificación de activos y cartera de activos.

La feature actual es `feature/costo-promedio-activo`.

Últimos commits de la feature:
- `6cb038b` — `test: cubrir costo promedio de posicion activa`;
- `da09ef0` — `feat: calcular costo promedio de posicion activa`.

La feature está pendiente de integración en `main` mediante fast-forward.

## Persistencia

Repositorios JPA relevantes:

- `OperacionFinancieraRepository`
- `MovimientoActivoRepository`
- `ActivoRepository`
- `BonoRepository`

`MovimientoActivoRepository.listarPorPerfilFinanciero(Long)` contempla las cuentas de origen y destino mediante la consulta correspondiente para incluir compras y ventas.

## Etapas cerradas

- `feature/operacion-financiera`: integrada en `main`.
- `feature/identificacion-activo`: integrada en `main`.
- `feature/cartera-activos`: integrada en `main` mediante fast-forward.

## Reglas de continuidad

Código y tests son la fuente de verdad técnica. La documentación resume el estado y debe actualizarse al cerrar bloques importantes.

No considerar implementado ningún pendiente hasta contar con código verificable y tests correspondientes.

## Próximo paso

Cerrar documentalmente `feature/costo-promedio-activo`, verificar `git diff`, `git diff --check` y `git status`, y luego integrar la feature en `main` mediante `git merge --ff-only` una vez confirmada la validación final.
