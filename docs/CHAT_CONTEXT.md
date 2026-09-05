# SOFP — Contexto para continuar con ChatGPT

## Estado actual — 05/09/2026

La fuente de verdad es el código, los tests y los commits actuales. `docs/` es documentación auxiliar.

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

Comparación verificada: `feature/swing-shell` está **274 commits por delante y 2 por detrás** de `main`. No se realizó merge.

## Último estado funcional

El shell Swing de Fase 8 integra Inicio, Cuentas, Categorías, Gastos, Movimientos, Inversiones y Reportes mediante `CardLayout`.

La arquitectura funcional acordada es:

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

Gastos utiliza `GastosPanel → GastoService → MovimientoService → Movimiento` de tipo `EGRESO`, y el resultado aparece en `Movimientos`.

## FormaPago

Integración **completada y validada**.

`Movimiento` persiste la forma de pago. `MovimientoService` la propaga al registrar. `GastoService` exige una forma de pago.

Formas disponibles: `EFECTIVO`, `TRANSFERENCIA`, `TARJETA_DEBITO`, `TARJETA_CREDITO` y `QR`.

`TARJETA_CREDITO` continúa temporalmente rechazada hasta implementar obligaciones/pasivos. No se debe simular un egreso inmediato sobre una cuenta para una compra a crédito.

## Última validación conocida

El usuario informó el **05/09/2026 13:04:09 -03:00**:

- `mvn test`;
- Tests run: **590**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración: **11:29 min**.

## Reglas vigentes

Los egresos respetan fondos disponibles, incluyendo modificaciones de importe y tipo. Un egreso igual al saldo está permitido y deja saldo cero.

Las categorías con movimientos se conservan y se desactivan en lugar de eliminarse físicamente.

Cuenta y forma de pago son conceptos distintos.

Las transferencias entre cuentas propias no son ingresos ni gastos y se relacionan mediante `OperacionFinanciera`.

La UI no duplica reglas de negocio.

## Próximo bloque

El próximo bloque funcional real es el diseño de obligaciones/pasivos para tarjeta de crédito, si se decide continuar con esa funcionalidad. Después podrán evolucionarse ingresos/transferencias, pasivos, patrimonio neto y capacidades de análisis.

## Protocolo para nuevas sesiones

1. Revisar rama actual.
2. Revisar últimos commits.
3. Comparar con `main`.
4. Revisar README y documentación de continuidad.
5. Revisar archivos modificados recientemente.
6. Revisar tests relacionados.
7. Identificar último cambio, último test conocido y próximo paso.

Prioridad: **código → tests → commits → main → documentación**.

No modificar `main`, no crear ramas nuevas salvo indicación explícita y no asumir sincronizaciones o resultados de tests no informados.
