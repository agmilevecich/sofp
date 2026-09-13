# SOFP — Historial del proyecto

## Estado documental — 13/09/2026

Los estados técnicos deben verificarse siempre contra código, tests y Git. Este documento registra hitos; no reemplaza la inspección del estado real.

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
22. Atomicidad de compra con tarjeta mediante coordinación transaccional de movimiento y obligación.
23. Configuración de JAR ejecutable y dependencias runtime.
24. Auditoría funcional transversal del modelo de tarjeta, obligaciones, pagos, autorización y mutabilidad.
25. Protección de movimientos que originan obligaciones frente a cambios estructurales y eliminación.
26. Integración del pago coordinado de tarjeta en `ObligacionesPanel`.
27. Integración de `PagoTarjetaService` en `MainFrame` y `Main`.
28. Cobertura de pago real desde UI y saldo de la cuenta pagadora.
29. Cierre de la superficie pública de `ObligacionService`: se eliminó el registro de pagos sin `usuarioId` y la API pública exige autorización explícita.

## Estado actual de persistencia

La aplicación utiliza `jdbc:h2:tcp://localhost/./database/sofp`.

H2 Server se ejecuta en `localhost:9092` y H2 Console en `localhost:8082`.

Los tests continúan aislados con su `persistence.xml` de test y H2 en memoria.

## Estado actual de tarjetas y cuotas

Una tarjeta es una `Cuenta` con `TipoCuenta.TARJETA_CREDITO`. Dispone de límite, día de cierre y día de vencimiento.

La deuda se representa mediante `Obligacion`, vinculada uno-a-uno al `Movimiento` de origen. El origen debe ser un egreso de una cuenta de tarjeta.

La moneda económica del consumo se conserva desde `Movimiento` hacia la obligación y no se convierte automáticamente.

El crédito disponible se calcula inicialmente por moneda, sin conversiones implícitas.

`CicloFacturacion` es un objeto de dominio no persistente. Calcula inicio, cierre y vencimiento y resuelve meses cortos y cambio de año.

Al registrar un gasto con tarjeta, `GastoService` genera automáticamente las cuotas dentro de la transacción de la obligación.

El pago coordinado mediante `PagoTarjetaService` valida propiedad/perfil, cuenta pagadora, categoría, estado activo, moneda, importe y fondos; luego registra el movimiento real de salida y reduce la obligación en una única transacción.

El registro directo de pagos mediante `ObligacionService` exige ahora `usuarioId` y valida que la obligación pertenezca al perfil del usuario antes de modificarla.

## Validación más reciente conocida

Suite general informada por el usuario el 13/09/2026: **696/696**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.

Suite relacionada de obligaciones/pagos/UI: **69/69**, `BUILD SUCCESS`.

Tests específicos de UI de pago: **6/6**, `BUILD SUCCESS`.

Tests específicos de `ObligacionService`: **9/9**, `BUILD SUCCESS`.

## Estado de auditoría

### Hallazgos ya cerrados

- integridad estructural del movimiento origen de obligación;
- pago real desde UI mediante `PagoTarjetaService`;
- integración del coordinador en la aplicación;
- cobertura del flujo UI y actualización del saldo de la cuenta pagadora;
- superficie pública de `ObligacionService` para registrar pagos: requiere `usuarioId` y mantiene aislamiento por perfil.

### Hallazgos todavía abiertos

- cambios estructurales de tipo/moneda de `Cuenta` con historial;
- reglas de pago asociadas a ciclo, vencimiento, mora, gracia y días no hábiles;
- tratamiento multidivisa definitivo del límite de tarjetas;
- financiación avanzada;
- UI específica de tarjetas;
- pasivos/patrimonio y análisis;
- gestión de entidades financieras;
- pulido de consola.

## Próxima secuencia de trabajo

1. Integridad de tipo/moneda de `Cuenta`.
2. Reglas de ciclo aplicadas al pago.
3. Multidivisa de tarjetas.
4. Financiación avanzada.
5. UI específica de tarjetas.
6. Pasivos/patrimonio y análisis.
7. Gestión de entidades financieras.
8. Pulido de consola.

No hacer merge a `main` automáticamente.
