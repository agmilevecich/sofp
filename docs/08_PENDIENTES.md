# SOFP — Pendientes

Este documento contiene únicamente trabajo pendiente o por decidir.

## Pendientes inmediatos

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
- Build 051: suite general confirmada en **342/342 tests en verde**, `Failures: 0`, `Errors: 0`, `Skipped: 0`, `BUILD SUCCESS`.
- Build 052: incorporación de `Bono` como especialización mínima de `Activo`, `BonoTest` con 5/5 y `BonoRepositoryTest` con 6/6 tests en verde.
- Build 052: suite general confirmada en **353/353 tests en verde**, `Failures: 0`, `Errors: 0`, `Skipped: 0`, `BUILD SUCCESS`.
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

## Estado actual — Activo

`Activo` ya está implementado como entidad base para el futuro bloque de inversiones.

Incluye actualmente:

- herencia de `EntidadAuditable`;
- `nombre` obligatorio;
- `Moneda` obligatoria;
- métodos de dominio para cambiar nombre y moneda;
- persistencia JPA mediante `ActivoRepository`.

Cobertura:

- `ActivoTest`: **8/8 tests en verde**.
- `ActivoRepositoryTest`: **6/6 tests en verde**.

`Activo` se mantiene deliberadamente como una entidad mínima. Cantidad, precio, cotización y posición no forman parte todavía de esta entidad.

## Estado actual — Bono

`Bono` ya está implementado como primera especialización de `Activo`.

Incluye actualmente:

- herencia de `Activo`;
- identidad de tipo `Bono`;
- herencia de `nombre` y `Moneda`;
- persistencia JPA mediante `BonoRepository`;
- sin atributos financieros específicos adicionales.

Cobertura:

- `BonoTest`: **5/5 tests en verde**.
- `BonoRepositoryTest`: **6/6 tests en verde**.

La entidad se mantiene deliberadamente mínima. No se incorporarán valor nominal, tasa, vencimiento, cupón, amortización, emisor u otros atributos financieros hasta definir explícitamente las reglas de dominio.

Suite completa: **353/353 tests en verde**, con `Failures: 0`, `Errors: 0`, `Skipped: 0` y `BUILD SUCCESS`.

La última ejecución general confirmada finalizó el **25/08/2026 20:14:39 -03:00**, con una duración de **16:07 min**.

## Estado Git de referencia

- Rama única de trabajo y continuidad: `feature/operacion-financiera`.
- `0793126`: `feat: incorporar entidad Activo`.
- `5270e31`: `test: agregar cobertura de Activo`.
- `1624f8c`: `feat: incorporar repositorio de Activo`.
- `70bdbf7`: `test: agregar cobertura de ActivoRepository`.
- `589acf1`: `feat: incorporar entidad Bono`.
- `c11f1f4`: `test: agregar cobertura de Bono`.
- `6925447`: `feat: incorporar repositorio de Bono`.
- `74b8fff`: `test: agregar cobertura de BonoRepository`.
- `678c6ea`: `test: registrar Bono en persistencia JPA`.
- GitHub y Bitbucket están sincronizados.
- La documentación de continuidad se mantiene en la misma rama.
- `main` permanece separado y no debe modificarse hasta que el bloque funcional esté considerado estable.
- Las ramas auxiliares creadas accidentalmente durante la implementación de `Bono` fueron eliminadas de los remotos.
- `docs/continuidad-sofp`: **eliminada**. No debe volver a utilizarse para documentación de continuidad.

## Pendientes de arquitectura / evolución

- Definir las reglas específicas de cada instrumento financiero antes de agregar atributos a sus entidades.
- Definir la evolución específica de `Bono` a partir de reglas financieras explícitas.
- Completar progresivamente la capa `service` según las necesidades reales del dominio.
- Incorporar DTOs cuando los casos de uso y las fronteras de la aplicación lo requieran.
- Incorporar la interfaz de usuario cuando el dominio y los casos de uso estén suficientemente consolidados.
- Definir reportes y cálculos derivados de movimientos.
- Ampliar las reglas de saldos y consistencia financiera cuando aparezcan nuevos casos de uso que lo requieran.
- Evaluar posteriormente el modelado específico de posiciones de activos y sus movimientos cuando comience el bloque de inversiones.
- Confirmar la estrategia definitiva de coordinación transaccional de la operación si aparecen nuevos casos de uso que la requieran.

## Regla

No convertir un pendiente en trabajo realizado hasta que exista implementación verificable y tests correspondientes.
