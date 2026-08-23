# SOFP — Decisiones

Este documento registra decisiones que conviene conservar durante toda la vida del proyecto.

## D-001 — El repositorio es la memoria permanente

La continuidad del proyecto no dependerá de una única conversación de ChatGPT. El código, Git, tests y documentación dentro del repositorio constituyen la fuente permanente de verdad.

## D-002 — Desarrollo incremental por Builds

El proyecto se desarrolla en bloques pequeños y verificables. Cada Build debe tener un objetivo concreto y terminar con tests en verde y un commit identificable.

## D-003 — Persistencia con JPA/Hibernate

Se utiliza Jakarta Persistence con Hibernate como implementación ORM.

## D-004 — H2 como base de datos de desarrollo

H2 se utiliza como base de datos para desarrollo y pruebas de persistencia.

## D-005 — BigDecimal para importes

Los valores monetarios se representan con `BigDecimal`, evitando `float`/`double` para cálculos financieros.

## D-006 — Dominio antes de interfaz

El modelo de dominio, sus reglas y persistencia se construyen antes de avanzar fuertemente sobre la interfaz de usuario.

## D-007 — Tests como condición de avance

Una funcionalidad no se considera cerrada hasta verificar sus tests correspondientes y mantener las pruebas anteriores funcionando.

## D-008 — Sistema de continuidad documental

Se mantienen documentos específicos para estado actual, contexto de ChatGPT, decisiones, Builds, tests y pendientes. Esto permite continuar el proyecto en nuevas conversaciones o con otras herramientas sin perder contexto.

## D-009 — Las transferencias no son un TipoMovimiento

Una transferencia entre cuentas se considera una operación financiera que produce dos movimientos relacionados:

- un movimiento de tipo `EGRESO` en la cuenta origen;
- un movimiento de tipo `INGRESO` en la cuenta destino.

Por lo tanto, `TRANSFERENCIA` **no debe incorporarse al enum `TipoMovimiento`**. El enum representa el efecto individual de un movimiento sobre una cuenta y mantiene inicialmente los valores `INGRESO` y `EGRESO`.

La transferencia se modela mediante `OperacionFinanciera`, que agrupa y relaciona los movimientos que representan sus efectos. Esto permite conservar la trazabilidad de que el egreso y el ingreso pertenecen al mismo hecho de negocio.

La relación persistente se implementa mediante:

- `Movimiento.operacionFinanciera` con `@ManyToOne` y columna `operacion_financiera_id`;
- `OperacionFinanciera.movimientos` con `@OneToMany(mappedBy = "operacionFinanciera")`.

Una `OperacionFinanciera` admite como máximo dos movimientos y un `Movimiento` no puede quedar asociado a dos operaciones financieras diferentes. La asociación se realiza desde `OperacionFinanciera.agregarMovimiento(...)`, que mantiene ambos lados de la relación y rechaza movimientos nulos, repetidos o ya asociados a otra operación.

Ejemplo conceptual:

```text
OperacionFinanciera: TRANSFERENCIA $100.000
        |
        +-- Movimiento EGRESO  -> Cuenta origen     -$100.000
        |
        +-- Movimiento INGRESO -> Cuenta destino    +$100.000
```

Esta decisión queda establecida como criterio arquitectónico para las futuras implementaciones de transferencias, `OperacionFinanciera`, `MovimientoCuenta` y movimientos relacionados con otros hechos financieros.
