# SOFP — Diseño y adaptación de tarjetas de crédito

## Estado — 12/09/2026

**Rama:** `feature/swing-shell`

Este documento separa explícitamente lo implementado de lo pendiente.

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

## 4. Ciclo de facturación — IMPLEMENTADO PARA CONSUMOS Y CUOTAS

`CicloFacturacion` es un objeto de dominio no persistente.

`Cuenta.calcularCicloFacturacion(LocalDate)` resuelve cierre exacto, ciclo siguiente, meses cortos, vencimiento y cambio de año.

`Obligacion.getCicloFacturacion()` deriva el ciclo desde cuenta + fecha del movimiento de origen.

La generación de cuotas ya utiliza este dominio y existe cobertura de cruce de año.

Pendiente: definir reglas específicas del pago frente al ciclo, vencimiento, mora/gracia y días no hábiles.

## 5. Pagos — SERVICIO COORDINADO IMPLEMENTADO; UI PENDIENTE

`PagoTarjetaService` coordina en una única transacción:

1. localizar y autorizar la obligación;
2. validar cuenta pagadora, categoría, perfil y moneda;
3. validar cuenta activa;
4. validar importe y saldo pendiente;
5. validar fondos;
6. registrar egreso real en la cuenta pagadora;
7. registrar pago sobre la obligación;
8. confirmar ambas operaciones juntas.

Puede realizar pagos parciales o totales.

**Gap actual:** `ObligacionesPanel` todavía llama directamente a `ObligacionService.registrarPago(...)` y `Main` no integra el servicio coordinador completo. La UI debe incorporar cuenta pagadora y categoría y utilizar `PagoTarjetaService`.

## 6. Integridad movimiento ↔ obligación — PENDIENTE CRÍTICO

Un movimiento origen de una obligación no debe poder cambiar importe, fecha/hora, tipo ni ser eliminado sin una política explícita de actualización coordinada.

La implementación actual todavía permite caminos de modificación/eliminación desde `MovimientoService`. Esto puede dejar obligación, cuotas y ciclo en estado inconsistente.

Próximo cambio mínimo: bloquear estas modificaciones estructurales cuando existe una obligación de origen y agregar tests de persistencia.

## 7. Seguridad de API — PENDIENTE CRÍTICO

Revisar métodos públicos de `ObligacionService` sin `usuarioId`. Las operaciones de consulta/modificación expuestas no deben permitir bypass de autorización por invocación directa del servicio.

## 8. Cuotas y financiación

Las cuotas simples están implementadas y se generan automáticamente. Sigue pendiente la financiación avanzada: intereses, CFT, cuotas variables, adelantos, refinanciación, anulaciones y ajustes.

## 9. Saldo monetario

La auditoría confirmó que el criterio actual es coherente: consumos con tarjeta no reducen el saldo monetario de la cuenta; los egresos ordinarios sí. El consumo de tarjeta afecta la obligación y el crédito disponible.

## 10. UI específica

Pendiente una UI completa para consultar límite/disponible, consumos, ciclos, cierres, vencimientos, deuda y pagos reales.

## 11. Cuenta — INTEGRIDAD PENDIENTE

Revisar `CuentaService` para evitar cambios de tipo o moneda que vuelvan incoherente un historial financiero existente.

## 12. Orden de trabajo

1. Proteger movimiento origen de obligación.
2. Cerrar superficies públicas de `ObligacionService`.
3. Integrar pago real en UI.
4. Proteger tipo/moneda de `Cuenta` con historial.
5. Definir reglas de ciclo durante pagos.
6. Definir multidivisa de tarjetas.
7. Implementar financiación avanzada.
8. Construir UI específica de tarjetas.
9. Pasivos/patrimonio y análisis.

## 13. Validación actual

Suite general informada por el usuario: **690/690**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.

El servicio coordinador de pagos y la atomicidad de compra cuentan con cobertura específica; los gaps de UI y mutabilidad descritos arriba requieren nuevos tests antes de considerarse cerrados.
