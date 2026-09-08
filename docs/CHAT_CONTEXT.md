# SOFP — Contexto para continuar con ChatGPT

## Estado actual — 08/09/2026

La fuente de verdad es el código, los tests y los commits actuales. `docs/` es documentación auxiliar y puede quedar desactualizada; ante contradicción prevalecen código y tests.

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

Último cambio funcional verificado: `f9339db2a500d5530eea95dc39e14e2725f4eb8f` — `test: cubrir navegacion hacia ingresos`.

Los commits posteriores son documentales. No se realizó merge a `main`.

## Último bloque funcional cerrado

El bloque de Ingresos quedó integrado al shell Swing sobre el núcleo financiero común.

`IngresosPanel` registra ingresos mediante `IngresoService`, que delega en `MovimientoService` para crear un `Movimiento` de tipo `INGRESO`. El formulario utiliza cuenta, categoría, importe, fecha y descripción y filtra cuentas/categorías activas del perfil/usuario autorizado.

La navegación hacia Ingresos está integrada en `SidebarPanel`/`MainFrame`.

Commits del bloque:

- `d99cc6a` — formulario de ingresos.
- `2977f36` — tests del formulario.
- `4e6b363` — integración en `MainFrame`.
- `cd781a1` — navegación/sidebar.
- `f9339db` — tests de navegación.

## Bloques funcionales anteriores

El bloque de obligaciones está integrado en dominio, persistencia, servicio, autorización y shell Swing.

`Obligacion` representa una obligación originada por un `Movimiento` de egreso con `TARJETA_CREDITO`. Conserva importe original, saldo pendiente, estado y movimiento de origen.

`ObligacionService` permite listar obligaciones por usuario y registrar pagos autorizados. Los pagos actualizan `PARCIAL` o `PAGADA` y las reglas del dominio rechazan pagos no positivos, sobrepagos y pagos sobre obligaciones ya pagadas.

`GastoService` crea la obligación cuando registra un gasto con tarjeta de crédito y recibe `ObligacionService`. Si no recibe ese servicio, rechaza la operación con `IllegalStateException`.

`ObligacionesPanel` lista las obligaciones del usuario, permite seleccionar una y registrar pagos, muestra estado/saldo/fecha, maneja errores y refresca la lista conservando la selección.

## Arquitectura funcional

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

Ingresos utiliza:

**`IngresosPanel` → `IngresoService` → `MovimientoService` → `Movimiento` `INGRESO` → `Movimientos`.**

Gastos utiliza:

**`GastosPanel` → `GastoService` → `MovimientoService` → `Movimiento` `EGRESO` → `Movimientos`.**

Las obligaciones se coordinan desde `GastoService`/`ObligacionService`; la UI no duplica sus reglas.

## Shell Swing

El shell integra Inicio, Cuentas, Categorías, Ingresos, Gastos, Movimientos, Inversiones, Reportes y Obligaciones mediante `MainFrame`, `SidebarPanel` y `CardLayout`.

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

`mvn test`, ejecutado el **08/09/2026 15:16:17 -03:00**:

- **630** tests;
- 0 failures;
- 0 errors;
- 0 skipped;
- `BUILD SUCCESS`;
- duración **11:55 min**.

### Suite focalizada de Ingresos/navegación

`mvn -Dtest=IngresosPanelTest,MainFrameNavigationTest test`, ejecutado el **08/09/2026 14:55:35 -03:00**:

- **5** tests;
- 0 failures;
- 0 errors;
- 0 skipped;
- `BUILD SUCCESS`;
- duración **01:21 min**.

### Suite relacionada

`mvn -Dtest=IngresoServiceTest,IngresosPanelTest,MainFrameNavigationTest,MainFrameObligacionesTest,ObligacionesPanelTest,GastosPanelTest test`, ejecutado el **08/09/2026 14:58:53 -03:00**:

- **18** tests;
- 0 failures;
- 0 errors;
- 0 skipped;
- `BUILD SUCCESS`;
- duración **01:31 min**.

## Estado Git conocido

`main` permanece en `a4be859...`. La rama de trabajo es `feature/swing-shell` y la comparación verificada con `main` indica **366 commits adelante y 0 atrás**.

No se asume el estado local de `git diff`, `git diff --check` ni `git status`; esos resultados deben ser informados desde el entorno local cuando se haga la validación final.

## Pendientes reales

1. Evolucionar transferencias mediante `OperacionFinanciera`.
2. Ampliar pasivos y patrimonio neto.
3. Evolucionar análisis histórico, resúmenes, evolución patrimonial, vencimientos y dashboard.
4. Limpiar posteriormente la salida de consola de la aplicación sin perder diagnóstico.

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
