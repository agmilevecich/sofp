# SOFP — Estado actual

> Documento de continuidad. La fuente de verdad técnica es el código, los tests y los commits actuales; `docs/` es documentación auxiliar.

## Estado verificado — 11/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

Último commit del bloque funcional de tarjetas: `4b26734` — `test: ajustar orden de seleccion en gastos`.

La rama de trabajo continúa separada de `main`; no se realizó merge.

## Último bloque funcional cerrado

### Selección explícita de tarjeta de crédito en Gastos

`GastosPanel` filtra dinámicamente la cuenta disponible cuando la forma de pago es `TARJETA_CREDITO`.

Cuando se selecciona `TARJETA_CREDITO`:

- se muestran únicamente cuentas activas;
- se muestran únicamente cuentas con `TipoCuenta.TARJETA_CREDITO`;
- la tarjeta seleccionada se utiliza como `Cuenta` al registrar el gasto;
- las cuentas de ahorro, corriente, efectivo u otros tipos no se mezclan en esa selección.

También se agregó cobertura específica para la selección de tarjeta y se ajustó el orden de interacción de los tests de `GastosPanel` para seleccionar primero la forma de pago y luego la cuenta, que es el flujo vigente de la UI.

## Validación más reciente

El usuario ejecutó `mvn test` el **11/09/2026 14:10:20 -03:00**:

- **688/688** tests;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración **12:52 min**.

También se ejecutaron previamente los tests específicos de `GastosPanel` y selección de tarjeta con resultado exitoso.

La validación final de Git fue informada por el usuario:

- `git diff`: sin cambios;
- `git diff --check`: sin errores;
- `git status`: working tree limpio;
- rama local sincronizada con `github/feature/swing-shell`.

## Persistencia y H2

La aplicación SOFP utiliza H2 mediante servidor TCP:

`jdbc:h2:tcp://localhost/./database/sofp`

Esto permite que SOFP y H2 Console utilicen simultáneamente la misma base persistente.

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

`CicloFacturacion` es un objeto de dominio no persistente y ya está implementado; queda pendiente su integración completa con consumos, obligaciones y pagos.

## Reglas financieras vigentes

- `EGRESO` superior al saldo disponible: rechazado.
- `EGRESO` igual al saldo disponible: permitido y deja saldo cero.
- Las modificaciones de importe y tipo respetan fondos disponibles.
- Categorías con movimientos se conservan y se desactivan en lugar de eliminarse físicamente.
- Cuenta y forma de pago son conceptos distintos.
- Una compra con `TARJETA_CREDITO` genera un movimiento de egreso y una obligación.
- Las cuotas se generan automáticamente al registrar el gasto cuando se solicita una cantidad mayor que una.
- El crédito disponible se calcula inicialmente por moneda de la tarjeta, sin conversión implícita.
- Transferencias propias no son ingresos ni gastos.
- La UI no debe duplicar reglas financieras.

## Próximo paso real

**Integración de ciclos de facturación con consumos, obligaciones y pagos.**

Después deberán abordarse la unificación del saldo de tarjetas, la profundización de pagos y liberación de crédito, pasivos/patrimonio y análisis histórico/dashboard.

## Protocolo de continuidad

Ante una nueva sesión: rama → últimos commits → comparación con `main` → README/documentación → código → tests → último resultado conocido → próximo paso.

Prioridad: **código → tests → commits → `main` → documentación → conversaciones anteriores**.

No hacer merge a `main` automáticamente.
