# SOFP — Pendientes

Este documento contiene únicamente trabajo pendiente o por decidir.

## Estado actual

Las etapas `feature/operacion-financiera` y `feature/identificacion-activo` fueron integradas en `main` y quedaron validadas con la suite completa.

Actualmente se encuentra en desarrollo `feature/cartera-activos`.

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

Esta etapa está cerrada y fue integrada en `main` mediante `0a554fb`.

### Cartera de activos

Implementado y validado en `feature/cartera-activos`:
- listado de movimientos de activos por perfil financiero;
- obtención de posiciones agrupadas por activo;
- cálculo mediante `CalculadorPosicionActivo`;
- exclusión de posiciones con cantidad final cero;
- separación de movimientos entre perfiles financieros;
- inclusión correcta de compras y ventas al listar movimientos por perfil;
- cobertura específica en `CarteraActivoServiceTest` con **5/5 tests en verde**;
- suite general con **446/446 tests en verde**.

## Validación global

La suite general más reciente fue ejecutada desde IntelliJ IDEA el **28/08/2026 17:47:36 -03:00**:

- **446/446 tests en verde**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración: **12:34 min**.

## Próximo cambio

Antes del merge de `feature/cartera-activos` a `main` se debe:
- revisar los commits de la feature;
- comparar la feature con `main`;
- verificar tests específicos y suite completa;
- revisar `git diff`, `git diff --check` y `git status`;
- confirmar que la documentación de cierre esté actualizada.

No hacer merge a `main` automáticamente.

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
