# SOFP — Historial del proyecto

## Estado documental — 11/09/2026

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
16. Ciclos de facturación y vencimientos básicos en el dominio.
17. Integración inicial de cuotas al registro de gastos con tarjeta.
18. Corrección de tests para respetar la generación automática de cuotas.
19. Configuración de H2 de la aplicación mediante servidor TCP compartido con H2 Console.
20. Selección explícita de tarjeta de crédito en `GastosPanel`.
21. Cobertura de cuotas al cruzar el fin de año.

## Estado actual de persistencia

La aplicación utiliza `jdbc:h2:tcp://localhost/./database/sofp`.

H2 Server se ejecuta en `localhost:9092` y H2 Console en `localhost:8082`.

SOFP y H2 Console fueron probados simultáneamente sobre la misma base persistente.

Los tests continúan aislados con su `persistence.xml` de test y H2 en memoria.

## Estado actual de tarjetas y cuotas

Una tarjeta es una `Cuenta` con `TipoCuenta.TARJETA_CREDITO`. Dispone de límite, día de cierre y día de vencimiento.

La deuda se representa mediante `Obligacion`. La moneda económica del consumo se conserva desde `Movimiento` hacia la obligación y no se convierte automáticamente.

El crédito disponible se calcula inicialmente por moneda, sin conversiones implícitas.

`CicloFacturacion` es un objeto de dominio no persistente. `Cuenta.calcularCicloFacturacion(LocalDate)` calcula inicio, cierre y vencimiento y ajusta días inexistentes al último día real del mes.

Al registrar un gasto con tarjeta, `GastoService` genera automáticamente la cantidad solicitada de cuotas dentro de la transacción de la obligación y las cuotas quedan persistidas.

`ObligacionCuotasTest` cubre además cuotas cuyo ciclo atraviesa el cambio de año.

## Validación más reciente conocida

Suite general ejecutada por el usuario el **11/09/2026 20:11:17 -03:00**: **689/689**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, duración 10:22 min.

Los tests relacionados del bloque de obligaciones/cuotas/servicios quedaron en **49/49**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.

## Próximos hitos

1. Auditar los pendientes documentados contra el código y los tests actuales.
2. Confirmar si la integración completa de ciclos con consumos, obligaciones y pagos continúa pendiente.
3. Unificar el saldo monetario de tarjetas entre `MovimientoService` y `CuentaService` si corresponde.
4. Profundizar pagos y liberación de crédito.
5. Pasivos y patrimonio neto.
6. Análisis histórico, vencimientos, resúmenes y dashboard.
7. Pulido de consola.

No hacer merge a `main` automáticamente ni crear ramas nuevas salvo indicación explícita.
