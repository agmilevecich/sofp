# SOFP — Continuidad canónica actual

> Última actualización: 06/10/2026.  
> Fuente de verdad: código actual, tests y commits de GitHub. Esta documentación es auxiliar y puede quedar desactualizada; si contradice al código, prevalece el código.

## Estado actual

- Rama de trabajo: `feature/swing-shell`.
- Rama estable: `main`.
- `main`: `a23d3a5c0658ffbca93391c34f79ad8bc37fdc10`.
- HEAD actual: `26bf0f3a44956d8bbb5978893c8b12019e664015` — `fix: completar test de consulta temporal de movimientos de activos`.
- Comparación GitHub: **365 commits ahead / 0 behind** según la última comparación registrada.
- No se modificó ni mergeó `main`.
- Validación local posterior: rama sincronizada con GitHub/Bitbucket y working tree limpio.

## Última validación completa

`mvn test` ejecutado por el usuario:

- **991 tests**
- **0 failures**
- **0 errors**
- **0 skipped**
- `BUILD SUCCESS`
- Finalizado: **06/10/2026 15:07:38 -03:00**
- Duración: **31:08 min**

Este resultado reemplaza al anterior de 986/986 y es la última suite completa conocida.

## Etapa cerrada: auditoría de inversiones

La auditoría de inversiones verificó el recorrido de operaciones de compra/venta, movimientos de activos, posiciones, cartera, valorización, cotizaciones y aislamiento por perfil financiero.

### Corrección de producción

Se detectó y corrigió un hueco real en la validación de ventas: la posición disponible no debía incluir compras posteriores al momento de la venta.

Se incorporó en `MovimientoActivoRepository` una consulta temporal por activo, perfil y fecha/hora, y `OperacionFinancieraService.venderActivo()` pasó a validar la posición disponible al momento de la venta.

También quedó validada la compatibilidad de moneda entre activo y cuenta para compra y venta.

### Cobertura específica

La batería específica de inversiones quedó en:

- `OperacionFinancieraServiceTest`: **29/29**
- `MovimientoActivoRepositoryTest`: **11/11**
- `CarteraActivoServiceTest` + `CarteraActivoServiceMovimientosTest` + `PosicionActivoServiceTest`: **20/20**
- Batería completa de inversiones, incluyendo `OperacionFinancieraCompraServiceTest` y `OperacionFinancieraVentaServiceTest`: **177/177**
- Suite completa posterior: **991/991**

Todos los resultados fueron 0 failures, 0 errors y 0 skipped.

### Decisiones y límites actuales

- La posición de activos se reconstruye a partir de `MovimientoActivo`.
- La venta se valida contra la posición disponible al momento de la operación.
- No se introdujo una regla nueva que obligue a disponer de saldo monetario suficiente para comprar un activo; esa decisión queda fuera de esta auditoría porque implicaría una regla de negocio explícita.
- Las cotizaciones actuales pueden persistirse mediante `CotizacionActivoService`.
- Las conversiones y valorizaciones mantienen las reglas existentes; no se introdujeron conversiones implícitas.

## RC1 — estabilidad de uso real

La etapa RC1 quedó validada previamente con **986/986**. La suite completa posterior a la auditoría de inversiones subió a **991/991**, sin regresiones.

Se mantienen validadas las comprobaciones anteriores de:

- arranque y creación/recuperación de H2;
- persistencia entre reinicios;
- login y navegación principal;
- categorías tipadas por movimiento;
- gastos y transferencias;
- actualización de reportes;
- patrimonio financiero;
- movimientos informativos;
- pasivos, financiación y refinanciación.

## Punto exacto para continuar

**RC1 y la auditoría de inversiones están técnicamente validadas.**

El repositorio local fue sincronizado con GitHub y quedó limpio:

`git syncsofp` → actualizado/sincronizado  
`git status` → working tree clean  
`git diff` → sin cambios  
`git diff --check` → sin problemas

El siguiente paso no es ejecutar nuevamente la suite. Corresponde cerrar documentalmente esta auditoría y, después, reconstruir desde GitHub el próximo hueco funcional real antes de implementar otra funcionalidad.

No hacer merge a `main` automáticamente.

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
