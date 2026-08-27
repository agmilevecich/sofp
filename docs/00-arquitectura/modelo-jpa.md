# Modelo JPA

Este documento define los criterios técnicos del modelo JPA de SOFP. Las secciones de estado actual describen únicamente entidades implementadas; las extensiones futuras se identifican explícitamente como tales.

## 1. Estrategia de identificación

Todas las entidades persistentes tendrán un identificador único, inmutable y generado por la base de datos. El tipo utilizado es `Long`, empleando su tipo envoltorio para distinguir una entidad aún no persistida de una entidad ya almacenada.

La estrategia utilizada inicialmente es `IDENTITY`, adecuada para H2 y bajo responsabilidad de la base de datos.

## 2. Entidad base de auditoría

`EntidadAuditable` es una clase base JPA reutilizable y no una entidad con tabla propia. Sus campos forman parte de las tablas de las entidades que la extienden.

Los campos comunes son:

- `id`
- `fechaCreacion`
- `fechaActualizacion`
- `creadoPor`
- `actualizadoPor`

## 3. Entidades implementadas

El modelo actual incluye, entre otras, las siguientes entidades de dominio:

```text
EntidadAuditable
    |
    +-- Usuario
    +-- PerfilFinanciero
    +-- InstitucionFinanciera
    +-- Moneda
    +-- Cuenta
    +-- Categoria
    +-- Movimiento
    +-- OperacionFinanciera
```

Los instrumentos financieros (`Activo` y sus especializaciones) forman parte de la arquitectura prevista, pero no deben presentarse en este documento como si ya constituyeran la totalidad del modelo persistente implementado.

## 4. Relaciones JPA relevantes del estado actual

`Movimiento` pertenece obligatoriamente a una `Cuenta` y a una `Categoria`.

`Movimiento` puede estar asociado opcionalmente a una `OperacionFinanciera` mediante `@ManyToOne`, utilizando la columna `operacion_financiera_id`.

`OperacionFinanciera` mantiene la relación inversa mediante `@OneToMany(mappedBy = "operacionFinanciera")`. En el dominio se limita la colección a un máximo de dos movimientos.

`Cuenta` pertenece a un `PerfilFinanciero`, una `InstitucionFinanciera` y una `Moneda`.

## 5. Reglas de persistencia

### Carga diferida

Las relaciones se configuran con carga diferida (`LAZY`) cuando corresponde, evitando cargas innecesarias.

### Cascadas

Las cascadas se aplican de forma mínima y explícita. No se utiliza una cascada indiscriminada para todas las operaciones.

### Restricciones de eliminación

Las eliminaciones respetan las restricciones de integridad definidas por el dominio y la persistencia. No se debe eliminar información relacionada de forma implícita cuando ello comprometa la trazabilidad financiera.

### Validaciones básicas

Las entidades validan campos obligatorios y reglas propias del dominio. Los servicios coordinan las reglas que involucran varias entidades.

## 6. Convenciones del proyecto

### Tablas y columnas

El esquema físico utiliza nombres en español, en minúscula y con `snake_case`. Las tablas usan sustantivos plurales y las claves foráneas siguen el patrón `<entidad>_id`.

### Lenguaje del modelo

Las clases y atributos del dominio se nombran en español, utilizando PascalCase para clases y camelCase para atributos.

### Organización de paquetes

Las entidades se encuentran bajo `ar.com.agmilevecich.sofp.domain`, los componentes de persistencia bajo `ar.com.agmilevecich.sofp.persistence` y la configuración bajo `ar.com.agmilevecich.sofp.config`.

## 7. Saldos

El saldo de una cuenta se deriva de sus movimientos y no se mantiene como una fuente primaria independiente. `CuentaService.calcularSaldo(...)` implementa actualmente esta regla consultando `MovimientoRepository.listarPorCuenta(...)`.

Los movimientos de tipo `INGRESO` suman al saldo y los de tipo `EGRESO` lo disminuyen.

## 8. Consideraciones futuras

El modelo podrá evolucionar de forma incremental para incorporar activos financieros y el cálculo de sus posiciones.

Una eventual separación entre movimientos monetarios de cuentas y movimientos específicos de posiciones de activos podrá evaluarse cuando el dominio de inversiones lo requiera. Esa arquitectura todavía no está implementada y, por lo tanto, `MovimientoCuenta` y `MovimientoActivo` no deben tratarse como entidades existentes en el estado actual.
