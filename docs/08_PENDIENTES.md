# SOFP — Pendientes

## Estado — 05/09/2026

**Rama estable:** `main` → `a4be859`.
**Rama de trabajo:** `feature/swing-shell` → `4ae0a27`.
**Último commit de la rama:** `4ae0a27` — `test: cubrir formas de pago`.
**Último cambio funcional/test previo:** `85b767c` — `test: aislar persistencia en CategoriaServiceTest`.

La rama de trabajo continúa divergida respecto de `main`. No se realizó merge.

## Bloques cerrados

### Fondos insuficientes

**Completado y validado.**

`MovimientoService` rechaza `EGRESO` cuando supera el saldo disponible. Un egreso igual al saldo está permitido y deja saldo cero. La validación también contempla modificaciones de importe y tipo, excluyendo el movimiento actual del cálculo cuando corresponde.

Pruebas específicas: `MovimientoFondosInsuficientesTest` **6/6**.
Pruebas relacionadas: `MovimientoServiceTest` **57/57** y `RegistrarMovimientoPanelTest` **4/4**.

### Categorías con movimientos

**Completado y validado.**

Una categoría que ya tiene movimientos no se elimina físicamente. Se conserva el historial y se desactiva la categoría. La UI informa la situación de forma amigable.

`CategoriaServiceTest`: **23/23**.

Se corrigió además el aislamiento del contexto de persistencia en el `setUp()` de `CategoriaServiceTest`, cerrando `JpaTestManager` antes de crear el `EntityManager`.

Commit: `85b767c`.

## FormaPago

`FormaPago` fue definida mediante `927c66c` y su test mediante `4ae0a27`.

El test `FormaPagoTest` **todavía no fue ejecutado o, al menos, no existe un resultado informado**. No debe considerarse validado.

La integración de `FormaPago` con `Movimiento` queda pendiente y deberá hacerse dentro del flujo funcional, no como una pieza aislada que desvíe el objetivo del Swing.

## Criterio funcional adoptado a partir de ControlFinanzas

ControlFinanzas se utiliza como banco de ideas y referencia, no como arquitectura para copiar.

El criterio acordado para SOFP es:

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

La experiencia Swing buscada se aproxima funcionalmente a ControlFinanzas: el usuario debe poder registrar el hecho financiero desde un panel especializado y verlo luego en el historial consolidado.

## Próximo bloque funcional — Gastos

**Prioridad actual: primer corte de `GastosPanel`.**

El panel Gastos deberá permitir registrar inicialmente compras, pagos de servicios y otros egresos.

El flujo conceptual es:

**Gastos → servicio específico → `Movimiento` de tipo `EGRESO` → `Movimientos` como historial consolidado.**

`Movimientos` debe continuar siendo la historia financiera común y consolidada. No debe existir una segunda fuente de verdad financiera para los gastos.

El primer corte no necesita resolver todavía tarjetas de crédito, pasivos ni todas las variantes de `FormaPago`. Debe establecer correctamente el flujo básico de un gasto hasta `Movimiento` y su reflejo en `Movimientos`.

## Próximos bloques posteriores

1. Integrar `FormaPago` al flujo de Gastos y al modelo financiero cuando corresponda.
2. Diferenciar correctamente compras con tarjeta de crédito y obligaciones/pasivos.
3. Evolucionar ingresos y transferencias mediante el mismo núcleo común.
4. Incorporar progresivamente pasivos/obligaciones y patrimonio neto.
5. Llevar a SOFP las capacidades de análisis valiosas de ControlFinanzas: resúmenes mensuales/históricos, distribución por categoría/tipo, evolución patrimonial, vencimientos y dashboard.

## Validación vigente

Suite general más reciente informada: **580/580 tests en verde**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`.

Comando: `mvn clean test`.
Duración: **10:58 min**.
Finalización: **05/09/2026 09:49:58 -03:00**.

No asumir ejecuciones posteriores sin resultado informado.

## Integración

No hacer merge a `main` automáticamente. No crear ramas nuevas salvo indicación explícita. Antes de una eventual integración, revisar commits, comparación con `main`, tests, diff, diff-check, status y documentación.
