# SOFP — Pendientes

## Estado — 13/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.
**Rama documental de continuidad:** `docs/continuidad-sofp`.

**Último commit de código:** `41ebb2b14b79efe5c61926d95306820dcd7069ea` — `test: corregir saldo esperado en pago de tarjeta`.

La rama de trabajo continúa separada de `main`.

Suite general más reciente informada por el usuario: **696/696**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.

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

## Auditoría: pendientes reales en orden

### P0 — 1. Cerrar superficies públicas de `ObligacionService`

Revisar métodos sin `usuarioId` que permitan consultar o modificar obligaciones. La autorización no debe depender de que la UI llame correctamente al overload autorizado.

Trabajo:

- convertir en internos los métodos de coordinación que no deban ser públicos; o
- exigir `usuarioId` en operaciones expuestas;
- mantener aislamiento por perfil;
- agregar pruebas directas de intento de acceso cruzado.

### P1 — 2. Proteger cambios estructurales de `Cuenta`

Revisar `CuentaService` para impedir cambios de tipo o moneda cuando ya existe historial financiero que haga incompatible la modificación.

Definir primero la regla mínima compatible con el dominio actual y luego cubrirla con tests.

### P1 — 3. Completar ciclo de facturación durante el pago

La generación de ciclos/cuotas está implementada. Falta decidir cómo se comportan pagos respecto de vencimiento, mora, gracia, días no hábiles y orden temporal.

No implementar reglas de negocio no decididas.

### P1 — 4. Definir multidivisa de tarjetas

Actualmente el consumo y la obligación conservan su moneda y el pago exige coincidencia de moneda. Falta definir cómo se comporta el límite de una tarjeta frente a consumos en monedas diferentes.

No introducir conversiones implícitas.

### P1 — 5. Financiamiento avanzado

Pendiente definir e implementar, cuando corresponda:

- intereses;
- CFT/costo financiero;
- cuotas variables;
- adelantos;
- refinanciación;
- anulaciones/reversiones;
- ajustes.

### P2 — 6. UI específica de tarjetas

Una vez estabilizado dominio/servicios:

- límite y disponible;
- consumos;
- ciclos;
- cierres y vencimientos;
- deuda;
- pagos reales.

### P2 — 7. Pasivos, patrimonio y análisis

Ampliar pasivos/patrimonio neto y posteriormente histórico, vencimientos, resúmenes y dashboard.

### P2/P3 — 8. Gestión de entidades financieras

No existe todavía un panel específico para registrar y gestionar entidades financieras. Queda pendiente definir e implementar cuando corresponda.

### P3 — 9. Pulido de consola

Prioridad baja. No debe interferir con reglas financieras ni servicios.

## Orden de ejecución recomendado

1. Autorización/superficie pública de obligaciones.
2. Integridad de tipo/moneda de cuentas.
3. Reglas de ciclo aplicadas al pago.
4. Multidivisa.
5. Financiamiento avanzado.
6. UI específica de tarjetas.
7. Pasivos/patrimonio y análisis.
8. Gestión de entidades financieras.
9. Pulido.

## Regla de cierre

Para cada bloque: tests específicos → tests relacionados → suite general cuando corresponda → `git diff` → `git diff --check` → `git status` → documentación.

No considerar terminado un bloque porque compila. No modificar tests para hacerlos pasar.
