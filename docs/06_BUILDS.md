# SOFP — Historial de Builds

## Build actual — Cuotas y pagos de tarjeta

**Estado: COMPLETADO Y VALIDADO.**

El bloque reciente consolidó la integración de cuotas al registro de gastos con tarjeta de crédito. `GastoService` genera las cuotas solicitadas automáticamente y la generación ocurre dentro de la transacción de la obligación.

Se corrigió el fixture de `GastosPanelTest` para respetar el registro automático de cuotas y se ajustó `PagoTarjetaServiceTest` para crear las tres cuotas mediante `GastoService`, evitando generación manual duplicada.

Commits recientes:

- `51d4afe` — `fix: ajustar test de cuotas al registro automatico`.
- `4ce1591` — `fix: generar cuotas dentro de la transaccion del gasto`.
- `87fab04` — `fix: persistir cuotas al registrar obligaciones`.
- `4092212` — `fix: corregir fixture de cuotas en GastosPanelTest`.
- `afbfd7b` — `test: cubrir cuotas en GastoService`.
- `3fd91b1` — `feat: integrar cuotas al registro de gastos`.

## Validación — 10/09/2026

Suite general conocida:

- `mvn test`;
- **671/671**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- finalización **10:39:38 -03:00**.

Suite relacionada con el ajuste de cuotas:

`mvn -Dtest=GastosPanelTest,GastoServiceTest,ObligacionTest,ObligacionCuotasTest,PagoTarjetaServiceTest test`

- **32/32**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración **01:19 min**;
- finalización **13:14:29 -03:00**.

Validaciones focalizadas:

- `PagoTarjetaServiceTest`: **4/4**, `BUILD SUCCESS`, 13:08:50 -03:00.
- `GastosPanelTest`: **6/6**, `BUILD SUCCESS`.

## Bloques funcionales consolidados

Fondos insuficientes, categorías con movimientos, Gastos, Ingresos, FormaPago, Obligaciones/pagos, autorización por usuario, Transferencias, moneda explícita, crédito/límite de tarjeta, ciclos básicos de facturación y cuotas/financiación inicial.

## Fase 8 — Swing

La Fase 8 integra Inicio, Cuentas, Categorías, Ingresos, Gastos, Movimientos, Inversiones, Reportes, Obligaciones y Transferencias mediante `MainFrame`, `SidebarPanel` y `CardLayout`.

`GastosPanel` permite seleccionar forma de pago y cantidad de cuotas. El próximo ajuste de UI será permitir seleccionar explícitamente qué tarjeta de crédito se utiliza cuando la forma de pago sea `TARJETA_CREDITO`.

## Próximo bloque

Antes de modificar la UI, revisar `GastosPanelTest` y las convenciones actuales de cuentas/paneles. Luego implementar el selector específico de tarjeta con cobertura de tests.

## Estado Git verificado antes de esta actualización

`main` → `a4be859...`.
`feature/swing-shell` estaba en `51d4afe...` y **470 commits adelante / 0 atrás**.

Las actualizaciones documentales posteriores avanzan la rama; el estado Git definitivo debe verificarse nuevamente al cerrar esta actualización documental.

No hacer merge a `main` automáticamente.
