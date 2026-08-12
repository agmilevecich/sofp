# SOFP — Estado de Tests

## Criterio

Los tests son parte del cierre de cada Build. Una funcionalidad se considera verificada cuando sus tests correspondientes quedan en verde y no se introducen regresiones conocidas.

## Tests / áreas conocidas

- `UsuarioTest`
- `MonedaTest`
- `CuentaTest`
- `CategoriaTest`
- `MovimientoTest`
- `JpaManagerTest`
- `UsuarioJpaTest`
- `CategoriaJpaTest`
- `CuentaJpaTest`
- `MovimientoJpaTest`
- `UsuarioRepositoryTest`
- `PerfilFinancieroRepositoryTest`
- `InstitucionFinancieraRepositoryTest`
- `MonedaRepositoryTest`
- `CuentaRepositoryTest`
- `MovimientoRepositoryTest`
- `CuentaServiceTest`
- `MovimientoServiceTest`

## Build 011

Build 011 incorporó la estabilización de la infraestructura de pruebas JPA:

- `JpaTestManager` separado de `JpaManager`.
- H2 de pruebas en memoria.
- `create-drop` para el esquema de pruebas.
- Aislamiento de los datos utilizados por los tests JPA.
- Cierre de `EntityManager` y `JpaTestManager` al finalizar las pruebas correspondientes.
- Corrección del conflicto de unicidad del email de `Usuario` al ejecutar la batería completa.

Resultado: todos los tests de la batería general terminaron en verde.

## Build 012

Se incorporaron y verificaron los tests de los nuevos repositorios JPA:

- `UsuarioRepositoryTest`
- `PerfilFinancieroRepositoryTest`
- `InstitucionFinancieraRepositoryTest`
- `MonedaRepositoryTest`

Los cuatro tests de repositorio terminaron en verde.

La infraestructura de pruebas continúa aislada mediante H2 en memoria y `JpaTestManager`.

## Build 013

Se incorporó y verificó:

- `CuentaRepositoryTest`

El test verifica:

- Guardado y búsqueda de una cuenta por ID.
- Listado de cuentas.
- Listado de cuentas por perfil financiero.
- Actualización de una cuenta existente.

Resultado: `CuentaRepositoryTest` terminó completamente en verde.

También se ejecutó la batería general de tests del proyecto y todos los tests terminaron en verde.

La infraestructura de pruebas continúa aislada mediante H2 en memoria y `JpaTestManager`.

## Build 014

Se incorporó y verificó:

- `MovimientoRepositoryTest`

El test verifica:

- Guardado y actualización de movimientos.
- Búsqueda por ID.
- Listado general.
- Listado por cuenta.
- Listado por categoría.

Resultado: `MovimientoRepositoryTest` terminó en verde.

La batería general del proyecto quedó en **64/64 tests en verde**.

## Build 015

Se incorporó y verificó:

- `CuentaServiceTest`

El test verifica el cálculo del saldo a partir de los movimientos de una cuenta:

1. Cuenta sin movimientos → `0.00`.
2. Un ingreso de `10,000.00` → `10,000.00`.
3. Un egreso de `3,000.00` → `-3,000.00`.
4. Múltiples movimientos (`10,000 + 5,000 - 3,000`) → `12,000.00`.

Durante las pruebas se detectó que el `EntityManagerFactory` de tests podía reutilizar la misma base H2 en memoria entre pruebas. Se resolvió cerrando `JpaTestManager` en el `tearDown()` de `CuentaServiceTest`, manteniendo el aislamiento de cada test.

Resultado: los 4 tests de `CuentaServiceTest` terminaron en verde.

Además, se ejecutó la batería general del proyecto y los **68/68 tests terminaron en verde**.

## Build 016

Se incorporó y verificó:

- `MovimientoServiceTest`

El test verifica:

1. Registro de un `INGRESO`.
2. Registro de un `EGRESO`.
3. Listado de movimientos por cuenta.
4. Listado de movimientos por categoría.
5. Listado de todos los movimientos.
6. Búsqueda de un movimiento por ID.

Durante la implementación se detectaron y resolvieron tres incidencias principales:

- `TransientPropertyValueException` al persistir una `Cuenta` cuyo `PerfilFinanciero` todavía no estaba persistido.
- Violación de unicidad de `Moneda.codigo` por reutilización de la infraestructura de H2 entre pruebas.
- `TransactionRequiredException` durante `flush()` al no existir una transacción activa en el servicio.

La solución final utiliza `JpaTestManager.close()` en el `tearDown()` para mantener el aislamiento de la base de pruebas y `MovimientoService` controla explícitamente `begin`, `flush`, `commit` y `rollback` durante el registro.

Resultado: los 6 tests de `MovimientoServiceTest` terminaron en verde.

Además, se ejecutó la batería general del proyecto y los **74/74 tests terminaron en verde**.

## Resultado actual

El Build 016 está cerrado y verificado. La infraestructura de pruebas JPA continúa funcionando con H2 en memoria y `JpaTestManager`.

## Regla de actualización

Cuando un test sea agregado, corregido o ejecutado como parte de un Build, registrar aquí:

- nombre del test;
- objetivo;
- resultado;
- cualquier incidencia relevante;
- fecha/Build.

No registrar como verde un test que no haya sido realmente ejecutado con éxito.
