# SOFP — Historial del proyecto

## Estado documental — 12/09/2026

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
22. Atomicidad de compra con tarjeta mediante coordinación transaccional de movimiento y obligación.
23. Configuración de JAR ejecutable y dependencias runtime.
24. Auditoría funcional transversal del modelo de tarjeta, obligaciones, pagos, autorización y mutabilidad.

## Estado actual de persistencia

La aplicación utiliza `jdbc:h2:tcp://localhost/./database/sofp`.

H2 Server se ejecuta en `localhost:9092` y H2 Console en `localhost:8082`.

Los tests continúan aislados con su `persistence.xml` de test y H2 en memoria.

## Estado actual de tarjetas y cuotas

Una tarjeta es una `Cuenta` con `TipoCuenta.TARJETA_CREDITO`. Dispone de límite, día de cierre y día de vencimiento.

La deuda se representa mediante `Obligacion`. La moneda económica del consumo se conserva desde `Movimiento` hacia la obligación y no se convierte automáticamente.

El crédito disponible se calcula inicialmente por moneda, sin conversiones implícitas.

`CicloFacturacion` es un objeto de dominio no persistente. Calcula inicio, cierre y vencimiento y resuelve meses cortos y cambio de año.

Al registrar un gasto con tarjeta, `GastoService` genera automáticamente las cuotas dentro de la transacción de la obligación.

## Validación más reciente conocida

Suite general informada por el usuario: **690/690**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.

## Auditoría del 12/09/2026

### Hallazgos críticos

- Un movimiento que origina una obligación todavía puede ser modificado estructuralmente o eliminado; esto puede romper la consistencia entre movimiento, obligación, cuotas y ciclo.
- La UI de obligaciones todavía no utiliza el flujo completo de `PagoTarjetaService`; existe riesgo de reducir deuda sin registrar la salida real de fondos si se usa el servicio directo incorrecto.
- `ObligacionService` conserva operaciones públicas sin `usuarioId` que pueden permitir bypass de autorización si se invocan directamente.

### Hallazgos importantes

- `CuentaService` permite cambios de tipo/moneda que deben revisarse cuando existe historial financiero.
- El ciclo de facturación está bien resuelto para creación de obligaciones/cuotas, pero aún no define todas las reglas de pago, mora, gracia y días no hábiles.
- El tratamiento multidivisa definitivo del límite de tarjetas sigue abierto.
- La financiación actual es de cuotas simples; intereses, CFT, refinanciación, adelantos, anulaciones y ajustes siguen pendientes.

### Elementos ya consolidados

- atomicidad básica de compra con tarjeta;
- generación automática de cuotas;
- cruce de fin de año;
- base de ciclos de facturación;
- criterio inicial de crédito disponible;
- flujo coordinado de pago en servicio;
- H2 TCP;
- JAR ejecutable.

## Próxima secuencia de trabajo

1. Integridad movimiento ↔ obligación.
2. Autorización y superficie pública de `ObligacionService`.
3. Integración del pago real en UI.
4. Integridad de tipo/moneda de cuentas.
5. Reglas de ciclo aplicadas al pago.
6. Multidivisa de tarjetas.
7. Financiación avanzada.
8. UI específica de tarjetas.
9. Pasivos/patrimonio y análisis.
10. Pulido de consola.

No hacer merge a `main` automáticamente.