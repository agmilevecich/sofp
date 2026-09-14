# SOFP — Tests

## Estado de validación — 14/09/2026

### Suite general más reciente

El usuario ejecutó `mvn test` y obtuvo **704/704**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizado 13/09/2026 a las 22:05:14 -03:00.

### Persistencia de obligaciones

`mvn -Dtest=ObligacionJpaTest test`: **2/2**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizado 13/09/2026 a las 21:12:49 -03:00.

## Cobertura temporal incorporada

Los tests del bloque temporal cubren rechazo de pago anterior al consumo, rechazo de fecha futura, estabilidad del ciclo histórico ante cambios posteriores de configuración, período de gracia, vencimiento en fin de semana, persistencia de las fechas históricas y continuidad de pagos parciales/en orden de cuotas.

La suite completa de 704 tests confirma que la adaptación de persistencia no rompió el resto del sistema.

## Validaciones previas relevantes

- Suite de obligaciones/pagos/UI: **69/69**.
- `ObligacionServiceTest`: **9/9**.
- UI de pago de tarjeta: **6/6**.
- Integridad de Cuenta: **66/66** específicos y **154/154** relacionados.
- Suite completa anterior a la implementación temporal: **700/700**.

## Cobertura pendiente detectada por auditoría

La suite vigente no cierra todavía el comportamiento multidivisa completo. La auditoría detectó la necesidad de agregar tests para:

- saldo de una cuenta con movimientos en más de una moneda;
- validación de fondos separada por moneda;
- consumo de tarjeta en moneda distinta de la moneda de la tarjeta;
- impacto de ese consumo sobre el límite;
- pago/liquidación de deuda cuando la moneda de la deuda y la cuenta pagadora difieren, una vez definida la regla de negocio;
- límites de `Moneda.cantidadDecimales`;
- política de eliminación de una cuenta con historial, cuando se defina esa regla.

Estos tests **no se inventan ni se modifican todavía**: primero deben existir las reglas de negocio y luego se implementará el cambio mínimo acompañado de cobertura.

## Criterio de cierre

El bloque temporal queda validado por tests específicos y suite completa. El resultado 704/704 fue informado por el usuario y es el último resultado conocido; no se debe asumir un nuevo resultado local hasta que el usuario lo ejecute e informe.
