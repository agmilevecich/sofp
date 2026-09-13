# SOFP — Tests

## Estado de validación — 13/09/2026

### Suite general más reciente

El usuario ejecutó `mvn test` y obtuvo:

- Tests run: **696**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- tiempo: **21:14 min**.

Este resultado reemplaza al anterior 695/695.

### Suite relacionada de obligaciones/pagos/UI

- **69/69**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- tiempo: **06:28 min**.

### Tests específicos de UI de pago

- **6/6**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- tiempo: **01:44 min**.

## Cobertura funcional relevante

La suite incluye seguridad/aislamiento, cuentas, categorías, movimientos, fondos disponibles, Gastos, Ingresos, Transferencias, Inversiones, Reportes, shell Swing, obligaciones, cuotas, tarjetas y pagos.

### Integridad movimiento ↔ obligación

`MovimientoObligacionIntegridadTest` cubre:

1. bloqueo de modificación de importe del movimiento origen;
2. bloqueo de modificación de fecha/hora;
3. bloqueo de modificación de tipo;
4. bloqueo de eliminación;
5. conservación de cambios permitidos de descripción y observaciones.

Los tests verifican además la persistencia del movimiento y la obligación en estado consistente.

### Cuotas y ciclos

`CicloFacturacionTest` cubre cierres, meses cortos, febrero, vencimiento, fechas nulas y cambio de año.

`ObligacionCuotasTest` cubre una compra del `2026-12-16` con tres cuotas que atraviesan el fin de año.

### Atomicidad de compra con tarjeta

Existe cobertura para rollback conjunto cuando falla la creación de la obligación después de registrar el movimiento.

### Pagos de tarjeta

`PagoTarjetaServiceTest` cubre el servicio coordinador y las reglas de saldo, moneda, autorización y pagos parciales/totales existentes.

`ObligacionesPanelPagoTarjetaTest` cubre el flujo real desde UI: selección de cuenta pagadora/categoría, pago parcial y movimiento de salida de fondos.

`ObligacionesPanelTest` verifica actualización de la obligación y saldo de la cuenta pagadora después del pago.

## Gaps de tests pendientes

Los pendientes se corresponden ahora con funcionalidades todavía no implementadas o reglas aún no definidas:

1. acceso directo no autorizado a `ObligacionService`;
2. cambios de tipo/moneda de `Cuenta` con historial financiero;
3. reglas de pago respecto de ciclo, vencimiento, mora, gracia y días no hábiles cuando sean definidas;
4. escenarios multidivisa de tarjeta una vez definida la regla;
5. casos límite de financiación avanzada;
6. tests del futuro panel de gestión de entidades financieras cuando se implemente.

## Criterio de cierre

No considerar una funcionalidad terminada solamente porque compila. Cada bloque debe contemplar éxito, null cuando corresponda, entidad inexistente, reglas de negocio, persistencia, relaciones y casos límite relevantes.

Después de cambios importantes: tests específicos → relacionados → suite general cuando corresponda → `git diff` → `git diff --check` → `git status`.

Los estados locales de Git solo se consideran confirmados cuando el usuario los informa o se verifican en el entorno correspondiente.