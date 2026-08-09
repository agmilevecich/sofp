# SOFP — Estado de Tests

## Criterio

Los tests son parte del cierre de cada Build. Una funcionalidad se considera verificada cuando sus tests correspondientes quedan en verde y no se introducen regresiones conocidas.

## Tests / áreas conocidas

- `UsuarioTest`
- `MonedaTest`
- `CuentaTest`
- `CategoriaTest`
- `MovimientoTest`
- Tests JPA de las entidades correspondientes

## Último bloque

Build 010 incorpora:

- `MovimientoTest`
- `MovimientoJpaTest`

El test JPA verifica persistencia y recuperación de `Movimiento`, incluyendo sus relaciones con `Cuenta` y `Categoria`.

## Regla de actualización

Cuando un test sea agregado, corregido o ejecutado como parte de un Build, registrar aquí:

- nombre del test;
- objetivo;
- resultado;
- cualquier incidencia relevante;
- fecha/Build.

No registrar como verde un test que no haya sido realmente ejecutado con éxito.
