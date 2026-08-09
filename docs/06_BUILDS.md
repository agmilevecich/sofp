# SOFP — Historial de Builds

## Build 001
Configuración inicial del proyecto.

## Build 002
Configuración inicial de persistencia y base de datos.

## Build 003
Primeras entidades y consolidación del dominio.

## Build 004
Evolución del modelo de dominio y pruebas.

## Build 005
Diseño de la entidad `Cuenta`.

## Builds posteriores

Se realizaron trabajos de consolidación del dominio, arquitectura, validaciones, instituciones financieras, categorías y cuentas.

## Build 009.1
Implementación de la entidad `Categoria`.

## Build 010
Implementación de la entidad `Movimiento`.

Incluye:

- Entidad `Movimiento`.
- Enum `TipoMovimiento`.
- Validación de importe positivo.
- Relaciones con `Cuenta` y `Categoria`.
- Test unitario.
- Test JPA.

## Build 011 — Aislamiento y estabilización de tests JPA con H2

### Objetivo

Separar la base H2 utilizada por los tests de la base H2 de desarrollo y conseguir que la batería completa de tests pueda ejecutarse de forma repetible, sin depender de borrar manualmente la base de datos.

### Cambios principales

- Se incorporó `JpaTestManager` para la infraestructura específica de pruebas.
- Los tests JPA utilizan la unidad de persistencia `sofp-persistence-unit-test`.
- Los tests utilizan H2 en memoria con `hibernate.hbm2ddl.auto=create-drop`.
- Se evitó compartir los datos persistidos entre ejecuciones de tests mediante una base de test aislada.
- Los tests JPA cierran correctamente el `EntityManager` y `JpaTestManager`.
- Se resolvió el conflicto de unicidad del email de `Usuario` que aparecía al ejecutar la batería completa.
- Se mantuvo separada la persistencia de producción (`JpaManager`) de la infraestructura de pruebas (`JpaTestManager`).

### Tests verificados

- `JpaManagerTest`
- `UsuarioJpaTest`
- `CategoriaJpaTest`
- `CuentaJpaTest`
- `MovimientoJpaTest`
- Batería general de tests del proyecto.

### Resultado

Todos los tests de la batería general terminaron en verde. Los tests JPA también funcionan individualmente sin necesidad de borrar manualmente `database/sofp.mv.db`.

### Commit de código

Pendiente de registrar en `main`. La documentación de continuidad se actualiza ahora para conservar el estado alcanzado.

### Próximo paso

Registrar el commit del código de este Build en `main` y luego continuar con el siguiente bloque funcional definido a partir del estado real del proyecto.

### Regla para futuros Builds

Agregar cada Build nuevo inmediatamente después de cerrarlo, incluyendo:

- número y nombre;
- objetivo;
- cambios principales;
- tests ejecutados;
- resultado;
- commit asociado;
- próximo paso.
