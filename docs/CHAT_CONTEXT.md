# SOFP — Contexto para continuar con ChatGPT

## Estado actual — 10/09/2026

La fuente de verdad es el código, los tests y los commits actuales. `docs/` es documentación auxiliar y ante contradicción prevalecen código y tests.

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.
**Comparación verificada antes de la actualización documental:** 470 commits adelante, 0 atrás.

Último commit funcional previo a la actualización documental: `51d4afe` — `fix: ajustar test de cuotas al registro automatico`.

## Último bloque cerrado

Cuotas automáticas en gastos con tarjeta.

`GastoService` genera las cuotas solicitadas al registrar una compra con `TARJETA_CREDITO`, dentro de la transacción de la obligación, y las cuotas quedan persistidas.

`PagoTarjetaServiceTest` fue corregido para registrar el gasto directamente con `cantidadCuotas = 3`, en lugar de generar manualmente cuotas sobre una obligación que ya las tenía. Esto eliminó el error `La obligación ya tiene cuotas generadas`.

## Ciclos de facturación — YA IMPLEMENTADOS

`CicloFacturacion` es un objeto de dominio no persistente. `Cuenta.calcularCicloFacturacion(LocalDate)` calcula inicio, cierre y vencimiento.

`CicloFacturacionTest` cubre 9 escenarios. Los ciclos no deben plantearse como implementación desde cero; queda pendiente su integración con consumos/obligaciones/pagos.

## Arquitectura

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

Gastos → `GastoService` → `MovimientoService` → `Movimiento EGRESO`.

Ingresos → `IngresoService` → `MovimientoService` → `Movimiento INGRESO`.

Transferencias → `OperacionFinancieraService` → `OperacionFinanciera` con EGRESO/INGRESO.

## Reglas vigentes

- egreso superior al saldo: rechazado;
- egreso igual al saldo: permitido;
- modificaciones respetan fondos disponibles;
- categorías con movimientos se conservan y desactivan;
- cuenta y forma de pago son conceptos distintos;
- tarjeta de crédito genera movimiento y obligación;
- las cuotas solicitadas se generan automáticamente al registrar el gasto;
- obligación conserva moneda del movimiento de origen;
- crédito disponible inicial se calcula por moneda, sin conversión implícita;
- transferencias propias no son ingresos ni gastos;
- UI no duplica reglas de negocio.

## Tests

Suite general más reciente conocida: `mvn test` → **671/671**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, ejecutada el **10/09/2026 10:39:38 -03:00**.

Suite relacionada más reciente: `GastosPanelTest,GastoServiceTest,ObligacionTest,ObligacionCuotasTest,PagoTarjetaServiceTest` → **32/32**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, ejecutada el **10/09/2026 13:14:29 -03:00**, duración 01:19 min.

Focalizados: `PagoTarjetaServiceTest` 4/4 y `GastosPanelTest` 6/6.

## Próximo paso

1. Revisar `GastosPanelTest` y las convenciones actuales de cuentas/paneles.
2. Implementar selección explícita de tarjeta de crédito en `GastosPanel` cuando la forma de pago sea `TARJETA_CREDITO`.
3. Cubrir que solo aparezcan tarjetas activas y que la tarjeta seleccionada sea la cuenta usada por `GastoService`.
4. Integrar `CicloFacturacion` con consumos y obligaciones.
5. Unificar el cálculo de saldo de tarjetas entre `MovimientoService` y `CuentaService`.
6. Profundizar pagos/liberación de crédito, pasivos/patrimonio, análisis y dashboard.

## Protocolo de nuevas sesiones

Revisar siempre: rama → últimos commits → comparación con `main` → README/docs → código → tests → último resultado → próximo paso.

No modificar `main`, no crear ramas nuevas salvo indicación explícita y no asumir resultados locales no informados.
