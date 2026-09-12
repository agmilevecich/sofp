# SOFP — Contexto para continuar con ChatGPT

## Estado actual — 12/09/2026

La fuente de verdad es el código, Git y los tests actuales; `docs/` es documentación auxiliar. Antes de proponer cambios, reconstruir siempre el estado desde GitHub.

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

Último commit de código: `6c1b896` — `build: configurar jar ejecutable y dependencias`.

No se realizó merge a `main`.

## Validación más reciente

Suite general informada por el usuario: **690/690**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.

El JAR ejecutable fue probado con `java -Dsofp.dev=true -jar target/SOFP-1.0-SNAPSHOT.jar`.

## Estado funcional

- Shell Swing integrado.
- Gastos con selección explícita de tarjeta activa.
- Compra con tarjeta → movimiento + obligación + cuotas.
- Atomicidad de compra con tarjeta implementada y testeada.
- Ciclos y cuotas con cruce de año implementados.
- Pago coordinado de tarjeta implementado en `PagoTarjetaService`.
- Moneda explícita en movimientos y obligaciones; sin conversión automática.
- H2 de aplicación por TCP y tests aislados en H2 memoria.
- JAR ejecutable configurado.

## Auditoría vigente

### P0 — Integridad movimiento ↔ obligación

Bloquear modificación de importe, fecha/hora, tipo y eliminación de un movimiento que sea origen de una obligación. Agregar tests de cada caso y verificar persistencia.

### P0 — Autorización de obligaciones

Revisar operaciones públicas de `ObligacionService` sin `usuarioId`. Hacer internos los métodos de coordinación o exigir autorización explícita en operaciones públicas.

### P0 — Pago real en UI

`ObligacionesPanel` debe dejar de registrar solamente la reducción de deuda y pasar a usar `PagoTarjetaService`, con cuenta pagadora y categoría. El flujo debe registrar salida real de fondos y deuda en la misma transacción.

### P1 — Integridad de Cuenta

Revisar cambios de tipo y moneda de cuentas con historial financiero.

### P1 — Ciclo aplicado al pago

Definir antes de implementar reglas de vencimiento, mora, gracia y días no hábiles.

### P1 — Multidivisa

Definir tratamiento definitivo del límite de tarjeta frente a consumos en distintas monedas. No introducir conversiones implícitas.

### P1 — Financiación avanzada

Intereses, CFT, cuotas variables, adelantos, refinanciación, anulaciones y ajustes.

### P2 — UI específica de tarjetas

Límite/disponible, consumos, ciclos, vencimientos, deuda y pagos reales.

### P2 — Pasivos/patrimonio y análisis

Pasivos, patrimonio neto, histórico, vencimientos, resúmenes y dashboard.

## Orden exacto para continuar

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

## Protocolo

Antes de cada bloque: revisar implementación, clases relacionadas, repositorios, tests y reglas de negocio. Luego cambio mínimo → tests específicos → relacionados → suite → diff → diff-check → status → documentación.

No modificar `main`, no asumir tests locales no informados y no considerar cerrada una funcionalidad solo porque compila.