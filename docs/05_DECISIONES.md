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

## D-019 — Tarjeta de crédito genera una obligación

La decisión original de no simular una salida inmediata de fondos se mantiene. Una compra con `TARJETA_CREDITO` se registra como `Movimiento` de tipo `EGRESO` y `GastoService`, mediante `ObligacionService`, crea una `Obligacion` asociada al movimiento persistido.

Si `GastoService` no dispone de `ObligacionService`, el uso de `TARJETA_CREDITO` se rechaza con `IllegalStateException`. No se debe reintroducir una simulación de pago inmediato sobre la cuenta.

## D-020 — Obligaciones como pasivo especializado

`Obligacion` representa actualmente el pasivo originado por una compra con tarjeta de crédito. Conserva importe original, saldo pendiente, estado y movimiento de origen. Sus estados son `PENDIENTE`, `PARCIAL` y `PAGADA`.

Los pagos se registran mediante `ObligacionService`, que mantiene las reglas transaccionales y delega la lógica de estado en el dominio.

La UI especializada está implementada mediante `ObligacionesPanel`. Consulta obligaciones por usuario, permite registrar pagos autorizados y refresca el estado sin duplicar reglas de dominio.

## D-021 — Compatibilidad de constructores del shell

Los constructores existentes de `MainFrame` que no reciben `ObligacionService` deben continuar funcionando mientras no necesiten registrar operaciones que requieran obligaciones. Cuando el servicio está disponible, el shell utiliza la integración completa de `GastoService`.

## D-022 — Pagos de obligaciones autorizados por usuario

La interfaz no debe registrar pagos utilizando únicamente el identificador de la obligación. El flujo de usuario debe pasar por la operación de servicio que recibe también el `usuarioId` y verifica la propiedad de la obligación antes de modificarla.

## D-023 — Refresco de obligaciones conserva selección

Cuando `ObligacionesPanel` refresca la lista después de un pago, debe conservar la obligación previamente seleccionada si continúa presente. Esto evita que el refresco deshabilite el botón de pago por pérdida de selección.

## D-024 — Ingresos como panel especializado sobre Movimiento

Los ingresos se registran mediante:

**`IngresosPanel` → `IngresoService` → `MovimientoService` → `Movimiento` `INGRESO` → `Movimientos`.**

El panel no mantiene un registro financiero paralelo. Cuenta, categoría, importe, fecha y descripción son datos del formulario; la persistencia y las reglas financieras permanecen en el núcleo común.

## D-025 — Ingresos autorizados por usuario

El registro de ingresos debe utilizar el `usuarioId` autorizado y servicios que validen la pertenencia de cuenta y categoría al perfil correspondiente. La UI no debe reemplazar las reglas de autorización del servicio.

## Actualización — 08/09/2026

El bloque de Ingresos quedó implementado e integrado al shell mediante `IngresosPanel`, `IngresoService`, `MainFrame` y `SidebarPanel`.

Commits funcionales:

- `d99cc6a` — `feat: agregar formulario de ingresos`.
- `2977f36` — `test: cubrir formulario de ingresos`.
- `4e6b363` — `feat: integrar ingresos al shell`.
- `cd781a1` — `feat: agregar ingresos a la navegacion`.
- `f9339db` — `test: cubrir navegacion hacia ingresos`.

La validación focalizada fue **5/5**, la relacionada **18/18** y la suite general posterior **630/630**, todas con `BUILD SUCCESS` y sin failures, errors ni skipped.

La suite general de **630/630** fue ejecutada por el usuario el **08/09/2026 15:16:17 -03:00**, con una duración de **11:55 min**.
