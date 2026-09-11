# SOFP — Pendientes

## Estado — 11/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

El bloque de selección explícita de tarjeta de crédito quedó cerrado y validado. No se realizó merge a `main`.

## Último bloque cerrado

### Selección explícita de tarjeta de crédito en Gastos

**Completado y validado.**

Cuando `FormaPago.TARJETA_CREDITO` está seleccionada, `GastosPanel` ofrece únicamente cuentas activas con `TipoCuenta.TARJETA_CREDITO`. La tarjeta seleccionada se pasa como `Cuenta` al registro del gasto.

Se agregaron tests específicos para tarjetas activas y cuentas de otros tipos, y se ajustó el orden de interacción de los tests existentes para respetar el nuevo flujo de la UI.

La suite general quedó en **688/688**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.

## Pendientes en orden

1. **Integrar ciclos de facturación y vencimientos con consumos/obligaciones.**
2. Unificar el tratamiento del saldo de tarjetas entre `MovimientoService` y `CuentaService`.
3. Profundizar pagos de tarjeta y liberación de crédito, incluyendo reglas de ciclo y moneda.
4. Ampliar pasivos y patrimonio neto.
5. Análisis histórico, vencimientos, resúmenes y dashboard.
6. Pulido de consola, de baja prioridad.

## Nota sobre UI de tarjetas

Una tarjeta de crédito es una `Cuenta` con `TipoCuenta.TARJETA_CREDITO`. No se creó una entidad nueva para seleccionar la tarjeta. `GastosPanel` selecciona una cuenta de ese tipo y la pasa a `GastoService`.

La UI no debe duplicar reglas financieras: el servicio continúa siendo responsable de registrar el gasto, validar y generar la obligación/cuotas.

## Integración

No hacer merge a `main` automáticamente ni crear ramas nuevas salvo indicación explícita.

Antes de cerrar un bloque: tests específicos → relacionados → suite general cuando corresponda → `git diff` → `git diff --check` → `git status` → documentación.

## Continuidad

En una nueva sesión reconstruir siempre desde GitHub: rama → commits → comparación con `main` → README/docs → código → tests → último resultado conocido → próximo paso.
