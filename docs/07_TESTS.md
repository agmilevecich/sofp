# SOFP — Tests

## Estado de validación — 13/09/2026

### Suite general más reciente

El usuario ejecutó `mvn test` y obtuvo **700/700**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, 46:17 min, finalizado 19:00:15 -03:00.

### Suite relacionada de `Cuenta`

`CuentaServiceTest,CuentaTest,MovimientoServiceTest,OperacionFinancieraServiceTest,CuentaServiceIntegridadTest`: **154/154**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, 22:18 min, finalizado 17:58:12 -03:00.

### Tests específicos de integridad

`CuentaServiceIntegridadTest,CuentaServiceTest`: **66/66**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, 09:19 min, finalizado 17:31:57 -03:00.

`CuentaServiceIntegridadTest` agrega cobertura específica para:

- rechazo de cambio de tipo con movimientos financieros;
- rechazo de cambio de moneda con movimientos financieros;
- cambio entre tipos no tarjeta cuando no existe historial;
- rechazo de transiciones hacia/desde `TARJETA_CREDITO` mediante la API genérica.

La corrección de integridad mantiene la autorización por usuario y no introduce una igualdad universal entre moneda de cuenta y moneda de movimiento.

## Validaciones previas relevantes

- Suite de obligaciones/pagos/UI: **69/69**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.
- `ObligacionServiceTest`: **9/9**, `BUILD SUCCESS`.
- UI de pago de tarjeta: **6/6**, `BUILD SUCCESS`.

La suite relacionada de obligaciones había mostrado previamente un warning de Surefire por demora en la terminación de la JVM después de `System.exit(0)`, sin fallo de tests.

## Criterio de cierre de la etapa

La etapa de integridad de `Cuenta` se considera validada porque se ejecutaron tests específicos, suite relacionada y suite completa, todos con `BUILD SUCCESS` y sin failures/errors/skips.

La validación final de Git también fue correcta: `git syncsofp` sin cambios pendientes, `git diff` vacío, `git diff --check` sin salida y `git status` limpio.

## Próximo foco de tests

El siguiente bloque deberá definir primero las reglas de ciclo aplicadas al pago y luego cubrirlas con tests de dominio/servicio, incluyendo vencimiento, mora, gracia, días no hábiles y orden temporal según las reglas que se adopten.
