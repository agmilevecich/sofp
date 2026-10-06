# SOFP — Continuidad canónica actual

> Última actualización: 06/10/2026.  
> Fuente de verdad: código actual, tests y commits de GitHub. Esta documentación es auxiliar y puede quedar desactualizada; si contradice al código, prevalece el código.

## Estado actual

- Rama de trabajo: `feature/swing-shell`.
- Rama estable: `main`.
- `main`: `a23d3a5c0658ffbca93391c34f79ad8bc37fdc10`.
- HEAD actual: `d03e26e44766ea76889381223360f0e57894bb34` — `test: corregir consulta de refinanciacion en obligaciones`.
- Comparación GitHub: **385 commits ahead / 0 behind**.
- No se modificó ni mergeó `main`.

## Última validación completa

`mvn test` ejecutado por el usuario después de integrar la UI de refinanciación:

- **994 tests**
- **0 failures**
- **0 errors**
- **0 skipped**
- `BUILD SUCCESS`
- Finalizado: **06/10/2026 17:55:38 -03:00**
- Duración: **30:47 min**

Este resultado reemplaza al anterior de 991/991.

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
- Batería completa de inversiones, incluyendo compra y venta: **177/177**
- Suite completa posterior: **991/991**

Todos los resultados fueron 0 failures, 0 errors y 0 skipped.

## Etapa actual: UI de refinanciación

Se incorporó la primera integración funcional de refinanciación en la interfaz Swing.

### Implementación

- Nuevo `RefinanciacionForm` para capturar fecha de inicio, cantidad de cuotas, interés inicial, cargos iniciales y tasa anual.
- `ObligacionesPanel` incorpora la acción de refinanciar la obligación seleccionada.
- `RefinanciacionService` quedó conectado al flujo de `MainFrame` y se inicializa desde `Main` usando el mismo `EntityManager`.
- Se preservaron los constructores legacy de `MainFrame` y `ObligacionesPanel` mediante delegaciones compatibles.
- La refinanciación queda restringida a obligaciones que corresponden al flujo de tarjeta de crédito y que no estén pagadas o ya refinanciadas.

### Validación específica

La integración quedó validada con:

- `MainFrameTest` + `MainFrameReportesTest` + `ObligacionesPanelTest` + `RefinanciacionFormTest`: **15/15**
- Suite completa posterior: **994/994**
- 0 failures, 0 errors y 0 skipped.

Los commits principales de esta integración fueron:

- `d764811` — `feat: agregar formulario de refinanciacion`
- `42e8c6e` — `test: cubrir formulario de refinanciacion`
- `9e86434` — `feat: integrar refinanciacion en obligaciones`
- `2121d2c` — `test: cubrir refinanciacion desde obligaciones`
- `318e29b` — `feat: pasar refinanciacion al marco principal`
- `f274b85` — `feat: inicializar servicio de refinanciacion`
- `c8672fd`, `ddf323a`, `8110039`, `57df93a`, `bb940b4` — correcciones de constructores y delegaciones.
- `d03e26e` — corrección de la consulta de refinanciación en el test de obligaciones.

## RC1 — estabilidad de uso real

La estabilidad de RC1 mantiene validación completa. La suite pasó de 991/991 a **994/994** después de la incorporación de la UI de refinanciación, sin regresiones.

Se mantienen validadas las comprobaciones anteriores de:

- arranque y creación/recuperación de H2;
- persistencia entre reinicios;
- login y navegación principal;
- categorías tipadas por movimiento;
- gastos y transferencias;
- actualización de reportes;
- patrimonio financiero;
- movimientos informativos;
- pasivos, financiación y refinanciación;
- inversiones y validación temporal de posiciones.

## Punto exacto para continuar

**La integración UI de refinanciación está validada por la suite completa 994/994.**

El siguiente paso es una **validación funcional manual del flujo de refinanciación desde la aplicación ejecutable**, no otra ejecución automática de la suite.

Orden propuesto:

1. `git syncsofp`
2. `git status`
3. ejecutar la aplicación;
4. ingresar con el usuario de desarrollo;
5. abrir Obligaciones;
6. seleccionar una obligación de tarjeta elegible;
7. verificar que aparece/habilita **Refinanciar**;
8. completar el formulario;
9. confirmar la refinanciación;
10. comprobar que la obligación cambia de estado y que la refinanciación queda persistida;
11. cerrar y volver a abrir la aplicación para comprobar persistencia.

Después de esa validación manual, revisar `git diff`, `git diff --check` y `git status`, y mantener la documentación actualizada.

No hacer merge a `main` automáticamente.

## Pendientes conocidos

- Validación funcional manual completa de la UI de refinanciación.
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
