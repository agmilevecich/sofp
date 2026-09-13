# SOFP — Contexto para continuar con ChatGPT

## Estado actual — 13/09/2026

La fuente de verdad es el código, los tests y Git. `docs/` es documentación auxiliar y ante contradicción prevalecen código y tests.

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.
**Rama documental de continuidad:** `docs/continuidad-sofp`.

**Último commit de código:** `41ebb2b14b79efe5c61926d95306820dcd7069ea` — `test: corregir saldo esperado en pago de tarjeta`.

No se realizó merge a `main`.

## Validación más reciente

Suite general informada por el usuario: **696/696**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.

Suite relacionada: **69/69**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.

Tests específicos de UI de pago: **6/6**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.

El usuario informó además que `git syncsofp`, `git diff`, `git diff --check` y `git status` dejaron la rama sincronizada y el working tree limpio.

## Estado consolidado

La Fase 8 Swing está integrada. Gastos con tarjeta generan movimiento + obligación + cuotas en una operación coordinada. El pago coordinado existe en `PagoTarjetaService` y ya está integrado en `ObligacionesPanel`, `MainFrame` y `Main`.

El pago desde UI selecciona cuenta pagadora y categoría y registra salida real de fondos junto con la reducción de deuda mediante el servicio coordinador.

H2 aplicación: `jdbc:h2:tcp://localhost/./database/sofp`. Tests: H2 memoria independiente.

## Pendientes reales

P0:

1. Revisar operaciones públicas de `ObligacionService` que no reciben `usuarioId`, para impedir bypass de autorización.

P1:

2. Proteger cambios de tipo/moneda de `Cuenta` cuando exista historial financiero.
3. Definir reglas de ciclo aplicadas al pago: vencimiento, mora, gracia y días no hábiles.
4. Definir multidivisa de tarjetas sin conversiones implícitas.
5. Definir financiación avanzada.

P2/P3:

6. UI específica de tarjetas.
7. Pasivos, patrimonio, histórico, vencimientos, resúmenes y dashboard.
8. Gestión de entidades financieras: todavía no existe un panel específico; queda pendiente de definición e implementación.
9. Pulido de consola.

## Reglas

No duplicar reglas de negocio en Swing. Mantener autorización en servicios/repositorios. No inventar reglas multidivisa o de mora. Cada bloque debe incluir tests y validación de persistencia cuando corresponda.

## Continuidad

Reconstruir siempre desde GitHub antes de cambios: rama → commits → comparación con `main` → documentación → código → tests → último resultado → próximo paso.

No modificar `main`, no asumir resultados locales no informados y no considerar terminado un bloque porque compila.

## Próximo paso recomendado

Auditar y cerrar la superficie pública de `ObligacionService`. Antes de modificarla, revisar implementación, repositorios, clases relacionadas y tests actuales, manteniendo el cambio mínimo.
