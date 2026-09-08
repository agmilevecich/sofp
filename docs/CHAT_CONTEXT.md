# SOFP — Contexto para continuar con ChatGPT

## Estado actual — 08/09/2026

La fuente de verdad es el código, los tests y los commits actuales. `docs/` es documentación auxiliar y puede quedar desactualizada; ante contradicción prevalecen código y tests.

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

HEAD funcional documentado: `87052df953dbd282a43c5647d05b68d1854c4f17`.

No se realizó merge a `main`.

## Último bloque funcional cerrado

El bloque de obligaciones quedó integrado en dominio, persistencia, servicio, autorización y shell Swing.

`Obligacion` representa una obligación originada por un `Movimiento` de egreso con `TARJETA_CREDITO`. Conserva importe original, saldo pendiente, estado y movimiento de origen.

`ObligacionService` permite listar obligaciones por usuario y registrar pagos autorizados. Los pagos actualizan `PARCIAL` o `PAGADA` y las reglas del dominio rechazan pagos no positivos, sobrepagos y pagos sobre obligaciones ya pagadas.

`GastoService` crea la obligación cuando registra un gasto con tarjeta de crédito y recibe `ObligacionService`. Si no recibe ese servicio, rechaza la operación con `IllegalStateException`.

## Commits funcionales recientes

- `43cfd9c` — autorización de pagos.
- `456fbfb` — tests de autorización.
- `f20023d` — `ObligacionesPanel`.
- `264dd54` — navegación/sidebar.
- `87b8e46` — integración en `MainFrame`.
- `7194a5d` — tests del panel.
- `029de48` — test de navegación.
- `166b5f0` — conservar selección al refrescar obligaciones.
- `87052df` — cubrir navegación hacia obligaciones.

## Arquitectura funcional

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

Gastos utiliza:

**`GastosPanel` → `GastoService` → `MovimientoService` → `Movimiento` `EGRESO` → `Movimientos`.**

Las obligaciones se coordinan desde `GastoService`/`ObligacionService`; la UI no duplica sus reglas.

## Shell Swing

El shell integra Inicio, Cuentas, Categorías, Gastos, Movimientos, Inversiones, Reportes y Obligaciones mediante `MainFrame`, `SidebarPanel` y `CardLayout`.

`ObligacionesPanel` lista las obligaciones del usuario, permite seleccionar una obligación y registrar pagos, muestra estado/saldo/fecha, maneja errores y refresca la lista conservando la selección.

Existe compatibilidad para constructores anteriores de `MainFrame` que no reciben `ObligacionService`.

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

`mvn test`, ejecutado el **08/09/2026 13:27:36 -03:00**:

- **618** tests;
- 0 failures;
- 0 errors;
- 0 skipped;
- `BUILD SUCCESS`;
- duración **21:26 min**.

Esta suite fue ejecutada antes de la UI de obligaciones y sigue siendo la última suite general completa conocida.

### Suite focalizada actual

`mvn -Dtest=MainFrameNavigationTest,MainFrameObligacionesTest,ObligacionesPanelTest,ObligacionServiceTest test`, ejecutado el **08/09/2026 14:14:14 -03:00**:

- **14** tests;
- 0 failures;
- 0 errors;
- 0 skipped;
- `BUILD SUCCESS`;
- duración **01:48 min**.

`ObligacionesPanelTest`: **3/3**.

No asumir que `mvn test` completo fue ejecutado después de estos cambios.

## Pendientes reales

1. Ejecutar la suite completa `mvn test` sobre el estado actual.
2. Continuar la evolución de ingresos y transferencias mediante el núcleo común.
3. Ampliar pasivos y patrimonio neto.
4. Evolucionar análisis histórico, resúmenes, evolución patrimonial, vencimientos y dashboard.
5. Limpiar posteriormente la salida de consola de la aplicación sin perder diagnóstico.

## Protocolo para nuevas sesiones

1. Revisar rama actual.
2. Revisar últimos commits.
3. Comparar con `main`.
4. Revisar README y documentación de continuidad.
5. Revisar archivos modificados recientemente.
6. Revisar tests relacionados.
7. Identificar último cambio, último test conocido y próximo paso.

Prioridad: **código → tests → commits → `main` → documentación**.

No modificar `main`, no crear ramas nuevas salvo indicación explícita y no asumir sincronizaciones o resultados de tests no informados.
