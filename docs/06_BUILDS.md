# SOFP — Historial de Builds

## Estado documental — 16/09/2026

**Rama de trabajo:** `feature/swing-shell`.
**Último commit de código validado:** `50052cb` — `fix: calcular crédito multidivisa pendiente`.

## Última validación conocida

- `mvn test`: **740/740**.
- Failures: 0.
- Errors: 0.
- Skipped: 0.
- `BUILD SUCCESS`.
- Finalizada: **16/09/2026 15:54:16 -03:00**.
- Tiempo total: **09:50 min**.

## Bloque implementado

### Valorización de cierre de obligaciones multidivisa

`Obligacion` conserva `tipoCambioCierre` e `importeValorizacionCierre` como datos históricos separados de la liquidación. `valorarCierre(TipoCambio)` valida las monedas, rechaza segunda valoración y no modifica la deuda original ni el estado.

### Crédito disponible con consumos multidivisa

`ObligacionRepository.sumarCreditoUtilizadoPorCuenta(...)` usa:

- obligación en moneda de la tarjeta → `saldoPendiente`;
- obligación multidivisa valorizada → valorización de cierre proporcional al saldo original pendiente;
- consumo sin obligación asociada → comportamiento existente.

No se realizan conversiones implícitas.

## Commits del bloque reciente

- `5f490c6` — `fix: importar tipo de cambio en cierre de ciclo`.
- `0b44642` — `feat: valorar obligaciones al cerrar ciclo`.
- `c699ae7` — `test: cubrir valorizacion de cierre de ciclo`.
- `75ea7b7` — `test: cubrir crédito multidivisa parcial`.
- `50052cb` — `fix: calcular crédito multidivisa pendiente`.

## Validaciones específicas

- `TipoCambioRepositoryTest`: **6/6**.
- `ObligacionServiceCierreTest`: **4/4**.
- `ObligacionRepositoryTest`: **7/7**.
- `MovimientoCreditoMultimonedaTest`: **1/1**.
- `PagoTarjetaServiceTest`: **10/10**.
- `ObligacionLiquidacionTest`: **13/13**.

## Estado final de la etapa

La suite completa quedó en **740/740**, sin failures, errors ni skipped. También se verificó `git diff` vacío, `git diff --check` sin observaciones y `git status` limpio.

## Próximo bloque

1. Definir el flujo de obtención/registro de la valorización de cierre dentro de la aplicación.
2. Definir qué ocurre con una obligación multidivisa todavía no valorizada al cierre.
3. Completar persistencia/UI del flujo integral de cierre y pago multidivisa.
4. Continuar con los pendientes P2/P3 cuando corresponda.

No se modificó `main` y no se deben introducir conversiones implícitas.
