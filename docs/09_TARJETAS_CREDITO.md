# SOFP — Diseño y adaptación de tarjetas de crédito

## Estado auditado — 14/09/2026

**Rama:** `feature/swing-shell`

Este documento separa explícitamente lo implementado de lo pendiente. La fuente de verdad es el código y los tests actuales.

## 1. Modelo

Una tarjeta de crédito es una `Cuenta` con `TipoCuenta.TARJETA_CREDITO`. Tiene límite, día de cierre y día de vencimiento.

La deuda se representa mediante `Obligacion` y el consumo mediante `Movimiento`.

## 2. Moneda — IMPLEMENTACIÓN PARCIAL, MULTIDIVISA PENDIENTE

La moneda del consumo se conserva en `Movimiento` y en la obligación. Una compra ARS genera obligación ARS y una compra USD genera obligación USD. No se realiza conversión automática.

Los pagos coordinados requieren coincidencia de moneda entre deuda y cuenta pagadora.

La auditoría integral confirmó que esta base es correcta como protección contra conversiones implícitas, pero insuficiente para cerrar multidivisa.

## 3. Hallazgos multidivisa

Se confirmaron tres problemas que deben resolverse antes de considerar cerrada esta funcionalidad:

1. `CuentaService.calcularSaldo` suma movimientos de la cuenta sin separar por moneda.
2. `MovimientoService` utiliza el mismo saldo mezclado para validar fondos.
3. Un consumo de tarjeta en moneda distinta de la moneda configurada para la tarjeta no entra en el cálculo actual del límite, porque la validación del crédito se aplica solo cuando ambas monedas coinciden.

Además, `PagoTarjetaService` exige misma moneda entre deuda y cuenta pagadora y no existe todavía una operación de conversión/liquidación trazable.

No se deben agregar conversiones automáticas sin definir moneda de liquidación, tasa de cambio, fecha/fuente de cotización y tratamiento contable del movimiento resultante.

## 4. Crédito disponible — IMPLEMENTADO SOLO PARA MONEDA COINCIDENTE

Criterio actual para consumos en la moneda de la tarjeta:

`crédito disponible = límite de crédito − consumos de tarjeta pendientes en la moneda correspondiente`

Las compras con tarjeta no reducen el saldo monetario de la cuenta como si fueran salidas inmediatas de fondos.

Los pagos reducen la obligación y liberan crédito.

Para consumos en moneda distinta, el criterio no está cerrado y actualmente no debe interpretarse como soporte multidivisa completo.

## 5. Ciclo de facturación — IMPLEMENTADO Y CON HISTORIAL PERSISTENTE

`CicloFacturacion` es un objeto de dominio no persistente.

`Cuenta.calcularCicloFacturacion(LocalDate)` resuelve cierre exacto, ciclo siguiente, meses cortos, vencimiento y cambio de año. El vencimiento efectivo se desplaza al lunes cuando cae sábado o domingo.

Al crear una `Obligacion`, se persisten los datos históricos del ciclo: inicio, cierre, vencimiento y días de gracia. Las `Cuota` también conservan persistentemente las fechas de su ciclo.

`Obligacion.getCicloFacturacion()` utiliza los datos históricos persistidos cuando están disponibles y mantiene fallback para obligaciones antiguas sin esos campos.

## 6. Pagos — SERVICIO COORDINADO IMPLEMENTADO

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

## 7. Reglas temporales de pago — IMPLEMENTADAS

- pago anterior al consumo: rechazado;
- fecha futura: rechazada;
- vencimiento en sábado/domingo: desplazado al lunes;
- días de gracia configurables, por defecto 0;
- mora evaluada a partir del vencimiento efectivo más gracia;
- para obligaciones con cuotas, la fecha límite se determina a partir de la primera cuota pendiente;
- pagos parciales continúan aplicándose en orden ascendente de cuotas.

No existe todavía calendario de feriados, fecha efectiva separada del movimiento ni cálculo de intereses/punitorios/CFT.

## 8. Integridad movimiento ↔ obligación — IMPLEMENTADA

El movimiento que origina una obligación queda protegido frente a cambios estructurales que romperían la correspondencia histórica: importe, fecha/hora, tipo y eliminación están sujetos a las reglas de integridad implementadas en `MovimientoService`.

Las pruebas verifican además que, cuando una modificación estructural es rechazada, los valores persistidos originales permanecen intactos.

## 9. Seguridad de API — IMPLEMENTADA PARA PAGOS

El registro de pagos exige `usuarioId` y valida pertenencia/autorización antes de modificar la obligación.

## 10. Cuotas y financiación

Las cuotas simples están implementadas y se generan automáticamente. Los pagos parciales se aplican en orden ascendente.

Sigue pendiente la financiación avanzada: intereses, CFT, cuotas variables, adelantos, refinanciación, anulaciones/reversiones y ajustes.

## 11. Saldo monetario — REQUIERE CORRECCIÓN MULTIDIVISA

El criterio de no descontar consumos de tarjeta del saldo de fondos sigue siendo correcto. Sin embargo, la auditoría confirmó que los cálculos actuales de saldo/fondos no separan movimientos por moneda. Por lo tanto, el saldo de una cuenta con movimientos en monedas distintas no puede considerarse monetariamente correcto hasta resolver esta regla.

## 12. Integridad estructural de Cuenta — IMPLEMENTADA

Una cuenta con movimientos no puede cambiar de tipo ni de moneda. Además, la API genérica no permite transiciones hacia o desde `TARJETA_CREDITO` mediante la operación de modificación estructural.

## 13. UI específica

Pendiente una UI completa para consultar límite/disponible, consumos, ciclos, cierres, vencimientos, deuda y pagos reales de forma específica para tarjetas.

## 14. Compatibilidad histórica

Los nuevos campos temporales se mantienen nullable cuando corresponde para no romper datos existentes. Las obligaciones antiguas sin ciclo histórico completo utilizan fallback al cálculo anterior; `diasGracia` nulo se interpreta como 0.

## 15. Orden de trabajo pendiente

1. Resolver multidivisa: saldos por moneda, crédito disponible y liquidación.
2. Cubrir multidivisa con tests específicos y relacionados.
3. Financiación avanzada.
4. UI específica de tarjetas.
5. Pasivos/patrimonio y análisis.
6. Gestión de entidades financieras.
7. Pulido de consola.

El calendario de feriados y una fecha efectiva separada requieren decisión de negocio antes de implementarse.

## 16. Validación actual

Suite general informada por el usuario: **704/704**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, 13/09/2026 22:05:14 -03:00.

`ObligacionJpaTest`: **2/2**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, 13/09/2026 21:12:49 -03:00.

Validaciones relacionadas previas: suite obligaciones/pagos/UI **69/69**, `ObligacionServiceTest` **9/9**, UI de pago **6/6**, integridad de `Cuenta` **66/66** específicos y **154/154** relacionados.

## 17. Conclusión de auditoría

El modelo de tarjeta está consolidado para moneda coincidente, ciclos, cuotas simples, pagos coordinados, autorización e integridad histórica.

La multidivisa es el principal bloque funcional abierto. El siguiente cambio de código no debe empezar por una conversión aislada: primero debe hacer que saldo, fondos y crédito sean monetariamente coherentes por moneda y luego incorporar una regla explícita de liquidación.
