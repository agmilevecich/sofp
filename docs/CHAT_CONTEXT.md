# SOFP — Contexto para continuar con ChatGPT

## Estado actual — 11/09/2026

La fuente de verdad es el código, los tests y los commits actuales. `docs/` es documentación auxiliar y ante contradicción prevalecen código y tests.

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

Último commit funcional: `44661fb` — `test: cubrir cuotas al cruzar fin de año`.

GitHub verifica 514 commits adelante y 0 atrás respecto de `main`. No se realizó merge.

## Último bloque cerrado

### Selección explícita de tarjeta de crédito en Gastos

Cuando se selecciona `FormaPago.TARJETA_CREDITO`, `GastosPanel` filtra las cuentas para mostrar únicamente cuentas activas con `TipoCuenta.TARJETA_CREDITO`. La tarjeta elegida se utiliza como `Cuenta` al registrar el gasto.

## Último cambio de cobertura

`ObligacionCuotasTest` cubre el cruce de fin de año: compra `2026-12-16`, tres cuotas y ciclos/vencimientos hasta abril de 2027.

## Persistencia y H2

La aplicación usa:

`jdbc:h2:tcp://localhost/./database/sofp`

H2 Server corre en `localhost:9092` y H2 Console en `localhost:8082`. Los tests usan su propio `persistence.xml` con H2 en memoria.

## Arquitectura

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

Gastos → `GastoService` → `MovimientoService` → `Movimiento EGRESO`.

Ingresos → `IngresoService` → `MovimientoService` → `Movimiento INGRESO`.

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

Suite general más reciente: `mvn test` → **689/689**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, ejecutada el **11/09/2026 20:11:17 -03:00**, duración 10:22 min.

Tests relacionados del bloque de obligaciones/cuotas: **49/49**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.

## Próximo paso

1. Auditar los pendientes documentados contra el código y los 689 tests actuales.
2. Si sigue siendo necesario, integrar `CicloFacturacion` con consumos, obligaciones y pagos mediante el cambio mínimo.
3. Cubrir reglas de ciclo, fechas de cierre/vencimiento, relaciones y casos límite.
4. Ejecutar tests específicos, relacionados y suite general cuando corresponda.

## Protocolo de nuevas sesiones

Revisar siempre: rama → últimos commits → comparación con `main` → README/docs → código → tests → último resultado → próximo paso.

No modificar `main`, no crear ramas nuevas salvo indicación explícita y no asumir resultados locales no informados.
