# SOFP — Pendientes

Este documento contiene únicamente trabajo pendiente o por decidir.

Las etapas `feature/operacion-financiera`, `feature/identificacion-activo`, `feature/cartera-activos`, `feature/costo-promedio-activo`, `feature/valorizacion-posicion-activo` y `feature/reportes-cartera` fueron integradas en `main` y quedaron validadas.

## Validación global vigente

La suite general más reciente fue ejecutada desde IntelliJ IDEA el **29/08/2026 13:29:56 -03:00**:

- **480/480 tests en verde**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración: **12:23 min**.

## Estado de la feature cerrada

`feature/reportes-cartera` está funcionalmente implementada, validada e integrada en `main` mediante fast-forward.

El bloque incluye:

- reporte de cartera de activos;
- composición valorizada de cartera;
- detalle de movimientos de cartera;
- evolución histórica del saldo de una cuenta.

El estado final de `main` y `feature/reportes-cartera` coincide en el commit `0b73e87`.

## Próximo paso

No existe actualmente una feature funcional pendiente de integración.

El próximo trabajo debe definirse a partir del mapa real de `main`, revisando código, entidades, repositorios, servicios, tests y reglas de negocio para seleccionar la siguiente evolución funcional mínima.

Antes de implementar una nueva feature:

- revisar la implementación actual;
- identificar las clases relacionadas;
- revisar los tests existentes;
- verificar las reglas de negocio ya establecidas;
- crear una rama de trabajo desde `main` sincronizado;
- implementar el cambio mínimo y agregar cobertura específica.

No hacer cambios directamente sobre `main` durante el desarrollo de una nueva feature.

## Pendientes de arquitectura / evolución

La siguiente evolución funcional queda abierta y debe decidirse a partir del código y los casos de uso existentes.

La parte gráfica se considera una etapa posterior: primero se continuará consolidando el backend y sus reglas de negocio para que la UI se apoye sobre servicios ya estabilizados.

Posibles líneas de evolución, sujetas a revisión del código antes de decidir:

- completar progresivamente la capa `service` según necesidades reales del dominio;
- evolucionar la valorización desde un precio informado hacia una fuente de precios cuando exista un caso de uso concreto;
- definir reglas específicas de cada instrumento financiero antes de agregar atributos financieros adicionales;
- incorporar DTOs cuando los casos de uso y fronteras de aplicación lo requieran;
- ampliar reportes y cálculos derivados de movimientos;
- incorporar interfaz de usuario cuando dominio y casos de uso estén consolidados.

## Regla

No convertir un pendiente en trabajo realizado hasta que exista implementación verificable y tests correspondientes.
