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

Se implementó **Build 046 — `OperacionFinancieraService`** en la rama `feature/operacion-financiera`.

El servicio permite materializar una transferencia como:

- una `OperacionFinanciera`;
- un `EGRESO` en la cuenta origen;
- un `INGRESO` en la cuenta destino.

La operación y los dos movimientos se persisten dentro de una única transacción, con rollback ante excepciones.

El servicio valida además cuentas activas, coherencia entre cuenta y categoría, moneda común y parámetros obligatorios.

## Tests

`OperacionFinancieraServiceTest` quedó en **20/20 tests en verde** en la validación local.

La cobertura incluye operación exitosa, generación de ambos movimientos, parámetros nulos, importes inválidos, cuentas inactivas, perfiles incompatibles, monedas diferentes y ausencia de persistencia ante operaciones rechazadas.

La suite general de referencia anterior era **289/289 tests en verde**. Al pasar el test del servicio de 11 a 20 casos, se esperan **298 tests** en la suite completa una vez incorporada la ampliación al commit de la rama feature.

## Último commit de código

En la rama `feature/operacion-financiera`:

- `a995937` — `feat: implementar servicio de operacion financiera`

Este commit contiene la implementación de `OperacionFinancieraService`.

Los tests ampliados hasta 20 casos todavía están en el trabajo local y deben incorporarse al siguiente commit de `feature/operacion-financiera`.

**Importante:** la funcionalidad continúa aislada de `main`. No se incorporó ningún cambio de Build 046 a `main`.

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

`MovimientoRepository` proporciona guardar, actualizar, buscar por ID, listar todos, listar por cuenta, listar por categoría y eliminar movimientos.

Todavía no existe `OperacionFinancieraRepository`.

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

`OperacionFinancieraService` coordina la creación de la operación financiera y sus dos movimientos dentro de una única transacción.

## Tests confirmados

Conteo confirmado de tests de services:

- `CategoriaServiceTest`: **21**
- `CuentaServiceTest`: **47**
- `InstitucionFinancieraServiceTest`: **26**
- `MonedaServiceTest`: **17**
- `MovimientoServiceTest`: **50**
- `PerfilFinancieroServiceTest`: **13**
- `UsuarioServiceTest`: **15**
- `OperacionFinancieraServiceTest`: **20** localmente

Total de tests de services registrados: **209**.

Tests de dominio destacados:

- `MovimientoTest`: **27**
- `OperacionFinancieraTest`: **7**

La suite completa de **298 tests** queda pendiente de ejecución después de incorporar los 9 tests nuevos del servicio al commit de la rama feature.

La infraestructura de pruebas utiliza `JpaTestManager`, H2 en memoria y aislamiento entre ejecuciones.

## Decisiones de dominio relevantes

Las transferencias no se modelan como un tercer `TipoMovimiento`. Conceptualmente una transferencia genera un **EGRESO en la cuenta origen** y un **INGRESO en la cuenta destino**, vinculados mediante una `OperacionFinanciera`.

`OperacionFinancieraService` ya materializa esta operación y coordina su persistencia transaccional.

## Git

Estado de referencia:

- rama de funcionalidad: `feature/operacion-financiera`;
- commit de producción de Build 046: `a995937` — `feat: implementar servicio de operacion financiera`;
- rama de documentación: `docs/continuidad-sofp`;
- `main`: permanece en `028aaee` y no fue modificado por Build 046.

La documentación de continuidad se mantiene separada de la rama feature, siguiendo el flujo acordado para el proyecto.

## Próximo paso

Incorporar los tests ampliados de `OperacionFinancieraServiceTest` a `feature/operacion-financiera`, ejecutar la suite completa de **298 tests**, verificar `git diff`, `git diff --check` y `git status`, y recién entonces cerrar definitivamente Build 046.

No implementar todavía `OperacionFinancieraRepository` hasta confirmar si la persistencia de la operación requiere un repositorio independiente.

## Regla de continuidad

Cada bloque importante debe terminar con código funcionando, tests en verde, commit identificable cuando corresponda, actualización de documentación y registro del próximo paso.
