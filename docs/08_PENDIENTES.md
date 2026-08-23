# SOFP — Pendientes

Este documento contiene únicamente trabajo pendiente o por decidir.

## Pendientes inmediatos

- Mantener sincronizada la documentación de continuidad de `docs/continuidad-sofp` con los remotos.
- Definir el siguiente bloque funcional de `feature/operacion-financiera`.

## Trabajo recientemente completado

- Build 044: ampliación de `MovimientoTest`, con 27/27 tests específicos y suite general 282/282 en verde.
- Build 045: implementación del dominio `OperacionFinanciera`, con 7 tests específicos y suite general 289/289 en verde.
- Build 046: implementación de `OperacionFinancieraService`, con 20 tests específicos y suite general 300/300 en verde.
- Build 047: finalización de cobertura de `OperacionFinancieraServiceTest` y suite general 309/309 en verde.
- Build 048: implementación de `OperacionFinancieraRepository`, integración con `OperacionFinancieraService`, 10 tests de repositorio y suite general 319/319 en verde.
- Build 049: asociación persistente de `Movimiento` con `OperacionFinanciera`, 7 tests nuevos, `OperacionFinancieraTest` en 14/14 y suite general 326/326 en verde.

## Estado actual — Asociación Movimiento / OperacionFinanciera

La relación entre `Movimiento` y `OperacionFinanciera` está implementada y validada.

Incluye:

- `Movimiento.operacionFinanciera` con `@ManyToOne` y columna `operacion_financiera_id`.
- `OperacionFinanciera.movimientos` con `@OneToMany(mappedBy = "operacionFinanciera")`.
- Colección expuesta como lista no modificable.
- Máximo de dos movimientos por operación.
- Rechazo de movimientos nulos.
- Rechazo de movimientos repetidos.
- Rechazo de movimientos ya asociados a otra operación financiera.
- Asociación automática del movimiento al agregarlo a la operación.
- `OperacionFinancieraService` asociando el `EGRESO` y el `INGRESO` antes de persistirlos.

Cobertura específica de `OperacionFinancieraTest`: **14 tests en verde**.

Suite completa: **326/326 tests en verde**, con `Failures: 0`, `Errors: 0`, `Skipped: 0` y `BUILD SUCCESS`.

Por lo tanto, la asociación entre `Movimiento` y `OperacionFinanciera` ya no es un pendiente y no debe volver a aparecer como trabajo por implementar.

## Estado Git de referencia

- Rama de funcionalidad: `feature/operacion-financiera`.
- Rama de documentación: `docs/continuidad-sofp`.
- `main` permanece separado y no debe modificarse hasta que el bloque funcional esté considerado estable.
- Último commit de código: `11dc0ae` — `feat: asociar movimientos a operacion financiera`.

## Pendientes de arquitectura / evolución

- Definir el siguiente bloque funcional de `feature/operacion-financiera`.
- Completar progresivamente la capa `service` según las necesidades reales del dominio.
- Incorporar DTOs cuando los casos de uso y las fronteras de la aplicación lo requieran.
- Incorporar la interfaz de usuario cuando el dominio y los casos de uso estén suficientemente consolidados.
- Definir reportes y cálculos derivados de movimientos.
- Ampliar las reglas de saldos y consistencia financiera.
- Confirmar la estrategia definitiva de coordinación transaccional de la operación si aparecen nuevos casos de uso que la requieran.

## Regla

No convertir un pendiente en trabajo realizado hasta que exista implementación verificable y tests correspondientes.
