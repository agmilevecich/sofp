# SOFP — Historial de Builds

## Estado documental — 16/09/2026

**Rama de trabajo:** `feature/swing-shell`.
**Último commit de código previo a la actualización documental:** `d61609df65b23c49a81851dc5742c73261a6d924` — `fix: importar Movimiento en test de credito`.

## Última validación conocida

- `mvn test`: **723/723**.
- Failures: 0.
- Errors: 0.
- Skipped: 0.
- `BUILD SUCCESS`.
- Finalizada: **16/09/2026 13:11:00 -03:00**.
- Tiempo total: **11:02 min**.

## Bloque implementado

### Valorización de cierre de obligaciones multidivisa

Se incorporó una valorización histórica de cierre separada de la liquidación de la obligación.

`Obligacion` ahora conserva:

- `importeValorizacionCierre`;
- `tipoCambioCierre`.

`Obligacion.valorarCierre(TipoCambio)`:

- exige tipo de cambio no nulo;
- rechaza una segunda valorización;
- rechaza la valorización mediante tipo de cambio cuando las monedas original y de liquidación son iguales;
- valida que la moneda origen del tipo de cambio sea la moneda original;
- valida que la moneda destino sea la moneda de liquidación;
- conserva la cotización histórica;
- calcula la valorización sin modificar la deuda original ni su estado.

La liquidación explícita mediante `liquidar(TipoCambio)` permanece separada de esta valorización.

### Crédito disponible con consumos multidivisa

`ObligacionRepository.sumarCreditoUtilizadoPorCuenta(...)` incorpora la valorización de cierre al cálculo del crédito utilizado:

- obligación en moneda de la tarjeta → usa `saldoPendiente`;
- obligación en moneda diferente con valorización de cierre → usa `importeValorizacionCierre`;
- consumo de tarjeta sin obligación asociada → conserva el comportamiento existente;
- obligación multidivisa sin valorización de cierre → no se inventa una conversión.

`MovimientoService.calcularCreditoUtilizado(...)` utiliza este cálculo.

Se mantiene la regla de que el crédito de nuevos consumos se valida directamente cuando el movimiento está en la moneda de la tarjeta. La valorización de cierre de consumos extranjeros se trata como un dato histórico posterior al consumo.

### Commits del bloque reciente

- `5bf57042` — `feat: considerar valorizacion de cierre en credito`.
- `8843d10` — `feat: usar valorizacion de cierre para credito`.
- `0a56d9f` — `test: cubrir credito con valorizacion de cierre`.
- `149b6ab` — `fix: conservar comportamiento de MovimientoService`.
- `d61609d` — `fix: importar Movimiento en test de credito`.

## Validaciones específicas de la etapa

- `MovimientoCreditoMultimonedaTest`: **1/1**.
- `MovimientoServiceTest,MovimientoMultimonedaTest,MovimientoServiceSaldoTest`: **62/62**.
- `PagoTarjetaServiceTest,SaldoTarjetaCreditoTest,TarjetaCreditoPagoCreditoTest`: **17/17**.
- `MovimientoObligacionIntegridadTest,ObligacionServiceTest`: **14/14**.
- `ObligacionJpaTest`: **3/3**.
- `ObligacionLiquidacionTest`: **13/13**.

## Estado final de la etapa

La suite completa quedó en **723/723**, sin failures, errors ni skipped.

Además, en la copia local se verificó:

- `git diff`: vacío;
- `git diff --check`: sin observaciones;
- `git status`: working tree limpio y rama sincronizada con `bitbucket/feature/swing-shell`.

## Próximo bloque

1. Definir el flujo de obtención/registro de la valorización de cierre dentro de la aplicación.
2. Definir qué ocurre con una obligación multidivisa todavía no valorizada al cierre.
3. Revisar el cálculo de crédito cuando una obligación valorizada recibe pagos parciales.
4. Completar persistencia/UI del cierre y pago multidivisa.
5. Continuar con los pendientes P2/P3 cuando corresponda.

No se modificó `main` y no se deben introducir conversiones implícitas.
