# SOFP — Pendientes

Este documento contiene únicamente trabajo pendiente o por decidir.

## Estado actual

Las siguientes etapas están implementadas, validadas e integradas en `main` mediante fast-forward:

- `feature/operacion-financiera`;
- `feature/identificacion-activo`;
- `feature/cartera-activos`;
- `feature/costo-promedio-activo`;
- `feature/valorizacion-posicion-activo`;
- `feature/reportes-cartera`;
- `feature/seguridad-perfil-financiero`.

La última feature cerrada fue `feature/seguridad-perfil-financiero`.

## Validación global vigente

Suite completa ejecutada el **29/08/2026 20:00:23 -03:00**:

- **486/486 tests en verde**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración: **15:50 min**.

## Seguridad de PerfilFinanciero

La autorización de las operaciones que modifican el perfil está implementada en `PerfilFinancieroService` y cubierta por **19/19 tests en verde**.

La feature está integrada en `main`; no constituye un pendiente.

## Pendientes funcionales

No existe actualmente una feature funcional pendiente de integración.

El próximo trabajo debe definirse a partir del código real de `main`, sin asumir que las propuestas de esta sección son funcionalidades aprobadas.

## Líneas de evolución posibles

Sujetas a revisión del código y decisión explícita antes de implementar:

- completar progresivamente la capa `service` según necesidades reales del dominio;
- evolucionar la valorización desde un precio informado hacia una fuente de precios cuando exista un caso de uso concreto;
- definir reglas específicas de cada instrumento financiero antes de agregar atributos financieros adicionales;
- incorporar DTOs cuando los casos de uso y fronteras de aplicación lo requieran;
- ampliar reportes y cálculos derivados de movimientos;
- incorporar interfaz de usuario cuando dominio y casos de uso estén consolidados.

## Próximo paso

Revisar el estado real de `main`, identificar una necesidad funcional concreta y seleccionar la siguiente evolución mínima. La nueva funcionalidad deberá desarrollarse en una rama propia y no sobre `main` directamente.

## Regla

No convertir una línea de evolución en trabajo realizado hasta que exista implementación verificable y tests correspondientes.
