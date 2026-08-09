# Build 011 - Infraestructura de pruebas JPA con H2

## Objetivo

Aislar las pruebas de persistencia JPA de la base de datos real de la aplicación y permitir la ejecución de todos los tests JPA en conjunto sin conflictos entre datos de prueba.

---

## Situación inicial

Las pruebas JPA utilizaban la misma base H2 de la aplicación:

```text
jdbc:h2:file:./database/sofp
```

Al ejecutar los tests individualmente podían pasar correctamente, pero al ejecutar la batería completa aparecían conflictos por restricciones `UNIQUE`, especialmente sobre `Usuario.email`.

Ejemplo:

```text
Unique index or primary key violation
USUARIOS(EMAIL)
```

Esto ocurría porque los tests compartían datos persistidos entre ejecuciones.

---

## Solución implementada

Se creó una infraestructura específica para tests mediante `JpaTestManager`.

La configuración de pruebas utiliza una unidad de persistencia independiente:

```text
sofp-persistence-unit-test
```

y una base H2 en memoria, separada de la base de producción/desarrollo.

La configuración de test utiliza:

```text
jdbc:h2:mem:sofp_test_<identificador>
```

con:

```text
hibernate.hbm2ddl.auto = create-drop
```

De esta manera los datos de las pruebas no se almacenan en `database/sofp`.

---

## Entidades registradas en la unidad de persistencia de test

La unidad de persistencia de pruebas reconoce las entidades principales del dominio:

- `Usuario`
- `PerfilFinanciero`
- `InstitucionFinanciera`
- `Moneda`
- `Cuenta`
- `Categoria`
- `Movimiento`

---

## JpaTestManager

Se implementó `JpaTestManager` como infraestructura exclusiva para los tests JPA.

Responsabilidades principales:

- crear `EntityManager` para pruebas;
- crear una base H2 en memoria aislada;
- utilizar `create-drop` para el esquema de pruebas;
- cerrar correctamente el `EntityManagerFactory` mediante `JpaTestManager.close()`.

La infraestructura de producción `JpaManager` permanece separada y continúa utilizando la base H2 en archivo de la aplicación.

---

## Tests JPA actualizados

Se actualizaron los tests que utilizan H2 para utilizar `JpaTestManager`:

- `JpaManagerTest`
- `UsuarioJpaTest`
- `CategoriaJpaTest`
- `CuentaJpaTest`
- `MovimientoJpaTest`

Los tests fueron ejecutados individualmente y posteriormente en conjunto.

---

## Resultado

La batería general de tests terminó correctamente con todos los tests en verde.

Se comprobó que:

- los tests JPA funcionan individualmente;
- los tests JPA funcionan ejecutándose conjuntamente;
- no se producen conflictos por valores `UNIQUE` entre tests;
- no es necesario borrar manualmente `database/sofp.mv.db` entre pruebas;
- la base de datos de la aplicación permanece separada de la base de datos de pruebas.

---

## Decisiones

### Base de datos de producción/desarrollo

Continúa utilizando:

```text
jdbc:h2:file:./database/sofp
```

### Base de datos de pruebas

Utiliza H2 en memoria y una configuración exclusiva para tests.

Esto permite que los tests sean reproducibles y no dependan del estado previo de la base de datos de desarrollo.

---

## Estado

**Build 011 completado.**

La infraestructura de persistencia y pruebas JPA queda establecida como base para las siguientes etapas del desarrollo del SOFP.

---

## Continuidad

El proyecto queda preparado para continuar con el siguiente bloque funcional sin que las pruebas de persistencia dependan de la base de datos utilizada por la aplicación.
