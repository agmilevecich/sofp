# SOFP — Estado actual

> Documento de continuidad del proyecto. Debe actualizarse al cerrar cada bloque importante.

## Identidad del proyecto

**Nombre:** SOFP — Sistema Operativo Financiero Personal  
**Repositorio:** agmilevecich/sofp  
**Rama principal:** `main`  
**Rama única de trabajo y continuidad:** `feature/operacion-financiera`  

La documentación de continuidad se mantiene en `docs/` dentro de la misma rama de trabajo. La rama `docs/continuidad-sofp` fue eliminada y no forma parte del flujo de continuidad del proyecto.

## Estado funcional actual

**Build 052 — Incorporación de `Bono` como especialización mínima de `Activo` — cerrado y validado.**

Se incorporó `Bono` como primera especialización de `Activo`, sin atributos financieros específicos adicionales. `Bono` hereda `nombre` y `Moneda` desde `Activo` y aporta únicamente su identidad de tipo.

Se incorporó `BonoRepository` con operaciones de guardar, actualizar, buscar por ID y listar.

Se registró `Bono` explícitamente en la unidad de persistencia utilizada por los tests JPA.

## Última validación

**Build 052 — cerrado y validado.**

`BonoTest`: **5/5 tests en verde**.

`BonoRepositoryTest`: **6/6 tests en verde**.

Suite general: **353/353 tests en verde**.

- Failures: **0**.
- Errors: **0**.
- Skipped: **0**.
- **BUILD SUCCESS**.
- Ejecución general: **25/08/2026 20:14:39 -03:00**.
- Duración: **16:07 min**.

## Git

- Rama única de trabajo y continuidad: `feature/operacion-financiera`.
- `main` permanece separado y no fue modificado.
- Build 051: `aa02066` — `fix: registrar Activo en persistencia de tests`.
- Build 052: `589acf1` — `feat: incorporar entidad Bono`.
- Build 052: `c11f1f4` — `test: agregar cobertura de Bono`.
- Build 052: `6925447` — `feat: incorporar repositorio de Bono`.
- Build 052: `74b8fff` — `test: agregar cobertura de BonoRepository`.
- Build 052: `678c6ea` — `test: registrar Bono en persistencia JPA`.
- GitHub y Bitbucket están sincronizados en `feature/operacion-financiera`.
- Working tree confirmado limpio antes de la validación del Build 052.
- `git diff --check` confirmado limpio antes de la validación del Build 052.
- Las ramas auxiliares creadas accidentalmente durante la implementación de `Bono` fueron eliminadas de los remotos; solo permanece la rama de trabajo `feature/operacion-financiera` junto con `main`.

## Dominio construido

Entidades principales: `Usuario`, `PerfilFinanciero`, `InstitucionFinanciera`, `Moneda`, `Cuenta`, `Categoria`, `Movimiento`, `OperacionFinanciera`, `Activo` y `Bono`.

`Activo` representa la base común para futuros instrumentos financieros y actualmente contiene `nombre` y `moneda` como datos propios del activo.

`Bono` es la primera especialización de `Activo` y actualmente no incorpora atributos financieros adicionales.

Enumeraciones principales: `TipoInstitucionFinanciera`, `TipoMoneda`, `TipoCuenta` y `TipoMovimiento`.

## Persistencia

Repositorios JPA: `UsuarioRepository`, `PerfilFinancieroRepository`, `InstitucionFinancieraRepository`, `MonedaRepository`, `CuentaRepository`, `MovimientoRepository`, `CategoriaRepository`, `OperacionFinancieraRepository`, `ActivoRepository` y `BonoRepository`.

`ActivoRepositoryTest`: **6/6 tests en verde**.

`BonoRepositoryTest`: **6/6 tests en verde**.

La persistencia de `Activo` y `Bono` quedó validada junto con sus asociaciones obligatorias con `Moneda`.

## Services

La capa `service` contiene `CuentaService`, `MovimientoService`, `CategoriaService`, `PerfilFinancieroService`, `UsuarioService`, `InstitucionFinancieraService`, `MonedaService` y `OperacionFinancieraService`.

## Decisiones de dominio relevantes

Las transferencias no se modelan como un tercer `TipoMovimiento`. Una transferencia genera un **EGRESO** en la cuenta origen y un **INGRESO** en la cuenta destino, vinculados mediante una `OperacionFinanciera`.

Una `OperacionFinanciera` puede contener como máximo dos movimientos y cada `Movimiento` puede estar asociado a una única `OperacionFinanciera`.

`Activo` se mantiene deliberadamente como una entidad mínima. Cantidad, precio, cotización y posición se reservarán para los bloques de operaciones y posiciones de inversión.

`Bono` se mantiene deliberadamente como una especialización mínima de `Activo`. No se incorporan todavía valor nominal, tasa, vencimiento, cupón, amortización, emisor u otros atributos financieros porque esas reglas de dominio aún no fueron definidas explícitamente.

## Próximo paso

Definir la siguiente evolución del bloque de inversiones a partir de reglas de negocio explícitas. La especialización mínima `Bono` ya quedó validada; antes de agregar atributos financieros específicos se deberán definir y documentar sus reglas de dominio.

Antes de implementar un nuevo componente, revisar el dominio, repositorios, servicios y tests relacionados y mantener el cambio mínimo necesario.

## Regla de continuidad

Cada bloque importante debe terminar con código funcionando, tests en verde, commit identificable cuando corresponda, actualización de documentación y registro del próximo paso. Código, tests y documentación de continuidad deben permanecer en la misma rama `feature/operacion-financiera`.
