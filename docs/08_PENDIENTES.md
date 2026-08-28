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
- cobertura de persistencia que verifica el rechazo de símbolos duplicados para `Activo` y `Bono`;
- adaptación de los tests y casos existentes al nuevo constructor identificable.

## Validación global

La suite general más reciente fue ejecutada desde IntelliJ IDEA el **28/08/2026 12:15:12 -03:00**:

- **441/441 tests en verde**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración: **10:01 min**.

Además, `ActivoRepositoryTest` y `BonoRepositoryTest` fueron ejecutados específicamente y resultaron en **18/18 tests en verde**.

## Próximo cambio

- Revisar si aparece una necesidad real de búsqueda o identificación adicional de instrumentos.
- Realizar la revisión final de la rama contra `main` antes de decidir el merge.

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
