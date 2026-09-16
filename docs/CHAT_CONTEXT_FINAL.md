# SOFP — Contexto final de continuidad

## Estado auditado — 16/09/2026

La fuente de verdad es el código, Git y los tests actuales; `docs/` es documentación auxiliar.

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell` → último commit de código `48cf588`.

No se realizó merge a `main`.

## Último bloque cerrado

Se integró el cierre de ciclo desde `ObligacionesPanel` mediante el botón `Cerrar ciclo`. El panel delega en `ObligacionService`, utiliza el ciclo persistido de la obligación y refresca la UI.

Commits:

- `a5e6a86` — `feat: permitir cerrar ciclo desde obligaciones`.
- `48cf588` — `test: cubrir cierre de ciclo desde obligaciones`.

## Validación final conocida

- `mvn test`: **744/744**.
- 0 failures, 0 errors, 0 skipped.
- `BUILD SUCCESS`.
- Finalizada **16/09/2026 18:47:51 -03:00**.
- Tiempo total **10:32 min**.
- `ObligacionesPanelTest`: **6/6**, `BUILD SUCCESS`, finalizada **18:22:54 -03:00**.

El usuario informó `git status` limpio, `git diff` vacío y `git diff --check` sin observaciones.

## Multidivisa

La deuda original, la valorización de cierre y la liquidación son conceptos separados. Se mantienen moneda original y moneda de liquidación, cotización histórica explícita, liquidación trazable, saldo de liquidación, pagos multidivisa, valorización histórica para crédito y reducción proporcional tras pagos parciales. No existen conversiones implícitas. La falta de cotización histórica necesaria para cierre provoca error y rollback.

## Próximo paso

Diseñar el flujo de obtención/registro de valorización de cierre dentro de la aplicación, definir obligaciones multidivisa todavía no valorizadas al cierre y completar persistencia/UI del flujo integral de cierre y pago multidivisa.

## Estabilización futura

Antes del fast-forward a `main`, y separada del desarrollo funcional actual: arranque automático de H2 desde Java; cierre limpio de H2; consola silenciosa; logging técnico a archivo; errores de conexión/arranque mediante `JOptionPane`; y ningún `MainFrame` parcialmente inicializado si el arranque falla.

## Protocolo

Antes de cada cambio: rama → commits → comparación con `main` → documentación → código → tests → último resultado informado. Después: cambio mínimo → tests específicos → relacionados → suite → diff → diff-check → status → documentación.
