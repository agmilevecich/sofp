# SOFP — Auditoría de seguridad y aislamiento de datos

## Alcance

Auditoría transversal realizada sobre el código actual de `main`, priorizando identidad, perfiles financieros, cuentas, categorías, movimientos, operaciones financieras, activos, posiciones, valorización, cartera y reportes.

La fuente de verdad técnica es el código y los tests actuales. Esta documentación registra el resultado de la auditoría realizada hasta el cierre de esta etapa.

## Estado de la auditoría

**Auditoría exploratoria: FINALIZADA.**

Se completó el recorrido de los bloques relevantes identificados para esta etapa. No se considera necesario continuar con una revisión indefinida por bloques: los problemas restantes están identificados y pasan a la etapa de corrección y pruebas.

## Resultado general

La arquitectura actual distingue correctamente entre:

- integridad de dominio;
- consistencia entre entidades;
- autorización del propietario;
- catálogos globales;
- datos derivados de cartera.

La seguridad de `PerfilFinanciero` ya está implementada y probada. Sin embargo, la auditoría detectó que el mismo principio de aislamiento no está aplicado de manera uniforme a todas las operaciones sobre recursos pertenecientes a un perfil.

## Bloques aprobados

- `PerfilFinanciero`: relación obligatoria con `Usuario` y propietario inequívoco.
- `PerfilFinancieroRepository`: búsqueda por ID y listado por usuario diferenciados.
- `PerfilFinancieroService`: las operaciones protegidas de modificación y activación/desactivación verifican propietario mediante `perfilId + usuarioId`.
- `Cuenta`: relación obligatoria con `PerfilFinanciero`.
- `CuentaRepository`: consulta por perfil correctamente filtrada.
- `Categoria`: relación con `PerfilFinanciero` y persistencia revisadas.
- `InstitucionFinanciera`: catálogo global.
- `Moneda`: catálogo global.
- `Activo`: catálogo global.
- `ActivoRepository`: acceso global coherente con el modelo de catálogo.
- `MovimientoActivo`: integridad interna revisada; cantidades y precios positivos, tipo obligatorio y asociación opcional a operación financiera.
- `MovimientoActivoRepository.listarPorPerfilFinanciero()`: filtrado por cuentas origen/destino del perfil.
- `CalculadorPosicionActivo`: cálculo derivado sin efectos de persistencia.
- `PosicionActivo`: cantidad, costo de adquisición, precio promedio y prevención de posición negativa revisados.
- `ValorizacionPosicionActivo`: valor actual, ganancia/pérdida, rendimiento y validaciones revisados.
- `ReporteCarteraActivo`: totales y composición revisados.
- `DetalleComposicionCarteraActivo`: validaciones e inmutabilidad revisadas.
- `DetalleMovimientoCarteraActivo`: cálculo del importe y exposición de datos revisados.
- `CarteraActivoService`: construcción de cartera y valorizaciones a partir de movimientos filtrados por perfil.
- `MovimientoService.registrar()`: cuenta y categoría deben pertenecer al mismo perfil; cuentas desactivadas son rechazadas.
- `MovimientoService.cambiarCategoria()`: mantiene consistencia de perfil entre cuenta y categoría.
- `OperacionFinancieraService`: validaciones de consistencia de cuentas, categorías, monedas y movimientos revisadas.

## Hallazgos confirmados

### 1. `CuentaService`: autorización insuficiente en operaciones sobre cuentas

Las operaciones mutables sobre una cuenta se realizan por `cuentaId` sin una comprobación explícita del propietario/usuario solicitante equivalente a la implementada en `PerfilFinancieroService`.

Esto afecta, entre otras, operaciones de modificación de datos, activación/desactivación y eliminación.

**Riesgo:** un usuario que consiga el ID de una cuenta de otro perfil podría intentar modificarla o eliminarla a través de la capa de servicio.

### 2. `CategoriaService`: autorización insuficiente en operaciones sobre categorías

Las operaciones mutables sobre una categoría por ID no aplican de forma uniforme una comprobación explícita del propietario.

**Riesgo:** posible modificación o eliminación de una categoría perteneciente a otro perfil si el servicio recibe su ID.

### 3. `MovimientoService`: modificación y eliminación sin autorización de propietario

Los métodos:

- `modificarDescripcion()`;
- `modificarObservaciones()`;
- `modificarTipoMovimiento()`;
- `modificarImporte()`;
- `modificarFechaHora()`;
- `eliminar()`;

