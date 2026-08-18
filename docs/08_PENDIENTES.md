# SOFP — Pendientes

Este documento contiene únicamente trabajo pendiente o por decidir.

## Pendientes inmediatos

- Sincronizar la documentación de continuidad de `docs/continuidad-sofp` con los remotos.
- Publicar el commit de código `4d9dc2a` de `main` en los remotos.
- Revisar los casos de uso pendientes del dominio y definir el siguiente Build.
- No implementar código nuevo hasta definir objetivo y tests del siguiente bloque.

## Trabajo recientemente completado

- Build 029: eliminación en `CategoriaRepository`.
- Build 030: eliminación en `CategoriaService`.
- Build 031: eliminación en `CuentaRepository`.
- Build 032: eliminación en `CuentaService`.
- Build 033: reglas de negocio de `Movimiento`.
- Build 034: ampliación de cobertura de `MovimientoServiceTest`.

## Build 034

Se agregaron 17 tests nuevos en `MovimientoServiceTest`, alcanzando **32/32 tests en verde**.

La batería general quedó en **163/163 tests en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.

Commit de código: `4d9dc2a` — `test: ampliar cobertura de MovimientoService`.

## Pendientes de arquitectura / evolución

- Completar progresivamente la capa `service` según las necesidades reales del dominio.
- Incorporar DTOs cuando los casos de uso y las fronteras de la aplicación lo requieran.
- Incorporar la interfaz de usuario cuando el dominio y los casos de uso estén suficientemente consolidados.
- Definir reportes y cálculos derivados de movimientos.
- Ampliar las reglas de saldos y consistencia financiera.

## Estado de Git de referencia

- Código actual de `main`: `4d9dc2a` — `test: ampliar cobertura de MovimientoService`.
- La documentación de continuidad se mantiene en `docs/continuidad-sofp`.
- La documentación de Build 034 fue registrada en `docs/01-builds/Build-034.md`.

## Regla

No convertir un pendiente en trabajo realizado hasta que exista implementación verificable y tests correspondientes.
