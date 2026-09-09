# SOFP — Continuidad 2026-09-08

> Corte histórico de continuidad. Para el estado vigente utilizar `docs/00_ESTADO_ACTUAL.md` y `docs/CHAT_CONTEXT.md`, verificando siempre código, tests y Git.

## Actualización posterior — 09/09/2026

Este documento queda como registro histórico del corte del 08/09/2026. Desde ese corte, el bloque de moneda en movimientos y obligaciones fue completado.

La rama de trabajo `feature/swing-shell` continúa separada de `main`.

El último commit funcional vigente pasó a ser:

- `13a68fb` — `fix: estabilizar formato de moneda en obligaciones`.

El bloque agregó la conservación de moneda económica del consumo en la obligación y su visualización en `ObligacionesPanel`, con formato decimal estable mediante `Locale.ROOT`.

Validaciones posteriores:

- `mvn test -Dtest=ObligacionesPanelTest` → **4/4**, BUILD SUCCESS, 01:20 min, 09/09/2026 13:05:03 -03:00.
- `mvn test` → **642/642**, BUILD SUCCESS, 09:43 min, 09/09/2026 13:15:48 -03:00.
- `git diff`, `git diff --check`, `git status` → working tree limpio y rama sincronizada.

Para continuar desde el estado vigente no utilizar los conteos, commits ni pendientes de este corte como fuente actual; reconstruir desde GitHub.
