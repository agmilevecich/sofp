# SOFP — Contexto para continuar con ChatGPT

## Estado actual — 08/09/2026

La fuente de verdad es el código, los tests y los commits actuales. `docs/` es documentación auxiliar y puede quedar desactualizada; ante contradicción prevalecen código y tests.

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

HEAD verificado antes de esta actualización documental: `7f05cd1ef48576e0bc59a3828cd254e0bd4a9d9b` — `test: actualizar expectativas de gastos con crédito`.

La comparación con `main` indica **326 commits por delante y 0 por detrás**, con merge-base `a4be85913847200cb70976d5266d9cbba10b3100`. No se realizó merge.

## Último bloque funcional cerrado

El bloque reciente de tarjeta de crédito quedó consolidado en dominio, persistencia y servicios.

`Obligacion` representa una obligación originada por un `Movimiento` de egreso con `TARJETA_CREDITO`. Conserva importe original, saldo pendiente, estado y movimiento de origen.

`ObligacionService` permite registrar, consultar y registrar pagos con transacción JPA. Los pagos actualizan `PARCIAL` o `PAGADA` y las reglas del dominio rechazan pagos no positivos, sobrepagos y pagos sobre obligaciones ya pagadas.

`GastoService` crea la obligación cuando registra un gasto con tarjeta de crédito y recibe `ObligacionService`. Si no recibe ese servicio, rechaza la operación con `IllegalStateException`.

Los últimos commits de corrección fueron:

- `6c7d70a` — `fix: mantener compatibilidad de MainFrame sin ObligacionService`.
- `7f05cd1` — `test: actualizar expectativas de gastos con crédito`.

## Arquitectura funcional

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

Gastos utiliza:

**`GastosPanel` → `GastoService` → `MovimientoService` → `Movimiento` `EGRESO` → `Movimientos`.**

Las obligaciones se coordinan desde `GastoService`/`ObligacionService`; la UI no debe duplicar sus reglas.

## Shell Swing

El shell integra Inicio, Cuentas, Categorías, Gastos, Movimientos, Inversiones y Reportes mediante `MainFrame` y `CardLayout`.

Existe compatibilidad para constructores anteriores de `MainFrame` que no reciben `ObligacionService`, evitando romper pruebas y usos existentes.

Todavía no existe un panel Swing específico de obligaciones/pagos.

## Reglas financieras vigentes

- `EGRESO` superior al saldo disponible: rechazado.
- `EGRESO` igual al saldo disponible: permitido y deja saldo cero.
- Las modificaciones de importe y tipo respetan fondos disponibles.
- Categorías con movimientos se conservan y se desactivan en lugar de eliminarse físicamente.
- `Cuenta` y `FormaPago` son conceptos distintos.
- Una compra con tarjeta de crédito genera un egreso y una obligación; no se simula el pago inmediato de la cuenta.
- Transferencias entre cuentas propias no son ingresos ni gastos; se modelan mediante `OperacionFinanciera`.

## Última validación conocida

El usuario informó el **08/09/2026 13:27:36 -03:00**:

- comando: `mvn test`;
- Tests run: **618**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración: **21:26 min**.

Validación focalizada previa: `GastosPanelTest` + `MainFrameMovimientosTest` → **8/8**, sin fallos ni errores.

La primera ejecución general del bloque había presentado 1 failure y 2 errors. Fueron resueltos por `6c7d70a` y `7f05cd1`; la ejecución posterior quedó en **618/618**.

Validación final local informada por el usuario:

- `git diff`: limpio;
- `git diff --check`: sin errores;
- `git status`: working tree limpio;
- rama sincronizada con `github/feature/swing-shell`.

## Pendientes reales

1. Crear panel Swing para consultar obligaciones y registrar pagos.
2. Integrar el panel en `MainFrame`/`SidebarPanel` y definir refrescos después de gastos con tarjeta y pagos.
3. Continuar la evolución de ingresos y transferencias mediante el núcleo común.
4. Ampliar pasivos y patrimonio neto.
5. Evolucionar análisis histórico, resúmenes, evolución patrimonial, vencimientos y dashboard.
6. Limpiar posteriormente la salida de consola de la aplicación sin perder diagnóstico.

## Protocolo para nuevas sesiones

1. Revisar rama actual.
2. Revisar últimos commits.
3. Comparar con `main`.
4. Revisar README y documentación de continuidad.
5. Revisar archivos modificados recientemente.
6. Revisar tests relacionados.
7. Identificar último cambio, último test conocido y próximo paso.

Prioridad: **código → tests → commits → main → documentación**.

No modificar `main`, no crear ramas nuevas salvo indicación explícita y no asumir sincronizaciones o resultados de tests no informados.
