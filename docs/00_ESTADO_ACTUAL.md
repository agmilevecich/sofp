# SOFP — Estado actual

> Documento de continuidad. La fuente de verdad técnica es el código, los tests y los commits actuales; `docs/` es documentación auxiliar.

## Estado verificado — 14/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell` → `3a001a57c435237e62ab04f6c09a0657ff24fcb2`.

**Último commit:** `3a001a57c435237e62ab04f6c09a0657ff24fcb2` — `test: adaptar persistencia de obligaciones a reglas temporales`.

## Último bloque cerrado

### Reglas temporales de ciclos y pagos de tarjeta

Quedó implementado y validado el primer bloque temporal: el ciclo histórico de una obligación queda persistido al crearla; las cuotas conservan sus fechas; el vencimiento se ajusta si cae sábado o domingo; la tarjeta admite días de gracia; la mora se evalúa sobre vencimiento efectivo más gracia; los pagos anteriores al consumo y futuros son rechazados; se mantiene el pago parcial y en orden de cuotas. Los nuevos campos son compatibles con datos existentes mediante valores nulos/fallback.

La configuración histórica de cada obligación ya no depende de recalcular el ciclo desde la tarjeta actual cuando los datos históricos están presentes.

### Integridad estructural de Cuenta

Se protege la integridad histórica: una cuenta con movimientos no puede cambiar de tipo ni de moneda, y la API genérica no permite transiciones hacia o desde `TARJETA_CREDITO`.

## Validación más reciente conocida

El usuario ejecutó `mvn test`: **704/704**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizado el 13/09/2026 a las 22:05:14 -03:00.

También se verificó `ObligacionJpaTest`: **2/2**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.

## Pendientes reales

### P1

- Multidivisa de tarjetas.
- Financiación avanzada: intereses, CFT, cuotas variables, adelantos, refinanciación, anulaciones/reversiones y ajustes.
- Refinamiento de la fuente temporal (`Clock`) para evitar depender directamente de `LocalDateTime.now()` nos tests futuros.

### P2

- UI específica de tarjetas.
- Pasivos, patrimonio y análisis.
- Gestión de entidades financieras.

### P3

- Pulido de consola.

### Fuera del bloque temporal actual

No están implementados calendario de feriados, fecha efectiva separada de la fecha/hora del movimiento ni recargos financieros. No se deben inventar reglas sin decisión de negocio explícita.

## Protocolo de continuidad

Ante una nueva sesión: rama → últimos commits → comparación con `main` → README/docs → código → tests → último resultado conocido → próximo paso.

No modificar `main` automáticamente. No asumir resultados locales no informados.
