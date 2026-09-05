# SOFP — Pendientes

## Estado — 05/09/2026

**Rama estable:** `main` → `a4be859`.
**Rama de trabajo:** `feature/swing-shell`.
**Último commit funcional:** `98dead73` — `test: preparar saldo para registro de gasto`.

La rama de trabajo continúa divergida respecto de `main`: **260 commits por delante y 2 por detrás**. No se realizó merge.

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

### Gastos — primer corte funcional

**Completado y validado.**

`GastosPanel` permite registrar compras, pagos de servicios y otros egresos básicos mediante `GastoService`, delegando finalmente en `MovimientoService` como `EGRESO`.

El registro queda persistido en el historial común de `Movimientos`, manteniendo una única fuente de verdad financiera.

La regla de fondos insuficientes se conserva: Gastos no bypassa las reglas del núcleo financiero.

Cobertura específica en `GastosPanelTest` y suite general posterior: **586/586**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`.

El fixture que registra un gasto fue ajustado en `98dead73` para disponer previamente de un ingreso de $1.000 antes de registrar un egreso de $100. El cambio fue exclusivamente de test.

## FormaPago

`FormaPago` está definida con efectivo, transferencia, tarjeta de débito, tarjeta de crédito y QR.

La integración de `FormaPago` con `Movimiento` queda pendiente y deberá hacerse dentro del flujo funcional de Gastos, no como una pieza aislada.

## Criterio funcional adoptado a partir de ControlFinanzas

ControlFinanzas se utiliza como banco de ideas y referencia, no como arquitectura para copiar.

El criterio acordado para SOFP es:

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

La experiencia Swing debe permitir registrar el hecho financiero desde un panel especializado y verlo luego en el historial consolidado.

## Próximo bloque funcional

El primer corte de Gastos ya está cerrado. El próximo paso funcional es integrar `FormaPago` dentro del flujo de Gastos cuando el modelo financiero lo requiera.

Después se podrá abordar correctamente el tratamiento de tarjetas de crédito, obligaciones/pasivos y patrimonio neto.

## Próximos bloques posteriores

1. Integrar `FormaPago` al flujo de Gastos y al modelo financiero cuando corresponda.
2. Diferenciar correctamente compras con tarjeta de crédito y obligaciones/pasivos.
3. Evolucionar ingresos y transferencias mediante el mismo núcleo común.
4. Incorporar progresivamente pasivos/obligaciones y patrimonio neto.
5. Llevar a SOFP las capacidades de análisis valiosas de ControlFinanzas: resúmenes mensuales/históricos, distribución por categoría/tipo, evolución patrimonial, vencimientos y dashboard.

## Validación vigente

Suite general más reciente informada: **586/586 tests en verde**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`.

Comando: `mvn test`.
Duración: **10:43 min**.
Finalización: **05/09/2026 12:34:00 -03:00**.

## Integración

No hacer merge a `main` automáticamente. No crear ramas nuevas salvo indicación explícita. Antes de una eventual integración, revisar commits, comparación con `main`, tests, diff, diff-check, status y documentación.
