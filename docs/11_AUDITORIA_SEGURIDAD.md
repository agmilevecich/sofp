# SOFP — Auditoría de seguridad y aislamiento de datos

## Alcance de esta etapa

Auditoría progresiva realizada sobre el código actual de `main`, priorizando entidades, repositorios y servicios relacionados con `PerfilFinanciero`, cuentas, categorías, movimientos y operaciones financieras.

La auditoría todavía NO está cerrada. Este documento registra únicamente hallazgos verificados hasta este punto; no convierte los bloques pendientes en elementos aprobados.

## Estado de la auditoría

### Aprobados

- `PerfilFinanciero`: relación obligatoria con `Usuario` y propietario inequívoco.
- `PerfilFinancieroRepository`: búsqueda por ID y listado filtrado por usuario correctamente diferenciados.
- `PerfilFinancieroService`: las operaciones protegidas de modificación de descripción y activación/desactivación verifican propietario mediante `perfilId + usuarioId`.
- `Cuenta`: relación obligatoria con `PerfilFinanciero`.
- `CuentaRepository`: consulta por perfil correctamente filtrada; la búsqueda técnica por ID no se considera por sí misma un problema de autorización.
- `Categoria`: modelo y persistencia revisados; las operaciones funcionales están cubiertas por tests.
- `InstitucionFinanciera`: catálogo global; no corresponde autorización por perfil.
- `Moneda`: catálogo global; no corresponde autorización por perfil.
- `Activo`: catálogo global; no pertenece a un perfil financiero.
- `ActivoRepository`: acceso global coherente con el modelo de catálogo.
- `Movimiento`: integridad interna revisada; cuenta, categoría y datos básicos obligatorios.
- `MovimientoService.registrar()`: verifica que cuenta y categoría pertenezcan al mismo perfil y rechaza cuentas desactivadas.
- `MovimientoService.cambiarCategoria()`: mantiene la consistencia entre perfil de la cuenta y nueva categoría.
- `OperacionFinanciera`: reglas internas de coherencia revisadas.
- `OperacionFinancieraService`: las validaciones de consistencia entre cuentas, categorías, monedas y movimientos están implementadas.
- `PosicionActivoService`: responsabilidad limitada a consulta/cálculo de posición; no se detectó por sí sola una vulnerabilidad confirmada.
- `MovimientoActivo`: integridad interna revisada, incluyendo cantidades, precios, tipo y asociación a operación financiera.
- `Usuario` / `UsuarioRepository`: revisados como raíz de identidad y catálogo de usuarios; no se aplica artificialmente el aislamiento por perfil.

## Hallazgos confirmados

### 1. Operaciones mutables de `CuentaService`

Las operaciones sobre una cuenta por ID requieren revisión de autorización por propietario. El patrón correcto ya utilizado en `PerfilFinancieroService` debe servir como referencia.

### 2. Operaciones mutables de `CategoriaService`

Las operaciones sobre una categoría por ID requieren revisión de autorización por propietario. Los tests funcionales existentes no cubren suficientemente el intento de operar sobre una categoría perteneciente a otro perfil.

### 3. `MovimientoService`: modificación y eliminación

Los métodos siguientes operan a partir de `movimientoId` sin recibir `usuarioId`/perfil autorizado ni realizar una comprobación equivalente a la de `PerfilFinancieroService`:

- `modificarDescripcion()`;
- `modificarObservaciones()`;
- `modificarTipoMovimiento()`;
- `modificarImporte()`;
- `modificarFechaHora()`;
- `eliminar()`.

`cambiarCategoria()` verifica consistencia de perfil, pero esa comprobación no constituye por sí sola autorización del usuario solicitante.

### 4. `OperacionFinancieraService`: creación de operaciones

Las operaciones de transferencia, compra y venta validan correctamente la consistencia de las entidades recibidas, pero no se ha verificado un mecanismo de autorización del usuario solicitante equivalente al de `PerfilFinancieroService`.

El riesgo potencial es que una operación pueda recibir objetos pertenecientes a otro perfil si esos objetos llegan a la capa de servicio. Este hallazgo debe resolverse en la capa de coordinación/autorización, sin modificar innecesariamente entidades globales.

## Hallazgos potenciales pendientes de confirmación

### MovimientoActivo / propietario indirecto

`MovimientoActivo` no tiene relación directa con `PerfilFinanciero`. El propietario se deriva por las relaciones de la operación financiera y sus cuentas.

La consulta de posición mediante `PosicionActivoService` todavía requiere completar la auditoría de las implementaciones reales de persistencia y cálculo antes de clasificarla como vulnerable o aprobada definitivamente.

### Cuenta + Categoría de perfiles distintos

El constructor de `Movimiento` no impone por sí mismo que cuenta y categoría pertenezcan al mismo perfil. `MovimientoService.registrar()` sí realiza esta comprobación. Por lo tanto, actualmente la regla está protegida en el servicio; queda verificar todos los caminos alternativos de creación.

## Principio utilizado

La auditoría diferencia deliberadamente:

- integridad de dominio;
- consistencia entre entidades;
- autorización del propietario;
- catálogos globales.

No se debe agregar una validación de propietario a entidades globales como `Activo`, `Moneda` o `InstitucionFinanciera`.

Cuando una entidad pertenece a un perfil, la autorización debe verificarse en el servicio que coordina la operación, siguiendo el patrón ya implementado para `PerfilFinanciero`.

## Estado de cierre

**Auditoría en curso.**

Bloques revisados: identidad, perfil financiero, cuenta, categoría, catálogos globales, movimiento, operación financiera y primera revisión de activos.

Pendiente: completar los servicios/repositorios restantes, especialmente los caminos de cartera, posiciones y movimientos de activos, revisar tests de autorización y realizar una revisión transversal final para detectar caminos alternativos de acceso a datos ajenos.

No se considera terminada la auditoría hasta completar ese recorrido.
