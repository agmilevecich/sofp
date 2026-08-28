# SOFP — Pendientes

Este documento contiene únicamente trabajo pendiente o por decidir.

## Estado actual

La etapa `feature/operacion-financiera` fue integrada en `main` y quedó validada con la suite completa.

La rama de trabajo actual es `feature/identificacion-activo`.

### Identificación de activos

Implementado y validado:
- símbolo obligatorio en `Activo`;
- símbolo heredado por `Bono`;
- símbolo único en persistencia;
- `ActivoRepository.buscarPorSimbolo(String)`;
- `BonoRepository.buscarPorSimbolo(String)`;
- cobertura de tests para ambas búsquedas;
- adaptación de los tests y casos existentes al nuevo constructor identificable.

## Validación global

La suite general más reciente fue ejecutada el **27/08/2026 19:52:48 -03:00**:

- **435/435 tests en verde**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración: **19:08 min**.

Posteriormente se ejecutaron las pruebas específicas de los cambios de búsqueda por símbolo y fueron informadas como verdes.

## Próximo cambio

- Agregar cobertura específica para la regla de unicidad del símbolo en persistencia, verificando primero que la restricción existente sea la que debe protegerse mediante el test.
- Revisar después si aparece una necesidad real de búsqueda o identificación adicional de instrumentos.

## Pendientes de arquitectura / evolución

- Definir reglas específicas de cada instrumento financiero antes de agregar atributos financieros adicionales.
- Definir la evolución específica de `Bono` a partir de reglas financieras explícitas.
- Completar progresivamente la capa `service` según necesidades reales del dominio.
- Incorporar DTOs cuando los casos de uso y fronteras de aplicación lo requieran.
- Incorporar interfaz de usuario cuando dominio y casos de uso estén consolidados.
- Definir reportes y cálculos derivados de movimientos.
- Evaluar nuevas reglas de saldos y consistencia financiera cuando aparezcan casos de uso que las requieran.

## Regla

No convertir un pendiente en trabajo realizado hasta que exista implementación verificable y tests correspondientes.
