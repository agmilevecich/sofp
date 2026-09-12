# SOFP — Tests

## Estado de validación — 12/09/2026

### Suite general más reciente

El usuario ejecutó `mvn test` y obtuvo:

- Tests run: **690**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`.

Este resultado reemplaza al histórico 689/689 documentado el 11/09/2026.

## Cobertura funcional relevante

La suite incluye seguridad/aislamiento, cuentas, categorías, movimientos, fondos disponibles, Gastos, Ingresos, Transferencias, Inversiones, Reportes, shell Swing, obligaciones, cuotas, tarjetas y pagos.

### Cuotas y ciclos

`CicloFacturacionTest` cubre cierres, meses cortos, febrero, vencimiento, fechas nulas y cambio de año.

`ObligacionCuotasTest` cubre una compra del `2026-12-16` con tres cuotas que atraviesan el fin de año.

### Atomicidad de compra con tarjeta

Existe cobertura para el caso en que falla la creación de la obligación después de registrar el movimiento. El objetivo es confirmar rollback conjunto.

### Pagos de tarjeta

`PagoTarjetaServiceTest` cubre el servicio coordinador y las reglas de saldo, moneda, autorización y pagos parciales/totales existentes.

## Gaps detectados por la auditoría

Se deben agregar tests antes o junto con cada mejora:

1. movimiento origen de obligación: impedir cambio de importe;
2. movimiento origen de obligación: impedir cambio de fecha/hora;
3. movimiento origen de obligación: impedir cambio de tipo;
4. movimiento origen de obligación: impedir eliminación;
5. persistencia consistente después de cada rechazo;
6. acceso directo no autorizado a `ObligacionService`;
7. pago desde UI debe registrar salida real de fondos;
8. pago desde UI debe respetar moneda, saldo, perfil y pago parcial/total;
9. cambios de tipo/moneda de `Cuenta` con historial financiero;
10. reglas de pago respecto de ciclo, vencimiento y días no hábiles cuando sean definidas;
11. escenarios multidivisa de tarjeta una vez definida la regla;
12. casos límite de financiación cuando se implemente.

## Criterio de cierre

No considerar una funcionalidad terminada solamente porque compila. Cada bloque debe contemplar éxito, null cuando corresponda, entidad inexistente, reglas de negocio, persistencia, relaciones y casos límite relevantes.

Después de cambios importantes: tests específicos → relacionados → suite general cuando corresponda → `git diff` → `git diff --check` → `git status`.

Los estados locales de Git solo se consideran confirmados cuando el usuario los informa o se verifican en el entorno correspondiente.