# SOFP — Contexto para continuar con ChatGPT

La fuente de verdad es el código, Git y los tests actuales; `docs/` es documentación auxiliar. Antes de proponer cambios, reconstruir siempre el estado desde GitHub.

## Estado verificado — 04/09/2026

**Rama estable:** `main` → `a4be859`.
**Rama de trabajo:** `feature/swing-shell` → `2b2bf3e` — `test: adaptar cambio de tipo a fondos disponibles`.

La rama de trabajo continúa divergida respecto de `main`: está por delante y contiene los cambios de la Fase 8 Swing y de validación financiera. Los dos commits exclusivos de `main` conocidos son documentales y no se incorporan automáticamente.

## Fase 8 — Shell Swing

Componentes implementados: `MainFrame`, `HeaderPanel`, `SidebarPanel`, `InicioPanel`, `CuentasPanel`, `CategoriasPanel`, `MovimientosPanel`, `InversionesPanel`, `ReportesPanel`, `StatusBarPanel`, `RegistrarCuentaPanel`, `RegistrarMovimientoPanel` y `ui.Main`.

`MainFrame` usa `CardLayout` para Inicio, Cuentas, Categorías, Movimientos, Inversiones y Reportes. La UI utiliza servicios existentes y no duplica reglas de negocio.

## Seguridad

La auditoría transversal de seguridad y aislamiento de datos está completada e integrada en `main`. Se mantienen protegidos por propietario/usuario perfiles, cuentas, categorías, movimientos, posiciones/cartera y operaciones financieras.

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

## Regla de negocio cerrada — Fondos insuficientes

Un `EGRESO` debe rechazarse si supera el saldo disponible de la cuenta. Un egreso exactamente igual al saldo disponible está permitido y deja saldo cero.

La regla está implementada en `MovimientoService` y se aplica tanto al registro como a modificaciones de importe y tipo cuando corresponda. La validación utiliza el saldo contextualizado por usuario y excluye correctamente el movimiento actual al modificarlo.

Se mantiene la separación deliberada del método interno de registro utilizado por fixtures/compatibilidad de paquetes; la validación de fondos se aplica al flujo público que registra movimientos para un usuario.

## Tests y validación

`MovimientoFondosInsuficientesTest`: **6/6 tests en verde**, Failures 0, Errors 0, Skipped 0.

`MovimientoServiceTest`: **57/57 tests en verde** en la validación posterior al ajuste del fixture de cambio de tipo.

`RegistrarMovimientoPanelTest`: **4/4 tests en verde**.

Suite general más reciente: **577/577 tests en verde**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`.

La suite general se ejecutó con `mvn clean test` y terminó el 04/09/2026 a las 18:54:20 -03:00, con una duración total de **11:16 min**.

El fixture de `MovimientoServiceTest.deberiaModificarTipoMovimiento` fue adaptado para contar con un ingreso previo independiente, de modo que la conversión de un ingreso de 50.000 a egreso sea financieramente válida bajo la nueva regla. El cambio está en el commit `2b2bf3e` y no altera la intención del test.

## Últimos commits funcionales/test

- `2b2bf3e` — `test: adaptar cambio de tipo a fondos disponibles`;
- `805e9fa` — `test: corregir comparacion de saldo en movimiento`;
- `8b44177` — `test: corregir cobertura de cambio de tipo con fondos insuficientes`;
- `6fb37dc` — `test: cubrir validacion de fondos insuficientes`;
- `5dd8372` — `fix: validar fondos disponibles en movimientos`;
- `5ce00a2` — `docs: registrar decisiones funcionales de SOFP`;
- `1838f1b` — `docs: actualizar contexto de continuidad`.

## Regla de negocio pendiente

### Categorías con movimientos

Una categoría referenciada por movimientos no debe eliminarse físicamente. Debe conservarse el historial, probablemente mediante desactivación, y la UI debe mostrar un mensaje amigable ante el intento de eliminación.

## Próximo paso

Reconstruir nuevamente el estado del código antes de implementar el siguiente bloque. Revisar `Categoria`, `CategoriaService`, repositorio, `Movimiento`, relaciones JPA y tests/UI de categorías.

El próximo bloque funcional previsto es resolver la eliminación/desactivación de categorías con movimientos asociados, evitando el borrado físico y la exposición de `ConstraintViolationException`.

No hacer merge automático a `main`. No crear ramas nuevas. Después de cambios importantes: tests específicos, relacionados y suite general cuando corresponda; `git diff`, `git diff --check` y `git status`.
