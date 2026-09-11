# SOFP — Pendientes

## Estado — 11/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

Último commit: `44661fb` — `test: cubrir cuotas al cruzar fin de año`.

GitHub verifica que `feature/swing-shell` está 514 commits adelante y 0 atrás respecto de `main`. No se realizó merge.

La suite completa más reciente es **689/689**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.

## Último bloque cerrado

### Selección explícita de tarjeta de crédito en Gastos

Completado y validado. Cuando `FormaPago.TARJETA_CREDITO` está seleccionada, `GastosPanel` ofrece únicamente cuentas activas con `TipoCuenta.TARJETA_CREDITO`. La tarjeta seleccionada se pasa como `Cuenta` al registro del gasto.

## Último bloque de cobertura cerrado

### Cuotas al cruzar fin de año

`ObligacionCuotasTest` cubre ahora una compra del `2026-12-16` con tres cuotas y verifica correctamente los ciclos y vencimientos de enero, febrero, marzo y abril de 2027.

El bloque de tests relacionados quedó en **49/49** y la suite general en **689/689**, ambos con `BUILD SUCCESS`.

## Pendientes en orden

1. **Integrar ciclos de facturación y vencimientos con consumos, obligaciones y pagos.**
2. Auditar y unificar el tratamiento del saldo de tarjetas entre `MovimientoService` y `CuentaService`.
3. Profundizar pagos de tarjeta y liberación de crédito, incluyendo reglas de ciclo y moneda.
4. Ampliar pasivos y patrimonio neto.
5. Análisis histórico, vencimientos, resúmenes y dashboard.
6. Pulido de consola, de baja prioridad.

## Estado de la etapa Swing Shell

La etapa tiene el shell principal implementado, integración de gastos con tarjeta, obligaciones/cuotas y cobertura amplia de UI, dominio, servicios y persistencia.

Antes de iniciar otro cambio funcional se debe auditar cada pendiente contra el código y los tests actuales. No asumir que un pendiente documental continúa siendo necesario si el código ya lo resolvió.

## Integración

No hacer merge a `main` automáticamente ni crear ramas nuevas salvo indicación explícita.

Antes de cerrar un bloque: tests específicos → relacionados → suite general cuando corresponda → `git diff` → `git diff --check` → `git status` → documentación.

## Continuidad

En una nueva sesión reconstruir siempre desde GitHub: rama → commits → comparación con `main` → README/docs → código → tests → último resultado conocido → próximo paso.
