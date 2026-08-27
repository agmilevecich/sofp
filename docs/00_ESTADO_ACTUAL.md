# SOFP — Estado actual

> Documento de continuidad del proyecto. Debe actualizarse al cerrar cada bloque importante.

## Identidad del proyecto

**Nombre:** SOFP — Sistema Operativo Financiero Personal  
**Repositorio:** agmilevecich/sofp  
**Rama principal:** `main`  
**Rama de trabajo y continuidad:** `feature/operacion-financiera`  

La documentación de continuidad se mantiene en `docs/` dentro de la misma rama de trabajo. `main` permanece separada hasta disponer de un estado funcional estable y validado para realizar el merge.

## Estado funcional actual

**Bloque actual — compra de activo: implementado y validado con persistencia.**

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

## Última validación — Build 058

Se implementó y validó el caso de uso de compra de activo mediante `OperacionFinancieraService.comprarActivo(...)`.

La compra mantiene sincronizados el movimiento monetario de egreso y el `MovimientoActivo.COMPRA`, y valida el importe como `cantidad × precioUnitario`.

Tests específicos:

- `OperacionFinancieraCompraServiceTest`: **13/13 tests en verde**.

Suite general final:

```text
Tests run: 419
Failures: 0
Errors: 0
Skipped: 0
BUILD SUCCESS
```

- Ejecución final: **27/08/2026 12:45:13 -03:00**.
- Duración: **15:09 min**.
- Tests específicos de compra: **13/13**.
- Suite general: **419/419**.

Durante la validación apareció una diferencia de escala de `BigDecimal` al recuperar valores desde H2 (`100` frente a `100.00000000` y valores equivalentes). Se corrigieron únicamente las comparaciones del test mediante `compareTo()`, sin modificar la lógica de producción.

## Validación Git

La rama de trabajo quedó validada previamente con:

```text
git status
nothing to commit, working tree clean

git diff --check
sin errores
```

La documentación de continuidad se actualiza ahora en GitHub sobre `feature/operacion-financiera`.

## Git

Commits funcionales recientes relacionados con el bloque:

- `993e278` — `fix: validar tipo de movimiento de activo en operacion`
- `9a719c8` — `test: adaptar persistencia de movimiento activo a compra`
- `135df51` — `fix: adaptar operacion financiera a cuentas segun tipo`
- `ccf058c` — `test: cubrir cuentas segun tipo de operacion financiera`

El test de compra de activo fue posteriormente ajustado para comparar `BigDecimal` sin depender de la escala de persistencia.

## Dominio construido

Entidades principales: `Usuario`, `PerfilFinanciero`, `InstitucionFinanciera`, `Moneda`, `Cuenta`, `Categoria`, `Movimiento`, `OperacionFinanciera`, `Activo`, `Bono` y `MovimientoActivo`.

`Activo` representa la base común para futuros instrumentos financieros y actualmente contiene `nombre` y `moneda`.

`Bono` es la primera especialización de `Activo` y se mantiene deliberadamente mínima.

`MovimientoActivo` representa el efecto de una operación sobre la tenencia de un `Activo`, con tipo `COMPRA` o `VENTA`, cantidad positiva y precio unitario.

`TipoOperacionFinanciera` distingue `TRANSFERENCIA`, `COMPRA` y `VENTA`.

`OperacionFinanciera` agrupa el movimiento monetario y el movimiento específico del activo que forman parte de una misma operación. Para los movimientos monetarios valida la correspondencia entre cuenta y rol: origen/egreso y destino/ingreso.

La operación también valida la coherencia entre `TipoOperacionFinanciera` y `TipoMovimientoActivo`: una compra requiere movimiento `COMPRA`, una venta requiere movimiento `VENTA` y una transferencia no admite movimientos de activo.

`PosicionActivo` representa la cantidad acumulada de un activo a partir de sus movimientos. Una compra incrementa la posición y una venta la disminuye. Actualmente es un concepto de dominio no persistente.

`CalculadorPosicionActivo` construye una `PosicionActivo` desde movimientos ordenados, sin introducir persistencia.

## Persistencia

Repositorios JPA: `UsuarioRepository`, `PerfilFinancieroRepository`, `InstitucionFinancieraRepository`, `MonedaRepository`, `CuentaRepository`, `MovimientoRepository`, `CategoriaRepository`, `OperacionFinancieraRepository`, `ActivoRepository`, `BonoRepository` y `MovimientoActivoRepository`.

`PosicionActivo` y `CalculadorPosicionActivo` no poseen repositorio ni tabla propia en esta etapa.

## Services

La capa `service` contiene `CuentaService`, `MovimientoService`, `CategoriaService`, `PerfilFinancieroService`, `UsuarioService`, `InstitucionFinancieraService`, `MonedaService`, `OperacionFinancieraService` y `PosicionActivoService`.

`OperacionFinancieraService` incorpora el caso de uso de compra de activo, coordinando la creación y persistencia de la operación, movimiento monetario y movimiento de activo.

## Decisiones de dominio relevantes

Las transferencias generan un **EGRESO** en la cuenta origen y un **INGRESO** en la cuenta destino, vinculados mediante una `OperacionFinanciera` de tipo `TRANSFERENCIA`.

Una `OperacionFinanciera` puede contener como máximo dos movimientos y cada `Movimiento` puede estar asociado a una única operación.

En una `COMPRA`, la cuenta de origen es obligatoria y el movimiento de activo debe ser `COMPRA`. En una `VENTA`, la cuenta de destino es obligatoria y el movimiento de activo debe ser `VENTA`. Una `TRANSFERENCIA` requiere origen y destino y no admite movimientos de activo.

La compra de activo exige cantidad y precio unitario positivos y mantiene el importe monetario sincronizado con `cantidad × precioUnitario`.

`Activo` y `Bono` se mantienen deliberadamente mínimos hasta definir reglas financieras adicionales.

`MovimientoActivo` almacena una cantidad positiva y utiliza `COMPRA` o `VENTA` para determinar el efecto sobre la tenencia.

`PosicionActivo` no permite una cantidad acumulada negativa. Una venta superior a la tenencia se rechaza en el dominio; la incorporación de posiciones short queda para una decisión futura explícita.

`PosicionActivoService` coordina la obtención de movimientos desde persistencia y delega el cálculo al dominio.

## Próximo paso

Implementar progresivamente el caso de uso de **venta de activo**, aprovechando la estructura ya implementada para compra.

La venta deberá:

- requerir cuenta de destino;
- generar un movimiento monetario `INGRESO`;
- generar un `MovimientoActivo.VENTA`;
- calcular el importe como `cantidad × precioUnitario`;
- validar la coherencia con `TipoOperacionFinanciera.VENTA`;
- posteriormente validar la posición disponible para impedir ventas superiores a la tenencia.

Las comisiones y gastos se dejan para una etapa posterior, una vez estabilizado el núcleo de compra/venta.

No realizar todavía merge a `main`. Primero completar y validar el bloque de venta y los siguientes controles de integridad y persistencia.

## Regla de continuidad

Cada bloque importante debe terminar con código funcionando, tests en verde, commit identificable cuando corresponda, actualización de documentación y registro del próximo paso. Código, tests y documentación de continuidad deben permanecer en `feature/operacion-financiera` hasta decidir el merge a `main`.
