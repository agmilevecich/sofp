# SOFP — Continuidad 2026-09-16

## Estado auditado

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.
**Último commit de código:** `50052cb` — `fix: calcular crédito multidivisa pendiente`.

La rama continúa separada de `main`; no se realizó merge.

## Bloque cerrado

Se completó y validó el tratamiento del crédito utilizado para obligaciones multidivisa valorizadas al cierre.

La regla vigente es:

- obligación en moneda de la tarjeta → usa `saldoPendiente`;
- obligación multidivisa valorizada → usa la valorización histórica de cierre proporcional al saldo original todavía pendiente;
- consumo sin obligación asociada → conserva el comportamiento existente;
- obligación multidivisa sin valorización → no se inventa una conversión.

La valorización de cierre sigue siendo independiente de la liquidación y del pago.

## Validación

Suite general ejecutada por el usuario:

- `mvn test`: **740/740**.
- Failures: 0.
- Errors: 0.
- Skipped: 0.
- `BUILD SUCCESS`.
- Finalizada: **16/09/2026 15:54:16 -03:00**.
- Tiempo total: **09:50 min**.

Pruebas específicas de la etapa:

- `TipoCambioRepositoryTest`: 6/6.
- `ObligacionServiceCierreTest`: 4/4.
- `ObligacionRepositoryTest`: 7/7.
- `MovimientoCreditoMultimonedaTest`: 1/1.
- `PagoTarjetaServiceTest`: 10/10.
- `ObligacionLiquidacionTest`: 13/13.

Validación final local informada: `git diff` vacío, `git diff --check` sin observaciones y `git status` limpio.

## Decisiones vigentes

1. La deuda original conserva su moneda original.
2. La valorización de cierre usa una cotización histórica explícita.
3. La valorización no reemplaza la liquidación.
4. No se realizan conversiones implícitas.
5. El pago posterior es independiente de la valorización de cierre.
6. Los pagos parciales reducen proporcionalmente el crédito valorizado mientras disminuye el `saldoPendiente` original.

## Pendientes reales

1. Definir el flujo de obtención y registro de la valorización de cierre dentro de la aplicación.
2. Definir qué ocurre con una obligación multidivisa todavía no valorizada al cierre.
3. Completar persistencia/UI del flujo integral de cierre y pago multidivisa.
4. Continuar luego con P2: eliminación de cuentas con historial, `Clock` y migraciones/versionado formal.
5. P3: financiación avanzada, UI específica de tarjetas, pasivos/patrimonio/análisis, entidades financieras y pulido de consola.

## Próximo paso

Antes de modificar código, reconstruir nuevamente el estado desde GitHub y revisar el flujo actual de cierre/registro en servicios, repositorios y UI. Diseñar tests de la próxima regla de negocio antes de implementarla.

## Regla de continuidad

Código actual → tests → commits → comparación con `main` → documentación → próximo paso. No modificar `main` automáticamente y no asumir resultados locales no informados.
