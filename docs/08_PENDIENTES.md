# SOFP — Pendientes

## Estado — 13/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

**Último commit funcional:** `2813fa34c953f4f6408903c1e3b4fc2e7f3b58c5`.
Los commits posteriores registrados en esta etapa son de documentación.

Suite general más reciente informada: **696/696**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.
Suite `ObligacionServiceTest`: **9/9**, `BUILD SUCCESS`.
Suite relacionada: **69/69**, `BUILD SUCCESS`.

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

## Auditoría de `Cuenta` — terminada

La auditoría transversal del eje de cuentas quedó terminada sin cambios de código.

Se revisaron: `Cuenta`, `CuentaService`, `CuentaRepository`, `TipoCuenta`, `Moneda`, `Movimiento`, `MovimientoRepository`, `MovimientoService`, `GastoService`, `OperacionFinanciera`, `MovimientoActivo`, `CuentaTest`, `CuentaServiceTest`, `MovimientoServiceTest` y `OperacionFinancieraServiceTest`.

Conclusiones:

1. `modificarTipoCuenta` y `modificarMoneda` no bloquean actualmente modificaciones cuando existen movimientos históricos.
2. `cambiarTipoCuenta` puede crear un estado incompleto al convertir una cuenta común en tarjeta, porque los datos de crédito no se configuran automáticamente.
3. Cambiar moneda con historial puede romper la interpretación histórica de la cuenta.
4. No debe imponerse una igualdad universal entre moneda de cuenta y moneda de movimiento: los consumos de tarjeta pueden conservar una moneda económica distinta.
5. Las transferencias entre cuentas ya exigen misma moneda.
6. El repositorio de cuentas no contiene reglas de negocio de tipo/moneda.

## Pendientes reales en orden

### P1 — 1. Integridad estructural de `Cuenta`

Implementar la regla derivada de la auditoría:

- bloquear cambio de tipo cuando exista historial financiero;
- bloquear cambio de moneda cuando exista historial financiero;
- impedir estados incompletos al convertir hacia/desde `TARJETA_CREDITO`;
- mantener autorización por usuario;
- conservar el caso válido de consumos de tarjeta en moneda económica extranjera;
- agregar tests de cuenta sin historial, con historial, tarjeta, persistencia y autorización.

La política exacta de los datos de crédito al cambiar de tipo debe quedar explícita en el dominio; no limpiar ni inventar datos implícitamente.

### P1 — 2. Ciclo aplicado al pago

Definir vencimiento, mora, gracia, días no hábiles y orden temporal antes de implementar.

### P1 — 3. Multidivisa de tarjetas

Definir tratamiento definitivo del límite frente a consumos en monedas diferentes. No introducir conversiones implícitas.

### P1 — 4. Financiamiento avanzado

Intereses, CFT, cuotas variables, adelantos, refinanciación, anulaciones/reversiones y ajustes.

### P2 — 5. UI específica de tarjetas

Límite/disponible, consumos, ciclos, cierres, vencimientos, deuda y pagos reales.

### P2 — 6. Pasivos, patrimonio y análisis

Pasivos, patrimonio neto, histórico, vencimientos, resúmenes y dashboard.

### P2/P3 — 7. Gestión de entidades financieras

Todavía no existe un panel específico para registrar y gestionar entidades financieras.

### P3 — 8. Pulido de consola

Prioridad baja.

## Orden de ejecución

1. Integridad de `Cuenta`.
2. Reglas de ciclo durante pagos.
3. Multidivisa.
4. Financiamiento.
5. UI específica.
6. Pasivos/patrimonio/análisis.
7. Gestión de entidades financieras.
8. Pulido.

## Regla de cierre

Tests específicos → relacionados → suite general → `git diff` → `git diff --check` → `git status` → documentación.
