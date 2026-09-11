# SOFP — Estado actual

> Documento de continuidad. La fuente de verdad técnica es el código, los tests y los commits actuales; `docs/` es documentación auxiliar.

## Estado verificado — 11/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

Último commit: `44661fb` — `test: cubrir cuotas al cruzar fin de año`.

GitHub verifica que la rama está **514 commits adelante y 0 atrás** respecto de `main`. No se realizó merge a `main`.

## Último bloque funcional cerrado

### Selección explícita de tarjeta de crédito en Gastos

`GastosPanel` filtra dinámicamente las cuentas cuando la forma de pago es `TARJETA_CREDITO`.

Cuando se selecciona `TARJETA_CREDITO`:

- se muestran únicamente cuentas activas;
- se muestran únicamente cuentas con `TipoCuenta.TARJETA_CREDITO`;
- la tarjeta seleccionada se utiliza como `Cuenta` al registrar el gasto;
- las cuentas de otros tipos no se mezclan en esa selección.

El flujo de UI vigente es seleccionar primero la forma de pago y luego la cuenta.

## Último cambio de tests

Se agregó cobertura para generación de cuotas al cruzar el fin de año:

- compra: `2026-12-16`;
- cuota 1: cierre `2027-01-15`, vencimiento `2027-02-10`;
- cuota 2: cierre `2027-02-15`, vencimiento `2027-03-10`;
- cuota 3: cierre `2027-03-15`, vencimiento `2027-04-10`.

## Validación más reciente

El usuario ejecutó `mvn test` el **11/09/2026 20:11:17 -03:00**:

- **689/689** tests;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración **10:22 min**.

También se ejecutaron los tests relacionados del dominio/servicios:

- **49/49** tests;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`.

## Validación Git

El usuario informó:

- `git diff main...feature/swing-shell --check`: sin salida;
- `git status`: working tree limpio;
- rama sincronizada con `github/feature/swing-shell`.

La comparación GitHub contra `main` confirma 514 commits adelante y 0 atrás, con 109 archivos modificados respecto de `main`.

## Persistencia y H2

La aplicación SOFP utiliza H2 mediante servidor TCP:

`jdbc:h2:tcp://localhost/./database/sofp`

El servidor H2 se ejecuta en `localhost:9092` y H2 Console en `localhost:8082`. La aplicación y la consola utilizan la misma base persistente.

Los tests mantienen una configuración independiente con H2 en memoria mediante su propio `src/test/resources/META-INF/persistence.xml`.

## Arquitectura funcional vigente

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

Gastos: `GastosPanel → GastoService → MovimientoService → Movimiento EGRESO`.

Ingresos: `IngresosPanel → IngresoService → MovimientoService → Movimiento INGRESO`.

Una compra con `TARJETA_CREDITO` genera un movimiento de egreso y una obligación. `GastoService` registra automáticamente las cuotas solicitadas dentro de la transacción de la obligación.

## Moneda, obligaciones y tarjetas

Los movimientos admiten moneda explícita. Una obligación conserva la moneda económica del movimiento de origen. No se convierte automáticamente ARS↔USD al crear la obligación.

Las obligaciones tienen estados `PENDIENTE`, `PARCIAL` y `PAGADA`. Los pagos de tarjeta están autorizados por usuario.

Una tarjeta de crédito es una `Cuenta` con `TipoCuenta.TARJETA_CREDITO`, límite, día de cierre y día de vencimiento.

`CicloFacturacion` es un objeto de dominio no persistente. Ya está implementado y tiene cobertura propia; queda pendiente su integración completa con consumos, obligaciones y pagos.

## Próximo paso real

Auditar los pendientes actuales contra el código y los 689 tests para separar pendientes funcionales reales de mejoras futuras. El siguiente bloque funcional candidato continúa siendo la integración de ciclos de facturación y vencimientos con consumos, obligaciones y pagos.

Después deberán abordarse la unificación del saldo de tarjetas, la profundización de pagos y liberación de crédito, pasivos/patrimonio y análisis histórico/dashboard.

## Protocolo de continuidad

Ante una nueva sesión: rama → últimos commits → comparación con `main` → README/documentación → código → tests → último resultado conocido → próximo paso.

Prioridad: **código → tests → commits → `main` → documentación → conversaciones anteriores**.

No hacer merge a `main` automáticamente.
