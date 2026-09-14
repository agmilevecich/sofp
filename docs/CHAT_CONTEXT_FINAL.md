# SOFP — Contexto final de continuidad

## Estado — 14/09/2026

La fuente de verdad es el código, Git y los tests actuales; `docs/` es documentación auxiliar. Antes de proponer cambios, reconstruir siempre el estado desde GitHub.

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

**Último bloque funcional:** reglas temporales de ciclos y pagos de tarjeta.
**Último commit funcional/test:** `3a001a57` — `test: adaptar persistencia de obligaciones a reglas temporales`.
**Última actualización documental:** cierre integral de continuidad posterior al bloque temporal.

No se realizó merge a `main`.

## Validación más reciente

Suite general informada por el usuario: **704/704**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizada 13/09/2026 22:05:14 -03:00.

`ObligacionJpaTest`: **2/2**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.

## Estado consolidado

La integridad de `Cuenta` está implementada: una cuenta con movimientos no puede cambiar de tipo ni de moneda, y no se permiten transiciones genéricas hacia o desde `TARJETA_CREDITO`.

El movimiento origen de una obligación está protegido frente a modificaciones estructurales incompatibles con la obligación y las pruebas cubren la conservación de los valores originales cuando la modificación es rechazada.

El pago de tarjeta está coordinado por `PagoTarjetaService`, exige autorización por usuario y está integrado en la UI.

## Reglas temporales cerradas

- ciclo histórico de la obligación persistido al crearla;
- fechas históricas persistidas en `Cuota`;
- vencimiento de fin de semana desplazado al lunes;
- días de gracia configurables, por defecto 0;
- mora evaluada sobre vencimiento efectivo más gracia;
- pago anterior al consumo rechazado;
- fecha futura rechazada;
- pagos parciales en orden ascendente de cuotas;
- estabilidad histórica frente a cambios posteriores de configuración cuando existen datos persistidos;
- compatibilidad con datos existentes mediante campos nullable/fallback.

No se implementaron feriados, fecha efectiva separada, intereses, punitorios, CFT ni refinanciación.

## Pendientes

1. Multidivisa de tarjetas.
2. Financiación avanzada.
3. Evaluar abstracción `Clock` para tests deterministas de fecha futura.
4. UI específica de tarjetas.
5. Pasivos, patrimonio y análisis.
6. Gestión de entidades financieras.
7. Pulido de consola.

## Orden de continuidad

Antes de cada nuevo bloque: revisar rama y commits actuales, comparar con `main`, revisar documentación como apoyo, inspeccionar implementación y clases relacionadas, revisar tests y tomar como válido únicamente el último resultado informado por el usuario. Después: cambio mínimo → tests específicos → relacionados → suite → diff → diff-check → status → documentación.

No modificar `main` automáticamente y no asumir resultados locales no informados.
