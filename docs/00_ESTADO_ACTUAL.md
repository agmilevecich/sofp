# SOFP — Estado actual

> Documento de continuidad. La fuente de verdad técnica es el código, los tests y los commits actuales; `docs/` es documentación auxiliar.

## Estado verificado — 10/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

GitHub verifica que `feature/swing-shell` está **470 commits adelante y 0 atrás** respecto de `main`. No se realizó merge a `main`.

Último commit funcional/documentado verificado antes de esta actualización: `51d4afe4e40a558026b17e44ae6e54085c7a908d` — `fix: ajustar test de cuotas al registro automatico`.

## Último bloque funcional cerrado

### Cuotas automáticas en gastos con tarjeta

`GastoService` genera automáticamente la cantidad solicitada de cuotas al registrar un gasto con `FormaPago.TARJETA_CREDITO`. La generación de cuotas ocurre dentro de la transacción del registro de la obligación y las cuotas se persisten correctamente.

Se corrigió `PagoTarjetaServiceTest` para que el escenario de tres cuotas utilice el registro real del gasto con `cantidadCuotas = 3`, en lugar de generar manualmente las cuotas después. Esto evita la excepción `La obligación ya tiene cuotas generadas` y hace que el test represente el flujo productivo actual.

Commit:

- `51d4afe` — `fix: ajustar test de cuotas al registro automatico`.

## Validación más reciente conocida

### Suite general

El usuario ejecutó `mvn test` el **10/09/2026 10:39:38 -03:00**:

- **671/671** tests;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`.

### Suite relacionada con el último ajuste

El usuario ejecutó:

`mvn -Dtest=GastosPanelTest,GastoServiceTest,ObligacionTest,ObligacionCuotasTest,PagoTarjetaServiceTest test`

el **10/09/2026 13:14:29 -03:00**:

- **32/32** tests;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración **01:19 min**.

También se verificó `PagoTarjetaServiceTest` de forma aislada: **4/4**, `BUILD SUCCESS`, 13:08:50 -03:00.

`GastosPanelTest` aislado: **6/6**, `BUILD SUCCESS`.

## Arquitectura funcional vigente

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

Gastos: `GastosPanel → GastoService → MovimientoService → Movimiento EGRESO`.

Una compra con `TARJETA_CREDITO` genera un movimiento de egreso y una obligación. `GastoService` registra las cuotas solicitadas automáticamente.

## Moneda, obligaciones y tarjetas

Los movimientos admiten moneda explícita. Una obligación conserva la moneda económica del movimiento de origen. No se convierte automáticamente ARS↔USD al crear la obligación.

Las obligaciones tienen estados `PENDIENTE`, `PARCIAL` y `PAGADA`. Los pagos de tarjeta están autorizados por usuario.

Una tarjeta de crédito es una `Cuenta` con `TipoCuenta.TARJETA_CREDITO`, límite, día de cierre y día de vencimiento.

## Reglas financieras vigentes

- `EGRESO` superior al saldo disponible: rechazado.
- `EGRESO` igual al saldo disponible: permitido y deja saldo cero.
- Las modificaciones de importe y tipo respetan fondos disponibles.
- Categorías con movimientos se conservan y se desactivan en lugar de eliminarse físicamente.
- Cuenta y forma de pago son conceptos distintos.
- Una compra con `TARJETA_CREDITO` genera un movimiento de egreso y una obligación.
- Las cuotas se generan automáticamente al registrar el gasto cuando se solicita una cantidad mayor que una.
- El crédito disponible se calcula inicialmente por moneda de la tarjeta, sin conversión implícita.
- Transferencias propias no son ingresos ni gastos.
- La UI no debe duplicar reglas financieras.

## Últimos commits funcionales relevantes

- `51d4afe` — `fix: ajustar test de cuotas al registro automatico`.
- `4ce1591` — `fix: generar cuotas dentro de la transaccion del gasto`.
- `87fab04` — `fix: persistir cuotas al registrar obligaciones`.
- `4092212` — `fix: corregir fixture de cuotas en GastosPanelTest`.
- `afbfd7b` — `test: cubrir cuotas en GastoService`.
- `3fd91b1` — `feat: integrar cuotas al registro de gastos`.

## Próximo paso real

Revisar `GastosPanelTest` antes de modificar la UI para resolver la selección explícita de tarjeta de crédito en `GastosPanel`.

Objetivo previsto del próximo bloque:

- mostrar/usar una selección específica de tarjetas de crédito cuando `FormaPago.TARJETA_CREDITO` esté elegida;
- listar únicamente cuentas activas de tipo `TARJETA_CREDITO`;
- pasar la tarjeta seleccionada como `Cuenta` a `GastoService`;
- cubrir con tests que la tarjeta correcta sea la utilizada y que no se mezclen cuentas de otros tipos.

Antes de implementar, revisar `GastosPanelTest`, `GastosPanel`, servicios/repositorios de cuentas y convenciones de los paneles existentes.

## Protocolo de continuidad

Ante una nueva sesión: rama → últimos commits → comparación con `main` → README/documentación → código → tests → último resultado conocido → próximo paso.

Prioridad: **código → tests → commits → `main` → documentación → conversaciones anteriores**.

No hacer merge a `main` automáticamente.
