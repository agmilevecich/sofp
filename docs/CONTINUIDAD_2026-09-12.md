# SOFP — Continuidad 2026-09-12

## Estado verificado

- Rama de trabajo: `feature/swing-shell`.
- Rama documental de continuidad: `docs/continuidad-sofp`.
- `main`: `a4be85913847200cb70976d5266d9cbba10b3100`.
- Último commit de código: `6c1b896` — `build: configurar jar ejecutable y dependencias`.
- No se realizó merge a `main`.
- La rama documental `docs/continuidad-sofp` fue recreada desde el estado más reciente de `feature/swing-shell` para recuperar el canal dedicado de continuidad.

## Validación conocida

El usuario informó una ejecución completa de `mvn test` con:

- **690/690** tests;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`.

Este resultado reemplaza al histórico 689/689 documentado el 11/09/2026.

El JAR ejecutable fue verificado con:

`java -Dsofp.dev=true -jar target/SOFP-1.0-SNAPSHOT.jar`

## Últimos cambios consolidados

1. Ciclos y cuotas con cobertura de cambio de año.
2. Coordinación transaccional de movimientos y obligaciones.
3. Atomicidad de compra con tarjeta.
4. Cobertura de rollback de compra con tarjeta.
5. Configuración de JAR ejecutable y dependencias runtime.

## Auditoría funcional — 12/09/2026

La auditoría se realizó sobre código, servicios, dominio y tests actuales. Los siguientes puntos representan el mapa de trabajo futuro; no implican que ya estén corregidos.

### P0 — Integridad movimiento ↔ obligación

Problema: un movimiento origen de una obligación puede seguir siendo modificado estructuralmente o eliminado desde `MovimientoService`.

Riesgo: obligación, cuotas y ciclo pueden conservar información incompatible con el movimiento.

Cambio mínimo previsto:

- bloquear modificación de importe;
- bloquear modificación de fecha/hora;
- bloquear cambio de tipo;
- bloquear eliminación;
- mantener cambios descriptivos no económicos.

Tests: cuatro operaciones anteriores + persistencia/estado final.

### P0 — Autorización de ObligacionService

Hay operaciones sin `usuarioId` que pueden exponer consultas o modificaciones sin la misma barrera de autorización que usa la UI.

Objetivo: hacer internos los métodos de coordinación o exigir `usuarioId` en operaciones públicas.

Tests: aislamiento por perfil y acceso directo no autorizado.

### P0 — Pago real de tarjeta desde UI

`PagoTarjetaService` ya coordina cuenta pagadora + movimiento de salida + reducción de obligación en una transacción.

Gap: `ObligacionesPanel` todavía llama directamente a `ObligacionService.registrarPago(...)` y `Main` no completa la integración del servicio coordinador.

Objetivo:

- seleccionar cuenta pagadora;
- seleccionar categoría;
- llamar `PagoTarjetaService`;
- mantener autorización, moneda y fondos;
- garantizar atomicidad.

### P1 — Integridad de Cuenta

Revisar cambios de tipo y moneda en `CuentaService` cuando existe historial financiero. Definir una regla mínima de dominio antes de implementarla.

### P1 — Ciclo durante pagos

La generación de ciclos/cuotas ya funciona. Falta definir cómo se comportan pagos respecto de vencimiento, mora, gracia y días no hábiles.

No inventar reglas de negocio.

### P1 — Multidivisa de tarjetas

Consumo y obligación conservan moneda; pagos exigen coincidencia de moneda. Falta definir el límite de una tarjeta frente a consumos en monedas distintas.

No introducir conversiones implícitas.

### P1 — Financiación avanzada

Pendiente: intereses, CFT, cuotas variables, adelantos, refinanciación, anulaciones y ajustes.

### P2 — UI específica de tarjetas

Límite/disponible, consumos, ciclos, cierres, vencimientos, deuda y pagos reales.

### P2 — Pasivos, patrimonio y análisis

Pasivos/patrimonio neto, histórico, vencimientos, resúmenes y dashboard.

### P3 — Pulido de consola

Baja prioridad.

## Pendientes que la auditoría descartó como independientes

- integración básica de ciclos con consumos/obligaciones: implementada;
- generación automática de cuotas: implementada;
- cruce de año: cubierto;
- atomicidad básica de compra con tarjeta: implementada y testeada;
- unificación básica del saldo monetario de tarjeta: coherente en el modelo actual;
- JAR ejecutable: implementado y probado.

## Orden exacto de ejecución

1. Movimiento ↔ obligación.
2. Autorización de `ObligacionService`.
3. Pago real desde UI.
4. Integridad de `Cuenta`.
5. Reglas de ciclo durante pagos.
6. Multidivisa.
7. Financiación.
8. UI específica.
9. Pasivos/patrimonio/análisis.
10. Pulido.

## Regla de continuidad

Código y tests prevalecen sobre documentación histórica. Antes de cada cambio: revisar implementación, clases relacionadas, repositorios, tests y reglas de negocio. Luego: cambio mínimo → tests específicos → relacionados → suite → diff → diff-check → status → documentación.

No modificar `main` automáticamente y no asumir resultados locales no informados.