# SOFP — Estado actual

> Documento de continuidad. La fuente de verdad técnica es el código, los tests y los commits actuales; `docs/` es documentación auxiliar y puede quedar desactualizada.

## Estado verificado — 13/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

**Último commit:** `41ebb2b14b79efe5c61926d95306820dcd7069ea` — `test: corregir saldo esperado en pago de tarjeta`.

La rama de trabajo continúa separada de `main`; no se realizó merge.

## Último bloque cerrado

### Pago real de tarjeta integrado en la UI

`ObligacionesPanel` utiliza `PagoTarjetaService` para coordinar el pago. La UI permite seleccionar cuenta pagadora y categoría, valida la selección y registra el pago mediante el servicio coordinador.

El flujo realiza en una operación transaccional la reducción de la obligación y el movimiento `EGRESO` de la cuenta pagadora. El movimiento de pago utiliza `FormaPago.TRANSFERENCIA`, por lo que reduce el saldo monetario de la cuenta pagadora.

La integración de la aplicación se completa en `Main`, que crea `PagoTarjetaService` y lo inyecta en `MainFrame` y `ObligacionesPanel`.

La cobertura UI incluye el pago parcial y la comprobación de la salida real de fondos. Se mantiene la autorización por perfil/usuario en los servicios.

## Validación más reciente conocida

El usuario ejecutó `mvn test` el 13/09/2026 y obtuvo:

- **696/696** tests;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- tiempo informado: **21:14 min**.

Validaciones relacionadas informadas:

- suite de servicios/dominio/UI de obligaciones y pagos: **69/69**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`;
- tests específicos de `ObligacionesPanel` y pago de tarjeta: **6/6**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.

El usuario también informó que `git syncsofp`, `git diff`, `git diff --check` y `git status` terminaron correctamente; el árbol de trabajo quedó limpio y sincronizado con GitHub.

## Arquitectura funcional vigente

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

Gastos: `GastosPanel → GastoService → MovimientoService → Movimiento EGRESO`.

Ingresos: `IngresosPanel → IngresoService → MovimientoService → Movimiento INGRESO`.

Una compra con `TARJETA_CREDITO` genera movimiento de egreso + obligación + cuotas dentro de la coordinación transaccional correspondiente.

El pago de tarjeta se coordina mediante `PagoTarjetaService`: cuenta pagadora + movimiento de salida + actualización de obligación.

`MovimientoService`, `ObligacionService` y `GastoService` participan en las coordinaciones transaccionales necesarias según la operación.

## Persistencia y H2

La aplicación utiliza:

`jdbc:h2:tcp://localhost/./database/sofp`

H2 Server: `localhost:9092`. H2 Console: `localhost:8082`.

Los tests mantienen un `persistence.xml` independiente con H2 en memoria.

## Tarjetas, moneda y ciclos

Una tarjeta de crédito es una `Cuenta` con `TipoCuenta.TARJETA_CREDITO`, límite, cierre y vencimiento.

`Obligacion` es una relación uno-a-uno con el `Movimiento` de origen. El origen debe ser un `EGRESO` de una cuenta de tarjeta. La obligación conserva importe original, saldo pendiente, moneda, fecha y ciclo derivados del movimiento.

Los movimientos tienen moneda explícita. No existe conversión automática ARS↔USD.

El pago de tarjeta exige coincidencia de moneda entre obligación y cuenta pagadora.

El crédito disponible se calcula según el criterio actual de límite menos consumos pendientes en la moneda correspondiente.

`CicloFacturacion` es un objeto de dominio no persistente y `Cuenta.calcularCicloFacturacion(LocalDate)` resuelve cierres, meses cortos y cambio de año. La generación de cuotas ya utiliza esta información.

## Integridad movimiento ↔ obligación

`MovimientoService` bloquea modificaciones estructurales y eliminación de un `Movimiento` que es origen de una `Obligacion`: importe, fecha/hora y tipo no pueden cambiarse y el movimiento no puede eliminarse. Se mantienen permitidos los cambios descriptivos de descripción y observaciones.

`MovimientoObligacionIntegridadTest` cubre estos rechazos y la persistencia consistente de movimiento y obligación.

## Estado de pendientes reales

### P0 — API pública de obligaciones y autorización

Revisar las operaciones de `ObligacionService` que todavía pueden invocarse sin `usuarioId`. La autorización por perfil no debe poder bypassarse mediante un método público de coordinación.

Objetivo: hacer internos los métodos de coordinación que no deban exponerse o exigir autorización explícita en las operaciones públicas.

### P1 — Integridad de Cuenta

Revisar cambios de tipo y moneda de una cuenta cuando ya existe historial financiero y definir la regla mínima compatible con el dominio actual.

### P1 — Ciclo de facturación aplicado al pago

Definir y probar comportamiento respecto de vencimiento, mora, gracia, días no hábiles y orden temporal. No inventar reglas antes de decidirlas.

### P1 — Multidivisa de tarjetas

Definir el tratamiento definitivo del límite de una tarjeta frente a consumos en monedas diferentes. No introducir conversiones implícitas.

### P1 — Financiación avanzada

Quedan fuera del alcance actual: intereses, CFT/costo financiero, cuotas variables, adelantos, refinanciación, anulaciones/reversiones y ajustes.

### P2 — UI específica de tarjetas

Después de estabilizar dominio y servicios: límite/disponible, consumos, ciclos, cierres, vencimientos, deuda y pagos reales.

### P2 — Pasivos, patrimonio y análisis

Ampliar pasivos/patrimonio neto y luego histórico, vencimientos, resúmenes y dashboard.

### P2/P3 — Gestión de entidades financieras

No existe todavía un panel específico para registrar/gestionar entidades financieras. Queda pendiente definir e implementar cuando corresponda.

## Qué ya no debe figurar como pendiente independiente

- integridad del movimiento origen de obligación: implementada y testeada;
- integración básica de `CicloFacturacion` con consumos/obligaciones: implementada;
- generación automática de cuotas: implementada;
- cuotas que cruzan fin de año: testeadas;
- atomicidad básica de compra con tarjeta: implementada y testeada;
- pago coordinado de tarjeta en servicio: implementado y testeado;
- pago real de tarjeta desde la UI: implementado y testeado;
- integración de `PagoTarjetaService` en `Main`/`MainFrame`: implementada;
- JAR ejecutable: implementado y verificado manualmente.

## Protocolo de continuidad

Ante una nueva sesión: rama → últimos commits → comparación con `main` → README/docs → código → tests → último resultado conocido → próximo paso.

Prioridad: **código → tests → commits → `main` → documentación → conversaciones anteriores**.

No modificar `main` automáticamente. No asumir resultados locales no informados. No considerar terminada una tarea solo porque compila.