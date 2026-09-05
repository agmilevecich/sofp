# SOFP — Pendientes

## Estado — 05/09/2026

**Rama estable:** `main` → `a4be859`.
**Rama de trabajo:** `feature/swing-shell` → `ae978289`.
**Último cambio funcional/test:** `85b767c` — `test: aislar persistencia en CategoriaServiceTest`.

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

Se corrigió además el aislamiento del contexto de persistencia en el `setUp()` de `CategoriaServiceTest`, cerrando `JpaTestManager` antes de crear el `EntityManager`. Esto eliminó el conflicto de unicidad de `ARS` observado en la suite general.

Commit: `85b767c`.

## Criterio funcional adoptado a partir de ControlFinanzas

ControlFinanzas se utiliza como banco de ideas y referencia, no como arquitectura para copiar.

El criterio acordado para SOFP es:

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

Los futuros paneles podrán especializarse en gastos, ingresos, transferencias, inversiones, préstamos/deudas, pagos de tarjeta, historial y dashboard, pero todos deben alimentar el mismo núcleo financiero.

También se distingue **Cuenta** de **Forma/Medio de pago**. Se prevén tarjeta de crédito, tarjeta de débito, QR, transferencia y efectivo. La tarjeta de crédito deberá poder representar una obligación/pasivo sin exigir una salida inmediata de fondos de la cuenta bancaria.

Objetivo patrimonial: `TOTAL ACTIVOS - TOTAL PASIVOS = PATRIMONIO NETO`.

Un préstamo otorgado debe disminuir liquidez pero permanecer como derecho de cobro. Una transferencia entre cuentas propias no debe computarse como ingreso ni gasto.

Estos son criterios de diseño/roadmap, no funcionalidades que deban darse por terminadas.

## Próximos bloques funcionales candidatos

1. Definir e incorporar `FormaPago` sin confundirla con `Cuenta`.
2. Evolucionar movimientos especializados para gastos, ingresos y transferencias manteniendo el núcleo común.
3. Incorporar progresivamente pasivos/obligaciones y patrimonio neto.
4. Llevar a SOFP las capacidades de análisis que resultaron valiosas en ControlFinanzas: resúmenes mensuales/históricos, distribución por categoría/tipo, evolución patrimonial, vencimientos y dashboard.

El orden definitivo debe decidirse después de revisar el código y tests actuales del bloque elegido.

## Validación vigente

Suite general más reciente: **580/580 tests en verde**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`.

Comando: `mvn clean test`.
Duración: **10:58 min**.
Finalización: **05/09/2026 09:49:58 -03:00**.

No asumir ejecuciones posteriores sin resultado informado.

## Integración

No hacer merge a `main` automáticamente. No crear ramas nuevas salvo indicación explícita. Antes de una eventual integración, revisar commits, comparación con `main`, tests, diff, diff-check, status y documentación.
