# SOFP — Estado actual

> Documento de continuidad. La fuente de verdad técnica es el código, los tests y los commits actuales; `docs/` es documentación auxiliar.

## Estado verificado — 12/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

Último commit: `6c1b896` — `build: configurar jar ejecutable y dependencias`.

La rama de trabajo continúa separada de `main`; no se realizó merge.

## Últimos bloques cerrados

### Cuotas al cruzar fin de año

`ObligacionCuotasTest` cubre una compra del `2026-12-16` con tres cuotas y verifica ciclos/vencimientos hasta abril de 2027.

### Atomicidad de compra con tarjeta

`GastoService` coordina movimiento + obligación dentro de una única transacción cuando el medio es `TARJETA_CREDITO`. Existe cobertura de rollback cuando falla la creación de la obligación.

### JAR ejecutable

`pom.xml` configura `maven-jar-plugin` y `maven-dependency-plugin` para generar un JAR ejecutable con `Main` y dependencias en `target/lib`. El usuario verificó el arranque con `java -Dsofp.dev=true -jar target/SOFP-1.0-SNAPSHOT.jar`.

## Validación más reciente conocida

El usuario ejecutó `mvn test` y obtuvo:

- **690/690** tests;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`.

El último resultado informado es posterior a la documentación histórica del 11/09 y prevalece sobre los conteos anteriores de 689 tests.

## Arquitectura funcional vigente

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

Gastos: `GastosPanel → GastoService → MovimientoService → Movimiento EGRESO`.

Ingresos: `IngresosPanel → IngresoService → MovimientoService → Movimiento INGRESO`.

Una compra con `TARJETA_CREDITO` genera un movimiento de egreso y una obligación. Las cuotas se generan automáticamente por el flujo de gasto.

`MovimientoService`, `ObligacionService` y `GastoService` permiten coordinar transacciones cuando forman parte de una operación compuesta.

## Persistencia y H2

La aplicación utiliza:

`jdbc:h2:tcp://localhost/./database/sofp`

H2 Server: `localhost:9092`. H2 Console: `localhost:8082`.

Los tests mantienen un `persistence.xml` independiente con H2 en memoria.

## Tarjetas, moneda y ciclos

Una tarjeta de crédito es una `Cuenta` con `TipoCuenta.TARJETA_CREDITO`, límite, cierre y vencimiento.

Los movimientos tienen moneda explícita. La obligación conserva la moneda económica del movimiento de origen. No hay conversión automática ARS↔USD.

El crédito disponible se calcula según el criterio actual de límite menos consumos pendientes en la moneda correspondiente.

`CicloFacturacion` es un objeto de dominio no persistente y `Cuenta.calcularCicloFacturacion(LocalDate)` resuelve cierres, meses cortos y cambio de año. La generación de cuotas ya utiliza esta información. La política completa del ciclo sobre pagos, mora y días no hábiles sigue abierta.

## Auditoría de continuidad — 12/09/2026

La auditoría contra el código y tests actuales determinó que varios pendientes documentales anteriores ya no representan el estado real. Los siguientes puntos son ahora los principales trabajos pendientes.

### P0 — Integridad movimiento ↔ obligación

Un movimiento que origina una obligación de tarjeta todavía puede ser modificado o eliminado por caminos de `MovimientoService` que permiten cambiar importe, fecha/hora, tipo o eliminar el movimiento. La obligación conserva datos derivados que pueden quedar inconsistentes.

Regla propuesta para el próximo cambio mínimo: bloquear modificaciones estructurales y eliminación de un movimiento que sea origen de una obligación; mantener permitidos los cambios descriptivos que no alteren la identidad económica.

Tests pendientes: importe, fecha/hora, tipo y eliminación, incluyendo persistencia y rollback/estado final.

### P0 — Pago de tarjeta integrado a UI

`PagoTarjetaService` ya coordina correctamente cuenta pagadora + movimiento de salida + pago de obligación en una transacción. Sin embargo, `ObligacionesPanel` todavía utiliza directamente `ObligacionService.registrarPago(...)` y `Main` no integra `PagoTarjetaService`.

La UI todavía no completa el flujo real de pago porque debe seleccionar cuenta pagadora y categoría y utilizar el servicio coordinador. Este punto es funcionalmente crítico para evitar reducir deuda sin registrar la salida real de fondos.

### P0 — API pública de obligaciones y autorización

Existen operaciones de `ObligacionService` sin `usuarioId` que permiten consultar o modificar obligaciones directamente. Deben revisarse las superficies públicas para que la autorización por perfil no pueda ser bypass mediante el servicio.

Objetivo: hacer internos los métodos de coordinación que no deban ser públicos o exigir `usuarioId` en las operaciones expuestas a la UI.

### P1 — Integridad de Cuenta

`CuentaService` permite modificar tipo y moneda de una cuenta aun cuando puede existir historial financiero asociado. Debe definirse y aplicar una regla que impida cambios estructurales incompatibles con operaciones históricas relevantes.

### P1 — Ciclo de facturación completo

La base de dominio y la generación de cuotas están implementadas. Falta definir y probar el comportamiento del ciclo durante el pago: vencimiento, mora/gracia, días no hábiles y relación temporal entre cuotas y pagos. No se deben inventar reglas antes de decidirlas.

### P1 — Multidivisa de tarjetas

La moneda del movimiento y de la obligación está definida y los pagos requieren coincidencia de moneda. Sigue pendiente el tratamiento definitivo de una tarjeta y su límite frente a consumos en monedas distintas. No realizar conversiones implícitas.

### P1 — Financiación avanzada

Las cuotas actuales son iguales salvo corrección de centavos. Quedan fuera intereses, CFT, refinanciación, adelantos, anulaciones y ajustes.

### P2 — UI específica de tarjetas

Después de estabilizar dominio y servicios: límite/disponible, consumos, ciclos, vencimientos, deuda y pagos reales.

### P2 — Pasivos, patrimonio y análisis

Ampliar pasivos/patrimonio neto y luego resúmenes, vencimientos, histórico y dashboard.

## Qué ya no debe figurar como pendiente independiente

- integración básica de `CicloFacturacion` con consumos/obligaciones: ya existe;
- generación automática de cuotas: ya existe;
- unificación básica del saldo monetario de tarjeta: auditada como coherente;
- atomicidad básica de compra con tarjeta: implementada y testeada;
- JAR ejecutable: implementado y verificado manualmente.

## Protocolo de continuidad

Ante una nueva sesión: rama → últimos commits → comparación con `main` → documentación → código → tests → último resultado conocido → próximo paso.

Prioridad: **código → tests → commits → `main` → documentación → conversaciones anteriores**.

No modificar `main` automáticamente. No asumir resultados locales no informados.