# SOFP — Diseño y adaptación de tarjetas de crédito

## Estado auditado — 15/09/2026

**Rama:** `feature/swing-shell`

Este documento separa explícitamente lo implementado de lo pendiente. La fuente de verdad es el código y los tests actuales.

## 1. Modelo

Una tarjeta de crédito es una `Cuenta` con `TipoCuenta.TARJETA_CREDITO`. Tiene límite, día de cierre y día de vencimiento.

La deuda se representa mediante `Obligacion` y el consumo mediante `Movimiento`.

## 2. Moneda — LIQUIDACIÓN HISTÓRICA IMPLEMENTADA, INTEGRACIÓN DE PAGO PENDIENTE

La moneda del consumo se conserva en `Movimiento` y como `monedaOriginal` de la obligación. La moneda de la tarjeta/cuenta queda como `monedaLiquidacion`.

`TipoCambio` representa una cotización histórica con moneda origen, moneda destino, cotización, fecha/hora y fuente.

`Obligacion.liquidar(TipoCambio)` valida que la cotización corresponda a las monedas de la obligación, calcula `importeLiquidacion`, conserva la cotización utilizada y evita una segunda liquidación.

No se realiza conversión automática al crear el consumo ni se recalcula una liquidación histórica con una cotización posterior.

## 3. Estado actual de saldos y fondos

La cuenta calcula su saldo usando únicamente movimientos de la moneda de la cuenta. `MovimientoService` valida fondos usando la moneda económica del movimiento. ARS y USD no deben mezclarse en estos cálculos.

## 4. Multidivisa de tarjetas — PENDIENTE PARCIAL

Ya está resuelto el modelo de la obligación y la cotización histórica. Falta definir e implementar:

1. cómo impacta un consumo en moneda distinta sobre el límite;
2. en qué moneda se expresa el crédito disponible;
3. cómo `PagoTarjetaService` utiliza una liquidación histórica explícita;
4. cómo se representa el pago real cuando cuenta pagadora y obligación usan monedas distintas;
5. cobertura completa de servicio, persistencia y UI.

No se deben agregar conversiones implícitas.

## 5. Crédito disponible

Para consumos en la moneda de la tarjeta, el criterio actual es:

`crédito disponible = límite de crédito − consumos de tarjeta pendientes en la moneda correspondiente`

Las compras con tarjeta no reducen el saldo monetario de la cuenta como si fueran salidas inmediatas de fondos. Los pagos reducen la obligación y liberan crédito.

Para consumos en moneda distinta, el criterio definitivo todavía debe cerrarse.

## 6. Ciclo de facturación — IMPLEMENTADO Y CON HISTORIAL PERSISTENTE

`CicloFacturacion` es un objeto de dominio no persistente.

`Cuenta.calcularCicloFacturacion(LocalDate)` resuelve cierre exacto, ciclo siguiente, meses cortos, vencimiento y cambio de año. El vencimiento efectivo se desplaza al lunes cuando cae sábado o domingo.

Al crear una `Obligacion`, se persisten los datos históricos del ciclo: inicio, cierre, vencimiento y días de gracia. Las `Cuota` conservan persistentemente sus fechas.

## 7. Pagos — SERVICIO COORDINADO IMPLEMENTADO

`PagoTarjetaService` coordina la autorización, validaciones, egreso real y aplicación del pago dentro de una única operación transaccional.

Puede realizar pagos parciales o totales y está integrado en `ObligacionesPanel`.

La extensión para liquidación multidivisa histórica todavía no fue incorporada al servicio.

## 8. Reglas temporales de pago — IMPLEMENTADAS

- pago anterior al consumo: rechazado;
- fecha futura: rechazada;
- vencimiento en sábado/domingo: desplazado al lunes;
- días de gracia configurables, por defecto 0;
- mora evaluada a partir del vencimiento efectivo más gracia;
- pagos parciales continúan aplicándose en orden ascendente de cuotas.

No existe todavía calendario de feriados, fecha efectiva separada del movimiento ni cálculo de intereses/punitorios/CFT.

## 9. Integridad movimiento ↔ obligación — IMPLEMENTADA

El movimiento que origina una obligación queda protegido frente a cambios estructurales que romperían la correspondencia histórica: importe, fecha/hora, tipo y eliminación están sujetos a las reglas implementadas en `MovimientoService`.

## 10. Seguridad de API — IMPLEMENTADA PARA PAGOS

El registro de pagos exige `usuarioId` y valida pertenencia/autorización antes de modificar la obligación.

## 11. Cuotas y financiación

Las cuotas simples sin interés están implementadas y se generan automáticamente. Los pagos parciales se aplican en orden ascendente.

Sigue pendiente la financiación avanzada: intereses, CFT, cuotas variables, adelantos, refinanciación, anulaciones/reversiones y ajustes.

## 12. Integridad estructural de Cuenta — IMPLEMENTADA

Una cuenta con movimientos no puede cambiar de tipo ni de moneda. La API genérica tampoco permite transiciones hacia o desde `TARJETA_CREDITO`.

## 13. UI específica

Pendiente una UI completa para consultar límite/disponible, consumos, ciclos, cierres, vencimientos, deuda y pagos reales de forma específica para tarjetas.

## 14. Compatibilidad histórica

Los nuevos campos se mantienen nullable cuando corresponde para no romper datos existentes y utilizan fallback cuando los registros antiguos no contienen la información histórica.

## 15. Validación actual

- `TipoCambioTest`: **10/10**.
- `TipoCambioJpaTest`: **1/1**.
- `ObligacionTest`: **12/12**.
- `ObligacionJpaTest`: **3/3**.
- `ObligacionLiquidacionTest`: **5/5**.
- `ObligacionTipoCambioJpaTest`: **1/1**.
- Suite general: **712/712**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, 15/09/2026 18:05:46 -03:00.

## 16. Orden de trabajo pendiente

1. Integrar liquidación histórica en `PagoTarjetaService`.
2. Definir impacto de moneda extranjera sobre límite/crédito disponible.
3. Cubrir servicio, persistencia y UI.
4. Financiación avanzada.
5. UI específica de tarjetas.
6. Pasivos/patrimonio y análisis.
7. Gestión de entidades financieras.
8. Pulido de consola.

El calendario de feriados y una fecha efectiva separada requieren decisión de negocio antes de implementarse.

## 17. Conclusión

El modelo de tarjeta está consolidado para moneda coincidente, ciclos, cuotas simples, pagos coordinados, autorización e integridad histórica. La primera etapa de liquidación multidivisa histórica ya está modelada, validada y persistida.

El siguiente cambio debe concentrarse en la integración de esa liquidación con el flujo real de `PagoTarjetaService`, sin conversiones implícitas.
