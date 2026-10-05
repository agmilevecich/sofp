# SOFP — Continuidad canónica actual

> Última actualización: 05/10/2026.  
> Fuente de verdad: código actual, tests y commits de GitHub. Esta documentación es auxiliar y puede quedar desactualizada; si contradice al código, prevalece el código.

## Estado actual

- Rama de trabajo: `feature/swing-shell`.
- Rama estable: `main`.
- `main`: `a23d3a5c0658ffbca93391c34f79ad8bc37fdc10`.
- HEAD actual: `f60c225984a96d025d148f5368f21ecc67bd8e1b` — `fix: localizar panel de movimientos contextual en test`.
- Comparación GitHub: **365 commits ahead / 0 behind**.
- No se modificó ni mergeó `main`.

## Última validación completa

`mvn test` ejecutado por el usuario:

- **986 tests**
- **0 failures**
- **0 errors**
- **0 skipped**
- `BUILD SUCCESS`
- Finalizado: **05/10/2026 19:24:45 -03:00**
- Duración: **28:05 min**

Esta es la última suite completa conocida y reemplaza los resultados históricos anteriores.

## Validación RC1 — estabilidad de uso real

La etapa RC1 tuvo como objetivo verificar el arranque, persistencia y flujo de uso real de la aplicación sin introducir cambios contables implícitos.

### Arranque y persistencia H2

Se validó manualmente el arranque de la aplicación con la base H2 ausente. El servidor H2 se inicia en el puerto 9092 antes de JPA y, mediante `-ifNotExists`, permite crear automáticamente `database/sofp.mv.db` cuando todavía no existe.

También se confirmó mediante `netstat` que el servidor H2 queda escuchando en 9092.

Se validó persistencia entre reinicios: se creó una categoría desde la aplicación, se cerró SOFP completamente y, después del reinicio y login, la categoría continuó disponible.

### Movimientos, gastos y patrimonio

Se estableció que `MovimientosPanel` es un panel informativo de consulta. El alta de movimientos se realiza desde los paneles de Ingresos y Gastos.

Se cubrió el flujo de gasto por transferencia desde la UI. La transferencia se comporta como egreso monetario normal y reduce el saldo de la cuenta cuando existen fondos suficientes.

También se agregó cobertura para que un gasto de **86.000,00** sobre un ingreso previo de **1.500.000,00** produzca un saldo/patrimonio de **1.414.000,00**.

### Reportes

Se corrigió la actualización del panel de reportes al navegar hacia él después de registrar movimientos.

Se verificó que el reporte refleje el gasto registrado desde la UI y que la navegación no muestre datos obsoletos.

### Categorías

Se consolidó la clasificación por tipo de movimiento. Las categorías nuevas pueden quedar asociadas a `INGRESO` o `EGRESO`; las categorías históricas sin clasificación explícita permanecen como `Sin clasificar` y no se infiere su tipo por el nombre.

La cobertura específica de categorías continúa verde.

### Ajustes de tests RC1

Se corrigieron regresiones de tests provocadas por la evolución intencional de la UI:

- `MainFrameCategoriasTest`: adaptado a categorías tipadas.
- `MainFrameMovimientosTest`: adaptado al panel informativo de movimientos.
- Se corrigió la localización del panel contextual de movimientos en el test.
- Se corrigió la preparación de fondos del test de gastos.
- Se corrigió la conservación de la cuenta seleccionada al cambiar la forma de pago en Gastos.

La ejecución enfocada posterior a estas correcciones quedó en **4/4**, 0 failures, 0 errors, 0 skipped.

## Etapa cerrada: auditoría de pasivos y patrimonio neto

Se auditó el recorrido:

`ObligacionRepository → Obligacion / Financiacion / Refinanciacion → PatrimonioFinancieroService → ResumenPatrimonial`

y su interacción con obligaciones de tarjeta, liquidaciones, cuotas, financiación, cargos, refinanciación, pagos y escenarios multidivisa.

### Corrección de producción

Se corrigió `ObligacionRepository.sumarCreditoUtilizadoPorCuenta()` para evitar duplicar el capital de una financiación normal cuando la obligación también tiene cuotas pendientes.

La corrección resta de la suma de cuotas el capital pendiente de financiaciones normales asociadas a obligaciones con cuotas. No se modificaron reglas contables nuevas ni se introdujeron conversiones implícitas.

### Cobertura validada

- `ObligacionRepositoryTest`: **17/17**
- `PatrimonioFinancieroServiceTest`: **14/14**
- `ObligacionServiceTest + RefinanciacionTest`: **36/36**
- suite completa posterior: **986/986**

## Punto exacto para continuar

**RC1 — estabilidad de uso real quedó validada técnicamente en la suite completa: 986/986.**

No hay un fallo pendiente conocido en este bloque.

Antes de iniciar una nueva funcionalidad, corresponde hacer la validación final de repositorio local indicada por el flujo del proyecto:

`git syncsofp → git diff → git diff --check → git status`

y revisar que el estado local coincida con el HEAD de GitHub.

No hacer merge a `main` automáticamente.

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
