# SOFP — Contexto para continuar con ChatGPT

## Estado actual — 10/09/2026

La fuente de verdad es el código, los tests y los commits actuales. `docs/` es documentación auxiliar y ante contradicción prevalecen código y tests.

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell` → `548063914ad7a3fe6ae028dad606aad57f7ca42e`.
**Comparación:** 443 commits adelante, 0 atrás.

El último commit es documental (`5480639`). El último cambio funcional es `46290786` — cierre del contexto JPA de `PosicionActivoServiceTest`.

## Último bloque cerrado

Crédito disponible y límite de tarjetas, conservación de consumos sin obligación y aislamiento JPA/H2 de la suite.

Regla actual:

`crédito disponible = límite de crédito − consumos de tarjeta pendientes en la moneda de la tarjeta`

No hay conversiones implícitas entre monedas.

## Ciclos de facturación — YA IMPLEMENTADOS

`CicloFacturacion` es un objeto de dominio no persistente. `Cuenta.calcularCicloFacturacion(LocalDate)` calcula inicio, cierre y vencimiento.

Casos cubiertos por `CicloFacturacionTest` (**9 tests**): consumo antes del cierre, día exacto de cierre, día posterior, vencimiento posterior al cierre, meses cortos, febrero, último día real y cambio de año, además de fecha nula.

No volver a plantear ciclos como implementación desde cero. El siguiente trabajo es integrar esta lógica con consumos/obligaciones y revisar sus interacciones con pagos.

## Arquitectura

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

Gastos → `GastoService` → `MovimientoService` → `Movimiento EGRESO`.

Ingresos → `IngresoService` → `MovimientoService` → `Movimiento INGRESO`.

Transferencias → `OperacionFinancieraService` → `OperacionFinanciera` con EGRESO/INGRESO.

## Reglas vigentes

- egreso superior al saldo: rechazado;
- egreso igual al saldo: permitido;
- modificaciones respetan fondos disponibles;
- categorías con movimientos se conservan y desactivan;
- cuenta y forma de pago son conceptos distintos;
- tarjeta de crédito genera movimiento y obligación;
- obligación conserva moneda del movimiento de origen;
- crédito disponible inicial se calcula por moneda, sin conversión implícita;
- transferencias propias no son ingresos ni gastos;
- UI no duplica reglas de negocio.

## Tests

Suite general más reciente: `mvn test` → **664/664**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, ejecutada el **09/09/2026 21:58:35 -03:00**, 10:04 min.

`TarjetaCreditoPagoCreditoTest` → **5/5** en validación focalizada conocida.
`CicloFacturacionTest` → **9 tests** incluidos en la suite general.

## Estado local conocido

Después de `git syncsofp`, el usuario informó working tree sin cambios versionados, `git diff --check` sin salida y únicamente `surefire-debug.txt` sin rastrear. No agregar ese archivo al repositorio.

## Próximo paso

1. Integrar `CicloFacturacion` con consumos y obligaciones.
2. Unificar el cálculo de saldo de tarjetas entre `MovimientoService` y `CuentaService`.
3. Profundizar pagos/liberación de crédito y reglas multidivisa explícitas.
4. Cuotas y financiación.
5. UI específica de tarjetas.
6. Pasivos/patrimonio, análisis y dashboard.

## Protocolo de nuevas sesiones

Revisar siempre: rama → últimos commits → comparación con `main` → README/docs → código → tests → último resultado → próximo paso.

No modificar `main`, no crear ramas nuevas salvo indicación explícita y no asumir resultados locales no informados.
