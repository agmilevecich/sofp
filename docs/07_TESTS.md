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

## Resultado actual

El último bloque de trabajo quedó verificado con todos los tests correspondientes en verde. No se avanzó al siguiente bloque hasta comprobar el funcionamiento de los repositorios y sus pruebas.

## Regla de actualización

Cuando un test sea agregado, corregido o ejecutado como parte de un Build, registrar aquí:

- nombre del test;
- objetivo;
- resultado;
- cualquier incidencia relevante;
- fecha/Build.

No registrar como verde un test que no haya sido realmente ejecutado con éxito.
