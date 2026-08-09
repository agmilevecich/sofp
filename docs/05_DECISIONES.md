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
