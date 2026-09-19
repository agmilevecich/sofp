# SOFP — Continuidad 2026-09-11

## Estado verificado

- Rama de trabajo: `feature/swing-shell`
- `main`: `a4be85913847200cb70976d5266d9cbba10b3100`
- Último commit funcional/test: `44661fb` — `test: cubrir cuotas al cruzar fin de año`.
- GitHub verifica la rama 514 commits adelante y 0 atrás respecto de `main`.
- No se realizó merge a `main`.

## Última validación

El usuario ejecutó `mvn test` el **11/09/2026 20:11:17 -03:00**:

- **689/689** tests;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración **10:22 min**.

También ejecutó los tests relacionados de obligaciones/cuotas/servicios:

- **49/49** tests;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`.

## Último cambio

Se agregó cobertura para cuotas al cruzar el fin de año en `ObligacionCuotasTest`:

- compra `2026-12-16`;
- cuota 1: cierre `2027-01-15`, vencimiento `2027-02-10`;
- cuota 2: cierre `2027-02-15`, vencimiento `2027-03-10`;
- cuota 3: cierre `2027-03-15`, vencimiento `2027-04-10`.

## Estado funcional

El shell Swing integra Inicio, Cuentas, Categorías, Ingresos, Gastos, Movimientos, Inversiones, Reportes, Obligaciones y Transferencias.

`GastosPanel` permite seleccionar la tarjeta activa cuando la forma de pago es `TARJETA_CREDITO`. La tarjeta se pasa como `Cuenta` al `GastoService`.

`GastoService` registra el movimiento de egreso y, para tarjeta de crédito, genera la obligación y las cuotas solicitadas dentro de la transacción.

Las tarjetas son `Cuenta` con `TipoCuenta.TARJETA_CREDITO`, límite, cierre y vencimiento.

`CicloFacturacion` está implementado como objeto de dominio no persistente y cuenta con cobertura de fechas, meses cortos y cruce de año. Su integración completa con consumos, obligaciones y pagos sigue siendo un pendiente a auditar.

## Estado Git

El usuario informó después de sincronizar:

- `git status`: working tree limpio;
- rama sincronizada con `github/feature/swing-shell`.

La comparación `main...feature/swing-shell` realizada sobre GitHub confirma:

- 514 commits adelante;
- 0 atrás;
- 109 archivos modificados respecto de `main`;
- `git diff --check`: sin salida.

## Próximo paso

Auditar `docs/08_PENDIENTES.md` contra el código y los 689 tests actuales para determinar qué pendientes siguen siendo funcionales reales y cuáles ya fueron resueltos o deben quedar como mejoras futuras.

El candidato funcional principal continúa siendo la integración completa de ciclos de facturación/vencimientos con consumos, obligaciones y pagos, pero no se debe modificar código hasta verificar que sigue siendo necesario.

## Regla de continuidad

Código y tests actuales prevalecen sobre documentación histórica. No modificar `main`, no crear ramas nuevas salvo indicación explícita y no asumir resultados locales no informados.
