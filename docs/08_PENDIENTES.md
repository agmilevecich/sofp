# SOFP — Pendientes

## Estado — 04/09/2026

**Rama estable:** `main` → `a4be859`.
**Rama de trabajo:** `feature/swing-shell` → `2b2bf3e` (`test: adaptar cambio de tipo a fondos disponibles`).

La rama de trabajo continúa divergida respecto de `main`. Los commits exclusivos de `main` conocidos son documentales y no se incorporan automáticamente.

## Criterio funcional adoptado a partir de ControlFinanzas

ControlFinanzas se utiliza como banco de ideas y referencia, no como arquitectura para copiar.

El criterio acordado para SOFP es:

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

Los futuros paneles podrán especializarse en gastos, ingresos, transferencias, inversiones, préstamos/deudas, pagos de tarjeta, historial y dashboard, pero todos deben alimentar el mismo núcleo financiero.

También se distingue **Cuenta** de **Forma/Medio de pago**. Se prevén: tarjeta de crédito, tarjeta de débito, QR, transferencia y efectivo. La tarjeta de crédito deberá poder representar una obligación/pasivo sin exigir una salida inmediata de fondos de la cuenta bancaria.

Objetivo de patrimonio: `TOTAL ACTIVOS - TOTAL PASIVOS = PATRIMONIO NETO`.

Un préstamo otorgado debe disminuir liquidez pero permanecer como derecho de cobro. Una transferencia entre cuentas propias no debe computarse como ingreso ni gasto.

Estos son **criterios de diseño pendientes de implementación**, no funcionalidades que deban darse por terminadas.

## Bloques cerrados

### Fondos insuficientes

**Completado y validado.**

`MovimientoService` rechaza `EGRESO` cuando supera el saldo disponible. Un egreso igual al saldo está permitido y deja saldo cero. La validación también contempla modificaciones de importe y tipo, excluyendo el movimiento actual del cálculo cuando corresponde.

Pruebas específicas: `MovimientoFondosInsuficientesTest` **6/6**.

Pruebas relacionadas: `MovimientoServiceTest` **57/57** y `RegistrarMovimientoPanelTest` **4/4**.

Suite general: `mvn clean test` — **577/577**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`, duración **11:16 min**, finalizada el **04/09/2026 18:54:20 -03:00**.

Último commit del bloque: `2b2bf3e` — `test: adaptar cambio de tipo a fondos disponibles`.

## Reglas de negocio pendientes

### Categorías con movimientos

Una categoría que ya tenga movimientos no debe eliminarse físicamente. Debe conservarse el historial y, según el diseño definitivo, impedir su eliminación y permitir su desactivación.

La UI debe mostrar un mensaje comprensible en lugar de propagar una excepción de integridad referencial de Hibernate.

## Próximos bloques funcionales candidatos

1. Resolver correctamente la eliminación/desactivación de categorías con movimientos asociados.
2. Definir e incorporar `FormaPago` sin confundirla con `Cuenta`.
3. Evolucionar movimientos especializados para gastos, ingresos y transferencias manteniendo el núcleo común.
4. Incorporar progresivamente pasivos/obligaciones y patrimonio neto.
5. Llevar a SOFP, adaptadas a su arquitectura, las capacidades de análisis que resultaron valiosas en ControlFinanzas: resúmenes mensuales/históricos, distribución por categoría/tipo, evolución patrimonial, vencimientos y dashboard.

El orden definitivo debe decidirse después de revisar el código y tests actuales del bloque elegido.

## Validación vigente

Suite general más reciente: **577/577 tests en verde**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`.

No asumir ejecuciones posteriores sin resultado informado por el usuario.

Antes de cerrar cada bloque: tests específicos → tests relacionados → suite general cuando corresponda → `git diff` → `git diff --check` → `git status`.

## Integración

No hacer merge a `main` automáticamente. No crear ramas nuevas. Antes de una eventual integración, revisar commits, comparación con `main`, tests, diff, diff-check, status y documentación.
