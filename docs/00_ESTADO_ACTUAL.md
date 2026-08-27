# SOFP — Estado actual

> Documento de continuidad del proyecto. Debe actualizarse al cerrar cada bloque importante.

## Identidad del proyecto

**Nombre:** SOFP — Sistema Operativo Financiero Personal  
**Repositorio:** agmilevecich/sofp  
**Rama principal:** `main`  
**Rama única de trabajo y continuidad:** `feature/operacion-financiera`  

La documentación de continuidad se mantiene en `docs/` dentro de la misma rama de trabajo. `main` permanece separada hasta disponer de un estado funcional estable y validado para realizar el merge.

## Estado funcional actual

**Bloque actual — `OperacionFinanciera`: integridad reforzada entre tipo de operación y movimiento de activo, validada mediante tests y persistencia.**

`OperacionFinanciera` agrupa movimientos monetarios y puede identificarse mediante `TipoOperacionFinanciera`: `TRANSFERENCIA`, `COMPRA` o `VENTA`.

La arquitectura actual contempla:

```text
Activo
 └── Bono

OperacionFinanciera
 ├── TipoOperacionFinanciera
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

## Última validación — Build 057

Se reforzó la integridad de `OperacionFinanciera` para que el tipo de movimiento de activo sea coherente con el tipo de operación financiera. Además, se adaptó el test de persistencia para representar explícitamente una compra de activo.

Reglas validadas:

- `COMPRA` admite `MovimientoActivo.COMPRA`.
- `COMPRA` rechaza `MovimientoActivo.VENTA`.
- `VENTA` admite `MovimientoActivo.VENTA`.
- `VENTA` rechaza `MovimientoActivo.COMPRA`.
- `TRANSFERENCIA` rechaza movimientos de activo.
- Una compra persistida puede recuperar su `MovimientoActivo` y su importe correctamente.

Tests específicos:

- `OperacionFinancieraMovimientoActivoIntegridadTest`: **4/4**.
- `OperacionFinancieraRepositoryTest`: **12/12**.
- Total específico verificado en el bloque: **16/16**.

Suite general final:

```text
Tests run: 406
Failures: 0
Errors: 0
Skipped: 0
BUILD SUCCESS
```

- Ejecución final: **26/08/2026 20:38:25 -03:00**.
- Duración: **13:00 min**.

Durante la validación del bloque se detectó una regresión en `OperacionFinancieraRepositoryTest`: un test histórico utilizaba una transferencia como datos base y luego intentaba agregar un `MovimientoActivo.COMPRA`. Se corrigió el test para crear explícitamente una operación `COMPRA`, manteniendo intacto el helper compartido de transferencias.

Validación Git registrada:

- `git status`: working tree limpio antes de las actualizaciones de documentación.
- `git diff --check`: sin errores.
- Rama de trabajo: `feature/operacion-financiera`.
- Documentación actualizada en GitHub y mantenida en la misma rama.

## Git

Commits funcionales más recientes del bloque:

- `9a719c8` — `test: adaptar persistencia de movimiento activo a compra`.
- `993e278` — `fix: validar tipo de movimiento de activo en operacion`.
- `70e4584` — `test: cubrir tipo de operacion financiera`.
- `3025848` — `feat: incorporar tipo a operacion financiera`.
- `21d8ed9` — `feat: agregar tipo de operacion financiera`.

Commits de documentación del Build 057:

- `5c2322d` — `docs: registrar Build 057 y compra de activo`.
- `43cea5d` — `docs: registrar tests del Build 057`.
- El estado actual queda actualizado en este archivo mediante el commit correspondiente generado por GitHub.

## Dominio construido

Entidades principales: `Usuario`, `PerfilFinanciero`, `InstitucionFinanciera`, `Moneda`, `Cuenta`, `Categoria`, `Movimiento`, `OperacionFinanciera`, `Activo`, `Bono` y `MovimientoActivo`.

`Activo` representa la base común para futuros instrumentos financieros y actualmente contiene `nombre` y `moneda` como datos propios del activo.

`Bono` es la primera especialización de `Activo` y actualmente no incorpora atributos financieros adicionales.

`MovimientoActivo` representa el efecto de una operación sobre la tenencia de un `Activo`, con tipo `COMPRA` o `VENTA`, cantidad y precio unitario. La cantidad se almacena como valor positivo; el tipo determina el efecto sobre la tenencia.

`TipoOperacionFinanciera` distingue actualmente `TRANSFERENCIA`, `COMPRA` y `VENTA`.

`OperacionFinanciera` permite agrupar el movimiento monetario y el movimiento específico del activo que forman parte de una misma operación. Para sus movimientos monetarios se valida la correspondencia entre cuenta y rol: origen/egreso y destino/ingreso.

La operación financiera también valida la coherencia entre `TipoOperacionFinanciera` y `TipoMovimientoActivo`: una compra requiere movimiento de activo `COMPRA`, una venta requiere movimiento de activo `VENTA` y una transferencia no admite movimientos de activo.

`PosicionActivo` representa la cantidad acumulada de un activo a partir de sus movimientos. Una compra incrementa la posición y una venta la disminuye. Actualmente es un concepto de dominio no persistente.

`CalculadorPosicionActivo` construye una `PosicionActivo` desde una colección ordenada de `MovimientoActivo`, sin introducir persistencia ni una nueva entidad.

Enumeraciones principales: `TipoInstitucionFinanciera`, `TipoMoneda`, `TipoCuenta`, `TipoMovimiento`, `TipoMovimientoActivo` y `TipoOperacionFinanciera`.

## Persistencia

Repositorios JPA: `UsuarioRepository`, `PerfilFinancieroRepository`, `InstitucionFinancieraRepository`, `MonedaRepository`, `CuentaRepository`, `MovimientoRepository`, `CategoriaRepository`, `OperacionFinancieraRepository`, `ActivoRepository`, `BonoRepository` y `MovimientoActivoRepository`.

`PosicionActivo` y `CalculadorPosicionActivo` no poseen repositorio ni tabla propia en esta etapa, deliberadamente.

Las entidades nuevas están registradas explícitamente en la unidad de persistencia JPA utilizada por los tests.

## Services

La capa `service` contiene `CuentaService`, `MovimientoService`, `CategoriaService`, `PerfilFinancieroService`, `UsuarioService`, `InstitucionFinancieraService`, `MonedaService`, `OperacionFinancieraService` y `PosicionActivoService`.

Los services coordinan repositorios y reglas de dominio cuando existe un caso de uso que lo requiere; no se crean servicios vacíos únicamente para replicar los repositorios.

## Decisiones de dominio relevantes

Las transferencias no se modelan como un tercer `TipoMovimiento`. Una transferencia genera un **EGRESO** en la cuenta origen y un **INGRESO** en la cuenta destino, vinculados mediante una `OperacionFinanciera` de tipo `TRANSFERENCIA`.

Una `OperacionFinanciera` puede contener como máximo dos movimientos y cada `Movimiento` puede estar asociado a una única `OperacionFinanciera`.

Además, el dominio exige que el primer movimiento corresponda a la cuenta origen y sea un `EGRESO`, y que el segundo corresponda a la cuenta destino y sea un `INGRESO`.

`TipoOperacionFinanciera` distingue `TRANSFERENCIA`, `COMPRA` y `VENTA` y permite preparar el dominio para operaciones de inversión.

En una `COMPRA`, la cuenta de origen es obligatoria y el movimiento de activo debe ser `COMPRA`. En una `VENTA`, la cuenta de destino es obligatoria y el movimiento de activo debe ser `VENTA`. Una `TRANSFERENCIA` requiere origen y destino y no admite movimientos de activo.

`Activo` se mantiene deliberadamente como una entidad mínima. Cantidad, precio, cotización y posición se reservan para los bloques de operaciones y posiciones de inversión.

`Bono` se mantiene deliberadamente como una especialización mínima de `Activo`. No se incorporan todavía valor nominal, tasa, vencimiento, cupón, amortización, emisor u otros atributos financieros porque esas reglas de dominio aún no fueron definidas explícitamente.

`MovimientoActivo` almacena una cantidad positiva y utiliza `COMPRA` o `VENTA` para determinar el signo de la variación de tenencia.

`PosicionActivo` no permite que la cantidad acumulada resulte negativa. Una venta superior a la tenencia se rechaza en el dominio, dejando abierta para una futura decisión explícita la incorporación de posiciones short.

`CalculadorPosicionActivo` es deliberadamente un componente de dominio, no una entidad persistente ni un servicio vacío. `PosicionActivoService` coordina la obtención de movimientos desde persistencia y delega el cálculo al dominio.

## Próximo paso

Implementar progresivamente el caso de uso de **compra de activo**, comenzando por sus reglas de dominio y tests. La compra deberá mantener sincronizados el movimiento monetario y el movimiento de activo y validar que el importe sea `cantidad × precioUnitario`.

La venta y su validación contra la posición disponible se abordarán posteriormente. Las comisiones y gastos se dejan para una etapa posterior, una vez estabilizado el núcleo de compra/venta.

No realizar todavía merge a `main`. La rama `feature/operacion-financiera` continúa siendo la fuente de continuidad hasta alcanzar un estado funcional estable y validado.

Antes de implementar un nuevo componente, revisar el dominio, repositorios, servicios y tests relacionados y mantener el cambio mínimo necesario.

## Regla de continuidad

Cada bloque importante debe terminar con código funcionando, tests en verde, commit identificable cuando corresponda, actualización de documentación y registro del próximo paso. Código, tests y documentación de continuidad deben permanecer en la misma rama `feature/operacion-financiera`.
