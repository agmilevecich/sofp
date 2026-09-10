# SOFP — Diseño y adaptación de tarjetas de crédito

## Estado — 10/09/2026

**Rama:** `feature/swing-shell`

Este documento separa explícitamente lo implementado de lo pendiente.

## 1. Modelo

Una tarjeta de crédito es una `Cuenta` con `TipoCuenta.TARJETA_CREDITO`. Actualmente tiene límite, día de cierre y día de vencimiento.

La deuda se representa mediante `Obligacion` y el consumo mediante `Movimiento`.

## 2. Moneda — IMPLEMENTADO

La moneda del consumo se conserva en la obligación. Una compra ARS genera obligación ARS y una compra USD genera obligación USD. No se realiza conversión automática al crear la obligación.

`ObligacionesPanel` muestra importe y saldo pendiente con código de moneda y formato estable mediante `Locale.ROOT`.

## 3. Crédito disponible y límite — IMPLEMENTADO EN SU PRIMER CRITERIO

Criterio actual:

`crédito disponible = límite de crédito − consumos de tarjeta pendientes en la moneda de la tarjeta`

Se valida el límite al registrar consumos y al modificar importe/tipo. Los consumos con tarjeta no afectan el saldo monetario de la cuenta.

El tratamiento multidivisa definitivo del límite todavía no está cerrado: no debe asumirse un límite USD independiente ni realizar conversiones implícitas.

## 4. Ciclo de facturación — BASE DE DOMINIO IMPLEMENTADA

`CicloFacturacion` es un objeto de dominio no persistente.

`Cuenta.calcularCicloFacturacion(LocalDate)`:

- incluye el día exacto de cierre en el ciclo que cierra ese día;
- asigna el día posterior al ciclo siguiente;
- calcula inicio como el día posterior al cierre anterior;
- ajusta días inexistentes al último día real del mes;
- calcula vencimiento según día configurado y siguiente mes cuando corresponde;
- resuelve cambio de año.

`CicloFacturacionTest` contiene **9 tests** para estos casos.

### Pendiente de integración

La lógica aún debe conectarse con el flujo real de consumos/obligaciones y posteriormente con pagos y UI. El objeto de ciclo no debe duplicarse en Swing ni en otra entidad sin necesidad.

## 5. Vencimiento

La fecha de vencimiento ya se calcula como parte de `CicloFacturacion`. La política de días no hábiles todavía no está definida.

## 6. Pagos

Los pagos de obligaciones ya existen y liberan crédito en el modelo actual. Falta profundizar el flujo específico de tarjeta, especialmente su relación con ciclos, moneda del pago y pagos en moneda diferente de la deuda.

## 7. Cuotas y financiación

Pendiente. Debe definirse antes importe de cuota, intereses, asignación a ciclos, compromiso inicial del límite, pagos parciales, anulaciones y ajustes.

## 8. Inconsistencia pendiente de saldo

Debe revisarse la diferencia documentada entre `MovimientoService` y `CuentaService`: el tratamiento de egresos con `TARJETA_CREDITO` debe ser coherente en el cálculo del saldo monetario.

## 9. Seguridad

Las operaciones de tarjeta deben respetar aislamiento por usuario/perfil y autorización en servicios/repositorios. Swing no debe ser la barrera de seguridad.

## 10. UI

La UI específica de tarjetas queda para después de estabilizar las reglas de dominio. Debe permitir progresivamente consultar tarjeta, límite/disponible, consumos, moneda, ciclo, cierre, vencimiento, deuda y pagos.

## 11. Orden de trabajo actualizado

1. Integrar `CicloFacturacion` con consumos y obligaciones.
2. Unificar saldo monetario de tarjetas entre `MovimientoService` y `CuentaService`.
3. Profundizar pagos y liberación de crédito, con reglas multidivisa explícitas.
4. Definir e implementar cuotas/financiación.
5. Construir UI específica de tarjetas.
6. Ampliar pruebas de seguridad, persistencia, monedas, ciclos, pagos y casos límite.

## 12. Principios

- código y tests prevalecen sobre documentación;
- cambios mínimos;
- no duplicar núcleos financieros;
- no convertir monedas automáticamente para ocultar diferencias;
- separar instrumento, consumo, deuda y pago;
- reglas de negocio fuera de Swing;
- no tratar decisiones abiertas como definitivas.

## 13. Validación actual

Suite general: **664/664**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, ejecutada el 09/09/2026 21:58:35 -03:00.

`TarjetaCreditoPagoCreditoTest`: **5/5** en la validación focalizada conocida.

`CicloFacturacionTest`: **9 tests** incluidos en la suite general.
