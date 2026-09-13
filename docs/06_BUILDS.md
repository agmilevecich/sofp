# SOFP — Historial de Builds

## Cierre de etapa — 13/09/2026

**Estado: VALIDADO para la etapa de autorización; auditoría de Cuenta terminada sin cambios de código.**

Últimos commits funcionales de la etapa de autorización de obligaciones:

- `a2a96bb13a0d342e2ccfb6f7302ae791c1dd8147` — `fix: exigir usuario al registrar pagos de obligaciones`;
- `2813fa34c953f4f6408903c1e3b4fc2e7f3b58c5` — `test: adaptar pagos de obligaciones a usuario autorizado`.

Los commits posteriores son de documentación de continuidad.

## Validaciones informadas por el usuario

### Tests específicos de UI

`ObligacionesPanelTest,ObligacionesPanelPagoTarjetaTest`: **6/6**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.

### `ObligacionServiceTest`

**9/9**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`; finalizado 13/09/2026 16:22:17 -03:00.

### Suite relacionada

`PagoTarjetaServiceTest,ObligacionServiceTest,MovimientoServiceTest,ObligacionesPanelTest,ObligacionesPanelPagoTarjetaTest,MainFrameObligacionesTest`: **69/69**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`; 10:17 min; finalizado 13/09/2026 16:35:14 -03:00.

La ejecución mostró un warning de Surefire por demora en la terminación de la JVM después de `System.exit(0)`, pero terminó con `BUILD SUCCESS` y sin tests fallidos.

### Suite completa

`mvn test`: **696/696**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`; 21:14 min; finalizado 13/09/2026 14:09:41 -03:00.

No se ejecutó una nueva suite completa después del cierre de autorización. 696/696 es el último resultado general informado.

## Auditoría de integridad de `Cuenta` — 13/09/2026

Se revisaron `Cuenta`, `CuentaService`, `CuentaRepository`, `TipoCuenta`, `Moneda`, `Movimiento`, `MovimientoRepository`, `MovimientoService`, `GastoService`, `OperacionFinanciera`, `MovimientoActivo` y los tests relevantes.

Resultado: **auditoría terminada; no se modificó código ni se ejecutaron nuevos tests durante la auditoría.**

Hallazgos:

1. `CuentaService.modificarTipoCuenta` y `modificarMoneda` no verifican historial financiero antes de modificar la cuenta.
2. `Cuenta.cambiarTipoCuenta` permite convertir una cuenta común en `TARJETA_CREDITO` sin configurar límite/cierre/vencimiento, y permite salir de tarjeta sin una política para los datos de crédito existentes.
3. Cambiar moneda con historial puede hacer que la moneda estructural actual de la cuenta deje de representar correctamente su historial.
4. `Movimiento` posee moneda propia y `GastoService` permite una moneda económica distinta para consumos con tarjeta; por eso no corresponde imponer igualdad cuenta/moneda indiscriminadamente.
5. `OperacionFinancieraService` sí exige misma moneda para transferencias entre cuentas.
6. `CuentaRepository` mantiene responsabilidad de persistencia y no introduce reglas de negocio de tipo/moneda.

Conclusión: el siguiente cambio debe proteger tipo/moneda cuando exista historial y resolver explícitamente la coherencia de los datos de tarjeta al cambiar de tipo. No se debe introducir una regla genérica que elimine la capacidad de registrar consumos de tarjeta en moneda extranjera.

## Próximos bloques

1. implementar y testear la regla mínima de integridad de `Cuenta` definida por esta auditoría;
2. definir reglas de ciclo aplicadas al pago;
3. definir multidivisa;
4. financiación avanzada;
5. UI específica de tarjetas;
6. pasivos/patrimonio y análisis;
7. gestión de entidades financieras;
8. pulido de consola.

No se modificó `main`.
