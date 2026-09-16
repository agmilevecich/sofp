# SOFP — Pendientes

## Estado auditado — 16/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.
**Último commit de código:** `d61609df65b23c49a81851dc5742c73261a6d924`.

Última validación informada: **723/723**, 0 fallos, 0 errores, 0 omitidos, `BUILD SUCCESS`, 16/09/2026 13:11:00 -03:00.

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
- Valorización histórica de cierre separada de la liquidación.
- Uso de la valorización de cierre para crédito disponible de consumos multidivisa valorizados.

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
9. cuenta pagadora en moneda de liquidación;
10. valorización histórica de cierre;
11. persistencia de la valorización de cierre;
12. utilización de la valorización para crédito disponible.

## Pendientes inmediatos

1. Definir el flujo de obtención y registro de la valorización de cierre dentro de la aplicación.
2. Definir qué ocurre con una obligación multidivisa que todavía no tiene valorización de cierre.
3. Revisar el crédito utilizado después de pagos parciales sobre obligaciones valorizadas.
4. Diseñar tests para las reglas anteriores antes de modificar el código.
5. Completar persistencia/UI del flujo integral de cierre y pago multidivisa.

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
