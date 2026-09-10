# SOFP — Estado actual

> Documento de continuidad. La fuente de verdad técnica es el código, los tests y los commits actuales; `docs/` es documentación auxiliar.

## Estado verificado — 10/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell` → `548063914ad7a3fe6ae028dad606aad57f7ca42e`.

La comparación verificada en GitHub indica **443 commits adelante y 0 atrás** respecto de `main`. No se realizó merge a `main`.

El commit `5480639` es documental; el último cambio funcional relevante es `46290786` — `fix: cerrar contexto JPA de PosicionActivoServiceTest`.

## Último bloque funcional cerrado

### Crédito disponible, límite de tarjeta y aislamiento JPA de tests

Está implementado y validado el criterio inicial:

`crédito disponible = límite de crédito − consumos de tarjeta pendientes en la moneda de la tarjeta`

No se realizan conversiones implícitas entre monedas.

También se valida el límite al registrar consumos con `TARJETA_CREDITO`, al modificar importe/tipo y se excluyen esos consumos del saldo monetario de la cuenta. Los consumos sin obligación se conservan para el cálculo del crédito sin doble contabilización.

El aislamiento de tests quedó reforzado mediante `JpaTestManager` por hilo y cierre explícito del `EntityManagerFactory` en `PosicionActivoServiceTest`.

## Ciclos de facturación

`CicloFacturacion` ya existe como objeto de dominio no persistente y `Cuenta.calcularCicloFacturacion(LocalDate)` ya está implementado.

La lógica actual:

- asigna el consumo al ciclo cuyo cierre corresponde;
- considera el mismo día de cierre dentro del ciclo que cierra ese día;
- envía el consumo posterior al cierre al ciclo siguiente;
- ajusta cierre y vencimiento al último día real del mes;
- resuelve correctamente cambios de año;
- exige fecha de consumo no nula.

`CicloFacturacionTest` cubre estos casos y actualmente contiene **9 tests**.

Esto significa que ciclos y vencimientos **no deben documentarse como pendientes de implementación**. Lo pendiente es su integración con consumos, obligaciones, pagos y UI cuando corresponda.

## Arquitectura funcional

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

`Movimientos` es el historial financiero común y consolidado, no una segunda fuente de verdad.

Gastos: `GastosPanel → GastoService → MovimientoService → Movimiento EGRESO`.

Ingresos: `IngresosPanel → IngresoService → MovimientoService → Movimiento INGRESO`.

Transferencias: `TransferenciasPanel → OperacionFinancieraService → OperacionFinanciera`, con `EGRESO` en origen e `INGRESO` en destino.

## Moneda, obligaciones y tarjetas

Los movimientos admiten moneda explícita. Una obligación conserva la moneda económica del movimiento de origen. No se convierte automáticamente ARS↔USD al crear la obligación.

Las compras con tarjeta de crédito generan obligaciones. Las obligaciones tienen estados `PENDIENTE`, `PARCIAL` y `PAGADA` y pagos autorizados por usuario.

`ObligacionesPanel` muestra importe y saldo pendiente con código de moneda y formato decimal estable mediante `Locale.ROOT`.

## Reglas financieras vigentes

- `EGRESO` superior al saldo disponible: rechazado.
- `EGRESO` igual al saldo disponible: permitido y deja saldo cero.
- Las modificaciones de importe y tipo respetan fondos disponibles.
- Categorías con movimientos se conservan y se desactivan en lugar de eliminarse físicamente.
- Cuenta y forma de pago son conceptos distintos.
- Una compra con `TARJETA_CREDITO` genera un movimiento de egreso y una obligación.
- El crédito disponible se calcula inicialmente por moneda de la tarjeta, sin conversión implícita.
- Transferencias propias no son ingresos ni gastos.
- La UI no debe duplicar reglas financieras.

## Validación más reciente conocida

El usuario ejecutó `mvn test` el **09/09/2026 21:58:35 -03:00**:

- **664/664** tests;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración **10:04 min**.

También se validaron previamente `TarjetaCreditoPagoCreditoTest` **5/5** y baterías relacionadas sin fallos.

## Estado local informado

Después de `git syncsofp`, el usuario informó:

- rama `feature/swing-shell` sincronizada con `github/feature/swing-shell`;
- `git diff` sin cambios versionados;
- `git diff --check` sin salida;
- único archivo no rastreado: `surefire-debug.txt`.

`su​refire-debug.txt` es un artefacto local de diagnóstico y no debe agregarse al repositorio.

## Próximo paso real

Antes de avanzar sobre UI específica de tarjetas, revisar e integrar el comportamiento ya existente de `CicloFacturacion` con el flujo de consumos/obligaciones y revisar la inconsistencia pendiente del cálculo de saldo entre `MovimientoService` y `CuentaService` para tarjetas.

Después: pagos de tarjeta más completos, cuotas/financiación, UI específica de tarjetas, pasivos/patrimonio y dashboard.

No hacer merge a `main` automáticamente.

## Protocolo de continuidad

Ante una nueva sesión: rama → últimos commits → comparación con `main` → README/documentación → código → tests → último resultado conocido → próximo paso.

Prioridad: **código → tests → commits → `main` → documentación → conversaciones anteriores**.
