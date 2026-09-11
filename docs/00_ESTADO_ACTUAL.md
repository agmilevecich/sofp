# SOFP — Estado actual

> Documento de continuidad. La fuente de verdad técnica es el código, los tests y los commits actuales; `docs/` es documentación auxiliar.

## Estado verificado — 11/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

Último commit funcional verificado: `34eb4cc51e302086375a5c745ed512d6247048ec` — `config: conectar SOFP a H2 por TCP`.

Antes de la actualización documental, GitHub verificó que `feature/swing-shell` estaba **495 commits adelante y 0 atrás** respecto de `main`. No se realizó merge a `main`.

## Último bloque funcional cerrado

### H2 persistente por TCP

La aplicación SOFP quedó configurada para conectarse a H2 mediante servidor TCP:

`jdbc:h2:tcp://localhost/./database/sofp`

Esto permite que SOFP y H2 Console utilicen simultáneamente la misma base persistente.

El usuario verificó manualmente que la aplicación funciona y que H2 Console funciona desde el navegador sobre la misma base.

Los tests mantienen una configuración independiente con H2 en memoria mediante su propio `src/test/resources/META-INF/persistence.xml`.

## Validación más reciente

El usuario ejecutó `mvn test` el **11/09/2026 13:14:41 -03:00**:

- **687/687** tests;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración **18:31 min**.

También se verificó manualmente SOFP + H2 Console simultáneamente.

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

**Selección explícita de tarjeta de crédito en `GastosPanel`.**

Objetivo:

- cuando la forma de pago sea `TARJETA_CREDITO`, permitir seleccionar explícitamente qué tarjeta se utiliza;
- ofrecer únicamente cuentas activas de tipo `TARJETA_CREDITO`;
- pasar la tarjeta seleccionada como `Cuenta` a `GastoService`;
- cubrir con tests la selección correcta y evitar mezclar cuentas de otros tipos.

Antes de implementar, revisar `GastosPanel`, `GastoService`, `CuentaService`, repositorio de cuentas y tests relacionados.

## Protocolo de continuidad

Ante una nueva sesión: rama → últimos commits → comparación con `main` → README/documentación → código → tests → último resultado conocido → próximo paso.

Prioridad: **código → tests → commits → `main` → documentación → conversaciones anteriores**.

No hacer merge a `main` automáticamente.
