# SOFP — Pendientes

Este documento contiene únicamente trabajo pendiente o por decidir.

## Pendientes inmediatos

- Revisar `Movimiento`, `MovimientoService`, `MovimientoRepository` y sus tests antes de implementar la materialización de transferencias.
- Definir dónde debe residir la coordinación transaccional de una transferencia.
- Definir cómo se vincularán los dos `Movimiento` resultantes con `OperacionFinanciera`.
- Determinar si corresponde incorporar `OperacionFinancieraRepository` y `OperacionFinancieraService` después de confirmar las reglas anteriores.
- Mantener sincronizada la documentación de continuidad de `docs/continuidad-sofp` con los remotos.

## Trabajo recientemente completado

- Build 029: eliminación en `CategoriaRepository`.
- Build 030: eliminación en `CategoriaService`.
- Build 031: eliminación en `CuentaRepository`.
- Build 032: eliminación en `CuentaService`.
- Build 033: reglas de negocio de `Movimiento`.
- Build 034: ampliación de cobertura de `MovimientoServiceTest`.
- Build 035: ampliación de cobertura de `CuentaServiceTest`.
- Build 036: ampliación de cobertura de `InstitucionFinancieraServiceTest`.
- Build 037: ampliación de cobertura de `MonedaServiceTest`.
- Build 038: ampliación de cobertura de `PerfilFinancieroServiceTest`.
- Build 039: ampliación de cobertura de `UsuarioServiceTest` y endurecimiento del contrato de `UsuarioService`.
- Build 040: ampliación de cobertura de `CategoriaServiceTest`.
- Build 041: reforzamiento de validaciones de servicios y dominio y ampliación de cobertura de `InstitucionFinancieraServiceTest`.
- Build 042: ampliación de cobertura de `CuentaServiceTest`.
- Build 043: ampliación de cobertura de `MovimientoServiceTest`.
- Build 044: ampliación de `MovimientoTest`, con 23 tests nuevos, 27/27 tests específicos en verde y suite general 282/282 en verde.
- Build 045: implementación del dominio `OperacionFinanciera`, con 7 tests específicos en verde y suite general 289/289 en verde.

## Build 045 — Cerrado

Se incorporó `OperacionFinanciera` como entidad de dominio para representar una transferencia entre una cuenta origen y una cuenta destino.

Reglas implementadas:

- cuenta origen obligatoria;
- cuenta destino obligatoria;
- importe obligatorio y positivo;
- cuenta origen y destino no pueden ser la misma.

Se incorporó `OperacionFinancieraTest` con **7/7 tests en verde**.

Commit de código: `1f650dc` — `feat: implementar dominio de operacion financiera`.

El commit está en la rama `feature/operacion-financiera` y la rama quedó con `working tree clean`.

Suite general:

- Tests run: **289**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`

La ejecución finalizó el **20/08/2026 a las 21:46:51 -03:00** y duró **08:47 min**.

**Build 045 queda cerrado y validado.**

## Estado de Git de referencia

- Rama de funcionalidad: `feature/operacion-financiera`.
- Último commit de código de esa rama: `1f650dc` — `feat: implementar dominio de operacion financiera`.
- Último commit de código previamente confirmado en `main`: `dca3b80` — `test: corregir datos compartidos de Movimiento`.
- La documentación de continuidad se mantiene en `docs/continuidad-sofp`.

## Pendientes de arquitectura / evolución

- Materializar transferencias como un `EGRESO` en origen y un `INGRESO` en destino.
- Vincular ambos movimientos con `OperacionFinanciera`.
- Definir y probar la coordinación transaccional de la operación.
- Incorporar repositorio y servicio de `OperacionFinanciera` solamente cuando las reglas y responsabilidades estén confirmadas.
- Completar progresivamente la capa `service` según las necesidades reales del dominio.
- Incorporar DTOs cuando los casos de uso y las fronteras de la aplicación lo requieran.
- Incorporar la interfaz de usuario cuando el dominio y los casos de uso estén suficientemente consolidados.
- Definir reportes y cálculos derivados de movimientos.
- Ampliar las reglas de saldos y consistencia financiera.

## Regla

No convertir un pendiente en trabajo realizado hasta que exista implementación verificable y tests correspondientes.
