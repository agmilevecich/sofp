# SOFP — Estado actual

> Documento de continuidad del proyecto. Debe actualizarse al cerrar cada bloque importante de trabajo.

## Identidad del proyecto

**Nombre:** SOFP — Sistema Operativo Financiero Personal  
**Repositorio:** agmilevecich/sofp  
**Rama principal:** `main`  
**Rama de trabajo de la funcionalidad actual:** `feature/operacion-financiera`  
**Rama de documentación:** `docs/continuidad-sofp`

## Objetivo

Construir una aplicación Java de finanzas personales, preparada para múltiples usuarios, con persistencia JPA/Hibernate, base H2 y una interfaz que se incorporará progresivamente.

## Stack conocido

- Java / JDK 23.0.1
- IntelliJ IDEA Community 2026.1.1
- Maven
- JPA / Jakarta Persistence
- Hibernate 6.6.18.Final
- H2 2.3.232
- JUnit 5.11.4
- BigDecimal para importes monetarios

## Estado funcional actual

**Build 047 — Completar cobertura de `OperacionFinancieraService` está cerrado y validado.**

Posteriormente se completó el siguiente bloque funcional sobre `OperacionFinanciera`, incorporando su repositorio JPA e integrándolo en el servicio.

Actualmente `OperacionFinancieraService` permite materializar una transferencia como:

- una `OperacionFinanciera`;
- un `EGRESO` en la cuenta origen;
- un `INGRESO` en la cuenta destino.

La operación y los dos movimientos se persisten dentro de una única transacción, con rollback ante excepciones. La entidad `OperacionFinanciera` se persiste ahora mediante `OperacionFinancieraRepository`.

El servicio valida además cuentas activas, coherencia entre cuenta y categoría, moneda común y parámetros obligatorios. La descripción es obligatoria y su ausencia es rechazada con `NullPointerException`.

## Tests

`OperacionFinancieraServiceTest` quedó en **20/20 tests en verde**.

`OperacionFinancieraRepositoryTest` quedó en **10/10 tests en verde**.

`OperacionFinancieraTest` mantiene **7/7 tests en verde**.

La suite general del proyecto fue ejecutada mediante Maven y quedó en **319/319 tests en verde**, con `Failures: 0`, `Errors: 0`, `Skipped: 0` y `BUILD SUCCESS`.

La ejecución finalizó el **23/08/2026 a las 12:57:51 -03:00**, con una duración total de **09:07 min**.

## Último commit de código

En la rama `feature/operacion-financiera`:

- `3d0543c` — `feat: implementar repositorio de operacion financiera`

Posteriormente se sincronizó la rama con GitHub y Bitbucket mediante el commit de documentación `d8afb4d`.

La rama `feature/operacion-financiera` quedó limpia y sincronizada con ambos remotos.

Los commits relevantes del bloque actual son:

- `a995937` — `feat: implementar servicio de operacion financiera`.
- `2e4b94f` — `fix: permitir descripcion nula en transferencia`.
- `62f2da3` — `fix: validar descripcion en transferencia`.
- `615161c` — `test: completar cobertura de OperacionFinancieraService`.
- `3d0543c` — `feat: implementar repositorio de operacion financiera`.

**Importante:** la funcionalidad continúa aislada de `main`. No fue incorporada a `main`.

## Dominio construido

Entidades principales:

- `Usuario`
- `PerfilFinanciero`
- `InstitucionFinanciera`
- `Moneda`
- `Cuenta`
- `Categoria`
- `Movimiento`
- `OperacionFinanciera`

Enumeraciones principales:

- `TipoInstitucionFinanciera`
- `TipoMoneda`
- `TipoCuenta`
- `TipoMovimiento`

## Persistencia

Repositorios JPA incorporados:

- `UsuarioRepository`
- `PerfilFinancieroRepository`
- `InstitucionFinancieraRepository`
- `MonedaRepository`
- `CuentaRepository`
- `MovimientoRepository`
- `CategoriaRepository`
- `OperacionFinancieraRepository`

`MovimientoRepository` proporciona guardar, actualizar, buscar por ID, listar todos, listar por cuenta, listar por categoría y eliminar movimientos.

`OperacionFinancieraRepository` proporciona:

- `guardar(...)` para altas y actualizaciones;
- `buscarPorId(...)` con `Optional`;
- `listarTodas()`;
- `listarPorCuentaOrigen(...)`;
- `listarPorCuentaDestino(...)`;
- validación de parámetros obligatorios mediante `NullPointerException`.

