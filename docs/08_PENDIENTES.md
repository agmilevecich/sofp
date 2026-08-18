# SOFP — Pendientes

Este documento contiene únicamente trabajo pendiente o por decidir.

## Pendientes inmediatos

- Revisar el estado de Git de los cambios de `MonedaServiceTest` y registrar el commit de código cuando corresponda.
- Mantener sincronizada la documentación de continuidad de `docs/continuidad-sofp` con los remotos.
- Revisar los casos de uso pendientes del dominio y definir el siguiente Build.
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

## Build 037

Se ampliaron los tests de `MonedaServiceTest`, pasando de **8 a 17 tests en verde**.

La batería general quedó en **210/210 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.

La ejecución terminó con `BUILD SUCCESS` el 18/08/2026 a las 18:52:19 -03:00.

El commit específico de código de Build 037 todavía no fue registrado. No se debe considerar publicado hasta contar con un SHA verificable.

## Estado de Git de referencia

- Último commit de código confirmado: `bd7f4bd` — `test: ampliar cobertura de InstitucionFinancieraService`.
- La documentación de Build 037 quedó registrada en `docs/01-builds/Build-037.md`.
- La documentación de continuidad se mantiene en `docs/continuidad-sofp`.

## Pendientes de arquitectura / evolución

- Completar progresivamente la capa `service` según las necesidades reales del dominio.
- Incorporar DTOs cuando los casos de uso y las fronteras de la aplicación lo requieran.
- Incorporar la interfaz de usuario cuando el dominio y los casos de uso estén suficientemente consolidados.
- Definir reportes y cálculos derivados de movimientos.
- Ampliar las reglas de saldos y consistencia financiera.

## Regla

No convertir un pendiente en trabajo realizado hasta que exista implementación verificable y tests correspondientes.
