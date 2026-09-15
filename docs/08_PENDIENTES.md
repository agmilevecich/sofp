# SOFP — Pendientes

## Estado auditado — 15/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.
**HEAD:** `c74717ab0e97398764a7ef6b4c9cb55cb93f20c5`.

Última suite completa informada: **712/712**, BUILD SUCCESS, 0 fallos, 0 errores, 0 omitidos. Finalizada el 15/09/2026 a las 18:05:46 -03:00.

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
- `TipoCambio` histórico.
- Separación de moneda original y moneda de liquidación de `Obligacion`.
- Liquidación explícita y trazable de una obligación mediante `TipoCambio`.
- Persistencia de la cotización histórica utilizada en la liquidación.

## Estado multidivisa

Ya resuelto:

1. saldo de cuenta filtrado por moneda;
2. disponibilidad de fondos filtrada por moneda;
3. coexistencia de saldos ARS/USD sin mezcla;
4. moneda original y moneda de liquidación en obligaciones;
5. cotización histórica explícita con fecha y fuente;
6. asociación de la cotización utilizada a la obligación;
7. cálculo explícito del importe de liquidación.

Pendiente:

1. integrar la liquidación multidivisa en `PagoTarjetaService`;
2. preservar el flujo actual de pagos en moneda coincidente;
3. definir impacto de consumos en moneda distinta sobre límite/crédito disponible;
4. cubrir pago real cruzando monedas;
5. validar la integración con UI.

No se deben introducir conversiones implícitas.

## P0/P1 — Próximo bloque

- Revisar `PagoTarjetaService` y sus tests actuales.
- Diseñar el cambio mínimo para aceptar una liquidación histórica explícita cuando corresponda.
- Mantener sin cambios el comportamiento de moneda coincidente.
- Agregar tests específicos, relacionados, persistencia y UI cuando corresponda.

## P2 — Robustez

- Política de eliminación de cuentas con historial financiero.
- Abstracción `Clock`.
- Migraciones/versionado formal de esquema para una futura etapa no local.

## P3 — Evolución

- Financiación avanzada: intereses, CFT, cuotas variables, adelantos, refinanciación, anulaciones/reversiones y ajustes.
- UI específica de tarjetas: límite/disponible, consumos, ciclos, cierres, vencimientos, deuda y pagos reales.
- Pasivos, patrimonio y análisis.
- Gestión de entidades financieras.
- Pulido de consola.

## Fuera de alcance actual

Calendario de feriados, fecha efectiva separada del movimiento e intereses/punitorios/CFT/refinanciación requieren decisiones de negocio antes de implementarse.

## Regla de cierre

Tests específicos → relacionados → suite general → `git diff` → `git diff --check` → `git status` → documentación.

La documentación de continuidad debe reflejar el último estado real de GitHub y nunca reemplazar la verificación del código y los tests.
