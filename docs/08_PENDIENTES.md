# SOFP — Pendientes

## Estado — 13/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

**Último cambio funcional:** `e5fbe0f9064841e2a03a671c0bf877e1897e8289` — `fix: proteger integridad estructural de cuentas`.
**Último commit documental de esta auditoría:** `c97c41835abcf39c0248a98f2c9fd606f3e7f956`.

Suite general más reciente informada: **700/700**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.

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
- **Auditoría completa de ciclo de facturación aplicado al pago.**

## P1 cerrado — Integridad estructural de `Cuenta`

La implementación mínima derivada de la auditoría quedó aplicada y validada. Se bloquean cambios de tipo/moneda con historial y transiciones genéricas hacia/desde tarjeta.

## P1 cerrado — Auditoría temporal de ciclos y pagos

La auditoría del bloque quedó completa sin modificar código. Se revisaron cálculo de ciclo, cierre, vencimiento, persistencia de cuotas, aplicación de pagos, fechas, mora, gracia, días no hábiles y estabilidad histórica.

### Hallazgos que ya existen en código

1. El ciclo básico se calcula correctamente según fecha de consumo y cierre.
2. El vencimiento siempre queda después del cierre y contempla meses de distinta duración.
3. Las cuotas persisten sus fechas de ciclo y vencimiento.
4. Los pagos se aplican en orden ascendente de cuota y admiten parciales.
5. El pago genera un egreso real de la cuenta pagadora.

### Gaps de negocio identificados

1. No se valida que la fecha del pago sea posterior o igual al consumo.
2. No existe clasificación de pago en término o tardío.
3. No existe período de gracia.
4. No existe ajuste por fines de semana o feriados.
5. No existe fecha efectiva separada de la fecha/hora del movimiento.
6. Actualmente se aceptan fechas futuras.
7. Los pagos parciales no tienen comportamiento temporal diferenciado por cuota.
8. `Obligacion.getCicloFacturacion()` recalcula con la configuración actual de la tarjeta, mientras las cuotas ya generadas conservan fechas persistidas.
9. `Cuenta.configurarDatosCredito(...)` puede modificar cierre/vencimiento después de existir historial sin una regla histórica específica.

Estos gaps no se implementan por inferencia: requieren reglas financieras explícitas. Intereses, punitorios, CFT y refinanciación quedan fuera de este bloque y pertenecen a financiación avanzada.

## Pendientes reales en orden

### P1 — 1. Implementar reglas temporales de pago

Convertir los hallazgos de la auditoría en reglas comprobables y tests, sin inventar mora/gracia/calendario.

### P1 — 2. Multidivisa de tarjetas

Definir tratamiento definitivo del límite frente a consumos en monedas diferentes. No introducir conversiones implícitas.

### P1 — 3. Financiamiento avanzado

Intereses, CFT, cuotas variables, adelantos, refinanciación, anulaciones/reversiones y ajustes.

### P2 — 4. UI específica de tarjetas

Límite/disponible, consumos, ciclos, cierres, vencimientos, deuda y pagos reales.

### P2 — 5. Pasivos, patrimonio y análisis

Pasivos, patrimonio neto, histórico, vencimientos, resúmenes y dashboard.

### P2/P3 — 6. Gestión de entidades financieras

Todavía no existe un panel específico para registrar y gestionar entidades financieras.

### P3 — 7. Pulido de consola

Prioridad baja.

## Regla de cierre

Tests específicos → relacionados → suite general → `git diff` → `git diff --check` → `git status` → documentación.
