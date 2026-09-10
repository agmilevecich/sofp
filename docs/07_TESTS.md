# SOFP — Tests

## Estado de validación — 10/09/2026

### Suite general más reciente

El usuario ejecutó `mvn test` el **09/09/2026 21:58:35 -03:00**.

Resultado:

- Tests run: **664**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración: **10:04 min**.

Es la validación general más reciente conocida y no debe sustituirse por resultados anteriores de 642/642 o 650/650.

### Diagnóstico y aislamiento JPA/H2

La suite había presentado un único error por duplicación de `ARS` en `monedas.codigo` dentro de `TarjetaCreditoPagoCreditoTest`. El diagnóstico con forks independientes pasó 664/664 y `TarjetaCreditoPagoCreditoTest` pasó **5/5**.

La causa fue reutilización de un `EntityManagerFactory`/contexto H2 entre tests. `PosicionActivoServiceTest` cerraba el `EntityManager` pero no el `EntityManagerFactory` almacenado por `JpaTestManager`.

La corrección quedó en dos pasos:

- `e8f6fdb` — aislamiento del contexto JPA por hilo y base H2 de test independiente;
- `46290786` — cierre del `EntityManagerFactory` mediante `@AfterEach` en `PosicionActivoServiceTest`.

La suite posterior pasó **664/664**.

## Ciclos de facturación

`CicloFacturacionTest` contiene **9 tests** y cubre:

1. consumo anterior al cierre;
2. consumo en el día exacto de cierre;
3. consumo posterior al cierre;
4. vencimiento posterior cuando el día de vencimiento es anterior al cierre;
5. vencimiento posterior cuando coincide con el cierre;
6. ajuste de cierre a febrero;
7. ajuste de vencimiento al último día real del mes;
8. cambio de año;
9. fecha de consumo nula.

La lógica reside en `Cuenta.calcularCicloFacturacion(LocalDate)` y `CicloFacturacion` es un objeto de dominio no persistente.

## Crédito y tarjetas

La cobertura incluye límites, crédito disponible, consumo parcial, límite exacto, exceso, monedas diferentes, liberación mediante pagos y consumos sin obligación sin doble contabilización.

`TarjetaCreditoPagoCreditoTest`: **5/5** en la validación focalizada conocida.

## Obligaciones y moneda

La cobertura incluye `ObligacionTest`, `ObligacionJpaTest`, `ObligacionServiceTest`, `ObligacionesPanelTest` y navegación. `ObligacionesPanelTest` había validado **4/4** la visualización de moneda antes del bloque posterior de tarjeta.

## Otros bloques

Continúan integrados en la suite: seguridad/aislamiento, cuentas, categorías, movimientos, fondos disponibles, Gastos, Ingresos, Transferencias, Inversiones, Reportes y shell Swing.

## Criterio de cierre

No considerar una funcionalidad terminada solamente porque compila. Cada bloque debe contemplar éxito, null cuando corresponda, entidad inexistente, reglas de negocio, persistencia, relaciones y casos límite relevantes.

Después de cambios importantes: tests específicos → relacionados → suite general cuando corresponda → `git diff` → `git diff --check` → `git status`.

Los estados locales de Git solo se consideran confirmados cuando el usuario los informa o se verifican en el entorno correspondiente.
