# SOFP — Pendientes

## Estado auditado — 16/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.
**Último commit de código:** `50052cb` — `fix: calcular crédito multidivisa pendiente`.

Última validación informada: **740/740**, 0 fallos, 0 errores, 0 omitidos, `BUILD SUCCESS`, 16/09/2026 15:54:16 -03:00.

## Bloques cerrados

- Shell Swing y navegación.
- Cuentas, categorías, ingresos, gastos, movimientos e inversiones.
- Obligaciones y pagos de tarjeta desde UI.
- Autorización de pagos por usuario.
- Integridad estructural e histórica.
- Ciclos, cuotas, vencimientos, gracia y mora.
- Aislamiento JPA/H2 para tests.
- Saldos y fondos separados por moneda.
- `Moneda.cantidadDecimales` no negativa.
- `TipoCambio` histórico.
- Moneda original y moneda de liquidación de `Obligacion`.
- Liquidación histórica explícita y trazable.
- `saldoLiquidacion` y pagos parciales/totales.
- Valorización histórica de cierre separada de la liquidación.
- Uso de la valorización para crédito disponible.
- Corrección del crédito proporcional después de pagos parciales en moneda original.

## Estado multidivisa

Resuelto:

1. saldo de cuenta y fondos por moneda;
2. consumo con moneda económica propia;
3. moneda original y de liquidación;
4. cotización histórica explícita;
5. asociación de cotización a la obligación;
6. importe y saldo de liquidación;
7. pagos parciales/totales sobre saldo de liquidación;
8. cuenta pagadora en moneda de liquidación;
9. valorización histórica de cierre;
10. utilización de la valorización para crédito disponible;
11. reducción proporcional del crédito utilizado después de pagos parciales.

## Pendientes inmediatos

1. Definir el flujo de obtención y registro de la valorización de cierre dentro de la aplicación.
2. Definir qué ocurre con una obligación multidivisa que todavía no tiene valorización de cierre.
3. Completar persistencia/UI del flujo integral de cierre y pago multidivisa.

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