obtienen el movimiento por ID y operan sobre él sin recibir `usuarioId`/contexto de perfil autorizado ni realizar una comprobación equivalente.

`cambiarCategoria()` comprueba consistencia de perfil, pero esa comprobación no equivale por sí sola a autorización del usuario solicitante.

### 4. `OperacionFinancieraService`: autorización del solicitante no demostrada

Las operaciones de transferencia, compra y venta verifican correctamente reglas de consistencia del dominio, pero no aplican una autorización explícita del usuario solicitante equivalente a la protección de `PerfilFinancieroService`.

**Riesgo:** si se suministran entidades pertenecientes a otro perfil a esta capa, la operación podría ejecutarse sin comprobar que quien la solicita sea propietario del perfil.

### 5. `PosicionActivoService`: aislamiento insuficiente por perfil

`obtenerPosicion(Activo activo)` utiliza la consulta de movimientos por activo. Esa consulta no está filtrada por perfil, por lo que la posición puede incorporar movimientos de distintos perfiles cuando un mismo activo aparece en más de una cartera.

Este es un hallazgo especialmente relevante porque afecta a datos derivados: una posición calculada podría revelar cantidades/costos agregados que no pertenecen exclusivamente al perfil consultante.

**Corrección esperada:** la obtención de posición debe estar contextualizada por perfil y utilizar movimientos filtrados por ese perfil.

### 6. Lecturas por ID/listados: revisar aislamiento de lectura

Existen operaciones técnicas de consulta por ID o listados globales en servicios/repositorios. No todas son vulnerabilidades por sí mismas, porque los repositorios también sirven como componentes técnicos.

La corrección debe determinar, caso por caso, cuáles son operaciones internas y cuáles son casos de uso expuestos al usuario. Toda lectura de recursos pertenecientes a un perfil debe aplicar el mismo aislamiento que las mutaciones.

## Observaciones de integridad

### `Movimiento` y perfiles distintos

El constructor de `Movimiento` no impone por sí mismo que cuenta y categoría pertenezcan al mismo perfil. La regla está actualmente protegida por `MovimientoService.registrar()`.

Esto es aceptable mientras el servicio sea el camino de creación utilizado por los casos de uso. Deben revisarse caminos alternativos de creación directa.

### `MovimientoActivo` y propietario indirecto

`MovimientoActivo` no tiene relación directa con `PerfilFinanciero`. El propietario se deriva mediante la operación financiera y sus cuentas. Esto es coherente con el modelo actual, pero exige que las consultas de cartera/posición se ejecuten siempre con contexto de perfil.

### Detalles de reporte

`DetalleMovimientoCarteraActivo` expone referencias a `MovimientoActivo` y `OperacionFinanciera`. No se clasifica como vulnerabilidad propia de la clase: la capa de aplicación debe decidir qué información cruza la frontera hacia UI/DTO/API.

## Tests y validación

La suite global vigente antes de esta auditoría consta de **486/486 tests en verde**, con `BUILD SUCCESS`, según el estado de continuidad registrado.

La existencia de tests verdes no implica que todos los escenarios de autorización estén cubiertos. Para cerrar las correcciones deberán agregarse/verificarse tests específicos para:

- propietario autorizado;
- usuario no propietario;
- recurso inexistente;
- parámetros `null` cuando corresponda;
- lectura de recursos propios;
- intento de lectura de recursos ajenos;
- modificación de recursos ajenos;
- eliminación de recursos ajenos;
- posición de un activo compartido entre perfiles;
- caminos alternativos de creación de movimientos.

## Principio de corrección

La referencia arquitectónica para las correcciones es el patrón ya implementado en `PerfilFinancieroService`:

1. recibir el identificador del recurso y el usuario solicitante cuando el caso de uso sea protegido;
2. obtener el recurso;
3. verificar propietario;
4. ejecutar la mutación o lectura autorizada;
5. mantener las validaciones de dominio existentes.

No agregar autorización por perfil a catálogos globales como `Activo`, `Moneda` o `InstitucionFinanciera`.

## Próxima etapa

La auditoría exploratoria queda cerrada. El trabajo siguiente es **corregir los hallazgos confirmados**, empezando por `PosicionActivoService` y estableciendo un patrón uniforme de autorización/aislamiento para las operaciones de cuentas, categorías, movimientos y operaciones financieras.

Después de cada corrección deberán ejecutarse tests específicos, tests relacionados y la suite completa cuando corresponda, además de revisar `git diff`, `git diff --check` y `git status`.
