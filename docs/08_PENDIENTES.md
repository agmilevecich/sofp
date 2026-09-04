# SOFP — Pendientes

## Estado — 04/09/2026

**Rama estable:** `main` → `a4be859`.
**Rama de trabajo:** `feature/swing-shell` → `70c2455` (`test: cubrir registro de categoria sin nombre`).

Comparación: **186 commits por delante y 2 por detrás de `main`**, estado `diverged`. Merge base: `96f3d999`. Los dos commits exclusivos de `main` son documentales.

## Criterio funcional adoptado a partir de ControlFinanzas

ControlFinanzas se utiliza como banco de ideas y referencia, no como arquitectura para copiar.

El criterio acordado para SOFP es:

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

Los futuros paneles podrán especializarse en gastos, ingresos, transferencias, inversiones, préstamos/deudas, pagos de tarjeta, historial y dashboard, pero todos deben alimentar el mismo núcleo financiero.

También se distingue **Cuenta** de **Forma/Medio de pago**. Se prevén: tarjeta de crédito, tarjeta de débito, QR, transferencia y efectivo. La tarjeta de crédito deberá poder representar una obligación/pasivo sin exigir una salida inmediata de fondos de la cuenta bancaria.

Objetivo de patrimonio: `TOTAL ACTIVOS - TOTAL PASIVOS = PATRIMONIO NETO`.

Un préstamo otorgado debe disminuir liquidez pero permanecer como derecho de cobro. Una transferencia entre cuentas propias no debe computarse como ingreso ni gasto.

Estos son **criterios de diseño pendientes de implementación**, no funcionalidades que deban darse por terminadas.

## Reglas de negocio pendientes

### Fondos insuficientes

`EGRESO` debe rechazarse cuando el importe sea mayor que el saldo disponible de la cuenta. Si el importe coincide con el saldo, debe permitirse y quedar en cero.

La regla debe estar en el servicio/dominio y cubrir también modificaciones de movimientos que puedan generar un saldo inválido.

### Categorías con movimientos

Una categoría que ya tenga movimientos no debe eliminarse físicamente. Debe conservarse el historial y, según el diseño definitivo, impedir su eliminación y permitir su desactivación. La UI debe mostrar un mensaje comprensible en lugar de propagar una excepción de Hibernate.

## Bloques Swing cerrados

Están implementados e integrados: `MainFrame`, `HeaderPanel`, `SidebarPanel`, `InicioPanel`, `CuentasPanel`, `CategoriasPanel`, `MovimientosPanel`, `InversionesPanel`, `ReportesPanel`, `StatusBarPanel`, `RegistrarCuentaPanel`, `RegistrarMovimientoPanel` y `ui.Main`.

Los bloques ya validados de movimientos, cuentas, categorías, inversiones y reportes permanecen cerrados dentro de sus alcances actuales.

La gestión de categorías continúa recibiendo cobertura incremental. `70c2455` agregó el caso de registro sin nombre.

## Próximos bloques funcionales candidatos

1. Fortalecer las reglas financieras del núcleo de movimientos, empezando por fondos insuficientes.
2. Resolver correctamente la eliminación/desactivación de categorías con movimientos asociados.
3. Definir e incorporar `FormaPago` sin confundirla con `Cuenta`.
4. Evolucionar movimientos especializados para gastos, ingresos y transferencias manteniendo el núcleo común.
5. Incorporar progresivamente pasivos/obligaciones y patrimonio neto.
6. Llevar a SOFP, adaptadas a su arquitectura, las capacidades de análisis que resultaron valiosas en ControlFinanzas: resúmenes mensuales/históricos, distribución por categoría/tipo, evolución patrimonial, vencimientos y dashboard.

El orden definitivo debe decidirse después de revisar el código y tests actuales del bloque elegido.

## Validación

`CategoriasPanelTest`: **3/3** en verde en la última ejecución conocida.

Última suite general conocida: **568/568 tests en verde**, `BUILD SUCCESS`. No atribuir una nueva suite general sin resultado informado por el usuario.

Antes de cerrar cada bloque: tests específicos → tests relacionados → suite general cuando corresponda → `git diff` → `git diff --check` → `git status`.

## Integración

No hacer merge a `main` automáticamente. No crear ramas nuevas. Antes de una eventual integración, revisar commits, comparación con `main`, tests, diff, diff-check, status y documentación.
