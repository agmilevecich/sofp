# SOFP — Nuevo chat / continuidad inmediata

## Snapshot vigente — 04/10/2026

Retomar siempre desde GitHub, no desde este archivo.

- Rama: `feature/swing-shell`
- main: `a23d3a5c0658ffbca93391c34f79ad8bc37fdc10`
- feature vs main: **320 ahead / 0 behind**
- main no fue modificada ni mergeada.
- Último commit de la etapa: `5a8cb25be5f15c776007b136f243e5e01f2f6bb1` — `test: corregir monto de financiacion sobre liquidacion`.

## Último resultado informado por el usuario

`mvn test`

**979 tests, 0 failures, 0 errors, 0 skipped, BUILD SUCCESS**

Finalizado 03/10/2026 22:54:12 -03:00. Duración 24:47 min.

## Qué se acaba de cerrar

Auditoría de pasivos integrada con patrimonio.

La corrección de producción evitó duplicar el capital de una financiación normal cuando la obligación tiene cuotas pendientes. El cálculo de crédito utilizado mantiene separados:

- obligación/liquidación;
- cuotas;
- financiaciones;
- cargos;
- refinanciaciones;
- consumos sin obligación.

Se validaron también liquidaciones financiadas, pagos y escenarios multidivisa.

## Evidencia

- ObligacionRepositoryTest: 17/17.
- PatrimonioFinancieroServiceTest: 13/13.
- ObligacionServiceTest + RefinanciacionTest: 36/36.
- Caso específico: 1/1.
- Suite completa: 979/979.

## Próximo paso

No hacer cambios todavía sin reconstruir GitHub. Identificar el siguiente bloque funcional real a partir del código actual y sus tests. Si la siguiente tarea implica una regla contable/financiera nueva, definirla antes de modificar producción.

## Flujo habitual

GitHub → cambio mínimo → commit pequeño → usuario ejecuta `git syncsofp` y tests → usuario informa resultado → diagnóstico → siguiente paso.

Nunca asumir que el usuario sincronizó ni que un test pasó si no lo informó.
