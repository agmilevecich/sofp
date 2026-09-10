# SOFP — Historial del proyecto

## Estado documental — 10/09/2026

Los estados técnicos deben verificarse siempre contra código, tests y Git.

## Hitos principales

1. Construcción progresiva del dominio financiero con JPA/Hibernate y H2.
2. Consolidación de `Movimiento` como núcleo financiero común.
3. Implementación de operaciones financieras e inversiones.
4. Auditoría transversal de seguridad y aislamiento por usuario/perfil.
5. Construcción del shell Swing de la Fase 8.
6. Integración de cuentas, categorías, movimientos, inversiones y reportes.
7. Gastos e Ingresos como paneles especializados sobre `Movimiento`.
8. Integración de `FormaPago`.
9. Obligaciones y pagos para compras con tarjeta de crédito.
10. Autorización de pagos por usuario y UI de obligaciones.
11. Transferencias mediante `OperacionFinanciera`.
12. Moneda explícita en movimientos y obligaciones.
13. Crédito disponible y validación de límite de tarjeta.
14. Conservación de consumos de tarjeta sin obligación sin doble contabilización.
15. Aislamiento del contexto JPA/H2 en tests.
16. Ciclos de facturación y vencimientos básicos en el dominio, con cobertura específica.
17. Integración inicial de cuotas al registro de gastos con tarjeta.
18. Corrección de tests para respetar la generación automática de cuotas.

## Estado actual de tarjetas y cuotas

Una tarjeta es una `Cuenta` con `TipoCuenta.TARJETA_CREDITO`. Dispone de límite, día de cierre y día de vencimiento.

La deuda se representa mediante `Obligacion`. La moneda económica del consumo se conserva desde `Movimiento` hacia la obligación y no se convierte automáticamente.

El crédito disponible se calcula inicialmente por moneda, sin conversiones implícitas.

`CicloFacturacion` es un objeto de dominio no persistente. `Cuenta.calcularCicloFacturacion(LocalDate)` calcula inicio, cierre y vencimiento y ajusta días inexistentes al último día real del mes.

Al registrar un gasto con tarjeta, `GastoService` genera automáticamente la cantidad solicitada de cuotas dentro de la transacción de la obligación y las cuotas quedan persistidas.

El último ajuste corrigió `PagoTarjetaServiceTest` para registrar las tres cuotas mediante `GastoService`, en lugar de generarlas manualmente sobre una obligación que ya las había generado.

## Validación más reciente conocida

Suite general ejecutada por el usuario el **10/09/2026 10:39:38 -03:00**: **671/671**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.

Suite relacionada ejecutada por el usuario el **10/09/2026 13:14:29 -03:00**:

- **32/32** tests;
- 0 failures;
- 0 errors;
- 0 skipped;
- `BUILD SUCCESS`;
- duración 01:19 min.

Focalizados: `PagoTarjetaServiceTest` **4/4** y `GastosPanelTest` **6/6**.

## Estado Git

Antes de la actualización documental, `feature/swing-shell` estaba en `51d4afe4e40a558026b17e44ae6e54085c7a908d` y GitHub verificó **470 commits adelante / 0 atrás** respecto de `main` (`a4be859...`).

La actualización documental genera nuevos commits en la misma rama. El SHA final debe verificarse después de completar todos los archivos documentales.

## Próximos hitos

1. Revisar `GastosPanelTest` y convenciones actuales de UI/cuentas.
2. Incorporar selección explícita de tarjeta de crédito en `GastosPanel`.
3. Cubrir con tests la selección y uso de la tarjeta correcta.
4. Integrar ciclos con consumos y obligaciones.
5. Unificar el saldo monetario de tarjetas entre `MovimientoService` y `CuentaService`.
6. Profundizar pagos y liberación de crédito.
7. Pasivos y patrimonio neto.
8. Análisis histórico, vencimientos, resúmenes y dashboard.
9. Pulido de consola.

No hacer merge a `main` automáticamente ni crear ramas nuevas salvo indicación explícita.
