# SOFP — Pendientes

## Estado auditado — 15/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

**Último bloque funcional:** validación de `Moneda.cantidadDecimales`.
**Última suite completa informada:** **693/693**, BUILD SUCCESS, 0 fallos, 0 errores, 0 omitidos. Finalizada el 15/09/2026 a las 12:03:19 -03:00.

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
- Saldos y disponibilidad de fondos separados por moneda.
- Validación de `Moneda.cantidadDecimales` no negativa.

## Estado multidivisa

La moneda económica de un movimiento puede diferir de la moneda de la cuenta.

Ya resuelto:

1. saldo de cuenta filtrado por moneda;
2. disponibilidad de fondos filtrada por moneda;
3. coexistencia de saldos ARS/USD sin mezcla.

Pendiente:

1. impacto de un consumo en moneda distinta sobre el límite de la tarjeta;
2. moneda de liquidación;
3. tasa, fecha y fuente de cotización;
4. conversión/liquidación trazable de pagos entre monedas;
5. tests específicos de estos casos.

No se deben introducir conversiones implícitas.

## P0/P1 — Próximo bloque

- Cerrar multidivisa de tarjetas sin conversiones implícitas.
- Definir las reglas de crédito disponible para consumos en moneda distinta.
- Definir liquidación/conversión de pagos cuando corresponda.
- Implementar el cambio mínimo después de fijar las reglas.
- Agregar tests específicos y relacionados.

## P2 — Robustez

- Política de eliminación de cuentas con historial financiero.
- Abstracción `Clock`.
- Migraciones/versionado formal de esquema para una futura etapa no local.

## P3 — Evolución

- Financiación avanzada: intereses, CFT, cuotas variables, adelantos, refinanciación, anulaciones/reversiones y ajustes.
- UI específica de tarjetas: límite/disponible, consumos, ciclos, cierres, vencimientos, deuda y pagos.
- Pasivos, patrimonio y análisis.
- Gestión de entidades financieras.
- Pulido de consola.

## Fuera de alcance actual

Calendario de feriados, fecha efectiva separada del movimiento e intereses/punitorios/CFT/refinanciación requieren decisiones de negocio antes de implementarse.

## Regla de cierre

Tests específicos → relacionados → suite general → `git diff` → `git diff --check` → `git status` → documentación.

La documentación de continuidad debe reflejar el último estado real de GitHub y nunca reemplazar la verificación del código y los tests.
