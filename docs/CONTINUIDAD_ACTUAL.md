# SOFP — Continuidad canónica actual

> Última actualización: 05/10/2026.  
> Fuente de verdad: código actual, tests y commits de GitHub. Esta documentación es auxiliar y puede quedar desactualizada; si contradice al código, prevalece el código.

## Estado actual

- Rama de trabajo: `feature/swing-shell`.
- Rama estable: `main`.
- `main`: `a23d3a5c0658ffbca93391c34f79ad8bc37fdc10`.
- Comparación GitHub: **345 commits ahead / 0 behind**.
- No se modificó ni mergeó `main`.
- Último commit relevante: `5c002c8776ee438771e3012cd0edb373c44951c7` — `fix: permitir crear la base H2 al arrancar`.

## Última validación completa

`mvn test` ejecutado por el usuario:

- **979 tests**
- 0 failures
- 0 errors
- 0 skipped
- `BUILD SUCCESS`
- Finalizado: **03/10/2026 22:54:12 -03:00**
- Duración: **24:47 min**

Esta es la última suite completa conocida y reemplaza los resultados históricos anteriores.

## Etapa cerrada: auditoría de pasivos y patrimonio neto

Se auditó el recorrido:

`ObligacionRepository → Obligacion / Financiacion / Refinanciacion → PatrimonioFinancieroService → ResumenPatrimonial`

y su interacción con obligaciones de tarjeta, liquidaciones, cuotas, financiación, cargos, refinanciación, pagos y escenarios multidivisa.

### Corrección de producción

Se corrigió `ObligacionRepository.sumarCreditoUtilizadoPorCuenta()` para evitar duplicar el capital de una financiación normal cuando la obligación también tiene cuotas pendientes.

La corrección resta de la suma de cuotas el capital pendiente de financiaciones normales asociadas a obligaciones con cuotas. No se modificaron reglas contables nuevas ni se introdujeron conversiones implícitas.

### Cobertura validada

- `ObligacionRepositoryTest`: **17/17**
- `PatrimonioFinancieroServiceTest`: **13/13**
- `ObligacionServiceTest + RefinanciacionTest`: **36/36**
- caso específico de liquidación parcialmente financiada: **1/1**
- suite completa: **979/979**

Quedaron cubiertos, entre otros, obligación simple, cuotas, financiación completa y parcial, cargos, pagos de capital/cargos, liquidación, liquidación parcialmente financiada, refinanciación activa, multidivisa con cotización histórica y aislamiento de la deuda respecto del patrimonio.

## Validación RC1 — ciclo de vida de H2

Se validó manualmente el arranque de la aplicación con la base H2 ausente. El servidor H2 se inicia en el puerto 9092 antes de JPA y, mediante la opción `-ifNotExists`, permite crear automáticamente `database/sofp.mv.db` cuando todavía no existe. SOFP volvió a funcionar con el mismo flujo de uso habitual después de recrear la base.

También se confirmó mediante `netstat` que el servidor H2 queda escuchando en 9092 y que la conexión de SOFP se establece correctamente.

La prueba funcional de categorías conocida continúa en **5/5**, sin fallos ni errores. La última suite completa conocida sigue siendo la de **979/979** del 03/10/2026; todavía no se debe considerar una nueva suite completa ejecutada después de los cambios posteriores.

## Punto exacto para continuar

La auditoría de pasivos quedó validada técnicamente. No hay un fallo pendiente conocido en este bloque.

El próximo trabajo debe elegirse reconstruyendo nuevamente el estado desde GitHub y revisando código, tests y reglas de negocio actuales. No continuar agregando tests por inercia si no existe un hueco funcional real.

## Pendientes conocidos

- Definiciones financieras que todavía no estén expresadas explícitamente en el modelo, especialmente donde impliquen nuevas reglas contables.
- Evolución de UI avanzada de financiación/refinanciación.
- Calendario bancario de feriados.
- Abstracción `Clock`.
- Migraciones/versionado formal de esquema.
- Estabilización futura del arranque/parada de H2 y manejo de errores de arranque.
- Optimización técnica de consultas de movimientos cuando corresponda, separada de reglas contables.

## Regla permanente de continuidad

Antes de cualquier cambio:

`GitHub → rama → últimos commits → comparación con main → código → repositorios → tests → reglas de negocio → documentación → último resultado → cambio mínimo`

Después de cambios importantes:

`tests específicos → tests relacionados → suite completa → git diff → git diff --check → git status → documentación`

No modificar ni mergear `main` automáticamente.
