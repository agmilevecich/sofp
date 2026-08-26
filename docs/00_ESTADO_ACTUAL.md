# SOFP — Estado actual

> Documento de continuidad del proyecto. Debe actualizarse al cerrar cada bloque importante.

## Identidad del proyecto

**Nombre:** SOFP — Sistema Operativo Financiero Personal  
**Repositorio:** agmilevecich/sofp  
**Rama principal:** `main`  
**Rama única de trabajo y continuidad:** `feature/operacion-financiera`  

La documentación de continuidad se mantiene en `docs/` dentro de la misma rama de trabajo. La rama `docs/continuidad-sofp` fue eliminada y no forma parte del flujo de continuidad del proyecto.

## Estado funcional actual

**Build 053 — Incorporación de `MovimientoActivo` — cerrado y validado.**

Se incorporó `MovimientoActivo` para representar el efecto de una operación sobre la tenencia de un activo, separado del efecto monetario representado por `Movimiento`.

Se incorporó `TipoMovimientoActivo` con `COMPRA` y `VENTA`, y `MovimientoActivoRepository` con persistencia JPA.

La cantidad se almacena como valor positivo; el tipo determina su efecto sobre la tenencia: compra positiva y venta negativa.

## Última validación

**Build 053 — cerrado y validado.**

`MovimientoActivoTest`: **11/11 tests en verde**.

`MovimientoActivoRepositoryTest`: **6/6 tests en verde**.

Suite general: **370/370 tests en verde**.

- Failures: **0**.
- Errors: **0**.
- Skipped: **0**.
- **BUILD SUCCESS**.
- Ejecución general: **26/08/2026 10:57:39 -03:00**.
- Duración: **24:34 min**.

Durante la validación del repositorio se corrigieron comparaciones `BigDecimal` en los tests para no depender de la escala decimal devuelta por JPA/H2. No se modificó código de producción por esta corrección.

## Git

- Rama única de trabajo y continuidad: `feature/operacion-financiera`.
- `main` permanece separado y no fue modificado.
- Build 052: `678c6ea` — `test: registrar Bono en persistencia JPA`.
- Build 053: `4be2264` — `feat: agregar tipo de movimiento de activo`.
- Build 053: `51d3860` — `feat: incorporar movimiento de activo`.
- Build 053: `381f3a6` — `feat: incorporar repositorio de movimiento de activo`.
- Build 053: `7a54b20` — `test: agregar cobertura de MovimientoActivo`.
- Build 053: `eb57063` — `test: agregar cobertura de MovimientoActivoRepository`.
- Build 053: `d14d114` — `test: registrar MovimientoActivo en persistencia JPA`.
- Corrección de tests: `a579d4e8` — `test: corregir comparacion decimal en MovimientoActivoRepositoryTest`.
- La documentación de continuidad se actualizó al cerrar el Build 053.
- GitHub y Bitbucket deberán quedar sincronizados después de traer estos cambios al entorno local.
- Las ramas auxiliares creadas durante la implementación de `Bono` fueron eliminadas de los remotos; permanece la rama de trabajo `feature/operacion-financiera` junto con `main`.

## Dominio construido

Entidades principales: `Usuario`, `PerfilFinanciero`, `InstitucionFinanciera`, `Moneda`, `Cuenta`, `Categoria`, `Movimiento`, `OperacionFinanciera`, `Activo`, `Bono` y `MovimientoActivo`.

`Activo` representa la base común para futuros instrumentos financieros y actualmente contiene `nombre` y `moneda` como datos propios del activo.

`Bono` es la primera especialización de `Activo` y actualmente no incorpora atributos financieros adicionales.

`MovimientoActivo` representa el efecto de una operación sobre la tenencia de un `Activo`, con tipo `COMPRA` o `VENTA`, cantidad y precio unitario.

Enumeraciones principales: `TipoInstitucionFinanciera`, `TipoMoneda`, `TipoCuenta`, `TipoMovimiento` y `TipoMovimientoActivo`.

## Persistencia

Repositorios JPA: `UsuarioRepository`, `PerfilFinancieroRepository`, `InstitucionFinancieraRepository`, `MonedaRepository`, `CuentaRepository`, `MovimientoRepository`, `CategoriaRepository`, `OperacionFinancieraRepository`, `ActivoRepository`, `BonoRepository` y `MovimientoActivoRepository`.

La persistencia de `Activo`, `Bono` y `MovimientoActivo` quedó validada mediante sus tests de repositorio.

## Services

La capa `service` contiene `CuentaService`, `MovimientoService`, `CategoriaService`, `PerfilFinancieroService`, `UsuarioService`, `InstitucionFinancieraService`, `MonedaService` y `OperacionFinancieraService`.

## Decisiones de dominio relevantes

Las transferencias no se modelan como un tercer `TipoMovimiento`. Una transferencia genera un **EGRESO** en la cuenta origen y un **INGRESO** en la cuenta destino, vinculados mediante una `OperacionFinanciera`.

Una `OperacionFinanciera` puede contener como máximo dos movimientos y cada `Movimiento` puede estar asociado a una única `OperacionFinanciera`.

`Activo` se mantiene deliberadamente como una entidad mínima. Cantidad, precio, cotización y posición se reservaron para los bloques de operaciones y posiciones de inversión.

`Bono` se mantiene deliberadamente como una especialización mínima de `Activo`. No se incorporan todavía valor nominal, tasa, vencimiento, cupón, amortización, emisor u otros atributos financieros porque esas reglas de dominio aún no fueron definidas explícitamente.

`MovimientoActivo` almacena una cantidad positiva y utiliza `COMPRA` o `VENTA` para determinar el signo de la variación de tenencia. La posición acumulada no se almacena todavía como entidad independiente.

## Próximo paso

Definir la siguiente evolución del bloque de inversiones a partir de reglas de negocio explícitas, especialmente la relación entre `OperacionFinanciera`, `Movimiento`, `MovimientoActivo` y la futura posición acumulada.

Antes de implementar un nuevo componente, revisar el dominio, repositorios, servicios y tests relacionados y mantener el cambio mínimo necesario.

## Regla de continuidad

Cada bloque importante debe terminar con código funcionando, tests en verde, commit identificable cuando corresponda, actualización de documentación y registro del próximo paso. Código, tests y documentación de continuidad deben permanecer en la misma rama `feature/operacion-financiera`.
