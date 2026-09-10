# SOFP — Historial del proyecto

## Estado documental — 10/09/2026

Los estados técnicos deben verificarse siempre contra código, tests y Git.

## Hitos principales

1. Construcción progresiva del dominio financiero con JPA/Hibernate y H2.
2. Consolidación de `Movimiento` como núcleo financiero común.
3. Implementación de operaciones financieras e inversiones.
4. Auditoría transversal de seguridad y aislamiento por usuario/perfil integrada en `main`.
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

## Estado actual de tarjetas

Una tarjeta es una `Cuenta` con `TipoCuenta.TARJETA_CREDITO`. Dispone de límite, día de cierre y día de vencimiento.

La deuda se representa mediante `Obligacion`. La moneda económica del consumo se conserva desde `Movimiento` hacia la obligación y no se convierte automáticamente.

El crédito disponible se calcula inicialmente por moneda, sin conversiones implícitas.

`CicloFacturacion` es un objeto de dominio no persistente. `Cuenta.calcularCicloFacturacion(LocalDate)` calcula inicio, cierre y vencimiento y ajusta días inexistentes al último día real del mes.

## Validación más reciente

`mvn test` ejecutado por el usuario el **09/09/2026 21:58:35 -03:00**: **664/664**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, 10:04 min.

## Estado Git

`main`: `a4be85913847200cb70976d5266d9cbba10b3100`.
`feature/swing-shell`: `548063914ad7a3fe6ae028dad606aad57f7ca42e`.

Comparación GitHub: **443 commits adelante y 0 atrás**. El commit `5480639` es documental; el último funcional es `46290786`.

## Próximos hitos

1. Integrar ciclos con consumos y obligaciones.
2. Unificar el saldo monetario de tarjetas entre `MovimientoService` y `CuentaService`.
3. Profundizar pagos y liberación de crédito, incluyendo multidivisa explícita.
4. Cuotas y financiación.
5. UI específica de tarjetas.
6. Pasivos y patrimonio neto.
7. Análisis histórico, vencimientos y dashboard.
8. Pulido de consola.

No hacer merge a `main` automáticamente ni crear ramas nuevas salvo indicación explícita.
