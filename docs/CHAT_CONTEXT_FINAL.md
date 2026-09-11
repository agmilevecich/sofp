# SOFP — Contexto para continuar con ChatGPT

## Estado actual — 11/09/2026

La fuente de verdad es el código, Git y los tests actuales; `docs/` es documentación auxiliar y puede quedar desactualizada. Antes de proponer cambios, reconstruir siempre el estado desde GitHub.

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

Último bloque funcional cerrado: selección explícita de tarjeta de crédito en `GastosPanel`.

## Último bloque funcional cerrado

### Selección explícita de tarjeta de crédito

Cuando se selecciona `FormaPago.TARJETA_CREDITO`, `GastosPanel` filtra la selección de cuentas para mostrar únicamente cuentas activas con `TipoCuenta.TARJETA_CREDITO`.

La tarjeta elegida se utiliza como `Cuenta` al registrar el gasto. Las cuentas de otros tipos no se mezclan en esa selección.

Se agregaron tests específicos y se ajustaron los tests existentes para respetar el flujo vigente de la UI: primero forma de pago, luego cuenta.

## Persistencia y H2

`persistence.xml` de la aplicación utiliza `jdbc:h2:tcp://localhost/./database/sofp`.

H2 Server se ejecuta en `localhost:9092` y H2 Console en `localhost:8082`. El usuario verificó manualmente que SOFP y H2 Console funcionan simultáneamente sobre la misma base.

El `persistence.xml` de tests mantiene H2 en memoria, por lo que la suite no depende del servidor TCP.

## Shell Swing — Fase 8

El shell integra Inicio, Cuentas, Categorías, Ingresos, Gastos, Movimientos, Inversiones, Reportes, Obligaciones y Transferencias mediante `MainFrame`, `SidebarPanel` y `CardLayout`.

`GastosPanel` permite seleccionar forma de pago, cantidad de cuotas y, para `TARJETA_CREDITO`, la tarjeta activa utilizada.

## Reglas de tarjetas

Una tarjeta de crédito es una `Cuenta` con `TipoCuenta.TARJETA_CREDITO`, límite, día de cierre y día de vencimiento.

Una compra con tarjeta genera un `Movimiento EGRESO` y una `Obligacion`. La obligación conserva la moneda económica del movimiento y no hay conversión automática.

El crédito disponible inicial se calcula por moneda como límite menos consumos pendientes.

## Ciclos de facturación

`CicloFacturacion` es un objeto de dominio no persistente. `Cuenta.calcularCicloFacturacion(LocalDate)` calcula inicio, cierre y vencimiento y ajusta días inexistentes al último día real del mes. `CicloFacturacionTest` contiene 9 tests.

Los ciclos están implementados; queda pendiente integrarlos con consumos, obligaciones y pagos.

## Tests

Suite general más reciente conocida: `mvn test` → **688/688**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, 11/09/2026 14:10:20 -03:00, duración 12:52 min.

Tests específicos del último bloque: `mvn -Dtest=GastosPanelTest,GastosPanelTarjetaCreditoTest test` → **8/8**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.

## Próximo paso exacto

1. Revisar la integración actual de `CicloFacturacion`, consumos, obligaciones y pagos.
2. Definir el cambio mínimo para conectar los ciclos con el flujo financiero existente.
3. Agregar tests de casos normales, límites, fechas de cierre/vencimiento y relaciones con obligaciones/pagos.
4. Ejecutar tests específicos, relacionados y suite general cuando corresponda.

## Continuidad

No modificar `main` ni crear ramas nuevas salvo indicación explícita. No asumir resultados locales no informados. Después de cambios importantes revisar tests, `git diff`, `git diff --check` y `git status`.

La documentación se actualiza al cerrar etapas importantes, pero siempre prevalecen código y tests actuales.
