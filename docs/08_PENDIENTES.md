# SOFP — Pendientes

Este documento contiene únicamente trabajo pendiente o por decidir.

## Pendientes inmediatos

- Revisar los casos de uso pendientes del dominio y definir el siguiente Build.
- Mantener sincronizada la documentación de continuidad de `docs/continuidad-sofp` con los remotos.
- No implementar código nuevo hasta definir objetivo y tests del siguiente bloque.

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

## Build 039

Se amplió `UsuarioServiceTest`, pasando de **5 a 15 tests en verde**, y se incorporó validación explícita de IDs nulos en `UsuarioService.activar(...)` y `UsuarioService.desactivar(...)`.

La batería general quedó en **227/227 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.

La ejecución terminó con `BUILD SUCCESS` el 18/08/2026 a las 22:34:24 -03:00.

Commit de código: `0e27dfe` — `test: ampliar cobertura de UsuarioService`.

El commit fue publicado en `main` de GitHub y Bitbucket mediante `git pushall`. El working tree quedó limpio.

## Estado de Git de referencia

- Último commit de código confirmado: `0e27dfe` — `test: ampliar cobertura de UsuarioService`.
- La documentación de Build 039 quedó registrada en `docs/01-builds/Build-039.md`.
- La documentación de continuidad se mantiene en `docs/continuidad-sofp`.

## Pendientes de arquitectura / evolución

- Completar progresivamente la capa `service` según las necesidades reales del dominio.
- Incorporar DTOs cuando los casos de uso y las fronteras de la aplicación lo requieran.
- Incorporar la interfaz de usuario cuando el dominio y los casos de uso estén suficientemente consolidados.
- Definir reportes y cálculos derivados de movimientos.
- Ampliar las reglas de saldos y consistencia financiera.

## Regla

No convertir un pendiente en trabajo realizado hasta que exista implementación verificable y tests correspondientes.
