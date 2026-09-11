# SOFP — Historial de Builds

## Build actual — H2 TCP y persistencia compartida

**Estado: COMPLETADO Y VALIDADO.**

La aplicación SOFP utiliza H2 mediante servidor TCP con:

`jdbc:h2:tcp://localhost/./database/sofp`

Esto permite utilizar SOFP y H2 Console simultáneamente sobre la misma base persistente.

Los tests conservan su propio `persistence.xml` con H2 en memoria y permanecen aislados del servidor TCP de desarrollo.

Commit funcional:

- `34eb4cc` — `config: conectar SOFP a H2 por TCP`.

## Validación — 11/09/2026

Suite general ejecutada por el usuario:

- `mvn test`;
- **687/687**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración **18:31 min**;
- finalización **13:14:41 -03:00**.

Validación manual adicional:

- H2 Server TCP activo en `localhost:9092`;
- SOFP inicia y utiliza la base persistente;
- H2 Console funciona en `localhost:8082`;
- SOFP y H2 Console acceden simultáneamente a la misma base.

## Bloques funcionales consolidados

Fondos insuficientes, categorías con movimientos, Gastos, Ingresos, FormaPago, Obligaciones/pagos, autorización por usuario, Transferencias, moneda explícita, crédito/límite de tarjeta, ciclos básicos de facturación y cuotas/financiación inicial.

## Fase 8 — Swing

La Fase 8 integra Inicio, Cuentas, Categorías, Ingresos, Gastos, Movimientos, Inversiones, Reportes, Obligaciones y Transferencias mediante `MainFrame`, `SidebarPanel` y `CardLayout`.

`GastosPanel` permite seleccionar forma de pago y cantidad de cuotas.

## Próximo bloque

Implementar selección explícita de la tarjeta de crédito utilizada cuando `FormaPago.TARJETA_CREDITO` esté seleccionada, con cobertura de tests para tarjetas activas y selección correcta.

Después: integración de ciclos con consumos/obligaciones, unificación del saldo de tarjetas, profundización de pagos/liberación de crédito y evolución de pasivos/patrimonio y análisis.

No hacer merge a `main` automáticamente.
