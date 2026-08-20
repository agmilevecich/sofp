# SOFP — Pendientes

Este documento contiene únicamente trabajo pendiente o por decidir.

## Pendientes inmediatos

- Revisar el estado actual del dominio, servicios, repositorios y tests para definir el siguiente bloque de trabajo.
- Definir la próxima funcionalidad antes de implementar código nuevo.
- Mantener sincronizada la documentación de continuidad de `docs/continuidad-sofp` con los remotos.

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

## Build 044 — Cerrado

Se amplió `MovimientoTest`, pasando de **4 a 27 tests en verde**.

Se agregaron **23 tests** para validar constructor y comportamiento de modificación de `Movimiento`, incluyendo valores nulos, importes inválidos, descripción, fecha/hora, categoría, tipo y observaciones.

No se modificó código de producción.

Commit de código: `6f53f79` — `test: ampliar cobertura de Movimiento`.

El commit fue publicado en `main` de GitHub y Bitbucket mediante `git pushall`. El working tree quedó limpio.

La suite general posterior al cambio confirmó:

- Tests run: **282**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`

La ejecución finalizó el **20/08/2026 a las 13:19:27 -03:00** y duró **07:21 min**.

**Build 044 queda cerrado y validado.**

## Estado de Git de referencia

- Último commit de código confirmado: `6f53f79` — `test: ampliar cobertura de Movimiento`.
- La documentación de continuidad se mantiene en `docs/continuidad-sofp`.

## Pendientes de arquitectura / evolución

- Completar progresivamente la capa `service` según las necesidades reales del dominio.
- Incorporar DTOs cuando los casos de uso y las fronteras de la aplicación lo requieran.
- Incorporar la interfaz de usuario cuando el dominio y los casos de uso estén suficientemente consolidados.
- Definir reportes y cálculos derivados de movimientos.
- Ampliar las reglas de saldos y consistencia financiera.
- Diseñar e implementar `OperacionFinanciera` cuando corresponda al siguiente bloque de dominio.

## Regla

No convertir un pendiente en trabajo realizado hasta que exista implementación verificable y tests correspondientes.
