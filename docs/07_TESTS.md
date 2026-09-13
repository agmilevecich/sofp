# SOFP — Tests

## Estado de validación — 13/09/2026

### Suite general más reciente

El usuario ejecutó `mvn test` y obtuvo **700/700**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, 46:17 min, finalizado 19:00:15 -03:00.

### Suite relacionada de `Cuenta`

`CuentaServiceTest,CuentaTest,MovimientoServiceTest,OperacionFinancieraServiceTest,CuentaServiceIntegridadTest`: **154/154**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.

### Tests específicos de integridad

`CuentaServiceIntegridadTest,CuentaServiceTest`: **66/66**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.

## Cobertura temporal auditada

La auditoría revisó el comportamiento existente de:

- cálculo del ciclo de facturación desde la fecha del consumo;
- cierre en el mes del consumo o en el siguiente según el día configurado;
- ajuste a último día real del mes;
- vencimiento posterior al cierre;
- cruce de año;
- generación y persistencia de fechas de cuotas;
- aplicación de pagos en orden de cuota;
- pagos parciales;
- coordinación del pago real con el movimiento de salida.

También se verificó la ausencia actual de tests/reglas para:

- pago con fecha anterior al consumo;
- pago posterior al vencimiento tratado como mora;
- período de gracia;
- fines de semana;
- feriados;
- fecha efectiva de pago separada de fecha/hora del movimiento;
- pagos con fecha futura;
- cambio histórico de cierre/vencimiento después de consumos;
- impacto temporal de pagos parciales sobre cuotas vencidas.

La auditoría no agrega tests que expresen reglas todavía indefinidas. Los tests deberán incorporarse cuando esas reglas de negocio queden establecidas.

## Resultado de la auditoría

El modelo tiene cobertura suficiente para el cálculo básico de ciclos, pero no existe todavía una política temporal de pagos. Por lo tanto, no corresponde declarar tests de mora, gracia o días no hábiles como faltantes de implementación accidental: son reglas de negocio aún no definidas.

Además, se identificó un punto de integridad histórica que deberá cubrirse: las fechas de una `Cuota` quedan persistidas, mientras `Obligacion.getCicloFacturacion()` recalcula el ciclo desde la configuración actual de la tarjeta. También deberá definirse cómo se protege `configurarDatosCredito(...)` después de existir historial.

## Validaciones previas relevantes

- Suite de obligaciones/pagos/UI: **69/69**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.
- `ObligacionServiceTest`: **9/9**, `BUILD SUCCESS`.
- UI de pago de tarjeta: **6/6**, `BUILD SUCCESS`.

## Criterio para el próximo bloque

Primero deben existir reglas temporales explícitas y luego tests de dominio/servicio que las fijen. La validación deberá seguir el flujo: tests específicos → relacionados → suite completa → diff → diff-check → status → documentación.
