# SOFP — Historial de Builds

## Builds 001–010

- **Build 001:** configuración inicial del proyecto.
- **Build 002:** configuración inicial de persistencia y base de datos.
- **Build 003:** primeras entidades y consolidación del dominio.
- **Build 004:** evolución del modelo de dominio y pruebas.
- **Build 005:** diseño de `Cuenta`.
- **Builds posteriores:** consolidación de dominio, arquitectura, validaciones e instituciones financieras.
- **Build 009.1:** implementación de `Categoria`.
- **Build 010:** implementación de `Movimiento`, `TipoMovimiento`, relaciones, validación de importe positivo y tests unitarios/JPA.

## Build 011 — Aislamiento y estabilización de tests JPA con H2

Se incorporó `JpaTestManager`, la unidad `sofp-persistence-unit-test`, H2 en memoria y `create-drop`, separando la persistencia de producción de la infraestructura de pruebas.

## Build 012 — Repositorios JPA de entidades base

Se incorporaron `UsuarioRepository`, `PerfilFinancieroRepository`, `InstitucionFinancieraRepository` y `MonedaRepository`, junto con sus tests.

## Build 013 — Repository JPA de Cuenta

Se incorporó `CuentaRepository` y sus pruebas para guardar/buscar, listar, listar por perfil y actualizar.

## Build 014 — Repository JPA de Movimiento

Se incorporó `MovimientoRepository` y `MovimientoRepositoryTest` para guardar, actualizar, buscar, listar todos, listar por cuenta y listar por categoría.

Resultado: **64/64 tests en verde**.

Commit: `4f0b20f` — `Build 014 - Implementación de MovimientoRepository`.

## Build 015 — Servicio de saldo de cuentas

Se incorporó `CuentaService` y `CuentaServiceTest` para calcular saldo: `INGRESO` suma, `EGRESO` resta y una cuenta sin movimientos devuelve `BigDecimal.ZERO`.

Resultado: **68/68 tests en verde**.

Commit: `4697815` — `feat: implementar servicio de saldo de cuentas`.

## Build 016 — Servicio de movimientos

Se incorporó `MovimientoService` y `MovimientoServiceTest` para registrar, buscar y listar movimientos mediante transacciones explícitas.

Resultado: **74/74 tests en verde**.

Commit: `8f8594e` — `feat: implementar servicio de movimientos`.

## Build 017 — Repository JPA de Categoria

Se incorporó `CategoriaRepository` y `CategoriaRepositoryTest`.

Commit: `f462b3b` — `feat: implementar CategoriaRepository`.

## Build 018 — Servicio de Categoria

Se incorporó `CategoriaService` y sus tests para registrar, buscar, listar y listar por perfil.

Resultado: **82/82 tests en verde**.

Commit: `d57e0b4` — `feat: implementar CategoriaService`.

## Build 019 — Servicio de PerfilFinanciero

Se incorporó `PerfilFinancieroService` y sus tests para guardar, buscar, listar por usuario, cambiar descripción y activar/desactivar.

Resultado: **88/88 tests en verde**.

Commit: `1cc00ca` — `feat: implementar PerfilFinancieroService`.

## Build 020 — Servicio de Usuario

Se incorporó `UsuarioService` y sus tests para guardar, buscar, listar, activar y desactivar usuarios.

Resultado: **93/93 tests en verde**.

Commit: `87786fe` — `feat: implementar UsuarioService`.

## Build 021 — Servicio de InstitucionFinanciera

Se incorporó `InstitucionFinancieraService` y sus tests para búsqueda, listado, modificaciones y activación/desactivación.

Resultado: **101/101 tests en verde**.

Commit: `20e21c3` — `feat: implementar InstitucionFinancieraService`.

## Build 022 — Servicio de Moneda

Se incorporó `MonedaService` y sus tests. Se corrigió el aislamiento de H2 mediante `JpaTestManager.close()`.

Resultado: **109/109 tests en verde**.

Commit: `0d0db87` — `feat: implementar MonedaService`.

## Build 023 — Ampliación de CuentaService

Se amplió `CuentaService` y `CuentaServiceTest` para registrar, buscar, listar, listar por perfil y verificar saldo.

Resultado: **113/113 tests en verde**.

Commit: `ea595d4` — `feat: ampliar CuentaService`.

## Build 024 — Ampliación de MovimientoService

