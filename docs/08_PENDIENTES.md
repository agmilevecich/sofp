# SOFP — Pendientes

Este documento contiene únicamente trabajo pendiente o por decidir.

## Pendientes inmediatos

- Mantener sincronizada la documentación de continuidad de `docs/continuidad-sofp` con los remotos.
- Definir el siguiente bloque funcional de `feature/operacion-financiera`.
- Verificar si la persistencia de `OperacionFinanciera` requiere un `OperacionFinancieraRepository` independiente antes de implementarlo.

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

## Build 047 — Cerrado

La cobertura de `OperacionFinancieraService` quedó completa para las reglas de negocio actualmente implementadas.

`OperacionFinancieraServiceTest`: **20 tests en verde**.

Suite completa: **309/309 tests en verde**, con `Failures: 0`, `Errors: 0`, `Skipped: 0` y `BUILD SUCCESS`.

No quedan pendientes de cobertura correspondientes al servicio de operación financiera dentro del alcance actual.

## Estado de Git de referencia

- Rama de funcionalidad: `feature/operacion-financiera`.
- Último commit de código: `615161c` — `test: completar cobertura de OperacionFinancieraService`.
- Rama de documentación: `docs/continuidad-sofp`.
- `main` permanece en `028aaee` y no fue modificado por Build 047.

## Pendientes de arquitectura / evolución

- Vincular formalmente ambos movimientos con `OperacionFinanciera` si el modelo de dominio lo requiere mediante una relación persistente.
- Confirmar la estrategia definitiva de coordinación transaccional de la operación.
- Evaluar si corresponde incorporar `OperacionFinancieraRepository`.
- Completar progresivamente la capa `service` según las necesidades reales del dominio.
- Incorporar DTOs cuando los casos de uso y las fronteras de la aplicación lo requieran.
- Incorporar la interfaz de usuario cuando el dominio y los casos de uso estén suficientemente consolidados.
- Definir reportes y cálculos derivados de movimientos.
- Ampliar las reglas de saldos y consistencia financiera.

## Regla

No convertir un pendiente en trabajo realizado hasta que exista implementación verificable y tests correspondientes.
