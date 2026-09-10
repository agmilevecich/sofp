# SOFP — Contexto para continuar con ChatGPT

## Estado actual — 10/09/2026

La fuente de verdad es el código, Git y los tests actuales; `docs/` es documentación auxiliar y puede quedar desactualizada. Antes de proponer cambios, reconstruir siempre el estado desde GitHub.

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

La última comparación verificada antes de la actualización documental indicó **470 commits adelante y 0 atrás** respecto de `main`.

El último commit funcional antes de esta actualización documental es `51d4afe` — `fix: ajustar test de cuotas al registro automatico`.

## Último bloque funcional cerrado

### Cuotas automáticas en gastos con tarjeta

`GastoService` genera automáticamente la cantidad solicitada de cuotas al registrar un gasto con `FormaPago.TARJETA_CREDITO`. La generación ocurre dentro de la transacción de la obligación y las cuotas quedan persistidas.

`PagoTarjetaServiceTest` se ajustó para registrar el gasto con `cantidadCuotas = 3`, en lugar de generar manualmente las cuotas después. El error anterior `La obligación ya tiene cuotas generadas` quedó resuelto.

Commits recientes relevantes:

- `51d4afe` — `fix: ajustar test de cuotas al registro automatico`.
- `4ce1591` — `fix: generar cuotas dentro de la transaccion del gasto`.
- `87fab04` — `fix: persistir cuotas al registrar obligaciones`.
- `4092212` — `fix: corregir fixture de cuotas en GastosPanelTest`.
- `afbfd7b` — `test: cubrir cuotas en GastoService`.
- `3fd91b1` — `feat: integrar cuotas al registro de gastos`.

## Shell Swing — Fase 8

El shell integra Inicio, Cuentas, Categorías, Ingresos, Gastos, Movimientos, Inversiones, Reportes, Obligaciones y Transferencias mediante `MainFrame`, `SidebarPanel` y `CardLayout`.

`GastosPanel` ya permite seleccionar forma de pago y cantidad de cuotas. El siguiente bloque de UI será incorporar una selección explícita de la tarjeta de crédito utilizada en una compra.

## Reglas de tarjetas

Una tarjeta de crédito es una `Cuenta` con `TipoCuenta.TARJETA_CREDITO`, límite, día de cierre y día de vencimiento.

Una compra con tarjeta genera un `Movimiento EGRESO` y una `Obligacion`. La obligación conserva la moneda económica del movimiento y no hay conversión automática.

El crédito disponible inicial se calcula por moneda como límite menos consumos pendientes.

## Ciclos de facturación

`CicloFacturacion` es un objeto de dominio no persistente. `Cuenta.calcularCicloFacturacion(LocalDate)` calcula inicio, cierre y vencimiento y ajusta días inexistentes al último día real del mes. `CicloFacturacionTest` contiene 9 tests.

Los ciclos están implementados; queda pendiente integrarlos con consumos, obligaciones y pagos.

## Tests

Suite general más reciente conocida: `mvn test` → **671/671**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, 10/09/2026 10:39:38 -03:00.

Suite relacionada más reciente: `mvn -Dtest=GastosPanelTest,GastoServiceTest,ObligacionTest,ObligacionCuotasTest,PagoTarjetaServiceTest test` → **32/32**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, 10/09/2026 13:14:29 -03:00, duración 01:19 min.

Focalizados: `PagoTarjetaServiceTest` 4/4 y `GastosPanelTest` 6/6.

## Próximo paso exacto

1. Revisar `GastosPanelTest`.
2. Revisar `GastosPanel`, `CuentaService`, repositorio de cuentas y convenciones de `CuentasPanel`/`RegistrarCuentaPanel`.
3. Definir el cambio mínimo para seleccionar una tarjeta de crédito explícita en `GastosPanel`.
4. Agregar tests para tarjetas activas y selección de la tarjeta utilizada.
5. Ejecutar tests específicos, relacionados y suite general cuando corresponda.

## Continuidad

No modificar `main` ni crear ramas nuevas salvo indicación explícita. No asumir resultados locales no informados. Después de cambios importantes revisar tests, `git diff`, `git diff --check` y `git status`.

La documentación debe actualizarse al cerrar etapas importantes, pero siempre prevalecen código y tests actuales.
