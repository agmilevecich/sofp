# SOFP — Historial de Builds

## Cierre de etapa — 13/09/2026

**Estado: VALIDADO para la etapa de integridad estructural de Cuenta.**

Cambio funcional principal:

- `e5fbe0f9064841e2a03a671c0bf877e1897e8289` — `fix: proteger integridad estructural de cuentas`.

Commits posteriores de tests:

- `2c19e35afb51f088952fba5270bdd7ba40b0ab91` — `test: cubrir integridad estructural de cuentas`;
- `439fda5d7fb3b914ab33453b90ce2cf31f3b867a` — `fix: corregir constructor de Usuario en test de integridad`;
- `00beeb17fcd781e039374dd5cf7f40ca60a39db4` — `fix: evitar moneda duplicada en test de integridad`.

## Validaciones informadas por el usuario

### Tests específicos de integridad

`mvn -Dtest=CuentaServiceIntegridadTest,CuentaServiceTest test`: **66/66**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`; 09:19 min; finalizado 13/09/2026 17:31:57 -03:00.

### Suite relacionada

`CuentaServiceTest,CuentaTest,MovimientoServiceTest,OperacionFinancieraServiceTest,CuentaServiceIntegridadTest`: **154/154**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`; 22:18 min; finalizado 13/09/2026 17:58:12 -03:00.

### Suite completa

`mvn test`: **700/700**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`; 46:17 min; finalizado 13/09/2026 19:00:15 -03:00.

Este resultado general incrementa la cobertura total conocida de 696 a 700 tests.

### Validaciones previas relevantes

- `ObligacionServiceTest`: **9/9**, `BUILD SUCCESS`.
- Suite de obligaciones/pagos/UI: **69/69**, `BUILD SUCCESS`.
- Tests específicos de UI de pago: **6/6**, `BUILD SUCCESS`.

## Integridad de `Cuenta` — cierre

La implementación protege:

1. cambio de tipo cuando existen movimientos financieros;
2. cambio de moneda cuando existen movimientos financieros;
3. transiciones genéricas hacia/desde `TARJETA_CREDITO` mediante `modificarTipoCuenta`;
4. autorización por propietario en las operaciones públicas.

No se impuso igualdad universal entre moneda de cuenta y moneda de movimiento. Se conserva el caso válido de consumos de tarjeta con moneda económica extranjera.

La cobertura nueva verifica cuenta con historial, cuenta sin historial, tarjeta y persistencia/servicios relacionados.

## Próximos bloques

1. definir y testear reglas de ciclo aplicadas al pago;
2. definir multidivisa de tarjetas;
3. financiación avanzada;
4. UI específica de tarjetas;
5. pasivos/patrimonio y análisis;
6. gestión de entidades financieras;
7. pulido de consola.

No se modificó `main`.
