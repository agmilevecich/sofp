# SOFP — Pendientes

Este documento contiene únicamente trabajo pendiente o por decidir.

## Pendientes inmediatos

- Definir el siguiente bloque funcional de `feature/operacion-financiera`.
- Mantener código, tests y documentación de continuidad dentro de `feature/operacion-financiera`.

## Trabajo recientemente completado

- Build 044: ampliación de `MovimientoTest`, con 27/27 tests específicos y suite general 282/282 en verde.
- Build 045: implementación del dominio `OperacionFinanciera`, con 7 tests específicos y suite general 289/289 en verde.
- Build 046: implementación de `OperacionFinancieraService`, con 20 tests específicos y suite general 300/300 en verde.
- Build 047: finalización de cobertura de `OperacionFinancieraServiceTest` y suite general 309/309 en verde.
- Build 048: implementación de `OperacionFinancieraRepository`, integración con `OperacionFinancieraService`, 10 tests de repositorio y suite general 319/319 en verde.
- Build 049: asociación persistente de `Movimiento` con `OperacionFinanciera`, 7 tests nuevos, `OperacionFinancieraTest` en 14/14 y suite general 326/326 en verde.
- Build 050: ampliación de `OperacionFinancieraServiceTest` con 2 tests nuevos, pasando a 22/22 y suite general 328/328 en verde.
- Cálculo de saldo de `Cuenta`: ya implementado en `CuentaService.calcularSaldo(...)` y cubierto por los tests existentes. No constituye un bloque pendiente de implementación.

## Estado actual — OperacionFinanciera

La funcionalidad de transferencia mediante `OperacionFinanciera` está implementada y validada.

Incluye:

- `Movimiento.operacionFinanciera` con `@ManyToOne` y columna `operacion_financiera_id`.
- `OperacionFinanciera.movimientos` con `@OneToMany(mappedBy = "operacionFinanciera")`.
- Colección expuesta como lista no modificable.
- Máximo de dos movimientos por operación.
- Rechazo de movimientos nulos.
- Rechazo de movimientos repetidos.
- Rechazo de movimientos ya asociados a otra operación financiera.
- Asociación automática del movimiento al agregarlo a la operación.
- `OperacionFinancieraService` asociando el `EGRESO` y el `INGRESO` antes de persistirlos.
- Rechazo de la misma cuenta como origen y destino mediante la regla de dominio.

Cobertura específica:

- `OperacionFinancieraServiceTest`: **22/22 tests en verde**.
- `OperacionFinancieraTest`: **14/14 tests en verde**.
- `OperacionFinancieraRepositoryTest`: **10/10 tests en verde**.

Suite completa: **328/328 tests en verde**, con `Failures: 0`, `Errors: 0`, `Skipped: 0` y `BUILD SUCCESS`.

La última ejecución confirmada desde IntelliJ finalizó el **25/08/2026 10:47:02 -03:00**, con una duración de **12:04 min**.

Por lo tanto, la funcionalidad de `OperacionFinanciera`, su repositorio, servicio y asociación con `Movimiento` ya no son pendientes de implementación.

## Estado Git de referencia

- Rama única de trabajo y continuidad: `feature/operacion-financiera`.
- Último commit de código: `0f64fa9` — `feat: asociar movimientos a operacion financiera`.
- Último commit de trabajo: `1f306e4` — `test: ampliar cobertura de OperacionFinancieraService`.
- GitHub y Bitbucket están sincronizados.
- La documentación de continuidad se mantiene en la misma rama.
- `main` permanece separado y no debe modificarse hasta que el bloque funcional esté considerado estable.
- `docs/continuidad-sofp`: **eliminada**. No debe volver a utilizarse para documentación de continuidad.

## Pendientes de arquitectura / evolución

- Definir el siguiente bloque funcional de `feature/operacion-financiera`.
- Completar progresivamente la capa `service` según las necesidades reales del dominio.
- Incorporar DTOs cuando los casos de uso y las fronteras de la aplicación lo requieran.
- Incorporar la interfaz de usuario cuando el dominio y los casos de uso estén suficientemente consolidados.
- Definir reportes y cálculos derivados de movimientos.
- Ampliar las reglas de saldos y consistencia financiera cuando aparezcan nuevos casos de uso que lo requieran.
- Evaluar posteriormente el modelado específico de posiciones de activos y sus movimientos cuando comience el bloque de inversiones.
- Confirmar la estrategia definitiva de coordinación transaccional de la operación si aparecen nuevos casos de uso que la requieran.

## Regla

No convertir un pendiente en trabajo realizado hasta que exista implementación verificable y tests correspondientes.
