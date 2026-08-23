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

## Estado actual — OperacionFinancieraRepository

La implementación de `OperacionFinancieraRepository` está completada e integrada en `OperacionFinancieraService`.

Incluye:

- Guardado de operaciones nuevas mediante `persist`.
- Actualización de operaciones existentes mediante `merge`.
- Búsqueda por identificador mediante `Optional`.
- Listado de todas las operaciones ordenadas por id.
- Listado por cuenta de origen.
- Listado por cuenta de destino.
- Validación de parámetros obligatorios mediante `NullPointerException`.

Cobertura específica del repositorio: **10 tests en verde**.

Cobertura del servicio `OperacionFinancieraService`: **20 tests en verde**.

Suite completa: **319/319 tests en verde**, con `Failures: 0`, `Errors: 0`, `Skipped: 0` y `BUILD SUCCESS`.

Por lo tanto, `OperacionFinancieraRepository` ya no es un pendiente y no debe volver a aparecer como trabajo por implementar.

## Estado Git de referencia

- Rama de funcionalidad: `feature/operacion-financiera`.
- Rama de documentación: `docs/continuidad-sofp`.
- `main` permanece separado y no debe modificarse hasta que el bloque funcional esté considerado estable.
- Último commit de código: `3d0543c` — `feat: implementar repositorio de operacion financiera`.

## Pendientes de arquitectura / evolución

- Vincular formalmente ambos movimientos con `OperacionFinanciera` si el modelo de dominio lo requiere mediante una relación persistente.
- Confirmar la estrategia definitiva de coordinación transaccional de la operación.
- Completar progresivamente la capa `service` según las necesidades reales del dominio.
- Incorporar DTOs cuando los casos de uso y las fronteras de la aplicación lo requieran.
- Incorporar la interfaz de usuario cuando el dominio y los casos de uso estén suficientemente consolidados.
- Definir reportes y cálculos derivados de movimientos.
- Ampliar las reglas de saldos y consistencia financiera.

## Regla

No convertir un pendiente en trabajo realizado hasta que exista implementación verificable y tests correspondientes.