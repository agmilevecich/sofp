# SOFP — Diseño y adaptación de tarjetas de crédito

## Estado — 10/09/2026

**Rama:** `feature/swing-shell`

Este documento separa explícitamente lo implementado de lo pendiente.

## 1. Modelo

Una tarjeta de crédito es una `Cuenta` con `TipoCuenta.TARJETA_CREDITO`. Actualmente tiene límite, día de cierre y día de vencimiento.

La deuda se representa mediante `Obligacion` y el consumo mediante `Movimiento`.

El pago coordinado de una tarjeta se representa mediante un egreso real sobre la cuenta pagadora y la reducción de la `Obligacion` asociada.

## 2. Moneda — IMPLEMENTADO

La moneda del consumo se conserva en la obligación. Una compra ARS genera obligación ARS y una compra USD genera obligación USD. No se realiza conversión automática al crear la obligación.

`ObligacionesPanel` muestra importe y saldo pendiente con código de moneda y formato estable mediante `Locale.ROOT`.

Los pagos coordinados de tarjeta requieren que la cuenta pagadora y la obligación utilicen la misma moneda. No se realiza conversión automática entre monedas.

## 3. Crédito disponible y límite — IMPLEMENTADO EN SU PRIMER CRITERIO

Criterio actual:

`crédito disponible = límite de crédito − consumos de tarjeta pendientes en la moneda de la tarjeta`

Se valida el límite al registrar consumos y al modificar importe/tipo. Los consumos con tarjeta no afectan el saldo monetario de la cuenta.

Los pagos de obligaciones reducen el saldo pendiente y, por lo tanto, liberan nuevamente crédito disponible.

El tratamiento multidivisa definitivo del límite todavía no está cerrado: no debe asumirse un límite USD independiente ni realizar conversiones implícitas.

## 4. Ciclo de facturación — BASE DE DOMINIO IMPLEMENTADA E INTEGRADA EN LA OBLIGACIÓN

`CicloFacturacion` es un objeto de dominio no persistente.

`Cuenta.calcularCicloFacturacion(LocalDate)`:

- incluye el día exacto de cierre en el ciclo que cierra ese día;
- asigna el día posterior al ciclo siguiente;
- calcula inicio como el día posterior al cierre anterior;
- ajusta días inexistentes al último día real del mes;
- calcula vencimiento según día configurado y siguiente mes cuando corresponde;
- resuelve cambio de año.

`Obligacion.getCicloFacturacion()` deriva el ciclo a partir de la cuenta y fecha del movimiento de origen, sin duplicar la información del ciclo en la obligación.

`CicloFacturacionTest` contiene **11 tests** para estos casos.

La integración con el flujo de consumo/obligación ya está realizada. La relación del pago con el ciclo todavía no se persiste como dato independiente.

## 5. Vencimiento

La fecha de vencimiento se calcula como parte de `CicloFacturacion` y puede consultarse desde la obligación mediante su ciclo de facturación.

La política de días no hábiles todavía no está definida.

## 6. Pagos — IMPLEMENTADO EN FLUJO COORDINADO

`PagoTarjetaService` coordina en una única transacción:

1. localizar y autorizar la obligación;
2. validar la cuenta pagadora, categoría, perfil financiero y moneda;
3. verificar que la cuenta pagadora esté activa;
4. verificar que el importe sea positivo y no supere el saldo pendiente;
5. verificar saldo monetario suficiente en la cuenta pagadora;
6. registrar un egreso `Movimiento` en la cuenta pagadora;
7. registrar el pago sobre la `Obligacion`;
8. confirmar ambas modificaciones en la misma transacción.

El pago puede ser parcial o total. Un pago total deja la obligación en estado `PAGADA` y libera nuevamente el crédito utilizado por la tarjeta.

Los pagos en moneda diferente de la deuda se rechazan explícitamente; no existe conversión automática.

La operación mantiene separadas la cuenta pagadora, el consumo de tarjeta, la obligación y el movimiento monetario que representa la salida real de fondos.

La implementación actual no crea un movimiento monetario de destino sobre la tarjeta: el pago se registra como salida de fondos de la cuenta pagadora y reducción de la obligación.

## 7. Cuotas y financiación

Pendiente. Debe definirse antes importe de cuota, intereses, asignación a ciclos, compromiso inicial del límite, pagos parciales, anulaciones y ajustes.

## 8. Saldo monetario — UNIFICADO

El tratamiento de los consumos con `TARJETA_CREDITO` es coherente entre `MovimientoService` y `CuentaService`: las compras con tarjeta no reducen el saldo monetario de la cuenta, porque generan una obligación y afectan el crédito disponible de la tarjeta.

Los egresos ordinarios sí reducen el saldo monetario y los ingresos lo incrementan.

## 9. Seguridad

Las operaciones de tarjeta deben respetar aislamiento por usuario/perfil y autorización en servicios/repositorios. Swing no debe ser la barrera de seguridad.

`PagoTarjetaService` valida la pertenencia de la obligación, cuenta pagadora y categoría al mismo perfil financiero antes de modificar datos.

## 10. UI

La UI específica de tarjetas queda para después de estabilizar las reglas de dominio. Debe permitir progresivamente consultar tarjeta, límite/disponible, consumos, moneda, ciclo, cierre, vencimiento, deuda y pagos.

## 11. Orden de trabajo actualizado

1. ~~Integrar `CicloFacturacion` con consumos y obligaciones.~~ **Completado.**
2. ~~Unificar saldo monetario de tarjetas entre `MovimientoService` y `CuentaService`.~~ **Completado.**
3. ~~Implementar flujo coordinado de pagos y liberación de crédito.~~ **Completado en su primera versión.**
4. Profundizar reglas de pagos, especialmente relación con ciclos y reglas multidivisa que puedan requerirse.
5. Definir e implementar cuotas/financiación.
6. Construir UI específica de tarjetas.
7. Ampliar pruebas de seguridad, persistencia, monedas, ciclos, pagos y casos límite.

## 12. Principios

- código y tests prevalecen sobre documentación;
- cambios mínimos;
- no duplicar núcleos financieros;
- no convertir monedas automáticamente para ocultar diferencias;
- separar instrumento, consumo, deuda y pago;
- reglas de negocio fuera de Swing;
- no tratar decisiones abiertas como definitivas.

## 13. Validación actual

Suite general: **671/671**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, ejecutada el 10/09/2026 10:39:38 -03:00.

`PagoTarjetaServiceTest`: **3/3** en la validación focalizada.

Tests relacionados (`TarjetaCreditoPagoCreditoTest`, `SaldoTarjetaCreditoTest`, `GastoServiceTest`): **11/11**, 0 failures, 0 errors, 0 skipped.

`CicloFacturacionTest`: **11/11** tests incluidos en la suite general.
