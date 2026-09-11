# SOFP — Contexto para continuar con ChatGPT

## Estado actual — 11/09/2026

La fuente de verdad es el código, Git y los tests actuales; `docs/` es documentación auxiliar y puede quedar desactualizada. Antes de proponer cambios, reconstruir siempre el estado desde GitHub.

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

Último commit funcional: `34eb4cc` — `config: conectar SOFP a H2 por TCP`.

Antes de la actualización documental, GitHub verificó **495 commits adelante y 0 atrás** respecto de `main`.

## Último bloque funcional cerrado

### H2 persistente por TCP

`persistence.xml` de la aplicación utiliza `jdbc:h2:tcp://localhost/./database/sofp`.

H2 Server se ejecuta en `localhost:9092` y H2 Console en `localhost:8082`. El usuario verificó manualmente que SOFP y H2 Console funcionan simultáneamente sobre la misma base.

El `persistence.xml` de tests mantiene H2 en memoria, por lo que la suite no depende del servidor TCP.

## Shell Swing — Fase 8

El shell integra Inicio, Cuentas, Categorías, Ingresos, Gastos, Movimientos, Inversiones, Reportes, Obligaciones y Transferencias mediante `MainFrame`, `SidebarPanel` y `CardLayout`.

`GastosPanel` ya permite seleccionar forma de pago y cantidad de cuotas. El siguiente bloque de UI será incorporar una selección explícita de la tarjeta de crédito utilizada en una compra.

## Reglas de tarjetas

Una tarjeta de crédito es una `Cuenta` con `TipoCuenta.TARJETA_CREDITO`, límite, día de cierre y día de vencimiento.

Una compra con tarjeta genera un `Movimiento EGRESO` y una `Obligacion`. La obligación conserva la moneda económica del movimiento y no hay conversión automática.

El crédito disponible inicial se calcula por moneda como límite menos consumos pendientes.

## Ciclos de facturación

`CicloFacturacion` es un objeto de dominio no persistente. `Cuenta.calcularCicloFacturacion(LocalDate)` calcula inicio, cierre y vencimiento y ajusta días inexistentes al último día real del mes. `CicloFacturacionTest` contiene 9 tests.

Los ciclos están implementados; queda pendiente integrarlos con consumos, obligaciones y pagos.

## Tests

Suite general más reciente conocida: `mvn test` → **687/687**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, 11/09/2026 13:14:41 -03:00, duración 18:31 min.

## Próximo paso exacto

1. Revisar `GastosPanel`, `GastoService`, `CuentaService`, repositorio de cuentas y tests relacionados.
2. Definir el cambio mínimo para seleccionar una tarjeta de crédito explícita en `GastosPanel`.
3. Agregar tests para tarjetas activas, cuentas de otros tipos y selección de la tarjeta utilizada.
4. Ejecutar tests específicos, relacionados y suite general cuando corresponda.

## Continuidad

No modificar `main` ni crear ramas nuevas salvo indicación explícita. No asumir resultados locales no informados. Después de cambios importantes revisar tests, `git diff`, `git diff --check` y `git status`.

La documentación debe actualizarse al cerrar etapas importantes, pero siempre prevalecen código y tests actuales.
