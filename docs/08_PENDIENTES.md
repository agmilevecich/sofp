# SOFP — Pendientes

Este documento contiene únicamente trabajo pendiente o por decidir.

Las etapas `feature/operacion-financiera`, `feature/identificacion-activo`, `feature/cartera-activos`, `feature/costo-promedio-activo` y `feature/valorizacion-posicion-activo` fueron integradas en `main` y quedaron validadas.

## Validación global

La suite general más reciente fue ejecutada desde IntelliJ IDEA el **28/08/2026 19:56:00 -03:00**:

- **455/455 tests en verde**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración: **11:24 min**.

## Próximo cambio

No existe actualmente una feature funcional pendiente de integración.

El próximo paso debe definirse a partir del mapa real de `main`, revisando código, entidades, repositorios, servicios, tests y reglas de negocio para seleccionar la siguiente evolución funcional mínima.

Antes de implementar una nueva feature:
- revisar la implementación actual;
- identificar las clases relacionadas;
- revisar los tests existentes;
- verificar las reglas de negocio ya establecidas;
- crear una rama de trabajo desde `main` sincronizado;
- implementar el cambio mínimo y agregar cobertura específica.

No hacer cambios directamente sobre `main` durante el desarrollo de una nueva feature.

## Estado de las etapas cerradas

- operaciones financieras: integrada y validada;
- identificación de activos por símbolo: integrada y validada;
- cartera de activos: integrada y validada;
- costo promedio de posición activa: integrado y validado;
- valorización de posición activa: integrada y validada.

## Pendientes de arquitectura / evolución

Luego del cierre de estas etapas se deberá definir la siguiente evolución funcional a partir del código y los casos de uso existentes.

La parte gráfica se considera una etapa posterior: primero se continuará consolidando el backend y sus reglas de negocio para que la UI se apoye sobre servicios ya estabilizados.

Posibles líneas de evolución, sujetas a revisión del código antes de decidir:
- completar progresivamente la capa `service` según necesidades reales del dominio;
- evolucionar la valorización desde un precio informado hacia una fuente de precios cuando exista un caso de uso concreto;
- definir reglas específicas de cada instrumento financiero antes de agregar atributos financieros adicionales;
- incorporar DTOs cuando los casos de uso y fronteras de aplicación lo requieran;
- definir reportes y cálculos derivados de movimientos;
- incorporar interfaz de usuario cuando dominio y casos de uso estén consolidados.

## Regla

No convertir un pendiente en trabajo realizado hasta que exista implementación verificable y tests correspondientes.
