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

- `PagoTarjetaServiceTest,ObligacionServiceTest,MovimientoServiceTest,ObligacionesPanelTest,ObligacionesPanelPagoTarjetaTest,MainFrameObligacionesTest`: **69/69**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`; durante el cierre apareció además un warning de Surefire por demora en la terminación de la JVM, sin convertir el proceso en fallo.
- tests específicos de `ObligacionesPanel` y pago de tarjeta: **6/6**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.
- `ObligacionServiceTest`: **9/9**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.

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

### Resultado

No se realizó ningún cambio de código durante esta auditoría. El objetivo fue determinar la regla mínima de integridad antes de implementar.

`Cuenta` relaciona obligatoriamente perfil financiero, institución financiera y moneda; `tipoCuenta` y `moneda` son mutables mediante métodos de dominio. `CuentaService` expone operaciones autorizadas para modificar ambos atributos. Actualmente esas operaciones no consultan si existen movimientos históricos. fileciteturn170file0L2-L2

Se identificaron cuatro puntos relevantes:

1. **Tipo de cuenta con historial:** cambiar el tipo después de existir movimientos puede cambiar la semántica de la cuenta histórica. La protección todavía no existe.
2. **Moneda con historial:** cambiar la moneda después de existir movimientos puede dejar una cuenta cuya moneda actual no representa la moneda de sus movimientos históricos. La protección todavía no existe.
3. **Cambio hacia/desde tarjeta:** `cambiarTipoCuenta(TARJETA_CREDITO)` no configura automáticamente límite, cierre ni vencimiento; inversamente, cambiar una tarjeta a otro tipo no limpia esos datos. Por lo tanto, la mutabilidad actual permite estados de cuenta que requieren una regla explícita antes de habilitar el cambio. fileciteturn170file0L2-L2
4. **Moneda explícita de movimientos:** `Movimiento` guarda su propia moneda y `GastoService` permite registrar gastos con una moneda económica distinta de la cuenta, precisamente para soportar consumos de tarjeta en moneda extranjera. Por eso no corresponde imponer ciegamente `movimiento.moneda == cuenta.moneda` para todos los movimientos. fileciteturn171file0L2-L2 fileciteturn191file0L2-L2

`OperacionFinancieraService` sí exige misma moneda para transferencias entre cuentas y valida que ambas cuentas pertenezcan al usuario y estén activas. fileciteturn182file0L2-L2

`CuentaRepository` no contiene reglas de negocio sobre tipo/moneda; su responsabilidad actual es persistencia y consulta. fileciteturn185file0L2-L2

### Regla mínima recomendada como base de implementación

Antes de permitir un cambio de tipo o moneda, el servicio debe determinar si la cuenta tiene historial financiero. Si existe historial, el cambio estructural debe rechazarse salvo que exista una regla de migración explícita y testeada. Para una cuenta sin historial, el cambio puede seguir siendo permitido, pero el cambio de tipo hacia `TARJETA_CREDITO` debe preservar la invariancia de sus datos obligatorios de crédito y el cambio desde tarjeta debe tener una política definida para esos datos.

La auditoría no implementa todavía esta regla porque el tratamiento exacto de datos de crédito al cambiar de tipo requiere decidir el comportamiento de negocio. La conclusión queda documentada para que el próximo cambio sea mínimo y no introduzca conversiones o migraciones implícitas.

## Estado de pendientes reales

### P0 — Cerrado: API pública de obligaciones y autorización

El overload público de `ObligacionService.registrarPago` sin `usuarioId` fue eliminado. El registro público de pagos exige usuario y valida el perfil propietario de la obligación.

### P1 — Integridad de `Cuenta` — auditoría terminada, implementación pendiente

La auditoría técnica quedó cerrada. El siguiente cambio deberá cubrir, como mínimo:

- bloqueo de cambio de tipo con historial;
- bloqueo de cambio de moneda con historial;
- coherencia de datos de crédito al cambiar hacia/desde `TARJETA_CREDITO`;
- tests de persistencia, autorización, cuenta sin historial y cuenta con movimientos;
- no confundir la moneda económica de un consumo de tarjeta con la moneda estructural de la cuenta.

### P1 — Ciclo de facturación aplicado al pago

Definir y probar comportamiento respecto de vencimiento, mora, gracia, días no hábiles y orden temporal. No inventar reglas antes de decidirlas.

### P1 — Multidivisa de tarjetas

Definir el tratamiento definitivo del límite de una tarjeta frente a consumos en monedas diferentes. No introducir conversiones implícitas.

### P1 — Financiación avanzada

Intereses, CFT/costo financiero, cuotas variables, adelantos, refinanciación, anulaciones/reversiones y ajustes.

### P2 — UI específica de tarjetas

Después de estabilizar dominio y servicios: límite/disponible, consumos, ciclos, cierres, vencimientos, deuda y pagos reales.

### P2 — Pasivos, patrimonio y análisis

Ampliar pasivos/patrimonio neto y luego histórico, vencimientos, resúmenes y dashboard.

### P2/P3 — Gestión de entidades financieras

No existe todavía un panel específico para registrar/gestionar entidades financieras. Queda pendiente definir e implementar cuando corresponda.

### P3 — Pulido de consola

Prioridad baja. No debe interferir con reglas financieras ni servicios.

## Qué ya no debe figurar como pendiente independiente

- integridad del movimiento origen de obligación: implementada y testeada;
- integración básica de `CicloFacturacion` con consumos/obligaciones: implementada;
- generación automática de cuotas: implementada;
- cuotas que cruzan fin de año: testeadas;
- atomicidad básica de compra con tarjeta: implementada y testeada;
- pago coordinado de tarjeta en servicio: implementado y testeado;
- pago real de tarjeta desde la UI: implementado y testeado;
- integración de `PagoTarjetaService` en `Main`/`MainFrame`: implementada;
- autorización del registro de pagos en `ObligacionService`: cerrada y testeada;
- JAR ejecutable: implementado y verificado manualmente.

## Protocolo de continuidad y sincronización documental

La documentación de continuidad se trabaja sobre **la rama activa de desarrollo**. La antigua rama `docs/continuidad-sofp` no forma parte de las ramas activas del repositorio; la continuidad se conserva en los documentos versionados dentro de la rama de trabajo.

Reglas:

1. Los cambios de código y las actualizaciones de continuidad se realizan sobre la rama de trabajo activa. No modificar `main` automáticamente.
2. Al cerrar una etapa importante, actualizar los documentos de continuidad en la rama activa junto con el estado real de código, tests y commits.
3. Si se crea temporalmente una rama documental, debe considerarse auxiliar y eliminarse al finalizar su propósito.
4. Evitar merge commits únicamente para integrar documentación de continuidad.
5. Antes de una nueva sesión, reconstruir el estado desde GitHub priorizando: código actual → tests → commits → `main` → documentación → conversaciones anteriores.
6. Si la documentación contradice al código o los tests, prevalecen siempre código y tests.

## Protocolo de continuidad

Ante una nueva sesión: rama → últimos commits → comparación con `main` → README/docs → código → tests → último resultado conocido → próximo paso.

Prioridad: **código → tests → commits → `main` → documentación → conversaciones anteriores**.

No modificar `main` automáticamente. No asumir resultados locales no informados. No considerar terminada una tarea solo porque compila.
