# SOFP — Decisiones

Este documento registra decisiones que conviene conservar durante toda la vida del proyecto.

## D-001 — El repositorio es la memoria permanente

La continuidad no dependerá de una única conversación. Código, Git, tests y documentación forman la memoria permanente, con prioridad del código y tests sobre `docs/`.

## D-002 — Desarrollo incremental por Builds

El proyecto se desarrolla en bloques pequeños y verificables. Cada Build debe tener objetivo concreto, tests y commit identificable.

## D-003 — Persistencia con JPA/Hibernate

Se utiliza Jakarta Persistence con Hibernate como implementación ORM.

## D-004 — H2 como base de datos de desarrollo

H2 se utiliza para desarrollo y pruebas de persistencia.

## D-005 — BigDecimal para importes

Los valores monetarios se representan con `BigDecimal`, evitando `float`/`double`.

## D-006 — Dominio antes de interfaz

El dominio, sus reglas y persistencia se construyen antes de avanzar fuertemente sobre la interfaz.

## D-007 — Tests como condición de avance

Una funcionalidad no se considera cerrada hasta verificar sus tests correspondientes y mantener las pruebas anteriores funcionando.

## D-008 — Sistema de continuidad documental

Se mantienen documentos de estado, contexto, Builds, tests y pendientes para poder continuar sin depender de una conversación concreta.

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

Una categoría referenciada por movimientos se conserva y se desactiva.

## D-016 — Criterios de ControlFinanzas son roadmap hasta su implementación

Resúmenes, rankings, evolución patrimonial, vencimientos, gráficos y dashboard son candidatos hasta que tengan código, reglas, persistencia cuando corresponda y tests.

## D-017 — Gastos como panel especializado de carga

El flujo acordado es:

**Gastos → `GastoService` → `MovimientoService` → `Movimiento` `EGRESO` → `Movimientos`.**

Gastos no tiene fuente de verdad independiente.

## D-018 — FormaPago integrada al flujo de Gastos

La integración de `FormaPago` se realiza dentro del flujo funcional de Gastos. `Movimiento` conserva la forma de pago y `GastoService` la exige.

## D-019 — Tarjeta de crédito genera una obligación

Una compra con `TARJETA_CREDITO` se registra como `Movimiento` de tipo `EGRESO` y `GastoService`, mediante `ObligacionService`, crea una `Obligacion` asociada al movimiento persistido. No se simula un pago inmediato sobre la cuenta.

## D-020 — Obligaciones como pasivo especializado

`Obligacion` representa actualmente el pasivo originado por una compra con tarjeta de crédito. Conserva importe original, saldo pendiente, estado, movimiento de origen y su moneda económica a través del movimiento de origen. Sus estados son `PENDIENTE`, `PARCIAL` y `PAGADA`.

Los pagos se registran mediante `ObligacionService`, que mantiene las reglas transaccionales y delega la lógica de estado en el dominio. `ObligacionesPanel` consulta obligaciones por usuario, permite pagos autorizados y refresca el estado conservando la selección.

## D-021 — Compatibilidad de constructores del shell

Los constructores existentes de `MainFrame` que no reciben `ObligacionService` deben continuar funcionando mientras no necesiten registrar operaciones que requieran obligaciones.

## D-022 — Pagos de obligaciones autorizados por usuario

La interfaz no debe registrar pagos utilizando únicamente el identificador de la obligación. El flujo debe pasar por la operación de servicio que recibe también `usuarioId` y verifica la propiedad.

## D-023 — Refresco de obligaciones conserva selección

Cuando `ObligacionesPanel` refresca la lista después de un pago, debe conservar la obligación previamente seleccionada si continúa presente.

## D-024 — Ingresos como panel especializado sobre Movimiento

Los ingresos se registran mediante:

**`IngresosPanel` → `IngresoService` → `MovimientoService` → `Movimiento` `INGRESO` → `Movimientos`.**

El panel no mantiene un registro financiero paralelo.

## D-025 — Ingresos autorizados por usuario

El registro de ingresos utiliza el `usuarioId` autorizado y servicios que validan la pertenencia de cuenta y categoría al perfil correspondiente.

## D-026 — Transferencias mediante OperacionFinanciera

Las transferencias entre cuentas propias se registran mediante `OperacionFinancieraService`, no como un ingreso o gasto independiente. Una transferencia crea una operación financiera con un `EGRESO` en la cuenta origen y un `INGRESO` en la cuenta destino.

`TransferenciasPanel` es solamente la interfaz de carga: las reglas de cuentas, perfiles, moneda, actividad, importe y autorización permanecen en el servicio central.

## D-027 — La obligación conserva la moneda económica del consumo

La moneda de una obligación es la moneda del `Movimiento` que la origina. Una compra en ARS genera una obligación en ARS y una compra en USD genera una obligación en USD.

No se realiza conversión automática de moneda al crear la obligación. La conversión entre monedas queda fuera de este flujo y deberá definirse explícitamente cuando se diseñe el pago multidivisa.

## D-028 — La UI muestra moneda explícita en obligaciones

`ObligacionesPanel` muestra el código de moneda tanto para `importeOriginal` como para `saldoPendiente`. El formato numérico se estabiliza mediante `Locale.ROOT` para que la representación de dos decimales no dependa del locale del entorno de ejecución.

## Actualización — 09/09/2026

El bloque de moneda en obligaciones quedó implementado y validado.

Commits:

- `9b92eac` — `feat: exponer moneda de la obligacion`.
- `47ced65` — `feat: mostrar moneda en obligaciones`.
- `fe0aa71` — `test: verificar moneda en obligaciones`.
- `13a68fb` — `fix: estabilizar formato de moneda en obligaciones`.

Validaciones informadas por el usuario:

- `mvn test -Dtest=ObligacionesPanelTest` → **4/4**, BUILD SUCCESS, **01:20 min**.
- `mvn test` → **642/642**, BUILD SUCCESS, **09:43 min**.
- `git diff`, `git diff --check`, `git status` → working tree limpio y rama sincronizada con `github/feature/swing-shell`.
