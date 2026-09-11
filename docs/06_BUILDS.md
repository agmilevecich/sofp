# SOFP — Historial de Builds

## Build actual — cierre de cobertura de obligaciones/cuotas

**Estado: COMPLETADO Y VALIDADO.**

Último commit: `44661fb` — `test: cubrir cuotas al cruzar fin de año`.

El último cambio agregó cobertura de cuotas al cruzar el fin de año, manteniendo el comportamiento existente de ciclos de facturación.

## Validación — 11/09/2026

Suite general ejecutada por el usuario:

- `mvn test`;
- **689/689**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración **10:22 min**;
- finalización **20:11:17 -03:00**.

Validación relacionada previa al cierre:

- **49/49** tests de obligaciones/cuotas/servicios;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`.

## Persistencia y H2

La aplicación SOFP utiliza H2 mediante servidor TCP con:

`jdbc:h2:tcp://localhost/./database/sofp`

SOFP y H2 Console utilizan la misma base persistente. Los tests conservan su propio `persistence.xml` con H2 en memoria.

## Bloques funcionales consolidados

Fondos insuficientes, categorías con movimientos, Gastos, Ingresos, FormaPago, Obligaciones/pagos, autorización por usuario, Transferencias, moneda explícita, crédito/límite de tarjeta, ciclos básicos de facturación, cuotas/financiación inicial, selección explícita de tarjeta de crédito y cobertura de cuotas al cruzar año.

## Fase 8 — Swing

La Fase 8 integra Inicio, Cuentas, Categorías, Ingresos, Gastos, Movimientos, Inversiones, Reportes, Obligaciones y Transferencias mediante `MainFrame`, `SidebarPanel` y `CardLayout`.

`GastosPanel` permite seleccionar forma de pago, cantidad de cuotas y, para `TARJETA_CREDITO`, la tarjeta activa utilizada.

## Próximo bloque

Auditar los pendientes contra el código actual. El candidato funcional principal es integrar ciclos de facturación y vencimientos con consumos, obligaciones y pagos, si la auditoría confirma que continúa pendiente.

Después: unificación del saldo de tarjetas, profundización de pagos/liberación de crédito, pasivos/patrimonio y análisis histórico/dashboard.

No hacer merge a `main` automáticamente.
