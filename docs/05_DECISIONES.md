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

Por lo tanto, `TRANSFERENCIA` no debe incorporarse al enum `TipoMovimiento`. El enum representa el efecto individual de un movimiento sobre una cuenta.

La transferencia se modela mediante `OperacionFinanciera`, que agrupa y relaciona los movimientos que representan sus efectos.

## D-010 — ControlFinanzas como banco de ideas

`agmilevecich/controlfinanzas` se utilizará como referencia funcional para descubrir capacidades, reglas y soluciones útiles. No se copiará su arquitectura automáticamente. Cada idea deberá contrastarse con el dominio, persistencia, seguridad y arquitectura actuales de SOFP.

## D-011 — Paneles especializados sobre un núcleo financiero común

SOFP seguirá el patrón:

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

Los paneles pueden especializarse en gastos, ingresos, transferencias, inversiones, préstamos/deudas, pagos de tarjeta, historial y dashboard, pero no deben crear núcleos financieros paralelos.

## D-012 — Cuenta y Forma de Pago son conceptos distintos

Una `Cuenta` identifica dónde se produce el efecto financiero. La `FormaPago` identifica cómo se realizó la operación.

Las formas previstas incluyen tarjeta de crédito, tarjeta de débito, QR, transferencia y efectivo.

No debe asumirse que toda forma de pago implica una salida inmediata de una cuenta. En particular, una compra con tarjeta de crédito puede generar una obligación/pasivo cuyo pago se producirá posteriormente.

## D-013 — El núcleo debe representar activos, pasivos y patrimonio

El objetivo funcional de largo plazo es que SOFP pueda responder cuánto dinero disponible existe, dónde está, cuánto valen las inversiones, qué se debe, qué se debe cobrar y cuál es el patrimonio neto.

Regla conceptual:

`TOTAL ACTIVOS - TOTAL PASIVOS = PATRIMONIO NETO`

Los préstamos otorgados deben mantenerse como derechos de cobro. Las transferencias entre cuentas propias no deben contabilizarse como ingreso ni gasto.

## D-014 — Egresos sujetos a fondos disponibles

Un `EGRESO` no debe superar el saldo disponible de la cuenta. Un egreso igual al saldo disponible es válido y deja saldo cero.

La regla se implementó en `MovimientoService` y se validó mediante pruebas específicas y suite general. También se contempla al modificar importe o tipo de movimientos existentes para evitar saldos inválidos.

Implementación: `5dd8372` — `fix: validar fondos disponibles en movimientos`.

Validación específica: `MovimientoFondosInsuficientesTest` **6/6**.

Suite general posterior: **577/577**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`.

## D-015 — No eliminar físicamente categorías con movimientos

Una categoría que ya esté referenciada por movimientos debe conservarse para proteger el historial financiero. El comportamiento previsto es impedir el borrado físico y permitir su desactivación cuando corresponda.

La interfaz debe traducir esta regla a un mensaje comprensible y no exponer una excepción de integridad referencial de Hibernate.

Esta decisión continúa pendiente de implementación.

## D-016 — Criterios de ControlFinanzas son roadmap hasta su implementación

Los resúmenes mensuales/históricos, rankings por categoría, evolución patrimonial, vencimientos, gráficos, dashboard y otras capacidades detectadas en ControlFinanzas quedan como candidatos de evolución de SOFP.

Una capacidad no se considerará implementada por estar documentada: requiere código, reglas de negocio, persistencia cuando corresponda y tests.
