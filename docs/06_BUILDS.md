# SOFP — Historial de Builds

## Build actual — cierre de etapa y auditoría

**Estado: VALIDADO.**

Último commit funcional de la etapa: `1fd43d5` — `fix: proteger movimientos origen de obligaciones`.

El commit posterior `87014d3` agrega la cobertura específica de integridad movimiento ↔ obligación.

## Validación más reciente

Suite general más reciente informada por el usuario el 13/09/2026:

- `mvn test`;
- **695/695**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- tiempo informado: **15:39 min**.

La validación relacionada anterior también fue exitosa:

- `mvn -Dtest=MovimientoServiceTest,MovimientoObligacionIntegridadTest test`;
- **55/55**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`.

## Bloques funcionales consolidados

Fondos insuficientes, categorías con movimientos, Gastos, Ingresos, FormaPago, Obligaciones/pagos, autorización por usuario, Transferencias, moneda explícita, crédito/límite de tarjeta, ciclos básicos de facturación, cuotas, atomicidad de compra con tarjeta, integridad del movimiento origen de obligación, selección explícita de tarjeta, H2 TCP y JAR ejecutable.

## Auditoría posterior

La auditoría del estado actual deja como próximos bloques principales:

1. cerrar superficies públicas de `ObligacionService` que puedan bypassar autorización;
2. integrar `PagoTarjetaService` en `ObligacionesPanel` y completar el flujo real de pago;
3. proteger cambios estructurales de tipo/moneda de cuentas con historial;
4. definir reglas de ciclo aplicadas al pago;
5. definir tratamiento multidivisa de tarjetas;
6. financiación avanzada y UI específica.

La protección de movimientos que originan obligaciones ya no figura como pendiente: está implementada y validada con tests específicos y suite completa.

No se modificó `main`.
