# SOFP — Pendientes

Este documento contiene únicamente trabajo pendiente o por decidir. Debe mantenerse actualizado para que una nueva sesión pueda retomar el proyecto rápidamente.

## Pendientes inmediatos

- Definir el siguiente bloque funcional del dominio para el Build 025.
- Continuar ampliando la capa `service` y los casos de uso según el diseño acordado.
- Mantener sincronizada la documentación de continuidad con el estado real de `main`.

## Trabajo recientemente completado

- Build 011: aislamiento y estabilización de tests JPA con H2.
- Build 012: repositorios JPA de `Usuario`, `PerfilFinanciero`, `InstitucionFinanciera` y `Moneda`, con sus tests correspondientes en verde.
- Build 013: `CuentaRepository` y `CuentaRepositoryTest`, con la batería general de tests en verde.
- Build 014: `MovimientoRepository` y `MovimientoRepositoryTest`, con la batería general de **64 tests en verde**.
- Build 015: `CuentaService` y `CuentaServiceTest`, con la batería general de **68 tests en verde**.
- Build 016: `MovimientoService` y `MovimientoServiceTest`, con la batería general de **74 tests en verde**.
- Build 017: `CategoriaRepository` y `CategoriaRepositoryTest`, con los tests correspondientes en verde.
- Build 018: `CategoriaService` y `CategoriaServiceTest`, con la batería general de **82 tests en verde**.
- Build 019: `PerfilFinancieroService` y `PerfilFinancieroServiceTest`, con la batería general de **88 tests en verde**.
- Build 020: `UsuarioService` y `UsuarioServiceTest`, con la batería general de **93 tests en verde**.
- Build 021: `InstitucionFinancieraService` y `InstitucionFinancieraServiceTest`, con la batería general de **101 tests en verde**.
- Build 022: `MonedaService` y `MonedaServiceTest`, con la batería general de **109 tests en verde**.
- Build 023: ampliación de `CuentaService` y `CuentaServiceTest`, con la batería general de **113 tests en verde**.
- Build 024: ampliación de `MovimientoService` y `MovimientoServiceTest`, con la batería general de **118 tests en verde**.

## Pendientes de arquitectura / evolución

- Completar progresivamente la capa `service` según las necesidades reales del dominio.
- Incorporar DTOs cuando los casos de uso y las fronteras de la aplicación lo requieran.
- Incorporar la interfaz de usuario cuando el dominio y los casos de uso estén suficientemente consolidados.
- Definir reportes y cálculos derivados de movimientos.
- Ampliar las reglas de saldos y consistencia financiera.

## Regla

No convertir un pendiente en trabajo realizado hasta que exista implementación verificable y tests correspondientes.
