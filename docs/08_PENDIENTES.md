# SOFP — Pendientes

Este documento contiene únicamente trabajo pendiente o por decidir.

## Pendientes inmediatos

- Revisar el estado final de `feature/operacion-financiera` contra `main`.
- Revisar commits y diferencia funcional antes de preparar el merge.
- Definir la siguiente evolución del bloque de inversiones a partir de reglas de negocio explícitas.
- Definir los atributos y comportamientos financieros específicos de `Bono` antes de incorporarlos al dominio.
- Mantener código, tests y documentación de continuidad dentro de `feature/operacion-financiera`.

## Estado actual — OperacionFinanciera

La funcionalidad de transferencia, compra y venta de activos está implementada en `feature/operacion-financiera`.

Compra incluye:
- operación `COMPRA`;
- movimiento monetario `EGRESO`;
- `MovimientoActivo.COMPRA`;
- cálculo de importe `cantidad × precioUnitario`;
- validaciones de parámetros y valores positivos;
- cuenta de origen activa;
- categoría perteneciente al perfil;
- persistencia y recuperación.

Venta incluye:
- operación `VENTA`;
- movimiento monetario `INGRESO`;
- `MovimientoActivo.VENTA`;
- cálculo de importe `cantidad × precioUnitario`;
- validaciones equivalentes a compra;
- persistencia y recuperación;
- verificación de las relaciones entre operación, movimiento monetario y movimiento de activo.

## Estado actual — PosicionActivo

La posición de un activo se calcula a partir de `MovimientoActivo` ordenados por id.

Se validó:
- compra de 100 → posición 100;
- compra de 100 + venta de 30 → posición 70;
- posición cero sin movimientos;
- rechazo de posición negativa;
- rechazo de movimiento de otro activo;
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

## Trabajo recientemente completado

- Implementación y validación de compra de activo.
- Implementación y validación de venta de activo.
- Corrección de la regla de dominio `VENTA → INGRESO`.
- Verificación de relaciones persistidas entre `OperacionFinanciera`, `Movimiento` y `MovimientoActivo`.
- Integración real de compra + venta con `PosicionActivoService`, validando 100 - 30 = 70.
- Suite general posterior a estos cambios: **433/433** en verde.

## Estado Git de referencia

- Rama de trabajo: `feature/operacion-financiera`.
- `main` permanece sin modificar.
- Los dos commits que `main` tiene y la feature no corresponden a un archivo temporal creado y eliminado; no contienen cambios funcionales que deban incorporarse a la feature.
- La documentación de continuidad se mantiene en la rama de trabajo.

## Pendientes de arquitectura / evolución

- Revisar commits y diferencia final contra `main`.
- Definir reglas específicas de cada instrumento financiero antes de agregar atributos a sus entidades.
- Definir evolución específica de `Bono` a partir de reglas financieras explícitas.
- Completar progresivamente la capa `service` según necesidades reales del dominio.
- Incorporar DTOs cuando los casos de uso y fronteras de aplicación lo requieran.
- Incorporar interfaz de usuario cuando dominio y casos de uso estén consolidados.
- Definir reportes y cálculos derivados de movimientos.
- Evaluar nuevas reglas de saldos y consistencia financiera cuando aparezcan casos de uso que las requieran.

## Regla

No convertir un pendiente en trabajo realizado hasta que exista implementación verificable y tests correspondientes.
