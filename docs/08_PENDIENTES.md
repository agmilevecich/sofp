# SOFP — Pendientes

## Estado — 13/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

**Último commit de código/documentación:** `0cfbcab221256ae9bafdab171dc103a22d0e7eb4` — `docs: actualizar tests tras cierre de autorizacion`.

La rama de trabajo continúa separada de `main`.

Suite general más reciente informada por el usuario: **696/696**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.
Suite específica de `ObligacionService`: **9/9**, `BUILD SUCCESS`.
Suite relacionada: **69/69**, `BUILD SUCCESS`.

## Bloques cerrados

- Selección explícita de tarjeta activa en `GastosPanel`.
- Generación de cuotas al registrar gastos con tarjeta.
- Cuotas que cruzan el fin de año.
- Atomicidad de compra con tarjeta: movimiento + obligación.
- Coordinación transaccional de movimientos y obligaciones.
- Pago coordinado de tarjeta en `PagoTarjetaService`.
- Pago real de tarjeta desde `ObligacionesPanel`.
- Integración de `PagoTarjetaService` en `Main`/`MainFrame`.
- Cálculo base de ciclos de facturación.
- Protección de movimientos que originan obligaciones.
- Tests de integridad movimiento ↔ obligación.
- Configuración de JAR ejecutable y copia de dependencias.
- Autorización del registro de pagos en `ObligacionService`: el API público exige `usuarioId` y valida pertenencia al perfil.

## Auditoría: pendientes reales en orden

### P0 — Cerrado: superficies públicas de `ObligacionService`

El overload público `registrarPago(Long obligacionId, BigDecimal importe)` fue eliminado. El registro de pagos requiere ahora `usuarioId`, evitando el bypass de autorización mediante una API pública sin contexto de usuario.

La cobertura de `ObligacionServiceTest` quedó adaptada a la API autorizada y validada con **9/9** tests.

### P1 — 1. Proteger cambios estructurales de `Cuenta`

Revisar `CuentaService` para impedir cambios de tipo o moneda cuando ya existe historial financiero que haga incompatible la modificación.

Definir primero la regla mínima compatible con el dominio actual y luego cubrirla con tests.

### P1 — 2. Completar ciclo de facturación durante el pago

La generación de ciclos/cuotas está implementada. Falta decidir cómo se comportan pagos respecto de vencimiento, mora, gracia, días no hábiles y orden temporal.

No implementar reglas de negocio no decididas.

### P1 — 3. Definir multidivisa de tarjetas

Actualmente el consumo y la obligación conservan su moneda y el pago exige coincidencia de moneda. Falta definir cómo se comporta el límite de una tarjeta frente a consumos en monedas diferentes.

No introducir conversiones implícitas.

### P1 — 4. Financiamiento avanzado

Pendiente definir e implementar, cuando corresponda:

- intereses;
- CFT/costo financiero;
- cuotas variables;
- adelantos;
- refinanciación;
- anulaciones/reversiones;
- ajustes.

### P2 — 5. UI específica de tarjetas

Una vez estabilizado dominio/servicios:

- límite y disponible;
- consumos;
- ciclos;
- cierres y vencimientos;
- deuda;
- pagos reales.

### P2 — 6. Pasivos, patrimonio y análisis

Ampliar pasivos/patrimonio neto y posteriormente histórico, vencimientos, resúmenes y dashboard.

### P2/P3 — 7. Gestión de entidades financieras

No existe todavía un panel específico para registrar y gestionar entidades financieras. Queda pendiente definir e implementar cuando corresponda.

### P3 — 8. Pulido de consola

Prioridad baja. No debe interferir con reglas financieras ni servicios.

## Orden de ejecución recomendado

1. Integridad de tipo/moneda de cuentas.
2. Reglas de ciclo aplicadas al pago.
3. Multidivisa.
4. Financiamiento avanzado.
5. UI específica de tarjetas.
6. Pasivos/patrimonio y análisis.
7. Gestión de entidades financieras.
8. Pulido.

## Regla de cierre

Para cada bloque: tests específicos → tests relacionados → suite general cuando corresponda → `git diff` → `git diff --check` → `git status` → documentación.

No considerar terminado un bloque porque compila. No modificar tests para hacerlos pasar.
