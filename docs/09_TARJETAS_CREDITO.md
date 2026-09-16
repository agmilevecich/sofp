# SOFP — Diseño y adaptación de tarjetas de crédito

## Estado auditado — 16/09/2026

**Rama:** `feature/swing-shell`

La fuente de verdad es el código y los tests actuales.

## 1. Modelo

Una tarjeta de crédito es una `Cuenta` con `TipoCuenta.TARJETA_CREDITO`. La deuda se representa mediante `Obligacion` y el consumo mediante `Movimiento`.

## 2. Moneda y liquidación histórica — IMPLEMENTADO

La moneda del consumo se conserva en `Movimiento` y como `monedaOriginal` de la obligación. La moneda de la tarjeta/cuenta queda como `monedaLiquidacion`.

`TipoCambio` representa una cotización histórica con moneda origen, moneda destino, cotización, fecha/hora y fuente. `Obligacion.liquidar(TipoCambio)` valida las monedas, calcula `importeLiquidacion`, conserva el tipo de cambio y evita una segunda liquidación.

`Obligacion` conserva además `saldoLiquidacion`, que comienza con el importe liquidado y se reduce mediante pagos.

No se realiza conversión automática ni se recalcula una liquidación histórica con una cotización posterior.

## 3. Valorización de cierre — IMPLEMENTADO

`Obligacion` conserva `tipoCambioCierre` e `importeValorizacionCierre` como datos históricos independientes de la liquidación.

`Obligacion.valorarCierre(TipoCambio)`:

- exige tipo de cambio no nulo;
- rechaza una segunda valorización;
- rechaza la operación cuando las monedas original y de liquidación son iguales;
- valida que el origen del tipo de cambio sea la moneda original;
- valida que el destino sea la moneda de liquidación;
- conserva la cotización histórica;
- calcula la valorización sin modificar `importeOriginal`, `saldoPendiente`, `importeLiquidacion`, `saldoLiquidacion` ni el estado.

La valorización de cierre no reemplaza a `liquidar(TipoCambio)`. La primera expresa históricamente el consumo en la moneda de la tarjeta, mientras que la segunda establece una liquidación explícita.

## 4. Saldos y fondos — IMPLEMENTADO

La cuenta calcula su saldo usando movimientos de su moneda. `MovimientoService` valida fondos usando la moneda económica del movimiento. ARS y USD no se mezclan implícitamente.

## 5. Pago multidivisa — IMPLEMENTADO EN SERVICIO

`PagoTarjetaService` distingue dos casos:

- obligación no liquidada: utiliza `saldoPendiente` y `registrarPago`;
- obligación liquidada: utiliza `saldoLiquidacion` y `registrarPagoLiquidacion`.

En el segundo caso, la cuenta pagadora debe utilizar `monedaLiquidacion`.

Se cubren pagos parciales y totales y se mantiene separado el saldo original del saldo efectivamente liquidado.

La valorización de cierre no determina por sí sola la forma de pago futura.

## 6. Crédito disponible — IMPLEMENTADO PARCIALMENTE

El cálculo de crédito utilizado contempla ahora la valorización de cierre de una obligación multidivisa cuando existe:

- obligación en moneda de la tarjeta → utiliza `saldoPendiente`;
- obligación en moneda diferente con valorización de cierre → utiliza `importeValorizacionCierre`;
- consumo de tarjeta sin obligación asociada → conserva el comportamiento existente.

No se realiza una conversión implícita para calcular la valorización. La cotización debe existir como `TipoCambio` histórico asociado al cierre.

La regla todavía no está completa para obligaciones multidivisa sin valorización de cierre y debe revisarse el efecto de pagos parciales sobre obligaciones valorizadas.

## 7. Ciclo de facturación — IMPLEMENTADO Y CON HISTORIAL PERSISTENTE

`CicloFacturacion` es un objeto de dominio no persistente. `Cuenta.calcularCicloFacturacion(LocalDate)` resuelve cierre, ciclo siguiente, meses cortos, vencimiento y cambio de año. El vencimiento efectivo se desplaza al lunes cuando cae sábado o domingo.

La obligación conserva los datos históricos del ciclo y las cuotas conservan sus fechas.

## 8. Pagos y reglas temporales — IMPLEMENTADOS

`PagoTarjetaService` coordina autorización, validaciones, egreso real y aplicación del pago en una única operación transaccional.

Se admiten pagos parciales o totales. Se rechazan pagos anteriores al consumo y fechas futuras. Se aplican días de gracia y mora según las reglas vigentes.

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

- `mvn test`: **723/723**.
- Failures: 0.
- Errors: 0.
- Skipped: 0.
- `BUILD SUCCESS`.
- Finalizada: **16/09/2026 13:11:00 -03:00**.
- Tiempo total: **11:02 min**.

Validaciones específicas recientes:

- `MovimientoCreditoMultimonedaTest`: 1/1.
- `MovimientoServiceTest,MovimientoMultimonedaTest,MovimientoServiceSaldoTest`: 62/62.
- `PagoTarjetaServiceTest,SaldoTarjetaCreditoTest,TarjetaCreditoPagoCreditoTest`: 17/17.
- `MovimientoObligacionIntegridadTest,ObligacionServiceTest`: 14/14.
- `ObligacionJpaTest`: 3/3.
- `ObligacionLiquidacionTest`: 13/13.

## 15. Orden de trabajo pendiente

1. Definir el flujo de obtención/registro de la valorización de cierre dentro de la aplicación.
2. Definir el comportamiento de consumos extranjeros todavía no valorizados al cierre.
3. Revisar el cálculo de crédito después de pagos parciales sobre obligaciones valorizadas.
4. Diseñar y cubrir esos casos antes de ampliar el código.
5. Completar persistencia/UI del cierre y pago multidivisa.
6. Financiación avanzada.
7. UI específica de tarjetas.
8. Pasivos/patrimonio y análisis.
9. Gestión de entidades financieras.
10. Pulido de consola.

El calendario de feriados y una fecha efectiva separada requieren decisión de negocio antes de implementarse.
