# SOFP — Pendientes

## Estado — 10/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

La comparación verificada antes de la actualización documental indicó **470 commits adelante y 0 atrás** respecto de `main`. No se realizó merge a `main`.

El último commit funcional antes de la actualización documental fue `51d4afe` — `fix: ajustar test de cuotas al registro automatico`.

## Último bloque cerrado

### Cuotas automáticas en gastos con tarjeta

**Completado y validado.**

`GastoService` genera automáticamente las cuotas solicitadas al registrar una compra con `TARJETA_CREDITO`. La generación se realiza dentro de la transacción de la obligación y las cuotas quedan persistidas.

`PagoTarjetaServiceTest` fue corregido para utilizar el registro real con tres cuotas, eliminando la generación manual duplicada que provocaba `La obligación ya tiene cuotas generadas`.

## Validación actual conocida

Suite general:

- `mvn test` — **671/671**;
- Failures 0;
- Errors 0;
- Skipped 0;
- `BUILD SUCCESS`;
- 10/09/2026 10:39:38 -03:00.

Suite relacionada:

- `GastosPanelTest,GastoServiceTest,ObligacionTest,ObligacionCuotasTest,PagoTarjetaServiceTest` — **32/32**;
- Failures 0;
- Errors 0;
- Skipped 0;
- `BUILD SUCCESS`;
- 10/09/2026 13:14:29 -03:00;
- duración 01:19 min.

Focalizados:

- `PagoTarjetaServiceTest`: 4/4.
- `GastosPanelTest`: 6/6.

## Pendientes en orden

1. **Revisar `GastosPanelTest` y las convenciones actuales de cuentas/paneles antes de modificar la UI.** ← próximo paso.
2. **Agregar selección explícita de tarjeta de crédito en `GastosPanel`** cuando `FormaPago.TARJETA_CREDITO` esté seleccionada.
3. Verificar mediante tests que solo se ofrezcan cuentas activas de tipo `TARJETA_CREDITO` y que la tarjeta seleccionada sea la cuenta utilizada por `GastoService`.
4. Integrar ciclos de facturación y vencimientos con consumos/obligaciones.
5. Unificar el tratamiento del saldo de tarjetas entre `MovimientoService` y `CuentaService`.
6. Profundizar pagos de tarjeta y liberación de crédito, incluyendo reglas de ciclo y moneda.
7. Ampliar pasivos y patrimonio neto.
8. Análisis histórico, vencimientos, resúmenes y dashboard.
9. Pulido de consola, de baja prioridad.

## Nota sobre UI de tarjetas

Una tarjeta de crédito es una `Cuenta` con `TipoCuenta.TARJETA_CREDITO`. No se prevé crear una entidad nueva para seleccionar la tarjeta. El objetivo es que `GastosPanel` seleccione una cuenta de ese tipo y la pase a `GastoService`.

La UI no debe duplicar reglas financieras: el servicio continúa siendo responsable de registrar el gasto, validar y generar la obligación/cuotas.

## Integración

No hacer merge a `main` automáticamente ni crear ramas nuevas salvo indicación explícita.

Antes de cerrar un bloque: tests específicos → relacionados → suite general cuando corresponda → `git diff` → `git diff --check` → `git status` → documentación.

## Continuidad

En una nueva sesión reconstruir siempre desde GitHub: rama → commits → comparación con `main` → README/docs → código → tests → último resultado conocido → próximo paso.
