# SOFP — Estado actual

> Documento de continuidad del proyecto. Debe actualizarse al cerrar cada bloque importante.

## Identidad del proyecto

**Nombre:** SOFP — Sistema Operativo Financiero Personal  
**Repositorio:** agmilevecich/sofp  
**Rama principal:** `main`  
**Rama única de trabajo y continuidad:** `feature/operacion-financiera`  

La documentación de continuidad se mantiene en `docs/` dentro de la misma rama de trabajo. `main` permanece separada hasta disponer de un estado funcional estable y validado para realizar el merge.

## Estado funcional actual

**Bloque actual — OperacionFinanciera — integridad de movimientos reforzada y validada.**

`OperacionFinanciera` agrupa los movimientos monetarios que representan una transferencia entre una cuenta origen y una cuenta destino. El dominio ahora refuerza además la coherencia entre cada movimiento y la cuenta correspondiente.

La arquitectura actual contempla:

```text
Activo
 └── Bono

OperacionFinanciera
 ├── Movimiento
 └── MovimientoActivo
        └── Activo

MovimientoActivo
      ↓
CalculadorPosicionActivo
      ↓
PosicionActivo

PosicionActivoService
      ↓
MovimientoActivoRepository
      ↓
CalculadorPosicionActivo
```

## Última validación — Build 055

El bloque de integridad de `OperacionFinanciera` quedó validado mediante tests específicos y suite general.

Tests específicos:

- `OperacionFinancieraTest`: **14/14**.
- `OperacionFinancieraIntegridadTest`: **4/4**.
- Total específico del bloque: **18/18**.

Suite general:

```text
Tests run: 397
Failures: 0
Errors: 0
Skipped: 0
BUILD SUCCESS
```

- Ejecución: **26/08/2026 16:48:26 -03:00**.
- Duración: **20:54 min**.

Validación Git posterior al Build:

- `git status`: working tree limpio.
- `git diff --check`: sin errores.
- Rama `feature/operacion-financiera` sincronizada con GitHub y Bitbucket.

## Git

Los últimos commits funcionales de la rama son:

- `e7c567b` — `test: adaptar OperacionFinancieraTest a reglas de integridad`.
- `31277fe` — `test: reforzar integridad de movimientos de operacion financiera`.
- `3dcadc7` — `fix: reforzar integridad de movimientos de operacion financiera`.
- `f484241` — `feat: incorporar servicio de posicion de activo`.
- `6967bce` — `test: ampliar cobertura de busqueda de movimientos por activo`.
- `4ead5a5` — `feat: agregar busqueda de movimientos de activo por activo`.
- `72f2b84` — `docs: registrar Build 054 y calculador de posicion`.

El último commit de documentación generado al cerrar este Build es `03ef85a7c69dd36deba6aeabfbe251fac6833071`.

## Dominio construido

Entidades principales: `Usuario`, `PerfilFinanciero`, `InstitucionFinanciera`, `Moneda`, `Cuenta`, `Categoria`, `Movimiento`, `OperacionFinanciera`, `Activo`, `Bono` y `MovimientoActivo`.

`Activo` representa la base común para futuros instrumentos financieros y actualmente contiene `nombre` y `moneda` como datos propios del activo.

`Bono` es la primera especialización de `Activo` y actualmente no incorpora atributos financieros adicionales.

`MovimientoActivo` representa el efecto de una operación sobre la tenencia de un `Activo`, con tipo `COMPRA` o `VENTA`, cantidad y precio unitario. La cantidad se almacena como valor positivo; el tipo determina el efecto sobre la tenencia.

`OperacionFinanciera` permite agrupar el movimiento monetario y el movimiento específico del activo que forman parte de una misma operación. Para sus movimientos monetarios se valida la correspondencia entre cuenta y rol: origen/egreso y destino/ingreso.

`PosicionActivo` representa la cantidad acumulada de un activo a partir de sus movimientos. Una compra incrementa la posición y una venta la disminuye. Actualmente es un concepto de dominio no persistente.

