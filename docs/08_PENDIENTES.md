# SOFP — Pendientes

## Estado — 13/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

**Último cambio funcional:** implementación de reglas temporales de ciclo/pago.
**Validación:** pendiente de ejecución local posterior al cambio. La última suite completa anterior fue **700/700**, `BUILD SUCCESS`.

## Bloques cerrados

- Selección explícita de tarjeta activa en `GastosPanel`.
- Generación de cuotas y cruce de año.
- Atomicidad de compra con tarjeta.
- Pago coordinado y pago real desde UI.
- Integración de `PagoTarjetaService` en shell.
- Protección de movimientos origen de obligaciones.
- Tests de integridad movimiento ↔ obligación.
- JAR ejecutable y dependencias runtime.
- Autorización del registro de pagos en `ObligacionService`.
- Auditoría transversal de `Cuenta`.
- Integridad estructural de `Cuenta`.
- Auditoría temporal de ciclos y pagos.

## P1 — Implementación temporal en curso

Se implementó en `feature/swing-shell` el primer bloque completo de reglas temporales:

1. rechazo de pagos anteriores al consumo que origina la obligación;
2. rechazo de fechas de pago futuras;
3. vencimiento desplazado al siguiente día hábil cuando cae sábado o domingo;
4. período de gracia configurable en la tarjeta, con valor por defecto 0;
5. evaluación de mora respecto del vencimiento efectivo más gracia;
6. para obligaciones con cuotas, la fecha límite se toma de la primera cuota pendiente;
7. congelamiento histórico del ciclo de una obligación al crearla;
8. compatibilidad con obligaciones/cuentas existentes cuyos nuevos campos todavía sean nulos, mediante fallback seguro al cálculo anterior;
9. tests nuevos para fechas inválidas, fechas futuras, estabilidad histórica, gracia y vencimientos en fin de semana.

No se agregaron intereses ni punitorios. La mora se determina como estado temporal derivable; la financiación avanzada queda separada.

## Pendientes reales en orden

### P1 — Validar implementación temporal

Ejecutar tests específicos, relacionados y luego suite general. Después revisar `git diff`, `git diff --check` y `git status`.

### P1 — Multidivisa de tarjetas

Definir tratamiento definitivo del límite frente a consumos en monedas diferentes. No introducir conversiones implícitas.

### P1 — Financiamiento avanzado

Intereses, CFT, cuotas variables, adelantos, refinanciación, anulaciones/reversiones y ajustes.

### P2 — UI específica de tarjetas

Límite/disponible, consumos, ciclos, cierres, vencimientos, deuda y pagos reales.

### P2 — Pasivos, patrimonio y análisis

Pasivos, patrimonio neto, histórico, vencimientos, resúmenes y dashboard.

### P2/P3 — Gestión de entidades financieras

Todavía no existe un panel específico para registrar y gestionar entidades financieras.

### P3 — Pulido de consola

Prioridad baja.

## Regla de cierre

Tests específicos → relacionados → suite general → `git diff` → `git diff --check` → `git status` → documentación.
