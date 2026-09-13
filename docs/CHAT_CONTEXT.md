# SOFP — Contexto para continuar con ChatGPT

## Estado actual — 13/09/2026

La fuente de verdad es el código, los tests y Git. `docs/` es documentación auxiliar y ante contradicción prevalecen código y tests.

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

**Últimos commits funcionales:**

- `a2a96bb` — `fix: exigir usuario al registrar pagos de obligaciones`;
- `2813fa3` — `test: adaptar pagos de obligaciones a usuario autorizado`.

Los commits posteriores de documentación actualizan el estado de continuidad en la rama activa. No se realizó merge a `main`.

## Validación más reciente

Suite general informada por el usuario: **696/696**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.

Suite específica de `ObligacionService`: **9/9**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.

Suite relacionada: **69/69**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`. En esa ejecución apareció un warning de Surefire por demora en la terminación de la JVM, sin fallo de tests.

Tests específicos de UI de pago: **6/6**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.

El usuario informó además que `git syncsofp`, `git diff`, `git diff --check` y `git status` dejaron la rama sincronizada y el working tree limpio.

## Estado consolidado

La Fase 8 Swing está integrada. Gastos con tarjeta generan movimiento + obligación + cuotas en una operación coordinada. El pago coordinado existe en `PagoTarjetaService` y ya está integrado en `ObligacionesPanel`, `MainFrame` y `Main`.

El pago desde UI selecciona cuenta pagadora y categoría y registra salida real de fondos junto con la reducción de deuda mediante el servicio coordinador.

`ObligacionService` ya no expone el registro de pagos sin usuario: la operación pública exige `usuarioId` y valida que la obligación pertenezca al perfil autorizado.

H2 aplicación: `jdbc:h2:tcp://localhost/./database/sofp`. Tests: H2 memoria independiente.

## Pendientes reales

P0: ninguno en el bloque de autorización de obligaciones; queda cerrado.

P1:

1. Proteger cambios de tipo/moneda de `Cuenta` cuando exista historial financiero.
2. Definir reglas de ciclo aplicadas al pago: vencimiento, mora, gracia y días no hábiles.
3. Definir multidivisa de tarjetas sin conversiones implícitas.
4. Definir financiación avanzada.

P2/P3:

5. UI específica de tarjetas.
6. Pasivos, patrimonio, histórico, vencimientos, resúmenes y dashboard.
7. Gestión de entidades financieras: todavía no existe un panel específico; queda pendiente de definición e implementación.
8. Pulido de consola.

## Reglas

No duplicar reglas de negocio en Swing. Mantener autorización en servicios/repositorios. No inventar reglas multidivisa o de mora. Cada bloque debe incluir tests y validación de persistencia cuando corresponda.

## Continuidad

Reconstruir siempre desde GitHub antes de cambios: rama → commits → comparación con `main` → documentación → código → tests → último resultado → próximo paso.

La documentación de continuidad se actualiza sobre la rama activa. No mantener una rama documental permanente separada salvo que se cree temporalmente para una necesidad concreta.

No modificar `main`, no asumir resultados locales no informados y no considerar terminado un bloque porque compila.

## Próximo paso recomendado

Auditar `CuentaService` y las entidades relacionadas para definir la protección mínima de cambios de tipo/moneda cuando exista historial financiero. Antes de modificarla, revisar implementación, repositorios, relaciones y tests actuales, manteniendo el cambio mínimo y sin inventar reglas de negocio.
