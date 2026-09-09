# SOFP — Contexto para continuar con ChatGPT

## Estado actual — 09/09/2026

La fuente de verdad es el código, los tests y los commits actuales. `docs/` es documentación auxiliar y puede quedar desactualizada; ante contradicción prevalecen código y tests.

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.
**Último commit funcional:** `13a68fb8429d930b2137b9c9e78f7b884077f33e` — `fix: estabilizar formato de moneda en obligaciones`.

La comparación verificada en GitHub indica **411 commits adelante y 0 atrás** respecto de `main`. No se realizó merge a `main`.

## Último bloque funcional cerrado

El bloque reciente de moneda quedó completado y validado.

Los movimientos admiten moneda explícita. En compras con tarjeta de crédito, la obligación conserva la moneda económica del movimiento de origen mediante `Obligacion.getMoneda()`.

Una compra en USD genera una obligación en USD y una compra en ARS genera una obligación en ARS. No se realiza conversión automática al crear la obligación.

`ObligacionesPanel` muestra importe original y saldo pendiente con el código de moneda. El renderer utiliza `Locale.ROOT` para estabilizar el formato decimal independientemente del locale.

Commits:

- `9b92eac` — `feat: exponer moneda de la obligacion`.
- `47ced65` — `feat: mostrar moneda en obligaciones`.
- `fe0aa71` — `test: verificar moneda en obligaciones`.
- `13a68fb` — `fix: estabilizar formato de moneda en obligaciones`.

## Bloques funcionales anteriores

Ingresos, Gastos, Obligaciones, FormaPago, reglas de saldo, seguridad/aislamiento, categorías con movimientos, Transferencias e integración general del shell continúan implementados y validados.

Ingresos utiliza:

**`IngresosPanel` → `IngresoService` → `MovimientoService` → `Movimiento` `INGRESO`.**

Gastos utiliza:

**`GastosPanel` → `GastoService` → `MovimientoService` → `Movimiento` `EGRESO`.**

Transferencias utilizan `OperacionFinanciera` para coordinar los movimientos de origen y destino.

## Arquitectura funcional

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

Las transferencias son la excepción semántica: la coordinación se realiza mediante `OperacionFinanciera`, que agrupa los movimientos de origen y destino.

## Shell Swing

El shell integra Inicio, Cuentas, Categorías, Ingresos, Gastos, Movimientos, Inversiones, Reportes, Obligaciones y Transferencias mediante `MainFrame`, `SidebarPanel` y `CardLayout`.

## Reglas financieras vigentes

- `EGRESO` superior al saldo disponible: rechazado.
- `EGRESO` igual al saldo disponible: permitido y deja saldo cero.
- Las modificaciones de importe y tipo respetan fondos disponibles.
- Categorías con movimientos se conservan y se desactivan en lugar de eliminarse físicamente.
- `Cuenta` y `FormaPago` son conceptos distintos.
- Una compra con tarjeta de crédito genera un egreso y una obligación.
- La obligación conserva la moneda del movimiento de origen.
- No se realiza conversión automática al crear la obligación.
- Transferencias entre cuentas propias no son ingresos ni gastos; se modelan mediante `OperacionFinanciera`.

## Últimas validaciones conocidas

### Suite general

`mvn test`, ejecutado el **09/09/2026 13:15:48 -03:00**:

- **642** tests;
- 0 failures;
- 0 errors;
- 0 skipped;
- `BUILD SUCCESS`;
- duración **09:43 min**.

### ObligacionesPanelTest

`mvn test -Dtest=ObligacionesPanelTest`, ejecutado el **09/09/2026 13:05:03 -03:00**:

- **4** tests;
- 0 failures;
- 0 errors;
- 0 skipped;
- `BUILD SUCCESS`;
- duración **01:20 min**.

## Estado Git conocido

`main` permanece en `a4be859...`. La rama de trabajo es `feature/swing-shell`. El último commit funcional es `13a68fb...`.

Los commits documentales posteriores actualizan continuidad y no agregan comportamiento funcional.

El usuario confirmó localmente `git diff`, `git diff --check` y `git status` con working tree limpio y rama sincronizada con `github/feature/swing-shell`.

## Pendientes reales

1. Ampliar pasivos y patrimonio neto.
2. Evolucionar análisis histórico, resúmenes, evolución patrimonial, vencimientos y dashboard.
3. Limpiar posteriormente la salida de consola de la aplicación sin perder diagnóstico.

## Protocolo para nuevas sesiones

1. Revisar rama actual.
2. Revisar últimos commits.
3. Comparar con `main`.
4. Revisar README y documentación de continuidad.
5. Revisar archivos modificados recientemente.
6. Revisar tests relacionados.
7. Identificar último cambio, último test conocido y próximo paso.

Prioridad: **código → tests → commits → `main` → documentación → conversaciones anteriores**.

No modificar `main`, no crear ramas nuevas salvo indicación explícita y no asumir sincronizaciones o resultados de tests no informados.
