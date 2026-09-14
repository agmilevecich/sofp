# SOFP — Pendientes

## Estado — 14/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell` → `3a001a57c435237e62ab04f6c09a0657ff24fcb2`.

**Último bloque funcional:** reglas temporales de ciclos y pagos, implementadas y validadas.
**Última suite completa informada:** **704/704**, BUILD SUCCESS.

## Bloques cerrados

- Selección explícita de tarjeta activa en `GastosPanel`.
- Generación de cuotas y cruce de año.
- Atomicidad de compra con tarjeta.
- Pago coordinado y pago real desde UI.
- Integración de `PagoTarjetaService` en shell.
- Protección de movimientos origen de obligaciones.
- Autorización del registro de pagos.
- Integridad estructural de `Cuenta`.
- Auditoría temporal de ciclos y pagos.
- Implementación temporal de pagos.

## Implementación temporal cerrada

1. Pago anterior al consumo: rechazado.
2. Fecha futura: rechazada.
3. Vencimiento en sábado/domingo: desplazado al lunes.
4. Días de gracia configurables, por defecto 0.
5. Mora derivada del vencimiento efectivo más gracia.
6. Obligaciones con cuotas: fecha límite basada en la primera cuota pendiente.
7. Ciclo histórico congelado en la obligación al crearla.
8. Compatibilidad con datos existentes mediante campos nuevos nullable y fallback.
9. Tests de persistencia adaptados a las reglas temporales.

No se agregaron intereses ni punitorios.

## Pendientes reales en orden

### P1

- Multidivisa de tarjetas; no introducir conversiones implícitas.
- Financiación avanzada: intereses, CFT, cuotas variables, adelantos, refinanciación, anulaciones/reversiones y ajustes.
- Evaluar abstracción de reloj (`Clock`) para hacer determinista la validación de fechas futuras.

### P2

- UI específica de tarjetas: límite/disponible, consumos, ciclos, cierres, vencimientos, deuda y pagos.
- Pasivos, patrimonio y análisis.

### P2/P3

- Gestión de entidades financieras.

### P3

- Pulido de consola.

## Fuera de alcance actual

Calendario de feriados, fecha efectiva separada del movimiento y recargos financieros no están implementados. Requieren decisiones de negocio antes de codificar.

## Regla de cierre

Tests específicos → relacionados → suite general → `git diff` → `git diff --check` → `git status` → documentación.
