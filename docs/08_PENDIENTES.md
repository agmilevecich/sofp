# SOFP — Pendientes

Este documento contiene únicamente trabajo pendiente o por decidir. Debe mantenerse actualizado para que una nueva sesión pueda retomar el proyecto rápidamente.

## Pendientes inmediatos

- Definir el siguiente bloque funcional del dominio.
- Continuar ampliando persistencia y repositorios según el diseño acordado.
- Mantener sincronizada la documentación de continuidad con el estado real de `main`.

## Trabajo recientemente completado

- Build 011: aislamiento y estabilización de tests JPA con H2.
- Build 012: repositorios JPA de `Usuario`, `PerfilFinanciero`, `InstitucionFinanciera` y `Moneda`, con sus tests correspondientes en verde.
- Build 013: `CuentaRepository` y `CuentaRepositoryTest`, con la batería general de tests en verde.


## Pendientes de arquitectura / evolución

- Completar progresivamente repository/service/dto según las necesidades reales del dominio.
- Incorporar la interfaz de usuario cuando el dominio y los casos de uso estén suficientemente consolidados.
- Definir reportes y cálculos derivados de movimientos.
- Diseñar reglas para saldos y consistencia financiera.

## Regla

No convertir un pendiente en trabajo realizado hasta que exista implementación verificable y tests correspondientes.
