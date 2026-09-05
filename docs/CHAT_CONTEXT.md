# SOFP — Contexto para continuar con ChatGPT

La fuente de verdad es el código, Git y los tests actuales; `docs/` es documentación auxiliar. Antes de proponer cambios, reconstruir siempre el estado desde GitHub.

## Estado verificado — 05/09/2026

**Rama estable:** `main` → `a4be859`.
**Rama de trabajo:** `feature/swing-shell` → `4ae0a27`.

Último commit de la rama: `4ae0a27` — `test: cubrir formas de pago`.
Último commit funcional/test previo: `85b767c` — `test: aislar persistencia en CategoriaServiceTest`.

Después de `85b767c` se definió `FormaPago` y se agregó `FormaPagoTest`. No se ha informado todavía la ejecución de ese test.

La suite general más reciente informada corresponde al estado anterior a esos commits: **580/580**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`.

## Fase 8 — Shell Swing

Componentes implementados: `MainFrame`, `HeaderPanel`, `SidebarPanel`, `InicioPanel`, `CuentasPanel`, `CategoriasPanel`, `MovimientosPanel`, `InversionesPanel`, `ReportesPanel`, `StatusBarPanel`, `RegistrarCuentaPanel`, `RegistrarMovimientoPanel` y `ui.Main`.

`MainFrame` usa `CardLayout` para Inicio, Cuentas, Categorías, Movimientos, Inversiones y Reportes. La UI utiliza servicios existentes y no duplica reglas de negocio.

## Objetivo UX actual del Swing

A partir del análisis de `ControlFinanzas`, el usuario definió que el Swing debe aproximarse funcionalmente a esa experiencia: un panel de **Gastos** donde se registren compras, pagos de servicios y otros egresos, y esos registros deben verse reflejados en la tabla/historial común de `Movimientos`.

El patrón arquitectónico acordado es:

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

Por lo tanto, `Gastos` será una interfaz de carga especializada y `Movimientos` será el historial financiero consolidado. No deben existir dos fuentes de verdad financieras.

Flujo conceptual:

**Gastos → servicio específico → `Movimiento` `EGRESO` → `Movimientos`.**

El primer corte de Gastos debe reutilizar las reglas existentes de `MovimientoService` y no crear un núcleo financiero paralelo.

## Seguridad

La auditoría transversal de seguridad y aislamiento de datos está completada e integrada en `main`. Se mantienen protegidos por propietario/usuario perfiles, cuentas, categorías, movimientos, posiciones/cartera y operaciones financieras.

## Criterios de diseño derivados de ControlFinanzas

`agmilevecich/controlfinanzas` queda establecido como **banco de ideas y referencia funcional**, no como arquitectura a copiar.

Se distingue **Cuenta** de **Forma/Medio de pago**. Formas previstas: tarjeta de crédito, tarjeta de débito, QR, transferencia y efectivo.

La tarjeta de crédito requiere modelar una obligación/pasivo que puede existir sin una salida inmediata de fondos de una cuenta bancaria.

Los préstamos otorgados deben conservarse como derechos de cobro. Las transferencias entre cuentas propias no deben computarse como ingreso ni gasto.

Objetivo patrimonial de largo plazo: `TOTAL ACTIVOS - TOTAL PASIVOS = PATRIMONIO NETO`.

Estos criterios son decisiones de diseño/roadmap; no deben considerarse funcionalidades implementadas hasta contar con código y tests.

## FormaPago

`FormaPago` fue agregada mediante `927c66c` y `FormaPagoTest` mediante `4ae0a27`.

`FormaPagoTest` todavía no tiene un resultado de ejecución informado. No considerar la funcionalidad validada hasta recibir ese resultado.

La integración de `FormaPago` con `Movimiento` queda pendiente y debe realizarse dentro del flujo funcional de Gastos cuando corresponda.

## Reglas de negocio cerradas

### Fondos insuficientes

Un `EGRESO` debe rechazarse si supera el saldo disponible de la cuenta. Un egreso exactamente igual al saldo disponible está permitido y deja saldo cero.

Producción: `5dd8372` — `fix: validar fondos disponibles en movimientos`.

### Categorías con movimientos

Una categoría referenciada por movimientos no debe eliminarse físicamente. Se conserva el historial y se desactiva la categoría. La UI informa la situación de forma amigable.

`CategoriaServiceTest`: **23/23**.

El aislamiento de persistencia del test se corrigió mediante `JpaTestManager.close()` antes de crear el `EntityManager` en cada `setUp()`. Commit: `85b767c`.

## Tests y validación

- `MovimientoFondosInsuficientesTest`: **6/6**;
- `MovimientoServiceTest`: **57/57**;
- `RegistrarMovimientoPanelTest`: **4/4**;
- `RegistrarCuentaPanelTest`: **6/6**;
- `CategoriaServiceTest`: **23/23**;
- suite general: **580/580**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`.

Suite general ejecutada con `mvn clean test`, finalizada el **05/09/2026 09:49:58 -03:00**, duración **10:58 min**.

No atribuir esta suite a los commits posteriores de `FormaPago`.

## Estado Git

`feature/swing-shell` continúa como rama de trabajo y `main` permanece en `a4be859`. No se realizó merge a `main`.

## Próximo paso

Implementar el primer corte funcional de `GastosPanel`: registrar un egreso de negocio y reflejarlo en el historial común de `Movimientos`.

Antes de implementar cambios, reconstruir nuevamente el código real y revisar `MainFrame`, `SidebarPanel`, `MovimientosPanel`, `RegistrarMovimientoPanel`, `MovimientoService`, `Movimiento`, cuentas, categorías y tests relacionados.

Después de cambios importantes: tests específicos, relacionados y suite general cuando corresponda; `git diff`, `git diff --check` y `git status`.

No hacer merge automático a `main`. No crear ramas nuevas salvo indicación explícita.
