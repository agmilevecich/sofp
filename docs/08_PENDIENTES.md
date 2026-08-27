# SOFP — Pendientes

Este documento contiene únicamente trabajo pendiente o por decidir.

## Pendientes inmediatos

- Ejecutar la suite general completa después de los cambios recientes de venta y posición.
- Revisar el estado final de `feature/operacion-financiera` contra `main` después de la suite general.
- Definir la siguiente evolución del bloque de inversiones a partir de reglas de negocio explícitas.
- Definir los atributos y comportamientos financieros específicos de `Bono` antes de incorporarlos al dominio.
- Mantener código, tests y documentación de continuidad dentro de `feature/operacion-financiera`.

## Trabajo recientemente completado

- Build 044: ampliación de `MovimientoTest`, con 27/27 tests específicos y suite general 282/282 en verde.
- Build 045: implementación del dominio `OperacionFinanciera`, con 7 tests específicos y suite general 289/289 en verde.
- Build 046: implementación de `OperacionFinancieraService`, con 20 tests específicos y suite general 300/300 en verde.
- Build 047: finalización de cobertura de `OperacionFinancieraServiceTest` y suite general 309/309 en verde.
- Build 048: implementación de `OperacionFinancieraRepository`, integración con `OperacionFinancieraService`, 10 tests de repositorio y suite general 319/319 en verde.
- Build 049: asociación persistente de `Movimiento` con `OperacionFinanciera`, 7 tests nuevos, `OperacionFinancieraTest` en 14/14 y suite general 326/326 en verde.
- Build 050: ampliación de `OperacionFinancieraServiceTest` con 2 tests nuevos, pasando a 22/22 y suite general 328/328 en verde.
- Validación individual posterior al Build 050: `OperacionFinancieraRepositoryTest` con 10/10 tests en verde desde IntelliJ, sin cambios de producción.
- Build 051: incorporación de `Activo`, `ActivoRepository`, `ActivoTest` con 8/8 y `ActivoRepositoryTest` con 6/6 tests en verde.
- Build 051: suite general confirmada en 342/342 tests en verde.
- Build 052: incorporación de `Bono` como especialización mínima de `Activo`, `BonoTest` con 5/5 y `BonoRepositoryTest` con 6/6 tests en verde.
- Build 052: suite general confirmada en 353/353 tests en verde.
- Build 058: compra de activo implementada y validada; `OperacionFinancieraCompraServiceTest` 13/13 y suite general 419/419 en verde.
- Validación posterior: `OperacionFinancieraTest`, `OperacionFinancieraServiceTest`, `OperacionFinancieraCompraServiceTest` y `OperacionFinancieraVentaServiceTest` quedaron en verde en conjunto; `PosicionActivoServiceTest` 4/4 en verde.
- Se amplió `OperacionFinancieraVentaServiceTest` para verificar las relaciones persistidas de `Movimiento` y `MovimientoActivo`.
- Se agregó integración en `PosicionActivoServiceTest` para validar compra de 100 + venta de 30 = posición 70 mediante los servicios reales.

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

## Tests recientes

- `OperacionFinancieraTest`: 17/17 verdes.
- `OperacionFinancieraServiceTest`: 22/22 verdes.
- `OperacionFinancieraCompraServiceTest`: 13/13 verdes.
- `OperacionFinancieraVentaServiceTest`: 13/13 verdes.
- `PosicionActivoServiceTest`: 4/4 verdes.

No se ha ejecutado todavía una nueva suite general completa después de los cambios recientes.

## Estado Git de referencia

- Rama de trabajo: `feature/operacion-financiera`.
- Último cambio de código: integración de compra/venta con posición.
- Últimos cambios de documentación: actualización de continuidad posterior a las validaciones.
- `main` permanece sin modificar.

## Pendientes de arquitectura / evolución

- Ejecutar suite general completa después de los cambios recientes.
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
