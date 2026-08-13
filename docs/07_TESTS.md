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
- `CategoriaRepositoryTest`
- `CuentaServiceTest`
- `MovimientoServiceTest`
- `CategoriaServiceTest`
- `PerfilFinancieroServiceTest`
- `UsuarioServiceTest`
- `InstitucionFinancieraServiceTest`
- `MonedaServiceTest`

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

## Build 017

Se incorporó y verificó:

- `CategoriaRepositoryTest`

El test cubre las operaciones principales del repositorio JPA de `Categoria` y fue ejecutado correctamente.

Resultado: todos los tests de `CategoriaRepositoryTest` terminaron en verde.

El bloque queda validado para cerrar el Build 017.

## Build 018

Se incorporó y verificó:

- `CategoriaServiceTest`

El test verifica:

1. Registro de una categoría.
2. Búsqueda de una categoría por ID.
3. Listado de todas las categorías.
4. Listado de categorías por perfil financiero.

Resultado: los 4 tests de `CategoriaServiceTest` terminaron en verde.

Además, se ejecutó la batería general del proyecto y los **82/82 tests terminaron en verde**.

No se registran incidencias pendientes para este bloque.

## Build 019

Se incorporó y verificó:

- `PerfilFinancieroServiceTest`

El test verifica:

1. Guardar y buscar un perfil por ID.
2. Listar todos los perfiles.
3. Listar perfiles por usuario.
4. Cambiar la descripción.
5. Desactivar un perfil.
6. Activar un perfil.

Resultado: los 6 tests de `PerfilFinancieroServiceTest` terminaron en verde.

Además, se ejecutó la batería general del proyecto y los **88/88 tests terminaron en verde**.

No se registran incidencias pendientes para este bloque.

## Build 020

Se incorporó y verificó:

- `UsuarioServiceTest`

El test verifica:

1. Guardar y buscar un usuario por ID.
2. Buscar un usuario por email.
3. Listar todos los usuarios.
4. Activar un usuario.
5. Desactivar un usuario.

Resultado: los 5 tests de `UsuarioServiceTest` terminaron en verde.

Además, se ejecutó la batería general del proyecto y los **93/93 tests terminaron en verde**.

No se registran incidencias pendientes para este bloque.

## Build 021

Se incorporó y verificó:

- `InstitucionFinancieraServiceTest`

El test verifica:

1. Guardar y buscar una institución financiera por ID.
2. Buscar una institución financiera por nombre.
3. Listar todas las instituciones financieras.
4. Renombrar una institución financiera.
5. Actualizar el sitio web.
6. Actualizar la descripción.
7. Activar una institución financiera.
8. Desactivar una institución financiera.

Resultado: los 8 tests de `InstitucionFinancieraServiceTest` terminaron en verde.

Durante la implementación se produjo inicialmente una diferencia en el test de listado de instituciones, debido a la presencia de registros persistidos previamente. El test fue corregido para aislar correctamente los datos de prueba.

Además, se ejecutó la batería general del proyecto y los **101/101 tests terminaron en verde**.

No se registran incidencias pendientes para este bloque.

## Build 022

Se incorporó y verificó:

- `MonedaServiceTest`

El test verifica:

1. Guardar y buscar una moneda por ID.
2. Buscar una moneda por código.
3. Listar todas las monedas.
4. Cambiar el nombre de una moneda.
5. Cambiar la cantidad de decimales.
6. Lanzar excepción al intentar modificar el nombre de una moneda inexistente.
7. Lanzar excepción al intentar modificar los decimales de una moneda inexistente.
8. Verificar el comportamiento inicial de una moneda creada.

Resultado: los 8 tests de `MonedaServiceTest` terminaron en verde.

Durante las pruebas se produjo inicialmente una violación de unicidad sobre `MONEDAS(CODIGO)` al intentar insertar nuevamente la moneda `ARS`. La causa fue la reutilización del mismo `EntityManagerFactory` y, por lo tanto, de la base H2 en memoria entre tests.

La solución consistió en cerrar `JpaTestManager` en el `tearDown()` de `MonedaServiceTest`, garantizando una nueva base H2 aislada para cada test.

Además, se ejecutó la batería general del proyecto y los **109/109 tests terminaron en verde**.

No se registran incidencias pendientes para este bloque.

## Build 023

Se incorporó y verificó:

- `CuentaServiceTest`

El test quedó ampliado de 4 a **8 tests**, cubriendo las operaciones de gestión de `Cuenta` incorporadas al servicio y manteniendo la verificación del cálculo del saldo a partir de los movimientos.

Se verificó el funcionamiento de:

1. Registrar una cuenta.
2. Buscar una cuenta por ID.
3. Listar todas las cuentas.
4. Listar cuentas por perfil financiero.
5. Calcular saldo sin movimientos.
6. Sumar ingresos al saldo.
7. Restar egresos al saldo.
8. Calcular correctamente un saldo con múltiples movimientos.

Resultado: los **8/8 tests de `CuentaServiceTest` terminaron en verde**.

Además, se ejecutó la batería general del proyecto y los **113/113 tests terminaron en verde**.

No se registran incidencias pendientes para este bloque.

## Build 024

Se amplió y verificó:

- `MovimientoServiceTest`

El test quedó ampliado de 6 a **11 tests**, manteniendo las operaciones existentes y agregando casos de modificación y validación del servicio.

Se verificó el funcionamiento de:

1. Registrar un `INGRESO`.
2. Registrar un `EGRESO`.
3. Listar movimientos por cuenta.
4. Listar movimientos por categoría.
5. Listar todos los movimientos.
6. Buscar un movimiento por ID.
7. Modificar la descripción de un movimiento.
8. Modificar las observaciones de un movimiento.
9. Cambiar la categoría de un movimiento.
10. Lanzar excepción al modificar un movimiento inexistente.
11. Lanzar excepción al cambiar la categoría de un movimiento inexistente.

Resultado: los **11/11 tests de `MovimientoServiceTest` terminaron en verde**.

Además, se ejecutó la batería general del proyecto y los **118/118 tests terminaron en verde**, con `Failures: 0`, `Errors: 0` y `Skipped: 0`.

No se registran incidencias pendientes para este bloque.
