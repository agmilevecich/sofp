# SOFP — Continuidad 2026-09-11

## Estado verificado

- Rama de trabajo: `feature/swing-shell`
- `main`: `a4be85913847200cb70976d5266d9cbba10b3100`
- Estado antes de esta actualización: `feature/swing-shell` en `34eb4cc`, 495 commits adelante y 0 atrás respecto de `main`.
- Último commit funcional: `34eb4cc` — `config: conectar SOFP a H2 por TCP`.
- No se realizó merge a `main`.

## H2 y persistencia

La aplicación SOFP utiliza H2 mediante servidor TCP:

`jdbc:h2:tcp://localhost/./database/sofp`

El servidor H2 se ejecuta en el puerto `9092`. La H2 Console se ejecuta en `localhost:8082` y se conecta mediante:

`jdbc:h2:tcp://localhost/./database/sofp`

El usuario verificó manualmente que SOFP y H2 Console funcionan simultáneamente sobre la misma base persistente.

Los tests mantienen su propio `persistence.xml` con H2 en memoria y no dependen del servidor TCP de desarrollo.

## Última validación

El usuario ejecutó `mvn test` el **11/09/2026 13:14:41 -03:00**:

- **687/687** tests;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración **18:31 min**.

También se verificó manualmente el funcionamiento simultáneo de la aplicación y H2 Console.

## Estado funcional

El shell Swing integra Inicio, Cuentas, Categorías, Ingresos, Gastos, Movimientos, Inversiones, Reportes, Obligaciones y Transferencias.

`GastoService` registra gastos como `Movimiento EGRESO`. Una compra con `FormaPago.TARJETA_CREDITO` genera una `Obligacion` y las cuotas solicitadas se generan automáticamente dentro de la transacción.

Las tarjetas de crédito son `Cuenta` con `TipoCuenta.TARJETA_CREDITO`, límite, día de cierre y día de vencimiento.

`CicloFacturacion` está implementado como objeto de dominio no persistente y queda pendiente su integración completa con consumos, obligaciones y pagos.

## Próximo paso

Implementar el cambio mínimo para que `GastosPanel` permita seleccionar explícitamente la tarjeta de crédito utilizada cuando la forma de pago sea `TARJETA_CREDITO`.

Antes de modificar código, revisar nuevamente `GastosPanel`, `GastoService`, `CuentaService`, repositorio de cuentas y tests relacionados. Cubrir tarjetas activas, selección correcta y cuentas de otros tipos.

## Cierre Git del bloque H2

Después de sincronizar el commit `34eb4cc`, el usuario informó:

- `git diff`: limpio;
- `git diff --check`: sin salida ni errores;
- `git status`: working tree clean;
- rama sincronizada con `github/feature/swing-shell`.

## Regla de continuidad

Código y tests actuales prevalecen sobre documentación histórica. No modificar `main`, no crear ramas nuevas salvo indicación explícita y no asumir resultados locales no informados.
