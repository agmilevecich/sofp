# SOFP — Pendientes

Este documento contiene únicamente trabajo pendiente o por decidir. Debe mantenerse actualizado para que una nueva sesión pueda retomar el proyecto rápidamente.

## Pendientes inmediatos

- Evaluar la incorporación de la operación de eliminación en `CategoriaService` siguiendo el patrón de `MovimientoService`.
- Definir las validaciones y pruebas correspondientes para la eliminación desde la capa de servicio.
- Mantener sincronizada la documentación de continuidad con el estado real de `main`.

## Trabajo recientemente completado

- Build 011: aislamiento y estabilización de tests JPA con H2.
- Build 012: repositorios JPA de entidades base.
- Build 013: `CuentaRepository` y `CuentaRepositoryTest`.
- Build 014: `MovimientoRepository` y `MovimientoRepositoryTest`, con la batería general en 64 tests en verde.
- Build 015: `CuentaService` y `CuentaServiceTest`, con la batería general en 68 tests en verde.
- Build 016: `MovimientoService` y `MovimientoServiceTest`, con la batería general en 74 tests en verde.
- Build 017: `CategoriaRepository` y `CategoriaRepositoryTest`.
- Build 018: `CategoriaService` y `CategoriaServiceTest`, con la batería general en 82 tests en verde.
- Build 019: `PerfilFinancieroService` y `PerfilFinancieroServiceTest`, con la batería general en 88 tests en verde.
- Build 020: `UsuarioService` y `UsuarioServiceTest`, con la batería general en 93 tests en verde.
- Build 021: `InstitucionFinancieraService` y `InstitucionFinancieraServiceTest`, con la batería general en 101 tests en verde.
- Build 022: `MonedaService` y `MonedaServiceTest`, con la batería general en 109 tests en verde.
- Build 023: ampliación de `CuentaService` y `CuentaServiceTest`, con la batería general en 113 tests en verde.
- Build 024: ampliación inicial de `MovimientoService` y `MovimientoServiceTest`, con la batería general en 118 tests en verde.
- Build 025: ampliación de `Movimiento` y finalización de nuevas operaciones de `MovimientoService`, con la batería general en 121 tests en verde.
- Build 026: incorporación de eliminación de movimientos en `MovimientoRepository` y `MovimientoService`, con la batería general en 121 tests en verde al cerrar el Build.
- Build 027: ampliación de `CuentaService` con operaciones de modificación y activación/desactivación, con la batería general en 128 tests en verde al cerrar el Build.
- Cobertura posterior al Build 027: test específico de eliminación en `MovimientoServiceTest`, elevando la batería general a **129 tests en verde**.
- Build 028: ampliación de `CategoriaService` y `CategoriaServiceTest`, elevando la batería general a **135 tests en verde**.
- Build 029: incorporación de eliminación en `CategoriaRepository` y su test, elevando la batería general a **136 tests en verde**.

## Pendientes de arquitectura / evolución

- Completar progresivamente la capa `service` según las necesidades reales del dominio.
- Incorporar DTOs cuando los casos de uso y las fronteras de la aplicación lo requieran.
- Incorporar la interfaz de usuario cuando el dominio y los casos de uso estén suficientemente consolidados.
- Definir reportes y cálculos derivados de movimientos.
- Ampliar las reglas de saldos y consistencia financiera.

## Estado de Git de referencia

- Build 029: `46ad669` — `feat: completar eliminacion de CategoriaRepository`.
- El commit de código fue publicado en `main` de GitHub y Bitbucket.
- La documentación de continuidad se está actualizando en `docs/continuidad-sofp`.

## Regla

No convertir un pendiente en trabajo realizado hasta que exista implementación verificable y tests correspondientes.
