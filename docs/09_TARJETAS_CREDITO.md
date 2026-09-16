# SOFP — Diseño y adaptación de tarjetas de crédito

## Estado auditado — 16/09/2026

**Rama:** `feature/swing-shell`

La fuente de verdad es el código y los tests actuales.

## 1. Modelo

Una tarjeta de crédito es una `Cuenta` con `TipoCuenta.TARJETA_CREDITO`. La deuda se representa mediante `Obligacion` y el consumo mediante `Movimiento`.

## 2. Moneda y liquidación histórica — IMPLEMENTADO

La moneda del consumo se conserva en `Movimiento` y como `monedaOriginal` de la obligación. La moneda de la tarjeta/cuenta queda como `monedaLiquidacion`.

`TipoCambio` representa una cotización histórica. `Obligacion.liquidar(TipoCambio)` valida las monedas, calcula `importeLiquidacion`, conserva el tipo de cambio y evita una segunda liquidación. `saldoLiquidacion` representa la deuda en moneda de liquidación y se reduce mediante pagos.

No se realiza conversión automática ni se recalcula una liquidación histórica con una cotización posterior.

## 3. Valorización de cierre — IMPLEMENTADO

`Obligacion` conserva `tipoCambioCierre` e `importeValorizacionCierre` como datos históricos independientes de la liquidación.

`Obligacion.valorarCierre(TipoCambio)` exige cambio no nulo, rechaza segunda valoración y monedas iguales, valida origen/destino y no modifica la deuda original, la liquidación ni el estado.

La valorización de cierre no reemplaza a `liquidar(TipoCambio)`.

## 4. Saldos y fondos — IMPLEMENTADO

La cuenta calcula su saldo usando movimientos de su moneda. `MovimientoService` valida fondos usando la moneda económica del movimiento. ARS y USD no se mezclan implícitamente.

## 5. Pago multidivisa — IMPLEMENTADO EN SERVICIO

`PagoTarjetaService` utiliza `saldoLiquidacion` cuando existe y, en obligaciones no liquidadas, `saldoPendiente`. La cuenta pagadora debe utilizar `monedaLiquidacion`. Se cubren pagos parciales y totales.

## 6. Crédito disponible — IMPLEMENTADO PARA OBLIGACIONES VALORIZADAS

El cálculo de crédito utilizado contempla la valorización de cierre:

- obligación en moneda de la tarjeta → `saldoPendiente`;
- obligación multidivisa valorizada → `importeValorizacionCierre` proporcional al saldo original todavía pendiente;
- consumo de tarjeta sin obligación asociada → comportamiento existente.

Ejemplo validado: USD 100 valorizados a ARS 1500 representan ARS 150.000 de crédito. Un pago parcial de USD 40 reduce el crédito utilizado a ARS 90.000; el pago total lo reduce a ARS 0.

Una obligación multidivisa sin valorización de cierre no recibe una conversión implícita. Su comportamiento futuro debe definirse.

## 7. Ciclo de facturación — IMPLEMENTADO Y CON HISTORIAL PERSISTENTE

`CicloFacturacion` es un objeto de dominio no persistente. `Cuenta.calcularCicloFacturacion(LocalDate)` resuelve cierre, ciclo siguiente, meses cortos, vencimiento y cambio de año. El vencimiento efectivo se desplaza al lunes cuando cae sábado o domingo.

## 8. Pagos y reglas temporales — IMPLEMENTADOS

`PagoTarjetaService` coordina autorización, validaciones, egreso real y aplicación del pago en una operación transaccional. Se admiten pagos parciales o totales y se aplican las reglas temporales vigentes.

## 9. Integridad histórica — IMPLEMENTADA

El movimiento que origina una obligación queda protegido frente a cambios estructurales incompatibles y eliminación.

## 10. Cuotas y financiación

Las cuotas simples sin interés están implementadas y se generan automáticamente. La financiación avanzada sigue pendiente: intereses, CFT, cuotas variables, adelantos, refinanciación, anulaciones/reversiones y ajustes.

## 11. Integridad estructural de Cuenta — IMPLEMENTADA

Una cuenta con movimientos no puede cambiar de tipo ni de moneda. La API genérica tampoco permite transiciones hacia o desde `TARJETA_CREDITO`.

## 12. UI específica — PENDIENTE

Pendiente una UI completa para consultar límite/disponible, consumos, valorizaciones de cierre, ciclos, vencimientos, deuda y pagos reales de forma específica para tarjetas. La integración actual de pagos continúa disponible mediante la UI existente.

## 13. Compatibilidad histórica

Los nuevos campos se mantienen nullable cuando corresponde y utilizan fallback para datos existentes.

## 14. Validación actual

- `mvn test`: **740/740**.
- Failures: 0.
- Errors: 0.
- Skipped: 0.
- `BUILD SUCCESS`.
- Finalizada: **16/09/2026 15:54:16 -03:00**.
- Tiempo total: **09:50 min**.

## 15. Orden de trabajo pendiente

1. Definir el flujo de obtención/registro de la valorización de cierre dentro de la aplicación.
2. Definir el comportamiento de consumos extranjeros todavía no valorizados al cierre.
3. Completar persistencia/UI del cierre y pago multidivisa.
4. Financiación avanzada.
5. UI específica de tarjetas.
6. Pasivos/patrimonio y análisis.
7. Gestión de entidades financieras.
8. Pulido de consola.

El calendario de feriados y una fecha efectiva separada requieren decisión de negocio antes de implementarse.
