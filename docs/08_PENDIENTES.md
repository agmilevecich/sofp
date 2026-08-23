# SOFP — Pendientes

Este documento contiene únicamente trabajo pendiente o por decidir.

## Pendientes inmediatos

- Mantener sincronizada la documentación de continuidad de `docs/continuidad-sofp` con los remotos.
- Definir el siguiente bloque funcional de `feature/operacion-financiera`.

## Trabajo recientemente completado

- Build 029: eliminación en `CategoriaRepository`.
- Build 030: eliminación en `CategoriaService`.
- Build 031: eliminación en `CuentaRepository`.
- Build 032: eliminación en `CuentaService`.
- Build 033: reglas de negocio de `Movimiento`.
- Build 034: ampliación de cobertura de `MovimientoServiceTest`.
- Build 035: ampliación de cobertura de `CuentaServiceTest`.
- Build 036: ampliación de cobertura de `InstitucionFinancieraServiceTest`.
- Build 037: ampliación de cobertura de `MonedaServiceTest`.
- Build 038: ampliación de cobertura de `PerfilFinancieroServiceTest`.
- Build 039: ampliación de cobertura de `UsuarioServiceTest` y endurecimiento del contrato de `UsuarioService`.
- Build 040: ampliación de cobertura de `CategoriaServiceTest`.
- Build 041: reforzamiento de validaciones de servicios y dominio y ampliación de cobertura de `InstitucionFinancieraServiceTest`.
- Build 042: ampliación de cobertura de `CuentaServiceTest`.
- Build 043: ampliación de cobertura de `MovimientoServiceTest`.
- Build 044: ampliación de `MovimientoTest`, con 23 tests nuevos, 27/27 tests específicos en verde y suite general 282/282 en verde.
- Build 045: implementación del dominio `OperacionFinanciera`, con 7 tests específicos en verde y suite general 289/289 en verde.
- Build 046: implementación de `OperacionFinancieraService`, ampliación de `OperacionFinancieraServiceTest` hasta 20/20 y cierre de la suite general con 300/300 tests en verde.
- Build 047: finalización de la cobertura de `OperacionFinancieraServiceTest` y cierre de la suite general con 309/309 tests en verde.
- Build posterior: implementación de `OperacionFinancieraRepository`, integración con `OperacionFinancieraService` y ampliación de la suite general hasta 319/319 tests en verde.

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

## Estado de Git de referencia

- Rama de funcionalidad: `feature/operacion-financiera`.
- Rama de documentación: `docs/continuidad-sofp`.
- Las ramas `feature/operacion-financiera` y `docs/continuidad-sofp` están sincronizadas apuntando al commit `6f39c58`.
- `main` permanece separado y no debe modificarse hasta que el bloque funcional esté considerado estable.

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
