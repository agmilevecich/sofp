# SOFP — Decisiones

Este documento registra decisiones permanentes del proyecto.

## D-001 — El repositorio es la memoria permanente
Código, Git, tests y documentación forman la memoria permanente, con prioridad del código y tests sobre `docs/`.

## D-002 — Desarrollo incremental por Builds
Cada bloque debe tener objetivo concreto, tests y commit identificable.

## D-003 — Persistencia con JPA/Hibernate
Se utiliza Jakarta Persistence con Hibernate como ORM.

## D-004 — H2 como base de desarrollo y tests
H2 se utiliza para desarrollo y pruebas de persistencia.

## D-005 — BigDecimal para importes
Los valores monetarios usan `BigDecimal`.

## D-006 — Dominio antes de interfaz
Las reglas de dominio y persistencia se construyen antes de avanzar fuertemente sobre la interfaz.

## D-007 — Tests como condición de avance
Una funcionalidad no se considera cerrada hasta verificar sus tests y mantener la suite previa funcionando.

## D-008 — Continuidad documental
Se mantienen estado, contexto, Builds, tests, decisiones e historial para continuar sin depender de una conversación.

## D-009 — Transferencias no son TipoMovimiento
Una transferencia propia produce `EGRESO` en origen e `INGRESO` en destino y se coordina mediante `OperacionFinanciera`.

## D-010 — ControlFinanzas como banco de ideas
Es referencia funcional, no arquitectura para copiar.

## D-011 — Paneles especializados sobre núcleo común
Patrón: **paneles especializados → servicios específicos → núcleo financiero basado en `Movimiento`**.

## D-012 — Cuenta y FormaPago son conceptos distintos
`Cuenta` identifica dónde se produce el efecto financiero; `FormaPago` cómo se realizó la operación.

## D-013 — Activos, pasivos y patrimonio
Objetivo: representar liquidez, inversiones, deudas, derechos de cobro y patrimonio neto. `TOTAL ACTIVOS - TOTAL PASIVOS = PATRIMONIO NETO`.

## D-014 — Egresos sujetos a fondos disponibles
Un egreso mayor al saldo se rechaza; uno igual se permite y deja saldo cero. También aplica a modificaciones.

## D-015 — Categorías con movimientos no se eliminan físicamente
Se conservan y se desactivan.

## D-016 — Roadmap no equivale a implementación
Resúmenes, rankings, evolución, vencimientos, gráficos y dashboard son candidatos hasta tener código y tests.

## D-017 — Gastos sobre Movimiento
`GastosPanel → GastoService → MovimientoService → Movimiento EGRESO`.

## D-018 — FormaPago integrada a Gastos
`Movimiento` conserva la forma de pago y `GastoService` la exige.

## D-019 — Tarjeta de crédito genera obligación
Una compra con `TARJETA_CREDITO` es un `Movimiento EGRESO` y genera `Obligacion`; no es un pago inmediato de la cuenta.

## D-020 — Obligación como pasivo especializado
Conserva importe, saldo, estado, movimiento de origen y moneda económica. Estados: `PENDIENTE`, `PARCIAL`, `PAGADA`.

## D-021 — Compatibilidad de constructores del shell
Los constructores existentes de `MainFrame` sin `ObligacionService` deben seguir funcionando mientras no requieran obligaciones.

## D-022 — Pagos autorizados por usuario
El pago debe pasar por servicio con `usuarioId` y verificación de pertenencia.

## D-023 — Refresco de obligaciones conserva selección
El refresco posterior a un pago conserva la selección si la obligación sigue presente.

## D-024 — Ingresos sobre Movimiento
`IngresosPanel → IngresoService → MovimientoService → Movimiento INGRESO`.

## D-025 — Ingresos autorizados
Cuenta y categoría deben pertenecer al perfil autorizado.

## D-026 — Transferencias mediante OperacionFinanciera
Las transferencias propias se coordinan mediante servicio central y no son ingreso/gasto independiente.

## D-027 — La obligación conserva moneda del consumo
No hay conversión automática al crear la obligación.

## D-028 — UI muestra moneda explícita
`ObligacionesPanel` muestra moneda en importe original y saldo pendiente; el formato usa `Locale.ROOT`.

## D-029 — Crédito disponible inicial de tarjetas
El criterio actual es `límite de crédito − consumos de tarjeta pendientes en la moneda de la tarjeta`. No se realizan conversiones implícitas entre monedas. Esta decisión es el primer criterio funcional y no cierra todavía el tratamiento multidivisa definitivo del límite.

## D-030 — Ciclo de facturación como objeto de dominio calculado
`CicloFacturacion` no es entidad persistente. `Cuenta.calcularCicloFacturacion(LocalDate)` calcula inicio, cierre y vencimiento, ajustando días inexistentes al último día real del mes. La lógica pertenece al dominio, no a Swing.

## D-031 — Aislamiento JPA de tests
El contexto JPA/H2 de tests debe quedar aislado por hilo y cerrado explícitamente al terminar tests que gestionen su propio ciclo de vida. Esto evita contaminación entre clases de test.

## D-032 — Cuotas generadas por el flujo de gasto
Cuando un gasto con `TARJETA_CREDITO` se registra con una cantidad de cuotas, `GastoService` es responsable de generar las cuotas dentro de la transacción de la obligación. Los tests deben utilizar ese flujo productivo y no generar manualmente cuotas ya creadas.

## Actualización — 10/09/2026

El bloque de crédito/límite, ciclos básicos, aislamiento JPA y cuotas iniciales está implementado y validado. La suite general conocida es **671/671** y la suite relacionada con cuotas es **32/32**.