`CalculadorPosicionActivo` construye una `PosicionActivo` desde una colección ordenada de `MovimientoActivo`, sin introducir persistencia ni una nueva entidad.

Enumeraciones principales: `TipoInstitucionFinanciera`, `TipoMoneda`, `TipoCuenta`, `TipoMovimiento` y `TipoMovimientoActivo`.

## Persistencia

Repositorios JPA: `UsuarioRepository`, `PerfilFinancieroRepository`, `InstitucionFinancieraRepository`, `MonedaRepository`, `CuentaRepository`, `MovimientoRepository`, `CategoriaRepository`, `OperacionFinancieraRepository`, `ActivoRepository`, `BonoRepository` y `MovimientoActivoRepository`.

`PosicionActivo` y `CalculadorPosicionActivo` no poseen repositorio ni tabla propia en esta etapa, deliberadamente.

Las entidades nuevas están registradas explícitamente en la unidad de persistencia JPA utilizada por los tests.

## Services

La capa `service` contiene `CuentaService`, `MovimientoService`, `CategoriaService`, `PerfilFinancieroService`, `UsuarioService`, `InstitucionFinancieraService`, `MonedaService`, `OperacionFinancieraService` y `PosicionActivoService`.

Los services coordinan repositorios y reglas de dominio cuando existe un caso de uso que lo requiere; no se crean servicios vacíos únicamente para replicar los repositorios.

## Decisiones de dominio relevantes

Las transferencias no se modelan como un tercer `TipoMovimiento`. Una transferencia genera un **EGRESO** en la cuenta origen y un **INGRESO** en la cuenta destino, vinculados mediante una `OperacionFinanciera`.

Una `OperacionFinanciera` puede contener como máximo dos movimientos y cada `Movimiento` puede estar asociado a una única `OperacionFinanciera`.

Además, el dominio exige que el primer movimiento corresponda a la cuenta origen y sea un `EGRESO`, y que el segundo corresponda a la cuenta destino y sea un `INGRESO`.

`Activo` se mantiene deliberadamente como una entidad mínima. Cantidad, precio, cotización y posición se reservan para los bloques de operaciones y posiciones de inversión.

`Bono` se mantiene deliberadamente como una especialización mínima de `Activo`. No se incorporan todavía valor nominal, tasa, vencimiento, cupón, amortización, emisor u otros atributos financieros porque esas reglas de dominio aún no fueron definidas explícitamente.

`MovimientoActivo` almacena una cantidad positiva y utiliza `COMPRA` o `VENTA` para determinar el signo de la variación de tenencia.

`PosicionActivo` no permite que la cantidad acumulada resulte negativa. Una venta superior a la tenencia se rechaza en el dominio, dejando abierta para una futura decisión explícita la incorporación de posiciones short.

`CalculadorPosicionActivo` es deliberadamente un componente de dominio, no una entidad persistente ni un servicio vacío. `PosicionActivoService` coordina la obtención de movimientos desde persistencia y delega el cálculo al dominio.

## Próximo paso

Revisar el modelo actual de `OperacionFinanciera` y del bloque de inversiones para identificar el siguiente caso de uso necesario. Antes de incorporar precio promedio, valuación, cotización, comisiones, resultado de la inversión u otras reglas financieras, definirlas explícitamente en el dominio.

No realizar todavía merge a `main`. La rama `feature/operacion-financiera` continúa siendo la fuente de continuidad hasta alcanzar un estado funcional estable y validado.

Antes de implementar un nuevo componente, revisar el dominio, repositorios, servicios y tests relacionados y mantener el cambio mínimo necesario.

## Regla de continuidad

Cada bloque importante debe terminar con código funcionando, tests en verde, commit identificable cuando corresponda, actualización de documentación y registro del próximo paso. Código, tests y documentación de continuidad deben permanecer en la misma rama `feature/operacion-financiera`.
