# SOFP — Pendientes

Este documento contiene únicamente trabajo pendiente o por decidir.

## Estado actual

La etapa `feature/operacion-financiera` fue integrada en `main` y quedó validada con la suite completa.

### OperacionFinanciera

Implementado y validado:
- transferencia;
- compra de activos;
- venta de activos;
- movimiento monetario asociado;
- movimiento de activo asociado;
- persistencia y recuperación;
- relaciones entre operación, movimiento monetario y movimiento de activo.

### PosicionActivo

Implementado y validado:
- cálculo de posición a partir de `MovimientoActivo`;
- compra como variación positiva;
- venta como variación negativa;
- rechazo de posición negativa;
- integración mediante `OperacionFinancieraService` y `PosicionActivoService`.

## Validación global

Build 059 quedó validado con la suite completa:

- **433/433 tests en verde**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- ejecución informada desde IntelliJ el **27/08/2026 15:24:11 -03:00**;
- duración: **17:35 min**.

## Pendientes de arquitectura / evolución

- Definir reglas específicas de cada instrumento financiero antes de agregar atributos a sus entidades.
- Definir la evolución específica de `Bono` a partir de reglas financieras explícitas.
- Completar progresivamente la capa `service` según necesidades reales del dominio.
- Incorporar DTOs cuando los casos de uso y fronteras de aplicación lo requieran.
- Incorporar interfaz de usuario cuando dominio y casos de uso estén consolidados.
- Definir reportes y cálculos derivados de movimientos.
- Evaluar nuevas reglas de saldos y consistencia financiera cuando aparezcan casos de uso que las requieran.

## Próximo paso

Revisar el dominio actual y decidir la siguiente funcionalidad de inversiones antes de crear una nueva rama `feature/...`.

## Regla

No convertir un pendiente en trabajo realizado hasta que exista implementación verificable y tests correspondientes.
