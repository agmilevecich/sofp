# SOFP — Contexto final de continuidad

## Estado auditado — 15/09/2026

La fuente de verdad es el código, Git y los tests actuales; `docs/` es documentación auxiliar. Antes de proponer cambios, reconstruir siempre el estado desde GitHub.

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.
**HEAD:** `c74717ab0e97398764a7ef6b4c9cb55cb93f20c5`.

No se realizó merge a `main`.

## Validación más reciente

Suite general: **712/712**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizada 15/09/2026 18:05:46 -03:00.

## Último bloque cerrado

Liquidación histórica multidivisa de `Obligacion`.

- `TipoCambio` histórico e inmutable.
- moneda original y moneda de liquidación separadas.
- tipo de cambio utilizado asociado a la obligación.
- liquidación explícita con validación de monedas.
- importe de liquidación separado del importe original.
- prevención de doble liquidación.
- persistencia JPA validada.

## Próximo bloque

Integrar esta liquidación en `PagoTarjetaService` y definir el impacto de consumos extranjeros sobre el crédito disponible. No introducir conversiones implícitas.

## Reglas vigentes

Saldos y fondos se calculan por moneda. Las obligaciones conservan historial temporal. Pagos requieren autorización de usuario. La integridad histórica de movimientos, obligaciones y cuentas está protegida.

## Protocolo

Antes de cada cambio: rama → commits → comparación con `main` → documentación → código → tests → último resultado informado. Después: cambio mínimo → tests específicos → relacionados → suite → diff → diff-check → status → documentación.
