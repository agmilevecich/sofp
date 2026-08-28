# SOFP — Estado actual

> Documento de continuidad del proyecto. El código y los tests actuales son la fuente de verdad técnica.

## Identidad del proyecto

**Nombre:** SOFP — Sistema Operativo Financiero Personal  
**Repositorio:** agmilevecich/sofp  
**Rama principal:** `main`  
**Estado:** `main` contiene las etapas `operacion-financiera` e `identificacion-activo` ya integradas.

## Estado funcional actual

El bloque de operaciones financieras quedó cerrado e integrado en `main`.

El bloque de identificación de activos mediante símbolo también quedó cerrado e integrado en `main`.

`Activo` posee:
- `nombre`;
- `simbolo` obligatorio;
- `moneda` obligatoria.

El símbolo está definido como único en persistencia (`unique = true`). `Bono` hereda de `Activo` y utiliza el constructor identificable `nombre + simbolo + moneda`.

## Identificación y búsqueda

Implementado y validado:

- `ActivoRepository.buscarPorSimbolo(String)` devuelve `Optional<Activo>`;
- `BonoRepository.buscarPorSimbolo(String)` devuelve `Optional<Bono>`;
- ambos rechazan `null` mediante `NullPointerException`;
- los tests cubren la búsqueda por símbolo;
- la persistencia rechaza símbolos duplicados tanto para `Activo` como para `Bono`;
- la restricción de unicidad se verifica mediante tests específicos de repositorio.

## Última validación global conocida

Suite general ejecutada desde IntelliJ IDEA el **28/08/2026 12:15:12 -03:00**:

- Tests run: **441**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- Resultado: **BUILD SUCCESS**
- Duración: **10:01 min**

Además, los tests específicos de `ActivoRepositoryTest` y `BonoRepositoryTest` fueron ejecutados y resultaron en **18/18 tests en verde**.

## Git

`main` quedó actualizado después del merge de `feature/identificacion-activo`.

Commit de merge: `0a554fb` — `merge: integrar identificacion de activos`.

La última sincronización local confirmó:
- `HEAD -> main` en `0a554fb`;
- `github/main` en `0a554fb`;
- `bitbucket/main` en `0a554fb`;
- working tree limpio.

## Persistencia

Repositorios JPA relevantes:

- `OperacionFinancieraRepository`
- `MovimientoActivoRepository`
- `ActivoRepository`
- `BonoRepository`

`ActivoRepository` y `BonoRepository` mantienen guardar, buscar por id, listar y buscar por símbolo.

## Etapas cerradas

- `feature/operacion-financiera`: integrada en `main` mediante `d39632b`.
- `feature/identificacion-activo`: integrada en `main` mediante `0a554fb`.

## Reglas de continuidad

Código y tests son la fuente de verdad técnica. La documentación resume el estado y debe actualizarse al cerrar bloques importantes.

No considerar implementado ningún pendiente hasta contar con código verificable y tests correspondientes.

## Próximo paso

Definir la próxima evolución funcional a partir del código y los casos de uso existentes. Antes de implementar, revisar las entidades, repositorios, servicios y tests actuales para elegir el cambio mínimo y mantener las reglas de negocio existentes.
