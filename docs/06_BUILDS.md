# SOFP — Historial de Builds

## Builds 001–010

- Build 001: configuración inicial del proyecto.
- Build 002: persistencia y base de datos.
- Build 003: primeras entidades y consolidación del dominio.
- Build 004: evolución del modelo y pruebas.
- Build 005: diseño de `Cuenta`.
- Builds posteriores: consolidación de dominio, arquitectura y validaciones.
- Build 009.1: implementación de `Categoria`.
- Build 010: implementación de `Movimiento`, `TipoMovimiento`, relaciones y validaciones.

## Builds 011–020

- Build 011: aislamiento y estabilización de tests JPA con H2.
- Build 012: repositorios JPA de entidades base.
- Build 013: repositorio JPA de `Cuenta`.
- Build 014: repositorio JPA de `Movimiento` — 64/64 tests.
- Build 015: servicio de saldo de cuentas — 68/68 tests.
- Build 016: servicio de movimientos — 74/74 tests.
- Build 017: repositorio JPA de `Categoria`.
- Build 018: `CategoriaService` — 82/82 tests.
- Build 019: `PerfilFinancieroService` — 88/88 tests.
- Build 020: `UsuarioService` — 93/93 tests.

## Builds 021–030

- Build 021: `InstitucionFinancieraService` — 101/101 tests.
- Build 022: `MonedaService` — 109/109 tests.
- Build 023: ampliación de `CuentaService` — 113/113 tests.
- Build 024: ampliación de `MovimientoService` — 118/118 tests.
- Build 025: ampliación de `Movimiento` y `MovimientoService` — 121/121 tests.
- Build 026: eliminación de `Movimiento` — 121/121 tests.
- Build 027: ampliación de `CuentaService` — 128/128 tests.
- Cobertura posterior al Build 027: 129/129 tests.
- Build 028: ampliación de `CategoriaService` — 135/135 tests.
- Build 029: eliminación en `CategoriaRepository` — 136/136 tests.
- Build 030: eliminación en `CategoriaService` — 138/138 tests.

## Builds 031–040

- Build 031: eliminación en `CuentaRepository` — 139/139 tests.
- Build 032: eliminación en `CuentaService` — 141/141 tests.
- Build 033: reglas de negocio de `Movimiento` — 144/144 tests.
- Build 034: ampliación de cobertura de `MovimientoService` — 163/163 tests.
- Build 035: ampliación de cobertura de `CuentaService` — 186/186 tests.
- Build 036: ampliación de cobertura de `InstitucionFinancieraService` — 201/201 tests.
- Build 037: ampliación de cobertura de `MonedaService` — 210/210 tests.
- Build 038: ampliación de cobertura de `PerfilFinancieroService` — 217/217 tests.
- Build 039: ampliación de cobertura de `UsuarioService` — 227/227 tests.
- Build 040: ampliación de cobertura de `CategoriaService` — 236/236 tests.

## Builds 041–044

- Build 041: reforzamiento de validaciones de servicios y dominio — 239/239 tests.
- Build 042: ampliación de cobertura de `CuentaService` — 246/246 tests.
- Build 043: ampliación de cobertura de `MovimientoService` — 259/259 tests.
- Build 044: ampliación de `MovimientoTest`, pasando a 27/27 tests y suite general 282/282 en verde.

## Build 045 — Implementación del dominio de OperacionFinanciera

Se incorporó la entidad de dominio `OperacionFinanciera` como representación de una transferencia entre dos cuentas.

La entidad contiene:

- `cuentaOrigen` obligatoria;
- `cuentaDestino` obligatoria;
- `importe` positivo obligatorio;
- regla que impide utilizar la misma cuenta como origen y destino;
- mapeo JPA mediante `operaciones_financieras`.

`OperacionFinancieraTest`: **7/7 tests en verde**.

Suite general: **289/289 tests en verde**.

Commit: `1f650dc` — `feat: implementar dominio de operacion financiera`.

## Build 046 — Implementación de OperacionFinancieraService

Se incorporó `OperacionFinancieraService` para materializar una transferencia mediante una `OperacionFinanciera`, un `EGRESO` en la cuenta origen y un `INGRESO` en la cuenta destino dentro de una única transacción.

Se validan cuentas activas, coherencia de perfiles, moneda común y parámetros obligatorios, incluida la descripción.

`OperacionFinancieraServiceTest`: **20/20 tests en verde**.

Suite general: **300/300 tests en verde**.

Commits principales: `a995937`, `2e4b94f` y `62f2da3`.

## Build 047 — Completar cobertura de OperacionFinancieraService

Se completaron las validaciones de cuentas inactivas, categorías de otro perfil, monedas diferentes, fecha/hora nula, descripción nula y ausencia de persistencia ante operaciones rechazadas.

`OperacionFinancieraServiceTest`: **20/20 tests en verde**.

Suite general: **309/309 tests en verde**.

Finalización: **21/08/2026 20:36:10 -03:00**.

Commit: `615161c` — `test: completar cobertura de OperacionFinancieraService`.

## Build 048 — Implementación de OperacionFinancieraRepository

**Estado: COMPLETADO Y VALIDADO.**

Se incorporó `OperacionFinancieraRepository` con `guardar(...)`, `buscarPorId(...)`, `listarTodas()`, `listarPorCuentaOrigen(...)` y `listarPorCuentaDestino(...)`, además de las validaciones de parámetros obligatorios.

