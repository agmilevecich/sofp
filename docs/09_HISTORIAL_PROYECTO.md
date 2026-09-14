# SOFP — Historial del proyecto

## Estado documental — 14/09/2026

Los estados técnicos deben verificarse siempre contra código, tests y Git. Este documento registra hitos; no reemplaza la inspección del estado real.

## Hitos principales

Se mantiene el historial previo de construcción del dominio, seguridad, Swing, movimientos, inversiones, cuentas, obligaciones, pagos, transferencias, H2 de tests, integridad de movimientos y cuentas.

32. Auditoría completa del ciclo de facturación aplicado al pago.
33. Implementación de reglas temporales de ciclos y pagos.
34. Validación de persistencia de obligaciones y suite completa tras adaptar tests a vencimientos de fin de semana.

## Bloque temporal — cierre 14/09/2026

Confirmado como implementado:

- cálculo del ciclo según consumo y cierre;
- persistencia histórica del ciclo en `Obligacion`;
- persistencia de fechas en `Cuota`;
- vencimiento ajustado por fin de semana;
- días de gracia;
- evaluación de mora;
- rechazo de pagos anteriores al consumo;
- rechazo de pagos futuros;
- pagos parciales y orden ascendente de cuotas;
- compatibilidad con datos existentes.

La adaptación final de `ObligacionJpaTest` quedó en el commit `3a001a57c435237e62ab04f6c09a0657ff24fcb2`.

## Validación

`mvn -Dtest=ObligacionJpaTest test`: **2/2**, BUILD SUCCESS.

`mvn test`: **704/704**, 0 failures, 0 errors, 0 skipped, BUILD SUCCESS, 13/09/2026 22:05:14 -03:00.

## Pendientes

- Multidivisa de tarjetas.
- Financiación avanzada.
- UI específica de tarjetas.
- Pasivos, patrimonio y análisis.
- Gestión de entidades financieras.
- Pulido de consola.
- Calendario de feriados y fecha efectiva separada, si se definen como reglas de negocio.

No hacer merge a `main` automáticamente.
