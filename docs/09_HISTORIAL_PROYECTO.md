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
29. Cierre de la superficie pública de `ObligacionService`: se eliminó el registro de pagos sin `usuarioId`.
30. Auditoría transversal de integridad de `Cuenta`: revisión de mutabilidad de tipo/moneda, historial financiero, datos específicos de tarjeta y moneda económica de movimientos.

## Auditoría de `Cuenta` — resultado 13/09/2026

La auditoría revisó el agregado `Cuenta`, sus servicios y repositorio, `TipoCuenta`, `Moneda`, el eje `Movimiento`, `GastoService`, `OperacionFinanciera` y `MovimientoActivo`, junto con la cobertura de tests relacionada.

No se modificó código durante la auditoría.

Hallazgos confirmados:

- tipo y moneda de cuenta son mutables y `CuentaService` no verifica historial antes de modificarlos;
- convertir una cuenta común en tarjeta no garantiza por sí solo límite/cierre/vencimiento completos;
- salir de tarjeta hacia otro tipo no tiene política explícita para los datos de crédito existentes;
- cambiar moneda con historial requiere protección para preservar la interpretación histórica;
- la moneda del `Movimiento` es económica y puede diferir de la moneda estructural de la cuenta en consumos de tarjeta;
- transferencias entre cuentas ya exigen misma moneda.

Conclusión: el siguiente cambio debe ser una protección mínima y explícita de estas invariantes, sin introducir conversiones automáticas ni destruir el soporte multidivisa de consumos de tarjeta.

## Estado de validación

Último `mvn test` informado: **696/696**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.

Suite relacionada: **69/69**, `BUILD SUCCESS`.

`ObligacionServiceTest`: **9/9**, `BUILD SUCCESS`.

No se ejecutaron nuevos tests durante la auditoría de Cuenta.

## Pendientes

1. Implementar y testear la integridad estructural de `Cuenta`.
2. Definir reglas de ciclo durante el pago.
3. Definir multidivisa de tarjetas.
4. Financiamiento avanzado.
5. UI específica de tarjetas.
6. Pasivos, patrimonio y análisis.
7. Gestión de entidades financieras.
8. Pulido de consola.

No hacer merge a `main` automáticamente.
