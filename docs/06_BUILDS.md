# SOFP — Historial de Builds

## Estado documental — 14/09/2026

**Etapa temporal de ciclos y pagos: IMPLEMENTADA Y VALIDADA.**

### Validación final

El usuario ejecutó `mvn test`: **704/704**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`; finalizado 13/09/2026 22:05:14 -03:00.

### Validación de persistencia tras adaptación temporal

`mvn -Dtest=ObligacionJpaTest test`: **2/2**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`; finalizado 13/09/2026 21:12:49 -03:00.

## Bloque temporal cerrado

Se implementaron y probaron: ciclo histórico persistido en `Obligacion`, fechas persistidas en `Cuota`, vencimiento ajustado para sábado/domingo, días de gracia, evaluación de mora, rechazo de pagos anteriores al consumo y futuros, estabilidad histórica ante cambios de configuración y compatibilidad con datos existentes.

La adaptación de `ObligacionJpaTest` usa una cuenta de crédito real en el fixture y refleja el vencimiento efectivo del fin de semana. El cambio quedó en `3a001a57c435237e62ab04f6c09a0657ff24fcb2`.

## Integridad de Cuenta — cierre previo

Se protege cambio de tipo/moneda con movimientos y transiciones hacia/desde `TARJETA_CREDITO`.

## Historial documental

Los builds y validaciones anteriores continúan siendo parte del historial del proyecto. Este documento registra el estado de cierre más reciente sin reemplazar el historial técnico conservado en Git.

## Próximo bloque

Definir antes de implementar: multidivisa de tarjetas y financiación avanzada. Quedan fuera del bloque temporal actual calendario de feriados, fecha efectiva independiente y recargos financieros.

No se modificó `main`.
