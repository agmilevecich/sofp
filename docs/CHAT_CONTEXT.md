# SOFP — Contexto para continuar con ChatGPT

La fuente de verdad es el código, Git y los tests actuales; `docs/` es documentación auxiliar. Antes de proponer cambios, reconstruir siempre el estado desde GitHub.

## Estado verificado — 04/09/2026

**Rama estable:** `main` → `a4be859`.
**Rama de trabajo:** `feature/swing-shell` → `70c2455` — `test: cubrir registro de categoria sin nombre`.

La rama de trabajo está **186 commits por delante y 2 por detrás de `main`**, estado `diverged`. Merge base: `96f3d999`. Los dos commits exclusivos de `main` son documentales.

## Seguridad

La auditoría transversal de seguridad y aislamiento de datos está completada e integrada en `main`. Se mantienen protegidos por propietario/usuario perfiles, cuentas, categorías, movimientos, posiciones/cartera y operaciones financieras.

## Fase 8 — Shell Swing

Componentes implementados: `MainFrame`, `HeaderPanel`, `SidebarPanel`, `InicioPanel`, `CuentasPanel`, `CategoriasPanel`, `MovimientosPanel`, `InversionesPanel`, `ReportesPanel`, `StatusBarPanel`, `RegistrarCuentaPanel`, `RegistrarMovimientoPanel` y `ui.Main`.

`MainFrame` usa `CardLayout` para Inicio, Cuentas, Categorías, Movimientos, Inversiones y Reportes. La UI utiliza servicios existentes y no duplica reglas de negocio.

## Criterios de diseño derivados de ControlFinanzas

`agmilevecich/controlfinanzas` queda establecido como **banco de ideas y referencia funcional**, no como arquitectura a copiar.

Patrón acordado para SOFP:

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

Los paneles futuros pueden cubrir gastos, ingresos, transferencias, inversiones, préstamos/deudas, pagos de tarjeta, historial y dashboard, todos convergiendo en el núcleo común.

Se distingue **Cuenta** de **Forma/Medio de pago**. Formas previstas: tarjeta de crédito, tarjeta de débito, QR, transferencia y efectivo.

La tarjeta de crédito requiere modelar una obligación/pasivo que puede existir sin una salida inmediata de fondos de una cuenta bancaria.

Los préstamos otorgados deben conservarse como derechos de cobro. Las transferencias entre cuentas propias no deben computarse como ingreso ni gasto.

Objetivo patrimonial de largo plazo: `TOTAL ACTIVOS - TOTAL PASIVOS = PATRIMONIO NETO`.

Estos criterios son decisiones de diseño/roadmap; no deben considerarse funcionalidades implementadas hasta contar con código y tests.

## Reglas de negocio pendientes

### Fondos insuficientes

Un `EGRESO` debe rechazarse cuando supere el saldo disponible de la cuenta. Un egreso igual al saldo debe permitirse y dejar saldo cero. La regla corresponde al servicio/dominio y debe cubrir también modificaciones de movimientos.

### Categorías con movimientos

Una categoría referenciada por movimientos no debe eliminarse físicamente. Debe conservarse el historial, probablemente mediante desactivación, y la UI debe mostrar un mensaje amigable ante el intento de eliminación.

## Bloques cerrados

Movimientos: **57/57 tests conocidos en verde**.

Inversiones/reportes: **21/21 tests conocidos en verde**.

Categorías: las validaciones conocidas permanecen en verde; `70c2455` agregó el caso de categoría sin nombre.

## Validaciones

Última suite general conocida: **568/568 tests en verde**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`.

Última validación específica conocida: `CategoriasPanelTest` **3/3**.

No asumir nuevas ejecuciones sin resultado informado por el usuario.

## Próximo paso

Antes de modificar código, revisar nuevamente implementación, clases relacionadas, servicios, repositorios, reglas de negocio y tests.

Orden funcional previsto: fondos insuficientes → categorías con movimientos → `FormaPago` → especialización de paneles sobre `Movimiento` → pasivos/obligaciones y patrimonio → análisis, vencimientos y dashboard adaptados a SOFP.

No hacer merge automático a `main`. No crear ramas nuevas. Después de cambios importantes: tests específicos, relacionados y suite general cuando corresponda; `git diff`, `git diff --check` y `git status`.
