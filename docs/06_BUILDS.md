# SOFP — Historial de Builds

## Estado documental — 13/09/2026

**Etapa de integridad estructural de Cuenta: VALIDADA.**
**Auditoría de ciclo de facturación aplicado al pago: COMPLETADA, sin cambios de código.**

## Última validación ejecutada por el usuario

### Tests específicos de integridad

`mvn -Dtest=CuentaServiceIntegridadTest,CuentaServiceTest test`: **66/66**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`; finalizado 13/09/2026 17:31:57 -03:00.

### Suite relacionada

`CuentaServiceTest,CuentaTest,MovimientoServiceTest,OperacionFinancieraServiceTest,CuentaServiceIntegridadTest`: **154/154**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`; finalizado 13/09/2026 17:58:12 -03:00.

### Suite completa

`mvn test`: **700/700**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`; finalizado 13/09/2026 19:00:15 -03:00.

## Integridad de `Cuenta` — cierre

La implementación protege:

1. cambio de tipo cuando existen movimientos financieros;
2. cambio de moneda cuando existen movimientos financieros;
3. transiciones genéricas hacia/desde `TARJETA_CREDITO` mediante `modificarTipoCuenta`;
4. autorización por propietario en las operaciones públicas.

No se impuso igualdad universal entre moneda de cuenta y moneda de movimiento.

## Auditoría de ciclo y pago — cierre documental

La revisión del código actual confirmó que el dominio ya calcula ciclos básicos y persiste las fechas de las cuotas, pero `PagoTarjetaService` todavía no aplica reglas temporales al registrar un pago.

Se verificó específicamente:

- cálculo de ciclo antes/después del cierre;
- ajuste de cierre y vencimiento a meses de distinta duración;
- cruce de año;
- condición estructural `inicio <= cierre < vencimiento`;
- persistencia de fechas en `Cuota`;
- orden ascendente de aplicación de pagos;
- pagos parciales;
- ausencia de validación de pago antes del consumo;
- ausencia de clasificación en término/mora;
- ausencia de gracia;
- ausencia de calendario de fines de semana/feriados;
- ausencia de fecha efectiva separada;
- aceptación actual de fechas futuras;
- diferencia entre fechas persistidas de `Cuota` y recálculo dinámico de `Obligacion.getCicloFacturacion()`;
- mutabilidad actual de `configurarDatosCredito(...)` sin regla histórica específica.

No se agregó código porque las reglas temporales faltantes son decisiones de negocio y no deben inferirse.

## Próximo bloque funcional

Convertir la auditoría en reglas explícitas y tests de dominio/servicio. Antes de implementar mora o gracia deberá existir una política definida para fecha de pago, vencimiento, días no hábiles y efecto sobre cuotas.

No se modificó `main`.
