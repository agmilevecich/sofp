# SOFP — Contexto final de continuidad

## Estado de cierre — 04/10/2026

### Rama

`feature/swing-shell`

### main

`a23d3a5c0658ffbca93391c34f79ad8bc37fdc10`

### Comparación

**320 commits ahead / 0 behind**

No se modificó ni mergeó `main`.

### Suite completa

**979/979**

- failures: 0
- errors: 0
- skipped: 0
- BUILD SUCCESS
- finalizada: 03/10/2026 22:54:12 -03:00
- duración: 24:47 min

## Última etapa: auditoría de pasivos

La auditoría confirmó que el pasivo usado para el cálculo patrimonial se reconstruye desde obligaciones, financiaciones, cargos, liquidaciones y refinanciaciones.

Se encontró y corrigió una doble contabilización del capital financiado cuando una obligación con cuotas tenía además una financiación normal pendiente.

La corrección quedó limitada a `ObligacionRepository`; no se inventó una nueva regla contable.

### Tests finales de la etapa

- `ObligacionRepositoryTest`: 17/17.
- `PatrimonioFinancieroServiceTest`: 13/13.
- `ObligacionServiceTest` + `RefinanciacionTest`: 36/36.
- Caso específico de liquidación parcialmente financiada: 1/1.
- Suite completa: 979/979.

## Estado funcional

Pasivos y patrimonio quedan validados técnicamente en los escenarios auditados.

La siguiente etapa no debe partir de documentación histórica. Debe comenzar por:

`GitHub → rama → commits → comparación con main → código → tests → reglas → próximo cambio mínimo`

## Pendientes técnicos

- calendario bancario de feriados;
- `Clock`;
- migraciones/versionado formal de esquema;
- estabilización del arranque/parada de H2;
- mejoras de UI avanzada;
- optimizaciones de repositorio cuando sean necesarias.

Las decisiones contables o financieras no definidas explícitamente requieren definición antes de implementarse.
