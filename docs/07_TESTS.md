# SOFP — Tests

## Estado de validación — 11/09/2026

### Suite general más reciente

El usuario ejecutó `mvn test` el **11/09/2026 20:11:17 -03:00**.

Resultado:

- Tests run: **689**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración **10:22 min**.

### Tests relacionados del último bloque

Se ejecutó el conjunto relacionado de obligaciones, cuotas, ciclos y servicios.

Resultado: **49/49**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.

### Último test agregado

`ObligacionCuotasTest` incorpora cobertura para una compra del `2026-12-16` con tres cuotas, verificando ciclos y vencimientos que atraviesan el fin de año y llegan hasta abril de 2027.

## Persistencia de tests

Los tests utilizan su propio `src/test/resources/META-INF/persistence.xml` con H2 en memoria. La configuración de la aplicación usa H2 TCP y no modifica el aislamiento de la suite.

## Cuotas

La cobertura verifica generación automática de cuotas al registrar gastos con tarjeta y el tratamiento de pagos por `PagoTarjetaService`.

## Crédito y tarjetas

La cobertura incluye límites, crédito disponible, consumo parcial, límite exacto, exceso, monedas diferentes, liberación mediante pagos, consumos sin obligación y selección explícita de tarjeta activa en la UI.

## Ciclos de facturación

`CicloFacturacionTest` cubre cierre exacto, ciclo siguiente, meses cortos, febrero, vencimiento posterior al cierre, cambio de año y fecha nula. `ObligacionCuotasTest` agrega cobertura de cuotas que atraviesan el fin de año.

## Obligaciones y moneda

La cobertura incluye `ObligacionTest`, `ObligacionJpaTest`, `ObligacionServiceTest`, `ObligacionesPanelTest` y navegación. Las obligaciones conservan la moneda económica del movimiento de origen.

## Otros bloques

Continúan integrados en la suite: seguridad/aislamiento, cuentas, categorías, movimientos, fondos disponibles, Gastos, Ingresos, Transferencias, Inversiones, Reportes y shell Swing.

## Criterio de cierre

No considerar una funcionalidad terminada solamente porque compila. Cada bloque debe contemplar éxito, null cuando corresponda, entidad inexistente, reglas de negocio, persistencia, relaciones y casos límite relevantes.

Después de cambios importantes: tests específicos → relacionados → suite general cuando corresponda → `git diff` → `git diff --check` → `git status`.

Los estados locales de Git solo se consideran confirmados cuando el usuario los informa o se verifican en el entorno correspondiente.
