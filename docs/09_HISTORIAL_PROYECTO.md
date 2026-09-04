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

Última validación conocida: `CategoriasPanelTest` **3/3** en verde.

## 2026-09-04 — Criterios funcionales derivados de ControlFinanzas

Se definió que `agmilevecich/controlfinanzas` será utilizado como banco de ideas y referencia funcional, no como arquitectura para copiar.

Se acordó mantener en SOFP el patrón:

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

Los futuros paneles podrán especializarse en gastos, ingresos, transferencias, inversiones, préstamos/deudas, pagos de tarjeta, historial y dashboard, convergiendo en el núcleo común.

Se adoptó la distinción conceptual entre **Cuenta** y **Forma/Medio de pago**, contemplando tarjeta de crédito, tarjeta de débito, QR, transferencia y efectivo.

Se reconoció que tarjeta de crédito requiere modelar una obligación/pasivo separada de la salida inmediata de fondos. También se definió que préstamos otorgados deben permanecer como derechos de cobro y que transferencias entre cuentas propias no son ingresos ni gastos.

El objetivo de largo plazo es representar activos, pasivos y patrimonio neto mediante `TOTAL ACTIVOS - TOTAL PASIVOS = PATRIMONIO NETO`, además de liquidez, ingresos, gastos, inversiones y obligaciones.

Estos criterios quedan registrados como decisiones de diseño/roadmap y **no como funcionalidades implementadas**.

## 2026-09-04 — Reglas de negocio detectadas

Se identificó la necesidad de impedir un `EGRESO` que supere el saldo disponible de la cuenta. El egreso igual al saldo debe permitirse y dejar saldo cero. La regla debe vivir en el servicio/dominio y contemplar también modificaciones de movimientos.

También se identificó que una categoría con movimientos asociados no debe eliminarse físicamente, para no romper el historial. Debe evaluarse su desactivación y una respuesta amigable de la UI ante el intento de eliminación.

## Estado Git — 2026-09-04

La rama activa continúa siendo `feature/swing-shell` y su último commit funcional/test conocido es `70c2455`.

La documentación de continuidad se actualizó para reflejar el estado real y las nuevas decisiones de diseño. Estas actualizaciones son commits `docs:` y no representan cambios funcionales.

No se realizó merge a `main` ni se crean ramas nuevas.

## Validación general conocida

La última suite general conocida continúa siendo **568/568 tests en verde**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`.

## Próximo avance

Implementar los próximos bloques solamente después de revisar código, servicios, repositorios, reglas y tests relacionados. Prioridad funcional prevista: fondos insuficientes, categorías con movimientos, `FormaPago` y luego evolución progresiva hacia operaciones financieras especializadas, pasivos/patrimonio y análisis.
