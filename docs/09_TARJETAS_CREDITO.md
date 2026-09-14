# SOFP — Diseño y adaptación de tarjetas de crédito

## Estado — 14/09/2026

**Rama:** `feature/swing-shell`

Este documento separa explícitamente lo implementado de lo pendiente. La fuente de verdad es el código y los tests actuales.

## 1. Modelo

Una tarjeta de crédito es una `Cuenta` con `TipoCuenta.TARJETA_CREDITO`. Tiene límite, día de cierre y día de vencimiento.

La deuda se representa mediante `Obligacion` y el consumo mediante `Movimiento`.

## 2. Moneda — IMPLEMENTADO CON REGLA ABIERTA DE LÍMITE

La moneda del consumo se conserva en la obligación. Una compra ARS genera obligación ARS y una compra USD genera obligación USD. No se realiza conversión automática.

Los pagos coordinados requieren coincidencia de moneda entre deuda y cuenta pagadora.

El tratamiento multidivisa definitivo del límite de la tarjeta todavía no está cerrado.

## 3. Crédito disponible — IMPLEMENTADO EN SU CRITERIO ACTUAL

Criterio actual:

`crédito disponible = límite de crédito − consumos de tarjeta pendientes en la moneda correspondiente`

Las compras con tarjeta no reducen el saldo monetario de la cuenta como si fueran salidas inmediatas de fondos.

Los pagos reducen la obligación y liberan crédito.

## 4. Ciclo de facturación — IMPLEMENTADO Y CON HISTORIAL PERSISTENTE

`CicloFacturacion` es un objeto de dominio no persistente.

`Cuenta.calcularCicloFacturacion(LocalDate)` resuelve cierre exacto, ciclo siguiente, meses cortos, vencimiento y cambio de año. El vencimiento efectivo se desplaza al lunes cuando cae sábado o domingo.

Al crear una `Obligacion`, se persisten los datos históricos del ciclo: inicio, cierre, vencimiento y días de gracia. Las `Cuota` también conservan persistentemente las fechas de su ciclo.

`Obligacion.getCicloFacturacion()` utiliza los datos históricos persistidos cuando están disponibles y mantiene fallback para obligaciones antiguas sin esos campos.

## 5. Pagos — SERVICIO COORDINADO IMPLEMENTADO

`PagoTarjetaService` coordina en una única transacción:

1. localizar y autorizar la obligación;
2. validar cuenta pagadora, categoría, perfil y moneda;
3. validar cuenta activa;
4. validar importe y saldo pendiente;
5. validar fondos;
6. validar que la fecha del pago no sea anterior al consumo ni posterior al momento actual;
7. registrar egreso real en la cuenta pagadora;
8. registrar pago sobre la obligación;
9. confirmar ambas operaciones juntas.

Puede realizar pagos parciales o totales.

La integración con `ObligacionesPanel` está implementada y cubierta por tests.

## 6. Reglas temporales de pago — IMPLEMENTADAS

- pago anterior al consumo: rechazado;
- fecha futura: rechazada;
- vencimiento en sábado/domingo: desplazado al lunes;
- días de gracia configurables, por defecto 0;
- mora evaluada a partir del vencimiento efectivo más gracia;
- para obligaciones con cuotas, la fecha límite se determina a partir de la primera cuota pendiente;
- pagos parciales continúan aplicándose en orden ascendente de cuotas.

No existe todavía calendario de feriados, fecha efectiva separada del movimiento ni cálculo de intereses/punitorios/CFT.

## 7. Integridad movimiento ↔ obligación — IMPLEMENTADA

El movimiento que origina una obligación queda protegido frente a cambios estructurales que romperían la correspondencia histórica: importe, fecha/hora, tipo y eliminación están sujetos a las reglas de integridad implementadas en `MovimientoService`.

Las pruebas verifican además que, cuando una modificación estructural es rechazada, los valores persistidos originales permanecen intactos.

## 8. Seguridad de API — IMPLEMENTADA PARA PAGOS

El registro de pagos exige `usuarioId` y valida pertenencia/autorización antes de modificar la obligación.

## 9. Cuotas y financiación

Las cuotas simples están implementadas y se generan automáticamente. Los pagos parciales se aplican en orden ascendente.

Sigue pendiente la financiación avanzada: intereses, CFT, cuotas variables, adelantos, refinanciación, anulaciones/reversiones y ajustes.

## 10. Saldo monetario

El criterio actual es coherente: consumos con tarjeta no reducen el saldo monetario de la cuenta; los egresos ordinarios sí. El consumo de tarjeta afecta la obligación y el crédito disponible.

## 11. Integridad estructural de Cuenta — IMPLEMENTADA

Una cuenta con movimientos no puede cambiar de tipo ni de moneda. Además, la API genérica no permite transiciones hacia o desde `TARJETA_CREDITO` mediante la operación de modificación estructural.

## 12. UI específica

Pendiente una UI completa para consultar límite/disponible, consumos, ciclos, cierres, vencimientos, deuda y pagos reales de forma específica para tarjetas.

## 13. Compatibilidad histórica

Los nuevos campos temporales se mantienen nullable cuando corresponde para no romper datos existentes. Las obligaciones antiguas sin ciclo histórico completo utilizan fallback al cálculo anterior; `diasGracia` nulo se interpreta como 0.

## 14. Orden de trabajo pendiente

1. Multidivisa de tarjetas.
2. Financiación avanzada.
3. UI específica de tarjetas.
4. Pasivos/patrimonio y análisis.
5. Gestión de entidades financieras.
6. Pulido de consola.

El calendario de feriados y una fecha efectiva separada requieren decisión de negocio antes de implementarse.

## 15. Validación actual

Suite general informada por el usuario: **704/704**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, 13/09/2026 22:05:14 -03:00.

`ObligacionJpaTest`: **2/2**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, 13/09/2026 21:12:49 -03:00.

Validaciones relacionadas previas: suite obligaciones/pagos/UI **69/69**, `ObligacionServiceTest` **9/9**, UI de pago **6/6**, integridad de `Cuenta` **66/66** específicos y **154/154** relacionados.
