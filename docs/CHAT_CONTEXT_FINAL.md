# SOFP — Contexto final de continuidad

## Estado — 13/09/2026

La fuente de verdad es el código, Git y los tests actuales; `docs/` es documentación auxiliar. Antes de proponer cambios, reconstruir siempre el estado desde GitHub.

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

**Últimos commits funcionales:**

- `a2a96bb` — `fix: exigir usuario al registrar pagos de obligaciones`;
- `2813fa3` — `test: adaptar pagos de obligaciones a usuario autorizado`.

Los commits posteriores de documentación registran el cierre del bloque. No se realizó merge a `main`.

## Validación más reciente

Suite general informada por el usuario: **696/696**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.

Suite específica de `ObligacionService`: **9/9**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.

Suite relacionada de obligaciones/pagos/UI: **69/69**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.

Tests específicos de UI de pago: **6/6**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.

La suite relacionada mostró un warning de Surefire por demora en la terminación de la JVM después de `System.exit(0)`, pero terminó con `BUILD SUCCESS` y sin tests fallidos.

El usuario informó `git syncsofp`, `git diff`, `git diff --check` y `git status` correctos, con working tree limpio y rama sincronizada.

## Estado funcional

- Shell Swing integrado.
- Gastos con selección explícita de tarjeta activa.
- Compra con tarjeta → movimiento + obligación + cuotas.
- Atomicidad de compra con tarjeta implementada y testeada.
- Ciclos y cuotas con cruce de año implementados.
- Protección de movimientos origen de obligaciones implementada y testeada.
- Pago coordinado de tarjeta implementado en `PagoTarjetaService`.
- Pago real desde `ObligacionesPanel` integrado y testeado.
- `PagoTarjetaService` conectado en `MainFrame` y `Main`.
- Moneda explícita en movimientos y obligaciones; sin conversión automática.
- `ObligacionService` exige `usuarioId` para registrar pagos y valida el perfil propietario.
- H2 de aplicación por TCP y tests aislados en H2 memoria.
- JAR ejecutable configurado.

## Pendientes

### P0 — Cerrado

Autorización de pagos en `ObligacionService`: eliminado el overload público sin `usuarioId`; la operación de registro de pago exige usuario autorizado.

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

### P2/P3 — Gestión de entidades financieras

Todavía no existe un panel específico. Definir e implementar cuando corresponda.

### P3 — Pulido

Mejoras de consola y presentación, sin interferir con reglas financieras.

## Orden exacto para continuar

1. Integridad de `Cuenta`.
2. Reglas de ciclo durante pagos.
3. Multidivisa.
4. Financiación.
5. UI específica.
6. Pasivos/patrimonio/análisis.
7. Gestión de entidades financieras.
8. Pulido.

## Protocolo

Antes de cada bloque: revisar implementación, clases relacionadas, repositorios, tests y reglas de negocio. Luego cambio mínimo → tests específicos → relacionados → suite → diff → diff-check → status → documentación.

La documentación de continuidad se actualiza sobre la rama activa. No modificar `main`, no asumir tests locales no informados y no considerar cerrada una funcionalidad solo porque compila.
