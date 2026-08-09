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

### Regla para futuros Builds

Agregar cada Build nuevo inmediatamente después de cerrarlo, incluyendo:

- número y nombre;
- objetivo;
- cambios principales;
- tests ejecutados;
- resultado;
- commit asociado;
- próximo paso.
