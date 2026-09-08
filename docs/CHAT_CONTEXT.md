# SOFP — Contexto para continuar con ChatGPT

## Estado actual — 08/09/2026

La fuente de verdad es el código, los tests y los commits actuales. `docs/` es documentación auxiliar y puede quedar desactualizada; ante contradicción prevalecen código y tests.

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.
**Último commit funcional:** `aa29d44` — `test: cubrir formulario de transferencias`.

La comparación verificada en GitHub indica **377 commits adelante y 0 atrás** respecto de `main`. No se realizó merge a `main`.

## Último bloque funcional cerrado

El bloque de Transferencias quedó implementado, integrado al shell Swing y validado.

`TransferenciasPanel` registra transferencias mediante `OperacionFinancieraService`, que crea una `OperacionFinanciera` con un `EGRESO` en la cuenta origen y un `INGRESO` en la cuenta destino.

El formulario utiliza cuentas y categorías activas del perfil/usuario autorizado, importe, fecha y descripción. Las transferencias propias siguen diferenciadas de ingresos y gastos.

Commits:

- `1753074` — `feat: agregar formulario de transferencias`.
- `aa29d44` — `test: cubrir formulario de transferencias`.

## Bloques funcionales anteriores

Ingresos, Gastos, Obligaciones, FormaPago, reglas de saldo, seguridad/aislamiento, categorías con movimientos e integración general del shell continúan implementados y validados.

Ingresos utiliza:

**`IngresosPanel` → `IngresoService` → `MovimientoService` → `Movimiento` `INGRESO`.**

Gastos utiliza:

**`GastosPanel` → `GastoService` → `MovimientoService` → `Movimiento` `EGRESO`.**

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
- Una compra con tarjeta de crédito genera un egreso y una obligación; no se simula el pago inmediato de la cuenta.
- Transferencias entre cuentas propias no son ingresos ni gastos; se modelan mediante `OperacionFinanciera`.

## Últimas validaciones conocidas

### Suite general

`mvn test`, ejecutado el **08/09/2026 18:13:55 -03:00**:

- **634** tests;
- 0 failures;
- 0 errors;
- 0 skipped;
- `BUILD SUCCESS`;
- duración **11:52 min**.

### Transferencias

`mvn -Dtest=TransferenciasPanelTest test`, ejecutado el **08/09/2026 17:59:29 -03:00**:

- **4** tests;
- 0 failures;
- 0 errors;
- 0 skipped;
- `BUILD SUCCESS`;
- duración **01:16 min**.

### Transferencias + navegación

`mvn -Dtest=TransferenciasPanelTest,MainFrameNavigationTest test`, ejecutado el **08/09/2026 18:01:19 -03:00**:

- **5** tests;
- 0 failures;
- 0 errors;
- 0 skipped;
- `BUILD SUCCESS`;
- duración **39 s**.

## Estado Git conocido

`main` permanece en `a4be859...`. La rama de trabajo es `feature/swing-shell`. El último cambio funcional conocido es `aa29d44`.

Los commits posteriores son documentales. El estado local de `git diff`, `git diff --check` y `git status` no se asume; debe verificarse en el entorno local.

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
