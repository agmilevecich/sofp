# SOFP — Pendientes

## Estado — 11/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

Antes de esta actualización documental, GitHub verificó `feature/swing-shell` en `34eb4cc`, **495 commits adelante y 0 atrás** respecto de `main`. No se realizó merge.

## Último bloque cerrado

### H2 persistente por TCP

**Completado y validado.**

La aplicación utiliza `jdbc:h2:tcp://localhost/./database/sofp` y puede compartir la base persistente con H2 Console.

El usuario verificó manualmente SOFP + H2 Console simultáneamente y luego ejecutó la suite general con resultado **687/687**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.

Los tests continúan usando H2 en memoria mediante su configuración de test independiente.

## Pendientes en orden

1. **Agregar selección explícita de tarjeta de crédito en `GastosPanel`** cuando `FormaPago.TARJETA_CREDITO` esté seleccionada.
2. Verificar mediante tests que solo se ofrezcan tarjetas activas de tipo `TARJETA_CREDITO` y que la tarjeta seleccionada sea la cuenta utilizada por `GastoService`.
3. Integrar ciclos de facturación y vencimientos con consumos/obligaciones.
4. Unificar el tratamiento del saldo de tarjetas entre `MovimientoService` y `CuentaService`.
5. Profundizar pagos de tarjeta y liberación de crédito, incluyendo reglas de ciclo y moneda.
6. Ampliar pasivos y patrimonio neto.
7. Análisis histórico, vencimientos, resúmenes y dashboard.
8. Pulido de consola, de baja prioridad.

## Nota sobre UI de tarjetas

Una tarjeta de crédito es una `Cuenta` con `TipoCuenta.TARJETA_CREDITO`. No se prevé crear una entidad nueva para seleccionar la tarjeta. El objetivo es que `GastosPanel` seleccione una cuenta de ese tipo y la pase a `GastoService`.

La UI no debe duplicar reglas financieras: el servicio continúa siendo responsable de registrar el gasto, validar y generar la obligación/cuotas.

## Integración

No hacer merge a `main` automáticamente ni crear ramas nuevas salvo indicación explícita.

Antes de cerrar un bloque: tests específicos → relacionados → suite general cuando corresponda → `git diff` → `git diff --check` → `git status` → documentación.

## Continuidad

En una nueva sesión reconstruir siempre desde GitHub: rama → commits → comparación con `main` → README/docs → código → tests → último resultado conocido → próximo paso.