Se ampliaron las operaciones de modificación de descripción, observaciones y categoría y las validaciones de IDs/existencia.

Resultado: **118/118 tests en verde**.

Commit: `110f7d7` — `feat: ampliar MovimientoService`.

## Build 025 — Ampliación de Movimiento y MovimientoService

Se incorporaron modificaciones de tipo, importe y fecha/hora en `Movimiento` y `MovimientoService`.

Resultado: **121/121 tests en verde**.

Commits: `da3b89d` y `81883ea`.

## Build 026 — Eliminación de Movimiento

Se incorporó eliminación en `MovimientoRepository` y `MovimientoService`, con validación de ID, existencia y transacción explícita.

Resultado: **121/121 tests en verde**.

Commit: `d386d02` — `feat: completar operaciones de Movimiento`.

## Build 027 — Ampliación de CuentaService

Se incorporaron modificaciones de cuenta y activación/desactivación, junto con `obtenerCuenta(...)` y transacciones explícitas.

Resultado: **128/128 tests en verde**.

Commit: `00d862c` — `feat: ampliar CuentaService`.

### Cobertura posterior al Build 027

Se agregó un test específico para eliminación de `MovimientoService`.

Resultado: **129/129 tests en verde**.

Commit: `3e93be2` — `test: cubrir eliminacion de MovimientoService`.

## Build 028 — Ampliación de CategoriaService

Se completaron operaciones de modificación y activación/desactivación de categorías.

Resultado: **135/135 tests en verde**.

Commit: `b5c200e` — `feat: ampliar CategoriaService`.

## Build 029 — Eliminación en CategoriaRepository

Se incorporó `CategoriaRepository.eliminar(...)` y su test específico.

Resultado: **136/136 tests en verde**.

Commit: `46ad669` — `feat: completar eliminacion de CategoriaRepository`.

## Build 030 — Eliminación en CategoriaService

Se incorporó `CategoriaService.eliminar(...)`, con validación, existencia y transacción explícita, más dos tests específicos.

Resultado: **138/138 tests en verde**.

Commit: `59b9628` — `feat: completar eliminacion de CategoriaService`.

## Build 031 — Eliminación en CuentaRepository

Se incorporó `CuentaRepository.eliminar(...)` y su test específico.

Resultado: **139/139 tests en verde**.

Commit: `40768d3` — `feat: completar eliminacion de CuentaRepository`.

## Build 032 — Eliminación en CuentaService

Se incorporó `CuentaService.eliminar(...)`, con validación, existencia y transacción explícita, más tests para eliminación existente e inexistente.

Resultado: **141/141 tests en verde**.

Commit: `a1a817d` — `feat: completar eliminacion de CuentaService`.

## Build 033 — Reglas de negocio de Movimiento

Se incorporaron en `MovimientoService` reglas de coherencia entre cuenta, categoría y perfil financiero y se rechazaron movimientos sobre cuentas desactivadas.

Resultado: **144/144 tests en verde**.

Commit: `b18ca96` — `feat: agregar reglas de negocio a movimientos`.

## Build 034 — Ampliación de cobertura de MovimientoService

Se ampliaron los tests de `MovimientoServiceTest` y se agregaron **17 tests nuevos**.

`MovimientoServiceTest`: **32/32 tests en verde**.

Batería general: **163/163 tests en verde**.

Commit: `4d9dc2a` — `test: ampliar cobertura de MovimientoService`.

## Build 035 — Ampliación de cobertura de CuentaService

Se ampliaron los tests de `CuentaServiceTest` y se agregaron **20 tests nuevos**.

`CuentaServiceTest`: **40/40 tests en verde**.

Batería general: **186/186 tests en verde**.

Commit: `57b8ad5` — `test: ampliar cobertura de CuentaService`.

## Build 036 — Ampliación de cobertura de InstitucionFinancieraService

Se ampliaron los tests de `InstitucionFinancieraServiceTest` con **15 tests nuevos**.

`InstitucionFinancieraServiceTest`: **23/23 tests en verde**.

Batería general: **201/201 tests en verde**.

Commit: `bd7f4bd` — `test: ampliar cobertura de InstitucionFinancieraService`.

## Build 037 — Ampliación de cobertura de MonedaService

Se amplió `MonedaServiceTest`, pasando de **8 a 17 tests en verde**.

Batería general: **210/210 tests en verde**.

