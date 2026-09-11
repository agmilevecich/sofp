# SOFP — Historial de Builds

## Build actual — Selección explícita de tarjeta de crédito

**Estado: COMPLETADO Y VALIDADO.**

`GastosPanel` filtra las cuentas disponibles al seleccionar `FormaPago.TARJETA_CREDITO` y ofrece únicamente cuentas activas con `TipoCuenta.TARJETA_CREDITO`.

La tarjeta seleccionada se utiliza como `Cuenta` al registrar el gasto. Las cuentas de otros tipos no participan de esa selección.

Commits funcionales del bloque:

- `eca75f7` — `feat: filtrar cuentas al elegir tarjeta de credito`;
- `2f9ad93` — `test: cubrir seleccion de tarjeta en gastos`;
- `4b26734` — `test: ajustar orden de seleccion en gastos`.

## Validación — 11/09/2026

Suite general ejecutada por el usuario:

- `mvn test`;
- **688/688**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración **12:52 min**;
- finalización **14:10:20 -03:00**.

Validación específica previa:

- `mvn -Dtest=GastosPanelTest,GastosPanelTarjetaCreditoTest test`;
- **8/8**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`.

Validación final de Git informada por el usuario:

- `git diff`: sin cambios;
- `git diff --check`: sin errores;
- `git status`: working tree limpio;
- rama sincronizada con `github/feature/swing-shell`.

## Persistencia y H2

La aplicación SOFP utiliza H2 mediante servidor TCP con:

`jdbc:h2:tcp://localhost/./database/sofp`

Esto permite utilizar SOFP y H2 Console simultáneamente sobre la misma base persistente.

Los tests conservan su propio `persistence.xml` con H2 en memoria y permanecen aislados del servidor TCP de desarrollo.

## Bloques funcionales consolidados

Fondos insuficientes, categorías con movimientos, Gastos, Ingresos, FormaPago, Obligaciones/pagos, autorización por usuario, Transferencias, moneda explícita, crédito/límite de tarjeta, ciclos básicos de facturación, cuotas/financiación inicial y selección explícita de tarjeta de crédito en Gastos.

## Fase 8 — Swing

La Fase 8 integra Inicio, Cuentas, Categorías, Ingresos, Gastos, Movimientos, Inversiones, Reportes, Obligaciones y Transferencias mediante `MainFrame`, `SidebarPanel` y `CardLayout`.

`GastosPanel` permite seleccionar forma de pago, cantidad de cuotas y, para `TARJETA_CREDITO`, la tarjeta activa utilizada.

## Próximo bloque

Integrar ciclos de facturación y vencimientos con consumos/obligaciones y pagos. Después: unificación del saldo de tarjetas, profundización de pagos/liberación de crédito, pasivos/patrimonio y análisis histórico/dashboard.

No hacer merge a `main` automáticamente.
