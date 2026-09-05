# SOFP — Pendientes

## Estado — 05/09/2026

**Rama estable:** `main` → `a4be859`.
**Rama de trabajo:** `feature/swing-shell`.

La rama de trabajo continúa sin merge a `main`.

## Bloques cerrados

### Fondos insuficientes

**Completado y validado.**

`MovimientoService` rechaza `EGRESO` cuando supera el saldo disponible. Un egreso igual al saldo está permitido y deja saldo cero. La regla también contempla modificaciones de importe y tipo.

Pruebas específicas: `MovimientoFondosInsuficientesTest` **6/6**.
Pruebas relacionadas: `MovimientoServiceTest` **57/57** y `RegistrarMovimientoPanelTest` **4/4**.

### Categorías con movimientos

**Completado y validado.**

Una categoría que ya tiene movimientos no se elimina físicamente. Se conserva el historial y se desactiva la categoría. La UI informa la situación de forma amigable.

`CategoriaServiceTest`: **23/23**.

### Gastos — primer corte funcional

**Completado y validado.**

`GastosPanel` permite registrar compras, pagos de servicios y otros egresos básicos mediante `GastoService`, delegando finalmente en `MovimientoService` como `EGRESO`.

El registro queda persistido en el historial común de `Movimientos`, manteniendo una única fuente de verdad financiera.

La suite general posterior a este bloque fue informada por el usuario como **586/586**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`.

## Bloque actual — Integración de FormaPago

**Implementado en código; pendiente de ejecución y validación.**

Se incorporó `FormaPago` al dominio de `Movimiento` como dato persistente y se propagó desde `GastosPanel` → `GastoService` → `MovimientoService`.

`GastosPanel` ahora permite seleccionar:

- efectivo;
- transferencia;
- tarjeta de débito;
- tarjeta de crédito;
- QR.

La tarjeta de crédito está explícitamente rechazada por `GastoService` en este corte, porque todavía no existe el modelo de obligación/pasivo necesario para representar correctamente una compra a crédito sin simular una salida inmediata de fondos de la cuenta.

Se agregó cobertura para:

- selector de forma de pago en `GastosPanel`;
- persistencia de la forma de pago en `Movimiento`;
- cambio de forma de pago en el dominio;
- rechazo de tarjeta de crédito hasta modelar la obligación.

Todavía no se informó ejecución de estos nuevos tests.

## Criterio funcional adoptado a partir de ControlFinanzas

ControlFinanzas se utiliza como banco de ideas y referencia, no como arquitectura para copiar.

El criterio acordado para SOFP es:

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

La experiencia Swing debe permitir registrar el hecho financiero desde un panel especializado y verlo luego en el historial consolidado.

## Próximo paso inmediato

Sincronizar la rama y ejecutar primero los tests afectados por la integración de `FormaPago` y después la suite general.

Comandos sugeridos:

`git syncsofp`

`mvn test`

Con el resultado se determinará si la integración queda cerrada o requiere una corrección.

## Próximos bloques posteriores

1. Cerrar y validar la integración actual de `FormaPago`.
2. Modelar correctamente obligaciones/pasivos para tarjeta de crédito.
3. Evolucionar ingresos y transferencias mediante el mismo núcleo común.
4. Incorporar progresivamente pasivos/obligaciones y patrimonio neto.
5. Llevar a SOFP las capacidades de análisis valiosas de ControlFinanzas: resúmenes mensuales/históricos, distribución por categoría/tipo, evolución patrimonial, vencimientos y dashboard.

## Validación vigente

La última suite general informada antes de los cambios actuales de `FormaPago` fue **586/586 tests en verde**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`, mediante `mvn test`, finalizada el **05/09/2026 12:34:00 -03:00**.

Los cambios actuales todavía no tienen resultado de ejecución informado.

## Integración

No hacer merge a `main` automáticamente. No crear ramas nuevas salvo indicación explícita. Antes de una eventual integración, revisar commits, comparación con `main`, tests, diff, diff-check, status y documentación.