## Build 038 — Ampliación de cobertura de PerfilFinancieroService

Se amplió `PerfilFinancieroServiceTest`, pasando de **6 a 13 tests en verde**.

Batería general: **217/217 tests en verde**.

Commit: `e2d3268` — `test: ampliar cobertura de PerfilFinancieroService`.

## Build 039 — Ampliación de cobertura de UsuarioService

Se amplió `UsuarioServiceTest`, pasando de **5 a 15 tests en verde**, con 10 tests nuevos y validaciones explícitas de IDs nulos en `UsuarioService.activar(...)` y `desactivar(...)`.

Batería general: **227/227 tests en verde**.

Commit: `0e27dfe` — `test: ampliar cobertura de UsuarioService`.

## Build 040 — Ampliación de cobertura de CategoriaService

Se amplió `CategoriaServiceTest`, pasando de **12 a 21 tests en verde** con 9 tests nuevos.

No se modificó código de producción.

Batería general: **236/236 tests en verde**.

Commit: `9be5972` — `test: ampliar cobertura de CategoriaService`.

## Build 041 — Reforzamiento de validaciones de servicios y dominio

Se reforzaron validaciones de parámetros nulos en `InstitucionFinanciera`, `MonedaService` y `PerfilFinancieroService`.

`InstitucionFinancieraServiceTest` pasó de **23 a 26 tests**, con 3 tests nuevos.

Batería general: **239/239 tests en verde**.

Resultado: `BUILD SUCCESS`, `Failures: 0`, `Errors: 0`, `Skipped: 0`.

Commit: `a9de29c` — `feat: reforzar validaciones de servicios y dominio`.

## Build 042 — Ampliación de cobertura de CuentaService

Se amplió `CuentaServiceTest` sin modificar código de producción.

`CuentaServiceTest` pasó de **40 a 47 tests en verde**.

La batería general pasó de **239 a 246 tests en verde**.

Resultado final:

- Tests run: **246**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`

La ejecución general terminó el **19/08/2026 a las 16:46:27 -03:00**.

`git diff --check` no reportó errores de whitespace antes del commit.

Commit: `526b378` — `test: ampliar cobertura de CuentaService`.

El commit fue publicado en `main` de GitHub y Bitbucket mediante `git pushall`.

## Build 043 — Ampliación de cobertura de MovimientoService

Se amplió `MovimientoServiceTest` sin modificar código de producción.

`MovimientoServiceTest` pasó de **37 a 50 tests en verde**.

La batería general pasó de **246 a 259 tests en verde**.

Durante la ejecución inicial se detectaron dos expectativas incorrectas en tests de registro: `Movimiento` valida `importe` y `descripcion` mediante `Validaciones`, devolviendo `IllegalArgumentException` para valores nulos. Los tests fueron ajustados al contrato real del dominio.

Resultado final:

- Tests run: **259**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`

La ejecución general terminó el **19/08/2026 a las 19:51:15 -03:00**.

`git diff --check` no reportó errores de whitespace antes del commit.

Commit: `b6384f0` — `test: ampliar cobertura de MovimientoService`.

El commit fue publicado en `main` de GitHub y Bitbucket mediante `git pushall`.

## Build 044 — Ampliación de cobertura de Movimiento — En curso

Se amplió `MovimientoTest` sin modificar código de producción.

Se incorporaron **23 tests nuevos**, pasando de **4 a 27 tests en verde** en la clase.

La cobertura agregada incluye validaciones del constructor y de las operaciones de modificación de `Movimiento`, incluyendo cuenta, categoría, tipo, importe, fecha/hora, descripción y observaciones.

Resultado específico confirmado:

- `MovimientoTest`: **27/27 tests en verde**
- Tests nuevos: **23**
- Failures: **0** en la ejecución específica

La suite general todavía no fue ejecutada después de este cambio, por lo que el Build 044 queda abierto hasta verificar la batería completa.

Commit de código: `6f53f79` — `test: ampliar cobertura de Movimiento`.

El commit fue publicado en `main` de GitHub y Bitbucket mediante `git pushall`.

## Regla de cierre

Cada Build debe quedar registrado con cambios principales, tests ejecutados, resultado, commit asociado cuando exista y próximo paso. Los cambios posteriores de cobertura también deben registrarse para mantener la documentación sincronizada con el estado real del proyecto.
