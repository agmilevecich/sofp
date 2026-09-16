# SOFP — Pendientes

## Estado auditado — 15/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.
**Último commit de código:** `e95585e043290eebb5789f2b628b1edcef8a7344`.

Última validación informada: **718/718**, 0 fallos, 0 errores, 0 omitidos, `BUILD SUCCESS`, 15/09/2026 20:05:19 -03:00.

## Bloques cerrados

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
- Saldos y fondos separados por moneda.
- `Moneda.cantidadDecimales` no negativa.
- `TipoCambio` histórico.
- Moneda original y moneda de liquidación de `Obligacion`.
- Liquidación histórica explícita y trazable.
- `saldoLiquidacion` de obligaciones liquidadas.
- Integración de pagos sobre `saldoLiquidacion` en `PagoTarjetaService`.

## Estado multidivisa

Resuelto:

1. saldo de cuenta filtrado por moneda;
2. fondos disponibles filtrados por moneda;
3. consumo con moneda económica propia;
4. moneda original y de liquidación;
5. cotización histórica explícita;
6. asociación de la cotización a la obligación;
7. importe y saldo de liquidación;
8. pago parcial/total sobre saldo de liquidación;
9. cuenta pagadora en moneda de liquidación.

## Pendientes inmediatos

1. Revisar `git diff`.
2. Revisar `git diff --check`.
3. Revisar `git status`.
4. Definir impacto de consumos en moneda distinta sobre límite/crédito disponible.
5. Diseñar y cubrir esa regla antes de modificar el cálculo.
6. Completar cobertura de persistencia/UI del pago multidivisa.

No se deben introducir conversiones implícitas.

## P2 — Robustez

- Política de eliminación de cuentas con historial financiero.
- Abstracción `Clock`.
- Migraciones/versionado formal de esquema para una futura etapa no local.

## P3 — Evolución

- Financiación avanzada.
- UI específica de tarjetas.
- Pasivos, patrimonio y análisis.
- Gestión de entidades financieras.
- Pulido de consola.

## Fuera de alcance actual

Calendario de feriados, fecha efectiva separada del movimiento e intereses/punitorios/CFT/refinanciación requieren decisiones de negocio antes de implementarse.

## Regla de cierre

Tests específicos → relacionados → suite general → `git diff` → `git diff --check` → `git status` → documentación.
