# SOFP — Tests

## Estado de validación — 15/09/2026

### Suite general más reciente

El usuario ejecutó `mvn test` y obtuvo **693/693**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizado **15/09/2026 12:03:19 -03:00**.

### Validación de Moneda

`mvn -Dtest=MonedaTest test`: **7/7**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizado 15/09/2026 11:21:20 -03:00.

Se cubre el rechazo de `cantidadDecimales` negativa tanto al crear como al modificar una moneda. La nulidad continúa produciendo `NullPointerException`.

### Tests relacionados

`mvn -Dtest=MonedaTest,CuentaTest,CuentaJpaTest,MovimientoTest test`: **53/53**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizado 15/09/2026 11:34:38 -03:00.

### Cobertura recuperada de CuentaService

`CuentaServiceCoberturaTest`: **35/35**, BUILD SUCCESS. Esta cobertura recuperó casos funcionales que habían desaparecido al simplificarse `CuentaServiceTest` y forma parte de la suite actual.

### Validaciones previas relevantes

- Integridad de `Cuenta`: **66/66** específicos y **154/154** relacionados.
- `MovimientoService`, `CuentaService` y multidivisa: **64/64** en la validación específica informada.
- `CuentaServiceEvolucionSaldoTest`: **5/5**.
- `ObligacionServiceTest`: **9/9**.
- Suite de obligaciones/pagos/UI: **69/69**.
- UI de pago de tarjeta: **6/6**.
- Persistencia de obligaciones temporal: `ObligacionJpaTest` **2/2**.

## Estado de cobertura multidivisa

Ya existe cobertura para:

- saldo de una cuenta separado por moneda;
- rechazo de un egreso cuando existe saldo en otra moneda pero no en la moneda del movimiento;
- validación de fondos para movimientos en la moneda correspondiente;
- coexistencia de saldos ARS y USD sin mezclarlos.

Todavía debe cubrirse, una vez definida la regla de negocio:

- consumo de tarjeta en moneda distinta de la tarjeta;
- impacto de ese consumo sobre el límite;
- liquidación/pago cruzando monedas;
- trazabilidad de conversión y tasa cuando corresponda.

## Criterio de cierre

Los resultados anteriores fueron informados por el usuario y no deben asumirse como nuevos resultados locales. La suite actual conocida es **693/693**. Un nuevo bloque debe validarse con tests específicos, tests relacionados y suite general antes de considerarse cerrado.