## Services

La capa `service` contiene:

- `CuentaService`
- `MovimientoService`
- `CategoriaService`
- `PerfilFinancieroService`
- `UsuarioService`
- `InstitucionFinancieraService`
- `MonedaService`
- `OperacionFinancieraService`

`MovimientoService` gestiona registro, búsqueda, listados, modificaciones y eliminación mediante transacciones explícitas. Desde Build 033 valida coherencia entre cuenta, categoría y perfil financiero y rechaza movimientos sobre cuentas desactivadas.

`OperacionFinancieraService` coordina la creación de la operación financiera y sus dos movimientos dentro de una única transacción y utiliza `OperacionFinancieraRepository` para persistir la operación.

## Tests confirmados

Conteo confirmado de tests de services:

- `CategoriaServiceTest`: **21**
- `CuentaServiceTest`: **47**
- `InstitucionFinancieraServiceTest`: **26**
- `MonedaServiceTest`: **17**
- `MovimientoServiceTest`: **50**
- `PerfilFinancieroServiceTest`: **13**
- `UsuarioServiceTest`: **15**
- `OperacionFinancieraServiceTest`: **20**

Total de tests de services registrados: **209**.

Tests de dominio destacados:

- `MovimientoTest`: **27**
- `OperacionFinancieraTest`: **7**

Tests de persistencia destacados:

- `OperacionFinancieraRepositoryTest`: **10**

Suite completa validada: **319/319 tests en verde**.

La infraestructura de pruebas utiliza `JpaTestManager`, H2 en memoria y aislamiento entre ejecuciones.

## Decisiones de dominio relevantes

Las transferencias no se modelan como un tercer `TipoMovimiento`. Conceptualmente una transferencia genera un **EGRESO en la cuenta origen** y un **INGRESO en la cuenta destino**, vinculados mediante una `OperacionFinanciera`.

`OperacionFinancieraService` materializa esta operación y coordina su persistencia transaccional. La persistencia de `OperacionFinanciera` se encapsula ahora en `OperacionFinancieraRepository`, siguiendo el patrón de repositorios JPA existente.

## Git

Estado de referencia:

- rama de funcionalidad: `feature/operacion-financiera`;
- último commit de código: `3d0543c` — `feat: implementar repositorio de operacion financiera`;
- último commit de sincronización de la rama: `d8afb4d`;
- rama de documentación: `docs/continuidad-sofp`;
- `main`: permanece sin incorporar el trabajo de `feature/operacion-financiera`.

La documentación de continuidad se mantiene separada de la rama feature, siguiendo el flujo acordado para el proyecto.

## Próximo paso

Definir y comenzar el siguiente bloque funcional en `feature/operacion-financiera`, manteniendo la documentación en `docs/continuidad-sofp` y sin incorporar todavía el trabajo a `main`.

Antes de implementar un nuevo componente, revisar el dominio, repositorios, servicios y tests relacionados y mantener el cambio mínimo necesario.

## Bloque posterior a Build 047 — Repositorio de OperacionFinanciera

**Fecha de cierre:** 23/08/2026  
**Estado:** COMPLETADO Y VALIDADO

Se incorporó `OperacionFinancieraRepository` y se agregó cobertura específica mediante `OperacionFinancieraRepositoryTest`.

También se modificó `OperacionFinancieraService` para recibir el repositorio por constructor y utilizarlo para persistir la `OperacionFinanciera` dentro de la transacción existente. Se actualizaron los tests del servicio para utilizar la nueva dependencia.

Resultado:

- `OperacionFinancieraRepositoryTest`: **10 tests en verde**.
- `OperacionFinancieraServiceTest`: **20 tests en verde**.
- Suite completa: **319 tests**.
- Failures: **0**.
- Errors: **0**.
- Skipped: **0**.
- **BUILD SUCCESS**.
- Tiempo: **09:07 min**.
- Finalización: **23/08/2026 12:57:51 -03:00**.

Commit principal del bloque:

- `3d0543c` — `feat: implementar repositorio de operacion financiera`.

La documentación de continuidad de este bloque debe quedar en `docs/continuidad-sofp`.

## Regla de continuidad

Cada bloque importante debe terminar con código funcionando, tests en verde, commit identificable cuando corresponda, actualización de documentación y registro del próximo paso.
