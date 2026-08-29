# SOFP — Pendientes

Este documento contiene únicamente trabajo pendiente o por decidir.

Las etapas `feature/operacion-financiera`, `feature/identificacion-activo`, `feature/cartera-activos`, `feature/costo-promedio-activo` y `feature/valorizacion-posicion-activo` fueron integradas en `main` y quedaron validadas.

La rama `feature/reportes-cartera` contiene actualmente el bloque funcional de reportes de cartera y evolución histórica de saldos, validado mediante la suite global.

## Validación global

La suite general más reciente fue ejecutada desde IntelliJ IDEA el **29/08/2026 13:29:56 -03:00**:

- **480/480 tests en verde**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración: **12:23 min**.

## Estado de la feature actual

`feature/reportes-cartera` está funcionalmente implementada y validada. El bloque incluye:

- reporte de cartera de activos;
- composición valorizada de cartera;
- detalle de movimientos de cartera;
- evolución histórica del saldo de una cuenta;
- cobertura específica y validación de suite general.

No se considera todavía integrada en `main`.

## Próximo paso

Completar la validación final de `feature/reportes-cartera` antes del merge:

- revisar los commits de la rama;
- comparar la rama contra `main`;
- revisar el diff completo;
- verificar `git diff --check`;
- verificar `git status`;
- confirmar que `roadmap.md`, `06_BUILDS.md`, `07_TESTS.md` y este documento sean coherentes con el estado actual;
- preparar el merge a `main`, sin realizarlo automáticamente.

No hacer cambios directamente sobre `main` durante esta etapa.

## Pendientes de arquitectura / evolución

Luego del cierre de `feature/reportes-cartera` se deberá definir la siguiente evolución funcional a partir del código y los casos de uso existentes.

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
