# SOFP — Contexto para continuar con ChatGPT

## Estado actual — 11/09/2026

La fuente de verdad es el código, los tests y los commits actuales. `docs/` es documentación auxiliar y ante contradicción prevalecen código y tests.

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

Último commit funcional: `34eb4cc` — `config: conectar SOFP a H2 por TCP`.

Antes de la actualización documental, GitHub verificó **495 commits adelante y 0 atrás** respecto de `main`.

## Último bloque cerrado

### H2 persistente por TCP

La aplicación usa:

`jdbc:h2:tcp://localhost/./database/sofp`

H2 Server corre en `localhost:9092` y H2 Console en `localhost:8082`. El usuario verificó que SOFP y H2 Console funcionan simultáneamente sobre la misma base.

Los tests usan su propio `persistence.xml` con H2 en memoria.

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
- las cuotas solicitadas se generan automáticamente al registrar el gasto;
- obligación conserva moneda del movimiento de origen;
- crédito disponible inicial se calcula por moneda, sin conversión implícita;
- transferencias propias no son ingresos ni gastos;
- UI no duplica reglas de negocio.

## Tests

Suite general más reciente: `mvn test` → **687/687**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, ejecutada el **11/09/2026 13:14:41 -03:00**, duración 18:31 min.

## Próximo paso

1. Implementar selección explícita de tarjeta de crédito en `GastosPanel` cuando la forma de pago sea `TARJETA_CREDITO`.
2. Cubrir que solo aparezcan tarjetas activas y que la tarjeta seleccionada sea la cuenta usada por `GastoService`.
3. Integrar `CicloFacturacion` con consumos y obligaciones.
4. Unificar el cálculo de saldo de tarjetas entre `MovimientoService` y `CuentaService`.
5. Profundizar pagos/liberación de crédito, pasivos/patrimonio, análisis y dashboard.

## Protocolo de nuevas sesiones

Revisar siempre: rama → últimos commits → comparación con `main` → README/docs → código → tests → último resultado → próximo paso.

No modificar `main`, no crear ramas nuevas salvo indicación explícita y no asumir resultados locales no informados.
