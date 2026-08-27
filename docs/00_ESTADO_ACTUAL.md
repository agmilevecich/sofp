# SOFP — Estado actual

> Documento de continuidad del proyecto. El código y los tests actuales son la fuente de verdad técnica.

## Identidad del proyecto

**Nombre:** SOFP — Sistema Operativo Financiero Personal  
**Repositorio:** agmilevecich/sofp  
**Rama principal:** `main`

La etapa `feature/operacion-financiera` fue integrada en `main`.

## Estado funcional actual

**Bloque cerrado — operaciones financieras con activos.**

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
- Build 059: **BUILD SUCCESS**; ejecución informada desde IntelliJ el **27/08/2026 15:24:11 -03:00**; duración **17:35 min**.

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

Cierre de la etapa:

- `d39632b` — `Merge branch 'feature/operacion-financiera'`
- `main` local, `github/main` y `bitbucket/main` quedaron alineados en `d39632b`.
- `feature/operacion-financiera` quedó integrada en `main`.

## Decisiones de dominio relevantes

Una `COMPRA` requiere cuenta de origen y genera `EGRESO` + `MovimientoActivo.COMPRA`.

Una `VENTA` requiere cuenta de destino y genera `INGRESO` + `MovimientoActivo.VENTA`.

Una `VENTA` no puede dejar una posición negativa. Una venta superior a la tenencia se rechaza en el dominio. La incorporación de posiciones short queda para una decisión futura explícita.

`MovimientoActivo` exige cantidad y precio unitario positivos.

Las comisiones y gastos se dejan para una etapa posterior, una vez estabilizado el núcleo de compra/venta.

## Próximo paso

Revisar el estado de `main` y definir la siguiente evolución del bloque de inversiones a partir de reglas de negocio explícitas.

Antes de implementar nuevos atributos o comportamientos financieros, revisar el dominio actual, servicios, repositorios y tests relacionados.

Posibles líneas a evaluar, sin considerarlas todavía implementadas:

- definir reglas específicas de los instrumentos financieros;
- determinar la evolución de `Bono`;
- ampliar progresivamente la capa `service` según casos de uso reales;
- incorporar DTOs cuando las fronteras de aplicación lo requieran;
- incorporar interfaz de usuario cuando dominio y casos de uso estén consolidados;
- definir reportes y cálculos derivados de movimientos;
- evaluar nuevas reglas de saldos y consistencia financiera cuando aparezcan casos de uso que las requieran.

## Regla de continuidad

Código y tests son la fuente de verdad técnica. La documentación resume el estado y debe actualizarse al cerrar bloques importantes. No considerar implementado ningún pendiente hasta contar con código verificable y tests correspondientes.
