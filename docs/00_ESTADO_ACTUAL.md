# SOFP — Estado actual

> Documento de continuidad. La fuente de verdad técnica es el código, los tests y los commits actuales; `docs/` es documentación auxiliar y puede quedar desactualizada.

## Estado verificado — 13/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

**Último commit verificado:** `00beeb17fcd781e039374dd5cf7f40ca60a39db4` — `fix: evitar moneda duplicada en test de integridad`.
**Último cambio funcional:** `e5fbe0f9064841e2a03a671c0bf877e1897e8289` — `fix: proteger integridad estructural de cuentas`.

Los commits posteriores al cambio funcional corresponden a adaptación/corrección de tests y documentación. La rama de trabajo continúa separada de `main`; no se realizó merge.

## Último bloque cerrado

### Integridad estructural de `Cuenta`

Se implementó la protección mínima derivada de la auditoría transversal de `Cuenta`.

`CuentaService.modificarTipoCuenta(...)` rechaza cambios de tipo cuando existen movimientos financieros y, mediante su validación específica, rechaza las transiciones genéricas hacia o desde `TARJETA_CREDITO`.

`CuentaService.modificarMoneda(...)` rechaza cambios de moneda cuando existen movimientos financieros.

La autorización por usuario se mantiene en ambas operaciones. No se introdujeron conversiones automáticas ni se alteró la posibilidad de que un movimiento de tarjeta tenga una moneda económica distinta de la moneda estructural de la cuenta.

### Cobertura agregada

Se incorporó `CuentaServiceIntegridadTest` para cubrir:

- rechazo de cambio de tipo con movimientos;
- rechazo de cambio de moneda con movimientos;
- cambio entre tipos no tarjeta sin historial;
- rechazo de transiciones hacia/desde `TARJETA_CREDITO` por la API genérica.

## Validación más reciente conocida

El usuario ejecutó la suite relacionada el 13/09/2026 y obtuvo:

- **154/154** tests;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- tiempo informado: **22:18 min**;
- finalizado a las **17:58:12 -03:00**.

La suite completa `mvn test` fue ejecutada posteriormente y obtuvo:

- **700/700** tests;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- tiempo informado: **46:17 min**;
- finalizado a las **19:00:15 -03:00**.

La validación específica inmediatamente anterior de `CuentaServiceIntegridadTest,CuentaServiceTest` obtuvo **66/66**, `BUILD SUCCESS`.

La validación final de Git informada por el usuario mostró `git syncsofp` correcto, `git diff` vacío, `git diff --check` sin salida, `git status` limpio y la rama sincronizada con GitHub y Bitbucket.

## Arquitectura funcional vigente

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

Gastos: `GastosPanel → GastoService → MovimientoService → Movimiento EGRESO`.

Ingresos: `IngresosPanel → IngresoService → MovimientoService → Movimiento INGRESO`.

Una compra con `TARJETA_CREDITO` genera movimiento de egreso + obligación + cuotas dentro de la coordinación transaccional correspondiente.

El pago de tarjeta se coordina mediante `PagoTarjetaService`: cuenta pagadora + movimiento de salida + actualización de obligación.

## Persistencia y H2

La aplicación utiliza `jdbc:h2:tcp://localhost/./database/sofp`.

H2 Server: `localhost:9092`. H2 Console: `localhost:8082`.

Los tests mantienen un `persistence.xml` independiente con H2 en memoria.

## Tarjetas, moneda y ciclos

Una tarjeta de crédito es una `Cuenta` con `TipoCuenta.TARJETA_CREDITO`, límite, cierre y vencimiento.

`Obligacion` es una relación uno-a-uno con el `Movimiento` de origen. El origen debe ser un `EGRESO` de una cuenta de tarjeta. La obligación conserva importe original, saldo pendiente, moneda, fecha y ciclo derivados del movimiento.

Los movimientos tienen moneda explícita. No existe conversión automática ARS↔USD.

El pago de tarjeta exige coincidencia de moneda entre obligación y cuenta pagadora.

## Integridad de `Cuenta` — cierre 13/09/2026

La auditoría transversal previa identificó tres riesgos principales: mutabilidad de tipo con historial, mutabilidad de moneda con historial y transiciones genéricas incompletas hacia/desde tarjeta. La implementación mínima ya quedó aplicada y validada.

Reglas vigentes:

1. una cuenta con movimientos financieros no puede cambiar de tipo;
2. una cuenta con movimientos financieros no puede cambiar de moneda;
3. la API genérica de cambio de tipo no permite transiciones hacia o desde `TARJETA_CREDITO`;
4. las operaciones continúan verificando pertenencia/autorización del usuario;
5. no se impone igualdad universal entre moneda de cuenta y moneda de movimiento, preservando consumos de tarjeta en moneda económica extranjera;
6. las transferencias entre cuentas continúan exigiendo misma moneda.

La política más avanzada para crear/configurar una tarjeta y para futuras migraciones específicas de datos de crédito queda separada de esta protección estructural. No se limpian ni migran datos implícitamente.

## Estado de pendientes reales

### P0 — Cerrado

API pública de obligaciones y autorización: el overload de `ObligacionService.registrarPago` sin `usuarioId` fue eliminado.

### P1 — Cerrado: integridad estructural de `Cuenta`

La protección de tipo/moneda y las transiciones genéricas hacia/desde tarjeta quedó implementada y cubierta por tests. Suite relacionada: **154/154**. Suite completa: **700/700**.

### P1 — Ciclo de facturación aplicado al pago

Definir y probar comportamiento respecto de vencimiento, mora, gracia, días no hábiles y orden temporal.

### P1 — Multidivisa de tarjetas

Definir el tratamiento definitivo del límite de una tarjeta frente a consumos en monedas diferentes.

### P1 — Financiación avanzada

Intereses, CFT/costo financiero, cuotas variables, adelantos, refinanciación, anulaciones/reversiones y ajustes.

### P2 — UI específica de tarjetas

Límite/disponible, consumos, ciclos, cierres, vencimientos, deuda y pagos reales.

### P2 — Pasivos, patrimonio y análisis

Ampliar pasivos/patrimonio neto y luego histórico, vencimientos, resúmenes y dashboard.

### P2/P3 — Gestión de entidades financieras

No existe todavía un panel específico para registrar/gestionar entidades financieras.

### P3 — Pulido de consola

Prioridad baja.

## Protocolo de continuidad

Ante una nueva sesión: rama → últimos commits → comparación con `main` → README/docs → código → tests → último resultado conocido → próximo paso.

Prioridad: **código → tests → commits → `main` → documentación → conversaciones anteriores**.

No modificar `main` automáticamente. No asumir resultados locales no informados. No considerar terminada una tarea solo porque compila.
