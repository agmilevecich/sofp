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

## Último bloque

Build 011 incorpora la estabilización de la infraestructura de pruebas JPA:

- `JpaTestManager` separado de `JpaManager`.
- H2 de pruebas en memoria.
- `create-drop` para el esquema de pruebas.
- Aislamiento de los datos utilizados por los tests JPA.
- Cierre de `EntityManager` y `JpaTestManager` al finalizar las pruebas correspondientes.
- Corrección del conflicto de unicidad del email de `Usuario` al ejecutar la batería completa.

## Resultado de Build 011

Se ejecutaron los tests JPA individualmente y posteriormente la batería general del proyecto.

Resultado: **todos los tests terminaron en verde**.

En particular, dejó de ser necesario borrar manualmente `database/sofp.mv.db` entre tests para evitar conflictos de datos.

## Incidencia resuelta

Al ejecutar la batería general anteriormente aparecía una violación de unicidad sobre `USUARIOS.EMAIL`, porque distintos tests compartían datos en la misma base H2 de pruebas. La infraestructura fue modificada para aislar la base utilizada por los tests.

## Regla de actualización

Cuando un test sea agregado, corregido o ejecutado como parte de un Build, registrar aquí:

- nombre del test;
- objetivo;
- resultado;
- cualquier incidencia relevante;
- fecha/Build.

No registrar como verde un test que no haya sido realmente ejecutado con éxito.
