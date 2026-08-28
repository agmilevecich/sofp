# SOFP — Estado actual

> Documento de continuidad del proyecto. El código y los tests actuales son la fuente de verdad técnica.

## Identidad del proyecto

**Nombre:** SOFP — Sistema Operativo Financiero Personal  
**Repositorio:** agmilevecich/sofp  
**Rama principal:** `main`  
**Estado:** `main` contiene las etapas `operacion-financiera`, `identificacion-activo`, `cartera-activos`, `costo-promedio-activo` y `valorizacion-posicion-activo` integradas y validadas.

## Estado funcional actual

El bloque de operaciones financieras quedó cerrado e integrado en `main`.

El bloque de identificación de activos mediante símbolo quedó cerrado e integrado en `main`.

El bloque de cartera de activos quedó cerrado e integrado en `main`.

El bloque de costo promedio de posición activa quedó cerrado e integrado en `main`.

El bloque de valorización de posición activa quedó cerrado e integrado en `main`.

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

Implementado y validado:

- acumulación del costo de adquisición de las compras;
- cálculo del precio promedio de la posición;
- mantenimiento del costo de adquisición remanente después de ventas;
- reinicio del costo de adquisición al cerrar completamente la posición;
- rechazo de ventas superiores a la cantidad disponible;
- rechazo de movimientos de otro activo;
- rechazo de movimientos nulos;
- cobertura específica mediante `PosicionActivoTest` con **8/8 tests en verde**.

La feature `costo-promedio-activo` fue integrada en `main` mediante fast-forward.

## Valorización de posición activa

Implementado y validado:

- cálculo del valor actual de una posición a partir de un precio informado;
- cálculo de ganancia o pérdida respecto del costo de adquisición;
- cálculo del rendimiento porcentual;
- valorización de posiciones cerradas;
- aceptación de precio actual cero;
- rechazo de posición nula;
- rechazo de precio actual nulo;
- rechazo de precio actual negativo;
- rendimiento cero cuando no existe costo de adquisición;
- cobertura específica mediante `ValorizacionPosicionActivoTest` con **8/8 tests en verde**.

La feature `valorizacion-posicion-activo` fue integrada en `main` mediante fast-forward hasta el commit `7379570`.

## Identificación y búsqueda

Implementado y validado:

- `ActivoRepository.buscarPorSimbolo(String)` devuelve `Optional<Activo>`;
- `BonoRepository.buscarPorSimbolo(String)` devuelve `Optional<Bono>`;
- ambos rechazan `null` mediante `NullPointerException`;
- los tests cubren la búsqueda por símbolo;
- la persistencia rechaza símbolos duplicados tanto para `Activo` como para `Bono`;
- la restricción de unicidad se verifica mediante tests específicos de repositorio.

## Última validación global conocida

Suite general ejecutada desde IntelliJ IDEA el **28/08/2026 19:56:00 -03:00**:

- Tests run: **455**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- Resultado: **BUILD SUCCESS**
- Duración: **11:24 min**

Además, los tests específicos de `PosicionActivoTest` y `ValorizacionPosicionActivoTest` fueron ejecutados durante el desarrollo de las features y resultaron en **8/8** y **8/8 tests en verde**, respectivamente.

## Git

`main` contiene las etapas funcionales cerradas de operaciones financieras, identificación de activos, cartera de activos, costo promedio y valorización de posiciones activas.

La última feature integrada fue `feature/valorizacion-posicion-activo`.

Últimos commits funcionales de la feature integrada:
- `7379570` — `test: cubrir valorizacion de posicion activa`;
- `ef19486` — `feat: agregar valorizacion de posicion activa`.

La integración se realizó mediante `git merge --ff-only`, sin commit de merge adicional. GitHub y Bitbucket quedaron actualizados hasta `7379570`.

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
- `feature/costo-promedio-activo`: integrada en `main` mediante fast-forward.
- `feature/valorizacion-posicion-activo`: integrada en `main` mediante fast-forward.

## Reglas de continuidad

Código y tests son la fuente de verdad técnica. La documentación resume el estado y debe actualizarse al cerrar bloques importantes.

No considerar implementado ningún pendiente hasta contar con código verificable y tests correspondientes.

## Próximo paso

Reconstruir el mapa funcional actual de `main` y revisar las entidades, repositorios, servicios, tests y reglas de negocio para seleccionar la siguiente evolución funcional mínima del backend. La parte gráfica continúa como etapa posterior, apoyándose sobre servicios y reglas de dominio estabilizados.
