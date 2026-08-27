# SOFP — Estado actual

> Documento de continuidad del proyecto. Debe actualizarse al cerrar cada bloque importante.

## Identidad del proyecto

**Nombre:** SOFP — Sistema Operativo Financiero Personal  
**Repositorio:** agmilevecich/sofp  
**Rama principal:** `main`  
**Rama de trabajo y continuidad:** `feature/operacion-financiera`

`main` permanece separada hasta disponer de un estado funcional estable y validado para realizar el merge.

## Estado funcional actual

**Bloque actual — compra y venta de activos implementadas y validadas, incluyendo posición e integración persistida.**

`OperacionFinanciera` distingue `TRANSFERENCIA`, `COMPRA` y `VENTA` y agrupa movimientos monetarios y movimientos de activos.

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

## Última validación

La compra y la venta de activos están implementadas mediante `OperacionFinancieraService`.

Compra:
- cuenta de origen;
- movimiento monetario `EGRESO`;
- `MovimientoActivo.COMPRA`;
- importe `cantidad × precioUnitario`.

Venta:
- cuenta de destino;
- movimiento monetario `INGRESO`;
- `MovimientoActivo.VENTA`;
- importe `cantidad × precioUnitario`.

La venta fue corregida para cumplir la regla de dominio de que su primer movimiento monetario debe ser un `INGRESO`.

Validaciones específicas:

- `OperacionFinancieraTest` + `OperacionFinancieraServiceTest` + `OperacionFinancieraCompraServiceTest` + `OperacionFinancieraVentaServiceTest`: **65/65 tests en verde**.
- `PosicionActivoServiceTest`: **4/4 tests en verde**.
- Suite general posterior a los cambios: **433/433 tests en verde**.

## Posición de activos

`MovimientoActivo` representa `COMPRA` como variación positiva y `VENTA` como variación negativa.

`PosicionActivo` acumula los movimientos y rechaza una posición negativa.

`CalculadorPosicionActivo` procesa movimientos ordenados.

`MovimientoActivoRepository.listarPorActivo()` garantiza orden determinista mediante `ORDER BY m.id`.

`PosicionActivoService` obtiene los movimientos persistidos y delega el cálculo al dominio.

La integración actual comprueba:

```text
COMPRA 100
VENTA 30
      ↓
POSICION 70
```

Los movimientos usados en esta comprobación son creados por `OperacionFinancieraService`, no directamente por el test.

## Persistencia

Repositorios JPA relevantes:

- `OperacionFinancieraRepository`
- `MovimientoActivoRepository`
- `ActivoRepository`
- `BonoRepository`

`OperacionFinanciera` mantiene relaciones persistidas con `Movimiento` y `MovimientoActivo`.

La prueba de venta verifica que, después de recuperar la operación desde la base de datos, ambos movimientos continúan asociados a la misma `OperacionFinanciera` y conservan cuenta, activo, tipo, cantidad y precio.

`PosicionActivo` y `CalculadorPosicionActivo` no poseen persistencia propia.

## Git

Últimos commits funcionales de esta etapa:

- `f2dc770` — `fix: validar movimiento monetario de venta`
- `26c4bca` — `test: verificar relaciones persistidas de venta`
- `6a2fd5f` — `test: integrar compra y venta con posicion de activo`

Últimos commits de documentación:

- `da0f4ed` — registrar Build 059 y suite completa;
- `77a6698` — cerrar validación de suite y actualizar pendientes;
- `ed3632e` — actualización previa de continuidad.

`main` permanece sin modificar.

## Decisiones de dominio relevantes

Una `COMPRA` requiere cuenta de origen y genera `EGRESO` + `MovimientoActivo.COMPRA`.

Una `VENTA` requiere cuenta de destino y genera `INGRESO` + `MovimientoActivo.VENTA`.

Una `VENTA` no puede dejar una posición negativa. Una venta superior a la tenencia se rechaza en el dominio. La incorporación de posiciones short queda para una decisión futura explícita.

`MovimientoActivo` exige cantidad y precio unitario positivos.

Las comisiones y gastos se dejan para una etapa posterior, una vez estabilizado el núcleo de compra/venta.

## Próximo paso

Realizar la revisión final de `feature/operacion-financiera` contra `main`:

1. revisar commits y archivos modificados;
2. revisar `git diff`;
3. revisar `git diff --check`;
4. revisar `git status` en el entorno local;
5. verificar que la documentación sea coherente;
6. determinar si la feature está lista para preparar el merge a `main`.

No realizar todavía el merge a `main`.

## Regla de continuidad

Código, tests y documentación de continuidad deben permanecer en `feature/operacion-financiera` hasta decidir el merge. La fuente de verdad técnica es el código y los tests actuales; la documentación es un resumen auxiliar.