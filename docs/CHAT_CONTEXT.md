# SOFP — Contexto para continuar con ChatGPT

La fuente de verdad es el código, Git y los tests actuales; `docs/` es documentación auxiliar. Antes de proponer cambios, reconstruir siempre el estado desde GitHub.

## Estado verificado — 05/09/2026

**Rama estable:** `main` → `a4be859`.
**Rama de trabajo:** `feature/swing-shell` → `fa46e02`.

Último commit funcional/test: `85b767c` — `test: aislar persistencia en CategoriaServiceTest`.

Después de ese commit se realizaron actualizaciones documentales de continuidad. La suite general fue ejecutada y quedó en **580/580**.

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

## Reglas de negocio cerradas

### Fondos insuficientes

Un `EGRESO` debe rechazarse si supera el saldo disponible de la cuenta. Un egreso exactamente igual al saldo disponible está permitido y deja saldo cero.

La regla está implementada en `MovimientoService` y se aplica al registro público y a las modificaciones de importe y tipo cuando corresponde.

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

## Últimos commits relevantes

- `85b767c` — `test: aislar persistencia en CategoriaServiceTest`;
- `3cffeb9` — `test: completar datos requeridos en alta de cuenta`;
- `2b2bf3e` — `test: adaptar cambio de tipo a fondos disponibles`;
- `805e9fa` — `test: corregir comparacion de saldo en movimiento`;
- `5dd8372` — `fix: validar fondos disponibles en movimientos`.

## Estado Git

`feature/swing-shell` continúa como rama de trabajo y `main` permanece en `a4be859`. No se realizó merge a `main`.

Los commits documentales posteriores al cambio funcional/test dejaron la rama en `fa46e02` al cierre de esta actualización.

## Próximo paso

El próximo bloque funcional candidato es definir e incorporar `FormaPago`, manteniendo la distinción entre `Cuenta` y medio de pago y el núcleo financiero común basado en `Movimiento`.

Antes de implementar cambios, reconstruir nuevamente el código real, revisar clases relacionadas, repositorios, reglas de negocio y tests. Después de cambios importantes: tests específicos, relacionados y suite general cuando corresponda; `git diff`, `git diff --check` y `git status`.

No hacer merge automático a `main`. No crear ramas nuevas salvo indicación explícita.
