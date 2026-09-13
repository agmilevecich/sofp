# SOFP — Estado actual

> Documento de continuidad. La fuente de verdad técnica es el código, los tests y los commits actuales; `docs/` es documentación auxiliar y puede quedar desactualizada.

## Estado verificado — 13/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

**Último commit funcional previo a esta auditoría:** `2813fa34c953f4f6408903c1e3b4fc2e7f3b58c5` — `test: adaptar pagos de obligaciones a usuario autorizado`.

Los commits posteriores corresponden a documentación de continuidad. La rama de trabajo continúa separada de `main`; no se realizó merge.

## Último bloque cerrado

### Autorización de pagos en `ObligacionService`

Se eliminó de la API pública el overload `registrarPago(Long obligacionId, BigDecimal importe)` que permitía registrar pagos sin identificar al usuario.

El registro de pagos expuesto por `ObligacionService` exige ahora `usuarioId` y mantiene la validación de pertenencia de la obligación al perfil del usuario antes de modificarla.

## Validación más reciente conocida

El usuario ejecutó `mvn test` el 13/09/2026 y obtuvo:

- **696/696** tests;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- tiempo informado: **21:14 min**.

Validaciones relacionadas informadas:

- suite de obligaciones/pagos/UI: **69/69**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`; durante el cierre apareció un warning de Surefire por demora en la terminación de la JVM, sin convertir el proceso en fallo;
- tests específicos de UI y pago de tarjeta: **6/6**, `BUILD SUCCESS`;
- `ObligacionServiceTest`: **9/9**, `BUILD SUCCESS`.

El usuario también informó que `git syncsofp`, `git diff`, `git diff --check` y `git status` terminaron correctamente; el árbol de trabajo quedó limpio y sincronizado.

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

## Auditoría transversal de integridad de `Cuenta` — cierre 13/09/2026

Se revisaron el agregado `Cuenta`, `CuentaService`, `CuentaRepository`, `TipoCuenta`, `Moneda`, `Movimiento`, `MovimientoRepository`, `MovimientoService`, `GastoService`, `OperacionFinanciera`, `MovimientoActivo` y la cobertura relevante de `CuentaTest`, `CuentaServiceTest`, `MovimientoServiceTest` y `OperacionFinancieraServiceTest`.

Resultado: auditoría terminada sin cambios de código.

Hallazgos:

1. Cambio de tipo con historial: actualmente no existe bloqueo y puede alterar la semántica histórica.
2. Cambio de moneda con historial: actualmente no existe bloqueo y puede dejar una moneda estructural que no represente correctamente el histórico.
3. Cambio hacia/desde tarjeta: el método actual puede dejar una tarjeta sin límite/cierre/vencimiento configurados o conservar datos de crédito sin política al salir de tarjeta.
4. Moneda explícita de movimientos: no debe imponerse igualdad universal con la cuenta porque los consumos de tarjeta pueden tener moneda económica distinta.
5. Transferencias: `OperacionFinancieraService` ya exige misma moneda entre cuenta origen y destino.
6. Persistencia: `CuentaRepository` no contiene reglas de negocio sobre tipo o moneda.

### Regla mínima derivada

Si existe historial financiero, el cambio estructural de tipo o moneda debe rechazarse salvo que exista una migración de negocio explícita y testeada. Sin historial, el cambio puede evaluarse, pero las transiciones hacia/desde `TARJETA_CREDITO` deben preservar invariantes de sus datos de crédito.

La auditoría no implementó la regla porque la política exacta de los datos de crédito al cambiar de tipo todavía requiere una decisión explícita. No se deben limpiar ni migrar datos implícitamente.

## Estado de pendientes reales

### P0 — Cerrado: API pública de obligaciones y autorización

El overload público de `ObligacionService.registrarPago` sin `usuarioId` fue eliminado.

### P1 — Integridad de `Cuenta` — auditoría terminada, implementación pendiente

El siguiente cambio deberá cubrir:

- bloqueo de cambio de tipo con historial;
- bloqueo de cambio de moneda con historial;
- coherencia de datos de crédito al cambiar hacia/desde `TARJETA_CREDITO`;
- tests de persistencia, autorización, cuenta sin historial y cuenta con movimientos;
- conservación del caso válido de consumo de tarjeta en moneda económica extranjera.

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
