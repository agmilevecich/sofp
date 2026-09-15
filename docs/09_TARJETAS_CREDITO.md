# SOFP — Diseño y adaptación de tarjetas de crédito

## Estado auditado — 15/09/2026

**Rama:** `feature/swing-shell`

Este documento separa explícitamente lo implementado de lo pendiente. La fuente de verdad es el código y los tests actuales.

## 1. Modelo

Una tarjeta de crédito es una `Cuenta` con `TipoCuenta.TARJETA_CREDITO`. Tiene límite, día de cierre y día de vencimiento.

La deuda se representa mediante `Obligacion` y el consumo mediante `Movimiento`.

## 2. Moneda — IMPLEMENTACIÓN PARCIAL, MULTIDIVISA PENDIENTE

La moneda del consumo se conserva en `Movimiento` y en la obligación. Una compra ARS genera obligación ARS y una compra USD genera obligación USD. No se realiza conversión automática.

Los pagos coordinados requieren coincidencia de moneda entre deuda y cuenta pagadora.

La auditoría confirmó que esta base es correcta como protección contra conversiones implícitas, pero insuficiente para cerrar multidivisa.

## 3. Estado actual de saldos y fondos

La cuenta calcula su saldo usando únicamente movimientos de la moneda de la cuenta. `MovimientoService` valida fondos usando la moneda económica del movimiento. Por lo tanto, ARS y USD ya no deben mezclarse en estos cálculos.

Esto está cubierto por `MovimientoMultimonedaTest` y por la suite relacionada actual.

## 4. Hallazgo multidivisa de tarjetas

Continúa pendiente definir qué ocurre cuando la moneda del consumo difiere de la moneda de la tarjeta. En particular:

1. cómo impacta ese consumo sobre el límite;
2. en qué moneda se expresa el crédito disponible;
3. cuál es la moneda de liquidación;
4. qué tasa se utiliza;
5. qué fecha y fuente de cotización se registran;
6. cómo se representa y audita la conversión/liquidación.

No se deben agregar conversiones automáticas sin esas decisiones.

## 5. Crédito disponible

Para consumos en la moneda de la tarjeta, el criterio actual es:

`crédito disponible = límite de crédito − consumos de tarjeta pendientes en la moneda correspondiente`

Las compras con tarjeta no reducen el saldo monetario de la cuenta como si fueran salidas inmediatas de fondos. Los pagos reducen la obligación y liberan crédito.

Para consumos en moneda distinta, el criterio no está cerrado y no debe interpretarse como soporte multidivisa completo.

## 6. Ciclo de facturación — IMPLEMENTADO Y CON HISTORIAL PERSISTENTE

`CicloFacturacion` es un objeto de dominio no persistente.

`Cuenta.calcularCicloFacturacion(LocalDate)` resuelve cierre exacto, ciclo siguiente, meses cortos, vencimiento y cambio de año. El vencimiento efectivo se desplaza al lunes cuando cae sábado o domingo.

Al crear una `Obligacion`, se persisten los datos históricos del ciclo: inicio, cierre, vencimiento y días de gracia. Las `Cuota` también conservan persistentemente las fechas de su ciclo.

`Obligacion.getCicloFacturacion()` utiliza los datos históricos persistidos cuando están disponibles y mantiene fallback para obligaciones antiguas sin esos campos.

## 7. Pagos — SERVICIO COORDINADO IMPLEMENTADO

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

## 8. Reglas temporales de pago — IMPLEMENTADAS

- pago anterior al consumo: rechazado;
- fecha futura: rechazada;
- vencimiento en sábado/domingo: desplazado al lunes;
- días de gracia configurables, por defecto 0;
- mora evaluada a partir del vencimiento efectivo más gracia;
- para obligaciones con cuotas, la fecha límite se determina a partir de la primera cuota pendiente;
- pagos parciales continúan aplicándose en orden ascendente de cuotas.

No existe todavía calendario de feriados, fecha efectiva separada del movimiento ni cálculo de intereses/punitorios/CFT.

## 9. Integridad movimiento ↔ obligación — IMPLEMENTADA

El movimiento que origina una obligación queda protegido frente a cambios estructurales que romperían la correspondencia histórica: importe, fecha/hora, tipo y eliminación están sujetos a las reglas de integridad implementadas en `MovimientoService`.

Las pruebas verifican además que, cuando una modificación estructural es rechazada, los valores persistidos originales permanecen intactos.

## 10. Seguridad de API — IMPLEMENTADA PARA PAGOS

El registro de pagos exige `usuarioId` y valida pertenencia/autorización antes de modificar la obligación.

## 11. Cuotas y financiación

Las cuotas simples sin interés están implementadas y se generan automáticamente. Los pagos parciales se aplican en orden ascendente.

Sigue pendiente la financiación avanzada: intereses, CFT, cuotas variables, adelantos, refinanciación, anulaciones/reversiones y ajustes.

## 12. Integridad estructural de Cuenta — IMPLEMENTADA

Una cuenta con movimientos no puede cambiar de tipo ni de moneda. Además, la API genérica no permite transiciones hacia o desde `TARJETA_CREDITO` mediante la operación de modificación estructural.

## 13. UI específica

Pendiente una UI completa para consultar límite/disponible, consumos, ciclos, cierres, vencimientos, deuda y pagos reales de forma específica para tarjetas.

## 14. Compatibilidad histórica

Los nuevos campos temporales se mantienen nullable cuando corresponde para no romper datos existentes. Las obligaciones antiguas sin ciclo histórico completo utilizan fallback al cálculo anterior; `diasGracia` nulo se interpreta como 0.

## 15. Orden de trabajo pendiente

1. Resolver multidivisa de tarjetas: límite, liquidación y pagos entre monedas.
2. Cubrir multidivisa con tests específicos y relacionados.
3. Financiación avanzada.
4. UI específica de tarjetas.
5. Pasivos/patrimonio y análisis.
6. Gestión de entidades financieras.
7. Pulido de consola.

El calendario de feriados y una fecha efectiva separada requieren decisión de negocio antes de implementarse.

## 16. Validación actual

- `MonedaTest`: **7/7**.
- `MonedaTest,CuentaTest,CuentaJpaTest,MovimientoTest`: **53/53**.
- Suite general: **693/693**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, 15/09/2026 12:03:19 -03:00.

## 17. Conclusión

El modelo de tarjeta está consolidado para moneda coincidente, ciclos, cuotas simples, pagos coordinados, autorización e integridad histórica. Los saldos y fondos generales ya respetan la moneda.

La multidivisa de tarjetas sigue siendo el principal bloque funcional abierto. El siguiente cambio no debe empezar por una conversión aislada: primero debe definirse el impacto sobre crédito disponible y la regla explícita de liquidación.
