# SOFP — Diseño y adaptación de tarjetas de crédito

## Estado auditado — 15/09/2026

**Rama:** `feature/swing-shell`

La fuente de verdad es el código y los tests actuales.

## 1. Modelo

Una tarjeta de crédito es una `Cuenta` con `TipoCuenta.TARJETA_CREDITO`. La deuda se representa mediante `Obligacion` y el consumo mediante `Movimiento`.

## 2. Moneda y liquidación histórica — IMPLEMENTADO

La moneda del consumo se conserva en `Movimiento` y como `monedaOriginal` de la obligación. La moneda de la tarjeta/cuenta queda como `monedaLiquidacion`.

`TipoCambio` representa una cotización histórica con moneda origen, moneda destino, cotización, fecha/hora y fuente. `Obligacion.liquidar(TipoCambio)` valida las monedas, calcula `importeLiquidacion`, conserva el tipo de cambio y evita una segunda liquidación.

`Obligacion` conserva además `saldoLiquidacion`, que comienza con el importe liquidado y se reduce mediante pagos.

No se realiza conversión automática ni se recalcula una liquidación histórica con una cotización posterior.

## 3. Saldos y fondos — IMPLEMENTADO

La cuenta calcula su saldo usando movimientos de su moneda. `MovimientoService` valida fondos usando la moneda económica del movimiento. ARS y USD no se mezclan implícitamente.

## 4. Pago multidivisa — IMPLEMENTADO EN SERVICIO

`PagoTarjetaService` distingue dos casos:

- obligación no liquidada: utiliza `saldoPendiente` y `registrarPago`;
- obligación liquidada: utiliza `saldoLiquidacion` y `registrarPagoLiquidacion`.

En el segundo caso, la cuenta pagadora debe utilizar `monedaLiquidacion`.

Se cubren pagos parciales y totales y se mantiene separado el saldo original del saldo efectivamente liquidado.

## 5. Crédito disponible — PENDIENTE

El criterio definitivo para el límite/crédito disponible cuando el consumo está en una moneda distinta de la tarjeta todavía debe definirse. No se debe introducir una conversión implícita para resolverlo.

## 6. Ciclo de facturación — IMPLEMENTADO Y CON HISTORIAL PERSISTENTE

`CicloFacturacion` es un objeto de dominio no persistente. `Cuenta.calcularCicloFacturacion(LocalDate)` resuelve cierre, ciclo siguiente, meses cortos, vencimiento y cambio de año. El vencimiento efectivo se desplaza al lunes cuando cae sábado o domingo.

La obligación conserva los datos históricos del ciclo y las cuotas conservan sus fechas.

## 7. Pagos y reglas temporales — IMPLEMENTADOS

`PagoTarjetaService` coordina autorización, validaciones, egreso real y aplicación del pago en una única operación transaccional.

Se admiten pagos parciales o totales. Se rechazan pagos anteriores al consumo y fechas futuras. Se aplican días de gracia y mora según las reglas vigentes.

## 8. Integridad histórica — IMPLEMENTADA

El movimiento que origina una obligación queda protegido frente a cambios estructurales incompatibles y eliminación.

## 9. Cuotas y financiación

Las cuotas simples sin interés están implementadas y se generan automáticamente. La financiación avanzada sigue pendiente: intereses, CFT, cuotas variables, adelantos, refinanciación, anulaciones/reversiones y ajustes.

## 10. Integridad estructural de Cuenta — IMPLEMENTADA

Una cuenta con movimientos no puede cambiar de tipo ni de moneda. La API genérica tampoco permite transiciones hacia o desde `TARJETA_CREDITO`.

## 11. UI específica — PENDIENTE

Pendiente una UI completa para consultar límite/disponible, consumos, ciclos, cierres, vencimientos, deuda y pagos reales de forma específica para tarjetas. La integración actual de pagos continúa disponible mediante la UI existente.

## 12. Compatibilidad histórica

Los nuevos campos se mantienen nullable cuando corresponde y utilizan fallback para datos existentes.

## 13. Validación actual

- `mvn test`: **718/718**.
- Failures: 0.
- Errors: 0.
- Skipped: 0.
- `BUILD SUCCESS`.
- Finalizada: **15/09/2026 20:05:19 -03:00**.

El resultado incluye la integración multidivisa de `PagoTarjetaService`.

## 14. Orden de trabajo pendiente

1. Revisar diff/diff-check/status del estado documental.
2. Definir impacto de moneda extranjera sobre límite/crédito disponible.
3. Diseñar y cubrir tests de esa regla.
4. Completar persistencia/UI del pago multidivisa.
5. Financiación avanzada.
6. UI específica de tarjetas.
7. Pasivos/patrimonio y análisis.
8. Gestión de entidades financieras.
9. Pulido de consola.

El calendario de feriados y una fecha efectiva separada requieren decisión de negocio antes de implementarse.
