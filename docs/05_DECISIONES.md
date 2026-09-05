# SOFP — Decisiones

Este documento registra decisiones que conviene conservar durante toda la vida del proyecto.

## D-001 — El repositorio es la memoria permanente

La continuidad del proyecto no dependerá de una única conversación de ChatGPT. Código, Git, tests y documentación del repositorio forman la memoria permanente, con prioridad del código y tests sobre `docs/`.

## D-002 — Desarrollo incremental por Builds

El proyecto se desarrolla en bloques pequeños y verificables. Cada Build debe tener objetivo concreto, tests y commit identificable.

## D-003 — Persistencia con JPA/Hibernate

Se utiliza Jakarta Persistence con Hibernate como implementación ORM.

## D-004 — H2 como base de datos de desarrollo

H2 se utiliza para desarrollo y pruebas de persistencia.

## D-005 — BigDecimal para importes

Los valores monetarios se representan con `BigDecimal`, evitando `float`/`double`.

## D-006 — Dominio antes de interfaz

El modelo de dominio, sus reglas y persistencia se construyen antes de avanzar fuertemente sobre la interfaz.

## D-007 — Tests como condición de avance

Una funcionalidad no se considera cerrada hasta verificar sus tests correspondientes y mantener las pruebas anteriores funcionando.

## D-008 — Sistema de continuidad documental

Se mantienen documentos de estado, contexto, decisiones, Builds, tests y pendientes para poder continuar el proyecto sin depender de una conversación concreta.

## D-009 — Las transferencias no son un TipoMovimiento

Una transferencia entre cuentas produce un `EGRESO` en origen y un `INGRESO` en destino. `TRANSFERENCIA` no pertenece a `TipoMovimiento`. La relación se modela mediante `OperacionFinanciera`.

## D-010 — ControlFinanzas como banco de ideas

`agmilevecich/controlfinanzas` es referencia funcional, no arquitectura para copiar. Cada idea debe contrastarse con el dominio, persistencia, seguridad y arquitectura de SOFP.

## D-011 — Paneles especializados sobre un núcleo financiero común

SOFP sigue el patrón:

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

No se deben crear núcleos financieros paralelos.

## D-012 — Cuenta y Forma de Pago son conceptos distintos

Una `Cuenta` identifica dónde se produce el efecto financiero. `FormaPago` identifica cómo se realizó la operación. No toda forma de pago implica una salida inmediata de una cuenta.

## D-013 — El núcleo debe representar activos, pasivos y patrimonio

Objetivo de largo plazo: representar liquidez, inversiones, deudas, derechos de cobro y patrimonio neto.

`TOTAL ACTIVOS - TOTAL PASIVOS = PATRIMONIO NETO`

Los préstamos otorgados representan derechos de cobro. Las transferencias propias no son ingresos ni gastos.

## D-014 — Egresos sujetos a fondos disponibles

Un `EGRESO` mayor al saldo disponible se rechaza. Uno igual al saldo está permitido y deja saldo cero. La regla también se aplica a modificaciones de importe y tipo.

## D-015 — No eliminar físicamente categorías con movimientos

Una categoría referenciada por movimientos se conserva y se desactiva. La interfaz comunica la situación sin exponer directamente la excepción de integridad referencial.

## D-016 — Criterios de ControlFinanzas son roadmap hasta su implementación

Resúmenes, rankings, evolución patrimonial, vencimientos, gráficos y dashboard son candidatos hasta que tengan código, reglas, persistencia cuando corresponda y tests.

## D-017 — Gastos como panel especializado de carga

El flujo acordado es:

**Gastos → `GastoService` → `MovimientoService` → `Movimiento` `EGRESO` → `Movimientos`.**

Gastos no tiene fuente de verdad independiente.

## D-018 — FormaPago integrada al flujo de Gastos

La integración de `FormaPago` se realiza dentro del flujo funcional de Gastos. `Movimiento` conserva la forma de pago y `GastoService` la exige.

## D-019 — Tarjeta de crédito requiere obligaciones/pasivos

`TARJETA_CREDITO` se rechaza actualmente en `GastoService`. No se debe simular un egreso inmediato sobre una cuenta cuando la compra genera una obligación que se pagará posteriormente. La habilitación queda condicionada a un modelo correcto de obligaciones/pasivos.

## Actualización — 05/09/2026

La integración de `FormaPago` quedó implementada y validada. `GastosPanel` ofrece las cinco formas actuales: efectivo, transferencia, tarjeta de débito, tarjeta de crédito y QR. La tarjeta de crédito continúa temporalmente bloqueada por la decisión D-019.

Suite general informada por el usuario: **590/590**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`, `mvn test`, finalizada el **05/09/2026 13:04:09 -03:00**, duración **11:29 min**.

La rama de trabajo sigue siendo `feature/swing-shell`; `main` permanece en `a4be859` y no se realizó merge.
