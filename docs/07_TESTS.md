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

### Suite relacionada de obligaciones/pagos/UI

- **69/69**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- tiempo: **10:17 min**.

Durante esta ejecución Surefire informó un warning por demora en la terminación de la JVM después de `System.exit(0)`, pero la ejecución terminó con `BUILD SUCCESS` y 69/69 tests correctos.

### Tests específicos de UI de pago

- **6/6**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`.

### Tests específicos de `ObligacionService`

- **9/9**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- finalizado el **13/09/2026 16:22:17 -03:00**.

## Cobertura funcional relevante

La suite incluye seguridad/aislamiento, cuentas, categorías, movimientos, fondos disponibles, Gastos, Ingresos, Transferencias, Inversiones, Reportes, shell Swing, obligaciones, cuotas, tarjetas y pagos.

### Autorización de pagos de obligaciones

`ObligacionService` ya no expone un `registrarPago` que permita modificar una obligación sin `usuarioId`.

`ObligacionServiceTest` cubre pagos parciales/totales, sobrepago, obligación inexistente, ID nulo y autorización por usuario/perfil. Los tests fueron adaptados a la API autorizada y la suite específica pasó 9/9.

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

1. acceso cruzado adicional sobre otras operaciones públicas de `ObligacionService`, si la auditoría posterior identifica alguna superficie pendiente;
2. cambios de tipo/moneda de `Cuenta` con historial financiero;
3. reglas de pago respecto de ciclo, vencimiento, mora, gracia y días no hábiles cuando sean definidas;
4. escenarios multidivisa de tarjeta una vez definida la regla;
5. casos límite de financiación avanzada;
6. tests del futuro panel de gestión de entidades financieras cuando se implemente.

## Criterio de cierre

No considerar una funcionalidad terminada solamente porque compila. Cada bloque debe contemplar éxito, null cuando corresponda, entidad inexistente, reglas de negocio, persistencia, relaciones y casos límite relevantes.

Después de cambios importantes: tests específicos → relacionados → suite general cuando corresponda → `git diff` → `git diff --check` → `git status`.

Los estados locales de Git solo se consideran confirmados cuando el usuario los informa o se verifican en el entorno correspondiente.
