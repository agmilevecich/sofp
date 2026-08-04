# Modelo JPA inicial

Este documento define los criterios técnicos para la primera implementación JPA de SOFP. Se mantiene alineado con la arquitectura del Build 003: el sistema es multiusuario, `PerfilFinanciero` es el contexto de la información financiera, `Moneda` es transversal y la auditoría se centraliza en `EntidadAuditable`.

## 1. Estrategia de identificación

Todas las entidades persistentes tendrán un identificador único, inmutable y generado por la base de datos. El tipo recomendado para los identificadores es `Long`, utilizando su tipo envoltorio para distinguir una entidad aún no persistida de una entidad ya almacenada.

La estrategia inicial de generación será `IDENTITY`. Es simple de operar con H2 y mantiene la generación del identificador bajo responsabilidad de la base de datos. El identificador técnico no expresará reglas de negocio ni será reutilizado después de eliminar un registro.

Si en el futuro se requiere integración distribuida, importación de datos o generación de identificadores fuera de la base de datos, esta decisión podrá revisarse para adoptar UUID. No se mezclarán ambas estrategias sin una decisión de arquitectura explícita.

## 2. Entidad base de auditoría

`EntidadAuditable` será una clase base JPA reutilizable, no una entidad con tabla propia. Se aplicará como superclase mapeada para que sus columnas formen parte de las tablas de las entidades que la extiendan.

Los campos comunes serán:

- `id`: identificador técnico único.
- `fechaCreacion`: fecha y hora de creación del registro.
- `fechaActualizacion`: fecha y hora de la última actualización.
- `creadoPor`: identificador o referencia técnica del usuario creador del registro.
- `actualizadoPor`: identificador o referencia técnica del usuario que realizó la última modificación.

El objetivo es aportar trazabilidad de creación y modificación a la información relevante del dominio. `Usuario`, `PerfilFinanciero` y `Moneda` heredarán esta base en la primera capa; las futuras entidades financieras que requieran auditoría seguirán el mismo criterio.

## 3. Primera capa de entidades

La primera etapa del modelo persistente incorporará las siguientes clases:

```text
EntidadAuditable
    |
    +-- Usuario
    |
    +-- PerfilFinanciero
    |
    +-- Moneda
```

### Usuario

Representará a la persona que utiliza el sistema. Será el propietario lógico de uno o varios perfiles financieros y la referencia de aislamiento de información en el modelo multiusuario.

### PerfilFinanciero

Representará un contexto financiero independiente de un usuario, como un perfil personal, familiar o empresarial. Reunirá en etapas posteriores cuentas, activos, operaciones y movimientos.

### Moneda

Representará una unidad monetaria reutilizable, identificada por un código estable. Será una entidad transversal para expresar la moneda de cuentas, activos, operaciones y movimientos cuando estas entidades sean incorporadas.

## 4. Relaciones JPA iniciales

La relación entre `Usuario` y `PerfilFinanciero` será uno a muchos bidireccional:

- Un `Usuario` puede tener cero o múltiples `PerfilFinanciero`.
- Cada `PerfilFinanciero` pertenece obligatoriamente a un único `Usuario`.
- La clave foránea residirá en la tabla de perfiles financieros mediante la columna `usuario_id`.

La colección de perfiles será la vista de navegación del usuario; la relación del perfil hacia su usuario será la propietaria del vínculo en persistencia. La asignación de un perfil a un usuario será obligatoria y no se permitirán perfiles huérfanos.

## 5. Reglas de persistencia

### Carga diferida

Las relaciones se configurarán con carga diferida (`LAZY`) por defecto. En particular, la referencia de `PerfilFinanciero` a `Usuario` será explícitamente diferida y la colección de perfiles de un usuario no se cargará hasta ser necesaria. Las consultas que requieran datos relacionados deberán declararlo de forma intencional para evitar cargas innecesarias.

### Cascadas

Las cascadas se aplicarán de forma mínima y explícita. Para la relación de `Usuario` con sus perfiles podrán habilitarse las operaciones de persistencia y actualización cuando el caso de uso las requiera. No se utilizará una cascada indiscriminada para todas las operaciones.

### Restricciones de eliminación

No se eliminará un `Usuario` mientras posea perfiles financieros. La eliminación de un `PerfilFinanciero` será una operación explícita y no eliminará a su usuario. `Moneda` no podrá eliminarse cuando sea referenciada por cuentas, activos, operaciones o movimientos. Estas restricciones preservan la integridad y la trazabilidad de la información financiera.

### Validaciones básicas

Las entidades validarán campos obligatorios, longitudes máximas y unicidad donde corresponda. Como mínimo:

- Los datos de identificación del usuario requeridos por el sistema no podrán ser nulos y deberán ser únicos cuando actúen como credenciales o identificadores funcionales.
- Un perfil deberá tener nombre y usuario propietario.
- El código de `Moneda` será obligatorio, único y seguirá el formato definido para su catálogo.
- Las fechas de auditoría se completarán automáticamente y conservarán coherencia temporal.

## 6. Convenciones del proyecto

### Tablas y columnas

El esquema físico usará nombres en español, en minúscula y con `snake_case`. Las tablas usarán sustantivos plurales, por ejemplo `usuarios`, `perfiles_financieros` y `monedas`. Las columnas seguirán el mismo criterio, por ejemplo `id`, `usuario_id`, `fecha_creacion` y `fecha_actualizacion`.

Las claves foráneas se nombrarán con el singular de la entidad referenciada seguido de `_id`. Las restricciones de unicidad e índices usarán nombres descriptivos y consistentes con la tabla y las columnas involucradas.

### Lenguaje del modelo

Las clases y atributos del dominio se nombrarán en español, utilizando PascalCase para clases y camelCase para atributos. Los nombres físicos conservarán el español en `snake_case`. Se evitará mezclar idiomas para un mismo concepto.

### Organización de paquetes JPA

Cuando se incorpore código, las entidades del dominio se organizarán bajo `ar.com.agmilevecich.sofp.domain`. Los componentes específicos de persistencia JPA se ubicarán bajo `ar.com.agmilevecich.sofp.persistence`, y la configuración de infraestructura permanecerá en `ar.com.agmilevecich.sofp.config`. Esta separación permitirá conservar el modelo de dominio distinguible de los detalles de acceso a datos.

## 7. Consideraciones futuras

El modelo podrá ampliarse de forma incremental sin alterar las decisiones iniciales:

- `Cuenta` pertenecerá a un `PerfilFinanciero` y se relacionará con `Moneda`.
- `Activo` pertenecerá a un `PerfilFinanciero`, utilizará la jerarquía definida en Build 003 y se relacionará con la moneda en la que se denomine o cotice.
- `OperacionFinanciera` pertenecerá a un `PerfilFinanciero` y representará el hecho de negocio registrado por el usuario.
- `MovimientoCuenta` y `MovimientoActivo` serán los efectos de una operación sobre saldos de cuentas y posiciones de activos; los saldos se derivarán de estos movimientos.
- `Tarjeta` se incorporará como medio asociado a una cuenta o perfil, con movimientos que respeten el mismo criterio de trazabilidad.

Las relaciones futuras mantendrán carga diferida, restricciones de integridad y auditoría cuando correspondan. El crecimiento del modelo deberá conservar la separación entre la operación financiera y los movimientos que genera.
