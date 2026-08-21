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

## Build 046 — Cerrado

Se incorporó `OperacionFinancieraService` en `feature/operacion-financiera`.

El servicio materializa una transferencia mediante:

- una `OperacionFinanciera`;
- un `EGRESO` en la cuenta origen;
- un `INGRESO` en la cuenta destino;
- persistencia coordinada dentro de una única transacción.

También valida cuentas activas, coherencia de perfiles financieros, moneda común y parámetros obligatorios. La descripción puede ser nula, según el contrato validado por los tests.

Commits asociados:

- `a995937` — `feat: implementar servicio de operacion financiera`.
- `2e4b94f` — `fix: permitir descripcion nula en transferencia`.

`OperacionFinancieraServiceTest` quedó en **20/20 tests en verde**.

La suite completa del proyecto quedó en **300/300 tests en verde**, con `Failures: 0`, `Errors: 0`, `Skipped: 0` y `BUILD SUCCESS`.

**Build 046 queda cerrado.**

## Estado de Git de referencia

- Rama de funcionalidad: `feature/operacion-financiera`.
- Último commit de código de Build 046: `2e4b94f` — `fix: permitir descripcion nula en transferencia`.
- Rama de documentación: `docs/continuidad-sofp`.
- `main` permanece en `028aaee` y no fue modificado por Build 046.

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
