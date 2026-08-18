# SOFP — Pendientes

Este documento contiene únicamente trabajo pendiente o por decidir.

## Pendientes inmediatos

- Sincronizar la documentación de continuidad de `docs/continuidad-sofp` con los remotos.
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

## Build 035

Se agregaron 20 tests nuevos en `CuentaServiceTest`, alcanzando **40/40 tests en verde**.

La batería general quedó en **186/186 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.

Commit de código: `57b8ad5` — `test: ampliar cobertura de CuentaService`.

El commit fue publicado en `main` de GitHub y Bitbucket.

## Pendientes de arquitectura / evolución

- Completar progresivamente la capa `service` según las necesidades reales del dominio.
- Incorporar DTOs cuando los casos de uso y las fronteras de la aplicación lo requieran.
- Incorporar la interfaz de usuario cuando el dominio y los casos de uso estén suficientemente consolidados.
- Definir reportes y cálculos derivados de movimientos.
- Ampliar las reglas de saldos y consistencia financiera.

## Estado de Git de referencia

- Código actual de `main`: `57b8ad5` — `test: ampliar cobertura de CuentaService`.
- La documentación de continuidad se mantiene en `docs/continuidad-sofp`.
- La documentación de Build 035 fue registrada en `docs/01-builds/Build-035.md`.

## Regla

No convertir un pendiente en trabajo realizado hasta que exista implementación verificable y tests correspondientes.
