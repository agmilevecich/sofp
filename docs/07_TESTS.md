# SOFP — Tests

## Estado de validación — 13/09/2026

### Suite general más reciente

El usuario ejecutó `mvn test` y obtuvo **696/696**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, 21:14 min.

### Suite relacionada de obligaciones/pagos/UI

**69/69**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, 10:17 min. Durante esta ejecución Surefire informó un warning por demora en la terminación de la JVM después de `System.exit(0)`, sin fallo de tests.

### Tests específicos

- UI de pago: **6/6**, `BUILD SUCCESS`.
- `ObligacionServiceTest`: **9/9**, `BUILD SUCCESS`, finalizado 13/09/2026 16:22:17 -03:00.

## Auditoría de `Cuenta` — cobertura actual

La revisión transversal incluyó `CuentaTest`, `CuentaServiceTest`, `MovimientoServiceTest` y `OperacionFinancieraServiceTest`, además de las implementaciones de dominio, servicios y repositorios relacionados.

Cobertura existente relevante:

- creación de cuenta común y tarjeta;
- validación de límite y días de tarjeta;
- cambio de moneda, institución y tipo en dominio;
- modificación de tipo/moneda mediante `CuentaService`;
- persistencia de modificaciones;
- cálculo de saldo;
- movimientos ordenados por cuenta;
- autorización por propietario en operaciones públicas;
- transferencias con validación de misma moneda;
- gastos con moneda económica explícita, necesaria para consumos de tarjeta en moneda extranjera.

Gap confirmado:

- no existe test que intente modificar tipo o moneda después de crear movimientos históricos;
- no existe protección actual contra convertir una cuenta común en tarjeta sin datos de crédito completos;
- no existe una política testeada para los datos de crédito al convertir una tarjeta a otro tipo.

La auditoría no modificó tests y no se ejecutó una nueva suite durante esta revisión. Por lo tanto, 696/696, 69/69 y 9/9 siguen siendo los últimos resultados informados por el usuario y no constituyen una validación posterior a esta auditoría.

## Criterio para el próximo cambio

Los tests deberán cubrir, como mínimo, cuenta sin historial, cuenta con movimientos, cambio de tipo, cambio de moneda, tarjeta y autorización. También deberán preservar el caso válido de consumo de tarjeta cuya moneda económica difiere de la moneda estructural de la cuenta.

No modificar tests solamente para hacerlos pasar. La regla debe derivarse del dominio actual y validarse con persistencia y relaciones.

## Criterio de cierre

Después del cambio: tests específicos → relacionados → suite general → `git diff` → `git diff --check` → `git status` → documentación.
