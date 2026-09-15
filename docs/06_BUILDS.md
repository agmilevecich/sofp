# SOFP — Historial de Builds

## Estado documental — 15/09/2026

**Etapa actual:** multidivisa en definición/implementación incremental. El bloque temporal de ciclos y pagos ya está cerrado.

### Validación global actual

El usuario ejecutó `mvn test`: **693/693**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`; finalizado **15/09/2026 12:03:19 -03:00**.

### Último bloque funcional cerrado

Validación de `Moneda.cantidadDecimales`:

- valores `null` siguen rechazados;
- valores negativos ahora son rechazados;
- la regla se aplica al crear y modificar `Moneda`.

Commits:
- `d4fdcd9` — `fix: validar decimales no negativos en Moneda`.
- `5a6de42` — `test: validar decimales no negativos en Moneda`.

Validaciones:
- `MonedaTest`: 7/7.
- `MonedaTest,CuentaTest,CuentaJpaTest,MovimientoTest`: 53/53.
- suite general: 693/693.

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

## Evolución de la suite

La suite histórica llegó a 704 tests durante el bloque temporal. Posteriormente se reorganizó cobertura y se incorporaron cambios de multidivisa y robustez; el estado actual válido es **693/693**. No existe objetivo de recuperar artificialmente el conteo histórico de 704.

## Próximo bloque

Resolver la multidivisa de tarjetas sin conversiones implícitas:

1. definir impacto de consumos extranjeros sobre crédito disponible;
2. definir moneda de liquidación;
3. definir tasa, fecha y fuente de cotización;
4. definir representación y trazabilidad de conversiones;
5. agregar cobertura específica y relacionada.

No se debe implementar una conversión aislada antes de fijar estas reglas.

No se modificó `main`.
