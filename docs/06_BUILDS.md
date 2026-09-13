# SOFP — Historial de Builds

## Cierre de etapa — 13/09/2026

**Estado: VALIDADO.**

Últimos commits de la etapa de autorización de obligaciones:

- `a2a96bb13a0d342e2ccfb6f7302ae791c1dd8147` — `fix: exigir usuario al registrar pagos de obligaciones`;
- `2813fa34c953f4f6408903c1e3b4fc2e7f3b58c5` — `test: adaptar pagos de obligaciones a usuario autorizado`.

Estos commits cierran el P0 de la superficie pública de `ObligacionService`: ya no existe el overload público de registro de pagos sin `usuarioId` y los tests utilizan la API autorizada.

La etapa anterior de integración del pago de tarjeta incluyó:

- `13dea0441718f73ddead9c3d39c35068d2a8962d` — integración del pago en obligaciones;
- `a28fce274cbb99f6f3f7226964ff4c3af15e8e99` — integración del servicio en `MainFrame`;
- `a47419f23b084f3beaec9c8035596ed1689740c2` — conexión desde `Main`;
- `8c397eaf7246f5bbc42a24278c71fb4a11692eac` — test de pago desde UI;
- `edc863d38eee12d82da9a53b4e1869b60a5581b0` — adaptación de tests de obligaciones;
- `778c089a66e20c723ce3f029e04b144b8341cb74` — corrección de tipos de combos;
- `8fdf5f599ac0036c9e334bdb31c1d55d2d1936b3` — corrección de firmas de movimientos en tests de pago;
- `5d2b8286afc428539825373e359cd35d5f891572` — corrección de firma de movimientos en tests de obligaciones;
- `41ebb2b14b79efe5c61926d95306820dcd7069ea` — corrección del saldo esperado en el test de pago.

## Validaciones informadas por el usuario

### Tests específicos de UI

`ObligacionesPanelTest,ObligacionesPanelPagoTarjetaTest`:

- **6/6**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`.

### `ObligacionServiceTest`

- **9/9**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- finalizado el **13/09/2026 16:22:17 -03:00**.

### Suite relacionada

`PagoTarjetaServiceTest,ObligacionServiceTest,MovimientoServiceTest,ObligacionesPanelTest,ObligacionesPanelPagoTarjetaTest,MainFrameObligacionesTest`:

- **69/69**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- tiempo: **10:17 min**;
- finalizado el **13/09/2026 16:35:14 -03:00**.

La ejecución mostró un warning de Surefire por demora en la terminación de la JVM después de `System.exit(0)`, pero terminó con `BUILD SUCCESS` y sin tests fallidos.

### Suite completa

`mvn test`:

- **696/696**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- tiempo: **21:14 min**;
- finalizado el **13/09/2026 14:09:41 -03:00**.

No se ejecutó una nueva suite completa después del cierre de autorización; 696/696 es el último resultado general informado.

### Verificaciones Git

Después de los cambios de código, el usuario ejecutó `git syncsofp`, `git diff`, `git diff --check` y `git status`.

Resultado informado:

- rama `feature/swing-shell`;
- sincronizada con `github/feature/swing-shell` y `bitbucket/feature/swing-shell`;
- `git diff` sin cambios;
- `git diff --check` sin errores;
- working tree limpio;
- `Everything up-to-date`.

## Bloques funcionales consolidados

Fondos insuficientes, categorías con movimientos, Gastos, Ingresos, FormaPago, Obligaciones/pagos, autorización por usuario, Transferencias, moneda explícita, crédito/límite de tarjeta, ciclos básicos de facturación, cuotas, atomicidad de compra con tarjeta, integridad del movimiento origen de obligación, selección explícita de tarjeta, pago real de tarjeta desde UI, H2 TCP y JAR ejecutable.

La autorización de pagos de `ObligacionService` queda consolidada: el registro público requiere `usuarioId` y valida el perfil propietario.

## Próximos bloques

1. proteger cambios estructurales de tipo/moneda de cuentas con historial;
2. definir reglas de ciclo aplicadas al pago;
3. definir tratamiento multidivisa de tarjetas;
4. financiación avanzada;
5. UI específica de tarjetas;
6. pasivos, patrimonio y análisis;
7. gestión de entidades financieras;
8. pulido de consola.

No se modificó `main`.
