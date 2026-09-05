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

## 2026-09-05 — Suite general final conocida

Se ejecutó `mvn clean test` sobre `feature/swing-shell` después de corregir el aislamiento.

Resultado: **580/580 tests**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`.

Duración: **10:58 min**.

Finalización: **05/09/2026 09:49:58 -03:00**.

Este es el último resultado de suite general informado y no debe atribuirse a los commits posteriores de `FormaPago`.

## 2026-09-05 — Definición de FormaPago

Se agregó `FormaPago` al dominio mediante `927c66c` con cinco valores: efectivo, transferencia, tarjeta de débito, tarjeta de crédito y QR.

Se agregó `FormaPagoTest` mediante `4ae0a27`.

La prueba todavía no tiene resultado de ejecución informado y, por lo tanto, la funcionalidad no se considera validada.

## 2026-09-05 — Redefinición del próximo avance del Swing

Se precisó el objetivo funcional del Swing a partir de la experiencia previa con ControlFinanzas.

El usuario quiere una experiencia donde exista un panel especializado de **Gastos** para registrar compras, pagos de servicios y otros egresos, y que esos registros aparezcan posteriormente en la tabla/historial común de `Movimientos`.

Se reafirmó que esto no implica crear una segunda fuente de verdad. El flujo será:

**Gastos → servicio específico → `Movimiento` `EGRESO` → `Movimientos` como historial consolidado.**

Por esta razón, el próximo bloque funcional pasa a ser el primer corte de `GastosPanel`. La integración de `FormaPago` queda como paso posterior dentro de ese flujo, cuando su relación con el modelo financiero esté correctamente definida.

## Estado Git — 2026-09-05

La rama activa continúa siendo `feature/swing-shell`.

Último commit actual de la rama: `4ae0a27` — `test: cubrir formas de pago`.

Último cambio funcional/test previo: `85b767c` — `test: aislar persistencia en CategoriaServiceTest`.

La rama no se integró a `main`. `main` permanece en `a4be859`.

## Próximo avance

Implementar el primer corte funcional de `GastosPanel`, revisando antes código Swing, `MovimientoService`, `Movimiento`, cuentas, categorías y tests relacionados.