`OperacionFinancieraService` fue actualizado para recibir el repositorio por constructor y utilizarlo dentro de la transacción existente.

Suite general: **319/319 tests en verde**, con `Failures: 0`, `Errors: 0`, `Skipped: 0` y `BUILD SUCCESS`.

Duración: **09:07 min**. Finalización: **23/08/2026 12:57:51 -03:00**.

Commit: `3d0543c` — `feat: implementar repositorio de operacion financiera`.

## Build 049 — Asociación de Movimiento con OperacionFinanciera

**Estado: COMPLETADO Y VALIDADO.**

Se incorporó la relación persistente entre `Movimiento` y `OperacionFinanciera`, con asociación bidireccional y límite de dos movimientos por operación.

`OperacionFinancieraService` asocia el `EGRESO` y el `INGRESO` a la operación antes de persistirlos.

`OperacionFinancieraTest`: **14/14 tests en verde**.

Suite general: **326/326 tests en verde**.

Finalización: **23/08/2026 19:07:25 -03:00**. Duración: **12:30 min**.

Commit funcional: `0f64fa9` — `feat: asociar movimientos a operacion financiera`.

## Build 050 — Ampliación de cobertura de OperacionFinancieraService

**Estado: COMPLETADO Y VALIDADO.**

Se agregaron dos pruebas específicas al servicio:

- `deberiaAsociarAmbosMovimientosALaMismaOperacionFinanciera`: verifica que los dos movimientos generados por una transferencia quedan asociados a la misma operación financiera persistida.
- `deberiaRechazarMismaCuentaComoOrigenYDestino`: verifica la regla de dominio y que una transferencia rechazada no persiste movimientos.

`OperacionFinancieraServiceTest` pasó de **20/20 a 22/22 tests en verde**.

Suite general:

- Tests run: **328**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`

Última ejecución confirmada desde IntelliJ: **25/08/2026 10:47:02 -03:00**. Duración: **12:04 min**.

Commit: `1f306e4` — `test: ampliar cobertura de OperacionFinancieraService`.

## Build 051 — Incorporación de Activo y persistencia JPA

**Estado: COMPLETADO Y VALIDADO.**

Se incorporó `Activo` como entidad base para el futuro bloque de inversiones.

Características actuales de `Activo`:

- hereda de `EntidadAuditable`;
- `nombre` obligatorio;
- `Moneda` obligatoria;
- constructor protegido para JPA;
- métodos de dominio para cambiar nombre y moneda;
- sin cantidad, precio, cotización ni posición, que se reservarán para etapas posteriores.

Se incorporó `ActivoRepository` para persistencia JPA.

Se incorporó `ActivoRepositoryTest` con **6/6 tests en verde**. La primera ejecución evidenció la necesidad de registrar `Activo` en la configuración de persistencia utilizada por los tests; corregida esa configuración, los seis tests quedaron en verde.

`ActivoTest`: **8/8 tests en verde**.

`ActivoRepositoryTest`: **6/6 tests en verde**.

Suite general:

- Tests run: **342**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`

Última ejecución general confirmada desde IntelliJ: **25/08/2026 16:30:22 -03:00**. Duración: **16:51 min**.

Commits:

- `0793126` — `feat: incorporar entidad Activo`
- `5270e31` — `test: agregar cobertura de Activo`
- `1624f8c` — `feat: incorporar repositorio de Activo`
- `70bdbf7` — `test: agregar cobertura de ActivoRepository`

## Build 052 — Incorporación de Bono como especialización de Activo

**Estado: COMPLETADO Y VALIDADO.**

Se incorporó `Bono` como primera especialización de `Activo`, manteniendo deliberadamente una definición mínima y sin atributos financieros adicionales.

Características actuales de `Bono`:

- entidad JPA que hereda de `Activo`;
- hereda `nombre` y `Moneda`;
- constructor protegido para JPA;
- constructor público con `nombre` y `Moneda`;
- no incorpora todavía valor nominal, tasa, vencimiento, cupón, amortización, emisor u otros datos específicos de bonos.

Se incorporó `BonoRepository` con persistencia JPA para guardar, actualizar, buscar por ID y listar bonos.

Se registró `Bono` explícitamente en la unidad de persistencia utilizada por los tests.

Pruebas específicas:

- `BonoTest`: **5/5 tests en verde**.
- `BonoRepositoryTest`: **6/6 tests en verde**.

Suite general:

- Tests run: **353**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`

Ejecución general: **25/08/2026 20:14:39 -03:00**. Duración: **16:07 min**.

Commits del bloque:

- `589acf1` — `feat: incorporar entidad Bono`
- `c11f1f4` — `test: agregar cobertura de Bono`
- `6925447` — `feat: incorporar repositorio de Bono`
- `74b8fff` — `test: agregar cobertura de BonoRepository`
- `678c6ea` — `test: registrar Bono en persistencia JPA`

## Estado actual

El último Build cerrado es **Build 052**. La última suite confirmada es **353/353 tests en verde**.

La documentación de continuidad se mantiene exclusivamente en `feature/operacion-financiera`.

## Próximo paso

Definir la siguiente evolución del bloque de inversiones a partir de reglas de negocio explícitas, manteniendo `Bono` como especialización mínima hasta que se definan sus atributos y comportamientos financieros.

## Regla de cierre

Cada Build debe quedar registrado con cambios principales, tests ejecutados, resultado, commit asociado cuando exista y próximo paso. La documentación debe mantenerse sincronizada con el código real y permanecer en la misma rama de trabajo.
