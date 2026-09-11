# SOFP — Tests

## Estado de validación — 11/09/2026

### Suite general más reciente

El usuario ejecutó `mvn test` el **11/09/2026 14:10:20 -03:00**.

Resultado:

- Tests run: **688**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración **12:52 min**.

Esta es la suite general más reciente conocida y válida.

### Tests específicos del último bloque

Se ejecutó:

`mvn -Dtest=GastosPanelTest,GastosPanelTarjetaCreditoTest test`

Resultado: **8/8**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.

La cobertura de `GastosPanelTarjetaCreditoTest` verifica que, al seleccionar `TARJETA_CREDITO`, se muestren únicamente tarjetas activas y que las cuentas de otros tipos no se mezclen. También verifica el retorno a la selección general de cuentas al cambiar la forma de pago.

`GastosPanelTest` mantiene el flujo de interacción vigente: seleccionar primero la forma de pago y luego la cuenta. Esto es necesario porque el cambio de forma de pago reconstruye el contenido de la selección de cuentas.

## Persistencia de tests

Los tests utilizan su propio `src/test/resources/META-INF/persistence.xml` con H2 en memoria. La configuración de la aplicación en `src/main/resources/META-INF/persistence.xml` usa H2 TCP y no modifica el aislamiento de la suite.

## Cuotas

La cobertura verifica la generación de cuotas al registrar gastos con tarjeta y el tratamiento de cuotas por `PagoTarjetaService`.

`GastosPanelTest` cubre actualmente el registro de un gasto con tres cuotas utilizando el flujo real de `GastoService`, que genera automáticamente las cuotas.

## Crédito y tarjetas

La cobertura incluye límites, crédito disponible, consumo parcial, límite exacto, exceso, monedas diferentes, liberación mediante pagos y consumos sin obligación sin doble contabilización.

## Ciclos de facturación

`CicloFacturacionTest` contiene 9 tests y cubre cierre exacto, ciclo siguiente, meses cortos, febrero, vencimiento posterior al cierre, cambio de año y fecha nula.

## Obligaciones y moneda

La cobertura incluye `ObligacionTest`, `ObligacionJpaTest`, `ObligacionServiceTest`, `ObligacionesPanelTest` y navegación. Las obligaciones conservan la moneda económica del movimiento de origen.

## Otros bloques

Continúan integrados en la suite: seguridad/aislamiento, cuentas, categorías, movimientos, fondos disponibles, Gastos, Ingresos, Transferencias, Inversiones, Reportes y shell Swing.

## Criterio de cierre

No considerar una funcionalidad terminada solamente porque compila. Cada bloque debe contemplar éxito, null cuando corresponda, entidad inexistente, reglas de negocio, persistencia, relaciones y casos límite relevantes.

Después de cambios importantes: tests específicos → relacionados → suite general cuando corresponda → `git diff` → `git diff --check` → `git status`.

Los estados locales de Git solo se consideran confirmados cuando el usuario los informa o se verifican en el entorno correspondiente.
