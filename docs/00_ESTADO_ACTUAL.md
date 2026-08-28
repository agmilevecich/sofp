# SOFP — Estado actual

> Documento de continuidad del proyecto. El código y los tests actuales son la fuente de verdad técnica.

## Identidad del proyecto

**Nombre:** SOFP — Sistema Operativo Financiero Personal  
**Repositorio:** agmilevecich/sofp  
**Rama principal:** `main`  
**Rama de trabajo actual:** `feature/identificacion-activo`

La etapa `feature/operacion-financiera` fue integrada en `main` mediante `d39632b`.

## Estado funcional actual

El bloque de operaciones financieras quedó cerrado. Actualmente se trabaja en la identificación de activos mediante símbolo.

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

Commits relevantes:

- `3f6c776` — `feat: agregar busqueda de activo por simbolo`
- `6179f2d` — `test: cubrir busqueda de activo por simbolo`
- `354e0b3` — `feat: agregar busqueda de bono por simbolo`
- `976aff7` — `test: cubrir busqueda de bono por simbolo`
- `7435ee0` — `test: cubrir unicidad del simbolo de activos`
- `94dc53e` — `test: cubrir unicidad del simbolo de bonos`
- `ac44680` — `fix: capturar unicidad de simbolos al guardar`

Antes de estos cambios se adaptaron constructores y tests de compra, venta, posición, integridad y persistencia al símbolo obligatorio.

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

La rama de trabajo actual es `feature/identificacion-activo`. `main` no debe modificarse mientras se continúa esta feature.

La comparación anterior conocida con `main` indicó que la feature estaba por delante y sin commits pendientes de incorporar desde `main`. Antes del merge se realizará una nueva comparación final y se revisarán commits, archivos modificados y estado de la rama.

## Persistencia

Repositorios JPA relevantes:

- `OperacionFinancieraRepository`
- `MovimientoActivoRepository`
- `ActivoRepository`
- `BonoRepository`

`ActivoRepository` y `BonoRepository` mantienen guardar, buscar por id, listar y buscar por símbolo.

## Reglas de continuidad

Código y tests son la fuente de verdad técnica. La documentación resume el estado y debe actualizarse al cerrar bloques importantes.

No considerar implementado ningún pendiente hasta contar con código verificable y tests correspondientes.

## Próximo paso

Realizar la revisión final de `feature/identificacion-activo` contra `main`: commits, archivos modificados, funcionalidades, tests y documentación. Determinar si la feature está lista para merge o si existe algún pendiente adicional.
