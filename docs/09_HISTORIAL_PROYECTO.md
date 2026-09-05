# SOFP — Historial del proyecto

## 2026-08-31 — Cierre de seguridad y aislamiento de datos

`feature/seguridad-aislamiento-datos` completó la revisión transversal de seguridad y fue integrada en `main` mediante fast-forward.

Validación final registrada: `AislamientoDatosServiceTest` **7/7** y suite general **512/512**, `BUILD SUCCESS`.

## 2026-08-31 / 2026-09-04 — Fase 8: shell Swing

`feature/swing-shell` desarrolló el shell Swing y conectó `MainFrame`, `HeaderPanel`, `SidebarPanel`, `InicioPanel`, `CuentasPanel`, `CategoriasPanel`, `MovimientosPanel`, `InversionesPanel`, `ReportesPanel`, `StatusBarPanel`, `RegistrarCuentaPanel`, `RegistrarMovimientoPanel` y `ui.Main`.

`MainFrame` usa `CardLayout` para Inicio, Cuentas, Categorías, Movimientos, Inversiones y Reportes, integrando los servicios existentes y manteniendo las reglas de negocio fuera de la UI.

## 2026-09-03 — Selector de fecha y hora de movimientos

Se incorporó LGoodDatePicker para la fecha de movimientos. La fecha inicial corresponde al día del sistema, con configuración regional `es-AR`, domingo como primer día y formato visual `dd/MM/uuuu`.

La hora dejó de ingresarse mediante ComboBox y se obtiene automáticamente con `LocalTime.now()` al registrar.

Validación individual registrada: `RegistrarMovimientoPanelTest` **4/4**, `BUILD SUCCESS`.

## 2026-09-03/04 — Gestión de categorías

`CategoriasPanel` se integró en la navegación de `MainFrame` y permite gestionar categorías del perfil del usuario mediante `CategoriaService`.

`66b22f3` corrigió la gestión de transacción y `flush` del alta cuando corresponde.

`6eefc36` corrigió el manejo del error al registrar categoría y `70c2455` agregó cobertura del registro sin nombre.

La regla de negocio posterior sobre categorías con movimientos quedó implementada y validada con `CategoriaServiceTest` **23/23**.

## 2026-09-04 — Criterios funcionales derivados de ControlFinanzas

Se definió que `agmilevecich/controlfinanzas` será utilizado como banco de ideas y referencia funcional, no como arquitectura para copiar.

Se acordó mantener en SOFP el patrón:

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

Los futuros paneles podrán especializarse en gastos, ingresos, transferencias, inversiones, préstamos/deudas, pagos de tarjeta, historial y dashboard, convergiendo en el núcleo común.

Se adoptó la distinción conceptual entre **Cuenta** y **Forma/Medio de pago**, contemplando tarjeta de crédito, tarjeta de débito, QR, transferencia y efectivo.

Se reconoció que tarjeta de crédito requiere modelar una obligación/pasivo separada de la salida inmediata de fondos. También se definió que préstamos otorgados deben permanecer como derechos de cobro y que transferencias entre cuentas propias no son ingresos ni gastos.

El objetivo de largo plazo es representar activos, pasivos y patrimonio neto mediante `TOTAL ACTIVOS - TOTAL PASIVOS = PATRIMONIO NETO`, además de liquidez, ingresos, gastos y obligaciones.

Estos criterios quedan registrados como decisiones de diseño/roadmap y no como funcionalidades implementadas hasta contar con código y tests.

## 2026-09-04 — Fondos insuficientes: implementación y validación

Se implementó en `MovimientoService` la regla que rechaza un `EGRESO` cuando supera el saldo disponible de la cuenta. Un egreso exactamente igual al saldo disponible se permite y deja saldo cero.

La validación también cubre modificaciones de importe y tipo de movimiento cuando pueden producir saldo negativo, excluyendo correctamente el movimiento actual del cálculo de fondos disponibles.

Commit de producción: `5dd8372` — `fix: validar fondos disponibles en movimientos`.

Cobertura específica: `MovimientoFondosInsuficientesTest` **6/6**.

Cobertura relacionada: `MovimientoServiceTest` **57/57** y `RegistrarMovimientoPanelTest` **4/4**.

Se ajustó el fixture de `MovimientoServiceTest.deberiaModificarTipoMovimiento` mediante un ingreso previo independiente para que la conversión sea válida bajo la nueva regla, sin cambiar la intención del test. Commit: `2b2bf3e`.

## 2026-09-05 — Categorías con movimientos: aislamiento de persistencia

La suite general reveló un conflicto de unicidad al intentar crear la moneda `ARS` desde `CategoriaServiceTest`. La causa se trató como problema de aislamiento del contexto de persistencia del test, no como motivo para alterar el fixture de negocio.

Se modificó únicamente el `setUp()` de `CategoriaServiceTest` para cerrar `JpaTestManager` antes de crear el `EntityManager` de cada test.

Commit: `85b767c` — `test: aislar persistencia en CategoriaServiceTest`.

Validación específica posterior: `CategoriaServiceTest` **23/23**, `BUILD SUCCESS`.

## 2026-09-05 — Primer corte funcional de Gastos

Se implementó `GastoService` y `GastosPanel` siguiendo el criterio acordado:

**Gastos → servicio específico → `Movimiento` `EGRESO` → `Movimientos` como historial consolidado.**

`GastosPanel` permite seleccionar cuenta y categoría activas, importe, fecha y descripción. `GastoService` delega en `MovimientoService`, manteniendo las reglas financieras y una única fuente de verdad.

La cobertura de `GastosPanelTest` valida el registro exitoso y su persistencia como `EGRESO` en el historial común.

El commit `98dead73` corrigió únicamente el fixture de prueba agregando un ingreso previo de $1.000 para registrar un gasto de $100 respetando la regla de fondos disponibles.

## 2026-09-05 — Suite general posterior a Gastos

El usuario ejecutó `mvn test` después de sincronizar la rama.

Resultado: **586/586 tests**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`.

Duración: **10:43 min**.

Finalización: **05/09/2026 12:34:00 -03:00**.

Con este resultado, el primer corte funcional de Gastos queda validado.

## Estado Git — 2026-09-05

La rama activa continúa siendo `feature/swing-shell`.

Último commit de código: `98dead73` — `test: preparar saldo para registro de gasto`.

Luego se actualizaron los documentos de continuidad y builds para reflejar el cierre del primer corte de Gastos.

La rama no se integró a `main`. `main` permanece en `a4be859`.

## Próximo avance

Integrar `FormaPago` dentro del flujo de Gastos cuando corresponda al modelo financiero y, posteriormente, abordar tarjetas de crédito, obligaciones/pasivos y patrimonio neto.
