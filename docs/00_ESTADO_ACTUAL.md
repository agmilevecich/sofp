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

`Activo` ahora posee:
- `nombre`;
- `simbolo` obligatorio;
- `moneda` obligatoria.

El símbolo está definido como único en persistencia (`unique = true`). `Bono` hereda de `Activo` y utiliza el constructor identificable `nombre + simbolo + moneda`.

## Identificación y búsqueda

Implementado y validado:

- `ActivoRepository.buscarPorSimbolo(String)` devuelve `Optional<Activo>`;
- `BonoRepository.buscarPorSimbolo(String)` devuelve `Optional<Bono>`;
- ambos rechazan `null` mediante `NullPointerException`;
- los tests cubren la búsqueda por símbolo.

Commits relevantes:

- `3f6c776` — `feat: agregar busqueda de activo por simbolo`
- `6179f2d` — `test: cubrir busqueda de activo por simbolo`
- `354e0b3` — `feat: agregar busqueda de bono por simbolo`
- `976aff7` — `test: cubrir busqueda de bono por simbolo`

Antes de estos cambios se adaptaron constructores y tests de compra, venta, posición, integridad y persistencia al símbolo obligatorio.

## Última validación global conocida

Suite general ejecutada desde IntelliJ IDEA el **27/08/2026 19:52:48 -03:00**:

- Tests run: **435**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- Resultado: **BUILD SUCCESS**
- Duración: **19:08 min**

Posteriormente se validaron desde IntelliJ las pruebas específicas de los cambios de identificación por símbolo, con todos los tests indicados en verde.

## Git

Estado confirmado en GitHub:

- `feature/identificacion-activo` está **19 commits por delante de `main`** y **0 por detrás**.
- `main`: `d9e7849` — `docs: actualizar contexto tras merge de operacion financiera`.
- HEAD de la feature: `976aff7` — `test: cubrir busqueda de bono por simbolo`.
- La rama de trabajo está sincronizada con GitHub y Bitbucket y el working tree local fue informado como limpio.
- `main` no debe modificarse mientras se continúa esta feature.

## Persistencia

Repositorios JPA relevantes:

- `OperacionFinancieraRepository`
- `MovimientoActivoRepository`
- `ActivoRepository`
- `BonoRepository`

`ActivoRepository` y `BonoRepository` mantienen guardar, buscar por id, listar y ahora buscar por símbolo.

## Reglas de continuidad

Código y tests son la fuente de verdad técnica. La documentación resume el estado y debe actualizarse al cerrar bloques importantes.

No considerar implementado ningún pendiente hasta contar con código verificable y tests correspondientes.

## Próximo paso

Revisar y cubrir con tests la regla de unicidad del símbolo en persistencia, verificando primero la implementación actual y manteniendo el cambio mínimo. Después continuar con la siguiente necesidad real de identificación de instrumentos.
