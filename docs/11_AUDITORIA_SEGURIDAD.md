# SOFP — Auditoría de seguridad y aislamiento de datos

## Alcance

Auditoría transversal realizada sobre el código del proyecto, priorizando identidad, perfiles financieros, cuentas, categorías, movimientos, operaciones financieras, activos, posiciones, valorización, cartera y reportes.

La fuente de verdad técnica es el código y los tests actuales. Esta documentación registra el estado de la auditoría y de sus correcciones; no sustituye la revisión del repositorio.

## Estado de la auditoría

**Auditoría exploratoria: FINALIZADA.**

El recorrido exploratorio identificó los bloques que requerían corrección. La etapa posterior de implementación está actualmente en curso en `feature/seguridad-aislamiento-datos`.

## Resultado actual — 31/08/2026

La suite general fue ejecutada después de las correcciones actuales:

- **503 tests**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración: **15:01 min**.

Por lo tanto, las correcciones realizadas hasta ahora mantienen **503/503 tests en verde**.

## Bloques corregidos en la feature actual

### `CuentaService`

Se incorporó autorización del propietario para las operaciones mutables relevantes mediante `usuarioId`.

La cobertura de `CuentaServiceTest` fue adaptada para representar la autorización por propietario.

### `CategoriaService`

Se incorporó autorización del propietario para las operaciones mutables relevantes mediante `usuarioId`.

Durante la adaptación de los tests se corrigieron además problemas de rollback y persistencia de perfiles.

### `MovimientoService`

Se incorporó autorización del propietario para las operaciones mutables relevantes mediante `usuarioId`, incluyendo modificaciones y eliminación.

`MovimientoServiceTest` fue adaptado a las nuevas firmas y reglas.

### `PosicionActivoService`

La posición dejó de calcularse únicamente por activo. Ahora recibe `PerfilFinanciero` y `Activo` y consulta movimientos mediante `activoId + perfilFinancieroId`.

`MovimientoActivoRepository` incorpora `listarPorActivoYPerfilFinanciero(...)`, filtrando los movimientos mediante las cuentas origen/destino de la operación financiera.

Esto evita mezclar movimientos del mismo activo pertenecientes a perfiles financieros diferentes.

## Principio de autorización aplicado

Las correcciones actuales siguen el patrón establecido previamente en `PerfilFinancieroService`:

1. recibir el identificador del recurso y el usuario solicitante cuando el caso de uso sea protegido;
2. obtener el recurso;
3. verificar propietario/perfil autorizado;
4. ejecutar la operación;
5. conservar las validaciones de dominio existentes.

No se debe aplicar autorización por perfil a catálogos globales como `Activo`, `Moneda` o `InstitucionFinanciera`.

## Hallazgos todavía pendientes de cierre

La etapa transversal de seguridad **todavía no está cerrada**. Permanecen:

### 1. `OperacionFinancieraService`

Debe verificarse/incorporarse autorización explícita del usuario solicitante para las operaciones protegidas de transferencia, compra y venta, manteniendo las reglas de consistencia del dominio.

### 2. Lecturas por ID y listados

Debe revisarse caso por caso qué consultas son internas y cuáles representan casos de uso accesibles al usuario. Las lecturas de recursos pertenecientes a un perfil deben respetar el aislamiento cuando corresponda.

### 3. Caminos alternativos de creación

Debe comprobarse que no exista una vía de creación directa que permita eludir las reglas de `MovimientoService`.

### 4. Cobertura específica pendiente

Deben completarse o verificarse tests para:

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

## Estado de cierre

**Correcciones realizadas:** cuentas, categorías, movimientos y aislamiento de posiciones.  
**Suite general:** 503/503 en verde.  
**Auditoría transversal:** pendiente de cierre definitivo hasta resolver los hallazgos restantes.

## Próxima etapa

Continuar con `OperacionFinancieraService`, luego revisar lecturas y caminos alternativos de creación. Después de cada corrección ejecutar tests específicos y relacionados; al cerrar el bloque, repetir la suite general y revisar `git diff`, `git diff --check` y `git status`.

Una vez cerrada la seguridad transversal, comparar nuevamente la feature con `main` y preparar la transición a Swing.
