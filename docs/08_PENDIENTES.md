# SOFP — Pendientes

## Estado — 13/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

**Último cambio funcional:** `e5fbe0f9064841e2a03a671c0bf877e1897e8289` — `fix: proteger integridad estructural de cuentas`.

Suite general más reciente informada: **700/700**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.
Suite relacionada de Cuenta: **154/154**, `BUILD SUCCESS`.
Suite específica `CuentaServiceIntegridadTest,CuentaServiceTest`: **66/66**, `BUILD SUCCESS`.

## Bloques cerrados

- Selección explícita de tarjeta activa en `GastosPanel`.
- Generación de cuotas y cruce de año.
- Atomicidad de compra con tarjeta.
- Pago coordinado y pago real desde UI.
- Integración de `PagoTarjetaService` en `Main`/`MainFrame`.
- Protección de movimientos origen de obligaciones.
- Tests de integridad movimiento ↔ obligación.
- JAR ejecutable y dependencias runtime.
- Autorización del registro de pagos en `ObligacionService`.
- Auditoría transversal de `Cuenta`.
- Integridad estructural de `Cuenta`.

## P1 cerrado — Integridad estructural de `Cuenta`

La implementación mínima derivada de la auditoría quedó aplicada y validada.

Reglas implementadas:

1. no permitir cambio de tipo con movimientos financieros;
2. no permitir cambio de moneda con movimientos financieros;
3. no permitir mediante la API genérica transiciones hacia o desde `TARJETA_CREDITO`;
4. mantener autorización por usuario;
5. preservar consumos de tarjeta cuya moneda económica difiere de la moneda estructural de la cuenta.

La política futura para migraciones específicas de datos de crédito sigue requiriendo una operación de negocio explícita; no se limpian ni migran datos implícitamente.

Cobertura agregada: `CuentaServiceIntegridadTest` y suite relacionada. Validación final: **700/700** en la suite completa.

## Pendientes reales en orden

### P1 — 1. Ciclo aplicado al pago

Definir vencimiento, mora, gracia, días no hábiles y orden temporal antes de implementar.

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

## Orden de ejecución

1. Reglas de ciclo durante pagos.
2. Multidivisa.
3. Financiamiento.
4. UI específica.
5. Pasivos/patrimonio/análisis.
6. Gestión de entidades financieras.
7. Pulido.

## Regla de cierre

Tests específicos → relacionados → suite general → `git diff` → `git diff --check` → `git status` → documentación.
