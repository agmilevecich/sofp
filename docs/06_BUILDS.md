# SOFP — Historial de Builds

## Estado documental — 15/09/2026

**Etapa actual:** multidivisa de tarjetas en integración incremental. El modelo histórico de liquidación ya está implementado; todavía falta conectarlo al flujo real de pago.

### Validación global actual

El usuario ejecutó `mvn test`: **712/712**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`; finalizado **15/09/2026 18:05:46 -03:00**.

## Último bloque funcional cerrado

### Liquidación histórica multidivisa de obligaciones

Se implementó `TipoCambio` como entidad histórica y se separaron en `Obligacion` la moneda original del consumo y la moneda de liquidación.

Se agregó la asociación del tipo de cambio utilizado y la operación explícita `Obligacion.liquidar(TipoCambio)`, con validación de monedas, prevención de doble liquidación y cálculo según la cotización histórica.

Commits principales:

- `6e2d38d` — `feat: modelar tipo de cambio historico`.
- `52a125e` — `test: validar tipo de cambio historico`.
- `8cd34fb` — `test: persistir tipo de cambio historico`.
- `f76822b` — `fix: registrar TipoCambio en persistencia de tests`.
- `fc4a0ff` — `test: corregir escala de cotizacion en prueba JPA`.
- `506166b` — `feat: separar moneda original y de liquidacion en Obligacion`.
- `bbc4dbb` — `fix: compatibilizar monedas de Obligacion con datos existentes`.
- `a8ed8e8` — `test: cubrir monedas original y de liquidacion de Obligacion`.
- `d168a82` — `test: persistir monedas original y liquidacion de Obligacion`.
- `17c81ba` — `feat: asociar tipo de cambio historico a Obligacion`.
- `1ab3d10` — tests de liquidación de obligación.
- `c74717a` — `test: persistir liquidacion historica de Obligacion`.

Validaciones del bloque:

- `TipoCambioTest`: 10/10.
- `TipoCambioJpaTest`: 1/1.
- `ObligacionTest`: 12/12.
- `ObligacionJpaTest`: 3/3.
- `ObligacionLiquidacionTest`: 5/5.
- `ObligacionTipoCambioJpaTest`: 1/1.
- Suite relacionada: 27/27.
- Suite general: 712/712.

## Bloques cerrados relevantes

- Shell Swing y navegación.
- Cuentas, categorías, ingresos, gastos, movimientos e inversiones.
- Obligaciones y pagos de tarjeta desde UI.
- Autorización de pagos por usuario.
- Integridad estructural de `Cuenta`.
- Integridad histórica Movimiento → Obligación.
- Ciclos históricos de obligaciones.
- Cuotas simples y pagos parciales.
- Vencimiento de fin de semana.
- Días de gracia y mora.
- Aislamiento JPA/H2 para tests.
- Saldos y disponibilidad de fondos separados por moneda.
- Validación de `Moneda.cantidadDecimales` no negativa.
- Modelo explícito de liquidación histórica multidivisa.

## Evolución de la suite

La suite histórica llegó a 704 tests durante el bloque temporal. Posteriormente se reorganizó cobertura y se incorporaron cambios de multidivisa y robustez. El estado actual válido es **712/712**. No existe objetivo de recuperar artificialmente el conteo histórico de 704.

## Próximo bloque

Integrar la liquidación histórica en `PagoTarjetaService` sin romper pagos de moneda coincidente y definir el impacto de consumos en moneda distinta sobre el crédito disponible.

No se deben introducir conversiones implícitas.

No se modificó `main`.
